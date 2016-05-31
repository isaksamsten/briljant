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

import java.util.List;
import java.util.function.*;
import java.util.stream.LongStream;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.function.LongBiPredicate;

import net.mintern.primitive.comparators.LongComparator;

/**
 * A n-dimensional array of long values.
 * 
 * @author Isak Karlsson
 */
public interface LongArray extends NumberArray, BaseArray<LongArray>, Iterable<Long> {

  static LongArray ones(int... shape) {
    LongArray array = zeros(shape);
    array.assign(1);
    return array;
  }

  static LongArray zeros(int... shape) {
    return Arrays.longArray(shape);
  }

  /**
   * @see Arrays#longVector(long...)
   */
  static LongArray of(long... data) {
    return Arrays.longVector(data);
  }

  // Assignments

  /**
   * Assign {@code value} to {@code this}
   *
   * @param value the value to assign
   * @return receiver modified
   */
  LongArray assign(long value);

  void assign(long[] values);

  /**
   * Assign value returned by {@link #size()} successive calls to
   * {@link java.util.function.LongSupplier#getAsLong()}
   *
   * @param supplier the supplier
   * @return receiver modified
   */
  void assign(LongSupplier supplier);

  /**
   * Assign {@code matrix} to {@code this}, applying {@code operator} to each value.
   *
   * @param array the matrix
   * @param operator the operator
   * @return receiver modified
   */
  void assign(LongArray array, LongUnaryOperator operator);

  void combineAssign(LongArray array, LongBinaryOperator combine);

  void assign(ComplexArray array, ToLongFunction<? super Complex> function);

  void assign(IntArray array, IntToLongFunction operator);

  void assign(DoubleArray array, DoubleToLongFunction function);

  // Transform

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * For example, {@code m.map(Math::sqrt)} is equal to
   *
   * <pre>
   *     Matrix n = m.copy();
   *     for(long i = 0; i < n.size(); i++)
   *        n.put(i, Math.sqrt(n.get(i));
   * </pre>
   *
   * To perform the operation in place, modifying {@code m}, use {@code m.assign(m, Math::sqrt)} or
   * more verbosely
   *
   * <pre>
   *     for(long i = 0; i < m.size(); i++)
   *       m.put(i, Math.sqrt(m.get(i));
   * </pre>
   *
   * @param operator the operator to apply to each element
   * @return a new matrix
   */
  LongArray map(LongUnaryOperator operator);

  IntArray mapToInt(LongToIntFunction map);

  DoubleArray mapToDouble(LongToDoubleFunction map);

  ComplexArray mapToComplex(LongFunction<Complex> map);

  <T> Array<T> mapToObj(LongFunction<? extends T> mapper);

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * @param operator the operator to apply to each element
   */
  void apply(LongUnaryOperator operator);

  BooleanArray where(LongPredicate predicate);

  BooleanArray where(LongArray array, LongBiPredicate predicate);

  long reduce(long identity, LongBinaryOperator reduce);

  /**
   * Reduces {@code this} longo a real value. For example, summing can be implemented as
   * {@code matrix.reduce(0, (a, b) -> a + b, x -> x)}
   *
   * The first value of {@code reduce} is the current value and the second value is the accumulator.
   *
   * @param identity the initial value
   * @param reduce takes two values and reduces them to one
   * @param map takes a value and possibly transforms it
   * @return the result
   */
  long reduce(long identity, LongBinaryOperator reduce, LongUnaryOperator map);

  LongArray reduceVector(int dim, ToLongFunction<? super LongArray> accumulator);

  // Filter

  LongArray filter(LongPredicate operator);

  /**
   * Get value at row {@code i} and column {@code j}
   *
   * @param i row
   * @param j column
   * @return value long
   */
  long get(int i, int j);

  /**
   * @param index get long
   * @return long at {@code index}
   */
  long get(int index);

  void set(int index, long value);

  void set(int[] ix, long value);

  long get(int... ix);

  void set(int row, int column, long value);

  default void sort() {
    sort(Long::compare);
  }

  void sort(LongComparator comparator);

  LongStream longStream();

  // Arithmetical operations ///////////

  List<Long> asList();

  Array<Long> asArray();

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param other the matrix
   * @return a new matrix
   */
  LongArray times(LongArray other);

  /**
   * Element wise multiplication.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @return a new matrix
   */
  LongArray times(long alpha, LongArray other);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  LongArray times(long scalar);

  /**
   * Element wise addition.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  LongArray plus(LongArray other);

  /**
   * Element wise addition.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  LongArray plus(long scalar);

  /**
   * Element wise addition.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @return a new matrix
   */
  LongArray plus(long alpha, LongArray other);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  LongArray minus(LongArray other);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param scalar the scalar
   * @return r r
   */
  LongArray minus(long scalar);

  /**
   * Element wise subtraction.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @return a new matrix
   */
  LongArray minus(long alpha, LongArray other);

  /**
   * <u>R</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  LongArray reverseMinus(long scalar);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the other
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  LongArray div(LongArray other);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  LongArray div(long other);

  /**
   * Element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  LongArray reverseDiv(long other);

  /**
   * Returns a new matrix with elements negated.
   *
   * @return a new matrix
   */
  LongArray negate();

  default Array<Long> boxed() {
    return asArray();
  }

  DoubleArray doubleArray();

  IntArray intArray();

  LongArray longArray();

  ComplexArray complexArray();

  BooleanArray lt(LongArray other);

  BooleanArray gt(LongArray other);

  BooleanArray eq(LongArray other);

  BooleanArray leq(LongArray other);

  BooleanArray geq(LongArray other);

  long[] data();
}
