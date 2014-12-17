package org.briljantframework.vector;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.*;

import com.google.common.collect.UnmodifiableIterator;
import com.google.common.primitives.Ints;

/**
 * @author Isak Karlsson
 */
public final class Vectors {

  private Vectors() {}

  public static Vector linspace(double start, double stop, int num) {
    DoubleVector.Builder builder = new DoubleVector.Builder(0, num);
    double step = (stop - start) / (num - 1);
    double value = start;
    for (int index = 0; index < num; index++) {
      builder.set(index, value);
      value += step;
    }

    return builder.build();
  }

  public static Vector linspace(double start, double stop) {
    return linspace(start, stop, 50);
  }

  public static Collection<Vector> split(Vector vector, int chunks) {
    checkArgument(vector.size() >= chunks, "size must be shorter than chunks");
    if (vector.size() == chunks) {
      return Collections.singleton(vector);
    }
    int bin = vector.size() / chunks;
    int remainder = vector.size() % chunks;

    return new AbstractCollection<Vector>() {
      @Override
      public Iterator<Vector> iterator() {
        return new UnmodifiableIterator<Vector>() {
          private int current = 0;
          private int remainders = 0;

          @Override
          public boolean hasNext() {
            return current < vector.size();
          }

          @Override
          public Vector next() {
            int binSize = bin;
            if (remainders < remainder) {
              remainders++;
              binSize += 1;
            }
            Vector.Builder builder = vector.newBuilder();
            for (int i = 0; i < binSize; i++) {
              builder.add(vector, current++);
            }
            return builder.build();
          }
        };
      }

      @Override
      public int size() {
        return chunks;
      }
    };
  }

  /**
   * @param vector the vector
   * @return the standard deviation
   */
  public static double std(VectorLike vector) {
    return std(vector, mean(vector));
  }

  /**
   * @param vector the vector
   * @param mean the mean
   * @return the standard deviation
   */
  public static double std(VectorLike vector, double mean) {
    double var = var(vector, mean);
    return Math.sqrt(var / (vector.size() - 1));
  }

  /**
   * @param vector the vector
   * @return the mean
   */
  public static double mean(VectorLike vector) {
    double mean = 0;
    for (int i = 0; i < vector.size(); i++) {
      mean += vector.get(i);
    }

    return mean / vector.size();
  }

  /**
   * @param vector the vector
   * @param mean the mean
   * @return the variance
   */
  public static double var(VectorLike vector, double mean) {
    double var = 0;
    for (int i = 0; i < vector.size(); i++) {
      double residual = vector.get(i) - mean;
      var += residual * residual;
    }
    return var;
  }

  /**
   * @param vector the vector
   * @return the variance
   */
  public static double var(VectorLike vector) {
    return var(vector, mean(vector));
  }

  /**
   * @param vector the vector
   * @return the indexes of {@code vector} sorted in increasing order by value
   */
  public static int[] sortIndex(VectorLike vector) {
    return sortIndex(vector, (o1, o2) -> Double.compare(vector.get(o1), vector.get(o2)));
  }

  /**
   * @param vector the vector
   * @param comparator the comparator
   * @return the indexes of {@code vector} sorted according to {@code comparator} by value
   */
  public static int[] sortIndex(VectorLike vector, Comparator<Integer> comparator) {
    int[] indicies = new int[vector.size()];
    for (int i = 0; i < indicies.length; i++) {
      indicies[i] = i;
    }
    List<Integer> tempList = Ints.asList(indicies);
    Collections.sort(tempList, comparator);
    return indicies;
  }

  /**
   * Inner product, i.e. the dot product x * y
   *
   * @param x a vector
   * @param y a vector
   * @return the dot product
   */
  public static double dot(VectorLike x, VectorLike y) {
    return dot(x, 1, y, 1);
  }

  /**
   * Take the inner product of two vectors (m x 1) and (1 x m) scaling them by alpha and beta
   * respectively
   *
   * @param x a row vector
   * @param alpha scaling factor for a
   * @param y a column vector
   * @param beta scaling factor for y
   * @return the inner product
   */
  public static double dot(VectorLike x, double alpha, VectorLike y, double beta) {
    if (x.size() != y.size()) {
      throw new IllegalArgumentException();
    }
    int size = y.size();
    double dot = 0;
    for (int i = 0; i < size; i++) {
      dot += (alpha * x.get(i)) * (beta * y.get(i));
    }
    return dot;
  }

  /**
   * Compute the sigmoid between a and b, i.e. 1/(1+e^(a'-b))
   *
   * @param a a vector
   * @param b a vector
   * @return the sigmoid
   */
  public static double sigmoid(VectorLike a, VectorLike b) {
    return 1.0 / (1 + Math.exp(dot(a, 1, b, -1)));
  }

  /**
   * @param vector the vector
   * @return the sum
   */
  public static double sum(VectorLike vector) {
    double sum = 0;
    for (int i = 0; i < vector.size(); i++) {
      sum += vector.get(i);
    }
    return sum;
  }
}
