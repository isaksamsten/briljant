package org.briljantframework.classification;

import com.carrotsearch.hppc.IntDoubleMap;
import com.carrotsearch.hppc.IntDoubleOpenHashMap;
import com.carrotsearch.hppc.ObjectDoubleMap;
import com.carrotsearch.hppc.ObjectDoubleOpenHashMap;

import org.briljantframework.Bj;
import org.briljantframework.Utils;
import org.briljantframework.classification.ShapeletTree.Threshold;
import org.briljantframework.classification.tree.ClassSet;
import org.briljantframework.classification.tree.Example;
import org.briljantframework.classification.tree.Gain;
import org.briljantframework.classification.tree.TreeBranch;
import org.briljantframework.classification.tree.TreeLeaf;
import org.briljantframework.classification.tree.TreeNode;
import org.briljantframework.classification.tree.TreeSplit;
import org.briljantframework.classification.tree.TreeVisitor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by isak on 17/03/15.
 */
public class RandomPointTree implements Classifier {

  private ClassSet classSet = null;
  private Vector classes = null;
  private Gain gain = Gain.INFO;

  private int[] index = null;

  public RandomPointTree(Vector classes, ClassSet classSet) {
    this.classes = classes;
    this.classSet = classSet;
  }

  private static double computeDistance(int[] index, int take, Vector x, Vector y) {
    double distance = 0;
    double sumF = 0, sumSquareF = 0;
    double sumS = 0, sumSquareS = 0;
    for (int i = 0; i < take; i++) {
      double f = x.getAsDouble(index[i]);
      double s = y.getAsDouble(index[i]);
      sumF += f;
      sumSquareF += f * f;
      sumS += s;
      sumSquareS += s * s;
    }
    double stdF = Math.sqrt((sumSquareF - (sumF * sumF) / take) / take);
    double meanF = sumF / take;

    double stdS = Math.sqrt((sumSquareS - (sumS * sumS) / take) / take);
    double meanS = sumS / take;
    for (int j = 0; j < take; j++) {
      int idx = index[j];
      double f = (x.getAsDouble(idx) - meanF) / stdF;
      double s = (y.getAsDouble(idx) - meanS) / stdS;
      distance += (f - s) * (f - s);
    }
    return Math.sqrt(distance / take);
  }

  @Override
  public Predictor fit(DataFrame x, Vector y) {
    if (classes == null) {
      classes = Vec.unique(y);
    }

    if (classSet == null) {
      classSet = new ClassSet(y, classes);
    }

    if (index == null) {
      index = new int[x.columns()];
      for (int i = 0; i < index.length; i++) {
        index[i] = i;
      }
    }

    TreeNode<SplitPoint> node = buildNode(x, y, classSet, 0);
    return new Predictor(classes, node, new SplitPointTreeVisitor());
  }

  private TreeNode<SplitPoint> buildNode(DataFrame x, Vector y, ClassSet classSet, int depth) {
    if (classSet.getTotalWeight() < 2 || classSet.getTargetCount() == 1) {
      return TreeLeaf.fromExamples(classSet);
    }

    TreeSplit<SplitPoint> split = findSplit(x, y, classSet);
    if (split == null) {
      return TreeLeaf.fromExamples(classSet);
    } else {
      TreeNode<SplitPoint> left, right;
      if (split.getLeft().getTargetCount() == 1) {
        left = TreeLeaf.fromExamples(split.getLeft());
      } else {
        left = buildNode(x, y, split.getLeft(), depth + 1);
      }

      if (split.getRight().getTargetCount() == 1) {
        right = TreeLeaf.fromExamples(split.getRight());
      } else {
        right = buildNode(x, y, split.getRight(), depth + 1);
      }
      return new TreeBranch<>(left, right, split.getThreshold());
    }
  }

  private TreeSplit<SplitPoint> findSplit(DataFrame x, Vector y, ClassSet classSet) {
    Threshold bestThreshold = Threshold.inf();
    TreeSplit<SplitPoint> bestSplit = null;
    for (int i = 0; i < 10; i++) {
      int take = Utils.randInt(2, 20);
      Utils.permute(index);
      ClassSet.Sample a = classSet.getRandomSample();
      Vector pivot = x.getRecord(a.getRandomExample().getIndex());

      IntDoubleMap distanceMap = new IntDoubleOpenHashMap();
      Threshold threshold = bestDistanceThresholdInSample(classSet, x, y, take, pivot, distanceMap);
      boolean lowerImpurity = threshold.impurity < bestThreshold.impurity;
      boolean equalImpuritySmallerGap =
          threshold.impurity == bestThreshold.impurity && threshold.gap > bestThreshold.gap;
      if (lowerImpurity || equalImpuritySmallerGap) {
        bestSplit = split(distanceMap, classSet, take, pivot, threshold.threshold);
        bestThreshold = threshold;
      }
    }

    if (bestSplit != null) {
      bestSplit.setImpurity(bestThreshold.impurity);
    }
    return bestSplit;
  }

  private TreeSplit<SplitPoint> split(IntDoubleMap distanceMap, ClassSet classSet, int take,
                                      Vector first,
                                      double threshold) {
    ClassSet left = new ClassSet(classSet.getDomain());
    ClassSet right = new ClassSet(classSet.getDomain());
    for (ClassSet.Sample sample : classSet.samples()) {
      String target = sample.getTarget();

      ClassSet.Sample leftSample = ClassSet.Sample.create(target);
      ClassSet.Sample rightSample = ClassSet.Sample.create(target);

      for (Example example : sample) {
        double pivotDistance = distanceMap.get(example.getIndex());
        if (pivotDistance == threshold) {
          if (Utils.getRandom().nextDouble() <= 0.5) {
            leftSample.add(example);
          } else {
            rightSample.add(example);
          }
        } else if (pivotDistance < threshold) {
          leftSample.add(example);
        } else {
          rightSample.add(example);
        }
      }

      if (!leftSample.isEmpty()) {
        left.add(leftSample);
      }
      if (!rightSample.isEmpty()) {
        right.add(rightSample);
      }
    }
    return new TreeSplit<>(
        left, right, new SplitPoint(first, Arrays.copyOf(index, take), threshold));
  }

  protected Threshold bestDistanceThresholdInSample(
      ClassSet classSet, DataFrame x, Vector y, int take, Vector pivot,
      IntDoubleMap memoizedDistances) {
    double sum = 0.0;
    List<ExampleDistance> distances = new ArrayList<>();
    for (Example example : classSet) {
      Vector second = x.getRecord(example.getIndex());
      double distance = computeDistance(index, take, pivot, second);
      memoizedDistances.put(example.getIndex(), distance);
      distances.add(new ExampleDistance(distance, example));
      sum += distance;
    }

    Collections.sort(distances);
    return findBestThreshold(distances, classSet, y, sum);
  }

  public Threshold findBestThreshold(List<ExampleDistance> distances,
                                     ClassSet classSet, Vector y,
                                     double distanceSum) {
    ObjectDoubleMap<String> lt = new ObjectDoubleOpenHashMap<>();
    ObjectDoubleMap<String> gt = new ObjectDoubleOpenHashMap<>();

    List<String> presentTargets = classSet.getTargets();
    DoubleMatrix ltRelativeFrequency = Bj.doubleVector(presentTargets.size());
    DoubleMatrix gtRelativeFrequency = Bj.doubleVector(presentTargets.size());

    double ltWeight = 0.0, gtWeight = 0.0;

    // Initialize all value to the right (i.e. all values are larger than the initial threshold)
    for (ClassSet.Sample sample : classSet.samples()) {
      double weight = sample.getWeight();
      gtWeight += weight;

      lt.put(sample.getTarget(), 0);
      gt.put(sample.getTarget(), weight);
    }

    // Transfer weights from the initial example
    Example first = distances.get(0).example;
    String prevTarget = y.getAsString(first.getIndex());
    gt.addTo(prevTarget, -first.getWeight());
    lt.addTo(prevTarget, first.getWeight());
    gtWeight -= first.getWeight();
    ltWeight += first.getWeight();

    double prevDistance = distances.get(0).distance;
    double lowestImpurity = Double.POSITIVE_INFINITY;
    double threshold = distances.get(0).distance / 2;
    double ltGap = 0.0, gtGap = distanceSum, largestGap = Double.NEGATIVE_INFINITY;
    for (int i = 1; i < distances.size(); i++) {
      ExampleDistance ed = distances.get(i);
      String target = y.getAsString(ed.example.getIndex());

      // IF previous target NOT EQUALS current target and the previous distance equals the current
      // (except for the first)
      boolean notSameDistance = ed.distance != prevDistance;
      boolean firstOrEqualTarget = prevTarget == null || !prevTarget.equals(target);
      boolean firstIteration = i == 1;
      if (firstIteration || notSameDistance && firstOrEqualTarget) {

        // Generate the relative frequency distribution
        for (int j = 0; j < presentTargets.size(); j++) {
          String presentTarget = presentTargets.get(j);
          ltRelativeFrequency.set(j, ltWeight != 0 ? lt.get(presentTarget) / ltWeight : 0);
          gtRelativeFrequency.set(j, gtWeight != 0 ? gt.get(presentTarget) / gtWeight : 0);
        }

        // If this split is better, update the threshold
        double impurity =
            gain.compute(ltWeight, ltRelativeFrequency, gtWeight, gtRelativeFrequency);
        double gap = (1 / ltWeight * ltGap) - (1 / gtWeight * gtGap);
        boolean lowerImpurity = impurity < lowestImpurity;
        boolean equalImpuritySmallerGap = impurity == lowestImpurity && gap > largestGap;
        if (lowerImpurity || equalImpuritySmallerGap) {
          lowestImpurity = impurity;
          largestGap = gap;
          threshold = (ed.distance + prevDistance) / 2;
        }
      }

      /*
       * Move cursor one example forward, and adjust the weights accordingly. Then calculate the new
       * gain for moving the threshold. If this results in a cleaner split, adjust the threshold (by
       * taking the average of the current and the previous value).
       */
      double weight = ed.example.getWeight();
      ltWeight += weight;
      gtWeight -= weight;
      lt.addTo(target, weight);
      gt.addTo(target, -weight);

      ltGap += ed.distance;
      gtGap -= ed.distance;

      prevDistance = ed.distance;
      prevTarget = target;
    }

    double minimumMargin = Double.POSITIVE_INFINITY;
    return new Threshold(threshold, lowestImpurity, largestGap, minimumMargin);
  }

  private static class SplitPointTreeVisitor implements TreeVisitor<SplitPoint> {

    @Override
    public DoubleMatrix visitLeaf(TreeLeaf<SplitPoint> leaf, Vector example) {
      DoubleMatrix probabilities = leaf.getProbabilities();
      return probabilities;
    }

    @Override
    public DoubleMatrix visitBranch(TreeBranch<SplitPoint> node, Vector example) {
      double threshold = node.getThreshold().threshold;
      int[] index = node.getThreshold().index;
      Vector f = node.getThreshold().vector;
      if (computeDistance(index, index.length, f, example) < threshold) {
        return visit(node.getLeft(), example);
      } else {
        return visit(node.getRight(), example);
      }
    }
  }

  private static class Predictor extends AbstractPredictor {

    private final TreeVisitor<SplitPoint> splitPointTreeVisitor;
    private final TreeNode<SplitPoint> treeNode;

    protected Predictor(Vector classes,
                        TreeNode<SplitPoint> treeNode,
                        TreeVisitor<SplitPoint> splitPointTreeVisitor) {
      super(classes);
      this.treeNode = treeNode;
      this.splitPointTreeVisitor = splitPointTreeVisitor;
    }

    @Override
    public DoubleMatrix estimate(Vector record) {
      return splitPointTreeVisitor.visit(treeNode, record);
    }
  }

  private static class ExampleDistance implements Comparable<ExampleDistance> {

    public final double distance;
    public final Example example;

    public ExampleDistance(double distance, Example example) {
      this.distance = distance;
      this.example = example;
    }

    @Override
    public int compareTo(ExampleDistance o) {
      return Double.compare(distance, o.distance);
    }

    @Override
    public String toString() {
      return String.format("ExampleDistance(id=%d, %.2f)", example.getIndex(), distance);
    }
  }

  private static class SplitPoint {

    private final Vector vector;
    private final int[] index;
    private final double threshold;

    private SplitPoint(Vector vector, int[] index, double threshold) {
      this.vector = vector;
      this.index = index;
      this.threshold = threshold;
    }
  }
}
