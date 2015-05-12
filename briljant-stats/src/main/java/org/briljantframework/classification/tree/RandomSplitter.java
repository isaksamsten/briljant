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

package org.briljantframework.classification.tree;

import org.briljantframework.Utils;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Vector;

/**
 * NOTE: This cannot be reused among trees (it is stateful for performance reasons)
 * <p>
 * Created by Isak Karlsson on 09/09/14.
 */
public class RandomSplitter extends AbstractSplitter {

  private final int maxFeatures;

  private final Gain criterion;
  private int[] features = null;

  public RandomSplitter(int maxFeatures, Gain criterion) {
    this.maxFeatures = maxFeatures;
    this.criterion = criterion;
  }

  public RandomSplitter(int maxFeatures) {
    this(maxFeatures, Gain.INFO);
  }

  public static Builder withMaximumFeatures(int maxFeatures) {
    return new Builder(maxFeatures);
  }

  @Override
  public TreeSplit<ValueThreshold> find(ClassSet classSet, DataFrame dataFrame, Vector column) {
    if (features == null) {
      initialize(dataFrame);
    }

    int maxFeatures =
        this.maxFeatures > 0 ? this.maxFeatures
                             : (int) Math.round(Math.sqrt(dataFrame.columns())) + 1;

    // TODO! Fix me!
    synchronized (features) {
      Utils.permute(features);
    }

    TreeSplit<ValueThreshold> bestSplit = null;
    double bestImpurity = Double.POSITIVE_INFINITY;
    for (int i = 0; i < features.length && i < maxFeatures; i++) {
      int axis = features[i];

      Object threshold = search(dataFrame.get(axis), classSet);
      if (Is.NA(threshold)) {
        continue;
      }

      TreeSplit<ValueThreshold> split = split(dataFrame, classSet, axis, threshold);
      double impurity = criterion.compute(classSet, split);
      if (impurity < bestImpurity) {
        bestSplit = split;
        bestImpurity = impurity;
      }
    }

    if (bestSplit != null) {
      bestSplit.setImpurity(bestImpurity);
    }
    return bestSplit;
  }

  private void initialize(DataFrame dataFrame) {
    this.features = new int[dataFrame.columns()];
    for (int i = 0; i < features.length; i++) {
      this.features[i] = i;
    }
  }

  /**
   * Search value.
   *
   * @param axis     the dataset
   * @param classSet the examples
   * @return the value
   */
  protected Object search(Vector axis, ClassSet classSet) {
    switch (axis.getType().getScale()) {
      case NOMINAL:
        return sampleCategoricValue(axis, classSet);
      case NUMERICAL:
        return sampleNumericValue(axis, classSet);
      default:
        throw new IllegalStateException(String.format("Header: %s, not supported", axis.getType()));
    }
  }

  /**
   * Sample numeric value.
   *
   * @param vector   the dataset
   * @param classSet the examples
   * @return the value
   */
  protected double sampleNumericValue(Vector vector, ClassSet classSet) {
    Example a = classSet.getRandomSample().getRandomExample();
    Example b = classSet.getRandomSample().getRandomExample();

    double valueA = vector.getAsDouble(a.getIndex());
    double valueB = vector.getAsDouble(b.getIndex());

    // TODO - what if both A and B are missing?
    if (Is.NA(valueA)) {
      return valueB;
    } else if (Is.NA(valueB)) {
      return valueB;
    } else {
      return (valueA + valueB) / 2;
    }

  }

  /**
   * Sample categoric value.
   *
   * @param axisVector the dataset
   * @param classSet   the examples
   * @return the value
   */
  protected Object sampleCategoricValue(Vector axisVector, ClassSet classSet) {
    Example example = classSet.getRandomSample().getRandomExample();
    return axisVector.get(Object.class, example.getIndex());
  }

  /**
   * The type Builder.
   */
  public static class Builder {

    private int maxFeatures;
    private Gain criterion = Gain.INFO;

    private Builder(int maxFeatures) {
      this.maxFeatures = maxFeatures;
    }

    /**
     * Sets max features.
     *
     * @param maxFeatures the max features
     * @return the max features
     */
    public Builder withMaximumFeatures(int maxFeatures) {
      this.maxFeatures = maxFeatures;
      return this;
    }

    /**
     * Sets withCriterion.
     *
     * @param criterion the withCriterion
     * @return the withCriterion
     */
    public Builder withCriterion(Gain criterion) {
      this.criterion = criterion;
      return this;
    }

    /**
     * Create random splitter.
     *
     * @return the random splitter
     */
    public RandomSplitter create() {
      return new RandomSplitter(maxFeatures, criterion);
    }

  }
}
