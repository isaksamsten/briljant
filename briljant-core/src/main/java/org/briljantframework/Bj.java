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

package org.briljantframework;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.briljantframework.array.Array;
import org.briljantframework.array.BaseArray;
import org.briljantframework.array.BitArray;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.LongArray;
import org.briljantframework.array.Op;
import org.briljantframework.array.Range;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.array.api.ArrayRoutines;
import org.briljantframework.array.netlib.NetlibArrayBackend;
import org.briljantframework.linalg.api.LinearAlgebraRoutines;
import org.briljantframework.sort.IndexComparator;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

/**
 * @author Isak Karlsson
 */
public final class Bj {

  private static final RealDistribution normalDistribution = new NormalDistribution(0, 1);
  private static final RealDistribution uniformDistribution = new UniformRealDistribution(-1, 1);

  private static final ArrayFactory MATRIX_FACTORY;
  private static final ArrayRoutines MATRIX_ROUTINES;

  public static final LinearAlgebraRoutines linalg;

  public static final String VERSION = "0.1";

  static {
    ArrayBackend backend =
        StreamSupport.stream(ServiceLoader.load(ArrayBackend.class).spliterator(), false)
            .filter(ArrayBackend::isAvailable)
            .sorted((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
            .findFirst()
            .orElse(new NetlibArrayBackend());

    MATRIX_FACTORY = backend.getArrayFactory();
    MATRIX_ROUTINES = backend.getArrayRoutines();
    linalg = backend.getLinearAlgebraRoutines();
  }

  private Bj() {
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#referenceArray(int...)
   */
  public static <T> Array<T> referenceArray(int... shape) {
    return MATRIX_FACTORY.referenceArray(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(Object[])
   */
  public static <T> Array<T> array(T[] data) {
    return MATRIX_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(Object[][])
   */
  public static <T> Array<T> array(T[][] data) {
    return MATRIX_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#doubleArray(int...)
   */
  public static DoubleArray doubleArray(int... shape) {
    return MATRIX_FACTORY.doubleArray(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#ones(int...)
   */
  public static DoubleArray ones(int... shape) {
    return MATRIX_FACTORY.ones(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#zero(int...)
   */
  public static DoubleArray zero(int... shape) {
    return MATRIX_FACTORY.zero(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#eye(int)
   */
  public static DoubleArray eye(int size) {
    return MATRIX_FACTORY.eye(size);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(double[])
   */
  public static DoubleArray array(double[] data) {
    return MATRIX_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(double[][])
   */
  public static DoubleArray array(double[][] data) {
    return MATRIX_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#linspace(double, double, int)
   */
  public static DoubleArray linspace(double start, double end, int size) {
    return MATRIX_FACTORY.linspace(start, end, size);
  }

  /**
   * Create a 1d-array with values sampled from the specified distribution.
   *
   * @param size         the size of the array
   * @param distribution the distribution to sample from
   * @return a new 1d-array
   */
  public static DoubleArray rand(int size, RealDistribution distribution) {
    return doubleArray(size).assign(distribution::sample);
  }

  /**
   * Create a 1d-array with values sampled from the normal (gaussian) distribution with mean {@code
   * 0} and standard deviation {@code 1}.
   *
   * <p>Example
   * <pre>{@code
   * > Bj.randn(9).reshape(3, 3);
   * array([[0.168, -0.297, -0.374],
   *        [1.030, -1.465,  0.636],
   *        [0.957, -0.990,  0.498]] type: double)
   * }</pre>
   *
   * @param size the size of the array
   * @return a new 1d-array
   */
  public static DoubleArray randn(int size) {
    return rand(size, normalDistribution);
  }

  /**
   * Create a 1d-array with values sampled uniformly from the range {@code [-1, 1]}
   * <p>Example
   * <pre>{@code
   * > Bj.rand(4).reshape(2,2)
   * array([[0.467, 0.898],
   *        [0.568, 0.103]] type: double)
   * }</pre>
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
  public static ComplexArray complexArray(int... shape) {
    return MATRIX_FACTORY.complexArray(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#complexArray(double[])
   */
  public static ComplexArray complexArray(double[] data) {
    return MATRIX_FACTORY.complexArray(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(org.apache.commons.math3.complex.Complex[])
   */
  public static ComplexArray array(Complex[] data) {
    return MATRIX_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(org.apache.commons.math3.complex.Complex[][])
   */
  public static ComplexArray array(Complex[][] data) {
    return MATRIX_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#intArray(int...)
   */
  public static IntArray intArray(int... shape) {
    return MATRIX_FACTORY.intArray(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(int[])
   */
  public static IntArray array(int[] data) {
    return MATRIX_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(int[][])
   */
  public static IntArray array(int[][] data) {
    return MATRIX_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#range()
   */
  public static Range range() {
    return MATRIX_FACTORY.range();
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#range(int)
   */
  public static Range range(int end) {
    return MATRIX_FACTORY.range(end);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#range(int, int)
   */
  public static Range range(int start, int end) {
    return MATRIX_FACTORY.range(start, end);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#range(int, int, int)
   */
  public static Range range(int start, int end, int step) {
    return MATRIX_FACTORY.range(start, end, step);
  }

  public static IntArray randi(int size, int l, int u) {
    RealDistribution distribution = new UniformRealDistribution(l, u);
    return intArray(size).assign(() -> (int) Math.round(distribution.sample()));
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#longArray(int...)
   */
  public static LongArray longArray(int... shape) {
    return MATRIX_FACTORY.longArray(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(long[])
   */
  public static LongArray array(long[] data) {
    return MATRIX_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(long[][])
   */
  public static LongArray array(long[][] data) {
    return MATRIX_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#booleanArray(int...)
   */
  public static BitArray booleanArray(int... shape) {
    return MATRIX_FACTORY.booleanArray(shape);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(boolean[])
   */
  public static BitArray array(boolean[] data) {
    return MATRIX_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#array(boolean[][])
   */
  public static BitArray array(boolean[][] data) {
    return MATRIX_FACTORY.array(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayFactory#diag(org.briljantframework.array.BaseArray)
   */
  public static <T extends BaseArray<T>> T diag(T data) {
    return MATRIX_FACTORY.diag(data);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#mean(org.briljantframework.array.DoubleArray)
   */
  public static double mean(DoubleArray x) {
    return MATRIX_ROUTINES.mean(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#mean(int, org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray mean(int dim, DoubleArray x) {
    return MATRIX_ROUTINES.mean(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#var(org.briljantframework.array.DoubleArray)
   */
  public static double var(DoubleArray x) {
    return MATRIX_ROUTINES.var(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#var(int, org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray var(int dim, DoubleArray x) {
    return MATRIX_ROUTINES.var(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#std(org.briljantframework.array.DoubleArray)
   */
  public static double std(DoubleArray x) {
    return MATRIX_ROUTINES.std(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#std(int, org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray std(int dim, DoubleArray x) {
    return MATRIX_ROUTINES.std(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sum(org.briljantframework.array.IntArray)
   */
  public static int sum(IntArray x) {
    return MATRIX_ROUTINES.sum(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sum(int, org.briljantframework.array.IntArray)
   */
  public static IntArray sum(int dim, IntArray x) {
    return MATRIX_ROUTINES.sum(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sum(org.briljantframework.array.DoubleArray)
   */
  public static double sum(DoubleArray x) {
    return MATRIX_ROUTINES.sum(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sum(int, org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray sum(int dim, DoubleArray x) {
    return MATRIX_ROUTINES.sum(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#prod(org.briljantframework.array.DoubleArray)
   */
  public static double prod(DoubleArray x) {
    return MATRIX_ROUTINES.prod(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#prod(int, org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray prod(int dim, DoubleArray x) {
    return MATRIX_ROUTINES.prod(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(org.briljantframework.array.DoubleArray)
   */
  public static double min(DoubleArray x) {
    return MATRIX_ROUTINES.min(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(int, org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray min(int dim, DoubleArray x) {
    return MATRIX_ROUTINES.min(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(org.briljantframework.array.IntArray)
   */
  public static int min(IntArray x) {
    return MATRIX_ROUTINES.min(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(int, org.briljantframework.array.IntArray)
   */
  public static IntArray min(int dim, IntArray x) {
    return MATRIX_ROUTINES.min(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(org.briljantframework.array.LongArray)
   */
  public static long min(LongArray x) {
    return MATRIX_ROUTINES.min(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(int, org.briljantframework.array.LongArray)
   */
  public static LongArray min(int dim, LongArray x) {
    return MATRIX_ROUTINES.min(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(org.briljantframework.array.Array,
   * java.util.Comparator)
   */
  public static <T> T min(Array<T> x, Comparator<T> cmp) {
    return MATRIX_ROUTINES.min(x, cmp);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(int, org.briljantframework.array.Array,
   * java.util.Comparator)
   */
  public static <T> Array<T> min(int dim, Array<T> x, Comparator<T> cmp) {
    return MATRIX_ROUTINES.min(dim, x, cmp);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(org.briljantframework.array.Array)
   */
  public static <T extends Comparable<T>> T min(Array<T> x) {
    return MATRIX_ROUTINES.min(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#min(int, org.briljantframework.array.Array)
   */
  public static <T extends Comparable<T>> Array<T> min(int dim, Array<T> x) {
    return MATRIX_ROUTINES.min(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(org.briljantframework.array.DoubleArray)
   */
  public static double max(DoubleArray x) {
    return MATRIX_ROUTINES.max(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(int, org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray max(int dim, DoubleArray x) {
    return MATRIX_ROUTINES.max(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(org.briljantframework.array.IntArray)
   */
  public static int max(IntArray x) {
    return MATRIX_ROUTINES.max(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(int, org.briljantframework.array.IntArray)
   */
  public static IntArray max(int dim, IntArray x) {
    return MATRIX_ROUTINES.max(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(org.briljantframework.array.LongArray)
   */
  public static long max(LongArray x) {
    return MATRIX_ROUTINES.max(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(int, org.briljantframework.array.LongArray)
   */
  public static LongArray max(int dim, LongArray x) {
    return MATRIX_ROUTINES.max(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(org.briljantframework.array.Array,
   * java.util.Comparator)
   */
  public static <T> T max(Array<T> x, Comparator<T> cmp) {
    return MATRIX_ROUTINES.max(x, cmp);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(int, org.briljantframework.array.Array,
   * java.util.Comparator)
   */
  public static <T> Array<T> max(int dim, Array<T> x, Comparator<T> cmp) {
    return MATRIX_ROUTINES.max(dim, x, cmp);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(org.briljantframework.array.Array)
   */
  public static <T extends Comparable<T>> T max(Array<T> x) {
    return MATRIX_ROUTINES.max(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#max(int, org.briljantframework.array.Array)
   */
  public static <T extends Comparable<T>> Array<T> max(int dim, Array<T> x) {
    return MATRIX_ROUTINES.max(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#norm2(org.briljantframework.array.DoubleArray)
   */
  public static double norm2(DoubleArray a) {
    return MATRIX_ROUTINES.norm2(a);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#norm2(org.briljantframework.array.ComplexArray)
   */
  public static Complex norm2(ComplexArray a) {
    return MATRIX_ROUTINES.norm2(a);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#asum(org.briljantframework.array.DoubleArray)
   */
  public static double asum(DoubleArray a) {
    return MATRIX_ROUTINES.asum(a);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#asum(org.briljantframework.array.ComplexArray)
   */
  public static double asum(ComplexArray a) {
    return MATRIX_ROUTINES.asum(a);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#iamax(org.briljantframework.array.DoubleArray)
   */
  public static int iamax(DoubleArray x) {
    return MATRIX_ROUTINES.iamax(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#iamax(org.briljantframework.array.ComplexArray)
   */
  public static int iamax(ComplexArray x) {
    return MATRIX_ROUTINES.iamax(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#cumsum(org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray cumsum(DoubleArray x) {
    return MATRIX_ROUTINES.cumsum(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#cumsum(int, org.briljantframework.array.DoubleArray)
   */
  public static DoubleArray cumsum(int dim, DoubleArray x) {
    return MATRIX_ROUTINES.cumsum(dim, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#trace(org.briljantframework.array.DoubleArray)
   */
  public static double trace(DoubleArray x) {
    return MATRIX_ROUTINES.trace(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#vsplit(org.briljantframework.array.BaseArray,
   * int)
   */
  public static <T extends BaseArray<T>> List<T> vsplit(T array, int parts) {
    return MATRIX_ROUTINES.vsplit(array, parts);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#vstack(java.util.Collection)
   */
  public static <T extends BaseArray<T>> T vstack(Collection<T> arrays) {
    return MATRIX_ROUTINES.vstack(arrays);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#hsplit(org.briljantframework.array.BaseArray,
   * int)
   */
  public static <T extends BaseArray<T>> List<T> hsplit(T matrix, int parts) {
    return MATRIX_ROUTINES.hsplit(matrix, parts);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#hstack(java.util.Collection)
   */
  public static <T extends BaseArray<T>> T hstack(Collection<T> matrices) {
    return MATRIX_ROUTINES.hstack(matrices);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#copy(org.briljantframework.array.BaseArray,
   * org.briljantframework.array.BaseArray)
   */
  public static <T extends BaseArray<T>> void copy(T from, T to) {
    MATRIX_ROUTINES.copy(from, to);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#swap(org.briljantframework.array.BaseArray,
   * org.briljantframework.array.BaseArray)
   */
  public static <T extends BaseArray<T>> void swap(T a, T b) {
    MATRIX_ROUTINES.swap(a, b);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#take(org.briljantframework.array.BaseArray,
   * int)
   */
  public static <T extends BaseArray<T>> T take(T x, int num) {
    return MATRIX_ROUTINES.take(x, num);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#repmat(org.briljantframework.array.BaseArray,
   * int, int)
   */
  public static <T extends BaseArray<T>> T repmat(T x, int r, int c) {
    return MATRIX_ROUTINES.repmat(x, r, c);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#repmat(org.briljantframework.array.BaseArray,
   * int)
   */
  public static <T extends BaseArray<T>> T repmat(T x, int n) {
    return MATRIX_ROUTINES.repmat(x, n);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#repeat(org.briljantframework.array.BaseArray,
   * int)
   */
  public static <T extends BaseArray<T>> T repeat(T x, int num) {
    return MATRIX_ROUTINES.repeat(x, num);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sort(org.briljantframework.array.Array)
   */
  public static <T extends Comparable<T>> Array<T> sort(Array<T> array) {
    return MATRIX_ROUTINES.sort(array);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sort(int, org.briljantframework.array.Array)
   */
  public static <T extends Comparable<T>> Array<T> sort(int dim, Array<T> array) {
    return MATRIX_ROUTINES.sort(dim, array);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sort(org.briljantframework.array.Array,
   * java.util.Comparator)
   */
  public static <T> Array<T> sort(Array<T> array, Comparator<T> cmp) {
    return MATRIX_ROUTINES.sort(array, cmp);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sort(int, org.briljantframework.array.Array,
   * java.util.Comparator)
   */
  public static <T> Array<T> sort(int dim, Array<T> array,
                                  Comparator<T> cmp) {
    return MATRIX_ROUTINES.sort(dim, array, cmp);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sort(org.briljantframework.array.BaseArray)
   */
  public static <T extends BaseArray<T>> T sort(T array) {
    return MATRIX_ROUTINES.sort(array);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sort(int, org.briljantframework.array.BaseArray)
   */
  public static <T extends BaseArray<T>> T sort(int dim, T array) {
    return MATRIX_ROUTINES.sort(dim, array);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sort(org.briljantframework.array.BaseArray,
   * org.briljantframework.sort.IndexComparator)
   */
  public static <T extends BaseArray<T>> T sort(T x, IndexComparator<T> cmp) {
    return MATRIX_ROUTINES.sort(x, cmp);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#sort(int, org.briljantframework.array.BaseArray,
   * org.briljantframework.sort.IndexComparator)
   */
  public static <T extends BaseArray<T>> T sort(int dim, T x, IndexComparator<T> cmp) {
    return MATRIX_ROUTINES.sort(dim, x, cmp);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#shuffle(org.briljantframework.array.BaseArray)
   */
  public static <T extends BaseArray<T>> T shuffle(T x) {
    return MATRIX_ROUTINES.shuffle(x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#dot(org.briljantframework.array.DoubleArray,
   * org.briljantframework.array.DoubleArray)
   */
  public static double dot(DoubleArray a, DoubleArray b) {
    return MATRIX_ROUTINES.dot(a, b);
  }

  /**
   * Compute the inner product of two arrays. If the arguments are {@code vectors}, the result
   * is equivalent to {@linkplain #dot(org.briljantframework.array.DoubleArray,
   * org.briljantframework.array.DoubleArray)}. In other cases, the arguments are raveled.
   * <p>Example
   * <pre>{@code
   * > Bj.inner(Bj.linspace(0,3,4), Bj.linspace(0,3,4).reshape(2, 2))
   * 14.0
   * }</pre>
   *
   * @param a the first array
   * @param b the second array
   * @return the inner product
   */
  public static double inner(DoubleArray a, DoubleArray b) {
    a = a.isVector() ? a : a.ravel();
    b = b.isVector() ? b : b.ravel();
    return dot(a, b);
  }

  /**
   * Computes the outer product of two arrays. If the arguments are {@code vectors}, the result is
   * equivalent to {@link #ger(double, org.briljantframework.array.DoubleArray,
   * org.briljantframework.array.DoubleArray, org.briljantframework.array.DoubleArray)}. In other
   * cases, the arguments are raveled.
   *
   * <p>Example
   * <pre>{@code
   * Bj.outer(Bj.linspace(0, 3, 4), Bj.linspace(0,3,4).reshape(2,2))
   * array([[0.000, 0.000, 0.000, 0.000],
   *        [0.000, 1.000, 2.000, 3.000],
   *        [0.000, 2.000, 4.000, 6.000],
   *        [0.000, 3.000, 6.000, 9.000]] type: double)
   * }</pre>
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
   * @see org.briljantframework.array.api.ArrayRoutines#dotc(org.briljantframework.array.ComplexArray,
   * org.briljantframework.array.ComplexArray)
   */
  public static Complex dotc(ComplexArray a, ComplexArray b) {
    return MATRIX_ROUTINES.dotc(a, b);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#dotu(org.briljantframework.array.ComplexArray,
   * org.briljantframework.array.ComplexArray)
   */
  public static Complex dotu(ComplexArray a, ComplexArray b) {
    return MATRIX_ROUTINES.dotu(a, b);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#scal(double, org.briljantframework.array.DoubleArray)
   */
  public static void scal(double alpha, DoubleArray x) {
    MATRIX_ROUTINES.scal(alpha, x);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#axpy(double, org.briljantframework.array.DoubleArray,
   * org.briljantframework.array.DoubleArray)
   */
  public static void axpy(double alpha, DoubleArray x, DoubleArray y) {
    MATRIX_ROUTINES.axpy(alpha, x, y);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#ger(double, org.briljantframework.array.DoubleArray,
   * org.briljantframework.array.DoubleArray, org.briljantframework.array.DoubleArray)
   */
  public static void ger(double alpha, DoubleArray x, DoubleArray y,
                         DoubleArray a) {
    MATRIX_ROUTINES.ger(alpha, x, y, a);
  }

  /**
   * @see org.briljantframework.array.api.ArrayRoutines#gemm(org.briljantframework.array.Op,
   * org.briljantframework.array.Op, double, org.briljantframework.array.DoubleArray,
   * org.briljantframework.array.DoubleArray, double, org.briljantframework.array.DoubleArray)
   */
  public static void gemv(Op transA, double alpha, DoubleArray a, DoubleArray x, double beta,
                          DoubleArray y) {
    MATRIX_ROUTINES.gemv(transA, alpha, a, x, beta, y);
  }

  /**
   * Delegates to {@link #gemv(org.briljantframework.array.Op, double,
   * org.briljantframework.array.DoubleArray, org.briljantframework.array.DoubleArray, double,
   * org.briljantframework.array.DoubleArray)}
   * with the first argument {@code Op.KEEP}.
   *
   * @see #gemv(org.briljantframework.array.Op, double, org.briljantframework.array.DoubleArray,
   * org.briljantframework.array.DoubleArray, double, org.briljantframework.array.DoubleArray)
   */
  public static void gemv(double alpha, DoubleArray a, DoubleArray x, double beta,
                          DoubleArray y) {
    gemv(Op.KEEP, alpha, a, x, beta, y);
  }

  /**
   * Delegates to {@link #gemv(org.briljantframework.array.Op, double,
   * org.briljantframework.array.DoubleArray, org.briljantframework.array.DoubleArray, double,
   * org.briljantframework.array.DoubleArray)}
   * with the first argument {@code Op.KEEP}, {@code alpha = 1} and {@code beta = 1}
   *
   * @see #gemv(org.briljantframework.array.Op, double, org.briljantframework.array.DoubleArray,
   * org.briljantframework.array.DoubleArray, double, org.briljantframework.array.DoubleArray)
   */
  public static void gemv(DoubleArray a, DoubleArray x, DoubleArray y) {
    gemv(1, a, x, 1, y);
  }

  public static void gemm(Op transA, Op transB, double alpha, DoubleArray a,
                          DoubleArray b, double beta, DoubleArray c) {
    MATRIX_ROUTINES.gemm(transA, transB, alpha, a, b, beta, c);
  }

  public static void gemm(double alpha, DoubleArray a,
                          DoubleArray b, double beta, DoubleArray c) {
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
   * @param array   the source array
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
   * and the
   * values in {@code values}. The value at {@code i} in array copy of {@code array} is set to
   * value
   * at
   * {@code i} from {@code values} if the boolean at {@code i} in {@code mask} is {@code true}.
   * </p>
   *
   * @param array  array source array
   * @param mask   the mask; same shape as {@code array}
   * @param values the values; same shape as {@code array}
   * @return a new array; the returned array has the same type as {@code array}.
   */
  public static <T extends BaseArray<T>> T mask(T array, BitArray mask, T values) {
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
   * @param a      the target matrix
   * @param mask   the mask; same shape as {@code a}
   * @param values the mask; same shape as {@code a}
   */
  public static <T extends BaseArray<T>> void putMask(T a, BitArray mask, T values) {
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
   * @param a       the source matrix
   * @param where   the selection matrix; same shape as {@code a}
   * @param replace the replacement value
   * @return a new matrix; the returned matrix has the same type as {@code a}.
   */
  public static IntArray select(IntArray a, BitArray where, int replace) {
    Check.shape(a, where);
    return a.copy().assign(where, (b, i) -> b ? replace : i);
  }

  public static DoubleArray cos(ComplexArray array) {
    return MATRIX_ROUTINES.abs(array);
  }

  public static DoubleArray sqrt(DoubleArray array) {
    return MATRIX_ROUTINES.sqrt(array);
  }

  public static DoubleArray pow(DoubleArray in, double power) {
    return MATRIX_ROUTINES.pow(in, power);
  }

  public static DoubleArray log2(DoubleArray array) {
    return MATRIX_ROUTINES.log2(array);
  }

  public static DoubleArray acos(DoubleArray array) {
    return MATRIX_ROUTINES.acos(array);
  }

  public static DoubleArray cosh(DoubleArray array) {
    return MATRIX_ROUTINES.cosh(array);
  }

  public static DoubleArray signum(DoubleArray in) {
    return MATRIX_ROUTINES.signum(in);
  }

  public static DoubleArray cos(DoubleArray array) {
    return MATRIX_ROUTINES.cos(array);
  }

  public static DoubleArray asin(DoubleArray array) {
    return MATRIX_ROUTINES.asin(array);
  }

  public static LongArray abs(LongArray array) {
    return MATRIX_ROUTINES.abs(array);
  }

  public static DoubleArray cbrt(DoubleArray array) {
    return MATRIX_ROUTINES.cbrt(array);
  }

  public static DoubleArray abs(DoubleArray array) {
    return MATRIX_ROUTINES.abs(array);
  }

  public static DoubleArray ceil(DoubleArray array) {
    return MATRIX_ROUTINES.ceil(array);
  }

  public static DoubleArray sinh(DoubleArray array) {
    return MATRIX_ROUTINES.sinh(array);
  }

  public static DoubleArray log(DoubleArray array) {
    return MATRIX_ROUTINES.log(array);
  }

  public static DoubleArray tanh(DoubleArray array) {
    return MATRIX_ROUTINES.tanh(array);
  }

  public static DoubleArray sin(DoubleArray array) {
    return MATRIX_ROUTINES.sin(array);
  }

  public static DoubleArray scalb(DoubleArray array, int scaleFactor) {
    return MATRIX_ROUTINES.scalb(array, scaleFactor);
  }

  public static DoubleArray exp(DoubleArray array) {
    return MATRIX_ROUTINES.exp(array);
  }

  public static DoubleArray log10(DoubleArray in) {
    return MATRIX_ROUTINES.log10(in);
  }

  public static DoubleArray floor(DoubleArray array) {
    return MATRIX_ROUTINES.floor(array);
  }

  public static DoubleArray tan(DoubleArray array) {
    return MATRIX_ROUTINES.tan(array);
  }

  public static IntArray abs(IntArray array) {
    return MATRIX_ROUTINES.abs(array);
  }

  public static LongArray round(DoubleArray in) {
    return MATRIX_ROUTINES.round(in);
  }

  public static DoubleArray atan(DoubleArray array) {
    return MATRIX_ROUTINES.atan(array);
  }

  public static ComplexArray sinh(ComplexArray array) {
    return MATRIX_ROUTINES.sinh(array);
  }

  public static ComplexArray exp(ComplexArray array) {
    return MATRIX_ROUTINES.exp(array);
  }

  public static ComplexArray acos(ComplexArray array) {
    return MATRIX_ROUTINES.acos(array);
  }

  public static ComplexArray sin(ComplexArray array) {
    return MATRIX_ROUTINES.sin(array);
  }

  public static DoubleArray abs(ComplexArray array) {
    return MATRIX_ROUTINES.abs(array);
  }

  public static ComplexArray sqrt(ComplexArray array) {
    return MATRIX_ROUTINES.sqrt(array);
  }

  public static ComplexArray log(ComplexArray array) {
    return MATRIX_ROUTINES.log(array);
  }

  public static ComplexArray floor(ComplexArray array) {
    return MATRIX_ROUTINES.floor(array);
  }

  public static ComplexArray tan(ComplexArray array) {
    return MATRIX_ROUTINES.tan(array);
  }

  public static ComplexArray tanh(ComplexArray array) {
    return MATRIX_ROUTINES.tanh(array);
  }

  public static ComplexArray asin(ComplexArray array) {
    return MATRIX_ROUTINES.asin(array);
  }

  public static ComplexArray cosh(ComplexArray array) {
    return MATRIX_ROUTINES.cosh(array);
  }

  public static ComplexArray atan(ComplexArray array) {
    return MATRIX_ROUTINES.atan(array);
  }

  public static ComplexArray ceil(ComplexArray array) {
    return MATRIX_ROUTINES.ceil(array);
  }
}
