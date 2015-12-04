/**
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

import java.util.List;
import java.util.function.DoubleToIntFunction;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongToIntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

import net.mintern.primitive.comparators.IntComparator;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Listable;
import org.briljantframework.function.IntBiPredicate;
import org.briljantframework.function.ToIntObjIntBiFunction;

/**
 * A n-dimensional array of integer values.
 * 
 * @author Isak Karlsson
 */
public interface IntArray extends BaseArray<IntArray>, Iterable<Integer>, Listable<Integer> {

  static IntArray ones(int... shape) {
    IntArray array = zeros(shape);
    array.assign(1);
    return array;
  }

  static IntArray zeros(int... shape) {
    return Arrays.newIntArray(shape);
  }

  /**
   * Assign {@code value} to {@code this}
   *
   * @param value the value to assign
   */
  void assign(int value);

  /**
   * @see Arrays#newIntVector(int...)
   */
  static IntArray of(int... data) {
    return Arrays.newIntVector(data);
  }

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
   * Assign {@code matrix} to {@code this}, applying {@code operator} to each value.
   *
   * @param operator the operator
   * @return receiver modified
   */
  void assign(IntArray matrix, IntUnaryOperator operator);

  /**
   * Assign {@code matrix} to {@code this}, applying {@code combine} to combine the i:th value of
   * {@code this} and {@code matrix}
   *
   * @param matrix the matrix
   * @param combine the combiner
   * @return receiver modified
   */
  void assign(IntArray matrix, IntBinaryOperator combine);

  void assign(ComplexArray matrix, ToIntFunction<? super Complex> function);

  void assign(DoubleArray matrix, DoubleToIntFunction function);

  void assign(LongArray matrix, LongToIntFunction operator);

  void assign(BooleanArray matrix, ToIntObjIntBiFunction<Boolean> function);

  void apply(IntUnaryOperator operator);

  // Transform

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * For example, {@code m.map(Math::sqrt)} is equal to
   *
   * <pre>
   *     Matrix n = m.copy();
   *     for(int i = 0; i < n.size(); i++)
   *        n.put(i, Math.sqrt(n.get(i));
   * </pre>
   *
   * To perform the operation in place, modifying {@code m}, use {@code m.assign(m, Math::sqrt)} or
   * more verbosely
   *
   * <pre>
   *     for(int i = 0; i < m.size(); i++)
   *       m.put(i, Math.sqrt(m.get(i));
   * </pre>
   *
   * @param operator the operator to apply to each element
   * @return a new matrix
   */
  IntArray map(IntUnaryOperator operator);

  LongArray mapToLong(IntToLongFunction function);

  DoubleArray mapToDouble(IntToDoubleFunction function);

  ComplexArray mapToComplex(IntFunction<Complex> function);

  <U> Array<U> mapToObj(IntFunction<? extends U> function);

  // Filter

  IntArray filter(IntPredicate operator);

  BooleanArray where(IntPredicate predicate);

  BooleanArray where(IntArray matrix, IntBiPredicate predicate);

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

  IntArray reduceVectors(int dim, ToIntFunction<? super IntArray> accumulator);

  // GET / SET

  /**
   * @param index get int
   * @return int at {@code index}
   */
  int get(int index);

  void set(int index, int value);

  /**
   * Get value at row {@code i} and column {@code j}
   *
   * @param i row
   * @param j column
   * @return value int
   */
  int get(int i, int j);

  void set(int row, int column, int value);

  void set(int[] ix, int value);

  int get(int... ix);

  void apply(int index, IntUnaryOperator operator);

  void apply(int i, int j, IntUnaryOperator operator);

  IntStream stream();

  List<Integer> toList();

  Array<Integer> boxed();

  void sort();

  void sort(IntComparator cmp);

  // Arithmetical operations ///////////

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param other the matrix
   * @return a new matrix
   */
  IntArray times(IntArray other);

  /**
   * Element wise multiplication. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.times(alpha).times(other.times(beta))}, but in one
   * pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta scaling for {@code other}
   * @return a new matrix
   */
  IntArray times(int alpha, IntArray other, int beta);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  IntArray times(int scalar);

  /**
   * Element wise addition.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  IntArray plus(IntArray other);

  /**
   * Element wise addition.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  IntArray plus(int scalar);

  void plusAssign(IntArray other);

  void plusAssign(int scalar);

  /**
   * Element wise addition. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.times(alpha).plus(other.times(beta))}, but in one
   * pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @return a new matrix
   */
  IntArray plus(int alpha, IntArray other);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  IntArray minus(IntArray other);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param scalar the scalar
   * @return r r
   */
  IntArray minus(int scalar);

  /**
   * Element wise subtraction. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.times(alpha).minus(other.times(beta))}, but in one
   * pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @return a new matrix
   */
  IntArray minus(int alpha, IntArray other);

  void minusAssign(IntArray other);

  void minusAssign(int scalar);

  /**
   * <u>R</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  IntArray reverseMinus(int scalar);

  void reverseMinusAssign(int scalar);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the other
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  IntArray div(IntArray other);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  IntArray div(int other);

  void divAssign(IntArray other);

  void divAssign(int other);

  /**
   * Element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  IntArray reverseDiv(int other);

  void reverseDivAssign(int other);

  /**
   * Returns a new matrix with elements negated.
   *
   * @return a new matrix
   */
  IntArray negate();

  int[] data();
}
