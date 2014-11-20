/*
 * ADEB - machine learning pipelines made easy
 * Copyright (C) 2014  Isak Karlsson
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.briljantframework.learning.time;

import com.carrotsearch.hppc.IntDoubleMap;
import com.carrotsearch.hppc.IntDoubleOpenHashMap;
import com.carrotsearch.hppc.ObjectDoubleMap;
import com.carrotsearch.hppc.ObjectDoubleOpenHashMap;
import org.briljantframework.data.column.Column;
import org.briljantframework.data.values.Value;
import org.briljantframework.learning.example.Example;
import org.briljantframework.learning.example.Examples;
import org.briljantframework.learning.tree.Gain;
import org.briljantframework.learning.tree.Splitter;
import org.briljantframework.learning.tree.Tree;
import org.briljantframework.matrix.RowVector;
import org.briljantframework.matrix.dataset.MatrixDataFrame;
import org.briljantframework.matrix.distance.Distance;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by Isak Karlsson on 17/09/14.
 */
public class RandomShapeletSplitter extends ShapeletSplitter {
    /**
     * The Metric.
     */
//    final Distance metric;
    private final Random random = new Random();
    private final int inspectedShapelets;

    private final int lowerLength, upperLength;
    private final double alpha;

    /**
     * Instantiates a new Random shapelet splitter.
     *
     * @param builder the builder
     */
    public RandomShapeletSplitter(Builder builder) {
        super(builder.distance.create(), builder.gain);
//        this.metric = builder.distance.create();
        this.inspectedShapelets = builder.shapelets;
        this.upperLength = builder.upper;
        this.lowerLength = builder.lower;
        this.alpha = builder.alpha;
    }

    /**
     * With distance.
     *
     * @param distance the distance
     * @return the builder
     */
    public static Builder withDistance(Distance distance) {
        return new Builder(() -> distance);
    }

    /**
     * Gets inspected shapelets.
     *
     * @return the inspected shapelets
     */
    public int getInspectedShapelets() {
        return inspectedShapelets;
    }

    /**
     * Gets lower length.
     *
     * @return the lower length
     */
    public int getLowerLength() {
        return lowerLength;
    }

    /**
     * Gets upper length.
     *
     * @return the upper length
     */
    public int getUpperLength() {
        return upperLength;
    }

    /**
     * Gets alpha.
     *
     * @return the alpha
     */
    public double getAlpha() {
        return alpha;
    }

    @Override
    public Tree.Split<ShapeletThreshold> find(Examples examples, MatrixDataFrame dataset, Column column) {
        int timeSeriesLength = dataset.columns();
        int upper = this.upperLength;
        int lower = this.lowerLength;

        if (upper < 0) {
            upper = timeSeriesLength;
        }

        if (Math.addExact(upper, lower) > timeSeriesLength) {
            upper = timeSeriesLength - lower;
        }

        int maxShapelets = this.inspectedShapelets;
        if (maxShapelets < 0) {
            int length = upper - lower;
            maxShapelets = (int) Math.round(Math.sqrt((length * (length + 1) / 2)));
        }

        List<Shapelet> shapelets = new ArrayList<>(maxShapelets);
        for (int i = 0; i < maxShapelets; i++) {
            RowVector timeSeries = dataset.getRow(examples.getRandomSample().getRandomExample().getIndex());
            int length = random.nextInt(upper) + lower;
            int start = random.nextInt(timeSeriesLength - length);
            shapelets.add(new IndexSortedNormalizedShapelet(start, length, timeSeries));
        }


        return findBestSplit(examples, dataset, column, shapelets);
    }

    /**
     * Find best split.
     *
     * @param examples  the examples
     * @param container the storage
     * @param column
     * @param shapelets the shapelets  @return the tree . split
     */
    protected Tree.Split<ShapeletThreshold> findBestSplit(Examples examples, MatrixDataFrame container,
                                                          Column column, List<Shapelet> shapelets) {
        Tree.Split<ShapeletThreshold> bestSplit = null;
        Threshold bestThreshold = Threshold.create(Double.NaN, Double.POSITIVE_INFINITY,
                Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, null);

        for (Shapelet shapelet : shapelets) {
            IntDoubleMap distanceMap = new IntDoubleOpenHashMap();
            Threshold threshold = bestDistanceThresholdInSample(examples, container, column,
                    shapelet, distanceMap);

            // TODO(isak) - don't split before we know the split is good
            Tree.Split<ShapeletThreshold> split = split(distanceMap, examples, threshold.threshold, shapelet);
            if (threshold.impurity < bestThreshold.impurity || (threshold.impurity == bestThreshold.impurity &&
                    threshold.gap > bestThreshold.gap)) {
                bestSplit = split;
                bestThreshold = threshold;
            }
        }

        if (bestSplit != null) {
            bestSplit.setImpurity(new double[]{
                    bestThreshold.impurity,
                    bestThreshold.leftRight[0],
                    bestThreshold.leftRight[1]
            });

        }
        return bestSplit;
    }

    /**
     * Best distance threshold in sample.
     *
     * @param examples    the examples
     * @param frame       the frame
     * @param column      the target
     * @param shapelet    the shapelet
     * @param distanceMap the distance map
     * @return the double [ ]
     */
    protected Threshold bestDistanceThresholdInSample(Examples examples, MatrixDataFrame frame, Column column,
                                                      Shapelet shapelet, IntDoubleMap distanceMap) {

        List<ExampleDistance> exampleDistances = new ArrayList<>();
        double distanceSum = 0.0;
        for (Example example : examples) {
            double distance = getDistanceMetric().distance(frame.getRow(example.getIndex()), shapelet);
            distanceSum += distance;

            exampleDistances.add(ExampleDistance.create(distance, example));
            distanceMap.put(example.getIndex(), distance);
        }

        Collections.sort(exampleDistances);
        return findBestThreshold(exampleDistances, examples, column, distanceSum);
    }

    /**
     * Basic implementation of the splitting procedure
     *
     * @param distanceMap the distance map (mapping examples to distances to the shapelet)
     * @param examples    the examples
     * @param threshold   the threshold
     * @param shapelet    the shapelet
     * @return the examples . split
     */
    protected Tree.Split<ShapeletThreshold> split(IntDoubleMap distanceMap, Examples examples,
                                                  double threshold, Shapelet shapelet) {
        Examples left = Examples.create();
        Examples right = Examples.create();

        /*
         * Partition every class separately
         */
        for (Examples.Sample sample : examples.samples()) {
            Value target = sample.getTarget();

            Examples.Sample leftSample = Examples.Sample.create(target);
            Examples.Sample rightSample = Examples.Sample.create(target);

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

        return new Tree.Split<>(left, right, ShapeletThreshold.create(shapelet, threshold));
    }

    /**
     * Find best threshold.
     *
     * @param exampleDistances the example distances
     * @param examples         the examples
     * @param targets          the targets
     * @param distanceSum      the distance sum
     * @return the double [ ]
     */
    public Threshold findBestThreshold(List<ExampleDistance> exampleDistances, Examples examples,
                                       Column targets, double distanceSum) {
        ObjectDoubleMap<Value> lt = new ObjectDoubleOpenHashMap<>();
        ObjectDoubleMap<Value> gt = new ObjectDoubleOpenHashMap<>();

        List<Value> presentTargets = examples.getTargets();
        double[] ltRelativeFrequency = new double[presentTargets.size()];
        double[] gtRelativeFrequency = new double[presentTargets.size()];

        double ltWeight = 0.0, gtWeight = 0.0;

        // Initialize all value to the right (i.e. all values are larger than the initial threshold)
        for (Examples.Sample sample : examples.samples()) {
            double weight = sample.getWeight();
            gtWeight += weight;

            lt.put(sample.getTarget(), 0);
            gt.put(sample.getTarget(), weight);
        }


        // Transfer weights from the initial example
        Example first = exampleDistances.get(0).example;
        Value prevTarget = targets.getValue(first.getIndex());
        gt.addTo(prevTarget, -first.getWeight());
        lt.addTo(prevTarget, first.getWeight());
        gtWeight -= first.getWeight();
        ltWeight += first.getWeight();

        double[] leftRight = new double[2];
        double[] bestLeftRight = new double[2];

        double prevDistance = exampleDistances.get(0).distance;
        double lowestImpurity = Double.POSITIVE_INFINITY;
        double threshold = exampleDistances.get(0).distance / 2;
        double ltGap = 0.0, gtGap = distanceSum, largestGap = Double.NEGATIVE_INFINITY;
        for (int i = 1; i < exampleDistances.size(); i++) {
            ExampleDistance ed = exampleDistances.get(i);
            Value target = targets.getValue(ed.example.getIndex());


            // IF previous target NOT EQUALS current target and the previous distance equals the current (except for
            // the first)
            if (i == 1 || (ed.distance != prevDistance && (prevTarget == null || !prevTarget.equals(target)))) {

                // Generate the relative frequency distribution
                for (int j = 0; j < presentTargets.size(); j++) {
                    Value presentTarget = presentTargets.get(j);
                    ltRelativeFrequency[j] = ltWeight != 0 ? lt.get(presentTarget) / ltWeight : 0;
                    gtRelativeFrequency[j] = gtWeight != 0 ? gt.get(presentTarget) / gtWeight : 0;
                }

                // If this split is better, update the threshold
                double impurity = getGain().calculate(ltWeight, ltRelativeFrequency, gtWeight,
                        gtRelativeFrequency, leftRight);
                double gap = (1 / ltWeight * ltGap) - (1 / gtWeight * gtGap);
                if (impurity < lowestImpurity || (impurity == lowestImpurity && gap > largestGap)) {
                    lowestImpurity = impurity;
                    largestGap = gap;
                    bestLeftRight = new double[]{leftRight[0], leftRight[1]};
                    threshold = (ed.distance + prevDistance) / 2;
                }
            }
            /*
                Move cursor one example forward, and adjust the weights accordingly
                  p = previous, c = current

                     p   c              p   c
               CLS : a   a b b b      a a   b b b
               VAL : 0 | 1 2 3 4   => 0 1 | 2 3 4
               CNT : 1      4          2      3
               FREQ: 1/1   3/4        2/2    3/3

                Then calculate the new gain for moving the threshold. If this results
                in a cleaner split, adjust the threshold (by taking the average of the
                current and the previous value).
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
//        for (ExampleDistance exampleDistance : exampleDistances) {
//            double residual = Math.abs(threshold - exampleDistance.distance);
//            if (residual < minimumMargin) {
//                minimumMargin = residual;
//            }
//        }

        return Threshold.create(threshold, lowestImpurity, largestGap, minimumMargin, bestLeftRight);
    }

    /**
     * The type Example distance.
     */
    public static class ExampleDistance implements Comparable<ExampleDistance> {
        /**
         * The Distance.
         */
        public final double distance;
        /**
         * The Example.
         */
        public final Example example;

        private ExampleDistance(double distance, Example example) {
            this.distance = distance;
            this.example = example;
        }

        /**
         * Create example distance.
         *
         * @param distance the distance
         * @param example  the example
         * @return the example distance
         */
        public static ExampleDistance create(double distance, Example example) {
            return new ExampleDistance(distance, example);
        }

        @Override
        public int compareTo(ExampleDistance o) {
            return Double.compare(distance, o.distance);
        }

        @Override
        public String toString() {
            return String.format("ED(id=%d, %.2f)", example.getIndex(), distance);
        }
    }

    /**
     * The type Builder.
     */
    public static class Builder implements Splitter.Builder<RandomShapeletSplitter> {

        private int shapelets = 10;
        private int sampleSize = -1;
        private Gain gain = Gain.INFO;
        private int upper = -1;
        private int lower = 2;
        private Distance.Builder distance;
        private double alpha;

        private Builder(Distance.Builder distance) {
            this.distance = distance;
        }

        /**
         * Distance builder.
         *
         * @param distance the distance
         * @return the builder
         */
        public Builder withDistance(Distance distance) {
            this.distance = () -> distance;
            return this;
        }

        /**
         * Distance builder.
         *
         * @param distance the distance
         * @return the builder
         */
        public Builder withDistance(Distance.Builder distance) {
            this.distance = distance;
            return this;
        }

        /**
         * Upper builder.
         *
         * @param upper the setUpperLength
         * @return the builder
         */
        public Builder withUpperLength(int upper) {
            this.upper = upper;
            return this;
        }

        /**
         * Lower builder.
         *
         * @param lower the setLowerLength
         * @return the builder
         */
        public Builder withLowerLength(int lower) {
            this.lower = lower;
            return this;
        }

        /**
         * Sample size.
         *
         * @param sampleSize the sample size
         * @return the builder
         */
        public Builder withSampleSize(int sampleSize) {
            this.sampleSize = sampleSize;
            return this;
        }

        /**
         * Alpha builder.
         *
         * @param alpha the setAlpha
         * @return the builder
         */
        public Builder withAlpha(double alpha) {
            this.alpha = alpha;
            return this;
        }

        /**
         * Max shapelets.
         *
         * @param maxShapelets the max shapelets
         * @return the builder
         */
        public Builder withInspectedShapelets(int maxShapelets) {
            this.shapelets = maxShapelets;
            return this;
        }

        @Override
        public RandomShapeletSplitter create() {
            return new RandomShapeletSplitter(this);
        }
    }

    private static class Threshold {
        /**
         * The Threshold.
         */
        public final double threshold,

        /**
         * The Impurity.
         */
        impurity,

        /**
         * The Gap.
         */
        gap,

        /**
         * The Margin.
         */
        margin;

        public final double[] leftRight;

        private Threshold(double threshold, double impurity, double gap, double margin, double[] leftRight) {
            this.threshold = threshold;
            this.impurity = impurity;
            this.gap = gap;
            this.margin = margin;
            this.leftRight = leftRight;
        }

        private static Threshold create(double threshold, double impurity, double gap, double margin,
                                        double[] leftRight) {
            return new Threshold(threshold, impurity, gap, margin, leftRight);
        }

        /**
         * Better than.
         *
         * @param bestThreshold the best threshold
         * @return the boolean
         */
        public boolean isBetterThan(Threshold bestThreshold) {
            return this.impurity < bestThreshold.impurity || (this.impurity == bestThreshold.impurity && this.gap >
                    bestThreshold.gap);
        }
    }
}
