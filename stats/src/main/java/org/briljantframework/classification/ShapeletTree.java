/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.classification;

import java.util.*;

import org.briljantframework.Check;
import org.briljantframework.Utils;
import org.briljantframework.classification.tree.*;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.Record;
import org.briljantframework.dataseries.Aggregator;
import org.briljantframework.dataseries.Approximations;
import org.briljantframework.dataseries.MeanAggregator;
import org.briljantframework.distance.Distance;
import org.briljantframework.distance.Euclidean;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.shapelet.EarlyAbandonSlidingDistance;
import org.briljantframework.shapelet.IndexSortedNormalizedShapelet;
import org.briljantframework.shapelet.Shapelet;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vectors;

import com.carrotsearch.hppc.*;
import com.carrotsearch.hppc.cursors.ObjectDoubleCursor;

/**
 * Created by Isak Karlsson on 16/09/14.
 */
public class ShapeletTree implements Classifier {

  protected final Random random = new Random();
  protected final Gain gain = Gain.INFO;

  private final ClassSet classSet;

  private final Distance metric;
  private final int inspectedShapelets;
  private final double alpha;
  private final double aggregateFraction;
  private final double minSplit;
  private final SampleMode sampleMode;
  private final Assessment assessment;
  private double lowerLength;
  private double upperLength;
  private Vector classes;

  protected ShapeletTree() {
    this(new Builder(), null, null);
  }

  public ShapeletTree(ClassSet classSet, Vector classes) {
    this(new Builder(), classSet, classes);
  }

  protected ShapeletTree(Builder builder, ClassSet classSet, Vector classes) {
    this.metric = builder.metric;
    this.inspectedShapelets = builder.inspectedShapelets;
    this.lowerLength = builder.lowerLength;
    this.upperLength = builder.upperLength;
    this.alpha = builder.alpha;
    this.aggregateFraction = builder.aggregateFraction;
    this.sampleMode = builder.sampleMode;
    this.assessment = builder.assessment;
    this.minSplit = builder.minSplit;

    Check.range(upperLength, lowerLength, 1);
    Check.range(lowerLength, 0, upperLength);
    this.classSet = classSet;
    this.classes = classes;
  }

  public ShapeletTree(double low, double high, Builder builder, ClassSet sample, Vector classes) {
    this(builder, sample, classes);
    this.lowerLength = low;
    this.upperLength = upperLength;
  }

  public Random getRandom() {
    return random;
  }

  public Gain getGain() {
    return gain;
  }

  public Distance getDistanceMetric() {
    return metric;
  }

  public int getInspectedShapelets() {
    return inspectedShapelets;
  }

  public double getLowerLength() {
    return lowerLength;
  }

  public double getUpperLength() {
    return upperLength;
  }

  public double getAlpha() {
    return alpha;
  }

  @Override
  public Predictor fit(DataFrame x, Vector y) {
    ClassSet classSet = this.classSet;
    Vector classes = this.classes != null ? this.classes : Vectors.unique(y);
    if (classSet == null) {
      classSet = new ClassSet(y, classes);
    }

    DataFrame dataFrame = x;
    if (sampleMode == SampleMode.DOWN_SAMPLE) {
      Check.range(aggregateFraction, 0.1, 1);
      dataFrame = Approximations.paa(x, (int) Math.round(x.columns() * aggregateFraction));
    }


    Params params = new Params();
    params.noExamples = classSet.getTotalWeight();
    params.lengthImportance = Matrices.newDoubleVector(x.columns());
    params.positionImportance = Matrices.newDoubleVector(x.columns());
    params.originalData = x;
    int size = Utils.randInt(10, x.columns() - 1);
    TreeNode<ShapeletThreshold> node = build(dataFrame, y, classSet, params);
    Predictor predictor =
        new Predictor(classes, node, new ShapletTreeVisitor(size, getDistanceMetric()),
            params.lengthImportance, params.positionImportance, params.depth, classSet);
    return predictor;
  }

  protected TreeNode<ShapeletThreshold> build(DataFrame x, Vector y, ClassSet classSet,
      Params params) {
    if (classSet.getTotalWeight() <= minSplit || classSet.getTargetCount() == 1) {
      return TreeLeaf.fromExamples(classSet);
    }

    params.depth += 1;
    TreeSplit<ShapeletThreshold> maxSplit = find(classSet, x, y, params);
    if (maxSplit == null) {
      return TreeLeaf.fromExamples(classSet);
    } else if (maxSplit.getLeft().isEmpty()) {
      return TreeLeaf.fromExamples(maxSplit.getRight());
    } else if (maxSplit.getRight().isEmpty()) {
      return TreeLeaf.fromExamples(maxSplit.getLeft());
    } else {
      Shapelet shapelet = maxSplit.getThreshold().getShapelet();
      Impurity impurity = getGain().getImpurity();

      double imp = impurity.impurity(classSet);
      double weight = (maxSplit.size() / params.noExamples) * (imp - maxSplit.getImpurity());

      params.lengthImportance.addTo(shapelet.size(), weight);
      int length = shapelet.size();
      int start = shapelet.start();
      int end = start + length;
      for (int i = start; i < end; i++) {
        params.positionImportance.update(i, v -> v + (weight / length));
      }

      TreeNode<ShapeletThreshold> leftNode = build(x, y, maxSplit.getLeft(), params);
      TreeNode<ShapeletThreshold> rightNode = build(x, y, maxSplit.getRight(), params);
      return new TreeBranch<>(leftNode, rightNode, maxSplit.getThreshold());
    }
  }

  public TreeSplit<ShapeletThreshold> find(ClassSet classSet, DataFrame x, Vector y, Params params) {
    int timeSeriesLength = params.originalData.columns();
    int upper = (int) Math.round(timeSeriesLength * upperLength);
    int lower = (int) Math.round(timeSeriesLength * lowerLength);
    if (lower < 2) {
      lower = 2;
    }

    if (Math.addExact(upper, lower) > timeSeriesLength) {
      upper = timeSeriesLength - lower;
    }
    if (lower == upper) {
      upper -= 2;
    }

    int maxShapelets = this.inspectedShapelets;
    if (maxShapelets < 0) {
      int length = upper - lower;
      maxShapelets = (int) Math.round(Math.sqrt(x.rows() * (length * (length + 1) / 2)));
    }
    List<Shapelet> shapelets = new ArrayList<>(maxShapelets);
    for (int i = 0; i < maxShapelets; i++) {
      int index = classSet.getRandomSample().getRandomExample().getIndex();
      Vector timeSeries = x.getRecord(index);
      int length = random.nextInt(upper) + lower;
      int start = random.nextInt(timeSeriesLength - length);
      if (sampleMode == SampleMode.DOWN_SAMPLE) {
        int downStart = (int) Math.round(start * aggregateFraction);
        int downLength = (int) Math.round(length * aggregateFraction);
        if (downStart + downLength > timeSeriesLength * aggregateFraction) {
          downLength -= 1;
        }
        shapelets.add(new DownsampledShapelet(index, start, length, downStart, downLength,
            timeSeries));
      }
      if (sampleMode == SampleMode.RANDOMIZE) {
        Vector.Builder meanVec = new DoubleVector.Builder();
        for (int j = 0; j < 10; j++) {
          Record record = x.getRecord(classSet.getRandomSample().getRandomExample().getIndex());
          Shapelet shapelet = new Shapelet(start, length, record);
          for (int k = 0; k < shapelet.size(); k++) {
            meanVec.set(k, shapelet.getAsDouble(k) / 10);
          }
        }

        IndexSortedNormalizedShapelet s =
            new IndexSortedNormalizedShapelet(0, meanVec.size(), meanVec.build());
        shapelets.add(s);
      } else {
        shapelets.add(new IndexSortedNormalizedShapelet(start, length, timeSeries));
      }
    }

    TreeSplit<ShapeletThreshold> bestSplit;
    if (assessment == Assessment.IG) {
      bestSplit = findBestSplit(classSet, x, y, shapelets);
    } else {
      bestSplit = findBestSplitFstat(classSet, x, y, shapelets);
    }
    if (sampleMode == SampleMode.DOWN_SAMPLE) {
      DownsampledShapelet best = (DownsampledShapelet) bestSplit.getThreshold().getShapelet();
      Shapelet shapelet =
          new IndexSortedNormalizedShapelet(best.start, best.length,
              params.originalData.getRecord(best.index));
      return findBestSplit(classSet, params.originalData, y, Arrays.asList(shapelet));
    } else {
      return bestSplit;
    }
  }

  protected TreeSplit<ShapeletThreshold> findBestSplit(ClassSet classSet, DataFrame x, Vector y,
      List<Shapelet> shapelets) {
    TreeSplit<ShapeletThreshold> bestSplit = null;
    Threshold bestThreshold = Threshold.inf();
    for (Shapelet shapelet : shapelets) {
      IntDoubleMap distanceMap = new IntDoubleOpenHashMap();
      Threshold threshold = bestDistanceThresholdInSample(classSet, x, y, shapelet, distanceMap);
      boolean lowerImpurity = threshold.impurity < bestThreshold.impurity;
      boolean equalImpuritySmallerGap =
          threshold.impurity == bestThreshold.impurity && threshold.gap > bestThreshold.gap;
      if (lowerImpurity || equalImpuritySmallerGap) {
        bestSplit = split(distanceMap, classSet, threshold.threshold, shapelet);
        bestThreshold = threshold;
      }
    }

    if (bestSplit != null) {
      bestSplit.setImpurity(bestThreshold.impurity);
    }
    return bestSplit;
  }

  protected Threshold bestDistanceThresholdInSample(ClassSet classSet, DataFrame x, Vector y,
      Shapelet shapelet, IntDoubleMap memoizedDistances) {

    double sum = 0.0;
    List<ExampleDistance> distances = new ArrayList<>();
    Distance distanceMetric = getDistanceMetric();
    for (Example example : classSet) {
      double distance = distanceMetric.compute(x.getRecord(example.getIndex()), shapelet);
      memoizedDistances.put(example.getIndex(), distance);
      distances.add(new ExampleDistance(distance, example));
      sum += distance;
    }

    Collections.sort(distances);
    return findBestThreshold(distances, classSet, y, sum);
  }

  protected TreeSplit<ShapeletThreshold> findBestSplitFstat(ClassSet classSet, DataFrame x,
      Vector y, List<Shapelet> shapelets) {
    IntDoubleMap bestDistanceMap = null;
    List<ExampleDistance> bestDistances = null;
    double bestStat = Double.NEGATIVE_INFINITY;
    Shapelet bestShapelet = null;
    double bestSum = 0;

    Distance metric = getDistanceMetric();
    for (Shapelet shapelet : shapelets) {
      List<ExampleDistance> distances = new ArrayList<>();
      IntDoubleMap distanceMap = new IntDoubleOpenHashMap();
      double sum = 0;
      for (Example example : classSet) {
        double dist = metric.compute(x.getRecord(example.getIndex()), shapelet);
        distanceMap.put(example.getIndex(), dist);
        distances.add(new ExampleDistance(dist, example));
        sum += dist;
      }
      double stat = assesShapeletQuality(distances, y);
      if (stat > bestStat) {
        bestStat = stat;
        bestDistanceMap = distanceMap;
        bestDistances = distances;
        bestShapelet = shapelet;
        bestSum = sum;
      }
    }

    Threshold t = findBestThreshold(bestDistances, classSet, y, bestSum);
    TreeSplit<ShapeletThreshold> split =
        split(bestDistanceMap, classSet, t.threshold, bestShapelet);
    split.setImpurity(t.impurity);
    return split;
  }

  private double assesShapeletQuality(List<ExampleDistance> distances, Vector y) {
    ObjectDoubleMap<String> sums = new ObjectDoubleOpenHashMap<>();
    ObjectDoubleMap<String> sumsSquared = new ObjectDoubleOpenHashMap<>();
    ObjectDoubleMap<String> sumOfSquares = new ObjectDoubleOpenHashMap<>();
    ObjectIntMap<String> sizes = new ObjectIntOpenHashMap<>();

    int numInstances = distances.size();
    for (ExampleDistance distance : distances) {
      String c = y.getAsString(distance.example.getIndex()); // getClassVal
      double thisDist = distance.distance; // getDistance
      sizes.addTo(c, 1);
      sums.addTo(c, thisDist); // sums[c] += thisDist
      sumOfSquares.addTo(c, thisDist * thisDist); // sumsOfSquares[c] += thisDist + thisDist
    }
    //
    double part1 = 0;
    double part2 = 0;
    for (ObjectDoubleCursor<String> sum : sums) {
      sumsSquared.put(sum.key, sum.value * sum.value); // sumsSquared[i] = sums[i] * sums[i]
      part1 += sumOfSquares.get(sum.key); // sumOfSquares[i]
      part2 += sum.value; // sums[i]
    }
    //
    part2 *= part2;
    part2 /= numInstances;
    double ssTotal = part1 - part2;
    //
    part1 = 0;
    part2 = 0;
    for (ObjectDoubleCursor<String> c : sumsSquared) {
      part1 += c.value / sizes.get(c.key);
      part2 += sums.get(c.key);
    }
    //
    double ssAmong = part1 - (part2 * part2) / numInstances;
    double ssWithin = ssTotal - ssAmong;
    //
    int dfAmong = sums.size() - 1;
    int dfWithin = numInstances - sums.size();
    //
    double msAmong = ssAmong / dfAmong;
    double msWithin = ssWithin / dfWithin;
    //
    double f = msAmong / msWithin;
    return Double.isNaN(f) ? 0 : f;
    // return Double.isNaN(f) ? 0 : f;

    // Map<String, DoubleVector.Builder> builders = new HashMap<>();
    // ObjectDoubleMap<String> meanClassDistance = new ObjectDoubleOpenHashMap<>();
    // double mean = 0;
    // int n = distances.size();
    //
    //
    // for (ExampleDistance distance : distances) {
    // String cls = y.getAsString(distance.example.getIndex());
    // builders.computeIfAbsent(cls, x -> new DoubleVector.Builder()).add(distance.distance);
    // meanClassDistance.addTo(cls, distance.distance);
    // mean += distance.distance;
    // }
    // mean = mean / n;
    //
    // double totalSS = 0;
    // for (ExampleDistance distance : distances) {
    // double diff = distance.distance - mean;
    // totalSS += diff * diff;
    // }
    //
    // System.out.println(builders.values().stream().mapToInt(Vector.Builder::size).sum() + " " +
    // n);
    // Map<String, DoubleVector> clsValues = new HashMap<>();
    // for (ObjectDoubleCursor<String> c : meanClassDistance) {
    // DoubleVector.Builder builder = builders.get(c.key);
    // DoubleVector meanVector = builder.build();
    // clsValues.put(c.key, meanVector);
    // meanClassDistance.put(c.key, c.value / meanVector.size());
    // }
    //
    // double noGroups = clsValues.size();
    // if (noGroups == n) {
    // return 0;
    // }
    //
    // double ssBetween = 0;
    // for (ObjectDoubleCursor<String> kv : meanClassDistance) {
    // double v = kv.value - mean;
    // double v1 = v * v;
    // int size = clsValues.get(kv.key).size();
    // ssBetween += size * v1;
    // }
    //
    // double ssWithin = 0;
    // for (Map.Entry<String, DoubleVector> kv : clsValues.entrySet()) {
    // DoubleVector d = kv.getValue();
    // double meanCj = meanClassDistance.get(kv.getKey());
    // for (int i = 0; i < d.size(); i++) {
    // double v = d.get(i) - meanCj;
    // ssWithin += (v * v);
    // }
    // }
    //
    // System.out.println((ssWithin + ssBetween) + " " + totalSS);
    // return Double.isNaN(ssWithin) ? 0 : (ssBetween / (noGroups - 1)) / (ssWithin / (n -
    // noGroups));
  }

  public Threshold findBestThreshold(List<ExampleDistance> distances, ClassSet classSet, Vector y,
      double distanceSum) {
    ObjectDoubleMap<String> lt = new ObjectDoubleOpenHashMap<>();
    ObjectDoubleMap<String> gt = new ObjectDoubleOpenHashMap<>();

    List<String> presentTargets = classSet.getTargets();
    DoubleMatrix ltRelativeFrequency = Matrices.newDoubleVector(presentTargets.size());
    DoubleMatrix gtRelativeFrequency = Matrices.newDoubleVector(presentTargets.size());

    double ltWeight = 0.0, gtWeight = 0.0;

    // Initialize all value to the right (i.e. all getPosteriorProbabilities are larger than the
    // initial threshold)
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
    Gain gain = getGain();
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

  protected TreeSplit<ShapeletThreshold> split(IntDoubleMap distanceMap, ClassSet classSet,
      double threshold, Shapelet shapelet) {
    ClassSet left = new ClassSet(classSet.getDomain());
    ClassSet right = new ClassSet(classSet.getDomain());

    /*
     * Partition every class separately
     */
    for (ClassSet.Sample sample : classSet.samples()) {
      String target = sample.getTarget();

      ClassSet.Sample leftSample = ClassSet.Sample.create(target);
      ClassSet.Sample rightSample = ClassSet.Sample.create(target);

      /*
       * STEP 1: Partition the examples according to threshold
       */
      for (Example example : sample) {
        double shapeletDistance = distanceMap.get(example.getIndex());
        if (shapeletDistance < threshold) {
          leftSample.add(example);
        } else {
          rightSample.add(example);
        }
      }

      /*
       * STEP 3: Ignore classes with no examples in the partition
       */
      if (!leftSample.isEmpty()) {
        left.add(leftSample);
      }
      if (!rightSample.isEmpty()) {
        right.add(rightSample);
      }
    }

    return new TreeSplit<>(left, right, new ShapeletThreshold(shapelet, threshold));
  }

  public enum SampleMode {
    DOWN_SAMPLE, NORMAL, RANDOMIZE
  }

  public enum Assessment {
    IG, FSTAT
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

  private static class DownsampledShapelet extends IndexSortedNormalizedShapelet {
    private final int start;
    private final int length;
    private final int index;

    public DownsampledShapelet(int index, int start, int length, int downStart, int downLength,
        Vector vector) {
      super(downStart, downLength, vector);

      this.start = start;
      this.length = length;
      this.index = index;
    }
  }

  protected static class Threshold {
    public final double threshold, impurity, gap, margin;

    public Threshold(double threshold, double impurity, double gap, double margin) {
      this.threshold = threshold;
      this.impurity = impurity;
      this.gap = gap;
      this.margin = margin;
    }

    public static Threshold inf() {
      return new Threshold(Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY,
          Double.NEGATIVE_INFINITY);
    }

    public boolean isBetterThan(Threshold bestThreshold) {
      return this.impurity < bestThreshold.impurity
          || (this.impurity == bestThreshold.impurity && this.gap > bestThreshold.gap);
    }
  }

  private static class Params {
    public double noExamples;
    public DataFrame originalData;
    private DoubleMatrix lengthImportance;
    private DoubleMatrix positionImportance;
    private int depth = 0;
  }

  public static class Predictor extends TreePredictor<ShapeletThreshold> {

    public final ClassSet classSet;
    private final int depth;
    private final DoubleMatrix lengthImportance;
    private final DoubleMatrix positionImportance;

    protected Predictor(Vector classes, TreeNode<ShapeletThreshold> node,
        ShapletTreeVisitor predictionVisitor, DoubleMatrix lengthImportance,
        DoubleMatrix positionImportance, int depth, ClassSet classSet) {
      super(classes, node, predictionVisitor);
      this.lengthImportance = lengthImportance;
      this.positionImportance = positionImportance;
      this.depth = depth;
      this.classSet = classSet;
    }

    /**
     * Gets position importance.
     *
     * @return the position importance
     */
    public DoubleMatrix getPositionImportance() {
      return positionImportance;
    }

    /**
     * Gets length importance.
     *
     * @return the length importance
     */
    public DoubleMatrix getLengthImportance() {
      return lengthImportance;
    }

    public int getDepth() {
      return depth;
    }
  }

  private static class ShapletTreeVisitor implements TreeVisitor<ShapeletThreshold> {

    private final Distance metric;
    private final Aggregator aggregator;

    private ShapletTreeVisitor(int size, Distance metric) {
      this.metric = metric;
      this.aggregator = new MeanAggregator(size);
    }

    @Override
    public DoubleMatrix visitLeaf(TreeLeaf<ShapeletThreshold> leaf, Vector example) {
      return leaf.getProbabilities();
    }

    @Override
    public DoubleMatrix visitBranch(TreeBranch<ShapeletThreshold> node, Vector example) {
      // aggregator.aggregate(example)
      if (metric.compute(example, node.getThreshold().getShapelet()) < node.getThreshold()
          .getDistance()) {
        return visit(node.getLeft(), example);
      } else {
        return visit(node.getRight(), example);
      }
    }
  }

  public static class Builder implements Classifier.Builder<ShapeletTree> {

    public Assessment assessment = Assessment.FSTAT;
    public double minSplit = 1;
    public Distance metric = EarlyAbandonSlidingDistance.create(Euclidean.getInstance());
    public int inspectedShapelets = 100;
    public double aggregateFraction = 0.5;
    public SampleMode sampleMode = SampleMode.NORMAL;
    public double lowerLength = 0.01;
    public double upperLength = 1;
    public double alpha = 0.5;

    public Builder withMinSplit(double minSplit) {
      this.minSplit = minSplit;
      return this;
    }

    public Builder withDistance(Distance metric) {
      this.metric = metric;
      return this;

    }

    public Builder withAssessment(Assessment assessment) {
      this.assessment = assessment;
      return this;
    }

    public Builder withInspectedShapelets(int inspectedShapelets) {
      this.inspectedShapelets = inspectedShapelets;
      return this;

    }

    public Builder withLowerLength(double lowerLength) {
      this.lowerLength = lowerLength;
      return this;
    }

    public Builder withUpperLength(double upperLength) {
      this.upperLength = upperLength;
      return this;

    }

    public Builder withAlpha(double alpha) {
      this.alpha = alpha;
      return this;
    }

    public Builder withMode(SampleMode sampleMode) {
      this.sampleMode = sampleMode;
      return this;
    }

    public Builder withAggregateFraction(double aggregateFraction) {
      this.aggregateFraction = aggregateFraction;
      return this;
    }

    public ShapeletTree build() {
      return new ShapeletTree();
    }
  }

}
