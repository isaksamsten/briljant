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

package org.briljantframework.learning.lazy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.Prediction;
import org.briljantframework.learning.ensemble.Ensemble;
import org.briljantframework.learning.example.Example;
import org.briljantframework.learning.example.Examples;
import org.briljantframework.matrix.distance.Distance;
import org.briljantframework.vector.Vector;

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
 * property value for the object. This value is the average of the values of its k nearest
 * neighbors. k-NN is a type of instance-based learning, or lazy learning, where the function is
 * only approximated locally and all computation is deferred until classification. The k-NN
 * algorithm is among the simplest of all machine learning algorithms.
 * <p>
 * Created by Isak Karlsson on 01/09/14.
 */
public class KNearestNeighbors implements Classifier {

  private final int neighbors;
  private final Distance distance;
  private Examples examples;

  /**
   * Instantiates a new K nearest classifier.
   *
   * @param builder the builder
   * @param sample the sample
   */
  public KNearestNeighbors(Builder builder, Examples sample) {
    this(builder);
    this.examples = sample;

  }

  private KNearestNeighbors(Builder builder) {
    this.neighbors = builder.neighbors;
    this.distance = builder.distance.create();
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
  public Model fit(DataFrame dataFrame, Vector vector) {
    return new Model(dataFrame, vector, distance, neighbors, examples);
  }

  @Override
  public String toString() {
    return "k-Nearest Neighbors";
  }

  /**
   * The type Builder.
   */
  public static class Builder implements Ensemble.Member, Classifier.Builder<KNearestNeighbors> {

    /**
     * The Neighbours.
     */
    public int neighbors;
    private Distance.Builder distance = () -> Distance.EUCLIDEAN;

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
    public Builder withDistance(Distance.Builder distance) {
      this.distance = distance;
      return this;
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
     * Build k nearest classifier.
     *
     * @return the k nearest classifier
     */
    public KNearestNeighbors create() {
      return new KNearestNeighbors(this);
    }

    @Override
    public KNearestNeighbors create(Examples sample) {
      return new KNearestNeighbors(this, sample);
    }
  }

  /**
   * Created by Isak Karlsson on 01/09/14.
   */
  public static class Model implements org.briljantframework.learning.Model {

    private final DataFrame frame;
    private final Vector targets;
    private final Distance distance;
    private final boolean isNumeric;
    private final int k;
    private final Examples examples;

    /**
     * Instantiates a new K nearest classification.
     *
     * @param dataset the storage
     * @param distance the distance
     * @param k the k
     * @param exampels the examples
     */
    Model(DataFrame dataset, Vector targets, Distance distance, int k, Examples exampels) {
      this.frame = dataset;
      this.targets = targets;

      this.isNumeric = false; // FIXME
      this.distance = distance;
      this.k = k;
      this.examples = exampels;
    }

    @Override
    public Prediction predict(Vector row) {
      MinMaxPriorityQueue<DistanceIndex> queue = MinMaxPriorityQueue.maximumSize(k).create();
      if (examples == null) {
        for (int i = 0; i < frame.rows(); i++) {
          double d = distance.distance(row, frame.getRow(i));
          queue.add(new DistanceIndex(d, i, targets.getAsString(i)));
        }
      } else {
        for (Example example : examples) {
          double dist = distance.distance(row, frame.getRow(example.getIndex()));
          queue.add(new DistanceIndex(dist, example.getIndex(), targets.getAsString(example
              .getIndex())));
        }
      }

      return isNumeric ? mean(queue) : majority(queue);
    }

    private Prediction mean(MinMaxPriorityQueue<DistanceIndex> queue) {
      // double mean = 0.0;
      // for (DistanceIndex di : queue) {
      // Numeric numeric = (Numeric) di.target;
      // mean += numeric.asDouble();
      // }
      // mean /= queue.size();
      return null; // FIXME Prediction.numeric(Numeric.valueOf(mean));
    }

    private Prediction majority(MinMaxPriorityQueue<DistanceIndex> it) {
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

      return Prediction.nominal(target, probabilities);
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
