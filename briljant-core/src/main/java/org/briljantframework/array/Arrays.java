/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.Predicate;
import java.util.stream.StreamSupport;

import net.mintern.primitive.comparators.DoubleComparator;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.array.api.ArrayRoutines;
import org.briljantframework.array.netlib.NetlibArrayBackend;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.function.DoubleBiPredicate;
import org.briljantframework.linalg.api.LinearAlgebraRoutines;
import org.briljantframework.sort.IndexComparator;

/**
 * @author Isak Karlsson
 */
public final class Arrays {

  public static final LinearAlgebraRoutines linalg;
  public static final String VERSION = "0.1";
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

  private Arrays() {}

  /**
   * @see org.briljantframework.array.api.ArrayFactory#referenceArray(int...)
   */
  public static <T> Array<T> newArray(int... shape) {
    return ARRAY_FACTORY.referenceArray(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(Object[])
   */
  @SafeVarargs
  public static <T> Array<T> newVector(T... data) {
    return ARRAY_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(Object[][])
   */
  public static <T> Array<T> newMatrix(T[][] data) {
    return ARRAY_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#doubleArray(int...)
   */
  public static DoubleArray newDoubleArray(int... shape) {
    return ARRAY_FACTORY.doubleArray(shape);
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
   * @see org.briljantframework.array.api.ArrayFactory#array(double[])
   */
  public static DoubleArray newDoubleVector(double... data) {
    return ARRAY_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(double[][])
   */
  public static DoubleArray newDoubleMatrix(double[][] data) {
    return ARRAY_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#linspace(double, double, int)
   */
  public static DoubleArray linspace(double start, double end, int size) {
    return ARRAY_FACTORY.linspace(start, end, size);
  }

  /**
   * Create a 1d-array with values sampled from the specified distribution.
   *
   * @param size the size of the array
   * @param distribution the distribution to sample from
   * @return a new 1d-array
   */
  public static DoubleArray rand(int size, RealDistribution distribution) {
    DoubleArray array = newDoubleArray(size);
    array.assign(distribution::sample);
    return array;
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
   * @see org.briljantframework.array.api.ArrayFactory#complexArray(int...)
   */
  public static ComplexArray newComplexArray(int... shape) {
    return ARRAY_FACTORY.complexArray(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#complexArray(double[])
   */
  public static ComplexArray newComplexArray(double[] data) {
    return ARRAY_FACTORY.complexArray(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(org.apache.commons.math3.complex.Complex[])
   */
  public static ComplexArray newComplexVector(Complex... data) {
    return ARRAY_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(org.apache.commons.math3.complex.Complex[][])
   */
  public static ComplexArray newComplexMatrix(Complex[][] data) {
    return ARRAY_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#intArray(int...)
   */
  public static IntArray newIntArray(int... shape) {
    return ARRAY_FACTORY.intArray(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(int[])
   */
  public static IntArray newIntVector(int... data) {
    return ARRAY_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(int[][])
   */
  public static IntArray newIntMatrix(int[][] data) {
    return ARRAY_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#range()
   */
  public static Range range() {
    return ARRAY_FACTORY.range();
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

  public static IntArray randi(int size, int l, int u) {
    RealDistribution distribution = new UniformRealDistribution(l, u);
    IntArray array = newIntArray(size);
    array.assign(() -> (int) Math.round(distribution.sample()));
    return array;
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#longArray(int...)
   */
  public static LongArray newLongArray(int... shape) {
    return ARRAY_FACTORY.longArray(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(long[])
   */
  public static LongArray newLongVector(long... data) {
    return ARRAY_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(long[][])
   */
  public static LongArray newLongMatrix(long[][] data) {
    return ARRAY_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#booleanArray(int...)
   */
  public static BooleanArray newBooleanArray(int... shape) {
    return ARRAY_FACTORY.booleanArray(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(boolean[])
   */
  public static BooleanArray newBooleanVector(boolean[] data) {
    return ARRAY_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(boolean[][])
   */
  public static BooleanArray newBooleanMatrix(boolean[][] data) {
    return ARRAY_FACTORY.array(data);
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

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sum(org.briljantframework.array.IntArray)
   */
  public static int sum(IntArray x) {
    return ARRAY_ROUTINES.sum(x);
  }

  public static int sum(BooleanArray x) {
    return sum(x.asInt());
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
   */
  public static <T extends BaseArray<T>> List<T> vsplit(T array, int parts) {
    if (array.isVector()) {
      array = array.reshape(array.size(), 1);
    }
    return split(array, parts, 1);
  }

  /**
   */
  public static <T extends BaseArray<T>> T vstack(List<T> arrays) {
    arrays.replaceAll(v -> v.isVector() ? v.reshape(v.size(), 1) : v);
    return concatenate(arrays, 1);
  }

  @SafeVarargs
  public static <T extends BaseArray<T>> T vstack(T... arrays) {
    return vstack(java.util.Arrays.asList(arrays));
  }

  public static <T extends BaseArray<T>> List<T> hsplit(T array, int parts) {
    if (array.isVector()) {
      array = array.reshape(1, array.size());
    }
    return split(array, parts, 0);
  }

  public static <T extends BaseArray<T>> T hstack(List<T> arrays) {
    arrays.replaceAll(a -> a.isVector() ? a.reshape(1, a.size()) : a);
    return concatenate(arrays, 0);
  }

  @SafeVarargs
  public static <T extends BaseArray<T>> T hstack(T... arrays) {
    return hstack(java.util.Arrays.asList(arrays));
  }

  public static <T extends BaseArray<T>> T concatenate(List<T> arrays) {
    return concatenate(arrays, 0);
  }

  public static <T extends BaseArray<T>> T concatenate(List<T> arrays, int dim) {
    T prototype = arrays.get(0);
    int[] shape = prototype.getShape();
    shape[dim] = 0;
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

  public static <T extends BaseArray<T>> List<T> split(T array, int parts, int dim) {
    Check.argument(array.size(dim) % parts == 0);
    int[] shape = array.getShape();
    shape[dim] /= parts;

    return new AbstractList<T>() {
      @Override
      public T get(int index) {
        T empty = array.newEmptyArray(shape);
        empty.assign(array.select(dim, index));
        return empty;
      }

      @Override
      public int size() {
        return parts;
      }
    };
  }

  public static <S extends BaseArray<S>> List<S> split(S array, int parts) {
    return split(array, parts, 1);
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
   * @see org.briljantframework.array.api.ArrayRoutines#take(org.briljantframework.array.BaseArray,
   *      int)
   */
  public static <T extends BaseArray<T>> T take(T x, int num) {
    return ARRAY_ROUTINES.take(x, num);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#repmat(org.briljantframework.array.BaseArray,
   *      int, int)
   */
  public static <T extends BaseArray<T>> T repmat(T x, int r, int c) {
    return ARRAY_ROUTINES.repmat(x, r, c);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#repmat(org.briljantframework.array.BaseArray,
   *      int)
   */
  public static <T extends BaseArray<T>> T repmat(T x, int n) {
    return ARRAY_ROUTINES.repmat(x, n);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#repeat(org.briljantframework.array.BaseArray,
   *      int)
   */
  public static <T extends BaseArray<T>> T repeat(T x, int num) {
    return ARRAY_ROUTINES.repeat(x, num);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sort(org.briljantframework.array.Array)
   */
  public static <T extends Comparable<T>> Array<T> sort(Array<T> array) {
    return ARRAY_ROUTINES.sort(array);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sort(int, org.briljantframework.array.Array)
   */
  public static <T extends Comparable<T>> Array<T> sort(int dim, Array<T> array) {
    return ARRAY_ROUTINES.sort(dim, array);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sort(org.briljantframework.array.Array,
   *      java.util.Comparator)
   */
  public static <T> Array<T> sort(Array<T> array, Comparator<T> cmp) {
    return ARRAY_ROUTINES.sort(array, cmp);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sort(int, org.briljantframework.array.Array,
   *      java.util.Comparator)
   */
  public static <T> Array<T> sort(int dim, Array<T> array, Comparator<T> cmp) {
    return ARRAY_ROUTINES.sort(dim, array, cmp);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sort(org.briljantframework.array.BaseArray)
   */
  public static <T extends BaseArray<T>> T sort(T array) {
    return ARRAY_ROUTINES.sort(array);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sort(int,
   *      org.briljantframework.array.BaseArray)
   */
  public static <T extends BaseArray<T>> T sort(int dim, T array) {
    return ARRAY_ROUTINES.sort(dim, array);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sort(org.briljantframework.array.BaseArray,
   *      org.briljantframework.sort.IndexComparator)
   */
  public static <T extends BaseArray<T>> T sort(T x, IndexComparator<T> cmp) {
    return ARRAY_ROUTINES.sort(x, cmp);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sort(int,
   *      org.briljantframework.array.BaseArray, org.briljantframework.sort.IndexComparator)
   */
  public static <T extends BaseArray<T>> T sort(int dim, T x, IndexComparator<T> cmp) {
    return ARRAY_ROUTINES.sort(dim, x, cmp);
  }

  public static <T extends BaseArray<T>> T shuffle(T x) {
    T out = x.copy();
    out.permute(out.size());
    return out;
  }

  public static DoubleArray dot(Op transA, Op transB, double alpha, DoubleArray a, DoubleArray b) {
    Check.argument(a.isMatrix() && b.isMatrix(), "require 2d-arrays");
    int m = a.size(transA == Op.KEEP ? 0 : 1);
    int bm = b.size(transB == Op.KEEP ? 0 : 1);
    int n = b.size(transB == Op.KEEP ? 1 : 0);
    int k = a.size(transA == Op.KEEP ? 1 : 0);
    if (m == 0 || k == 0 || n == 0 || bm == 0) {
      throw new IllegalArgumentException("empty result");
    }
    if (b.size(transB == Op.KEEP ? 0 : 1) != a.size(transA == Op.KEEP ? 1 : 0)) {
      throw new NonConformantException(a, b);
    }
    DoubleArray c = newDoubleArray(m, n);
    gemm(transA, transB, alpha, a, b, 1, c);
    return c;
  }

  public static DoubleArray dot(Op transA, Op transB, DoubleArray a, DoubleArray b) {
    return dot(transA, transB, 1, a, b);
  }

  public static DoubleArray dot(DoubleArray a, DoubleArray b) {
    return dot(1.0, a, b);
  }

  public static DoubleArray dot(double alpha, DoubleArray a, DoubleArray b) {
    return dot(Op.KEEP, Op.KEEP, alpha, a, b);
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
    return ARRAY_ROUTINES.inner(a, b);
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
   * Arrays.outer(Arrays.linspace(0, 3, 4), Arrays.linspace(0,3,4).reshape(2,2))
   * array([[0.000, 0.000, 0.000, 0.000],
   *        [0.000, 1.000, 2.000, 3.000],
   *        [0.000, 2.000, 4.000, 6.000],
   *        [0.000, 3.000, 6.000, 9.000]] type: double)
   * </pre>
   *
   * @param a the first argument of size {@code m}
   * @param b the second argument of size {@code n}
   * @return a new 2d-array of size {@code m x n}
   */
  public static DoubleArray outer(DoubleArray a, DoubleArray b) {
    a = a.isVector() ? a : a.ravel();
    b = b.isVector() ? b : b.ravel();

    DoubleArray out = newDoubleArray(a.size(), b.size());
    ger(1, a, b, out);
    return out;
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
   * @see org.briljantframework.array.api.ArrayRoutines#ger(double,
   *      org.briljantframework.array.DoubleArray, org.briljantframework.array.DoubleArray,
   *      org.briljantframework.array.DoubleArray)
   */
  public static void ger(double alpha, DoubleArray x, DoubleArray y, DoubleArray a) {
    ARRAY_ROUTINES.ger(alpha, x, y, a);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#gemm(org.briljantframework.array.Op,
   *      org.briljantframework.array.Op, double, org.briljantframework.array.DoubleArray,
   *      org.briljantframework.array.DoubleArray, double, org.briljantframework.array.DoubleArray)
   */
  public static void gemv(Op transA, double alpha, DoubleArray a, DoubleArray x, double beta,
      DoubleArray y) {
    ARRAY_ROUTINES.gemv(transA, alpha, a, x, beta, y);
  }

  /**
   * Delegates to
   * {@link #gemv(org.briljantframework.array.Op, double, org.briljantframework.array.DoubleArray, org.briljantframework.array.DoubleArray, double, org.briljantframework.array.DoubleArray)}
   * with the first argument {@code Op.KEEP}.
   *
   * @see #gemv(org.briljantframework.array.Op, double, org.briljantframework.array.DoubleArray,
   *      org.briljantframework.array.DoubleArray, double, org.briljantframework.array.DoubleArray)
   */
  public static void gemv(double alpha, DoubleArray a, DoubleArray x, double beta, DoubleArray y) {
    gemv(Op.KEEP, alpha, a, x, beta, y);
  }

  /**
   * Delegates to
   * {@link #gemv(org.briljantframework.array.Op, double, org.briljantframework.array.DoubleArray, org.briljantframework.array.DoubleArray, double, org.briljantframework.array.DoubleArray)}
   * with the first argument {@code Op.KEEP}, {@code alpha
   * = 1} and {@code beta = 1}
   *
   * @see #gemv(org.briljantframework.array.Op, double, org.briljantframework.array.DoubleArray,
   *      org.briljantframework.array.DoubleArray, double, org.briljantframework.array.DoubleArray)
   */
  public static void gemv(DoubleArray a, DoubleArray x, DoubleArray y) {
    gemv(1, a, x, 1, y);
  }

  public static void gemm(Op transA, Op transB, double alpha, DoubleArray a, DoubleArray b,
      double beta, DoubleArray c) {
    ARRAY_ROUTINES.gemm(transA, transB, alpha, a, b, beta, c);
  }

  public static void gemm(double alpha, DoubleArray a, DoubleArray b, double beta, DoubleArray c) {
    gemm(Op.KEEP, Op.KEEP, alpha, a, b, beta, c);
  }

  public static void gemm(DoubleArray a, DoubleArray b, DoubleArray c) {
    gemm(Op.KEEP, Op.KEEP, 1, a, b, 1, c);
  }

  /**
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
   * <p>
   * Take values in {@code array}, using the indexes in {@code indexes}.
   * </p>
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
   * <p>
   * Changes the values of array copy of {@code array} according to the values of the {@code mask}
   * and the values in {@code values}. The value at {@code i} in array copy of {@code array} is set
   * to value at {@code i} from {@code values} if the boolean at {@code i} in {@code mask} is
   * {@code true}.
   * </p>
   *
   * @param array array source array
   * @param mask the mask; same shape as {@code array}
   * @param values the values; same shape as {@code array}
   * @return a new array; the returned array has the same type as {@code array}.
   */
  public static <T extends BaseArray<T>> T mask(T array, BooleanArray mask, T values) {
    Check.shape(array, mask);
    Check.shape(array, values);

    T masked = array.copy();
    putMask(masked, mask, values);
    return masked;
  }

  /**
   * <p>
   * Changes the values of {@code a} according to the values of the {@code mask} and the values in
   * {@code values}.
   * </p>
   *
   * @param a the target matrix
   * @param mask the mask; same shape as {@code a}
   * @param values the mask; same shape as {@code a}
   */
  public static <T extends BaseArray<T>> void putMask(T a, BooleanArray mask, T values) {
    Check.shape(a, mask);
    Check.shape(a, values);
    for (int i = 0; i < a.size(); i++) {
      if (mask.get(i)) {
        a.set(i, values, i);
      }
    }
  }

  /**
   * <p>
   * Selects the values in {@code a} according to the values in {@code where}, replacing those not
   * selected with {@code replace}.
   * </p>
   *
   * @param a the source matrix
   * @param where the selection matrix; same shape as {@code a}
   * @param replace the replacement value
   * @return a new matrix; the returned matrix has the same type as {@code a}.
   */
  public static IntArray select(IntArray a, BooleanArray where, int replace) {
    Check.shape(a, where);
    IntArray copy = a.copy();
    copy.assign(where, (b, i) -> b ? replace : i);
    return copy;
  }

  public static IntArray order(DoubleArray array, DoubleComparator cmp) {
    IntArray order = Arrays.range(array.size()).copy();
    order.sort((a, b) -> cmp.compare(array.get(a), array.get(b)));
    return order;
  }

  public static IntArray order(int dim, DoubleArray array, DoubleComparator cmp) {
    int vectors = array.vectors(dim);
    IntArray order = IntArray.zeros(array.getShape());
    for (int i = 0; i < vectors; i++) {
      order.setVector(dim, i, order(array.getVector(dim, i), cmp));
    }
    return order;
  }

  public static IntArray order(DoubleArray array) {
    return order(array, Double::compare);
  }

  public static IntArray order(int dim, DoubleArray array) {
    return order(dim, array, Double::compare);
  }

  public static <T> int binarySearch(Array<? extends Comparable<? super T>> array, T x) {
    return Collections.binarySearch(array.toList(), x);
  }

  public static int binarySearch(IntArray array, int x) {
    return binarySearch(array.boxed(), x);
  }

  public static int binarySearch(DoubleArray array, double x) {
    return binarySearch(array.boxed(), x);
  }

  /**
   * Locate the insertion point for value in a to maintain sorted order. If value is already present
   * in the array, the insertion point will be before (to the left of) any existing entries.
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
   * Locate the insertion point for value in a to maintain sorted order. If value is already present
   * in the array, the insertion point will be after (to the right of) any existing entries.
   *
   * @param array the array
   * @param value the value
   * @param <T> the class of objects in the array
   * @return the insertion point of the value
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
   * @see #bisectLeft(Array, Object)
   */
  public static int bisectLeft(IntArray array, int value) {
    return bisectLeft(array.boxed(), value);
  }

  /**
   * @see #bisectRight(Array, Object)
   */
  public static int bisectRight(IntArray array, int value) {
    return bisectRight(array.boxed(), value);
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

  public static <S extends BaseArray<S>> S where(BooleanArray c, S x, S y) {
    Check.size(x.size() == y.size() && x.size() == c.size(), "Illegal sizes");
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
    DoubleArray selected = newDoubleArray(size);
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
