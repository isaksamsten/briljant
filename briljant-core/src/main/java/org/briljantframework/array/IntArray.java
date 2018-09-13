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
import java.util.List;
import java.util.function.*;
import java.util.stream.IntStream;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.IntVector;

import net.mintern.primitive.comparators.IntComparator;

/**
 * A n-dimensional array of integer values.
 * 
 * @author Isak Karlsson
 */
public interface IntArray
    extends NumberArray, BaseArray<IntArray>, Collection<Integer>, IntVector {

  static IntArray ones(int... shape) {
    IntArray array = zeros(shape);
    array.assign(1);
    return array;
  }

  static IntArray zeros(int... shape) {
    return Arrays.intArray(shape);
  }

  /**
   * @see Arrays#intVector(int...)
   */
  static IntArray of(int... data) {
    return Arrays.intVector(data);
  }

  /**
   * Returns a new int array consisting of the given elements, in order.
   * 
   * @param elements the elements
   * @return a new array
   */
  static IntArray copyOf(List<Integer> elements) {
    IntArray a = zeros(elements.size());
    for (int i = 0; i < elements.size(); i++) {
      a.set(i, elements.get(i));
    }
    return a;
  }

  /**
   * Assign {@code value} to {@code this}
   *
   * @param value the value to assign
   */
  void assign(int value);

  void assign(int[] data);

  /**
   * Assign value returned by {@link #size()} successive calls to
   * {@link java.util.function.IntSupplier#getAsInt()}
   *
   * @param supplier the supplier
   * @return receiver modified
   */
  void assign(IntSupplier supplier);

  /**
   * Assign {@code array} to {@code this}, applying {@code operator} to each value of {@code other}.
   *
   * @param operator the operator
   * @return receiver modified
   */
  void assign(IntArray array, IntUnaryOperator operator);

  /**
   * Assign {@code array} to {@code this}, applying {@code combine} to combine the i:th value of
   * {@code this} and {@code array}
   *
   * @param array the matrix
   * @param combine the combiner
   * @return receiver modified
   */
  void combineAssign(IntArray array, IntBinaryOperator combine);

  void assign(ComplexArray array, ToIntFunction<? super Complex> function);

  void assign(DoubleArray array, DoubleToIntFunction function);

  void assign(LongArray array, LongToIntFunction operator);

  void assign(BooleanArray array, ToIntFunction<Boolean> function);


  // Transform

  /**
   * Apply the operator to each element of this array.
   *
   * <pre>
   * {@code
   * x.apply(v -> v + 1) // add one to each element
   * }
   * </pre>
   *
   * @param operator the operator to apply
   */
  void apply(IntUnaryOperator operator);

  /**
   * Return a copy of this array, with the operator applied to each element of the new array.
   *
   * <pre>
   * {@code
   * x.map(v -> v + 1) // new array with 1 added to each element
   * }
   * </pre>
   * 
   * @param operator the operator to apply to each element
   * @return a new matrix
   */
  IntArray map(IntUnaryOperator operator);

  /**
   * Return a copy of this array with each element converted to a {@code long}
   *
   * <pre>
   * {@code
   * x.mapToLong(v -> Long.valueOf(v))
   * }
   * </pre>
   * 
   * @param function
   * @return
   */
  LongArray mapToLong(IntToLongFunction function);

  DoubleArray mapToDouble(IntToDoubleFunction function);

  ComplexArray mapToComplex(IntFunction<Complex> function);

  <U> Array<U> mapToObj(IntFunction<? extends U> function);

  // Filter

  IntArray filter(IntPredicate operator);

  BooleanArray where(IntPredicate predicate);

  void forEachPrimitive(IntConsumer consumer);

  int reduce(int identity, IntBinaryOperator reduce);

  /**
   * Reduces {@code this} into a real value. For example, summing can be implemented as
   * {@code matrix.reduce(0, (a, sumSoFar) -> a + sumSoFar, x -> x)}
   *
   * <p>
   * The operation {@code reduce} takes two parameters the current value and the accumulated value
   * (set to {@code identity} at the first iteration)
   *
   * @param identity the initial value
   * @param reduce takes two values and reduces them to one
   * @param map takes a value and possibly transforms it
   * @return the result
   */
  int reduce(int identity, IntBinaryOperator reduce, IntUnaryOperator map);

  /**
   * Reduce this array along the vectors of the specified dimension.
   *
   * @param dim
   * @param accumulator
   * @return
   */
  IntArray reduceVectors(int dim, ToIntFunction<? super IntArray> accumulator);

  // GET / SET

  /**
   * {@inheritDoc}
   *
   * @see #get(int)
   * @param index the index
   * @return the value
   */
  @Override
  default int getInt(int index) {
    return get(index);
  }

  /**
   * For nd-arrays, get the value at the specified index. For {@code n > 1}, return the value at the
   * ravel position.
   *
   * @param index the index
   * @return the value
   */
  int get(int index);

  /**
   * For nd-arrays, set the value at the specified index. For {@code n > 1}, set the value at the
   * ravel position.
   *
   * @param index the index
   * @param value the value
   */
  void set(int index, int value);

  /**
   * For 2d-arrays, get the value at the specified row and column.
   *
   * @param row row
   * @param column column
   * @return the value
   */
  int get(int row, int column);

  /**
   * For 2d-arrays, set the value at the specified row and column.
   *
   * @param row the row
   * @param column the column
   * @param value the value
   */
  void set(int row, int column, int value);

  /**
   * For nd-arrays, get the value at the specified index.
   *
   * @param index the index
   * @return the value
   */
  int get(int... index);

  /**
   * For nd-arrays, set the value at the specified index.
   *
   * @param index the index
   * @param value the value
   */
  void set(int[] index, int value);

  /**
   * Sort the array in ascending order.
   */
  void sort();

  /**
   * Sort the array according to the given comparator.
   *
   * @param cmp the comparator
   */
  void sort(IntComparator cmp);

  /**
   * Returns a new array with elements negated.
   *
   * @return a new matrix
   */
  IntArray negate();

  Array<Integer> boxed();

  IntStream intStream();

}
