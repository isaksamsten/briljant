package org.briljantframework.vector;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.*;

import org.briljantframework.Sort;

import com.google.common.collect.UnmodifiableIterator;
import com.google.common.primitives.Ints;

/**
 * @author Isak Karlsson
 */
public final class Vectors {

  private Vectors() {}

  public static Vector sort(Vector in, VectorComparator cmp) {
    Vector.Builder builder = in.newCopyBuilder();
    Sort.quickSort(0, in.size(), (a, b) -> cmp.compare(in, a, b), builder);
    return builder.build();
  }

  /**
   * <p>
   * Create a vector of length {@code num} with evenly spaced values between {@code start} and
   * {@code end}.
   * </p>
   * 
   * <p>
   * Returns a vector of {@link org.briljantframework.vector.DoubleVector#TYPE}
   * </p>
   * 
   * 
   * @param start the start value
   * @param stop the end value
   * @param num the number of steps (i.e. intermediate values)
   * @return a vector
   */
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

  /**
   * Returns a vector of length {@code 50}. With evenly spaced values in the range {@code start} to
   * {@code end}.
   * 
   * @param start the start value
   * @param stop the end value
   * @return a vector
   */
  public static Vector linspace(double start, double stop) {
    return linspace(start, stop, 50);
  }

  /**
   * <p>
   * Split {@code vector} into {@code chunks}. Handles the case when {@code vector.size()} is not
   * evenly dividable by chunks by making some chunks larger.
   * </p>
   *
   * <p>
   * This implementation is lazy, i.e. chunking is done 'on-the-fly'. To get a list,
   * {@code new ArrayList<>(Vectors.split(vec, 10))}
   * </p>
   * 
   * <p>
   * Ensures that {@code vector.getType()} is preserved.
   * </p>
   * 
   * @param vector the vector
   * @param chunks the number of chunks
   * @return a collection of {@code chunk} chunks
   */
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
      mean += vector.getAsDouble(i);
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
      double residual = vector.getAsDouble(i) - mean;
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
    return sortIndex(vector,
        (o1, o2) -> Double.compare(vector.getAsDouble(o1), vector.getAsDouble(o2)));
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
      dot += (alpha * x.getAsDouble(i)) * (beta * y.getAsDouble(i));
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
      sum += vector.getAsDouble(i);
    }
    return sum;
  }
}
