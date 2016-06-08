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
import java.util.Comparator;
import java.util.List;
import java.util.function.*;
import java.util.stream.Stream;

import org.apache.commons.math3.complex.Complex;

/**
 * A multidimensional array of reference types.
 * 
 * @author Isak Karlsson
 * @see BaseArray
 */
public interface Array<T> extends BaseArray<Array<T>>, Collection<T> {

  /**
   * @see Arrays#vector(Object[])
   */
  @SafeVarargs
  static <T> Array<T> of(T... data) {
    return Arrays.vector(data);
  }

  /**
   * @see Arrays#array(int...)
   */
  static <T> Array<T> empty(int... shape) {
    return Arrays.array(shape);
  }

  /**
   * Returns a new array containing the given elements, in order.
   *
   * @param elements the elements
   * @return a new array
   */
  static <T> Array<T> copyOf(List<? extends T> elements) {
    Array<T> a = empty(elements.size());
    for (int i = 0; i < elements.size(); i++) {
      a.set(i, elements.get(i));
    }
    return a;
  }

  /**
   * Assign the given value to each position in {@code this}.
   *
   * @param value the value
   */
  void assign(T value);

  /**
   * Assign the value produced by {@linkplain java.util.function.Supplier#get()} to every position
   * in {@code this}
   *
   * <p>
   * Example:
   *
   * <pre>
   * Array&lt;Double&gt; random = Arrays.referenceArray(3, 3);
   * Random rng = new Random();
   * random.assign(rng::nextGaussian);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[     0.6310493305783, 0.8472577240960372, 0.32059848527214957],
   *        [ 0.23032061567663129, 1.4297521011054555,   1.666247841931618],
   *        [-0.47219553601033654,  1.400574394946874, 0.14261753382770095]])
   * </pre>
   *
   * @param supplier the value supplier
   */
  void assign(Supplier<T> supplier);

  /**
   * Assigns the values of {@code other} to {@code this}, applying the operator to each element in
   * {@code other}
   * <p>
   * Example
   *
   * <pre>
   * Array&lt;Integer&gt; i = Arrays.range(0, 3).asArray();
   * Array&lt;String&gt; x = Arrays.array(new String[] {&quot;foo&quot;, &quot;bar&quot;, &quot;baz&quot;});
   * i.assign(x, String::length).mapToInt(Integer::intValue);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([3, 3, 3])
   * </pre>
   *
   * @param other the other array
   * @param operator the operator to apply to the elements in {@code other}
   */
  <U> void assign(Array<U> other, Function<? super U, ? extends T> operator);

  /**
   * Returns a {@code DoubleArray} consisting of the results of applying the given function to the
   * elements of this array.
   *
   * @param f a function to apply to each element
   * @return a new array
   */
  DoubleArray mapToDouble(ToDoubleFunction<? super T> f);

  /**
   * Returns a {@code LongArray} consisting of the results of applying the given function to the
   * elements of this array.
   *
   * @param f a function to apply to each element
   * @return a new array
   */
  LongArray mapToLong(ToLongFunction<? super T> f);

  /**
   * Returns an {@code IntArray} consisting of the results of applying the given function to the
   * elements of this array.
   *
   * @param f a function to apply to each element
   * @return a new array
   */
  IntArray mapToInt(ToIntFunction<? super T> f);

  /**
   * Returns a {@code ComplexArray} consisting of the results of applying the given function to the
   * elements of this array.
   *
   * @param f a function to apply to each element
   * @return a new array
   */
  ComplexArray mapToComplex(Function<? super T, Complex> f);

  BooleanArray mapToBoolean(Function<? super T, Boolean> f);

  /**
   * Returns an array consisting of the results of applying the given function to the elements of
   * this array.
   *
   * @param f a function to apply to each element
   * @return a new array
   */
  <U> Array<U> map(Function<? super T, ? extends U> f);

  /**
   * Apply the given operator to each element.
   * 
   * @param operator the operator
   */
  void apply(UnaryOperator<T> operator);

  /**
   * Return an array consisting of the elements of this stream that matches the given predicate. The
   * resulting array always have {@code 1} dimension.
   *
   * @param predicate the predicate to apply to each element to determine if it should be included
   * @return a new array
   */
  Array<T> filter(Predicate<? super T> predicate);

  /**
   * Return an array of the same shape consisting of the value of matching each element to the given
   * predicate.
   *
   * <p>
   * Example
   *
   * <pre>
   * Array&lt;String&gt; x = Arrays.array(new String[] {&quot;a&quot;, &quot;b&quot;, &quot;dd&quot;, &quot;ee&quot;}).reshape(2, 2);
   * x.where(v -&gt; v.length() &gt; 1);
   * </pre>
   * 
   * produces,
   * 
   * <pre>
   * array([[0, 1]
   *        [0, 1]] type: int)
   * </pre>
   *
   * @param predicate the predicate to apply to each element to determine the
   * @return a new array
   */
  BooleanArray where(Predicate<? super T> predicate);

  /**
   * Return an array of the same shape consisting of the the value of the given predicate after
   * matching the i:th element of {@code this} against the i:th element of {@code other}
   *
   * <p>
   * Example
   *
   * <pre>
   * {@code
   * > Array<String> x = Arrays.array(new String[]{"a", "b", "c"});
   * > Array<String> y = Arrays.array(new String[]{"a", "c", "b"});
   * > x.where(y, String::equalsIgnoreCase);
   * array([1, 0, 0] type: int);
   * }
   * </pre>
   *
   * @param other the other array
   * @param predicate the predicate to apply to each pair of elements
   * @return a new array
   */
  BooleanArray where(Array<? extends T> other, BiPredicate<? super T, ? super T> predicate);

  /**
   * Reduce the array element wise with the given initial value and accumulator.
   * 
   * @param initial the initial value
   * @param accumulator the accumulator
   * @return the accumulated value
   */
  T reduce(T initial, BinaryOperator<T> accumulator);

  /**
   * Reduce each series along the specified dimension using the given accumulator.
   * 
   * @param dim the dimension
   * @param accumulator the accumulator
   * @return a new array
   */
  Array<T> reduceVector(int dim, Function<? super Array<T>, ? extends T> accumulator);

  /**
   * Get the i:th element of this array. If {@code dims() != 1}, the array is traversed in flattened
   * column major order.
   *
   * @param i the index
   * @return the value at the i:th position
   */
  T get(int i);

  /**
   * Set the i:th element of this array to the given value.
   *
   * @param i the index
   * @param value the value
   */
  void set(int i, T value);

  /**
   * For 2d-arrays, get the element at the i:th row and the j:th column.
   *
   * @param i the row index
   * @param j the column index
   * @return the value
   */
  T get(int i, int j);

  /**
   * For 2d-arrays, set the element at the i:th row and the j:th column to the given value.
   *
   * @param i the row index
   * @param j the column index
   * @param value the value
   */
  void set(int i, int j, T value);

  /**
   * For nd-arrays, get the element at the given index
   *
   * @param index the index
   * @return the value
   */
  T get(int... index);

  /**
   * For nd-arrays, set the element at the given index to the given value
   *
   * @param index the index
   * @param value the value
   */
  void set(int[] index, T value);

  /**
   * Set the elements where the given array is {@code true} to the given value
   * <p/>
   * Example
   * 
   * <pre>
   * Array&lt;Integer&gt; a = Arrays.range(3 * 3).reshape(3, 3).asArray();
   * Array&lt;Integer&gt; b = Arrays.newArray(3, 3);
   * b.set(a.where(i -&gt; i &gt; 2), 10);
   * </pre>
   *
   * produces
   * 
   * <pre>
   * array([[null, 10, 10],
   *        [null, 10, 10],
   *        [null, 10, 10]])
   * </pre>
   *
   * @param array the array
   * @param value the value
   */
  void set(BooleanArray array, T value);

  /**
   * Get the elements where the given array is {@code true}.
   * 
   * <p/>
   * Example
   * 
   * <pre>
   * Array&lt;Integer&gt; a = Arrays.range(3 * 3).reshape(3, 3).asArray();
   * a.get(a.where(i -&gt; i &gt; 2));
   * </pre>
   *
   * produces
   *
   * <pre>
   * array([3, 4, 5, 6, 7, 8])
   * </pre>
   *
   * @param array the array
   * @return a new array
   */
  Array<T> get(BooleanArray array);

  /**
   * Return this array as a {@code Stream} with the values in the same order as
   * {@linkplain #get(int)}
   *
   * @return a stream
   */
  Stream<T> stream();

  void sort(Comparator<? super T> comparator);
}
