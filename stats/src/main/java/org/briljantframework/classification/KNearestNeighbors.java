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

import static com.google.common.base.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.briljantframework.classification.tree.Examples;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.distance.Distance;
import org.briljantframework.distance.Euclidean;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import com.google.common.collect.MinMaxPriorityQueue;

/**
 * In pattern recognition, the k-Nearest Neighbors algorithm (or k-NN for short) is a non-parametric
 * method used for classification and regression.[1] In both cases, the input consists of the k
 * closest training examples in the feature space. The output depends on whether k-NN is used for
 * classification or regression:
 * <p>
 * In k-NN classification, the output is a class membership. An object is classified by a majority
 * vote of its neighbors, with the object being assigned to the class most common among its k
 * nearest neighbors (k is a positive integer, typically small). If k = 1, then the object is simply
 * assigned to the class of that single nearest neighbor. In k-NN regression, the output is the
 * property value for the object. This value is the average of the getPosteriorProbabilities of its
 * k nearest neighbors. k-NN is a type of instance-based learning, or lazy learning, where the
 * function is only approximated locally and all computation is deferred until classification. The
 * k-NN algorithm is among the simplest of all machine learning algorithms.
 * <p>
 * Created by Isak Karlsson on 01/09/14.
 */
public class KNearestNeighbors implements Classifier {

  private final int neighbors;
  private final Distance distance;

  private KNearestNeighbors(Builder builder) {
    this.neighbors = builder.neighbors;
    this.distance = builder.distance;
  }

  /**
   * Builder builder.
   *
   * @return the builder
   */
  public static Builder builder() {
    return new Builder(10);
  }

  /**
   * Neighbours builder.
   *
   * @param k the k
   * @return the builder
   */
  public static Builder withNeighbors(int k) {
    return new Builder(k);
  }

  /**
   * Gets distance.
   *
   * @return the distance
   */
  public Distance getDistance() {
    return distance;
  }

  /**
   * Gets neighbors.
   *
   * @return the neighbors
   */
  public int getNeighbors() {
    return neighbors;
  }

  @Override
  public Model fit(DataFrame x, Vector y) {
    checkArgument(x.rows() == y.size(), "The size of x and y don't match: %s != %s.", x.rows(),
        y.size());
    checkArgument(y.getType().getScale() == VectorType.Scale.CATEGORICAL,
        "Can't handle continuous targets. ");
    for (int i = 0; i < x.columns(); i++) {
      checkArgument(x.getColumnType(i).getScale() == VectorType.Scale.NUMERICAL,
          "Can't handle non-numerical values");
    }
    return new Model(x, y, distance, neighbors);
  }

  @Override
  public String toString() {
    return "k-Nearest Neighbors";
  }

  /**
   * The type Builder.
   */
  public static class Builder implements Classifier.Builder<KNearestNeighbors> {

    /**
     * The Neighbours.
     */
    public int neighbors;
    private Distance distance = Euclidean.getInstance();

    /**
     * Instantiates a new Builder.
     *
     * @param neighbors the k
     */
    public Builder(int neighbors) {
      this.neighbors = neighbors;
    }

    /**
     * Neighbours builder.
     *
     * @param k the k
     * @return the builder
     */
    public Builder withNeighbors(int k) {
      this.neighbors = k;
      return this;
    }

    /**
     * Distance builder.
     *
     * @param distance the distance
     * @return the builder
     */
    public Builder withDistance(Distance distance) {
      this.distance = distance;
      return this;
    }

    /**
     * Build k nearest classifier.
     *
     * @return the k nearest classifier
     */
    public KNearestNeighbors build() {
      return new KNearestNeighbors(this);
    }

    public KNearestNeighbors create(Examples sample) {
      return new KNearestNeighbors(this);
    }
  }

  /**
   * Created by Isak Karlsson on 01/09/14.
   */
  public static class Model implements ClassifierModel {

    private final DataFrame frame;
    private final Vector targets;
    private final Distance distance;
    private final int k;

    /**
     * Instantiates a new K nearest classification.
     *
     * @param x the storage
     * @param distance the distance
     * @param k the k
     */
    Model(DataFrame x, Vector y, Distance distance, int k) {
      this.frame = x;
      this.targets = y;

      this.distance = distance;
      this.k = k;
    }

    @Override
    public Label predict(Vector row) {
      MinMaxPriorityQueue<DistanceIndex> queue = MinMaxPriorityQueue.maximumSize(k).create();
      for (int i = 0; i < frame.rows(); i++) {
        double d = distance.distance(row, frame.getRow(i));
        queue.add(new DistanceIndex(d, i, targets.getAsString(i)));
      }

      return majority(queue);
    }

    private Label majority(MinMaxPriorityQueue<DistanceIndex> it) {
      Map<String, Integer> values = new HashMap<>();
      for (DistanceIndex di : it) {
        values.compute(di.target, (i, v) -> v == null ? 1 : v + 1);
      }

      List<String> target = new ArrayList<>();
      List<Double> probabilities = new ArrayList<>();
      for (Map.Entry<String, Integer> kv : values.entrySet()) {
        target.add(kv.getKey());
        probabilities.add(kv.getValue() / (double) it.size());
      }

      return Label.nominal(target, probabilities);
    }
  }

  private static class DistanceIndex implements Comparable<DistanceIndex> {
    private final double distance;
    private final int index;
    private final String target;

    private DistanceIndex(double distance, int index, String value) {
      this.distance = distance;
      this.index = index;
      this.target = value;
    }

    @Override
    public int compareTo(DistanceIndex o) {
      return Double.compare(this.distance, o.distance);
    }

    @Override
    public String toString() {
      return String.format("%f:%d", distance, index);
    }
  }
}
