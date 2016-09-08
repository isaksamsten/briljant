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

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.*;
import java.util.stream.IntStream;

import net.mintern.primitive.comparators.IntComparator;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.function.IntBiPredicate;

/**
 * Special indexer for inserting new dimensions and selecting everything along a specified dimension
 * akin to {@code ..., : and np.newaxis} in <a href="http://numpy.org">Numpy</a>. All methods throw
 * an exception.
 * 
 * <p/>
 * Example
 * 
 * <pre>
 * import static org.briljantframework.array.BasicIndex.*;
 * import org.briljantframework.array.*;
 * 
 * class Test {
 *   public static void main(String[] args) {
 *     IntArray x = Range.of(5 * 10).reshape(5, 10);
 *     x.get(__, IntArray.of(0, 0, 1, 1).reshape(2, 2));
 *     x.get(ALL, IntArray.of(0, 0));
 *     x.get(IntArray.of(0, 4).reshape(2, 1), all);
 *   }
 * }
 * </pre>
 * 
 * which would produce
 * 
 * <pre>
 * array([[[0, 5],
 *         [0, 5]],
 * 
 *        [[1, 6],
 *         [1, 6]],
 * 
 *        [[2, 7],
 *         [2, 7]],
 * 
 *        [[3, 8],
 *         [3, 8]],
 * 
 *        [[4, 9],
 *         [4, 9]]])
 * </pre>
 * 
 * ,
 * 
 * <pre>
 *   array([[0, 0],
 *          [1, 1],
 *          [2, 2],
 *          [3, 3],
 *          [4, 4]])
 * </pre>
 *
 * and
 * 
 * <pre>
 * array([[[0, 5, 10, 15, 20, 25, 30, 35, 40, 45]],
 * 
 *        [[4, 9, 14, 19, 24, 29, 34, 39, 44, 49]]])
 * </pre>
 * 
 * {@code ALL}, {@code all} and {@code __} all indicates the same thing.
 * 
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public final class BasicIndex implements Range {

  /**
   * Special selector that selects everything along the indicated dimension
   */
  public static final BasicIndex ALL = new BasicIndex();

  /**
   * @see #ALL
   */
  public static final BasicIndex all = ALL;

  /**
   * @see #ALL
   */
  public static final BasicIndex __ = ALL;

  /**
   * Special selector that inserts a new dimension of size 1 along the indicated dimension.
   */
  public static final BasicIndex NEW_DIMENSION = null;

  /**
   * @see #NEW_DIMENSION
   */
  public static final BasicIndex newdim = NEW_DIMENSION;

  private BasicIndex() {}

  @Override
  public int step() {
    return 1;
  }

  @Override
  public int start() {
    return 0;
  }

  @Override
  public int end() {
    throw unsupported();
  }

  private UnsupportedOperationException unsupported() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void assign(int value) {
    throw unsupported();
  }

  @Override
  public void assign(int[] data) {
    throw unsupported();
  }

  @Override
  public void assign(IntSupplier supplier) {
    throw unsupported();
  }

  @Override
  public void assign(IntArray array, IntUnaryOperator operator) {
    throw unsupported();
  }

  @Override
  public void combineAssign(IntArray array, IntBinaryOperator combine) {
    throw unsupported();
  }

  @Override
  public void assign(ComplexArray array, ToIntFunction<? super Complex> function) {
    throw unsupported();
  }

  @Override
  public void assign(DoubleArray array, DoubleToIntFunction function) {
    throw unsupported();
  }

  @Override
  public void assign(LongArray array, LongToIntFunction operator) {
    throw unsupported();
  }

  @Override
  public void assign(BooleanArray array, ToIntFunction<Boolean> function) {
    throw unsupported();
  }

  @Override
  public void apply(IntUnaryOperator operator) {
    throw unsupported();
  }

  @Override
  public IntArray map(IntUnaryOperator operator) {
    throw unsupported();
  }

  @Override
  public LongArray mapToLong(IntToLongFunction function) {
    throw unsupported();
  }

  @Override
  public DoubleArray mapToDouble(IntToDoubleFunction function) {
    throw unsupported();
  }

  @Override
  public ComplexArray mapToComplex(IntFunction<Complex> function) {
    throw unsupported();
  }

  @Override
  public <U> Array<U> mapToObj(IntFunction<? extends U> function) {
    throw unsupported();
  }

  @Override
  public IntArray filter(IntPredicate operator) {
    throw unsupported();
  }

  @Override
  public BooleanArray where(IntPredicate predicate) {
    throw unsupported();
  }

  @Override
  public BooleanArray where(IntArray array, IntBiPredicate predicate) {
    throw unsupported();
  }

  @Override
  public void forEachPrimitive(IntConsumer consumer) {
    throw unsupported();
  }

  @Override
  public int reduce(int identity, IntBinaryOperator reduce) {
    throw unsupported();
  }

  @Override
  public int reduce(int identity, IntBinaryOperator reduce, IntUnaryOperator map) {
    throw unsupported();
  }

  @Override
  public IntArray reduceVectors(int dim, ToIntFunction<? super IntArray> accumulator) {
    throw unsupported();
  }

  @Override
  public void set(int index, int value) {
    throw unsupported();
  }

  @Override
  public int get(int row, int column) {
    throw unsupported();
  }

  @Override
  public void set(int row, int column, int value) {
    throw unsupported();
  }

  @Override
  public void set(int[] index, int value) {
    throw unsupported();
  }

  @Override
  public int get(int... index) {
    throw unsupported();
  }

  @Override
  public int get(int index) {
    throw unsupported();
  }

  @Override
  public IntStream intStream() {
    throw unsupported();
  }

  @Override
  public Array<Integer> boxed() {
    throw unsupported();
  }

  @Override
  public void sort() {
    throw unsupported();
  }

  @Override
  public void sort(IntComparator cmp) {
    throw unsupported();
  }

  @Override
  public IntArray negate() {
    throw unsupported();
  }

  @Override
  public void setFrom(int toIndex, IntArray from, int fromIndex) {
    throw unsupported();
  }

  @Override
  public void setFrom(int toRow, int toColumn, IntArray from, int fromRow, int fromColumn) {
    throw unsupported();
  }

  @Override
  public void setFrom(int[] toIndex, IntArray from, int[] fromIndex) {
    throw unsupported();
  }

  @Override
  public void setFrom(int[] toIndex, IntArray from, int fromIndex) {
    throw unsupported();
  }

  @Override
  public void setFrom(int toIndex, IntArray from, int[] fromIndex) {
    throw unsupported();
  }

  @Override
  public IntArray reverse() {
    throw unsupported();
  }

  @Override
  public void assign(IntArray o) {
    throw unsupported();
  }

  @Override
  public void forEach(int dim, Consumer<IntArray> consumer) {
    throw unsupported();
  }

  @Override
  public void setColumn(int i, IntArray vec) {
    throw unsupported();
  }

  @Override
  public IntArray getColumn(int index) {
    throw unsupported();
  }

  @Override
  public void setRow(int i, IntArray vec) {
    throw unsupported();
  }

  @Override
  public IntArray getRow(int i) {
    throw unsupported();
  }

  @Override
  public IntArray reshape(int... shape) {
    throw unsupported();
  }

  @Override
  public IntArray ravel() {
    return this;
  }

  @Override
  public IntArray select(int index) {
    throw unsupported();
  }

  @Override
  public IntArray select(int dimension, int index) {
    throw unsupported();
  }

  @Override
  public IntArray getView(Range... indexers) {
    throw unsupported();
  }

  @Override
  public IntArray getView(List<? extends Range> ranges) {
    throw unsupported();
  }

  @Override
  public IntArray getVector(int dimension, int index) {
    throw unsupported();
  }

  @Override
  public void setVector(int dimension, int index, IntArray other) {
    throw unsupported();
  }

  @Override
  public IntArray getDiagonal() {
    throw unsupported();
  }

  @Override
  public IntArray get(IntArray... arrays) {
    throw unsupported();
  }

  @Override
  public IntArray get(List<? extends IntArray> arrays) {
    throw unsupported();
  }

  @Override
  public void set(List<? extends IntArray> arrays, IntArray value) {
    throw unsupported();
  }

  @Override
  public IntArray getView(int rowOffset, int colOffset, int rows, int columns) {
    throw unsupported();
  }

  @Override
  public int size() {
    throw unsupported();
  }

  @Override public boolean isEmpty() {
    throw unsupported();
  }

  @Override public boolean contains(Object o) {
    throw unsupported();
  }

  @Override
  public int size(int dim) {
    throw unsupported();
  }

  @Override
  public int vectors(int i) {
    throw unsupported();
  }

  @Override
  public int stride(int i) {
    throw unsupported();
  }

  @Override
  public int getOffset() {
    throw unsupported();
  }

  @Override
  public int[] getShape() {
    throw unsupported();
  }

  @Override
  public int[] getStride() {
    throw unsupported();
  }

  public int getMajorStride() {
    throw unsupported();
  }

  @Override
  public int rows() {
    throw unsupported();
  }

  @Override
  public int columns() {
    throw unsupported();
  }

  @Override
  public int dims() {
    return 0;
  }

  @Override
  public boolean isVector() {
    return false;
  }

  @Override
  public boolean isMatrix() {
    return false;
  }

  @Override
  public IntArray asView(int[] shape, int[] stride) {
    throw unsupported();
  }

  @Override
  public IntArray asView(int offset, int[] shape, int[] stride) {
    throw unsupported();
  }

  @Override
  public IntArray newEmptyArray(int... shape) {
    throw unsupported();
  }

  @Override
  public boolean isView() {
    throw unsupported();
  }

  @Override
  public DoubleArray doubleArray() {
    throw unsupported();
  }

  @Override
  public IntArray intArray() {
    return this;
  }

  @Override
  public LongArray longArray() {
    throw unsupported();
  }

  @Override
  public ComplexArray complexArray() {
    throw unsupported();
  }

  @Override
  public boolean isContiguous() {
    throw unsupported();
  }

  @Override
  public IntArray transpose() {
    return this;
  }

  @Override
  public IntArray copy() {
    return this;
  }

  @Override
  public Iterator<Integer> iterator() {
    throw unsupported();
  }

  @Override public Object[] toArray() {
    throw unsupported();
  }

  @Override public <T> T[] toArray(T[] a) {
    throw unsupported();
  }

  @Override public boolean add(Integer o) {
    throw unsupported();
  }

  @Override public boolean remove(Object o) {
    throw unsupported();
  }

  @Override public boolean containsAll(Collection<?> c) {
    throw unsupported();
  }

  @Override public boolean addAll(Collection<? extends Integer> c) {
    throw unsupported();
  }

  @Override public boolean removeAll(Collection<?> c) {
    throw unsupported();
  }

  @Override public boolean retainAll(Collection<?> c) {
    throw unsupported();
  }

  @Override public void clear() {
    throw unsupported();
  }

  @Override
  public void swap(int a, int b) {
    throw unsupported();
  }
}
