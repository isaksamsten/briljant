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
import java.util.stream.Stream;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.ComplexVector;

/**
 * A n-dimensional array of double values.
 *
 * @author Isak Karlsson
 */
public interface ComplexArray
    extends NumberArray, BaseArray<ComplexArray>, Collection<Complex>, ComplexVector {

  static ComplexArray ones(int... shape) {
    ComplexArray array = zeros(shape);
    array.assign(Complex.ONE);
    return array;
  }

  static ComplexArray zeros(int... shape) {
    return Arrays.complexArray(shape);
  }

  static ComplexArray copyOf(List<Complex> elements) {
    ComplexArray a = zeros(elements.size());
    for (int i = 0; i < elements.size(); i++) {
      a.set(i, elements.get(i));
    }
    return a;
  }

  /**
   * @see Arrays#complexVector(Complex...)
   */
  static ComplexArray of(Complex... data) {
    return Arrays.complexVector(data);
  }

  static ComplexArray of(double... real) {
    return Arrays.complexVector(real);
  }

  /**
   * Assign {@code value} to {@code this}
   *
   * @param value the value to assign
   * @return receiver modified
   */
  void assign(Complex value);

  void assign(double[] value);

  void assign(Complex[] value);

  default void assign(double real) {
    assign(Complex.valueOf(real));
  }

  /**
   * Assign value returned by {@link #size()} successive calls to
   * {@link java.util.function.DoubleSupplier#getAsDouble()}
   *
   * @param supplier the supplier
   */
  void assign(Supplier<Complex> supplier);

  /**
   * Assign {@code matrix} to {@code this}, applying {@code operator} to each value.
   *
   * @param array the matrix
   * @param operator the operator
   */
  void assign(ComplexArray array, UnaryOperator<Complex> operator);

  void combineAssign(ComplexArray array, BinaryOperator<Complex> combine);

  /**
   * Assign {@code matrix} to this complex matrix.
   *
   * @param array matrix of real values
   */
  void assign(DoubleArray array);

  /**
   * Assign {@code matrix} to this complex matrix transforming each element.
   *
   * @param array the matrix
   * @param operator the operator
   */
  void assign(DoubleArray array, DoubleFunction<Complex> operator);

  void assign(LongArray array, LongFunction<Complex> operator);

  void assign(IntArray array, IntFunction<Complex> operator);

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

  <T> Array<T> mapToObj(Function<Complex, ? extends T> mapper);

  /**
   * Perform {@code operator} element wise to receiver.
   *
   * @param operator the operator to apply to each element
   */
  void apply(UnaryOperator<Complex> operator);

  ComplexArray filter(Predicate<Complex> predicate);

  BooleanArray where(Predicate<Complex> predicate);

  BooleanArray where(ComplexArray matrix, BiPredicate<Complex, Complex> predicate);

  Complex reduce(Complex identity, BinaryOperator<Complex> reduce);

  /**
   * Perform a reduction over all vectors along the given dimension
   *
   * <pre>
   * DoubleArray.of(1,2,3,4).reshape(2,2).reduceVectors(0, Double::sum));
   * </pre>
   *
   * sums each row
   *
   * @param dim the dimension
   * @param reduce the reduction
   * @return a new array
   */
  ComplexArray reduceVectors(int dim, Function<? super ComplexArray, ? extends Complex> reduce);

  /**
   * Reduces {@code this} into a real value. For example, summing can be implemented as
   * {@code matrix.reduce(0, (a, b) -> a + b, x -> x)}
   *
   * @param identity the initial value
   * @param reduce takes two values and reduces them to one
   * @param map takes a value and possibly transforms it
   * @return the result
   */
  Complex reduce(Complex identity, BinaryOperator<Complex> reduce, UnaryOperator<Complex> map);

  /**
   * Returns the conjugate transpose of this series.
   *
   * @return the conjugate transpose
   */
  ComplexArray conjugateTranspose();

  void set(int i, int j, Complex complex);

  void set(int index, Complex complex);

  void set(int[] index, Complex complex);

  /**
   * Flattens the traversal of the matrix in column-major order. The matrix is traversed in
   * column-major order. For example, given the following matrix
   * <p>
   *
   * <pre>
   *     1 2 3
   *     4 5 6
   * </pre>
   * <p>
   * this code
   * <p>
   *
   * <pre>
   * for (int i = 0; i &lt; x.size(); i++) {
   *   System.out.print(x.get(i));
   * }
   * </pre>
   * <p>
   * prints
   * <p>
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

  @Override
  default Complex getComplex(int i) {
    return get(i);
  }

  Array<Complex> asArray();

  Stream<Complex> stream();

  /**
   * Returns a new matrix with elements negated.
   *
   * @return a new matrix
   */
  ComplexArray negate();

  default Array<Complex> boxed() {
    return asArray();
  }

  double[] data();

  DoubleArray doubleArray();

  IntArray intArray();

  LongArray longArray();

  ComplexArray complexArray();
}
