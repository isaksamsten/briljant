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

import java.io.*;
import java.util.*;
import java.util.function.*;
import java.util.stream.*;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.distribution.IntegerDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformIntegerDistribution;
import org.apache.commons.math3.exception.NotStrictlyPositiveException;
import org.briljantframework.Check;
import org.briljantframework.array.api.*;
import org.briljantframework.array.netlib.NetlibArrayBackend;
import org.briljantframework.data.statistics.FastStatistics;
import org.briljantframework.exceptions.MultiDimensionMismatchException;
import org.briljantframework.function.DoubleBiPredicate;
import org.briljantframework.function.IntBiPredicate;
import org.briljantframework.function.LongBiPredicate;
import org.briljantframework.util.sort.IndexComparator;
import org.briljantframework.util.sort.QuickSort;

import net.mintern.primitive.comparators.DoubleComparator;
import net.mintern.primitive.comparators.IntComparator;
import net.mintern.primitive.comparators.LongComparator;

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

  private static final ArrayBackend ARRAY_BACKEND;
  private static final ArrayFactory ARRAY_FACTORY;
  private static final ArrayRoutines ARRAY_ROUTINES;

  static {
    ARRAY_BACKEND =
        StreamSupport.stream(ServiceLoader.load(ArrayService.class).spliterator(), false)
            .filter(ArrayService::isAvailable)
            .sorted((a, b) -> Integer.compare(b.getPriority(), a.getPriority())).findFirst()
            .map(ArrayService::getArrayBackend).orElseThrow(
                () -> new UnsupportedOperationException("Can't find ArrayService to initialize"));

    ARRAY_FACTORY = ARRAY_BACKEND.getArrayFactory();
    ARRAY_ROUTINES = ARRAY_BACKEND.getArrayRoutines();
    linalg = ARRAY_BACKEND.getLinearAlgebraRoutines();
  }

  private Arrays() {}

  public static ArrayBackend getArrayBackend() {
    return ARRAY_BACKEND;
  }

  /**
   * Reads a matrix from an IDX file.
   * 
   * @param inputStream the input stream
   * @return a double matrix
   * @throws IOException if an IO error occurs
   */
  public static DoubleArray readIdx(InputStream inputStream) throws IOException {
    DataInputStream dis = new DataInputStream(new BufferedInputStream(inputStream));
    int magic = dis.readInt();
    int first = magic >> 24 & 0xFF;
    int second = magic >> 16 & 0xFF;
    int size = magic >> 8 & 0xFF;
    int dims = magic & 0xFF;

    Check.state(first == 0 && second == 0);
    int[] shape = new int[dims];
    int sum = 1;
    for (int i = 0; i < dims; i++) {
      shape[i] = dis.readInt();
      sum *= shape[i];
    }

    int[] stride = StrideUtils.computeStride(shape);
    double[] data = new double[sum];
    for (int i = 0; i < data.length; i++) {
      double value;
      switch (size) {
        case 0x08:
          value = dis.readUnsignedByte();
          break;
        case 0x09:
          value = dis.readByte();
          break;
        case 0x0B:
          value = dis.readShort();
          break;
        case 0x0C:
          value = dis.readInt();
          break;
        case 0x0D:
          value = dis.readFloat();
          break;
        case 0x0E:
          value = dis.readDouble();
          break;
        default:
          throw new IllegalStateException("illegal size");
      }
      int cindex = StrideUtils.cindex(i, 0, stride, shape);
      data[cindex] = value;
    }

    return DoubleArray.of(data).reshape(shape);
  }

  public static void writeIdx(BaseArray<?> array, OutputStream outputStream) {
    DataOutputStream dis = new DataOutputStream(new BufferedOutputStream(outputStream));
    int dims = array.dims();
    int size;
    if (array instanceof DoubleArray) {
      size = 0x0E;
    } else if (array instanceof IntArray) {
      size = 0x0C;
    } else {
      throw new IllegalArgumentException();
    }
    throw new UnsupportedOperationException();

  }

  public static IntArray hist(DoubleArray array, double min, double max, int bins) {
    IntArray result = IntArray.zeros(bins);
    double binSize = (max - min) / bins;
    for (int i = 0; i < array.size(); i++) {
      double d = array.get(i);
      int bin = (int) ((d - min) / binSize);
      if (bin >= 0 && bin < bins) {
        result.set(bin, result.get(bin) + 1);
      }
    }
    return result;
  }

  public static IntArray hist(DoubleArray array, int bins) {
    FastStatistics statistics = new FastStatistics();
    statistics.addAll(array);
    return hist(array, statistics.getMin(), statistics.getMax(), bins);
  }

  public static <T> Array<T> unmodifiableArray(Array<T> array) {
    if (array instanceof UnmodifiableArray) {
      return array;
    }
    return new UnmodifiableArray<>(array);
  }

  public static DoubleArray unmodifiableArray(DoubleArray array) {
    if (array instanceof UnmodifiableDoubleArray) {
      return array;
    }
    return new UnmodifiableDoubleArray(array);
  }

  public static IntArray unmodifiableArray(IntArray array) {
    if (array instanceof UnmodifiableIntArray) {
      return array;
    }
    return new UnmodifiableIntArray(array);
  }

  public static LongArray unmodifiableArray(LongArray array) {
    return new UnmodifiableLongArray(array);
  }

  public static ComplexArray unmodifiableArray(ComplexArray array) {
    return new UnmodifiableComplexArray(array);
  }

  public static BooleanArray unmodifiableArray(BooleanArray array) {
    return new UnmodifiableBooleanArray(array);
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

  public static DoubleArray zeros(int... shape) {
    return ARRAY_FACTORY.newDoubleArray(shape);
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
   * @see org.briljantframework.array.api.ArrayFactory#newDoubleArray(int...)
   */
  public static DoubleArray doubleArray(int... shape) {
    return ARRAY_FACTORY.newDoubleArray(shape);
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
   * @see org.briljantframework.array.api.ArrayFactory#range(int)
   */
  public static Range range(int end) {
    return ARRAY_FACTORY.range(end);
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
   * @see ArrayFactory#rand(int)
   */
  public static DoubleArray rand(int size) {
    return ARRAY_FACTORY.rand(size);
  }

  /**
   * @see ArrayFactory#randn(int)
   */
  public static DoubleArray randn(int size) {
    return ARRAY_FACTORY.randn(size);
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
   * Returns a 1d-array with values sampled from the specified distribution.
   * 
   * @param size the size
   * @param distribution the distribution
   * @return
   */
  public static IntArray rand(int size, IntegerDistribution distribution) {
    IntArray array = intArray(size);
    array.assign(distribution::sample);
    return array;
  }

  /**
   * Creates a 1d-array with values sampled in the given range
   *
   * @param size the size of the array
   * @param lower the lower bound
   * @param upper the upper bound
   * @return a new array
   */
  @Deprecated
  public static IntArray randi(int size, int lower, int upper) {
    return rand(size, new UniformIntegerDistribution(lower, upper));
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#diag(org.briljantframework.array.BaseArray)
   */
  public static <T, S extends BaseArray<S>> S diag(S data) {
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
    return sum(x.intArray());
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sum(org.briljantframework.array.IntArray)
   */
  public static int sum(IntArray x) {
    return ARRAY_ROUTINES.sum(x);
  }

  public static IntArray sum(int dim, BooleanArray x) {
    return sum(dim, x.intArray());
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

  public static DoubleArray norm2(int dim, DoubleArray a) {
    return ARRAY_ROUTINES.norm2(dim, a);
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

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#trace(org.briljantframework.array.DoubleArray)
   */
  public static double trace(DoubleArray x) {
    return ARRAY_ROUTINES.trace(x);
  }

  /**
   * Returns a coordinate array from coordinate vectors (arrays with d > 1 are raveled)
   *
   * @param first the first array
   * @param rest rest of the arrays
   * @param <S> the class of arrays
   * @return a list of coordinate arrays (with shape
   *         {@code [first.size(), rest[0].size(), ..., rest[rest.length - 1].size()]}
   */
  @SafeVarargs
  public static <E, S extends BaseArray<S>> List<S> meshgrid(S first, S... rest) {
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
   * Split an array into multiple sub-arrays vertically (row-wise).
   *
   * <p/>
   * This is equivalent to {@link #split(BaseArray, int, int)} with dim=0 (default). The array is
   * always split along the first axis regardless of the array dimension.
   *
   * @param array the array
   * @param parts the number of sub-arrays
   * @param <T> the array type
   * @return a list of array parts
   * @see #split(BaseArray, int)
   */
  public static <E, T extends BaseArray<T>> List<T> vsplit(T array, int parts) {
    if (array.dims() == 1) {
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
  public static <E, T extends BaseArray<T>> List<T> split(T array, int parts, int dim) {
    Check.argument(array.size(dim) % parts == 0);
    return new SplitArrayList<>(array, dim, parts);
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
    if (array.dims() == 1) {
      array = array.reshape(1, array.size());
    }
    return split(array, parts, 1);
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

    // tries to resolve the case for lazily splitted arrays
    arrays = arrays instanceof SplitArrayList ? new ArrayList<>(arrays) : arrays;
    for (T array : arrays) {
      Check.argument(prototype.dims() == array.dims(),
          "all arrays must have the same number of dimensions");
      for (int i = 0; i < prototype.dims(); i++) {
        if (i != dim) {
          Check.argument(array.size(i) == prototype.size(i),
              "all input array dimensions expect for the concatenation dim must match");
        }
      }
      shape[dim] += array.size(dim);
    }

    T empty = prototype.newEmptyArray(shape);
    int i = 0;
    if (empty.dims() == 1) {
      for (T array : arrays) {
        for (int j = 0; j < array.size(); j++) {
          empty.setFrom(i++, array, j);
        }
      }
    } else {
      for (T array : arrays) {
        for (int j = 0; j < array.size(dim); j++) {
          empty.select(dim, i++).assign(array.select(dim, j));
        }
      }
    }

    return empty;
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
    List<T> arrayList = arrays instanceof SplitArrayList ? new ArrayList<>(arrays) : arrays;
    return concatenate(new AbstractList<T>() {
      @Override
      public T get(int index) {
        T v = arrayList.get(index);
        return v.dims() == 1 ? v.reshape(1, v.size()) : v;
      }

      @Override
      public int size() {
        return arrayList.size();
      }
    }, 0);
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
    List<T> arrayList = arrays instanceof SplitArrayList ? new ArrayList<>(arrays) : arrays;
    return concatenate(new AbstractList<T>() {
      @Override
      public T get(int index) {
        T v = arrayList.get(index);
        return v.dims() == 1 ? v.reshape(1, v.size()) : v;
      }

      @Override
      public int size() {
        return arrayList.size();
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

  /**
   * Take the specified number of elements from the beginning of the array.
   * 
   * @param x the array
   * @param num the number of elements to take
   * @param <T> the array type
   * @return a new array
   */
  public static <T extends BaseArray<T>> T take(T x, int num) {
    Check.argument(num > 0 && num <= x.size(), "to few/many elements to take");
    T c = x.newEmptyArray(num);
    for (int i = 0; i < num; i++) {
      c.setFrom(i, x, i);
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
        array.setFrom(pad + j, x, i);
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
   * DoubleArray b = Arrays.newDoubleMatrix(new double[][] {{1, 2}, {3, 4}});
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
  private static int[] prependDimension(int[] shape, int max) {
    int[] newArr = new int[max];
    int diff = Math.abs(max - shape.length);
    for (int i = 0; i < max; i++) {
      if (i < diff) {
        newArr[i] = 1;
      } else {
        newArr[i] = shape[i - diff];
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
          array.setFrom(pad + i, x, i);
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
   * @see #broadcastAll(List)
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <E extends BaseArray<E>> List<E> broadcastAll(E... arrays) {
    return broadcastAll(java.util.Arrays.asList(arrays));
  }

  /**
   * Broadcast the given arrays against each other.
   *
   * @param arrays the arrays to broadcast
   * @param <E> the array type
   * @return a list of broadcasted array views
   */
  public static <E extends BaseArray<E>> List<E> broadcastAll(List<? extends E> arrays) {
    Check.argument(!arrays.isEmpty(), "no arrays given");
    if (arrays.size() == 1) {
      return new ArrayList<>(arrays);
    }
    int[] shape = ShapeUtils.findCombinedBroadcastShape(arrays);
    return new BroadcastArrayList<>(arrays, shape);
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
  public static <E extends BaseArray<? extends E>> E broadcastTo(E x, int... newShape) {
    Check.argument(newShape.length > 0 && x.dims() <= newShape.length, "to few new dimensions");
    int[] oldShape = x.getShape();
    Check.argument(ShapeUtils.isBroadcastCompatible(oldShape, newShape),
        "Can't broadcast array with shape %s to %s", java.util.Arrays.toString(oldShape),
        java.util.Arrays.toString(newShape));

    int[] oldStrides = x.getStride();
    if (java.util.Arrays.equals(oldShape, newShape)) {
      return x.asView(oldShape, oldStrides);
    } else {
      newShape = ShapeUtils.findBroadcastShape(oldShape, newShape);
      int[] newStrides = StrideUtils.broadcastStrides(oldStrides, oldShape, newShape);
      return x.asView(newShape, newStrides);
    }
  }

  /**
   * Broadcasts (if possible) the two arrays to the same size and apply the specified function.
   *
   * @param <T> the type of the first array
   * @param <U> the type of the second array
   * @param <R> the return type
   * @param a the first array
   * @param b the second array
   * @param function the function
   * @return the function applied to the arrays
   */
  public static <T extends BaseArray<? extends T>, U extends BaseArray<? extends U>, R> R broadcastCombine(
      T a, U b, BiFunction<? super T, ? super U, ? extends R> function) {
    int[] combinedShape = ShapeUtils.findCombinedBroadcastShape(java.util.Arrays.asList(a, b));
    return function.apply(Arrays.broadcastTo(a, combinedShape),
        Arrays.broadcastTo(b, combinedShape));
  }

  /**
   * Broadcast the second array argument to the shape of the first, applying the specified consumer.
   * 
   * @param <T> the type of the first array
   * @param <U> the type of the second array
   * @param a the first array
   * @param b the second array broadcasted to the shape of the first
   * @param consumer the consumer
   */
  public static <T extends BaseArray<? extends T>, U extends BaseArray<? extends U>> void broadcastWith(
      T a, U b, BiConsumer<? super T, ? super U> consumer) {
    consumer.accept(a, ShapeUtils.broadcastToShapeOf(b, a));
  }

  public static <E extends BaseArray<? extends E>> Broadcast<E> broadcast(E array) {
    return new Broadcast<>(array);
  }

  /**
   * Interchange two dimensions of an array.
   *
   * @param array the array
   * @param a the first dimension
   * @param b the second dimension
   * @param <E> the array type
   * @return a new array
   */
  public static <E extends BaseArray<? extends E>> E swapDimension(E array, int a, int b) {
    int[] permute = new int[array.dims()];
    for (int i = 0; i < permute.length; i++) {
      permute[i] = i;
    }
    permute[a] = b;
    permute[b] = a;
    return transpose(array, permute);
  }

  /**
   * Transpose the given array while permuting the dimensions.
   *
   * @param array the array
   * @param permute the new indices for the dimensions
   * @param <E> the array type
   * @return a view
   */
  public static <E extends BaseArray<? extends E>> E transpose(E array, int[] permute) {
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

  public static <E extends BaseArray<? extends E>> E transpose(E array) {
    int[] permute = new int[array.dims()];
    for (int i = 0; i < permute.length; i++) {
      permute[i] = permute.length - 1 - i;
    }
    return transpose(array, permute);
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
    Array<T> out = array.copy();
    out.sort(Comparable::compareTo);
    return out;
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
   * Sort each series along the specified dimension in its natural order.
   *
   * @param array the array
   * @param <T> the element type
   * @return a new array
   */
  public static <T extends Comparable<T>> Array<T> sort(int dim, Array<T> array) {
    return sort(dim, array, (a, i, j) -> a.get(i).compareTo(a.get(j)));
  }

  /**
   * Sort each series along the specified dimension according to the given index comparator.
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
   * Sort each series along the specified dimension according to the given comparator.
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
   * @see Collections#binarySearch(List, Object)
   */
  public static <T> int binarySearch(Array<? extends Comparable<? super T>> array, T x) {
    return Collections.binarySearch(new ArrayListAdapter<>(array), x);
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
   * @see #binarySearch(Array, Object)
   */
  public static int binarySearch(DoubleArray array, double x) {
    return binarySearch(array.asArray(), x);
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
    int i = Collections.binarySearch(new ArrayListAdapter<>(array), value);
    if (i < 0) {
      return -i - 1;
    } else {
      return i;
    }
  }

  /**
   * @see #bisectLeft(Array, Object)
   */
  public static int bisectLeft(IntArray array, int value) {
    return bisectLeft(array.boxed(), value);
  }

  /**
   * @see #bisectLeft(Array, Object)
   */
  public static int bisectLeft(DoubleArray array, double value) {
    return bisectLeft(array.asArray(), value);
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
    int i = Collections.binarySearch(new ArrayListAdapter<>(array), value);
    if (i < 0) {
      return -i - 1;
    } else {
      return i + 1;
    }
  }

  /**
   * @see #bisectRight(Array, Object)
   */
  public static int bisectRight(IntArray array, int value) {
    return bisectRight(array.boxed(), value);
  }

  /**
   * @see #bisectRight(Array, Object)
   */
  public static int bisectRight(DoubleArray array, double value) {
    return bisectRight(array.asArray(), value);
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

  public static DoubleArray div(DoubleArray nominator, DoubleArray denominator) {
    return ARRAY_ROUTINES.div(nominator, denominator);
  }

  public static void minusAssign(DoubleArray a, DoubleArray out) {
    ARRAY_ROUTINES.minusAssign(a, out);
  }

  public static DoubleArray minus(DoubleArray a, DoubleArray b) {
    return ARRAY_ROUTINES.minus(a, b);
  }

  public static void plusAssign(DoubleArray a, DoubleArray out) {
    ARRAY_ROUTINES.plusAssign(a, out);
  }

  public static void timesAssign(DoubleArray a, DoubleArray out) {
    ARRAY_ROUTINES.timesAssign(a, out);
  }

  public static DoubleArray plus(DoubleArray a, DoubleArray b) {
    return ARRAY_ROUTINES.plus(a, b);
  }

  public static void divAssign(DoubleArray nominator, DoubleArray denominatorOut) {
    ARRAY_ROUTINES.divAssign(nominator, denominatorOut);
  }

  public static DoubleArray times(DoubleArray a, DoubleArray b) {
    return ARRAY_ROUTINES.times(a, b);
  }

  public static DoubleArray times(DoubleArray a, double b) {
    return times(a, doubleVector(b));
  }

  public static IntArray plus(IntArray a, IntArray b) {
    return ARRAY_ROUTINES.plus(a, b);
  }

  public static ComplexArray minus(ComplexArray a, ComplexArray b) {
    return ARRAY_ROUTINES.minus(a, b);
  }

  public static IntArray minus(IntArray a, IntArray b) {
    return ARRAY_ROUTINES.minus(a, b);
  }

  public static void timesAssign(ComplexArray a, ComplexArray out) {
    ARRAY_ROUTINES.timesAssign(a, out);
  }

  public static void plusAssign(IntArray a, IntArray out) {
    ARRAY_ROUTINES.plusAssign(a, out);
  }

  public static void plusAssign(LongArray a, LongArray out) {
    ARRAY_ROUTINES.plusAssign(a, out);
  }

  public static void timesAssign(IntArray a, IntArray out) {
    ARRAY_ROUTINES.timesAssign(a, out);
  }

  public static void divAssign(ComplexArray a, ComplexArray out) {
    ARRAY_ROUTINES.divAssign(a, out);
  }

  public static ComplexArray times(ComplexArray a, ComplexArray b) {
    return ARRAY_ROUTINES.times(a, b);
  }

  public static LongArray plus(LongArray a, LongArray b) {
    return ARRAY_ROUTINES.plus(a, b);
  }

  public static void timesAssign(LongArray a, LongArray out) {
    ARRAY_ROUTINES.timesAssign(a, out);
  }

  public static void minusAssign(IntArray a, IntArray out) {
    ARRAY_ROUTINES.minusAssign(a, out);
  }

  public static BooleanArray xor(BooleanArray a, BooleanArray b) {
    return ARRAY_ROUTINES.xor(a, b);
  }

  public static void minusAssign(LongArray a, LongArray out) {
    ARRAY_ROUTINES.minusAssign(a, out);
  }

  public static BooleanArray and(BooleanArray a, BooleanArray b) {
    return ARRAY_ROUTINES.and(a, b);
  }

  public static IntArray div(IntArray a, IntArray b) {
    return ARRAY_ROUTINES.div(a, b);
  }

  public static void divAssign(LongArray a, LongArray out) {
    ARRAY_ROUTINES.divAssign(a, out);
  }

  public static IntArray times(IntArray a, IntArray b) {
    return ARRAY_ROUTINES.times(a, b);
  }

  public static void plusAssign(ComplexArray a, ComplexArray out) {
    ARRAY_ROUTINES.plusAssign(a, out);
  }

  public static LongArray minus(LongArray a, LongArray b) {
    return ARRAY_ROUTINES.minus(a, b);
  }

  public static BooleanArray or(BooleanArray a, BooleanArray b) {
    return ARRAY_ROUTINES.or(a, b);
  }

  public static void minusAssign(ComplexArray a, ComplexArray out) {
    ARRAY_ROUTINES.minusAssign(a, out);
  }

  public static void divAssign(IntArray a, IntArray out) {
    ARRAY_ROUTINES.divAssign(a, out);
  }

  public static LongArray div(LongArray a, LongArray b) {
    return ARRAY_ROUTINES.div(a, b);
  }

  public static ComplexArray div(ComplexArray a, ComplexArray b) {
    return ARRAY_ROUTINES.div(a, b);
  }

  public static LongArray times(LongArray a, LongArray b) {
    return ARRAY_ROUTINES.times(a, b);
  }

  public static ComplexArray plus(ComplexArray a, ComplexArray b) {
    return ARRAY_ROUTINES.plus(a, b);
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

    // TODO(isak): select implementation based on the input
    Check.argument(a.isMatrix() && b.isMatrix(), "require 2d-arrays");
    int m = a.size(transA == ArrayOperation.KEEP ? 0 : 1);
    int bm = b.size(transB == ArrayOperation.KEEP ? 0 : 1);
    int n = b.size(transB == ArrayOperation.KEEP ? 1 : 0);
    int k = a.size(transA == ArrayOperation.KEEP ? 1 : 0);
    if (m == 0 || k == 0 || n == 0 || bm == 0) {
      throw new IllegalArgumentException("empty result");
    }
    if (b.size(transB == ArrayOperation.KEEP ? 0 : 1) != a
        .size(transA == ArrayOperation.KEEP ? 1 : 0)) {
      throw new MultiDimensionMismatchException(a, b);
    }
    DoubleArray c = doubleArray(m, n);
    gemm(transA, transB, alpha, a, b, 1, c);
    return c;
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

  /**
   * @see ArrayRoutines#gemm(ArrayOperation, ArrayOperation, double, DoubleArray, DoubleArray,
   *      double, DoubleArray)
   */
  public static void gemm(ArrayOperation transA, ArrayOperation transB, double alpha, DoubleArray a,
      DoubleArray b, double beta, DoubleArray c) {
    ARRAY_ROUTINES.gemm(transA, transB, alpha, a, b, beta, c);
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
      taken.setFrom(i, array, indexes.get(i));
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
        a.setFrom(i, values, i);
      }
    }
  }

  public static int arg(Predicate<Boolean> predicate, BooleanArray array) {
    for (int i = 0; i < array.size(); i++) {
      if (predicate.test(array.get(i))) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Test if any element in the array fulfill the predicate.
   * 
   * @param array the array
   * @param predicate the predicate
   * @param <T> the element type
   * @return true if any element fulfilling the predicate exist
   */
  public static <T> boolean any(Array<T> array, Predicate<? super T> predicate) {
    for (int i = 0; i < array.size(); i++) {
      if (predicate.test(array.get(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * @see #any(Array, Predicate)
   */
  public static boolean any(DoubleArray array, DoublePredicate predicate) {
    for (int i = 0; i < array.size(); i++) {
      if (predicate.test(array.get(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * @see #any(Array, Predicate)
   */
  public static boolean any(IntArray array, IntPredicate predicate) {
    // change if shown to be a bottleneck
    return any(array.boxed(), predicate::test);
  }

  /**
   * @see #any(Array, Predicate)
   */
  public static boolean any(LongArray array, LongPredicate predicate) {
    return any(array.asArray(), predicate::test);
  }

  /**
   * @see #any(Array, Predicate)
   */
  public static boolean any(ComplexArray array, Predicate<? super Complex> predicate) {
    return any(array.asArray(), predicate);
  }

  /**
   * @see #any(Array, Predicate)
   */
  public static boolean any(BooleanArray array) {
    return any(array.boxed(), (v) -> v);
  }

  /**
   * Remove single-dimensional entries from the array.
   *
   * @param array the array
   * @param <E> the array type
   * @return the input array with the dimensions of size 1 removed.
   */
  public static <E extends BaseArray<E>> E squeeze(E array) {
    int ones = 0;
    for (int i = 0; i < array.dims(); i++) {
      if (array.size(i) == 1) {
        ones++;
      }
    }

    if (ones == 0) {
      return array.asView(array.getShape(), array.getStride());
    } else {
      int[] newShape = new int[ones];
      int[] newStride = new int[ones];
      int outIndex = 0;
      for (int i = 0; i < array.dims(); i++) {
        if (array.size(i) != 1) {
          newShape[outIndex] = array.size(i);
          newStride[outIndex] = array.stride(outIndex);
          outIndex++;
        }
      }
      return array.asView(newShape, newStride);
    }
  }

  /**
   * Returns elements from either of the input arrays depending on the condition
   * 
   * @param condition the condition array
   * @param x the first input
   * @param y the second input
   * @param <E> the input type
   * @return a new array
   */
  public static <E extends BaseArray<E>> E where(BooleanArray condition, E x, E y) {
    int[] shape = condition.getShape();
    x = broadcastTo(x, shape); // performs error checking
    y = broadcastTo(y, shape);
    int size = x.size();
    E selected = x.newEmptyArray(shape);
    for (int i = 0; i < size; i++) {
      selected.setFrom(i, condition.get(i) ? x : y, i);
    }
    return selected;
  }

  public static <E extends BaseArray<E>> void set(E in, BooleanArray mask, E from) {
    Check.argument(from.dims() == 1, "value-array must be 1d");
    Check.dimension(in, mask);
    int sum = sum(mask);
    if (sum != from.size()) {
      from = broadcastTo(from, sum);
    }
    int fromIndex = 0;
    for (int i = 0; i < in.size(); i++) {
      if (mask.get(i)) {
        in.setFrom(i, from, fromIndex++);
      }
    }
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

  /**
   * Splits an array into a specified number of parts along the specified dimension.
   */
  static class SplitArrayList<T extends BaseArray<T>> extends AbstractList<T> {
    private final T array;
    private final int[] shape;
    private final int dim;
    private final int parts;

    /**
     * Note that this constructor, nor any methods perform any error checking. Given arguments are
     * assumed to be correct, i.e., the specified dimension exists and is evenly divisible by the
     * number of parts.
     */
    SplitArrayList(T array, int dim, int parts) {
      this.array = array;
      this.dim = dim;
      this.parts = parts;

      this.shape = array.getShape();
      shape[dim] /= parts;
    }

    @Override
    public T get(int index) {
      T empty = array.newEmptyArray(shape);
      int size = empty.size(dim);
      int rowPadding = index * size;
      if (empty.dims() == 1) {
        for (int i = 0; i < size; i++) {
          empty.setFrom(i, array, rowPadding + i);
        }
      } else {
        for (int i = 0; i < size; i++) {
          empty.select(dim, i).assign(array.select(dim, rowPadding + i));
        }
      }
      return empty;
    }

    @Override
    public int size() {
      return parts;
    }
  }

  /**
   * Broadcast each array to the given shape. Note that neither the constructor nor methods perform
   * any error checking, i.e., the arguments are assumed to be correct
   */
  static class BroadcastArrayList<E extends BaseArray<E>> extends AbstractList<E> {
    private final List<? extends E> arrays;
    private final int[] shape;

    BroadcastArrayList(List<? extends E> arrays, int[] shape) {
      this.arrays = arrays;
      this.shape = shape;
    }

    @Override
    public E get(int index) {
      E x = arrays.get(index);
      return x.asView(shape, StrideUtils.broadcastStrides(x.getStride(), x.getShape(), shape));
    }

    @Override
    public int size() {
      return arrays.size();
    }
  }


  private static class UnmodifiableArray<T> implements Array<T> {

    private final Array<T> array;

    @Override
    public String toString() {
      return array.toString();
    }

    @Override
    public boolean equals(Object obj) {
      return array.equals(obj);
    }

    @Override
    public int hashCode() {
      return array.hashCode();
    }

    @Override
    public void assign(T value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(Supplier<T> supplier) {
      throw new UnsupportedOperationException();
    }

    @Override
    public <U> void assign(Array<U> other, Function<? super U, ? extends T> operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public DoubleArray mapToDouble(ToDoubleFunction<? super T> f) {
      return array.mapToDouble(f);
    }

    @Override
    public LongArray mapToLong(ToLongFunction<? super T> f) {
      return array.mapToLong(f);
    }

    @Override
    public IntArray mapToInt(ToIntFunction<? super T> f) {
      return array.mapToInt(f);
    }

    @Override
    public ComplexArray mapToComplex(Function<? super T, Complex> f) {
      return array.mapToComplex(f);
    }

    @Override
    public BooleanArray mapToBoolean(Function<? super T, Boolean> f) {
      return array.mapToBoolean(f);
    }

    @Override
    public <U> Array<U> map(Function<? super T, ? extends U> f) {
      return array.map(f);
    }

    @Override
    public void apply(UnaryOperator<T> operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Array<T> filter(Predicate<? super T> predicate) {
      return array.filter(predicate);
    }

    @Override
    public BooleanArray where(Predicate<? super T> predicate) {
      return array.where(predicate);
    }

    @Override
    public BooleanArray where(Array<? extends T> other,
        BiPredicate<? super T, ? super T> predicate) {
      return array.where(other, predicate);
    }

    @Override
    public T reduce(T initial, BinaryOperator<T> accumulator) {
      return array.reduce(initial, accumulator);
    }

    @Override
    public Array<T> reduceVector(int dim, Function<? super Array<T>, ? extends T> accumulator) {
      return array.reduceVector(dim, accumulator);
    }

    @Override
    public T get(int i) {
      return array.get(i);
    }

    @Override
    public void set(int i, T value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public T get(int i, int j) {
      return array.get(i, j);
    }

    @Override
    public void set(int i, int j, T value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public T get(int... index) {
      return array.get(index);
    }

    @Override
    public void set(int[] index, T value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void set(BooleanArray array, T value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Array<T> get(BooleanArray array) {
      return this.array.get(array);
    }

    @Override
    public Stream<T> stream() {
      return array.stream();
    }

    @Override
    public void sort(Comparator<? super T> comparator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int toIndex, Array<T> from, int fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int toRow, int toColumn, Array<T> from, int fromRow, int fromColumn) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int[] toIndex, Array<T> from, int[] fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int[] toIndex, Array<T> from, int fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int toIndex, Array<T> from, int[] fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Array<T> reverse() {
      return array.reverse();
    }

    @Override
    public void assign(Array<T> o) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void forEach(int dim, Consumer<Array<T>> consumer) {
      array.forEach(dim, consumer);
    }

    @Override
    public void setColumn(int i, Array<T> vec) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Array<T> getColumn(int index) {
      return unmodifiableArray(array.getColumn(index));
    }

    @Override
    public void setRow(int i, Array<T> vec) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Array<T> getRow(int i) {
      return unmodifiableArray(array.getRow(i));
    }

    @Override
    public Array<T> reshape(int... shape) {
      return unmodifiableArray(array.reshape(shape));
    }

    @Override
    public Array<T> ravel() {
      return unmodifiableArray(array.ravel());
    }

    @Override
    public Array<T> select(int index) {
      return unmodifiableArray(array.select(index));
    }

    @Override
    public Array<T> select(int dimension, int index) {
      return unmodifiableArray(array.select(dimension, index));
    }

    @Override
    public Array<T> getView(Range... indexers) {
      return unmodifiableArray(array.getView(indexers));
    }

    @Override
    public Array<T> getView(List<? extends Range> ranges) {
      return unmodifiableArray(array.getView(ranges));
    }

    @Override
    public Array<T> getVector(int dimension, int index) {
      return unmodifiableArray(array.getVector(dimension, index));
    }

    @Override
    public List<Array<T>> getVectors(int dimension) {
      List<Array<T>> vectors = array.getVectors(dimension);
      return new AbstractList<Array<T>>() {
        @Override
        public Array<T> get(int index) {
          return unmodifiableArray(vectors.get(index));
        }

        @Override
        public int size() {
          return vectors.size();
        }
      };
    }

    @Override
    public void setVector(int dimension, int index, Array<T> other) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Array<T> getDiagonal() {
      return unmodifiableArray(array.getDiagonal());
    }

    @Override
    public Array<T> get(IntArray... arrays) {
      return unmodifiableArray(array.get(arrays));
    }

    @Override
    public Array<T> get(List<? extends IntArray> arrays) {
      return unmodifiableArray(array.get(arrays));
    }

    @Override
    public void set(IntArray[] indexers, Array<T> slice) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void set(List<? extends IntArray> arrays, Array<T> value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Array<T> getView(int rowOffset, int colOffset, int rows, int columns) {
      return unmodifiableArray(array.getView(rowOffset, colOffset, rows, columns));
    }

    @Override
    public int size() {
      return array.size();
    }

    @Override
    public int size(int dim) {
      return array.size(dim);
    }

    @Override
    public int vectors(int i) {
      return array.vectors(i);
    }

    @Override
    public int stride(int i) {
      return array.stride(i);
    }

    @Override
    public int getOffset() {
      return array.getOffset();
    }

    @Override
    public int[] getShape() {
      return array.getShape();
    }

    @Override
    public int[] getStride() {
      return array.getStride();
    }

    @Override
    public boolean isSquare() {
      return array.isSquare();
    }

    @Override
    public int rows() {
      return array.rows();
    }

    @Override
    public int columns() {
      return array.columns();
    }

    @Override
    public int dims() {
      return array.dims();
    }

    @Override
    public boolean isVector() {
      return array.isVector();
    }

    @Override
    public boolean isMatrix() {
      return array.isMatrix();
    }

    @Override
    public Array<T> asView(int[] shape, int[] stride) {
      return unmodifiableArray(array.asView(shape, stride));
    }

    @Override
    public Array<T> asView(int offset, int[] shape, int[] stride) {
      return unmodifiableArray(array.asView(offset, shape, stride));
    }

    @Override
    public Array<T> newEmptyArray(int... shape) {
      return array.newEmptyArray(shape);
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public boolean isContiguous() {
      return array.isContiguous();
    }

    @Override
    public Array<T> transpose() {
      return unmodifiableArray(array.transpose());
    }

    @Override
    public Array<T> copy() {
      return array.copy();
    }

    @Override
    public void swap(int a, int b) {
      array.swap(a, b);
    }

    @Override
    public Iterator<T> iterator() {
      return array.iterator();
    }

    @Override
    public void forEach(Consumer<? super T> action) {
      array.forEach(action);
    }

    @Override
    public Spliterator<T> spliterator() {
      return array.spliterator();
    }

    @Override
    public boolean isEmpty() {
      return size() == 0;
    }

    @Override
    public boolean contains(Object o) {
      return array.contains(o);
    }

    @Override
    public Object[] toArray() {
      return array.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
      return array.toArray(a);
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public final boolean add(T integer) {
      throw new UnsupportedOperationException();
    }

    @Override
    public final boolean remove(Object o) {
      throw new UnsupportedOperationException();
    }

    @Override
    public final boolean containsAll(Collection<?> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public final boolean addAll(Collection<? extends T> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public final boolean removeAll(Collection<?> c) {
      throw new UnsupportedOperationException();
    }

    UnmodifiableArray(Array<T> array) {
      this.array = array;
    }
  }


  private static final class UnmodifiableDoubleArray extends AbstractCollection<Double>
      implements DoubleArray {
    private final DoubleArray array;

    @Override
    public String toString() {
      return array.toString();
    }

    @Override
    public boolean equals(Object obj) {
      return array.equals(obj);
    }

    @Override
    public int hashCode() {
      return array.hashCode();
    }

    @Override
    public void set(int index, double value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(double value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(double[] array) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(DoubleSupplier supplier) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(DoubleArray array, DoubleUnaryOperator operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(IntArray array, IntToDoubleFunction function) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(LongArray array, LongToDoubleFunction function) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(ComplexArray array, ToDoubleFunction<? super Complex> function) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void combineAssign(DoubleArray array, DoubleBinaryOperator combine) {
      throw new UnsupportedOperationException();
    }

    @Override
    public DoubleArray combine(DoubleArray array, DoubleBinaryOperator combine) {
      return this.array.combine(array, combine);
    }

    @Override
    public <R, C> R collect(Collector<? super Double, C, R> collector) {
      return array.collect(collector);
    }

    @Override
    public <R> R collect(Supplier<R> supplier, ObjDoubleConsumer<R> consumer) {
      return array.collect(supplier, consumer);
    }

    @Override
    public DoubleArray map(DoubleUnaryOperator operator) {
      return array.map(operator);
    }

    @Override
    public IntArray mapToInt(DoubleToIntFunction function) {
      return array.mapToInt(function);
    }

    @Override
    public LongArray mapToLong(DoubleToLongFunction function) {
      return array.mapToLong(function);
    }

    @Override
    public ComplexArray mapToComplex(DoubleFunction<Complex> function) {
      return array.mapToComplex(function);
    }

    @Override
    public <T> Array<T> mapToObj(DoubleFunction<? extends T> mapper) {
      return array.mapToObj(mapper);
    }

    @Override
    public void apply(DoubleUnaryOperator operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public DoubleArray filter(DoublePredicate predicate) {
      return array.filter(predicate);
    }

    @Override
    public BooleanArray where(DoubleArray array, DoubleBiPredicate predicate) {
      return this.array.where(array, predicate);
    }

    @Override
    public void forEachDouble(DoubleConsumer consumer) {
      array.forEachDouble(consumer);
    }

    @Override
    public double reduce(double identity, DoubleBinaryOperator reduce) {
      return array.reduce(identity, reduce);
    }

    @Override
    public DoubleArray reduceVectors(int dim, ToDoubleFunction<? super DoubleArray> reduce) {
      return array.reduceVectors(dim, reduce);
    }

    @Override
    public void set(int i, int j, double value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void set(int[] ix, double value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public double get(int index) {
      return array.get(index);
    }

    @Override
    public double get(int i, int j) {
      return array.get(i, j);
    }

    @Override
    public double get(int... ix) {
      return array.get(ix);
    }

    @Override
    public void set(BooleanArray array, double value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public DoubleArray get(BooleanArray array) {
      return this.array.get(array);
    }

    @Override
    public void sort() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void sort(DoubleComparator comparator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public DoubleStream doubleStream() {
      return array.doubleStream();
    }

    @Override
    public List<Double> asList() {
      return Collections.unmodifiableList(array.asList());
    }

    @Override
    public Array<Double> asArray() {
      return unmodifiableArray(array.asArray());
    }

    @Override
    public DoubleArray negate() {
      return array.negate();
    }

    @Override
    public BooleanArray where(DoublePredicate predicate) {
      return array.where(predicate);
    }

    @Override
    public double getDouble(int index) {
      return array.getDouble(index);
    }

    @Override
    public void setFrom(int toIndex, DoubleArray from, int fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int toRow, int toColumn, DoubleArray from, int fromRow, int fromColumn) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int[] toIndex, DoubleArray from, int[] fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int[] toIndex, DoubleArray from, int fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int toIndex, DoubleArray from, int[] fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public DoubleArray reverse() {
      return unmodifiableArray(array.reverse());
    }

    @Override
    public void assign(DoubleArray o) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void forEach(int dim, Consumer<DoubleArray> consumer) {
      array.forEach(dim, consumer);
    }

    @Override
    public void setColumn(int i, DoubleArray vec) {
      throw new UnsupportedOperationException();
    }

    @Override
    public DoubleArray getColumn(int index) {
      return unmodifiableArray(array.getColumn(index));
    }

    @Override
    public void setRow(int i, DoubleArray vec) {
      throw new UnsupportedOperationException();
    }

    @Override
    public DoubleArray getRow(int i) {
      return unmodifiableArray(array.getRow(i));
    }

    @Override
    public DoubleArray reshape(int... shape) {
      return unmodifiableArray(array.reshape(shape));
    }

    @Override
    public DoubleArray ravel() {
      return unmodifiableArray(array.ravel());
    }

    @Override
    public DoubleArray select(int index) {
      return unmodifiableArray(array.select(index));
    }

    @Override
    public DoubleArray select(int dimension, int index) {
      return unmodifiableArray(array.select(dimension, index));
    }

    @Override
    public DoubleArray getView(Range... indexers) {
      return unmodifiableArray(array.getView(indexers));
    }

    @Override
    public DoubleArray getView(List<? extends Range> ranges) {
      return unmodifiableArray(array.getView(ranges));
    }

    @Override
    public DoubleArray getVector(int dimension, int index) {
      return unmodifiableArray(array.getVector(dimension, index));
    }

    @Override
    public List<DoubleArray> getVectors(int dimension) {
      List<DoubleArray> vectors = array.getVectors(dimension);
      return new AbstractList<DoubleArray>() {
        @Override
        public DoubleArray get(int index) {
          return unmodifiableArray(vectors.get(index));
        }

        @Override
        public int size() {
          return vectors.size();
        }
      };
    }

    @Override
    public void setVector(int dimension, int index, DoubleArray other) {
      throw new UnsupportedOperationException();
    }

    @Override
    public DoubleArray getDiagonal() {
      return unmodifiableArray(array.getDiagonal());
    }

    @Override
    public DoubleArray get(IntArray... arrays) {
      return unmodifiableArray(array.get(arrays));
    }

    @Override
    public DoubleArray get(List<? extends IntArray> arrays) {
      return unmodifiableArray(array.get(arrays));
    }

    @Override
    public void set(IntArray[] indexers, DoubleArray slice) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void set(List<? extends IntArray> arrays, DoubleArray value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public DoubleArray getView(int rowOffset, int colOffset, int rows, int columns) {
      return unmodifiableArray(array.getView(rowOffset, colOffset, rows, columns));
    }

    @Override
    public int size() {
      return array.size();
    }

    @Override
    public int size(int dim) {
      return array.size(dim);
    }

    @Override
    public int vectors(int i) {
      return array.vectors(i);
    }

    @Override
    public int stride(int i) {
      return array.stride(i);
    }

    @Override
    public int getOffset() {
      return array.getOffset();
    }

    @Override
    public int[] getShape() {
      return array.getShape();
    }

    @Override
    public int[] getStride() {
      return array.getStride();
    }

    @Override
    public boolean isSquare() {
      return array.isSquare();
    }

    @Override
    public int rows() {
      return array.rows();
    }

    @Override
    public int columns() {
      return array.columns();
    }

    @Override
    public int dims() {
      return array.dims();
    }

    @Override
    public boolean isVector() {
      return array.isVector();
    }

    @Override
    public boolean isMatrix() {
      return array.isMatrix();
    }

    @Override
    public DoubleArray asView(int[] shape, int[] stride) {
      return unmodifiableArray(array.asView(shape, stride));
    }

    @Override
    public DoubleArray asView(int offset, int[] shape, int[] stride) {
      return unmodifiableArray(array.asView(offset, shape, stride));
    }

    @Override
    public DoubleArray newEmptyArray(int... shape) {
      return array.newEmptyArray(shape);
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public DoubleArray doubleArray() {
      return this;
    }

    @Override
    public IntArray intArray() {
      return unmodifiableArray(array.intArray());
    }

    @Override
    public LongArray longArray() {
      return unmodifiableArray(array.longArray());
    }

    @Override
    public ComplexArray complexArray() {
      return unmodifiableArray(array.complexArray());
    }

    @Override
    public boolean isContiguous() {
      return array.isContiguous();
    }

    @Override
    public DoubleArray transpose() {
      return unmodifiableArray(array.transpose());
    }

    @Override
    public DoubleArray copy() {
      return array.copy();
    }

    @Override
    public void swap(int a, int b) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Double> iterator() {
      return asList().iterator();
    }

    @Override
    public void forEach(Consumer<? super Double> action) {
      array.forEach(action);
    }

    @Override
    public Spliterator<Double> spliterator() {
      return asList().spliterator();
    }

    UnmodifiableDoubleArray(DoubleArray array) {
      this.array = array;
    }
  }

  private static class UnmodifiableIntArray extends AbstractCollection<Integer>
      implements IntArray {
    private final IntArray array;

    UnmodifiableIntArray(IntArray array) {
      this.array = array;
    }

    @Override
    public String toString() {
      return array.toString();
    }

    @Override
    public boolean equals(Object obj) {
      return array.equals(obj);
    }

    @Override
    public int hashCode() {
      return array.hashCode();
    }

    @Override
    public void assign(int value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(int[] data) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(IntSupplier supplier) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(IntArray array, IntUnaryOperator operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void combineAssign(IntArray array, IntBinaryOperator combine) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(ComplexArray array, ToIntFunction<? super Complex> function) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(DoubleArray array, DoubleToIntFunction function) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(LongArray array, LongToIntFunction operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(BooleanArray array, ToIntFunction<Boolean> function) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void apply(IntUnaryOperator operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public IntArray map(IntUnaryOperator operator) {
      return array.map(operator);
    }

    @Override
    public LongArray mapToLong(IntToLongFunction function) {
      return array.mapToLong(function);
    }

    @Override
    public DoubleArray mapToDouble(IntToDoubleFunction function) {
      return array.mapToDouble(function);
    }

    @Override
    public ComplexArray mapToComplex(IntFunction<Complex> function) {
      return array.mapToComplex(function);
    }

    @Override
    public <U> Array<U> mapToObj(IntFunction<? extends U> function) {
      return array.mapToObj(function);
    }

    @Override
    public IntArray filter(IntPredicate operator) {
      return array.filter(operator);
    }

    @Override
    public BooleanArray where(IntPredicate predicate) {
      return array.where(predicate);
    }

    @Override
    public BooleanArray where(IntArray array, IntBiPredicate predicate) {
      return this.array.where(array, predicate);
    }

    @Override
    public void forEachPrimitive(IntConsumer consumer) {
      array.forEachPrimitive(consumer);
    }

    @Override
    public int reduce(int identity, IntBinaryOperator reduce) {
      return array.reduce(identity, reduce);
    }

    @Override
    public int reduce(int identity, IntBinaryOperator reduce, IntUnaryOperator map) {
      return array.reduce(identity, reduce, map);
    }

    @Override
    public IntArray reduceVectors(int dim, ToIntFunction<? super IntArray> accumulator) {
      return array.reduceVectors(dim, accumulator);
    }

    @Override
    public void set(int index, int value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int get(int row, int column) {
      return array.get(row, column);
    }

    @Override
    public void set(int row, int column, int value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void set(int[] index, int value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int get(int... index) {
      return array.get(index);
    }

    @Override
    public int get(int index) {
      return array.get(index);
    }

    @Override
    public IntStream intStream() {
      return array.intStream();
    }

    @Override
    public Array<Integer> boxed() {
      return unmodifiableArray(array.boxed());
    }

    @Override
    public void sort() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void sort(IntComparator cmp) {
      throw new UnsupportedOperationException();
    }

    @Override
    public IntArray negate() {
      return array.negate();
    }

    @Override
    public void setFrom(int toIndex, IntArray from, int fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int toRow, int toColumn, IntArray from, int fromRow, int fromColumn) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int[] toIndex, IntArray from, int[] fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int[] toIndex, IntArray from, int fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int toIndex, IntArray from, int[] fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public IntArray reverse() {
      return unmodifiableArray(array.reverse());
    }

    @Override
    public void assign(IntArray o) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void forEach(int dim, Consumer<IntArray> consumer) {
      array.forEach(dim, consumer);
    }

    @Override
    public void setColumn(int i, IntArray vec) {
      throw new UnsupportedOperationException();
    }

    @Override
    public IntArray getColumn(int index) {
      return unmodifiableArray(array.getColumn(index));
    }

    @Override
    public void setRow(int i, IntArray vec) {
      throw new UnsupportedOperationException();
    }

    @Override
    public IntArray getRow(int i) {
      return unmodifiableArray(array.getRow(i));
    }

    @Override
    public IntArray reshape(int... shape) {
      return unmodifiableArray(array.reshape(shape));
    }

    @Override
    public IntArray ravel() {
      return unmodifiableArray(array.ravel());
    }

    @Override
    public IntArray select(int index) {
      return unmodifiableArray(array.select(index));
    }

    @Override
    public IntArray select(int dimension, int index) {
      return unmodifiableArray(array.select(dimension, index));
    }

    @Override
    public IntArray getView(Range... indexers) {
      return unmodifiableArray(array.getView(indexers));
    }

    @Override
    public IntArray getView(List<? extends Range> ranges) {
      return unmodifiableArray(array.getView(ranges));
    }

    @Override
    public IntArray getVector(int dimension, int index) {
      return unmodifiableArray(array.getVector(dimension, index));
    }

    @Override
    public List<IntArray> getVectors(int dimension) {
      List<IntArray> vectors = array.getVectors(dimension);
      return new AbstractList<IntArray>() {
        @Override
        public IntArray get(int index) {
          return unmodifiableArray(vectors.get(index));
        }

        @Override
        public int size() {
          return vectors.size();
        }
      };
    }

    @Override
    public void setVector(int dimension, int index, IntArray other) {
      throw new UnsupportedOperationException();
    }

    @Override
    public IntArray getDiagonal() {
      return unmodifiableArray(array.getDiagonal());
    }

    @Override
    public IntArray get(IntArray... arrays) {
      return unmodifiableArray(array.get(arrays));
    }

    @Override
    public IntArray get(List<? extends IntArray> arrays) {
      return unmodifiableArray(array.get(arrays));
    }

    @Override
    public void set(IntArray[] indexers, IntArray slice) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void set(List<? extends IntArray> arrays, IntArray value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public IntArray getView(int rowOffset, int colOffset, int rows, int columns) {
      return unmodifiableArray(array.getView(rowOffset, colOffset, rows, columns));
    }

    @Override
    public int size() {
      return array.size();
    }

    @Override
    public int size(int dim) {
      return array.size(dim);
    }

    @Override
    public int vectors(int i) {
      return array.vectors(i);
    }

    @Override
    public int stride(int i) {
      return array.stride(i);
    }

    @Override
    public int getOffset() {
      return array.getOffset();
    }

    @Override
    public int[] getShape() {
      return array.getShape();
    }

    @Override
    public int[] getStride() {
      return array.getStride();
    }

    @Override
    public boolean isSquare() {
      return array.isSquare();
    }

    @Override
    public int rows() {
      return array.rows();
    }

    @Override
    public int columns() {
      return array.columns();
    }

    @Override
    public int dims() {
      return array.dims();
    }

    @Override
    public boolean isVector() {
      return array.isVector();
    }

    @Override
    public boolean isMatrix() {
      return array.isMatrix();
    }

    @Override
    public IntArray asView(int[] shape, int[] stride) {
      return unmodifiableArray(array.asView(shape, stride));
    }

    @Override
    public IntArray asView(int offset, int[] shape, int[] stride) {
      return unmodifiableArray(array.asView(offset, shape, stride));
    }

    @Override
    public IntArray newEmptyArray(int... shape) {
      return unmodifiableArray(array.newEmptyArray(shape));
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public DoubleArray doubleArray() {
      return unmodifiableArray(array.doubleArray());
    }

    @Override
    public IntArray intArray() {
      return unmodifiableArray(array.intArray());
    }

    @Override
    public LongArray longArray() {
      return unmodifiableArray(array.longArray());
    }

    @Override
    public ComplexArray complexArray() {
      return unmodifiableArray(array.complexArray());
    }

    @Override
    public boolean isContiguous() {
      return array.isContiguous();
    }

    @Override
    public IntArray transpose() {
      return unmodifiableArray(array.transpose());
    }

    @Override
    public IntArray copy() {
      return unmodifiableArray(array.copy());
    }

    @Override
    public void permute(int count) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void permute(int count, Random random) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void swap(int a, int b) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Integer> iterator() {
      return array.iterator();
    }

    @Override
    public void forEach(Consumer<? super Integer> action) {
      array.forEach(action);
    }

    @Override
    public Spliterator<Integer> spliterator() {
      return array.spliterator();
    }
  }


  private static class UnmodifiableLongArray implements LongArray {
    private final LongArray array;

    UnmodifiableLongArray(LongArray array) {
      this.array = array;
    }

    @Override
    public String toString() {
      return array.toString();
    }

    @Override
    public boolean equals(Object obj) {
      return array.equals(obj);
    }

    @Override
    public int hashCode() {
      return array.hashCode();
    }

    @Override
    public LongArray assign(long value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(long[] values) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(LongSupplier supplier) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(LongArray array, LongUnaryOperator operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void combineAssign(LongArray array, LongBinaryOperator combine) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(ComplexArray array, ToLongFunction<? super Complex> function) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(IntArray array, IntToLongFunction operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(DoubleArray array, DoubleToLongFunction function) {
      throw new UnsupportedOperationException();
    }

    @Override
    public LongArray map(LongUnaryOperator operator) {
      return array.map(operator);
    }

    @Override
    public IntArray mapToInt(LongToIntFunction map) {
      return array.mapToInt(map);
    }

    @Override
    public DoubleArray mapToDouble(LongToDoubleFunction map) {
      return array.mapToDouble(map);
    }

    @Override
    public ComplexArray mapToComplex(LongFunction<Complex> map) {
      return array.mapToComplex(map);
    }

    @Override
    public <T> Array<T> mapToObj(LongFunction<? extends T> mapper) {
      return array.mapToObj(mapper);
    }

    @Override
    public void apply(LongUnaryOperator operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public BooleanArray where(LongPredicate predicate) {
      return array.where(predicate);
    }

    @Override
    public BooleanArray where(LongArray array, LongBiPredicate predicate) {
      return this.array.where(array, predicate);
    }

    @Override
    public long reduce(long identity, LongBinaryOperator reduce) {
      return array.reduce(identity, reduce);
    }

    @Override
    public long reduce(long identity, LongBinaryOperator reduce, LongUnaryOperator map) {
      return array.reduce(identity, reduce, map);
    }

    @Override
    public LongArray reduceVector(int dim, ToLongFunction<? super LongArray> accumulator) {
      return array.reduceVector(dim, accumulator);
    }

    @Override
    public LongArray filter(LongPredicate operator) {
      return array.filter(operator);
    }

    @Override
    public long get(int i, int j) {
      return array.get(i, j);
    }

    @Override
    public long get(int index) {
      return array.get(index);
    }

    @Override
    public void set(int index, long value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void set(int[] ix, long value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public long get(int... ix) {
      return array.get(ix);
    }

    @Override
    public void set(int row, int column, long value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void sort() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void sort(LongComparator comparator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public LongStream longStream() {
      return array.longStream();
    }

    @Override
    public List<Long> asList() {
      return Collections.unmodifiableList(array.asList());
    }

    @Override
    public Array<Long> asArray() {
      return unmodifiableArray(array.asArray());
    }

    @Override
    public LongArray times(LongArray other) {
      return array.times(other);
    }

    @Override
    public LongArray times(long alpha, LongArray other) {
      return array.times(alpha, other);
    }

    @Override
    public LongArray times(long scalar) {
      return array.times(scalar);
    }

    @Override
    public LongArray plus(LongArray other) {
      return array.plus(other);
    }

    @Override
    public LongArray plus(long scalar) {
      return array.plus(scalar);
    }

    @Override
    public LongArray plus(long alpha, LongArray other) {
      return array.plus(alpha, other);
    }

    @Override
    public LongArray minus(LongArray other) {
      return array.minus(other);
    }

    @Override
    public LongArray minus(long scalar) {
      return array.minus(scalar);
    }

    @Override
    public LongArray minus(long alpha, LongArray other) {
      return array.minus(alpha, other);
    }

    @Override
    public LongArray reverseMinus(long scalar) {
      return array.reverseMinus(scalar);
    }

    @Override
    public LongArray div(LongArray other) {
      return array.div(other);
    }

    @Override
    public LongArray div(long other) {
      return array.div(other);
    }

    @Override
    public LongArray reverseDiv(long other) {
      return array.reverseDiv(other);
    }

    @Override
    public LongArray negate() {
      return unmodifiableArray(array.negate());
    }

    @Override
    public long[] data() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int toIndex, LongArray from, int fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int toRow, int toColumn, LongArray from, int fromRow, int fromColumn) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int[] toIndex, LongArray from, int[] fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int[] toIndex, LongArray from, int fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int toIndex, LongArray from, int[] fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public LongArray reverse() {
      return unmodifiableArray(array.reverse());
    }

    @Override
    public void assign(LongArray o) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void forEach(int dim, Consumer<LongArray> consumer) {
      array.forEach(dim, consumer);
    }

    @Override
    public void setColumn(int i, LongArray vec) {
      throw new UnsupportedOperationException();
    }

    @Override
    public LongArray getColumn(int index) {
      return unmodifiableArray(array.getColumn(index));
    }

    @Override
    public void setRow(int i, LongArray vec) {
      throw new UnsupportedOperationException();
    }

    @Override
    public LongArray getRow(int i) {
      return unmodifiableArray(array.getRow(i));
    }

    @Override
    public LongArray reshape(int... shape) {
      return unmodifiableArray(array.reshape(shape));
    }

    @Override
    public LongArray ravel() {
      return unmodifiableArray(array.ravel());
    }

    @Override
    public LongArray select(int index) {
      return unmodifiableArray(array.select(index));
    }

    @Override
    public LongArray select(int dimension, int index) {
      return unmodifiableArray(array.select(dimension, index));
    }

    @Override
    public LongArray getView(Range... indexers) {
      return unmodifiableArray(array.getView(indexers));
    }

    @Override
    public LongArray getView(List<? extends Range> ranges) {
      return unmodifiableArray(array.getView(ranges));
    }

    @Override
    public LongArray getVector(int dimension, int index) {
      return unmodifiableArray(array.getVector(dimension, index));
    }

    @Override
    public List<LongArray> getVectors(int dimension) {
      List<LongArray> vectors = array.getVectors(dimension);
      return new AbstractList<LongArray>() {
        @Override
        public LongArray get(int index) {
          return unmodifiableArray(vectors.get(index));
        }

        @Override
        public int size() {
          return vectors.size();
        }
      };
    }

    @Override
    public void setVector(int dimension, int index, LongArray other) {
      throw new UnsupportedOperationException();
    }

    @Override
    public LongArray getDiagonal() {
      return unmodifiableArray(array.getDiagonal());
    }

    @Override
    public LongArray get(IntArray... arrays) {
      return unmodifiableArray(array.get(arrays));
    }

    @Override
    public LongArray get(List<? extends IntArray> arrays) {
      return unmodifiableArray(array.get(arrays));
    }

    @Override
    public void set(IntArray[] indexers, LongArray slice) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void set(List<? extends IntArray> arrays, LongArray value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public LongArray getView(int rowOffset, int colOffset, int rows, int columns) {
      return unmodifiableArray(array.getView(rowOffset, colOffset, rows, columns));
    }

    @Override
    public int size() {
      return array.size();
    }

    @Override
    public int size(int dim) {
      return array.size(dim);
    }

    @Override
    public int vectors(int i) {
      return array.vectors(i);
    }

    @Override
    public int stride(int i) {
      return array.stride(i);
    }

    @Override
    public int getOffset() {
      return array.getOffset();
    }

    @Override
    public int[] getShape() {
      return array.getShape();
    }

    @Override
    public int[] getStride() {
      return array.getStride();
    }

    @Override
    public boolean isSquare() {
      return array.isSquare();
    }

    @Override
    public int rows() {
      return array.rows();
    }

    @Override
    public int columns() {
      return array.columns();
    }

    @Override
    public int dims() {
      return array.dims();
    }

    @Override
    public boolean isVector() {
      return array.isVector();
    }

    @Override
    public boolean isMatrix() {
      return array.isMatrix();
    }

    @Override
    public LongArray asView(int[] shape, int[] stride) {
      return unmodifiableArray(array.asView(shape, stride));
    }

    @Override
    public LongArray asView(int offset, int[] shape, int[] stride) {
      return unmodifiableArray(array.asView(offset, shape, stride));
    }

    @Override
    public LongArray newEmptyArray(int... shape) {
      return unmodifiableArray(array.newEmptyArray(shape));
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public DoubleArray doubleArray() {
      return unmodifiableArray(array.doubleArray());
    }

    @Override
    public IntArray intArray() {
      return unmodifiableArray(array.intArray());
    }

    @Override
    public LongArray longArray() {
      return unmodifiableArray(array.longArray());
    }

    @Override
    public ComplexArray complexArray() {
      return unmodifiableArray(array.complexArray());
    }

    @Override
    public boolean isContiguous() {
      return array.isContiguous();
    }

    @Override
    public LongArray transpose() {
      return unmodifiableArray(array.transpose());
    }

    @Override
    public LongArray copy() {
      return array.copy();
    }

    @Override
    public BooleanArray lt(LongArray other) {
      return array.lt(other);
    }

    @Override
    public BooleanArray gt(LongArray other) {
      return array.gt(other);
    }

    @Override
    public BooleanArray eq(LongArray other) {
      return array.eq(other);
    }

    @Override
    public BooleanArray leq(LongArray other) {
      return array.leq(other);
    }

    @Override
    public BooleanArray geq(LongArray other) {
      return array.geq(other);
    }

    @Override
    public void permute(int count) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void permute(int count, Random random) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void swap(int a, int b) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Long> iterator() {
      return asList().iterator();
    }

    @Override
    public void forEach(Consumer<? super Long> action) {
      array.forEach(action);
    }

    @Override
    public Spliterator<Long> spliterator() {
      return asList().spliterator();
    }
  }


  private static class UnmodifiableComplexArray implements ComplexArray {
    private final ComplexArray array;

    UnmodifiableComplexArray(ComplexArray array) {
      this.array = array;
    }

    @Override
    public String toString() {
      return array.toString();
    }

    @Override
    public boolean equals(Object obj) {
      return array.equals(obj);
    }

    @Override
    public int hashCode() {
      return array.hashCode();
    }

    @Override
    public void assign(Complex value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(double[] value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(Complex[] value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(double real) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(Supplier<Complex> supplier) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(ComplexArray array, UnaryOperator<Complex> operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void combineAssign(ComplexArray array, BinaryOperator<Complex> combine) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(DoubleArray array) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(DoubleArray array, DoubleFunction<Complex> operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(LongArray array, LongFunction<Complex> operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void assign(IntArray array, IntFunction<Complex> operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public ComplexArray map(UnaryOperator<Complex> operator) {
      return array.map(operator);
    }

    @Override
    public IntArray mapToInt(ToIntFunction<Complex> function) {
      return array.mapToInt(function);
    }

    @Override
    public LongArray mapToLong(ToLongFunction<Complex> function) {
      return array.mapToLong(function);
    }

    @Override
    public DoubleArray mapToDouble(ToDoubleFunction<Complex> function) {
      return array.mapToDouble(function);
    }

    @Override
    public <T> Array<T> mapToObj(Function<Complex, ? extends T> mapper) {
      return array.mapToObj(mapper);
    }

    @Override
    public void apply(UnaryOperator<Complex> operator) {
      throw new UnsupportedOperationException();
    }

    @Override
    public ComplexArray filter(Predicate<Complex> predicate) {
      return array.filter(predicate);
    }

    @Override
    public BooleanArray where(Predicate<Complex> predicate) {
      return array.where(predicate);
    }

    @Override
    public BooleanArray where(ComplexArray matrix, BiPredicate<Complex, Complex> predicate) {
      return array.where(matrix, predicate);
    }

    @Override
    public Complex reduce(Complex identity, BinaryOperator<Complex> reduce) {
      return array.reduce(identity, reduce);
    }

    @Override
    public ComplexArray reduceVectors(int dim,
        Function<? super ComplexArray, ? extends Complex> reduce) {
      return array.reduceVectors(dim, reduce);
    }

    @Override
    public Complex reduce(Complex identity, BinaryOperator<Complex> reduce,
        UnaryOperator<Complex> map) {
      return array.reduce(identity, reduce, map);
    }

    @Override
    public ComplexArray conjugateTranspose() {
      return unmodifiableArray(array.conjugateTranspose());
    }

    @Override
    public void set(int i, int j, Complex complex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void set(int index, Complex complex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void set(int[] index, Complex complex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Complex get(int index) {
      return array.get(index);
    }

    @Override
    public Complex get(int i, int j) {
      return array.get(i, j);
    }

    @Override
    public Complex get(int... index) {
      return array.get(index);
    }

    @Override
    public Complex getComplex(int i) {
      return array.getComplex(i);
    }

    @Override
    public Array<Complex> asArray() {
      return unmodifiableArray(array.asArray());
    }

    @Override
    public Stream<Complex> stream() {
      return array.stream();
    }

    @Override
    public ComplexArray negate() {
      return unmodifiableArray(array.negate());
    }

    @Override
    public double[] data() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int toIndex, ComplexArray from, int fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int toRow, int toColumn, ComplexArray from, int fromRow, int fromColumn) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int[] toIndex, ComplexArray from, int[] fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int[] toIndex, ComplexArray from, int fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void setFrom(int toIndex, ComplexArray from, int[] fromIndex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public ComplexArray reverse() {
      return unmodifiableArray(array.reverse());
    }

    @Override
    public void assign(ComplexArray o) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void forEach(int dim, Consumer<ComplexArray> consumer) {
      array.forEach(dim, consumer);
    }

    @Override
    public void setColumn(int i, ComplexArray vec) {
      throw new UnsupportedOperationException();
    }

    @Override
    public ComplexArray getColumn(int index) {
      return unmodifiableArray(array.getColumn(index));
    }

    @Override
    public void setRow(int i, ComplexArray vec) {
      throw new UnsupportedOperationException();
    }

    @Override
    public ComplexArray getRow(int i) {
      return unmodifiableArray(array.getRow(i));
    }

    @Override
    public ComplexArray reshape(int... shape) {
      return unmodifiableArray(array.reshape(shape));
    }

    @Override
    public ComplexArray ravel() {
      return unmodifiableArray(array.ravel());
    }

    @Override
    public ComplexArray select(int index) {
      return unmodifiableArray(array.select(index));
    }

    @Override
    public ComplexArray select(int dimension, int index) {
      return unmodifiableArray(array.select(dimension, index));
    }

    @Override
    public ComplexArray getView(Range... indexers) {
      return unmodifiableArray(array.getView(indexers));
    }

    @Override
    public ComplexArray getView(List<? extends Range> ranges) {
      return unmodifiableArray(array.getView(ranges));
    }

    @Override
    public ComplexArray getVector(int dimension, int index) {
      return unmodifiableArray(array.getVector(dimension, index));
    }

    @Override
    public List<ComplexArray> getVectors(int dimension) {
      List<ComplexArray> vectors = array.getVectors(dimension);
      return new AbstractList<ComplexArray>() {
        @Override
        public ComplexArray get(int index) {
          return unmodifiableArray(vectors.get(index));
        }

        @Override
        public int size() {
          return vectors.size();
        }
      };
    }

    @Override
    public void setVector(int dimension, int index, ComplexArray other) {
      throw new UnsupportedOperationException();
    }

    @Override
    public ComplexArray getDiagonal() {
      return unmodifiableArray(array.getDiagonal());
    }

    @Override
    public ComplexArray get(IntArray... arrays) {
      return unmodifiableArray(array.get(arrays));
    }

    @Override
    public ComplexArray get(List<? extends IntArray> arrays) {
      return unmodifiableArray(array.get(arrays));
    }

    @Override
    public void set(IntArray[] indexers, ComplexArray slice) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void set(List<? extends IntArray> arrays, ComplexArray value) {
      throw new UnsupportedOperationException();
    }

    @Override
    public ComplexArray getView(int rowOffset, int colOffset, int rows, int columns) {
      return unmodifiableArray(array.getView(rowOffset, colOffset, rows, columns));
    }

    @Override
    public int size() {
      return array.size();
    }

    @Override
    public boolean isEmpty() {
      return array.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
      return array.contains(o);
    }

    @Override
    public int size(int dim) {
      return array.size(dim);
    }

    @Override
    public int vectors(int i) {
      return array.vectors(i);
    }

    @Override
    public int stride(int i) {
      return array.stride(i);
    }

    @Override
    public int getOffset() {
      return array.getOffset();
    }

    @Override
    public int[] getShape() {
      return array.getShape();
    }

    @Override
    public int[] getStride() {
      return array.getStride();
    }

    @Override
    public boolean isSquare() {
      return array.isSquare();
    }

    @Override
    public int rows() {
      return array.rows();
    }

    @Override
    public int columns() {
      return array.columns();
    }

    @Override
    public int dims() {
      return array.dims();
    }

    @Override
    public boolean isVector() {
      return array.isVector();
    }

    @Override
    public boolean isMatrix() {
      return array.isMatrix();
    }

    @Override
    public ComplexArray asView(int[] shape, int[] stride) {
      return unmodifiableArray(array.asView(shape, stride));
    }

    @Override
    public ComplexArray asView(int offset, int[] shape, int[] stride) {
      return unmodifiableArray(array.asView(offset, shape, stride));
    }

    @Override
    public ComplexArray newEmptyArray(int... shape) {
      return array.newEmptyArray(shape);
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public DoubleArray doubleArray() {
      return unmodifiableArray(array.doubleArray());
    }

    @Override
    public IntArray intArray() {
      return unmodifiableArray(array.intArray());
    }

    @Override
    public LongArray longArray() {
      return unmodifiableArray(array.longArray());
    }

    @Override
    public ComplexArray complexArray() {
      return unmodifiableArray(array.complexArray());
    }

    @Override
    public boolean isContiguous() {
      return array.isContiguous();
    }

    @Override
    public ComplexArray transpose() {
      return unmodifiableArray(array.transpose());
    }

    @Override
    public ComplexArray copy() {
      return array.copy();
    }

    @Override
    public void permute(int count) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void permute(int count, Random random) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void swap(int a, int b) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<Complex> iterator() {
      return array.iterator();
    }

    @Override
    public Object[] toArray() {
      return array.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
      return array.toArray(a);
    }

    @Override
    public boolean add(Complex complex) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean remove(Object o) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean containsAll(Collection<?> c) {
      return array.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends Complex> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean removeAll(Collection<?> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean retainAll(Collection<?> c) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void clear() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void forEach(Consumer<? super Complex> action) {
      array.forEach(action);
    }

    @Override
    public Spliterator<Complex> spliterator() {
      return array.spliterator();
    }
  }


  private static class UnmodifiableBooleanArray implements BooleanArray {
    private final BooleanArray array;

    UnmodifiableBooleanArray(BooleanArray array) {
      this.array = array;
    }

    @Override
    public String toString() {
      return array.toString();
    }

    @Override
    public boolean equals(Object obj) {
      return array.equals(obj);
    }

    @Override
    public int hashCode() {
      return array.hashCode();
    }

    public DoubleArray doubleArray() {
      return unmodifiableArray(array.doubleArray());
    }

    public void setFrom(int[] toIndex, BooleanArray from, int[] fromIndex) {
      throw new UnsupportedOperationException();
    }

    public int size() {
      return array.size();
    }

    public void assign(boolean value) {
      throw new UnsupportedOperationException();
    }

    public BooleanArray ravel() {
      return unmodifiableArray(array.ravel());
    }

    public void forEach(int dim, Consumer<BooleanArray> consumer) {
      array.forEach(dim, consumer);
    }

    public int columns() {
      return array.columns();
    }

    public boolean isMatrix() {
      return array.isMatrix();
    }

    public boolean isView() {
      return true;
    }

    public BooleanArray getView(Range... indexers) {
      return unmodifiableArray(array.getView(indexers));
    }

    public int dims() {
      return array.dims();
    }

    public BooleanArray reduceAlong(int dim, Function<? super BooleanArray, Boolean> function) {
      return array.reduceAlong(dim, function);
    }

    public BooleanArray getVector(int dimension, int index) {
      return unmodifiableArray(array.getVector(dimension, index));
    }

    public BooleanArray getRow(int i) {
      return unmodifiableArray(array.getRow(i));
    }

    public void setFrom(int[] toIndex, BooleanArray from, int fromIndex) {
      throw new UnsupportedOperationException();
    }

    public BooleanArray reverse() {
      return unmodifiableArray(array.reverse());
    }

    public BooleanArray not() {
      return unmodifiableArray(array.not());
    }

    public BooleanArray transpose() {
      return unmodifiableArray(array.transpose());
    }

    public void set(int index, boolean value) {
      throw new UnsupportedOperationException();
    }

    public boolean get(int... index) {
      return array.get(index);
    }

    public List<BooleanArray> getVectors(int dimension) {
      List<BooleanArray> vectors = array.getVectors(dimension);
      return new AbstractList<BooleanArray>() {
        @Override
        public BooleanArray get(int index) {
          return unmodifiableArray(vectors.get(index));
        }

        @Override
        public int size() {
          return vectors.size();
        }
      };
    }

    public BooleanArray booleanArray() {
      return unmodifiableArray(array.booleanArray());
    }

    public void assign(Supplier<Boolean> supplier) {
      throw new UnsupportedOperationException();
    }

    public void set(IntArray[] indexers, BooleanArray slice) {
      throw new UnsupportedOperationException();
    }

    public int getOffset() {
      return array.getOffset();
    }

    public void set(List<? extends IntArray> arrays, BooleanArray value) {
      throw new UnsupportedOperationException();
    }

    public void setFrom(int toIndex, BooleanArray from, int[] fromIndex) {
      throw new UnsupportedOperationException();
    }

    public BooleanArray get(IntArray... arrays) {
      return unmodifiableArray(array.get(arrays));
    }

    public BooleanArray asView(int[] shape, int[] stride) {
      return unmodifiableArray(array.asView(shape, stride));
    }

    public ComplexArray asComplexArray() {
      return unmodifiableArray(array.asComplexArray());
    }

    public boolean isContiguous() {
      return array.isContiguous();
    }

    public boolean get(int index) {
      return array.get(index);
    }

    public BooleanArray all(int dim) {
      return array.all(dim);
    }

    public Stream<Boolean> stream() {
      return asList().stream();
    }

    public void permute(int count) {
      throw new UnsupportedOperationException();
    }

    public void set(int[] index, boolean value) {
      throw new UnsupportedOperationException();
    }

    public void setVector(int dimension, int index, BooleanArray other) {
      throw new UnsupportedOperationException();
    }

    public void permute(int count, Random random) {
      throw new UnsupportedOperationException();
    }

    public int[] getStride() {
      return array.getStride();
    }

    public BooleanArray newEmptyArray(int... shape) {
      return array.newEmptyArray(shape);
    }

    public void swap(int a, int b) {
      throw new UnsupportedOperationException();
    }

    public BooleanArray select(int index) {
      return unmodifiableArray(array.select(index));
    }

    public BooleanArray copy() {
      return array.copy();
    }

    public Spliterator<Boolean> spliterator() {
      return asList().spliterator();
    }

    public void apply(UnaryOperator<Boolean> operator) {
      throw new UnsupportedOperationException();
    }

    public LongArray longArray() {
      return unmodifiableArray(array.longArray());
    }

    public void setRow(int i, BooleanArray vec) {
      throw new UnsupportedOperationException();
    }

    public BooleanArray asView(int offset, int[] shape, int[] stride) {
      return unmodifiableArray(array.asView(offset, shape, stride));
    }

    public void assign(BooleanArray o) {
      throw new UnsupportedOperationException();
    }

    public int vectors(int i) {
      return array.vectors(i);
    }

    public void setColumn(int i, BooleanArray vec) {
      throw new UnsupportedOperationException();
    }

    public Array<Boolean> boxed() {
      return unmodifiableArray(array.boxed());
    }

    public int rows() {
      return array.rows();
    }

    public boolean get(int i, int j) {
      return array.get(i, j);
    }

    public BooleanArray any(int dim) {
      return array.any(dim);
    }

    public IntArray intArray() {
      return unmodifiableArray(array.intArray());
    }

    public boolean isSquare() {
      return array.isSquare();
    }

    public boolean reduce(boolean identity, BinaryOperator<Boolean> accumulator) {
      return array.reduce(identity, accumulator);
    }

    public void setFrom(int toIndex, BooleanArray from, int fromIndex) {
      throw new UnsupportedOperationException();
    }

    public BooleanArray select(int dimension, int index) {
      return unmodifiableArray(array.select(dimension, index));
    }

    public BooleanArray map(Function<Boolean, Boolean> mapper) {
      return array.map(mapper);
    }

    public boolean all() {
      return array.all();
    }

    public int[] getShape() {
      return array.getShape();
    }

    public void setFrom(int toRow, int toColumn, BooleanArray from, int fromRow, int fromColumn) {
      throw new UnsupportedOperationException();
    }

    public BooleanArray getView(int rowOffset, int colOffset, int rows, int columns) {
      return unmodifiableArray(array.getView(rowOffset, colOffset, rows, columns));
    }

    public int stride(int i) {
      return array.stride(i);
    }

    public BooleanArray getView(List<? extends Range> ranges) {
      return unmodifiableArray(array.getView(ranges));
    }

    public void set(int i, int j, boolean value) {
      throw new UnsupportedOperationException();
    }

    public List<Boolean> asList() {
      return Collections.unmodifiableList(array.asList());
    }

    public BooleanArray reshape(int... shape) {
      return unmodifiableArray(array.reshape(shape));
    }

    public Iterator<Boolean> iterator() {
      return asList().iterator();
    }

    public BooleanArray getDiagonal() {
      return unmodifiableArray(array.getDiagonal());
    }

    public boolean isVector() {
      return array.isVector();
    }

    public BooleanArray get(List<? extends IntArray> arrays) {
      return unmodifiableArray(array.get(arrays));
    }

    public BooleanArray getColumn(int index) {
      return unmodifiableArray(array.getColumn(index));
    }

    public int size(int dim) {
      return array.size(dim);
    }

    public void forEach(Consumer<? super Boolean> action) {
      array.forEach(action);
    }

    public boolean any() {
      return array.any();
    }
  }

  private static class ArrayListAdapter<T> extends AbstractList<T> {
    private final Array<T> array;

    public ArrayListAdapter(Array<T> array) {
      this.array = array;
    }

    @Override
    public T get(int index) {
      return array.get(index);
    }

    @Override
    public int size() {
      return array.size();
    }
  }
}
