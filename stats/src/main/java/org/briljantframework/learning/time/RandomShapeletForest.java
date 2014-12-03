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

package org.briljantframework.learning.time;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.Prediction;
import org.briljantframework.learning.ensemble.Ensemble;
import org.briljantframework.learning.ensemble.Sampler;
import org.briljantframework.matrix.RealArrayMatrix;
import org.briljantframework.matrix.RealMatrix;
import org.briljantframework.matrix.distance.Distance;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 24/09/14.
 */
public class RandomShapeletForest implements Classifier {

  private final Ensemble ensemble;

  private RandomShapeletForest(Builder builder) {
    this.ensemble = builder.ensemble.create();
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
    Ensemble.Model model = ensemble.fit(x, y);

    // double noModels = model.getModels().size();
    double[] averageLengthImportance = null;
    double[] averagePositionImportance = null;
    for (org.briljantframework.learning.Model m : model.getModels()) {
      ShapeletTree.Model stModel = (ShapeletTree.Model) m;

      RealMatrix lengthImportance = stModel.getLengthImportance();
      RealMatrix positionImportance = stModel.getPositionImportance();
      // double totalErrorReduction = stModel.getTotalErrorReduction();
      if (averageLengthImportance == null) {
        averageLengthImportance = new double[lengthImportance.columns()];
        averagePositionImportance = new double[positionImportance.columns()];
      }
      for (int i = 0; i < averageLengthImportance.length; i++) {
        averageLengthImportance[i] =
            averageLengthImportance[i] + (lengthImportance.get(i) / ensemble.size());

        averagePositionImportance[i] =
            averagePositionImportance[i] + (positionImportance.get(i) / ensemble.size());
      }
    }


    return new Model(RealArrayMatrix.columnVector(averageLengthImportance),
        RealArrayMatrix.columnVector(averagePositionImportance), model);
  }

  @Override
  public String toString() {
    return "Ensemble of Randomized Shapelet Trees";
  }

  /**
   * The type Model.
   */
  public static class Model implements org.briljantframework.learning.Model {

    private final Ensemble.Model model;
    private final RealArrayMatrix lengthImportance;
    private final RealArrayMatrix positionImportance;

    /**
     * Instantiates a new Model.
     *
     * @param lengthImportance the length importance
     * @param positionImportance the position importance
     * @param model the model
     */
    public Model(RealArrayMatrix lengthImportance, RealArrayMatrix positionImportance,
        Ensemble.Model model) {
      this.lengthImportance = lengthImportance;
      this.model = model;
      this.positionImportance = positionImportance;
    }

    @Override
    public Prediction predict(Vector row) {
      return model.predict(row);
    }

    /**
     * Gets length importance.
     *
     * @return the length importance
     */
    public RealMatrix getLengthImportance() {
      return lengthImportance;
    }

    /**
     * Gets position importance.
     *
     * @return the position importance
     */
    public RealMatrix getPositionImportance() {
      return positionImportance;
    }
  }

  /**
   * The type Builder.
   */
  public static class Builder implements Classifier.Builder<RandomShapeletForest> {

    private final RandomShapeletSplitter.Builder randomShapeletSplitter = RandomShapeletSplitter
        .withDistance(new EarlyAbandonSlidingDistance(Distance.EUCLIDEAN));

    private final Ensemble.Builder ensemble = Ensemble.withMember(ShapeletTree
        .withSplitter(randomShapeletSplitter));

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
     * Distance builder.
     *
     * @param distance the distance
     * @return the builder
     */
    public Builder withDistance(Distance.Builder distance) {
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
      ensemble.withSize(size);
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

    /**
     * Randomizer builder.
     *
     * @param randomizer the setRandomizer
     * @return the builder
     */
    public Builder withSampler(Sampler randomizer) {
      ensemble.withSampler(randomizer);
      return this;
    }

    @Override
    public RandomShapeletForest create() {
      return new RandomShapeletForest(this);
    }
  }
}
