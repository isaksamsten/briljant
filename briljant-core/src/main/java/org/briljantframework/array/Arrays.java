/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.array;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.DoublePredicate;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import net.mintern.primitive.comparators.DoubleComparator;
import net.mintern.primitive.comparators.IntComparator;
import net.mintern.primitive.comparators.LongComparator;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.array.api.ArrayRoutines;
import org.briljantframework.array.netlib.NetlibArrayBackend;
import org.briljantframework.exceptions.MultiDimensionMismatchException;
import org.briljantframework.function.DoubleBiPredicate;
import org.briljantframework.linalg.api.LinearAlgebraRoutines;
import org.briljantframework.sort.IndexComparator;
import org.briljantframework.sort.QuickSort;

/**
 * Utilities for multidimensional arrays. The arrays produces depend on the selected backend.
 *
 * <p/>
 * The methods here mostly delegate to appropriate {@link ArrayFactory factory},
 * {@link ArrayRoutines routine} or {@link LinearAlgebraRoutines linear algebra routine} methods.
 *
 * <p/>
 * It is unadvised to use these utilities if the array is not created by the factories here. For
 * example,
 *
 * <pre>
 * ArrayBackend backend = new BaseArrayBackend();
 * ArrayFactory factory = backend.getArrayFactory();
 *
 * DoubleArray x = factory.newDoubleArray(3, 3);
 * x.assign(10);
 *
 * // assuming that Arrays are using the default NetlibArrayBackend
 * DoubleArray y = Arrays.newDoubleArray(3, 3);
 *
 * // This will be slow since the array created by the BaseArrayBackend
 * // cannot be used by the NetlibArrayBackend
 * Arrays.dot(x, y);
 * </pre>
 *
 * <p/>
 * Note that the static factory methods present in e.g., {@link Array#of(Object[])} delegates to the
 * methods defined here and hence use the default array backend
 *
 * @author Isak Karlsson
 * @see ArrayBackend
 * @see ArrayRoutines
 * @see ArrayFactory
 * @see LinearAlgebraRoutines
 * @see NetlibArrayBackend
 * @see org.briljantframework.array.netlib.NetlibArrayFactory
 * @see org.briljantframework.array.netlib.NetlibLinearAlgebraRoutines
 */
public final class Arrays {

  /**
   * A link to the selected linear algebra routines
   *
   * @see LinearAlgebraRoutines
   */
  public static final LinearAlgebraRoutines linalg;

  private static final RealDistribution normalDistribution = new NormalDistribution(0, 1);
  private static final RealDistribution uniformDistribution = new UniformRealDistribution(-1, 1);
  private static final ArrayFactory ARRAY_FACTORY;
  private static final ArrayRoutines ARRAY_ROUTINES;

  static {
    ArrayBackend backend =
        StreamSupport.stream(ServiceLoader.load(ArrayBackend.class).spliterator(), false)
            .filter(ArrayBackend::isAvailable)
            .sorted((a, b) -> Integer.compare(b.getPriority(), a.getPriority())).findFirst()
            .orElse(new NetlibArrayBackend());

    ARRAY_FACTORY = backend.getArrayFactory();
    ARRAY_ROUTINES = backend.getArrayRoutines();
    linalg = backend.getLinearAlgebraRoutines();
  }

  private Arrays() {
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newArray(int...)
   */
  public static <T> Array<T> array(int... shape) {
    return ARRAY_FACTORY.newArray(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newVector(Object[])
   */
  @SafeVarargs
  public static <T> Array<T> vector(T... data) {
    return ARRAY_FACTORY.newVector(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newMatrix(Object[][])
   */
  public static <T> Array<T> matrix(T[][] data) {
    return ARRAY_FACTORY.newMatrix(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#ones(int...)
   */
  public static DoubleArray ones(int... shape) {
    return ARRAY_FACTORY.ones(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#zero(int...)
   */
  public static DoubleArray zero(int... shape) {
    return ARRAY_FACTORY.zero(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#eye(int)
   */
  public static DoubleArray eye(int size) {
    return ARRAY_FACTORY.eye(size);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newDoubleVector(double[])
   */
  public static DoubleArray doubleVector(double... data) {
    return ARRAY_FACTORY.newDoubleVector(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newDoubleMatrix(double[][])
   */
  public static DoubleArray doubleMatrix(double[][] data) {
    return ARRAY_FACTORY.newDoubleMatrix(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#linspace(double, double, int)
   */
  public static DoubleArray linspace(double start, double end, int size) {
    return ARRAY_FACTORY.linspace(start, end, size);
  }

  /**
   * Return a coordinate array from coordinate vectors (arrays with d > 1 are raveled)
   *
   * @param first the first array
   * @param rest rest of the arrays
   * @param <S> the class of arrays
   * @return a list of coordinate arrays (with shape
   *         {@code [first.size(), rest[0].size(), ..., rest[rest.length - 1].size()]}
   */
  @SafeVarargs
  public static <S extends BaseArray<S>> List<S> meshgrid(S first, S... rest) {
    List<S> arrays = new ArrayList<>();
    arrays.add(first);
    Collections.addAll(arrays, rest);

    int[] shape = new int[arrays.size()];
    for (int i = 0; i < arrays.size(); i++) {
      shape[i] = arrays.get(i).size();
    }

    List<S> newArrays = new ArrayList<>();
    for (S array : arrays) {
      newArrays.add(array.newEmptyArray(shape));
    }

    for (int i = 0; i < newArrays.size(); i++) {
      S newArray = newArrays.get(i);
      S array = arrays.get(i);
      for (int j = 0, vectors = newArray.vectors(i); j < vectors; j++) {
        newArray.getVector(i, j).assign(array);
      }
    }
    return Collections.unmodifiableList(newArrays);
  }

  /**
   * Create a 1d-array with values sampled from the normal (gaussian) distribution with mean
   * {@code 0} and standard deviation {@code 1}.
   *
   * <p>
   * Example
   *
   * <pre>
   * {@code
   * > Arrays.randn(9).reshape(3, 3);
   * array([[0.168, -0.297, -0.374],
   *        [1.030, -1.465,  0.636],
   *        [0.957, -0.990,  0.498]] type: double)
   * }
   * </pre>
   *
   * @param size the size of the array
   * @return a new 1d-array
   */
  public static DoubleArray randn(int size) {
    return rand(size, normalDistribution);
  }

  /**
   * Create a 1d-array with values sampled from the specified distribution.
   *
   * @param size the size of the array
   * @param distribution the distribution to sample from
   * @return a new 1d-array
   */
  public static DoubleArray rand(int size, RealDistribution distribution) {
    DoubleArray array = doubleArray(size);
    for (int i = 0; i < size; i++) {
      array.set(i, distribution.sample());
    }
    return array;
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newDoubleArray(int...)
   */
  public static DoubleArray doubleArray(int... shape) {
    return ARRAY_FACTORY.newDoubleArray(shape);
  }

  /**
   * Create a 1d-array with values sampled uniformly from the range {@code [-1, 1]}
   * <p>
   * Example
   *
   * <pre>
   * {@code
   * > Arrays.rand(4).reshape(2,2)
   * array([[0.467, 0.898],
   *        [0.568, 0.103]] type: double)
   * }
   * </pre>
   *
   * @param size the size of the array
   * @return a new 1d-array
   */
  public static DoubleArray rand(int size) {
    return rand(size, uniformDistribution);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newComplexArray(int...)
   */
  public static ComplexArray complexArray(int... shape) {
    return ARRAY_FACTORY.newComplexArray(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newComplexVector(double[])
   */
  public static ComplexArray complexVector(double... data) {
    return ARRAY_FACTORY.newComplexVector(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newComplexVector(org.apache.commons.math3.complex.Complex[])
   */
  public static ComplexArray complexVector(Complex... data) {
    return ARRAY_FACTORY.newComplexVector(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newComplexMatrix(org.apache.commons.math3.complex.Complex[][])
   */
  public static ComplexArray complexMatrix(Complex[][] data) {
    return ARRAY_FACTORY.newComplexMatrix(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newIntVector(int[])
   */
  public static IntArray intVector(int... data) {
    return ARRAY_FACTORY.newIntVector(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newIntMatrix(int[][])
   */
  public static IntArray intMatrix(int[][] data) {
    return ARRAY_FACTORY.newIntMatrix(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#range(int, int)
   */
  public static Range range(int start, int end) {
    return ARRAY_FACTORY.range(start, end);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#range(int, int, int)
   */
  public static Range range(int start, int end, int step) {
    return ARRAY_FACTORY.range(start, end, step);
  }

  public static IntArray randi(int size, int l, int u) {
    RealDistribution distribution = new UniformRealDistribution(l, u);
    IntArray array = intArray(size);
    array.assign(() -> (int) Math.round(distribution.sample()));
    return array;
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newIntArray(int...)
   */
  public static IntArray intArray(int... shape) {
    return ARRAY_FACTORY.newIntArray(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newLongArray(int...)
   */
  public static LongArray longArray(int... shape) {
    return ARRAY_FACTORY.newLongArray(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newLongVector(long[])
   */
  public static LongArray longVector(long... data) {
    return ARRAY_FACTORY.newLongVector(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newLongMatrix(long[][])
   */
  public static LongArray longMatrix(long[][] data) {
    return ARRAY_FACTORY.newLongMatrix(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newBooleanArray(int...)
   */
  public static BooleanArray booleanArray(int... shape) {
    return ARRAY_FACTORY.newBooleanArray(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newBooleanVector(boolean[])
   */
  public static BooleanArray booleanVector(boolean[] data) {
    return ARRAY_FACTORY.newBooleanVector(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#newBooleanMatrix(boolean[][])
   */
  public static BooleanArray booleanMatrix(boolean[][] data) {
    return ARRAY_FACTORY.newBooleanMatrix(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#diag(org.briljantframework.array.BaseArray)
   */
  public static <T extends BaseArray<T>> T diag(T data) {
    return ARRAY_FACTORY.diag(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#mean(org.briljantframework.array.DoubleArray)
   */
  public static double mean(DoubleArray x) {
    return ARRAY_ROUTINES.mean(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#mean(int,
   *      org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray mean(int dim, DoubleArray x) {
    return ARRAY_ROUTINES.mean(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#var(org.briljantframework.array.DoubleArray)
   */
  public static double var(DoubleArray x) {
    return ARRAY_ROUTINES.var(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#var(int,
   *      org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray var(int dim, DoubleArray x) {
    return ARRAY_ROUTINES.var(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#std(org.briljantframework.array.DoubleArray)
   */
  public static double std(DoubleArray x) {
    return ARRAY_ROUTINES.std(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#std(int,
   *      org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray std(int dim, DoubleArray x) {
    return ARRAY_ROUTINES.std(dim, x);
  }

  public static int sum(BooleanArray x) {
    return sum(x.asInt());
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sum(org.briljantframework.array.IntArray)
   */
  public static int sum(IntArray x) {
    return ARRAY_ROUTINES.sum(x);
  }

  public static IntArray sum(int dim, BooleanArray x) {
    return sum(dim, x.asInt());
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sum(int,
   *      org.briljantframework.array.IntArray)
   */
  public static IntArray sum(int dim, IntArray x) {
    return ARRAY_ROUTINES.sum(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sum(org.briljantframework.array.DoubleArray)
   */
  public static double sum(DoubleArray x) {
    return ARRAY_ROUTINES.sum(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sum(int,
   *      org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray sum(int dim, DoubleArray x) {
    return ARRAY_ROUTINES.sum(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#prod(org.briljantframework.array.DoubleArray)
   */
  public static double prod(DoubleArray x) {
    return ARRAY_ROUTINES.prod(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#prod(int,
   *      org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray prod(int dim, DoubleArray x) {
    return ARRAY_ROUTINES.prod(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(org.briljantframework.array.DoubleArray)
   */
  public static double min(DoubleArray x) {
    return ARRAY_ROUTINES.min(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(int,
   *      org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray min(int dim, DoubleArray x) {
    return ARRAY_ROUTINES.min(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(org.briljantframework.array.IntArray)
   */
  public static int min(IntArray x) {
    return ARRAY_ROUTINES.min(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(int,
   *      org.briljantframework.array.IntArray)
   */
  public static IntArray min(int dim, IntArray x) {
    return ARRAY_ROUTINES.min(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(org.briljantframework.array.LongArray)
   */
  public static long min(LongArray x) {
    return ARRAY_ROUTINES.min(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(int,
   *      org.briljantframework.array.LongArray)
   */
  public static LongArray min(int dim, LongArray x) {
    return ARRAY_ROUTINES.min(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(org.briljantframework.array.Array,
   *      java.util.Comparator)
   */
  public static <T> T min(Array<T> x, Comparator<T> cmp) {
    return ARRAY_ROUTINES.min(x, cmp);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(int, org.briljantframework.array.Array,
   *      java.util.Comparator)
   */
  public static <T> Array<T> min(int dim, Array<T> x, Comparator<T> cmp) {
    return ARRAY_ROUTINES.min(dim, x, cmp);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(org.briljantframework.array.Array)
   */
  public static <T extends Comparable<T>> T min(Array<T> x) {
    return ARRAY_ROUTINES.min(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(int, org.briljantframework.array.Array)
   */
  public static <T extends Comparable<T>> Array<T> min(int dim, Array<T> x) {
    return ARRAY_ROUTINES.min(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(org.briljantframework.array.DoubleArray)
   */
  public static double max(DoubleArray x) {
    return ARRAY_ROUTINES.max(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(int,
   *      org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray max(int dim, DoubleArray x) {
    return ARRAY_ROUTINES.max(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(org.briljantframework.array.IntArray)
   */
  public static int max(IntArray x) {
    return ARRAY_ROUTINES.max(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(int,
   *      org.briljantframework.array.IntArray)
   */
  public static IntArray max(int dim, IntArray x) {
    return ARRAY_ROUTINES.max(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(org.briljantframework.array.LongArray)
   */
  public static long max(LongArray x) {
    return ARRAY_ROUTINES.max(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(int,
   *      org.briljantframework.array.LongArray)
   */
  public static LongArray max(int dim, LongArray x) {
    return ARRAY_ROUTINES.max(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(org.briljantframework.array.Array,
   *      java.util.Comparator)
   */
  public static <T> T max(Array<T> x, Comparator<T> cmp) {
    return ARRAY_ROUTINES.max(x, cmp);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(int, org.briljantframework.array.Array,
   *      java.util.Comparator)
   */
  public static <T> Array<T> max(int dim, Array<T> x, Comparator<T> cmp) {
    return ARRAY_ROUTINES.max(dim, x, cmp);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(org.briljantframework.array.Array)
   */
  public static <T extends Comparable<T>> T max(Array<T> x) {
    return ARRAY_ROUTINES.max(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(int, org.briljantframework.array.Array)
   */
  public static <T extends Comparable<T>> Array<T> max(int dim, Array<T> x) {
    return ARRAY_ROUTINES.max(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#norm2(org.briljantframework.array.DoubleArray)
   */
  public static double norm2(DoubleArray a) {
    return ARRAY_ROUTINES.norm2(a);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#norm2(org.briljantframework.array.ComplexArray)
   */
  public static Complex norm2(ComplexArray a) {
    return ARRAY_ROUTINES.norm2(a);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#asum(org.briljantframework.array.DoubleArray)
   */
  public static double asum(DoubleArray a) {
    return ARRAY_ROUTINES.asum(a);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#asum(org.briljantframework.array.ComplexArray)
   */
  public static double asum(ComplexArray a) {
    return ARRAY_ROUTINES.asum(a);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#iamax(org.briljantframework.array.DoubleArray)
   */
  public static int iamax(DoubleArray x) {
    return ARRAY_ROUTINES.iamax(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#iamax(org.briljantframework.array.ComplexArray)
   */
  public static int iamax(ComplexArray x) {
    return ARRAY_ROUTINES.iamax(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#cumsum(org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray cumsum(DoubleArray x) {
    return ARRAY_ROUTINES.cumsum(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#cumsum(int,
   *      org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray cumsum(int dim, DoubleArray x) {
    return ARRAY_ROUTINES.cumsum(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#trace(org.briljantframework.array.DoubleArray)
   */
  public static double trace(DoubleArray x) {
    return ARRAY_ROUTINES.trace(x);
  }

  /**
   * Split an array into multiple sub-arrays vertically (row-wise).
   *
   * <p/>
   * This is equivalent to {@link #split(BaseArray, int, int)} with dim=0 (default), the array is
   * always split along the first axis regardless of the array dimension.
   *
   * @param array the array
   * @param parts the number of sub-arrays
   * @param <T> the array type
   * @return a list of array parts
   * @see #split(BaseArray, int)
   */
  public static <T extends BaseArray<T>> List<T> vsplit(T array, int parts) {
    if (array.isVector()) {
      array = array.reshape(array.size(), 1);
    }
    return split(array, parts, 0);
  }

  /**
   * Split an array into multiple sub-arrays of equal size.
   *
   * @param array the array
   * @param parts the number of parts
   * @param dim the dimension along which to split
   * @param <T> the type of array
   * @return a (lazy) list of sub-arrays. Each time {@link List#get(int)} is called, the sub-arrays
   *         are recomputed. If accessing sub-arrays multiple times, consider creating a new
   *         pre-computed list (e.g., {@code new ArrayList<>(Arrays.split(x, 2, 0)}).
   */
  public static <T extends BaseArray<T>> List<T> split(T array, int parts, int dim) {
    Check.argument(array.size(dim) % parts == 0);
    int[] shape = array.getShape();
    shape[dim] /= parts;

    return new AbstractList<T>() {
      @Override
      public T get(int index) {
        T empty = array.newEmptyArray(shape);
        int size = empty.size(dim);
        int rowPadding = index * size;
        for (int i = 0; i < size; i++) {
          empty.select(dim, i).assign(array.select(dim, rowPadding + i));
        }
        return empty;
      }

      @Override
      public int size() {
        return parts;
      }
    };
  }

  /**
   * Split an array along its first dimension into multiple sub-arrays of equal size.
   *
   * @param array the array
   * @param parts the number of parts
   * @param <S> the type of array
   * @return a list of sub-arrays
   * @see #split(BaseArray, int, int)
   */
  public static <S extends BaseArray<S>> List<S> split(S array, int parts) {
    return split(array, parts, 0);
  }

  /**
   * Split an array into multiple sub-arrays vertically (column-wise).
   *
   * <p/>
   * This is equivalent to {@link #split(BaseArray, int, int)} with dim=1 (default), the array is
   * always split along the second axis regardless of the array dimension.
   *
   * @param array the array
   * @param parts the parts (must be evenly dividable with {@code arrays.size(1))}
   * @param <T> the array type
   * @return a list of array parts
   * @see #split(BaseArray, int, int)
   */
  public static <T extends BaseArray<T>> List<T> hsplit(T array, int parts) {
    if (array.isVector()) {
      array = array.reshape(1, array.size());
    }
    return split(array, parts, 1);
  }

  /**
   * Stack arrays in sequence vertially (row wise).
   *
   * @param arrays the arrays
   * @param <T> the type of array
   * @return a new array
   * @see #vstack(List)
   */
  @SafeVarargs
  public static <T extends BaseArray<T>> T vstack(T... arrays) {
    return vstack(java.util.Arrays.asList(arrays));
  }

  /**
   * Stack arrays in sequence vertically (row wise). Take a sequence of arrays and stack them
   * vertically to make a single array. Rebuild arrays divided by {@link #vsplit(BaseArray, int)}.
   *
   * @param arrays a list of arrays
   * @param <T> the type of arrays
   * @return a new array
   * @see #concatenate(List, int)
   */
  public static <T extends BaseArray<T>> T vstack(List<T> arrays) {
    return concatenate(new AbstractList<T>() {
      @Override
      public T get(int index) {
        T v = arrays.get(index);
        return v.isVector() ? v.reshape(v.size(), 1) : v;
      }

      @Override
      public int size() {
        return arrays.size();
      }
    }, 0);
  }

  /**
   * Join a sequence of arrays along an existing dimension.
   *
   * @param arrays the arrays
   * @param dim the dimension along which the arrays are concatenated
   * @param <T> the type of array
   * @return a new array
   */
  public static <T extends BaseArray<T>> T concatenate(List<T> arrays, int dim) {
    T prototype = arrays.get(0);
    int[] shape = prototype.getShape();
    shape[dim] = 0;

    // TODO: 03/12/15 this will be a performance bottleneck for large splitted array lists
    for (T array : arrays) {
      Check.argument(prototype.dims() == array.dims(), "illegal dimension");
      for (int i = 0; i < prototype.dims(); i++) {
        if (i != dim) {
          Check.argument(array.size(i) == prototype.size(i), "illegal shape");
        }
      }
      shape[dim] += array.size(dim);
    }

    T empty = prototype.newEmptyArray(shape);
    int i = 0;
    for (T array : arrays) {
      for (int j = 0; j < array.size(dim); j++) {
        empty.select(dim, i++).assign(array.select(dim, j));
      }
    }

    return empty;
  }

  /**
   * Join a sequence of arrays along an existing dimension (dim = 1).
   *
   * @param arrays the arrays
   * @param <T> the type of array
   * @return a new array
   */
  public static <T extends BaseArray<T>> T concatenate(List<T> arrays) {
    return concatenate(arrays, 0);
  }

  /**
   * Stack arrays in sequence horizontally (column wise).
   *
   * @param arrays the arrays
   * @param <T> the type of array
   * @return a new array
   * @see #hstack(List)
   */
  @SafeVarargs
  public static <T extends BaseArray<T>> T hstack(T... arrays) {
    return hstack(java.util.Arrays.asList(arrays));
  }

  /**
   * Stack arrays in sequence horizontally (column wise). Take a sequence of arrays and stack them
   * horizontally to make a single array. Rebuild arrays divided by {@link #hsplit(BaseArray, int)}.
   *
   * @param arrays the arrays
   * @param <T> the type of array
   * @return a new array
   * @see #concatenate(List, int)
   */
  public static <T extends BaseArray<T>> T hstack(List<T> arrays) {
    return concatenate(new AbstractList<T>() {
      @Override
      public T get(int index) {
        T a = arrays.get(index);
        return a.dims() == 1 ? a.reshape(1, a.size()) : a;
      }

      @Override
      public int size() {
        return arrays.size();
      }
    }, 1);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#copy(org.briljantframework.array.BaseArray,
   *      org.briljantframework.array.BaseArray)
   */
  public static <T extends BaseArray<T>> void copy(T from, T to) {
    ARRAY_ROUTINES.copy(from, to);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#swap(org.briljantframework.array.BaseArray,
   *      org.briljantframework.array.BaseArray)
   */
  public static <T extends BaseArray<T>> void swap(T a, T b) {
    ARRAY_ROUTINES.swap(a, b);
  }

  public static <T extends BaseArray<T>> T take(T x, int num) {
    if (num < 0 || num > x.size()) {
      throw new IllegalArgumentException();
    }
    T c = x.newEmptyArray(num);
    for (int i = 0; i < num; i++) {
      c.set(i, x, i);
    }
    return c;
  }

  /**
   * Repeat the elements of an array. Ravels the array and repeate each element.
   *
   * <p/>
   * Example
   *
   * <pre>
   * Arrays.repeat(Arrays.range(2 * 2).reshape(2, 2), 2);
   * </pre>
   *
   * produces
   *
   * <pre>
   * array([0, 0, 1, 1, 2, 2, 3, 3])
   * </pre>
   *
   * @param x the array
   * @param num the number of repeats
   * @param <T> the type of array
   * @return a new array
   */
  public static <T extends BaseArray<T>> T repeat(T x, int num) {
    T array = x.newEmptyArray(x.size() * num);
    for (int i = 0; i < x.size(); i++) {
      int pad = i * num;
      for (int j = 0; j < num; j++) {
        array.set(pad + j, x, i);
      }
    }
    return array;
  }

  /**
   * Repeat elements of an array.
   * <p/>
   * Examples
   *
   * <pre>
   * IntArray x = Arrays.range(3 * 3).reshape(3, 3);
   * </pre>
   *
   * produces,
   *
   * <pre>
   * array([[0, 3, 6],
   *        [1, 4, 7],
   *        [2, 5, 8]])
   * </pre>
   *
   * and
   *
   * <pre>
   * Arrays.repeat(0, x, 3);
   * </pre>
   *
   * produces,
   *
   * <pre>
   * array([[0, 3, 6],
   *        [0, 3, 6],
   *        [0, 3, 6],
   *        [1, 4, 7],
   *        [1, 4, 7],
   *        [1, 4, 7],
   *        [2, 5, 8],
   *        [2, 5, 8],
   *        [2, 5, 8]])
   * </pre>
   *
   * and
   *
   * <pre>
   * Arrays.repeat(1, x, 3);
   * </pre>
   *
   * produces
   *
   * <pre>
   * array([[0, 0, 0, 3, 3, 3, 6, 6, 6],
   *        [1, 1, 1, 4, 4, 4, 7, 7, 7],
   *        [2, 2, 2, 5, 5, 5, 8, 8, 8]])
   * </pre>
   *
   * and
   *
   * <pre>
   * Arrays.repeat(0, Arrays.range(3), 3);
   * </pre>
   *
   * produces
   *
   * <pre>
   * array([[0, 1, 2],
   *        [0, 1, 2],
   *        [0, 1, 2]])
   * </pre>
   *
   * @param dim the dimension along which to repeat the elements
   * @param x the array
   * @param num the number of repetitions
   * @param <T> the array type
   * @return a new array
   */
  public static <T extends BaseArray<T>> T repeat(int dim, T x, int num) {
    if (x.dims() == 1) {
      int[] reshape = new int[2];
      reshape[dim] = 1;
      reshape[Math.abs(1 - dim)] = x.size();
      x = x.reshape(reshape);
    }
    int[] shape = x.getShape();
    shape[dim] *= num;
    T array = x.newEmptyArray(shape);
    for (int i = 0; i < x.size(dim); i++) {
      int pad = i * num;
      for (int j = 0; j < num; j++) {
        array.select(dim, pad + j).assign(x.select(dim, i));
      }
    }

    return array;
  }

  /**
   * Construct an array by repeating the given array the given number of times (per dimension).
   *
   * <p/>
   * The constructed array has the same dimension as {@code Math.max(reps.length, x.dims())}.
   *
   * <p/>
   * If {@code x.dims() < reps.length}, {@code x} is promoted to a {@code reps.length}-dimensional
   * array by prepending dimensions of size {@code 1}. For example, if {@code x} has shape 5 and the
   * replication is {@code new int[2]} the resulting array is promoted to 2d.
   *
   * <p/>
   * If {@code reps.length < x.dims()}, the replication array is prepended with ones.
   *
   * <p/>
   * Example
   *
   * <pre>
   * IntArray a = Range.of(3);
   * Arrays.tile(a, 2);
   * </pre>
   *
   * produces
   *
   * <pre>
   *   array([0, 1, 2, 0, 1, 2])
   * </pre>
   *
   * and
   *
   * <pre>
   * Arrays.tile(a, 2, 2);
   * </pre>
   *
   * produces
   *
   * <pre>
   * array([[0, 1, 2, 0, 1, 2],
   *        [0, 1, 2, 0, 1, 2]])
   * </pre>
   *
   * and
   *
   * <pre>
   * DoubleArray b = Arrays.newDoubleMatrix(new double[][] { {1, 2}, {3, 4}});
   * Arrays.tile(b, 2);
   * </pre>
   *
   * produces
   *
   * <pre>
   * array([[1.000, 2.000, 1.000, 2.000],
   *        [3.000, 4.000, 3.000, 4.000]])
   * </pre>
   *
   * and
   *
   * <pre>
   * Arrays.tile(b, 2, 1);
   * </pre>
   *
   * produces
   *
   * <pre>
   * array([[1.000, 2.000],
   *        [3.000, 4.000],
   *        [1.000, 2.000],
   *        [3.000, 4.000]])
   * </pre>
   *
   * @param x the array
   * @param reps the number of replications per dimension
   * @param <S> the array type
   * @return a new array
   */
  public static <S extends BaseArray<S>> S tile(S x, int... reps) {
    int dims = Math.max(x.dims(), reps.length);
    if (x.dims() < reps.length) {
      x = x.reshape(prependDimension(x.getShape(), dims));
    } else if (reps.length < x.dims()) {
      reps = prependDimension(reps, dims);
    }

    int[] shape = new int[dims];
    for (int i = 0; i < shape.length; i++) {
      int rep = reps[i];
      if (rep <= 0) {
        throw new NotStrictlyPositiveException(rep);
      }
      shape[i] = x.size(i) * rep;
    }

    S array = x.newEmptyArray(shape);
    tile(array, x, 0, reps);
    return array;
  }

  /*
   * Prepend ones
   */
  private static int[] prependDimension(int[] arr, int max) {
    int[] newArr = new int[max];
    int diff = Math.abs(max - arr.length);
    for (int i = 0; i < max; i++) {
      if (i < diff) {
        newArr[i] = 1;
      } else {
        newArr[i] = arr[i - diff];
      }
    }
    return newArr;
  }

  /*
   * Recursively fill the last dimension with the last dimension of x with repeated copies.
   */
  private static <S extends BaseArray<S>> void tile(S array, S x, int dim, int[] reps) {
    Check.argument(array.dims() == x.dims(), "Illegal array sizes to tile.");
    if (array.dims() == 1 && x.dims() == 1) {
      int size = x.size();
      for (int j = 0; j < reps[dim]; j++) {
        int pad = j * size;
        for (int i = 0; i < size; i++) {
          array.set(pad + i, x, i);
        }
      }
    } else {
      int size = x.size(0);
      for (int j = 0; j < reps[dim]; j++) {
        int pad = size * j;
        for (int i = 0; i < size; i++) {
          tile(array.select(0, pad + i), x.select(0, i), dim + 1, reps);
        }
      }
    }
  }

  /**
   * Broadcast the given arrays against each other.
   *
   * @param arrays the arrays to broadcast
   * @param <E> the array type
   * @return a list of broadcasted array views
   * @see #broadcastArrays(List)
   */
  public static <E extends BaseArray<E>> List<E> broadcastArrays(E... arrays) {
    return broadcastArrays(java.util.Arrays.asList(arrays));
  }

  /**
   * Broadcast the given arrays against each other.
   *
   * @param arrays the arrays to broadcast
   * @param <E> the array type
   * @return a list of broadcasted array views
   */
  public static <E extends BaseArray<E>> List<E> broadcastArrays(List<? extends E> arrays) {
    Check.argument(!arrays.isEmpty(), "no arrays given");
    if (arrays.size() == 1) {
      return new ArrayList<>(arrays);
    }
    int dims = arrays.stream().mapToInt(BaseArray::dims).max().getAsInt();
    int[] shape = new int[dims];
    java.util.Arrays.fill(shape, 1);
    for (E array : arrays) {
      for (int i = 0; i < shape.length; i++) {
        int shapeIndex = shape.length - 1 - i;
        int arrayIndex = array.dims() - 1 - i;
        if (i < array.dims()) {
          if (shape[shapeIndex] != array.size(arrayIndex)
              && (shape[shapeIndex] != 1 && array.size(arrayIndex) != 1)) {
            throw new IllegalArgumentException("arrays cannot be broadcast to the same shape");
          }
          shape[shapeIndex] = Math.max(shape[shapeIndex], array.size(arrayIndex));
        } else {
          shape[shapeIndex] = Math.max(shape[shapeIndex], 1);
        }
      }
    }
    final int[] newShape = shape;
    return new AbstractList<E>() {
      @Override
      public E get(int index) {
        E x = arrays.get(index);
        return x.asView(newShape,
                        StrideUtils.broadcastStrides(x.getStride(), x.getShape(), newShape));
      }

      @Override
      public int size() {
        return arrays.size();
      }
    };
  }

  /**
   * Broadcast the array to the specified shape. The array must be
   * {@linkplain ShapeUtils#isBroadcastCompatible(int[], int[]) broadcast compatible} with the given
   * shape. The returned array is not memory continuous and multiple cells might share value (i.e.,
   * modifications might change multiple cell values). {@linkplain BaseArray#copy() Copy} the array
   * to get a memory continuous array.
   * <p/>
   * Examples:
   * <p/>
   * Given the 1d-array:
   *
   * <pre>
   * IntArray a = IntArray.of(0, 1, 2);
   * </pre>
   *
   * broadcasting it to a {@code 4 x 3} 2d-array
   *
   * <pre>
   * Arrays.broadcastTo(a, 4, 3);
   * </pre>
   *
   * produces
   *
   * <pre>
   * array([[0, 1, 2],
   *        [0, 1, 2],
   *        [0, 1, 2],
   *        [0, 1, 2]])
   * </pre>
   *
   * Reshaping {@code a} to a {@code 3 x 1} 2d-array and broadcast to a {@code 3 x 6} array
   *
   * <pre>
   * Arrays.broadcastTo(a.reshape(3, 1), 3, 6);
   * </pre>
   *
   * produces,
   *
   * <pre>
   * array([[0, 0, 0, 0, 0, 0],
   *        [1, 1, 1, 1, 1, 1], 
   *        [2, 2, 2, 2, 2, 2]])
   * </pre>
   *
   * @param x the array
   * @param newShape the new shape
   * @param <E> the array type
   * @return a broadcasted view
   */
  public static <E extends BaseArray<E>> E broadcast(E x, int... newShape) {
    Check.argument(newShape.length > 0 && x.dims() <= newShape.length, "to few new dimensions");
    int[] oldShape = x.getShape();
    Check.argument(ShapeUtils.isBroadcastCompatible(oldShape, newShape),
                   "Can't broadcast array with shape %s to %s", java.util.Arrays.toString(oldShape),
                   java.util.Arrays.toString(newShape));

    int[] oldStrides = x.getStride();
    if (java.util.Arrays.equals(oldShape, newShape)) {
      return x.asView(oldShape, oldStrides);
    } else {
      newShape = ShapeUtils.broadcast(oldShape, newShape);
      int[] newStrides = StrideUtils.broadcastStrides(oldStrides, oldShape, newShape);
      return x.asView(newShape, newStrides);
    }
  }

  /**
   * Interchange two dimensions of an array.
   *
   * @param array the array
   * @param a the first dimension
   * @param b the second dimension
   * @param <E> the array type
   * @return
   */
  public static <E extends BaseArray<E>> E swapDimension(E array, int a, int b) {
    int[] dims = new int[array.dims()];
    for (int i = 0; i < dims.length; i++) {
      dims[i] = i;
    }
    dims[a] = b;
    dims[b] = a;
    return transpose(array, dims);
  }

  /**
   * Transpose the given array while permuting the dimensions.
   *
   * @param array the array
   * @param permute the new indices for the dimensions
   * @param <E> the array type
   * @return a view
   */
  private static <E extends BaseArray<E>> E transpose(E array, int[] permute) {
    // If no permutation is given, just transpose the array
    if (permute == null) {
      permute = new int[array.dims()];
      for (int i = 0; i < permute.length; i++) {
        permute[i] = permute.length - 1 - i;
      }
    }

    Check.argument(array.dims() == permute.length, "dimension don't match array");
    int n = permute.length;

    int[] reversePermutation = new int[n];
    int[] permutation = new int[n];

    java.util.Arrays.fill(reversePermutation, -1);
    for (int i = 0; i < n; i++) {
      int dim = permute[i];
      if (dim < 0 || dim >= array.dims()) {
        throw new IllegalArgumentException("invalid dimension for array");
      }

      if (reversePermutation[dim] != -1) {
        throw new IllegalArgumentException("repeated dimension in transpose");
      }

      reversePermutation[dim] = i;
      permutation[i] = dim;
    }

    int[] shape = new int[n];
    int[] stride = new int[n];
    for (int i = 0; i < n; i++) {
      shape[i] = array.size(permutation[i]);
      stride[i] = array.stride(permutation[i]);
    }

    return array.asView(array.getOffset(), shape, stride);
  }

  /**
   * Returns the sum along the specified dimension.
   *
   * @param dim the dimension
   * @param x the array
   * @return an array of sums
   */
  public static ComplexArray sum(int dim, ComplexArray x) {
    return ARRAY_ROUTINES.sum(dim, x);
  }

  /**
   * Return the sum.
   *
   * @param x the array
   * @return the sum
   */
  public static Complex sum(ComplexArray x) {
    return ARRAY_ROUTINES.sum(x);
  }

  /**
   * Return the sum.
   *
   * @param x the array
   * @return the sum
   */
  public static long sum(LongArray x) {
    return ARRAY_ROUTINES.sum(x);
  }

  /**
   * Returns the sum along the specified dimension.
   *
   * @param dim the dimension
   * @param x the array
   * @return an array of sums
   */
  public static LongArray sum(int dim, LongArray x) {
    return ARRAY_ROUTINES.sum(dim, x);
  }

  /**
   * Sort a double array in its natural order.
   *
   * @param x the array
   * @return a new sorted array
   * @see DoubleArray#sort()
   */
  public static DoubleArray sort(DoubleArray x) {
    return sort(x, Double::compare);
  }

  /**
   * Sort the array according to the given comparator.
   *
   * @param x the array
   * @param comparator the comparator
   * @return a new array
   */
  public static DoubleArray sort(DoubleArray x, DoubleComparator comparator) {
    DoubleArray c = x.copy();
    c.sort(comparator);
    return c;
  }

  /**
   * Sort the vectors along the specified dimensions in its natural order.
   *
   * @param dim the dimension
   * @param x the array
   * @return a new array
   */
  public static DoubleArray sort(int dim, DoubleArray x) {
    return sort(dim, x, Double::compare);
  }

  /**
   * Sort the vectors along the specified dimensions in its natural order.
   *
   * @param dim the dimension
   * @param x the array
   * @return a new array
   */
  public static DoubleArray sort(int dim, DoubleArray x, DoubleComparator comparator) {
    DoubleArray c = x.copy();
    int vectors = c.vectors(dim);
    for (int i = 0; i < vectors; i++) {
      c.getVector(dim, i).sort(comparator);
    }
    return c;
  }

  /**
   * Sort a int array in its natural order.
   *
   * @param x the array
   * @return a new sorted array
   * @see IntArray#sort()
   */
  public static IntArray sort(IntArray x) {
    return sort(x, Integer::compare);
  }

  /**
   * Sort the array according to the given comparator.
   *
   * @param x the array
   * @param comparator the comparator
   * @return a new array
   */
  public static IntArray sort(IntArray x, IntComparator comparator) {
    IntArray c = x.copy();
    c.sort(comparator);
    return c;
  }

  /**
   * Sort the vectors along the specified dimensions in its natural order.
   *
   * @param dim the dimension
   * @param x the array
   * @return a new array
   */
  public static IntArray sort(int dim, IntArray x) {
    return sort(dim, x, Integer::compare);
  }

  /**
   * Sort the vectors along the specified dimensions in its natural order.
   *
   * @param dim the dimension
   * @param x the array
   * @return a new array
   */
  public static IntArray sort(int dim, IntArray x, IntComparator comparator) {
    IntArray c = x.copy();
    int vectors = c.vectors(dim);
    for (int i = 0; i < vectors; i++) {
      c.getVector(dim, i).sort(comparator);
    }
    return c;
  }

  /**
   * Sort a long array in its natural order.
   *
   * @param x the array
   * @return a new sorted array
   * @see LongArray#sort()
   */
  public static LongArray sort(LongArray x) {
    return sort(x, Long::compare);
  }

  /**
   * Sort the array according to the given comparator.
   *
   * @param x the array
   * @param comparator the comparator
   * @return a new array
   */
  public static LongArray sort(LongArray x, LongComparator comparator) {
    LongArray c = x.copy();
    c.sort(comparator);
    return c;
  }

  /**
   * Sort the vectors along the specified dimensions in its natural order.
   *
   * @param dim the dimension
   * @param x the array
   * @return a new array
   */
  public static LongArray sort(int dim, LongArray x) {
    return sort(dim, x, Long::compare);
  }

  /**
   * Sort the vectors along the specified dimensions in its natural order.
   *
   * @param dim the dimension
   * @param x the array
   * @return a new array
   */
  public static LongArray sort(int dim, LongArray x, LongComparator comparator) {
    LongArray c = x.copy();
    int vectors = c.vectors(dim);
    for (int i = 0; i < vectors; i++) {
      c.getVector(dim, i).sort(comparator);
    }
    return c;
  }

  /**
   * Sort an array in its natural order.
   *
   * @param array the array
   * @param <T> the element type
   * @return a new array
   */
  public static <T extends Comparable<T>> Array<T> sort(Array<T> array) {
    return sort(array, (a, i, j) -> a.get(i).compareTo(a.get(j)));
  }

  /**
   * Sort the array using the given index comparator.
   *
   * @param x the array
   * @param cmp the index comparator
   * @param <S> the array type
   * @return a new array
   */
  public static <S extends BaseArray<S>> S sort(S x, IndexComparator<S> cmp) {
    S out = x.copy();
    QuickSort.quickSort(0, out.size(), (a, b) -> cmp.compare(out, a, b), out);
    return out;
  }

  /**
   * Sort each vector along the specified dimension in its natural order.
   *
   * @param array the array
   * @param <T> the element type
   * @return a new array
   */
  public static <T extends Comparable<T>> Array<T> sort(int dim, Array<T> array) {
    return sort(dim, array, (a, i, j) -> a.get(i).compareTo(a.get(j)));
  }

  /**
   * Sort each vector along the specified dimension according to the given index comparator.
   *
   * @param dim the dimension
   * @param x the array
   * @param cmp the index comparator
   * @param <S> the element type
   * @return a new array
   */
  public static <S extends BaseArray<S>> S sort(int dim, S x, IndexComparator<S> cmp) {
    S out = x.copy();
    int m = x.vectors(dim);
    for (int i = 0; i < m; i++) {
      S v = out.getVector(dim, i);
      QuickSort.quickSort(0, v.size(), (a, b) -> cmp.compare(v, a, b), v);
    }

    return out;
  }

  /**
   * Sort the array according to the given comparator.
   *
   * @param array the array
   * @param comparator the comparator
   * @param <T> the element type
   * @return a new array
   */
  public static <T> Array<T> sort(Array<T> array, Comparator<T> comparator) {
    return sort(array, (a, i, j) -> comparator.compare(a.get(i), a.get(j)));
  }

  /**
   * Sort each vector along the specified dimension according to the given comparator.
   *
   * @param dim the dimension
   * @param array the array
   * @param comparator the comparator
   * @param <T> the element type
   * @return a new array
   */
  public static <T> Array<T> sort(int dim, Array<T> array, Comparator<T> comparator) {
    return sort(dim, array, (a, i, j) -> comparator.compare(a.get(i), a.get(j)));
  }

  /**
   * Randomly shuffle the elements in the given array
   *
   * @param x the array
   * @param <S> the array type
   * @return a new (shuffled) array
   */
  public static <S extends BaseArray<S>> S shuffle(S x) {
    S out = x.copy();
    out.permute(out.size());
    return out;
  }

  /**
   * Dot product of two 2d-arrays. It is equivalent to matrix multiplication.
   *
   * @param a the first array
   * @param b the second array
   * @return a new array
   * @see #dot(ArrayOperation, ArrayOperation, DoubleArray, double, DoubleArray)
   */
  public static DoubleArray dot(DoubleArray a, DoubleArray b) {
    return dot(ArrayOperation.KEEP, ArrayOperation.KEEP, a, 1, b);
  }

  /**
   * Dot product of two 2d-arrays. It is equivalent to matrix multiplication
   *
   * <p/>
   * For more control and in some situations better performance consider
   * {@link #gemm(ArrayOperation, ArrayOperation, double, DoubleArray, DoubleArray, double, DoubleArray)
   * gemm}, {@link #ger(double, DoubleArray, DoubleArray, DoubleArray) ger}
   *
   * <p/>
   * The inner product is computed using {@link #inner(DoubleArray, DoubleArray)}.
   *
   * @param transA the transposition of the first array
   * @param transB the transposition of the second array
   * @param a the first array
   * @param b the second array
   * @return a new array
   * @see #gemm(ArrayOperation, ArrayOperation, double, DoubleArray, DoubleArray, double,
   *      DoubleArray)
   */
  public static DoubleArray dot(ArrayOperation transA, ArrayOperation transB, DoubleArray a,
                                double alpha, DoubleArray b) {
    Check.argument(a.isMatrix() && b.isMatrix(), "require 2d-arrays");
    int m = a.size(transA == ArrayOperation.KEEP ? 0 : 1);
    int bm = b.size(transB == ArrayOperation.KEEP ? 0 : 1);
    int n = b.size(transB == ArrayOperation.KEEP ? 1 : 0);
    int k = a.size(transA == ArrayOperation.KEEP ? 1 : 0);
    if (m == 0 || k == 0 || n == 0 || bm == 0) {
      throw new IllegalArgumentException("empty result");
    }
    if (b.size(transB == ArrayOperation.KEEP ? 0 : 1) != a.size(transA == ArrayOperation.KEEP ? 1
                                                                                              : 0)) {
      throw new MultiDimensionMismatchException(a, b);
    }
    DoubleArray c = doubleArray(m, n);
    gemm(transA, transB, alpha, a, b, 1, c);
    return c;
  }

  /**
   * @see ArrayRoutines#gemm(ArrayOperation, ArrayOperation, double, DoubleArray, DoubleArray,
   *      double, DoubleArray)
   */
  public static void gemm(ArrayOperation transA, ArrayOperation transB, double alpha,
                          DoubleArray a, DoubleArray b, double beta, DoubleArray c) {
    ARRAY_ROUTINES.gemm(transA, transB, alpha, a, b, beta, c);
  }

  /**
   * Dot product of two 2d-arrays. It is equivalent to matrix multiplication.
   *
   * @param alpha scaling factor for the first array
   * @param a the first array
   * @param b the second array
   * @return a new array
   * @see #dot(ArrayOperation, ArrayOperation, DoubleArray, double, DoubleArray)
   */
  public static DoubleArray dot(double alpha, DoubleArray a, DoubleArray b) {
    return dot(ArrayOperation.KEEP, ArrayOperation.KEEP, a, alpha, b);
  }

  /**
   * Dot product of two 2d-arrays. It is equivalent to matrix multiplication.
   *
   * @param transA transposition of the first array
   * @param transB transposition of the second array
   * @param a the first array
   * @param b the second array
   * @return a new array
   * @see #dot(ArrayOperation, ArrayOperation, DoubleArray, double, DoubleArray)
   */
  public static DoubleArray dot(ArrayOperation transA, ArrayOperation transB, DoubleArray a,
                                DoubleArray b) {
    return dot(transA, transB, a, 1.0, b);
  }

  /**
   * Compute the inner product of two arrays. If the arguments are non-{@code vectors}, the
   * arguments are raveled.
   *
   * <pre>
   * Arrays.inner(Arrays.linspace(0, 3, 4), Arrays.linspace(0, 3, 4).reshape(2, 2))
   * </pre>
   *
   * @param a the first array
   * @param b the second array
   * @return the inner product
   */
  public static double inner(DoubleArray a, DoubleArray b) {
    a = a.isVector() ? a : a.ravel();
    b = b.isVector() ? b : b.ravel();
    return ARRAY_ROUTINES.inner(a.ravel(), b.ravel());
  }

  /**
   * Computes the outer product of two arrays. If the arguments are {@code vectors}, the result is
   * equivalent to
   * {@link #ger(double, org.briljantframework.array.DoubleArray, org.briljantframework.array.DoubleArray, org.briljantframework.array.DoubleArray)}
   * . In other cases, the arguments are raveled.
   *
   * <p>
   * Example
   *
   * <pre>
   * Arrays.outer(Arrays.linspace(0, 3, 4), Arrays.linspace(0, 3, 4).reshape(2, 2));
   * </pre>
   *
   * produces
   *
   * <pre>
   * array([[0.000, 0.000, 0.000, 0.000],
   *        [0.000, 1.000, 2.000, 3.000],
   *        [0.000, 2.000, 4.000, 6.000],
   *        [0.000, 3.000, 6.000, 9.000]])
   * </pre>
   *
   * @param a the first argument of size {@code m}
   * @param b the second argument of size {@code n}
   * @return a new 2d-array of size {@code m x n}
   */
  public static DoubleArray outer(DoubleArray a, DoubleArray b) {
    a = a.isVector() ? a : a.ravel();
    b = b.isVector() ? b : b.ravel();

    DoubleArray out = doubleArray(a.size(), b.size());
    ger(1, a, b, out);
    return out;
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#ger(double,
   *      org.briljantframework.array.DoubleArray, org.briljantframework.array.DoubleArray,
   *      org.briljantframework.array.DoubleArray)
   */
  public static void ger(double alpha, DoubleArray x, DoubleArray y, DoubleArray a) {
    ARRAY_ROUTINES.ger(alpha, x, y, a);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#inner(org.briljantframework.array.ComplexArray,
   *      org.briljantframework.array.ComplexArray)
   */
  public static Complex inner(ComplexArray a, ComplexArray b) {
    return ARRAY_ROUTINES.inner(a.ravel(), b.ravel());
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#conjugateInner(org.briljantframework.array.ComplexArray,
   *      org.briljantframework.array.ComplexArray)
   */
  public static Complex conjugateInner(ComplexArray a, ComplexArray b) {
    return ARRAY_ROUTINES.conjugateInner(a.ravel(), b.ravel());
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#scal(double,
   *      org.briljantframework.array.DoubleArray)
   */
  public static void scal(double alpha, DoubleArray x) {
    ARRAY_ROUTINES.scal(alpha, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#axpy(double,
   *      org.briljantframework.array.DoubleArray, org.briljantframework.array.DoubleArray)
   */
  public static void axpy(double alpha, DoubleArray x, DoubleArray y) {
    ARRAY_ROUTINES.axpy(alpha, x, y);
  }

  /**
   * Delegates to
   * {@link #gemv(ArrayOperation, double, org.briljantframework.array.DoubleArray, org.briljantframework.array.DoubleArray, double, org.briljantframework.array.DoubleArray)}
   * with the first argument {@code Op.KEEP}, {@code alpha
   * = 1} and {@code beta = 1}
   *
   * @see #gemv(ArrayOperation, double, org.briljantframework.array.DoubleArray,
   *      org.briljantframework.array.DoubleArray, double, org.briljantframework.array.DoubleArray)
   */
  public static void gemv(DoubleArray a, DoubleArray x, DoubleArray y) {
    gemv(1, a, x, 1, y);
  }

  /**
   * Delegates to
   * {@link #gemv(ArrayOperation, double, org.briljantframework.array.DoubleArray, org.briljantframework.array.DoubleArray, double, org.briljantframework.array.DoubleArray)}
   * with the first argument {@code Op.KEEP}.
   *
   * @see #gemv(ArrayOperation, double, org.briljantframework.array.DoubleArray,
   *      org.briljantframework.array.DoubleArray, double, org.briljantframework.array.DoubleArray)
   */
  public static void gemv(double alpha, DoubleArray a, DoubleArray x, double beta, DoubleArray y) {
    gemv(ArrayOperation.KEEP, alpha, a, x, beta, y);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#gemm(ArrayOperation, ArrayOperation, double,
   *      org.briljantframework.array.DoubleArray, org.briljantframework.array.DoubleArray, double,
   *      org.briljantframework.array.DoubleArray)
   */
  public static void gemv(ArrayOperation transA, double alpha, DoubleArray a, DoubleArray x,
                          double beta, DoubleArray y) {
    ARRAY_ROUTINES.gemv(transA, alpha, a, x, beta, y);
  }

  public static void gemm(double alpha, DoubleArray a, DoubleArray b, double beta, DoubleArray c) {
    gemm(ArrayOperation.KEEP, ArrayOperation.KEEP, alpha, a, b, beta, c);
  }

  public static void gemm(DoubleArray a, DoubleArray b, DoubleArray c) {
    gemm(ArrayOperation.KEEP, ArrayOperation.KEEP, 1, a, b, 1, c);
  }

  /**
   * Find argument with max value.
   *
   * @param array the array
   * @return the index of the maximum value
   */
  public static int argmax(DoubleArray array) {
    int index = 0;
    double largest = array.get(0);
    for (int i = 1; i < array.size(); i++) {
      double v = array.get(i);
      if (v > largest) {
        index = i;
        largest = v;
      }
    }
    return index;
  }

  /**
   * Find argument with min value.
   *
   * @param array the array
   * @return the index of the minimum value
   */
  public static int argmin(DoubleArray array) {
    Check.argument(array.size() > 0);
    int index = 0;
    double smallest = array.get(0);
    int n = array.size();
    for (int i = 1; i < n; i++) {
      double v = array.get(i);
      if (v < smallest) {
        smallest = v;
        index = i;
      }
    }
    return index;
  }

  /**
   * Take values in {@code array}, using the indexes in {@code indexes}.
   *
   * @param array the source array
   * @param indexes the indexes of the values to extract
   * @return a new array; the returned matrix has the same type as {@code array} (as returned by
   */
  public static <T extends BaseArray<T>> T take(T array, IntArray indexes) {
    T taken = array.newEmptyArray(indexes.size());
    for (int i = 0; i < indexes.size(); i++) {
      taken.set(i, array, indexes.get(i));
    }
    return taken;
  }

  /**
   * Changes the values of array copy of {@code array} according to the values of the {@code mask}
   * and the values in {@code values}. The value at {@code i} in array copy of {@code array} is set
   * to value at {@code i} from {@code values} if the boolean at {@code i} in {@code mask} is
   * {@code true}.
   *
   * @param array array source array
   * @param mask the mask; same shape as {@code array}
   * @param values the values; same shape as {@code array}
   * @return a new array; the returned array has the same type as {@code array}.
   */
  public static <T extends BaseArray<T>> T mask(T array, BooleanArray mask, T values) {
    Check.dimension(array, mask);
    Check.dimension(array, values);

    T masked = array.copy();
    putMask(masked, mask, values);
    return masked;
  }

  /**
   * Changes the values of {@code a} according to the values of the {@code mask} and the values in
   * {@code values}.
   *
   * @param a the target matrix
   * @param mask the mask; same shape as {@code a}
   * @param values the mask; same shape as {@code a}
   */
  public static <T extends BaseArray<T>> void putMask(T a, BooleanArray mask, T values) {
    Check.dimension(a, mask);
    Check.dimension(a, values);
    for (int i = 0; i < a.size(); i++) {
      if (mask.get(i)) {
        a.set(i, values, i);
      }
    }
  }

  /**
   * Selects the values in {@code a} according to the values in {@code where}, replacing those not
   * selected with {@code replace}.
   *
   * @param a the source matrix
   * @param where the selection matrix; same shape as {@code a}
   * @param replace the replacement value
   * @return a new matrix; the returned matrix has the same type as {@code a}.
   */
  public static IntArray select(IntArray a, BooleanArray where, int replace) {
    Check.dimension(a, where);
    IntArray copy = a.copy();
    copy.assign(where, (b, i) -> b ? replace : i);
    return copy;
  }

  /**
   * Return the order of the values in array (with smallest index first).
   *
   * @param array the array
   * @return the indexes in order
   */
  public static IntArray order(DoubleArray array) {
    return order(array, Double::compare);
  }

  /**
   * Return the order of the values in the given array (according to the comparator).
   *
   * @param array the array
   * @param cmp the comparator
   * @return the indexes in order
   */
  public static IntArray order(DoubleArray array, DoubleComparator cmp) {
    IntArray order = Arrays.range(array.size()).copy();
    order.sort((a, b) -> cmp.compare(array.get(a), array.get(b)));
    return order;
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#range(int)
   */
  public static Range range(int end) {
    return ARRAY_FACTORY.range(end);
  }

  /**
   * The order of elements along the given dimension.
   *
   * @param dim the dimension
   * @param array the array
   * @return the order of each dimension
   */
  public static IntArray order(int dim, DoubleArray array) {
    return order(dim, array, Double::compare);
  }

  /**
   * The order of elements along the given dimension.
   *
   * @param dim the dimension
   * @param array the array
   * @param cmp the comparator
   * @return the order of each dimension
   */
  public static IntArray order(int dim, DoubleArray array, DoubleComparator cmp) {
    int vectors = array.vectors(dim);
    IntArray order = IntArray.zeros(array.getShape());
    for (int i = 0; i < vectors; i++) {
      order.setVector(dim, i, order(array.getVector(dim, i), cmp));
    }
    return order;
  }

  /**
   * Searches the specified array for the specified object using the binary search algorithm. The
   * array must be sorted into ascending order
   *
   * @param array the array
   * @param x the element
   * @return the index of the search key, if it is contained in the list; otherwise,
   *         <tt>(-(<i>insertion point</i>) - 1)</tt>.
   * @see #binarySearch(Array, Object)
   */
  public static int binarySearch(IntArray array, int x) {
    return binarySearch(array.boxed(), x);
  }

  /**
   * Searches the specified array for the specified object using the binary search algorithm. The
   * array must be sorted into ascending order
   *
   * @param array the array
   * @param x the element
   * @return the index of the search key, if it is contained in the list; otherwise,
   *         <tt>(-(<i>insertion point</i>) - 1)</tt>.
   * @see Collections#binarySearch(List, Object)
   */
  public static <T> int binarySearch(Array<? extends Comparable<? super T>> array, T x) {
    return Collections.binarySearch(array.toList(), x);
  }

  /**
   * Searches the specified array for the specified object using the binary search algorithm. The
   * array must be sorted into ascending order
   *
   * @param array the array
   * @param x the element
   * @return the index of the search key, if it is contained in the list; otherwise,
   *         <tt>(-(<i>insertion point</i>) - 1)</tt>.
   * @see #binarySearch(Array, Object)
   */
  public static int binarySearch(DoubleArray array, double x) {
    return binarySearch(array.boxed(), x);
  }

  /**
   * @see #bisectLeft(Array, Object)
   */
  public static int bisectLeft(IntArray array, int value) {
    return bisectLeft(array.boxed(), value);
  }

  /**
   * Locate the insertion point for value in a to maintain sorted order. If value is already present
   * in the array, the insertion point will be before (to the left of) any existing entries. The
   * array must be sorted in ascending order.
   *
   * @param array the array
   * @param value the value
   * @param <T> the class of objects in the array
   * @return the insertion point of the value
   */
  public static <T> int bisectLeft(Array<? extends Comparable<? super T>> array, T value) {
    int i = Collections.binarySearch(array.toList(), value);
    if (i < 0) {
      return -i - 1;
    } else {
      return i;
    }
  }

  /**
   * @see #bisectLeft(Array, Object)
   */
  public static int bisectLeft(DoubleArray array, double value) {
    return bisectLeft(array.boxed(), value);
  }

  /**
   * @see #bisectRight(Array, Object)
   */
  public static int bisectRight(IntArray array, int value) {
    return bisectRight(array.boxed(), value);
  }

  /**
   * Locate the insertion point for value in a to maintain sorted order. If value is already present
   * in the array, the insertion point will be after (to the right of) any existing entries. The
   * array must be sorted in ascending order.
   *
   * @param array the array
   * @param value the value
   * @param <T> the class of objects in the array
   * @return the insertion point of the value
   * @see #bisectLeft(Array, Object)
   */
  public static <T> int bisectRight(Array<? extends Comparable<? super T>> array, T value) {
    int i = Collections.binarySearch(array.toList(), value);
    if (i < 0) {
      return -i - 1;
    } else {
      return i + 1;
    }
  }

  /**
   * @see #bisectRight(Array, Object)
   */
  public static int bisectRight(DoubleArray array, double value) {
    return bisectRight(array.boxed(), value);
  }

  public static DoubleArray cos(ComplexArray array) {
    return ARRAY_ROUTINES.abs(array);
  }

  public static DoubleArray sqrt(DoubleArray array) {
    return ARRAY_ROUTINES.sqrt(array);
  }

  public static DoubleArray pow(DoubleArray in, double power) {
    return ARRAY_ROUTINES.pow(in, power);
  }

  public static DoubleArray log2(DoubleArray array) {
    return ARRAY_ROUTINES.log2(array);
  }

  public static DoubleArray acos(DoubleArray array) {
    return ARRAY_ROUTINES.acos(array);
  }

  public static DoubleArray cosh(DoubleArray array) {
    return ARRAY_ROUTINES.cosh(array);
  }

  public static DoubleArray signum(DoubleArray in) {
    return ARRAY_ROUTINES.signum(in);
  }

  public static DoubleArray cos(DoubleArray array) {
    return ARRAY_ROUTINES.cos(array);
  }

  public static DoubleArray asin(DoubleArray array) {
    return ARRAY_ROUTINES.asin(array);
  }

  public static LongArray abs(LongArray array) {
    return ARRAY_ROUTINES.abs(array);
  }

  public static DoubleArray cbrt(DoubleArray array) {
    return ARRAY_ROUTINES.cbrt(array);
  }

  public static DoubleArray abs(DoubleArray array) {
    return ARRAY_ROUTINES.abs(array);
  }

  public static DoubleArray ceil(DoubleArray array) {
    return ARRAY_ROUTINES.ceil(array);
  }

  public static DoubleArray sinh(DoubleArray array) {
    return ARRAY_ROUTINES.sinh(array);
  }

  public static DoubleArray log(DoubleArray array) {
    return ARRAY_ROUTINES.log(array);
  }

  public static DoubleArray tanh(DoubleArray array) {
    return ARRAY_ROUTINES.tanh(array);
  }

  public static DoubleArray sin(DoubleArray array) {
    return ARRAY_ROUTINES.sin(array);
  }

  public static DoubleArray scalb(DoubleArray array, int scaleFactor) {
    return ARRAY_ROUTINES.scalb(array, scaleFactor);
  }

  public static DoubleArray exp(DoubleArray array) {
    return ARRAY_ROUTINES.exp(array);
  }

  public static DoubleArray log10(DoubleArray in) {
    return ARRAY_ROUTINES.log10(in);
  }

  public static DoubleArray floor(DoubleArray array) {
    return ARRAY_ROUTINES.floor(array);
  }

  public static DoubleArray tan(DoubleArray array) {
    return ARRAY_ROUTINES.tan(array);
  }

  public static IntArray abs(IntArray array) {
    return ARRAY_ROUTINES.abs(array);
  }

  public static LongArray round(DoubleArray in) {
    return ARRAY_ROUTINES.round(in);
  }

  public static DoubleArray atan(DoubleArray array) {
    return ARRAY_ROUTINES.atan(array);
  }

  public static ComplexArray sinh(ComplexArray array) {
    return ARRAY_ROUTINES.sinh(array);
  }

  public static ComplexArray exp(ComplexArray array) {
    return ARRAY_ROUTINES.exp(array);
  }

  public static ComplexArray acos(ComplexArray array) {
    return ARRAY_ROUTINES.acos(array);
  }

  public static ComplexArray sin(ComplexArray array) {
    return ARRAY_ROUTINES.sin(array);
  }

  public static DoubleArray abs(ComplexArray array) {
    return ARRAY_ROUTINES.abs(array);
  }

  public static ComplexArray sqrt(ComplexArray array) {
    return ARRAY_ROUTINES.sqrt(array);
  }

  public static ComplexArray log(ComplexArray array) {
    return ARRAY_ROUTINES.log(array);
  }

  public static ComplexArray floor(ComplexArray array) {
    return ARRAY_ROUTINES.floor(array);
  }

  public static ComplexArray tan(ComplexArray array) {
    return ARRAY_ROUTINES.tan(array);
  }

  public static ComplexArray tanh(ComplexArray array) {
    return ARRAY_ROUTINES.tanh(array);
  }

  public static ComplexArray asin(ComplexArray array) {
    return ARRAY_ROUTINES.asin(array);
  }

  public static ComplexArray cosh(ComplexArray array) {
    return ARRAY_ROUTINES.cosh(array);
  }

  public static ComplexArray atan(ComplexArray array) {
    return ARRAY_ROUTINES.atan(array);
  }

  public static ComplexArray ceil(ComplexArray array) {
    return ARRAY_ROUTINES.ceil(array);
  }

  public static int arg(Predicate<Boolean> predicate, BooleanArray array) {
    for (int i = 0; i < array.size(); i++) {
      if (predicate.test(array.get(i))) {
        return i;
      }
    }
    return -1;
  }

  public static boolean any(DoubleArray array, DoublePredicate predicate) {
    for (int i = 0; i < array.size(); i++) {
      if (predicate.test(array.get(i))) {
        return true;
      }
    }
    return false;
  }

  public static <S extends BaseArray<S>> S where(BooleanArray c, S x, S y) {
    Check.dimension(x.size(), y.size());
    Check.dimension(x.size(), c.size());
    int size = x.size();
    S selected = x.newEmptyArray(size);
    for (int i = 0; i < size; i++) {
      selected.set(i, c.get(i) ? x : y, i);
    }
    return selected;
  }

  public static DoubleArray select(DoubleBiPredicate predicate, DoubleArray x, DoubleArray y) {
    Check.size(x, y);
    int size = x.size();
    DoubleArray selected = doubleArray(size);
    for (int i = 0; i < size; i++) {
      double a = x.get(i);
      double b = y.get(i);
      selected.set(i, predicate.test(a, b) ? a : b);
    }
    return selected;
  }

  public static double maxExcluding(DoubleArray array, int not) {
    Double max = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < array.size(); i++) {
      if (i == not) {
        continue;
      }
      double m = array.get(i);
      if (m > max) {
        max = m;
      }
    }
    return max;
  }
}
