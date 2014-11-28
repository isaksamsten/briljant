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

package org.briljantframework.learning.tree;

import org.briljantframework.Utils;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.learning.example.Example;
import org.briljantframework.learning.example.Examples;
import org.briljantframework.vector.DoubleValue;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Value;
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

  /**
   * Instantiates a new Random splitter.
   *
   * @param maxFeatures the max features
   * @param criterion the setCriterion
   */
  private RandomSplitter(int maxFeatures, Gain criterion) {
    this.maxFeatures = maxFeatures;
    this.criterion = criterion;
  }

  /**
   * With maximum features.
   *
   * @param maxFeatures the max features
   * @return the builder
   */
  public static Builder withMaximumFeatures(int maxFeatures) {
    return new Builder(maxFeatures);
  }

  @Override
  public Tree.Split<ValueThreshold> find(Examples examples, DataFrame dataFrame, Vector column) {
    if (features == null) {
      initialize(dataFrame);
    }

    int maxFeatures =
        this.maxFeatures > 0 ? this.maxFeatures
            : (int) Math.round(Math.sqrt(dataFrame.columns())) + 1;

    Utils.permute(features);

    Tree.Split<ValueThreshold> bestSplit = null;
    double bestImpurity = Double.POSITIVE_INFINITY;
    for (int i = 0; i < features.length && i < maxFeatures; i++) {
      int axis = features[i];

      Value threshold = search(dataFrame.getColumn(axis), examples);
      if (threshold.isNA(0)) {
        continue;
      }

      Tree.Split<ValueThreshold> split = split(dataFrame, examples, axis, threshold);
      double impurity = criterion.calculate(examples, split);
      if (impurity < bestImpurity) {
        bestSplit = split;
        bestImpurity = impurity;
      }
    }

    if (bestSplit != null) {
      bestSplit.setImpurity(new double[] {bestImpurity, 0, 0});
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
   * @param axis the dataset
   * @param examples the examples
   * @return the value
   */
  protected Value search(Vector axis, Examples examples) {
    switch (axis.getType().getScale()) {
      case CATEGORICAL:
        return sampleCategoricValue(axis, examples);
      case NUMERICAL:
        return sampleNumericValue(axis, examples);
      default:
        throw new IllegalStateException(String.format("Header: %s, not supported", axis.getType()));
    }
  }

  /**
   * Sample numeric value.
   *
   * @param vector the dataset
   * @param examples the examples
   * @return the value
   */
  protected Value sampleNumericValue(Vector vector, Examples examples) {
    Example a = examples.getRandomSample().getRandomExample();
    Example b = examples.getRandomSample().getRandomExample();

    double valueA = vector.getAsDouble(a.getIndex());
    double valueB = vector.getAsDouble(b.getIndex());

    // TODO - what if both A and B are missing?
    if (Is.NA(valueA)) {
      return new DoubleValue(valueB);
    } else if (Is.NA(valueB)) {
      return new DoubleValue(valueB);
    } else {
      return new DoubleValue((valueA + valueB) / 2);
    }

  }

  /**
   * Sample categoric value.
   *
   * @param axisVector the dataset
   * @param examples the examples
   * @return the value
   */
  protected Value sampleCategoricValue(Vector axisVector, Examples examples) {
    Example example = examples.getRandomSample().getRandomExample();
    return axisVector.getAsValue(example.getIndex());
  }

  /**
   * The type Builder.
   */
  public static class Builder implements Splitter.Builder<RandomSplitter> {
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
     * Sets setCriterion.
     *
     * @param criterion the setCriterion
     * @return the setCriterion
     */
    public Builder setCriterion(Gain criterion) {
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
