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
import java.util.stream.Stream;

import org.apache.commons.math3.complex.Complex;

/**
 * @author Isak Karlsson
 */
public interface Array<T> extends BaseArray<Array<T>> {

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
   * {@code
   * > Array<Double> random = Arrays.referenceArray(3, 3);
   * > Random rng = new Random();
   * > random.assign(rng::nextGaussian);
   * 
   * array([[     0.6310493305783, 0.8472577240960372, 0.32059848527214957],
   *        [ 0.23032061567663129, 1.4297521011054555,   1.666247841931618],
   *        [-0.47219553601033654,  1.400574394946874, 0.14261753382770095]] type: Double)
   * }
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
   * {@code
   * > Array<Integer> i = Arrays.range(0, 3).boxed();
   * > Array<String> x = Arrays.array(new String[]{"foo", "bar", "baz"});
   * > i.assign(x, String::length).mapToInt(Integer::intValue);
   * array([3, 3, 3] type: int)
   * }
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

  /**
   * Returns an array consisting of the results of applying the given function to the elements of
   * this array.
   *
   * @param f a function to apply to each element
   * @return a new array
   */
  <U> Array<U> map(Function<? super T, ? extends U> f);

  /**
   * Return a view of this array consisting of the results of lazily applying the {@code to}
   * function to each element of this array when accessing the elements and the {@code from}
   * function when setting the values.
   *
   * @param to the function to apply to elements when getting
   * @param from the function to apply to the argument when setting
   * @return an array view
   * @see #mapToDouble(java.util.function.ToDoubleFunction)
   */
  DoubleArray asDouble(ToDoubleFunction<? super T> to, DoubleFunction<T> from);

  /**
   * Return a view of this array consisting of the results of lazily (i.e. when accessing the value)
   * applying the given function to the elements of this array.
   *
   * @param to the function to apply to each element
   * @return an array view
   * @see #mapToDouble(java.util.function.ToDoubleFunction)
   */
  DoubleArray asDouble(ToDoubleFunction<? super T> to);

  /**
   * Return a view of this array consisting of the results of lazily applying the {@code to}
   * function to each element of this array when accessing the elements and the {@code from}
   * function when setting the values.
   *
   * @param to the function to apply to elements when getting
   * @param from the function to apply to the argument when setting
   * @return an array view
   * @see #mapToInt(java.util.function.ToIntFunction)
   */
  IntArray asInt(ToIntFunction<? super T> to, IntFunction<T> from);

  /**
   * Return a view of this array consisting of the results of lazily (i.e. when accessing the value)
   * applying the given function to the elements of this array.
   *
   * @param to the function to apply to each element
   * @return an array view
   * @see #mapToInt(java.util.function.ToIntFunction)
   */
  IntArray asInt(ToIntFunction<? super T> to);

  /**
   * Return a view of this array consisting of the results of lazily applying the {@code to}
   * function to each element of this array when accessing the elements and the {@code from}
   * function when setting the values.
   *
   * @param to the function to apply to elements when getting
   * @param from the function to apply to the argument when setting
   * @return an array view
   * @see #mapToLong(java.util.function.ToLongFunction)
   */
  LongArray asLong(ToLongFunction<? super T> to, LongFunction<T> from);

  /**
   * Return a view of this array consisting of the results of lazily (i.e. when accessing the value)
   * applying the given function to the elements of this array.
   *
   * @param to the function to apply to each element
   * @return an array view
   * @see #mapToLong(java.util.function.ToLongFunction)
   */
  LongArray asLong(ToLongFunction<? super T> to);

  /**
   * Return a view of this array consisting of the results of lazily applying the {@code to}
   * function to each element of this array when accessing the elements and the {@code from}
   * function when setting the values.
   *
   * @param to the function to apply to elements when getting
   * @param from the function to apply to the argument when setting
   * @return an array view
   */
  BooleanArray asBoolean(Function<? super T, Boolean> to, Function<Boolean, T> from);

  /**
   * Return a view of this array consisting of the results of lazily (i.e. when accessing the value)
   * applying the given function to the elements of this array.
   *
   * @param to the function to apply to each element
   * @return an array view
   */
  BooleanArray asBoolean(Function<? super T, Boolean> to);

  /**
   * Return a view of this array consisting of the results of lazily applying the {@code to}
   * function to each element of this array when accessing the elements and the {@code from}
   * function when setting the values.
   *
   * @param to the function to apply to elements when getting
   * @param from the function to apply to the argument when setting
   * @return an array view
   * @see #mapToComplex(java.util.function.Function)
   */
  ComplexArray asComplex(Function<? super T, Complex> to, Function<Complex, T> from);

  /**
   * Return a view of this array consisting of the results of lazily (i.e. when accessing the value)
   * applying the given function to the elements of this array.
   *
   * @param to the function to apply to each element
   * @return an array view
   * @see #mapToComplex(java.util.function.Function)
   */
  ComplexArray asComplex(Function<? super T, Complex> to);

  /**
   * Return an array consisting of the elements of this stream that matches the given predicate. The
   * resulting array always have {@code 1} dimension.
   *
   * @param predicate the predicate to apply to each element to determine if it should be included
   * @return a new array
   */
  Array<T> filter(Predicate<T> predicate);

  /**
   * Return an array of the same shape consisting of the value of matching each element to the given
   * predicate.
   *
   * <p>
   * Example
   * 
   * <pre>
   * {@code
   * > Array<String> x = Arrays.array(new String[]{"a", "b", "dd", "ee"}).reshape(2, 2);
   * > x.satisfies(v -> v.length() > 1);
   * array([[0, 1]
   *        [0, 1]] type: int)
   * }
   * </pre>
   *
   * @param predicate the predicate to apply to each element to determine the
   * @return a new array
   */
  BooleanArray satisfies(Predicate<T> predicate);

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
   * > x.satisfies(y, String::equalsIgnoreCase);
   * array([1, 0, 0] type: int);
   * }
   * </pre>
   *
   * @param other the other array
   * @param predicate the predicate to apply to each pair of elements
   * @return a new array
   */
  BooleanArray satisfies(Array<T> other, BiPredicate<T, T> predicate);

  T reduce(T initial, BinaryOperator<T> accumulator);

  Array<T> reduceVector(int dim, Function<? super Array<T>, T> accumulator);

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
   * Return this array as a {@code Stream} with the values in the same order as
   * {@linkplain #get(int)}
   *
   * @return a stream
   */
  Stream<T> stream();

  /**
   * Return this array as a {@code List} view
   *
   * @return a new list
   */
  List<T> list();

  /**
   * Return the contents of this array as a Java-array. If the array is a view, the returned data
   * might be longer (or shorter) than {@code this}
   *
   * @return a Java array (view or copy)
   */
  T[] data();
}
