/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.classification;

import org.briljantframework.Check;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.classification.tree.ClassSet;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.distance.Distance;
import org.briljantframework.distance.Euclidean;
import org.briljantframework.data.vector.Vectors;
import org.briljantframework.data.vector.Vector;

import java.util.EnumSet;

/**
 * In pattern recognition, the k-Nearest Neighbors algorithm (or k-NN for short) is a
 * non-parametric
 * method used for classification and regression.[1] In both cases, the input consists of the k
 * closest training examples in the feature space. The output depends on whether k-NN is used for
 * classification or regression:
 * <p>
 * In k-NN classification, the output is a class membership. An object is classified by a majority
 * vote of its neighbors, with the object being assigned to the class most common among its k
 * nearest neighbors (k is a positive integer, typically small). If k = 1, then the object is
 * simply
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

  public static Builder withNeighbors(int k) {
    return new Builder(k);
  }

  public Distance getDistance() {
    return distance;
  }

  public int getNeighbors() {
    return neighbors;
  }

  @Override
  public Model fit(DataFrame x, Vector y) {
    Check.argument(x.rows() == y.size(),
                   "The size of x and y don't match: %s != %s.", x.rows(), y.size());
    return new Model(x, y, distance, neighbors, Vectors.unique(y));
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

    public KNearestNeighbors create(ClassSet sample) {
      return new KNearestNeighbors(this);
    }
  }

  /**
   * Created by Isak Karlsson on 01/09/14.
   */
  public static class Model extends AbstractPredictor {

    private final DataFrame frame;
    private final Vector targets;
    private final Distance distance;
    private final int k;

    /**
     * Instantiates a new K nearest classification.
     *
     * @param x        the storage
     * @param distance the distance
     * @param k        the k
     */
    Model(DataFrame x, Vector y, Distance distance, int k, Vector classes) {
      super(classes);
      this.frame = x;
      this.targets = y;

      this.distance = distance;
      this.k = k;
    }

    @Override
    public DoubleArray estimate(Vector record) {
//      MinMaxPriorityQueue<DistanceIndex> queue = MinMaxPriorityQueue.maximumSize(k).create();
//      for (int i = 0; i < frame.rows(); i++) {
//        double d = distance.compute(record, frame.getRecord(i));
//        queue.add(new DistanceIndex(targets.get(Object.class, i), d));
//      }
//      ObjectIntMap<Object> votes = new ObjectIntOpenHashMap<>();
//      for (DistanceIndex di : queue) {
//        votes.putOrAdd(di.target, 1, 1);
//      }
//      Vector classes = getClasses();
//      int voters = queue.size();
//      DoubleArray probas = Bj.doubleArray(classes.size());
//      for (int i = 0; i < classes.size(); i++) {
//        probas.set(i, votes.getOrDefault(classes.get(Object.class, i), 0) / voters);
//      }
//      return probas;
      throw new UnsupportedOperationException();
    }

    @Override
    public EnumSet<Characteristics> getCharacteristics() {
      return EnumSet.of(Characteristics.ESTIMATOR);
    }
  }

  private static class DistanceIndex implements Comparable<DistanceIndex> {

    private final double distance;
    private final Object target;

    private DistanceIndex(Object value, double distance) {
      this.distance = distance;
      this.target = value;
    }

    @Override
    public int compareTo(DistanceIndex o) {
      return Double.compare(this.distance, o.distance);
    }

    @Override
    public String toString() {
      return String.format("%f", distance);
    }
  }
}
