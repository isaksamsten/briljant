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

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

import org.briljantframework.classification.tree.Examples;
import org.briljantframework.classification.tree.RandomShapeletSplitter;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.distance.Distance;
import org.briljantframework.distance.Euclidean;
import org.briljantframework.matrix.DefaultDoubleMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.shapelet.EarlyAbandonSlidingDistance;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 24/09/14.
 */
public class RandomShapeletForest extends AbstractEnsemble {

  private final ShapeletTree.Builder tree;

  private RandomShapeletForest(Builder builder) {
    super(builder.size);
    this.tree = builder.tree;
  }

  /**
   * Size builder.
   *
   * @param size the size
   * @return the builder
   */
  public static Builder withSize(int size) {
    return new Builder().withSize(size);
  }

  @Override
  public Model fit(DataFrame x, Vector y) {
    Examples examples = Examples.fromVector(y);
    List<FitTask> tasks = new ArrayList<>();
    for (int i = 0; i < size(); i++) {
      tasks.add(new FitTask(examples, x, y, tree));
    }

    // double noModels = model.getModels().size();
    double[] averageLengthImportance = null;
    double[] averagePositionImportance = null;
    List<ShapeletTree.Model> models;
    try {
      models = execute(tasks);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    for (ShapeletTree.Model m : models) {
      DoubleMatrix lengthImportance = m.getLengthImportance();
      DoubleMatrix positionImportance = m.getPositionImportance();

      if (averageLengthImportance == null) {
        averageLengthImportance = new double[lengthImportance.columns()];
        averagePositionImportance = new double[positionImportance.columns()];
      }
      for (int i = 0; i < averageLengthImportance.length; i++) {
        averageLengthImportance[i] =
            averageLengthImportance[i] + (lengthImportance.get(i) / size());
        averagePositionImportance[i] =
            averagePositionImportance[i] + (positionImportance.get(i) / size());
      }
    }


    return new Model(models, DefaultDoubleMatrix.rowVector(averageLengthImportance),
        DefaultDoubleMatrix.rowVector(averagePositionImportance));
  }

  @Override
  public String toString() {
    return "Ensemble of Randomized Shapelet Trees";
  }

  private static final class FitTask implements Callable<ShapeletTree.Model> {

    private final Examples examples;
    private final DataFrame x;
    private final Vector y;
    private final ShapeletTree.Builder builder;


    private FitTask(Examples examples, DataFrame x, Vector y, ShapeletTree.Builder builder) {
      this.examples = examples;
      this.x = x;
      this.y = y;
      this.builder = builder;
    }

    @Override
    public ShapeletTree.Model call() throws Exception {
      Random random = new Random(Thread.currentThread().getId() * System.currentTimeMillis());
      return builder.create(sample(examples, random)).fit(x, y);
    }

    public Examples sample(Examples examples, Random random) {
      Examples inBag = Examples.create();
      for (Examples.Sample sample : examples.samples()) {
        Examples.Sample inSample = Examples.Sample.create(sample.getTarget());
        int[] bootstrap = bootstrap(sample, random);
        for (int i = 0; i < bootstrap.length; i++) {
          if (bootstrap[i] > 0) {
            inSample.add(sample.get(i).updateWeight(bootstrap[i]));
          }
        }
        inBag.add(inSample);
      }
      return inBag;
    }

    private int[] bootstrap(Examples.Sample sample, Random random) {
      int[] bootstrap = new int[sample.size()];
      for (int i = 0; i < bootstrap.length; i++) {
        bootstrap[random.nextInt(bootstrap.length)]++;
      }

      return bootstrap;
    }
  }

  /**
   * The type Model.
   */
  public static class Model extends AbstractEnsemble.Model {

    private final DefaultDoubleMatrix lengthImportance;
    private final DefaultDoubleMatrix positionImportance;

    /**
     * Instantiates a new Model.
     *
     * @param lengthImportance the length importance
     * @param positionImportance the position importance
     */
    public Model(List<? extends ClassifierModel> models, DefaultDoubleMatrix lengthImportance,
        DefaultDoubleMatrix positionImportance) {
      super(models);
      this.lengthImportance = lengthImportance;
      this.positionImportance = positionImportance;
    }

    /**
     * Gets length importance.
     *
     * @return the length importance
     */
    public DoubleMatrix getLengthImportance() {
      return lengthImportance;
    }

    /**
     * Gets position importance.
     *
     * @return the position importance
     */
    public DoubleMatrix getPositionImportance() {
      return positionImportance;
    }
  }

  /**
   * The type Builder.
   */
  public static class Builder implements Classifier.Builder<RandomShapeletForest> {

    private final RandomShapeletSplitter.Builder randomShapeletSplitter = RandomShapeletSplitter
        .withDistance(new EarlyAbandonSlidingDistance(Euclidean.getInstance()));

    private final ShapeletTree.Builder tree = ShapeletTree.withSplitter(randomShapeletSplitter);
    private int size = 100;

    // private final AbstractEnsemble.Builder ensemble = AbstractEnsemble.withMember(
    // ShapeletTree.withSplitter(randomShapeletSplitter)).withSampler(BootstrapSample.create());

    /**
     * Lower builder.
     *
     * @param lower the setLowerLength
     * @return the builder
     */
    public Builder withLowerLength(int lower) {
      randomShapeletSplitter.withLowerLength(lower);
      return this;
    }

    /**
     * Sample size.
     *
     * @param sampleSize the sample size
     * @return the builder
     */
    public Builder withSampleSize(int sampleSize) {
      randomShapeletSplitter.withSampleSize(sampleSize);
      return this;
    }

    /**
     * Shapelets builder.
     *
     * @param maxShapelets the max shapelets
     * @return the builder
     */
    public Builder withInspectedShapelets(int maxShapelets) {
      randomShapeletSplitter.withInspectedShapelets(maxShapelets);
      return this;
    }

    /**
     * Distance builder.
     *
     * @param distance the distance
     * @return the builder
     */
    public Builder withDistance(Distance distance) {
      randomShapeletSplitter.withDistance(distance);
      return this;
    }

    /**
     * Upper builder.
     *
     * @param upper the setUpperLength
     * @return the builder
     */
    public Builder withUpperLength(int upper) {
      randomShapeletSplitter.withUpperLength(upper);
      return this;
    }

    /**
     * Size builder.
     *
     * @param size the size
     * @return the builder
     */
    public Builder withSize(int size) {
      this.size = size;
      return this;
    }

    /**
     * Alpha builder.
     *
     * @param alpha the setAlpha
     * @return the builder
     */
    public Builder withAlpha(double alpha) {
      randomShapeletSplitter.withAlpha(alpha);
      return this;
    }

    // /**
    // * Randomizer builder.
    // *
    // * @param randomizer the setRandomizer
    // * @return the builder
    // */
    // public Builder withSampler(SampleStrategy randomizer) {
    // ensemble.withSampler(randomizer);
    // return this;
    // }

    @Override
    public RandomShapeletForest build() {
      return new RandomShapeletForest(this);
    }
  }
}
