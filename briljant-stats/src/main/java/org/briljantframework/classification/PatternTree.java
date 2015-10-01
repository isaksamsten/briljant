/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.briljantframework.classification;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.math3.util.MathArrays;
import org.briljantframework.ArrayUtils;
import org.briljantframework.Bj;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.classification.tree.ClassSet;
import org.briljantframework.classification.tree.Example;
import org.briljantframework.classification.tree.Gain;
import org.briljantframework.classification.tree.TreeBranch;
import org.briljantframework.classification.tree.TreeLeaf;
import org.briljantframework.classification.tree.TreeNode;
import org.briljantframework.classification.tree.TreeSplit;
import org.briljantframework.classification.tree.TreeVisitor;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Vectors;

import com.carrotsearch.hppc.IntDoubleMap;
import com.carrotsearch.hppc.IntDoubleOpenHashMap;
import com.carrotsearch.hppc.ObjectDoubleMap;
import com.carrotsearch.hppc.ObjectDoubleOpenHashMap;

/**
 * Created by isak on 18/03/15.
 */
public class PatternTree implements Classifier {

  private final Mean mean = new Mean();
  private final Std std = new Std();
  private final Kurtosis kurtosis = new Kurtosis();
  private final Min min = new Min();
  private final Max max = new Max();
  private final AutoCorrelation autoCorrelation = new AutoCorrelation();

  private List<Feature> features = Arrays.asList(mean, std, kurtosis, min, max, autoCorrelation);

  private ClassSet classSet = null;
  private Vector classes = null;
  private Gain gain = Gain.INFO;

  private int[] index = null;

  public PatternTree(Vector classes, ClassSet classSet) {
    this.classes = classes;
    this.classSet = classSet;
  }

  @Override
  public Predictor fit(DataFrame x, Vector y) {
    if (classes == null) {
      classes = Vectors.unique(y);
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
    } else if (split.getLeft().isEmpty()) {
      return TreeLeaf.fromExamples(split.getRight());
    } else if (split.getRight().isEmpty()) {
      return TreeLeaf.fromExamples(split.getLeft());
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
      return new TreeBranch<>(left, right, classes, split.getThreshold(), 1);
    }
  }

  private TreeSplit<SplitPoint> findSplit(DataFrame x, Vector y, ClassSet classSet) {
    FeatureThreshold bestThreshold = new FeatureThreshold(Double.NaN, Double.POSITIVE_INFINITY);
    TreeSplit<SplitPoint> bestSplit = null;
    for (int i = 0; i < 50; i++) {
      int take = 1;
      ArrayUtils.shuffle(index);
      MathArrays.shuffle(index);
      for (Feature feature : features) {
        IntDoubleMap featureValues = new IntDoubleOpenHashMap();
        FeatureThreshold threshold =
            bestFeatureForIndex(classSet, x, y, take, feature, featureValues);
        boolean lowerImpurity = threshold.impurity < bestThreshold.impurity;
        if (lowerImpurity) {
          bestSplit = split(featureValues, classSet, take, threshold.threshold, feature);
          bestThreshold = threshold;
        }
      }
    }

    if (bestSplit != null) {
      bestSplit.setImpurity(bestThreshold.impurity);
    }
    return bestSplit;
  }

  private TreeSplit<SplitPoint> split(IntDoubleMap featureMap, ClassSet classSet, int take,
      double threshold, Feature feature) {
    ClassSet left = new ClassSet(classSet.getDomain());
    ClassSet right = new ClassSet(classSet.getDomain());
    for (ClassSet.Sample sample : classSet.samples()) {
      Object target = sample.getTarget();

      ClassSet.Sample leftSample = ClassSet.Sample.create(target);
      ClassSet.Sample rightSample = ClassSet.Sample.create(target);

      for (Example example : sample) {
        double featureValue = featureMap.get(example.getIndex());
        if (featureValue == threshold) {
          if (ThreadLocalRandom.current().nextDouble() <= 0.5) {
            leftSample.add(example);
          } else {
            rightSample.add(example);
          }
        } else if (featureValue < threshold) {
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
    return new TreeSplit<>(left, right, new SplitPoint(Arrays.copyOf(index, take), threshold,
        feature));
  }

  protected FeatureThreshold bestFeatureForIndex(ClassSet classSet, DataFrame x, Vector y,
      int take, Feature feature, IntDoubleMap featureValues) {
    double sum = 0.0;
    List<ExampleValue> distances = new ArrayList<>();
    for (Example example : classSet) {
      Vector second = x.loc().getRecord(example.getIndex());
      double featureValue = feature.compute(second, index, take);
      featureValues.put(example.getIndex(), featureValue);
      distances.add(new ExampleValue(featureValue, feature, example));
      sum += featureValue;
    }

    Collections.sort(distances);
    return findBestThreshold(distances, classSet, y, sum);
  }

  public FeatureThreshold findBestThreshold(List<ExampleValue> distances, ClassSet classSet,
      Vector y, double distanceSum) {
    ObjectDoubleMap<Object> lt = new ObjectDoubleOpenHashMap<>();
    ObjectDoubleMap<Object> gt = new ObjectDoubleOpenHashMap<>();

    List<Object> presentTargets = classSet.getTargets();
    DoubleArray ltRelativeFrequency = Bj.doubleArray(presentTargets.size());
    DoubleArray gtRelativeFrequency = Bj.doubleArray(presentTargets.size());

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
    Object prevTarget = y.loc().get(Object.class, first.getIndex());
    gt.addTo(prevTarget, -first.getWeight());
    lt.addTo(prevTarget, first.getWeight());
    gtWeight -= first.getWeight();
    ltWeight += first.getWeight();

    double prevDistance = distances.get(0).distance;
    double lowestImpurity = Double.POSITIVE_INFINITY;
    double threshold = distances.get(0).distance / 2;
    for (int i = 1; i < distances.size(); i++) {
      ExampleValue ed = distances.get(i);
      Object target = y.loc().get(Object.class, ed.example.getIndex());

      // IF previous target NOT EQUALS current target and the previous distance equals the current
      // (except for the first)
      boolean notSameDistance = ed.distance != prevDistance;
      boolean firstOrEqualTarget = prevTarget == null || !prevTarget.equals(target);
      boolean firstIteration = i == 1;
      if (firstIteration || notSameDistance && firstOrEqualTarget) {

        // Generate the relative frequency distribution
        for (int j = 0; j < presentTargets.size(); j++) {
          Object presentTarget = presentTargets.get(j);
          ltRelativeFrequency.set(j, ltWeight != 0 ? lt.get(presentTarget) / ltWeight : 0);
          gtRelativeFrequency.set(j, gtWeight != 0 ? gt.get(presentTarget) / gtWeight : 0);
        }

        // If this split is better, update the threshold
        double impurity =
            gain.compute(ltWeight, ltRelativeFrequency, gtWeight, gtRelativeFrequency);
        if (impurity < lowestImpurity) {
          lowestImpurity = impurity;
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

      prevDistance = ed.distance;
      prevTarget = target;
    }

    double minimumMargin = Double.POSITIVE_INFINITY;
    return new FeatureThreshold(threshold, lowestImpurity);
  }

  private interface Feature {

    double compute(Vector vector, int[] indexes, int size);
  }

  private static class SplitPointTreeVisitor implements TreeVisitor<SplitPoint> {

    @Override
    public DoubleArray visitLeaf(TreeLeaf<SplitPoint> leaf, Vector example) {
      DoubleArray probabilities = leaf.getProbabilities();
      return probabilities;
    }

    @Override
    public DoubleArray visitBranch(TreeBranch<SplitPoint> node, Vector example) {
      double threshold = node.getThreshold().threshold;
      int[] index = node.getThreshold().index;
      Feature f = node.getThreshold().feature;
      if (f.compute(example, index, index.length) < threshold) {
        return visit(node.getLeft(), example);
      } else {
        return visit(node.getRight(), example);
      }
    }
  }

  private static class Predictor extends AbstractPredictor {

    private final TreeVisitor<SplitPoint> splitPointTreeVisitor;
    private final TreeNode<SplitPoint> treeNode;

    protected Predictor(Vector classes, TreeNode<SplitPoint> treeNode,
        TreeVisitor<SplitPoint> splitPointTreeVisitor) {
      super(classes);
      this.treeNode = treeNode;
      this.splitPointTreeVisitor = splitPointTreeVisitor;
    }

    @Override
    public DoubleArray estimate(Vector record) {
      return splitPointTreeVisitor.visit(treeNode, record);
    }
  }

  private static class ExampleValue implements Comparable<ExampleValue> {

    public final double distance;
    public final Feature feature;
    public final Example example;

    public ExampleValue(double distance, Feature feature, Example example) {
      this.distance = distance;
      this.feature = feature;
      this.example = example;
    }

    @Override
    public int compareTo(ExampleValue o) {
      return Double.compare(distance, o.distance);
    }

    @Override
    public String toString() {
      return String.format("ExampleDistance(id=%d, %.2f)", example.getIndex(), distance);
    }
  }

  private static class SplitPoint {

    private final int[] index;
    private final double threshold;
    private final Feature feature;

    private SplitPoint(int[] index, double threshold, Feature feature) {
      this.index = index;
      this.threshold = threshold;
      this.feature = feature;
    }
  }

  public class Mean implements Feature {

    @Override
    public double compute(Vector vector, int[] indexes, int size) {
      double mean = 0;
      for (int i = 0; i < size; i++) {
        int idx = indexes[i];
        mean += vector.loc().getAsDouble(idx);
      }
      return mean / size;
    }
  }

  public class Max implements Feature {


    @Override
    public double compute(Vector vector, int[] indexes, int size) {
      double max = Double.NEGATIVE_INFINITY;
      for (int i = 0; i < size; i++) {
        int idx = indexes[i];
        double v = vector.loc().getAsDouble(idx);
        if (v > max) {
          max = v;
        }
      }
      return max;
    }
  }

  public class Min implements Feature {


    @Override
    public double compute(Vector vector, int[] indexes, int size) {
      double min = Double.POSITIVE_INFINITY;
      for (int i = 0; i < size; i++) {
        int idx = indexes[i];
        double v = vector.loc().getAsDouble(idx);
        if (v < min) {
          min = v;
        }
      }
      return min;
    }
  }

  public class Std implements Feature {

    @Override
    public double compute(Vector vector, int[] indexes, int size) {
      double sum = 0, sumS = 0;
      for (int i = 0; i < size; i++) {
        int idx = indexes[i];
        double v = vector.loc().getAsDouble(idx);
        sum += v;
        sumS += v * v;
      }
      return Math.sqrt((sumS - (sum * sum) / size) / size);
    }
  }

  public class AutoCorrelation implements Feature {

    private int k = 2;

    @Override
    public double compute(Vector vector, int[] indexes, int size) {
      double u = mean.compute(vector, indexes, size);
      double s = std.compute(vector, indexes, size);
      double auto = 0;
      for (int i = 0; i < size - k; i++) {
        int idx1 = indexes[i];
        int idx2 = indexes[i + k];
        double v1 = vector.loc().getAsDouble(idx1);
        double v2 = vector.loc().getAsDouble(idx2);
        auto += (v1 - u) * (v2 - u);
      }
      return auto / ((size - k) * s);
    }
  }

  public class Kurtosis implements Feature {

    @Override
    public double compute(Vector vector, int[] indexes, int n) {
      double mean = 0;
      for (int i = 0; i < n; i++) {
        int idx = indexes[i];
        mean += vector.loc().getAsDouble(idx);
      }
      mean /= n;

      double k = 0, m4 = 0, m2 = 0;
      for (int i = 0; i < n; i++) {
        int idx = indexes[i];
        double v = vector.loc().getAsDouble(idx) - mean;
        m4 += Math.pow(v, 4);
        m2 += v * v;
      }
      return (m4 / n) / ((m2 / n) * (m2 / n));
    }
  }

  private class FeatureThreshold {

    private final double threshold;
    private final double impurity;


    private FeatureThreshold(double threshold, double impurity) {
      this.threshold = threshold;
      this.impurity = impurity;
    }
  }
}
