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

package org.briljantframework.array;

import org.briljantframework.complex.Complex;

import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * Implements a 2-dimensional matrix of complex numbers.
 *
 * @author Isak Karlsson
 */
public interface ComplexArray extends BaseArray<ComplexArray>, Iterable<Complex> {

  /**
   * Assign {@code value} to {@code this}
   *
   * @param value the value to assign
   * @return receiver modified
   */
  ComplexArray assign(Complex value);

  void assign(Complex[] value);

  default ComplexArray assign(double real) {
    return assign(Complex.valueOf(real));
  }

  /**
   * Assign value returned by {@link #size()} successive calls to {@link
   * java.util.function.DoubleSupplier#getAsDouble()}
   *
   * @param supplier the supplier
   * @return receiver modified
   */
  ComplexArray assign(Supplier<Complex> supplier);

  /**
   * Assign {@code matrix} to {@code this}, applying {@code operator} to each value.
   *
   * @param matrix   the matrix
   * @param operator the operator
   * @return receiver modified
   */
  ComplexArray assign(ComplexArray matrix, UnaryOperator<Complex> operator);

  ComplexArray assign(ComplexArray matrix, BinaryOperator<Complex> combine);

  /**
   * Assign {@code matrix} to this complex matrix.
   *
   * @param matrix matrix of real values
   * @return receiver modified
   */
  ComplexArray assign(DoubleArray matrix);

  /**
   * Assign {@code matrix} to this complex matrix transforming each element.
   *
   * @param matrix   the matrix
   * @param operator the operator
   * @return receiver modified
   */
  ComplexArray assign(DoubleArray matrix, DoubleFunction<Complex> operator);

  ComplexArray assign(LongArray matrix, LongFunction<Complex> operator);

  ComplexArray assign(IntArray matrix, IntFunction<Complex> operator);

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * @param operator the operator to apply to each element
   * @return receiver modified
   */
  ComplexArray update(UnaryOperator<Complex> operator);

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * For example, {@code m.map(Complex::sqrt)} is equal to
   *
   * <pre>
   * ComplexMatrix n = m.copy();
   * for (int i = 0; i &lt; n.size(); i++)
   *   n.put(i, n.get(i).sqrt());
   * </pre>
   *
   * To perform the operation in place, modifying {@code m}, use {@code m.assign(m, Complex::sqrt)}
   * or more verbosely
   *
   * <pre>
   * for (int i = 0; i &lt; m.size(); i++)
   *   m.put(i, m.get(i).sqrt());
   * </pre>
   *
   * @param operator the operator to apply to each element
   * @return a new matrix
   */
  ComplexArray map(UnaryOperator<Complex> operator);

  IntArray mapToInt(ToIntFunction<Complex> function);

  LongArray mapToLong(ToLongFunction<Complex> function);

  DoubleArray mapToDouble(ToDoubleFunction<Complex> function);

  ComplexArray filter(Predicate<Complex> predicate);

  BitArray satisfies(Predicate<Complex> predicate);

  BitArray satisfies(ComplexArray matrix, BiPredicate<Complex, Complex> predicate);

  Complex reduce(Complex identity, BinaryOperator<Complex> reduce);

  /**
   * Reduces {@code this} into a real value. For example, summing can be implemented as {@code
   * matrix.reduce(0, (a, b) -> a + b, x -> x)}
   *
   * @param identity the initial value
   * @param reduce   takes two values and reduces them to one
   * @param map      takes a value and possibly transforms it
   * @return the result
   */
  Complex reduce(Complex identity, BinaryOperator<Complex> reduce, UnaryOperator<Complex> map);

  /**
   * Reduces each column. Column wise summing can be implemented as
   *
   * <pre>
   * matrix.reduceColumns(col -&gt; col.reduce(0, (a, b) -&gt; a + b, x -&gt; x));
   * </pre>
   *
   * @param reduce takes a {@code ComplexMatrix} and returns {@code Complex}
   * @return a new column vector with the reduced value
   */
  ComplexArray reduceColumns(Function<? super ComplexArray, ? extends Complex> reduce);

  /**
   * Reduces each rows. Row wise summing can be implemented as
   *
   * <pre>
   * matrix.reduceRows(row -&gt; row.reduce(0, (a, b) -&gt; a + b, x -&gt; x));
   * </pre>
   *
   * @param reduce takes a {@code ComplexMatrix} and returns {@code Complex}
   * @return a new column vector with the reduced value
   */
  ComplexArray reduceRows(Function<? super ComplexArray, ? extends Complex> reduce);

  /**
   * Returns the conjugate transpose of this vector.
   *
   * @return the conjugate transpose
   */
  ComplexArray conjugateTranspose();

  void set(int i, int j, Complex complex);

  void set(int index, Complex complex);

  void set(int[] index, Complex complex);

  /**
   * Flattens the traversal of the matrix in column-major order. The matrix is traversed in
   * column-major order. For example, given the following matrix <p>
   *
   * <pre>
   *     1 2 3
   *     4 5 6
   * </pre>
   * <p> this code <p>
   *
   * <pre>
   * for (int i = 0; i &lt; x.size(); i++) {
   *   System.out.print(x.get(i));
   * }
   * </pre>
   * <p> prints <p>
   *
   * <pre>
   * 142536
   * </pre>
   *
   * @param index the index
   * @return the value index
   */
  Complex get(int index);

  /**
   * Get value at row {@code i} and column {@code j}
   *
   * @param i row
   * @param j column
   * @return value Complex
   */
  Complex get(int i, int j);

  Complex get(int... index);

  Stream<Complex> stream();

  List<Complex> asList();

  /**
   * <u>m</u>atrix<u>m</u>ultiplication
   *
   * @param other the other
   * @return r r
   */
  ComplexArray mmul(ComplexArray other);

  /**
   * <u>M</u>atrix <u>M</u>atrix <u>M</u>ultiplication. Scaling {@code this} with {@code alpha} and
   * {@code other} with {@code beta}. Hence, it computes {@code this.mul(alpha).mul(other.mul(beta))},
   * but in one pass.
   *
   * @param alpha scaling for {@code this*other}
   * @param other the other matrix
   * @return a new matrix
   */
  ComplexArray mmul(Complex alpha, ComplexArray other);

  ComplexArray mmul(Op a, ComplexArray other, Op b);

  ComplexArray mmul(Complex alpha, Op a, ComplexArray other, Op b);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param other the matrix
   * @return a new matrix
   */
  ComplexArray mul(ComplexArray other);

  /**
   * Element wise multiplication. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code beta}. Hence, it computes {@code this.mul(alpha).mul(other.mul(beta))}, but in one
   * pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta  scaling for {@code other}
   * @return a new matrix
   */
  ComplexArray mul(Complex alpha, ComplexArray other, Complex beta);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  ComplexArray mul(Complex scalar);

  /**
   * Element wise addition.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  ComplexArray add(ComplexArray other);

  /**
   * Element wise addition.
   *
   * @param scalar the scalar
   * @return a new matrixget(
   */
  ComplexArray add(Complex scalar);

  /**
   * Element wise addition. Scaling {@code this} with {@code alpha} and {@code other} with {@code
   * beta}. Hence, it computes {@code this.mul(alpha).add(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta  scaling for {@code other}
   * @return a new matrix
   */
  ComplexArray add(Complex alpha, ComplexArray other, Complex beta);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param other the other matrix
   * @return a new matrix
   */
  ComplexArray sub(ComplexArray other);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param scalar the scalar
   * @return r r
   */
  ComplexArray sub(Complex scalar);

  /**
   * Element wise subtraction. Scaling {@code this} with {@code alpha} and {@code other} with
   * {@code
   * beta}. Hence, it computes {@code this.mul(alpha).sub(other.mul(beta))}, but in one pass.
   *
   * @param alpha scaling for {@code this}
   * @param other the other matrix
   * @param beta  scaling for {@code other}
   * @return a new matrix
   */
  ComplexArray sub(Complex alpha, ComplexArray other, Complex beta);

  /**
   * <u>R</u>eversed element wise subtraction. {@code scalar - this}.
   *
   * @param scalar the scalar
   * @return a new matrix
   */
  ComplexArray rsub(Complex scalar);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the other
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  ComplexArray div(ComplexArray other);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  ComplexArray div(Complex other);

  /**
   * Element wise division. {@code other / this}.
   *
   * @param other the scalar
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code this} contains {@code 0}
   */
  ComplexArray rdiv(Complex other);


  /**
   * Returns a new matrix with elements negated.
   *
   * @return a new matrix
   */
  ComplexArray negate();
}
