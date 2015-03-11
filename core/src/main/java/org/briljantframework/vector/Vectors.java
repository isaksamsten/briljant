package org.briljantframework.vector;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

import org.briljantframework.IndexComparator;
import org.briljantframework.QuickSort;

import com.google.common.base.Function;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.UnmodifiableIterator;
import com.google.common.primitives.Ints;

/**
 * @author Isak Karlsson
 */
public final class Vectors {

  public static final Set<VectorType> NUMERIC = Sets.newIdentityHashSet();
  public static final Set<VectorType> CATEGORIC = Sets.newIdentityHashSet();
  public static final VectorType DOUBLE = DoubleVector.TYPE;
  public static final VectorType COMPLEX = ComplexVector.TYPE;
  public static final VectorType INT = IntVector.TYPE;
  public static final VectorType BIT = BitVector.TYPE;
  public static final VectorType STRING = StringVector.TYPE;

  static {
    NUMERIC.add(DOUBLE);
    NUMERIC.add(INT);
    NUMERIC.add(COMPLEX);

    CATEGORIC.add(STRING);
    CATEGORIC.add(BIT);
  }

  private Vectors() {}

  /**
   * Finds the index, in {@code vector}, of the value at {@code index} in {@code values}. This
   * should be preferred over {@link #find(Vector, Value)} when possible. Hence, given
   * {@code Vector a}, {@code Vector b} and the index {@code i}, {@code find(a, b, i)} should be
   * preferred over {@code find(a, b.getAsValue(i))}.
   *
   * 
   * @param haystack the vector to search
   * @param needleSource the source of the needle
   * @param needle the needle in the source
   * @return the (first) index of {@code needleSource.getAsValue(needle)} in {@code haystack} or
   *         {@code -1}
   */
  public static int find(Vector haystack, Vector needleSource, int needle) {
    for (int i = 0; i < haystack.size(); i++) {
      if (haystack.compare(i, needleSource, needle) == 0) {
        return i;
      }
    }
    return -1;
  }

  public static int find(Vector haystack, Value needle) {
    for (int i = 0; i < haystack.size(); i++) {
      if (haystack.compare(i, needle) == 0) {
        return i;
      }
    }
    return -1;
  }

  public static int find(Vector haystack, int needle) {
    return find(haystack, Convert.toValue(needle));
  }

  public static int find(Vector haystack, String needle) {
    return find(haystack, Convert.toValue(needle));
  }

  public static int find(Vector vector, Predicate<Value> predicate) {
    for (int i = 0; i < vector.size(); i++) {
      if (predicate.test(vector.getAsValue(i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Creates a double vector of {@code size} filled with {@code NA}
   * 
   * @param size the size
   * @return a new vector
   */
  public static DoubleVector newDoubleNA(int size) {
    return new DoubleVector.Builder(size).build();
  }

  public static IntVector newIntVector(int... values) {
    return IntVector.newBuilderWithInitialValues(values).build();
  }

  /**
   * @param in the vector
   * @return a new vector sorted in ascending order
   */
  public static Vector sortAsc(Vector in) {
    Vector.Builder builder = in.newCopyBuilder();
    QuickSort.quickSort(0, in.size(), builder::compare, builder);
    return builder.build();
  }

  /**
   * @param in the vector
   * @return a new vector sorted in ascending order
   */
  public static Vector sortDesc(Vector in) {
    Vector.Builder builder = in.newCopyBuilder();
    QuickSort.quickSort(0, in.size(), (a, b) -> builder.compare(b, a), builder);
    return builder.build();
  }

  public static Vector sort(Vector in, IndexComparator<? super Vector> cmp) {
    Vector.Builder builder = in.newCopyBuilder();
    Vector tmp = builder.getTemporaryVector();
    QuickSort.quickSort(0, in.size(), (a, b) -> cmp.compare(tmp, a, b), builder);
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
  public static double std(Vector vector) {
    return std(vector, mean(vector));
  }

  /**
   * @param vector the vector
   * @param mean the mean
   * @return the standard deviation
   */
  public static double std(Vector vector, double mean) {
    double var = var(vector, mean);
    return Is.NA(var) ? DoubleVector.NA : Math.sqrt(var);
  }

  /**
   * @param vector the vector
   * @return the mean; or NA
   */
  public static double mean(Vector vector) {
    double mean = 0;
    int nonNA = 0;
    for (int i = 0; i < vector.size(); i++) {
      if (!vector.isNA(i)) {
        mean += vector.getAsDouble(i);
        nonNA += 1;
      }
    }

    return nonNA == 0 ? DoubleVector.NA : mean / (double) nonNA;
  }

  /**
   * @param vector the vector
   * @param mean the mean
   * @return the variance; or NA
   */
  public static double var(Vector vector, double mean) {
    double var = 0;
    int nonNA = 0;
    for (int i = 0; i < vector.size(); i++) {
      if (!vector.isNA(i)) {
        double residual = vector.getAsDouble(i) - mean;
        var += residual * residual;
        nonNA += 1;
      }
    }
    return nonNA == 0 ? DoubleVector.NA : var / (double) nonNA;
  }

  /**
   * @param vector the vector
   * @return the variance
   */
  public static double var(Vector vector) {
    return var(vector, mean(vector));
  }

  /**
   * @param vector the vector
   * @return the indexes of {@code vector} sorted in increasing order by value
   */
  public static int[] sortIndex(Vector vector) {
    return sortIndex(vector,
        (o1, o2) -> Double.compare(vector.getAsDouble(o1), vector.getAsDouble(o2)));
  }

  /**
   * @param vector the vector
   * @param comparator the comparator
   * @return the indexes of {@code vector} sorted according to {@code comparator} by value
   */
  public static int[] sortIndex(Vector vector, Comparator<Integer> comparator) {
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
  public static double dot(Vector x, Vector y) {
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
  public static double dot(Vector x, double alpha, Vector y, double beta) {
    org.briljantframework.Check.size(x, y);
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
  public static double sigmoid(Vector a, Vector b) {
    return 1.0 / (1 + Math.exp(dot(a, 1, b, -1)));
  }

  /**
   * @param vector the vector
   * @return the sum
   */
  public static double sum(Vector vector) {
    double sum = 0;
    for (int i = 0; i < vector.size(); i++) {
      sum += vector.getAsDouble(i);
    }
    return sum;
  }

  public static Vector unique(Vector... vectors) {
    vectors = checkNotNull(vectors);
    checkArgument(vectors.length > 0);
    Vector.Builder builder = vectors[0].newBuilder();
    Set<Value> taken = new HashSet<>();
    for (Vector vector : vectors) {
      for (int i = 0; i < vector.size(); i++) {
        Value value = vector.getAsValue(i);
        if (!taken.contains(value)) {
          taken.add(value);
          builder.add(vector, i);
        }
      }
    }
    return builder.build();
  }

  public static double min(Vector column) {
    return column.stream().filter(x -> !x.isNA()).mapToDouble(Value::getAsDouble).min()
        .orElse(DoubleVector.NA);
  }

  public static double max(Vector column) {
    return column.stream().filter(x -> !x.isNA()).mapToDouble(Value::getAsDouble).max()
        .orElse(DoubleVector.NA);
  }

  public static Map<Value, Integer> count(Vector vector) {
    Map<Value, Integer> freq = new HashMap<>();
    for (Value value : vector.asValueList()) {
      freq.compute(value, (x, i) -> i == null ? 1 : i + 1);
    }
    return Collections.unmodifiableMap(freq);
  }

  public static Value mode(Vector column) {
    Multiset<Value> values = HashMultiset.create();
    column.stream().forEach(values::add);
    return Ordering.natural().onResultOf(new Function<Multiset.Entry<Value>, Integer>() {
      @Override
      public Integer apply(Multiset.Entry<Value> input) {
        return input.getCount();
      }
    }).max(values.entrySet()).getElement();
  }
}
