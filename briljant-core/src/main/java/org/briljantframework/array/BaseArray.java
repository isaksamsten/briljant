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
import java.util.function.Consumer;

import org.briljantframework.sort.Swappable;

/**
 * A multi dimensional array of unknown element type. An nd-array is a (usually fixed size)
 * multidimensional container of items of the same (unknown type). The number of dimensions is
 * defined by it {@link #getShape() shape} which is an array of n positive integers that specify the
 * size of each dimension. The field (element) type is specified in subclasses.
 * 
 * <p/>
 * The contents of an nd-array can be accessed by indexing or slicing.
 * 
 * <p/>
 * Different arrays can be sharing data so that changes made to a specific array can be visible in
 * another, i.e., an array can be a view of another array while still sharing underlying data
 * buffers.
 * 
 * </p>
 * This interface is implemented by arrays for four primitive types ({@link IntArray int},
 * {@link LongArray long}, {@link IntArray int} and {@link BooleanArray boolean}) and the references
 * types ({@link ComplexArray Complex} and {@link Array Object}). We call the first five the
 * <i>specialized</i> types (they support numerical operations).
 * 
 * <p/>
 * This interface provides ways to:
 *
 * <ul>
 * <li>adapt one implementation to another.</li>
 * <li>get values of any type.</li>
 * <li>set values of any type.</li>
 * </ul>
 * </p>
 *
 * This interface defines five methods for adapting the current implementation to any of the five
 * specialized types. However, there are some caveats when adapting arrays and perform mutations.
 *
 * For example, given a {@code DoubleArray d} which is adapted to a
 * {@code ComplexArray c = d.asComplex()}, setting a position to a new {@code Complex} with an
 * imaginary part, e.g., {@code c.set(0, Complex.I)}, would just propagate the real part to the
 * underlying {@code DoubleArray}. Likewise, given an {@code IntArray} adapted to a
 * {@code DoubleArray}, setting a position to a double converts it to an {@code int} (using
 * {@code (int) value}).
 *
 * Finally, all specialized types must return them self when the specialization method is called. In
 * all other cases, a view should be returned. That is:
 * <ul>
 * <li>{@link DoubleArray#asDouble()} must return {@code this}</li>
 * <li>{@link IntArray#asInt()} must return {@code this}</li>
 * <li>{@link org.briljantframework.array.LongArray#asLong()} must return {@code this}</li>
 * <li>{@link BooleanArray#asBoolean()} must return {@code this}</li>
 * <li>{@link ComplexArray#asComplex()} must return {@code this}</li>
 * </ul>
 * 
 * <p/>
 * We define the following conversions between the specialized types
 * <ul>
 * <li>{@code Complex => double}: {@code value.real()}</li>
 * <li>{@code double => int}: {@code (int) value}</li>
 * <li>{@code int => boolean}: {@code value == 1 ? true : false}</li>
 * <li>{@code boolean => int}; {@code value ? 1 : 0}</li>
 * <li>{@code int => double}: {@code value}</li>
 * <li>{@code double => Complex}: {@code Complex.valueOf(value)}</li>
 * </ul>
 *
 * <p>
 * Remember that most subclasses provide, {@code get(int...)}, {@code get(int, int)} and
 * {@code get(int)}, returning the specialized type. For example, {@link DoubleArray#get(int, int)}.
 * </p>
 *
 * @author Isak Karlsson
 * @see AbstractBaseArray
 * @see Array
 * @see ComplexArray
 * @see DoubleArray
 * @see LongArray
 * @see IntArray
 * @see BooleanArray
 */
public interface BaseArray<S extends BaseArray<S>> extends Swappable {

  /**
   * Set the value at {@code toIndex} using the value at {@code fromIndex} in {@code from}, enabling
   * transferring of (primitive) values between arrays without knowing the field type.
   * <p>
   * For example, given the following signature {@code <T extends Array<T>> copy(T from, T to)} one
   * can generically implement swapping without knowing the element type.
   *
   * <pre>
   * &lt;T extends BaseArray&lt;T&gt;&gt; copy(T from, T to) {
   *   for (int i = 0; i &lt; from.size(); i++) {
   *     to.set(i, from, i);
   *   }
   * }
   * </pre>
   *
   * @param toIndex the index in {@code this}
   * @param from the other array
   * @param fromIndex the index in {@code from}
   */
  void set(int toIndex, S from, int fromIndex);

  /**
   * For 2d-arrays, perform {@code this.set(toRow, toColumn, from.get(fromRow, fromColumn)} while
   * avoiding unboxing of primitive values.
   *
   * @param toRow the row in {@code this}
   * @param toColumn the column in {@code this}
   * @param from the other array
   * @param fromRow the row in {@code from}
   * @param fromColumn the column in {@code from}
   * @see #set(int, BaseArray, int) for an example
   */
  void set(int toRow, int toColumn, S from, int fromRow, int fromColumn);

  /**
   * For nd-arrays, perform {@code this.set(toIndex, from.get(fromIndex)} while avoiding unboxing of
   * primitive values.
   *
   * @param toIndex the index in {@code this}
   * @param from the other array
   * @param fromIndex the index in {@code from}
   * @see #set(int, BaseArray, int) for an example
   */
  void set(int[] toIndex, S from, int[] fromIndex);

  /**
   * Reverses each dimension of the array.
   * 
   * @return a new array
   */
  S reverse();

  /**
   * Assign {@code o} to {@code this}.
   * <p>
   * 
   * <pre>
   * DoubleArray arr = Arrays.newVector(new double[]{1,2,3,4});
   * DoubleArray zero = Arrays.doubleArray(4);
   * zero.assign(arr);
   * zero
   * </pre>
   * 
   * produces,
   * 
   * <pre>
   * array([1.000, 2.000, 3.000, 4.000])
   * </pre>
   *
   * The given array will be {@link Arrays#broadcastTo(BaseArray, int...) broadcast} to the this
   * shape.
   * 
   * @param o the matrix
   */
  void assign(S o);

  /**
   * Iterate over each vector of this array along the specified dimension.
   * <p>
   * Example:
   * 
   * <pre>
   * DoubleArray a = Arrays.linspace(0, 1, 2 * 2 * 3).reshape(2, 2, 3)
   * a.forEach(0, x -> System.out.println(x))
   * </pre>
   * 
   * produces,
   * 
   * <pre>
   * array([0.000, 0.091])
   * array([0.182, 0.273])
   * array([0.364, 0.455])
   * array([0.545, 0.636])
   * array([0.727, 0.818])
   * array([0.909, 1.000])
   * </pre>
   */
  void forEach(int dim, Consumer<S> consumer);

  /**
   * For 2d-arrays, sets the column at position {@code i} to the values supplied.
   *
   * <p>
   * Example
   * 
   * <pre>
   * DoubleArray a = Arrays.linspace(0, 1, 3 * 3).reshape(3, 3);
   * a.setColumn(0, Arrays.zero(3));
   * </pre>
   * 
   * produces,
   * 
   * <pre>
   * array([[0.000, 0.375, 0.750],
   *        [0.000, 0.500, 0.875],
   *        [0.000, 0.625, 1.000]])
   * </pre>
   *
   * @param i the column index
   * @param vec the vector of values
   * @throws java.lang.IllegalStateException if array is not 2d
   */
  void setColumn(int i, S vec);

  /**
   * For 2d-arrays, gets the (column) vector at {@code index}. This method returns a column vector,
   * i.e. a 2d-array with shape {@code n x 1}.
   *
   * <p>
   * Example
   * 
   * <pre>
   * IntArray r = Arrays.range(3 * 3).reshape(3, 3).copy()
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[0, 3, 6],
   *        [1, 4, 7],
   *        [2, 5, 8]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * r.setColumn(0, Arrays.newVector(new int[] {0, 0, 1}))
   * </pre>
   * 
   * produces,
   * 
   * <pre>
   * array([[0, 3, 6],
   *        [0, 4, 7],
   *        [1, 5, 8]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * r.get(Arrays.range(3), Arrays.range(1)).assign(Arrays.newVector(new int[] {0, 1, 0}))
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[0, 3, 6],
   *        [1, 4, 7],
   *        [0, 5, 8]])
   * </pre>
   *
   * @param index the index
   * @return a vector of shape {@code n x 1}
   * @throws java.lang.IllegalStateException if array is not 2d
   */
  S getColumn(int index);

  /**
   * For 2d-arrays, sets the row at position {@code i} to the supplied values.
   *
   * <p>
   * Example
   * 
   * <pre>
   * IntArray r = Arrays.range(3 * 3).reshape(3, 3).copy()
   * r.setRow(0, Arrays.newVector(new int[]{0, 0, 1}))
   * </pre>
   * 
   * produces,
   * 
   * <pre>
   * // r: before
   * array([[0, 3, 6],
   *        [1, 4, 7],
   *        [2, 5, 8]])
   *        
   * // r: after
   * array([[0, 0, 1],
   *        [1, 4, 7],
   *        [2, 5, 8]])
   *
   * </pre>
   * 
   * and
   * 
   * <pre>
   * r.get(Arrays.range(1)).assign(Arrays.newVector(new int[] {0, 1, 0}))
   * </pre>
   * 
   * produces,
   * 
   * <pre>
   * array([[0, 1, 0],
   *        [1, 4, 7],
   *        [2, 5, 8]])
   * </pre>
   *
   * @param i the row index
   * @param vec a vector of size {@code m}
   * @throws java.lang.IllegalStateException if array is not 2d
   * @throws java.lang.IllegalArgumentException if {@code i > n || i < 0}
   */
  void setRow(int i, S vec);

  /**
   * For 2d-arrays, gets the (row) vector at {@code i}. This method returns a row-vector, i.e. a
   * 2d-array with shape {@code 1 x m}.
   *
   * @param i the row index
   * @return a vector of shape {@code 1 x m}
   * @throws java.lang.IllegalStateException if array is not 2d
   * @throws java.lang.IllegalArgumentException if {@code i > n || i < 0}
   */
  S getRow(int i);

  /**
   * Gives a new shape to an array without changing its data.
   *
   * <p>
   * In most cases reshaping can be performed without copying:
   * 
   * <pre>
   * IntArray x = Arrays.range(3 * 3).copy();
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([0, 1, 2, 3, 4, 5, 6, 7, 8])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * x.reshape(3, 3)
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[0, 3, 6],
   *        [1, 4, 7],
   *        [2, 5, 8]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * x.reshape(3, 3).data() == x.data() == true
   * // true
   * </pre>
   *
   * however, in some cases (when elements are non-contiguous) a copy is created
   *
   * <pre>
   * IntArray x = Arrays.range(3 * 3).reshape(3, 3).copy().transpose();
   * </pre>
   * 
   * which produces
   * 
   * <pre>
   * array([[0, 1, 2],
   *        [3, 4, 5],
   *        [6, 7, 8]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * x.reshape(1, 9);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[0, 3, 6, 1, 4, 7, 2, 5, 8]])
   * </pre>
   * 
   * then
   * 
   * <pre>
   * x.reshape(1, 9).data() == x.data() == false
   * </pre>
   *
   * <p>
   * Passing {@code -1} is a shortcut for {@code Array x; x.reshape(x.size())}
   *
   * <pre>
   * IntArray x = Arrays.range(3 * 3).reshape(3, 3).transpose();
   * </pre>
   * 
   * <pre>
   * array([[0, 1, 2],
   *        [3, 4, 5],
   *        [6, 7, 8]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * x.reshape(-1); // or x.reshape() or x.ravel()
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([0, 3, 6, 1, 4, 7, 2, 5, 8])
   * </pre>
   *
   * @param shape the new shape must be compatible with the old shape, i.e. {@code shape[0] * ... *
   *              shape[shape.length - 1] == this.size()}
   * @return if possible, returns a view of the array without changing its data; otherwise returns a
   *         copy with changed shape
   */
  S reshape(int... shape);

  /**
   * Returns a 1d array concatenating all vectors in column major ordering.
   * 
   * @return a 1d array
   */
  S ravel();

  /**
   * Select the {@code i:th} slice of the final dimension.
   *
   * <p>
   * Example
   * 
   * <pre>
   * IntArray x = Arrays.range(3 * 3 * 3).reshape(3, 3, 3);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[[0,  9, 18],
   *         [3, 12, 21],
   *         [6, 15, 24]],
   * 
   *        [[1, 10, 19],
   *         [4, 13, 22],
   *         [7, 16, 25]],
   * 
   *        [[2, 11, 20],
   *         [5, 14, 23],
   *         [8, 17, 26]]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * x.select(0);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[0,  9, 18],
   *        [3, 12, 21],
   *        [6, 15, 24]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * x.select(0).select(0);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([0, 9, 18])
   * </pre>
   *
   * <p>
   * Note that this is the same as {@code select(0, index)}.
   *
   * @param index the slice in the final dimension to extract
   * @return a view of the {@code i:th} slice in the final dimension
   */
  S select(int index);

  /**
   * Selects the {@code i:th} slice in the {@code d:th dimension}.
   *
   * <p>
   * Example
   * 
   * <pre>
   * IntArray x = Arrays.range(3*3*3).reshape(3, 3, 3)
   * </pre>
   * 
   * produces
   * 
   * <pre>
   *  array([[[0,  9, 18],
   *          [3, 12, 21],
   *          [6, 15, 24]],
   *  
   *         [[1, 10, 19],
   *          [4, 13, 22],
   *          [7, 16, 25]],
   *  
   *         [[2, 11, 20],
   *          [5, 14, 23],
   *          [8, 17, 26]]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * x.select(2, 1);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[ 9, 12, 15],
   *        [10, 13, 16],
   *        [11, 14, 17]])
   * </pre>
   */
  S select(int dimension, int index);

  /**
   * Integer based slicing.
   * 
   * @param indexers the indexers
   * @return a new array
   * @see #slice(List)
   */
  S slice(IntArray... indexers);

  /**
   * Integer-based slicing, as opposed to basic slicing, returns a copy of the array. Complex
   * slicing selects a subset of the data based on numerical indicies on a per dimension basis. To
   * include ranges of values, one simple way is to use {@code Arrays.range(end).flat()}.
   *
   * <p>
   * Examples
   *
   * <pre>
   * IntArray x = Arrays.range(2 * 3 * 4).reshape(2, 3, 4);
   * </pre>
   *
   * produces
   *
   * <pre>
   * array([[[0,  9, 18, 27],
   *         [3, 12, 21, 30],
   *         [6, 15, 24, 33]],
   * 
   *        [[1, 10, 19, 28],
   *         [4, 13, 22, 31],
   *         [7, 16, 25, 34]],
   * 
   *        [[2, 11, 20, 29],
   *         [5, 14, 23, 32],
   *         [8, 17, 26, 35]]])
   * </pre>
   *
   * and
   *
   * <pre>
   * x.select(IntArray.of(0, 1));
   * </pre>
   *
   * produces
   *
   * <pre>
   * array([[[0,  9, 18, 27],
   *         [3, 12, 21, 30],
   *         [6, 15, 24, 33]],
   * 
   *        [[1, 10, 19, 28],
   *         [4, 13, 22, 31],
   *         [7, 16, 25, 34]]])
   * </pre>
   *
   * and
   *
   * <pre>
   * x.select(IntArray.of(0, 1), IntArray.of(1, 2));
   * </pre>
   *
   * produces
   *
   * <pre>
   * array([[3, 12, 21, 30],
   *        [7, 16, 25, 34]])
   * </pre>
   *
   * and
   *
   * <pre>
   * x.select(IntArray.of(1,1), IntArray.of(1,1), IntArray.of(1,1)));
   * </pre>
   *
   * produces
   *
   * <pre>
   * array([13, 13])
   * </pre>
   *
   * @param indexers a list of indexes to include
   * @return a new array
   */
  S slice(List<? extends IntArray> indexers);

  /**
   * Gets the {@code i:th} vector along the {@code d:th} dimension. For 2d-arrays,
   * {@linkplain #getRow(int)} and {@linkplain #getColumn(int)} preserves the 2d-shape of the
   * vectors resulting in row-vectors and column-vectors respectively. This method results in
   * 1d-vectors.
   *
   * <p/>
   * Example
   * 
   * <pre>
   * IntArray x = Arrays.range(3 * 3 * 3).reshape(3, 3, 3);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[[0,  9, 18],
   *         [3, 12, 21],
   *         [6, 15, 24]],
   * 
   *        [[1, 10, 19],
   *         [4, 13, 22],
   *         [7, 16, 25]],
   * 
   *        [[2, 11, 20],
   *         [5, 14, 23],
   *         [8, 17, 26]]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * x.getVector(0, 0);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([0, 1, 2])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * x.getVector(1, 0);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([0, 3, 6])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * x.getVector(2, 9);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([0, 9, 18])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * IntArray y = x.select(0);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[0,  9, 18],
   *        [3, 12, 21],
   *        [6, 15, 24]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * y.getVector(0, 0);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([0, 3, 6])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * y.getColumn(0);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[0],
   *        [3],
   *        [6]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * y.getVector(1, 1);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([3, 12, 21])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * y.getRow(1);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[3, 12, 21]])
   * </pre>
   *
   * @param dimension the dimension
   * @param index the index of the vector
   * @return a view of the {@code i:th} vector of the {@code d:th} dimension
   */
  S getVector(int dimension, int index);

  /**
   * Sets the elements of the {@code i:th} vector in the {@code d:th} dimension to the values of
   * {@code other}. The size of {@code other} must equal the size of the {@code d:th} dimension
   * {@code size(dim)}.
   *
   * <p>
   * Example
   * 
   * <pre>
   * IntArray x = Arrays.range(3 * 3 * 3).reshape(3, 3, 3);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[[0,  9, 18],
   *         [3, 12, 21],
   *         [6, 15, 24]],
   * 
   *        [[1, 10, 19],
   *         [4, 13, 22],
   *         [7, 16, 25]],
   * 
   *        [[2, 11, 20],
   *         [5, 14, 23],
   *         [8, 17, 26]]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * x.setVector(0, 0, IntArray.zeros(3));
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[[0,  9, 18],
   *         [3, 12, 21],
   *         [6, 15, 24]],
   * 
   *        [[0, 10, 19],
   *         [4, 13, 22],
   *         [7, 16, 25]],
   * 
   *        [[0, 11, 20],
   *         [5, 14, 23],
   *         [8, 17, 26]]])
   * }
   * </pre>
   *
   * @param dimension the dimension
   * @param index the index of the vector
   * @param other the new values for the {@code i:th} vector in the {@code d:th} dimension
   */
  void setVector(int dimension, int index, S other);

  /**
   * <p>
   * Gets a view of the diagonal of a 2-d array
   *
   * <pre>
   * DoubleArray b = Arrays.newVector(new double[] {1, 2, 3, 4}).reshape(2, 2);
   * b.getDiagonal();
   * // array([1,4])
   * </pre>
   *
   * @return a diagonal view
   */
  S getDiagonal();

  /**
   * @param ranges the ranges (one for each dimension) to include in the view
   * @return a view
   * @see #get(java.util.List)
   */
  S get(RangeIndexer... ranges);

  /**
   * Basic slicing returns a view of the nd-array. The standard rules of slicing applies to basic
   * slicing on a per dimension basis, i.e., the values included in a range (with step > 0) is
   * selected for each dimension {@code d}. Given an nd-array, a range {@code range(i, j, k)} with
   * start index ({@code i}), end index ({@code j}) and step size ({@code k}) selects a series of
   * {@code m} values (in the d:th dimension) {@code i, i+k,...,i+(m-1)} where {@code m = q/k + r}
   * and {@code q=j-i} and {@code r = 1(i % k = 0)}. If {@code ranges.size() < dims()}, dimension d
   * {@code d > ranges.size() && d <= dims()} are assumed to be {@code range(0, size(d), 1)}
   *
   * <p>
   * It is simple to see that the (specialized in each primitive array) function {@code get(int...)}
   * is a special case returning a single field.
   * </p>
   *
   * <p>
   * Example
   * <ul>
   * <li>1d-array
   * 
   * <pre>
   * IntArray r = Arrays.range(10);
   * </pre>
   * 
   * produces,
   * 
   * <pre>
   * array([0, 1, 2, 3, 4, 5, 6, 7, 8, 9])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * r.get(Arrays.range(0, 8, 2));
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([0, 2, 4, 6])
   * </pre>
   * 
   * </pre>
   * 
   * </li>
   * <li>2d-array
   *
   * <pre>
   * IntArray r = Arrays.range(3 * 3).reshape(3, 3);
   * </pre>
   * 
   * produces,
   * 
   * <pre>
   * array([[0, 3, 6],
   *        [1, 4, 7],
   *        [2, 5, 8]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * r.get(Arrays.range(1, 2)
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[1, 4, 7]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * r.get(Arrays.range(0, 3, 2), Arrays.range(0, 3, 2))
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[0, 6],
   *        [2, 8]])
   * </pre>
   *
   * </li>
   * <li>nd-array
   *
   * <pre>
   * IntArray r = Arrays.range(3*3*3).reshape(3,3,3):
   * </pre>
   * 
   * produces
   * 
   * <pre>
   *  array([[[0,  9, 18],
   *          [3, 12, 21],
   *          [6, 15, 24]],
   * 
   *         [[1, 10, 19],
   *          [4, 13, 22],
   *          [7, 16, 25]],
   * 
   *         [[2, 11, 20],
   *          [5, 14, 23],
   *          [8, 17, 26]]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * r.get(Arrays.range(0, 3, 2));
   * </pre>
   * 
   * produces
   * 
   * <pre>
   *  array([[[0,  9, 18],
   *          [3, 12, 21],
   *          [6, 15, 24]],
   * 
   *         [[2, 11, 20],
   *          [5, 14, 23],
   *          [8, 17, 26]]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * r.get(Arrays.range(0, 3, 2), Arrays.range(0, 1), Arrays.range(0, 3, 2))
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[[0, 18]],
   * 
   *        [[2, 20]]])
   * </pre>
   * 
   * </li>
   * </ul>
   *
   * @param ranges a collection of ranges
   * @return a view
   */
  S get(List<? extends RangeIndexer> ranges);

  /**
   * For a 2d-array, get a view of row starting at {@code rowOffset} until {@code rowOffset + rows}
   * and columns starting at {@code colOffset} until {@code colOffset + columns}.
   * <p>
   * Example
   * 
   * <pre>
   * IntArray x = Arrays.range(1, 10).reshape(3, 3).transpose();
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[1, 2, 3],
   *        [4, 5, 6]
   *        [7, 8, 9]])
   * </pre>
   * 
   * and
   * 
   * <pre>
   * x.getView(1, 1, 2, 2);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[5, 6]
   *        [8, 9]])
   * </pre>
   *
   * @param rowOffset the row offset
   * @param colOffset the column offset
   * @param rows number of rows after row offset
   * @param columns number of columns after column offset
   * @return a view
   */
  S getView(int rowOffset, int colOffset, int rows, int columns);

  /**
   * Returns the linearized size of this matrix. If {@code dims() == } 1, then {@code size()} is
   * intuitive. However, if not, size is {@code shape[1] * shape[2] * ... *
   * shape[dims() - 1]} and used when iterating using {@code get(int)}. For matrices, to avoid cache
   * misses, {@code for(int i = 0; i < m.size(); i++) m.set(i, o.get(i))} should be preferred to
   *
   * <pre>
   * for(int i = 0; i < m.rows(); i++)
   *   for(int j = 0; j < m.columns(); j++)
   *      m.set(i, j, o.get(i, j))
   * </pre>
   *
   * If the {@code Array}-type is unknown, use:
   *
   * <pre>
   * for (int i = 0; i &lt; m.rows(); i++)
   *   for (int j = 0; j &lt; m.columns(); j++)
   *     m.set(i, j, m, i, j);
   * 
   * // or
   * for (int i = 0; i &lt; m.size(); i++)
   *   m.set(i, o, i);
   * </pre>
   *
   * @return the size
   */
  int size();

  /**
   * Get the size of the i:th dimension.
   *
   * @param dim the dimension
   * @return the size of the dimension
   */
  int size(int dim);

  /**
   * Get the number of of vectors along the {@code i}:th dimension.
   *
   * @param i the dimension
   * @return the number of vectors along the dimension
   */
  int vectors(int i);

  /**
   * The stride of the i:th dimension
   *
   * @param i the index of the dimension
   * @return the stride of the dimension
   */
  int stride(int i);

  /**
   * The offset for the starting element of the array
   *
   * @return the offset
   */
  int getOffset();

  /**
   * Returns a copy of the arrays shape
   *
   * @return a copy of the shape
   */
  int[] getShape();

  /**
   * Returns a copy of the arrays stride
   *
   * @return a copy of the strides
   */
  int[] getStride();

  /**
   * The major stride. In most cases this is {@code this.stride(0)} if {@code this.isContiguous()}
   * returns true.
   *
   * @return the major stride
   */
  int getMajorStride();

  /**
   * Returns {@code true} if this array is a 2d-array and both dimensions have the same size.
   *
   * @return {@code true} if the array is square
   */
  default boolean isSquare() {
    return dims() == 2 && rows() == columns();
  }

  /**
   * The number of rows.
   *
   * @return number or rows
   */
  int rows();

  /**
   * The number of columns.
   *
   * @return number of columns
   */
  int columns();

  /**
   * The number of dimensions of the array.
   *
   * @return the dimensions
   */
  int dims();

  /**
   * Return {@code true} if this array is a vector. The definition of vector is a 1d-array or a
   * 2d-array where the first or second dimension is {@code 1}.
   *
   * @return {@code true} if this array is a vector
   */
  boolean isVector();

  /**
   * Returns {@code true} if this array is a matrix. The definition of a matrix is a 2d-array. For
   * row and column vectors the definition of vector and matrix overlaps. For some 2d-arrays both
   * {@code isVector()} and {@code isMatrix()} returns {@code true}.
   *
   * @return {@code true} if this array is a matrix
   */
  boolean isMatrix();

  /**
   * Creates a view of this array with the specified shape and stride.
   *
   * @param shape the shape of the view
   * @param stride the stride of the view
   * @return a view
   * @see #asView(int, int[], int[], int)
   */
  S asView(int[] shape, int[] stride);

  /**
   * Create a view of {@code this} array with the specified offset, shape and stride.
   *
   * @param offset the offset (where indexing starts)
   * @param shape the shape of the view
   * @param stride the strides of the view
   * @return a view
   * @see #asView(int, int[], int[], int)
   */
  S asView(int offset, int[] shape, int[] stride);

  /**
   * Create a view of this array with the specified offset, shape, stride and major stride. This is
   * an advanced technique and in most cases can it be avoided.
   *
   * <p>
   * Example
   * 
   * <pre>
   * {@code
   * > IntArray x = Arrays.range(10).reshape(2,5);
   * array([[0, 2, 4, 6, 8],
   *        [1, 3, 5, 7, 9]] type: int)
   * 
   * > x.asView(new int[]{5}, new int[]{2});
   * array([0, 2, 4, 6, 8] type: int)
   * }
   * </pre>
   *
   * A more complex example could be that we have a 2d-array and want to extract {@code p x p}
   * non-overlapping blocks. For example, given a {@code 4 x 4} array
   * 
   * <pre>
   * array([[1, 5,  9, 13],
   *        [2, 6, 10, 14],
   *        [3, 7, 11, 15],
   *        [4, 8, 12, 16]] type: int)
   * </pre>
   *
   * we want to extract the blocks without copying
   *
   * <pre>
   * array([[[[ 1,  5],
   *          [ 2,  6]],
   * 
   *         [[ 3,  7],
   *          [ 4,  8]]],
   * 
   *        [[[ 9, 13],
   *          [10, 14]],
   * 
   *         [[11, 15],
   *          [12, 16]]]] type: int)
   * </pre>
   *
   * We can implement it as this:
   * 
   * <pre>
   * IntArray x = Arrays.range(1, 17).reshape(4, 4);
   * int h = x.size(0);
   * int w = x.size(1);
   * int bh = 2;
   * int bw = 2;
   * int[] shape = new int[] {h / bw, w / bw, bh, bw};
   * int[] strides = new int[] {h * bw, bh, 1, h};
   * IntArray y = x.asView(shape, strides);
   * </pre>
   * 
   * which produces {@code y} as:
   * 
   * <pre>
   * array([[[[ 1,  5],
   *          [ 2,  6]],
   * 
   *         [[ 3,  7],
   *          [ 4,  8]]],
   * 
   *        [[[ 9, 13],
   *          [10, 14]],
   * 
   *         [[11, 15],
   *          [12, 16]]]])
   * </pre>
   *
   * @param offset the offset (where indexing starts)
   * @param shape the shape of the view
   * @param stride the stride of the view
   * @param majorStride the index of the major stride (usually {@code 0} or {@code shape.length -
   *                    1})
   */
  S asView(int offset, int[] shape, int[] stride, int majorStride);

  /**
   * Create a new array with the given shape.
   *
   * @param shape the shape of the new array
   * @return a new array
   */
  S newEmptyArray(int... shape);

  /**
   * Returns {@code true} if the array is a view
   */
  boolean isView();

  /**
   * @return this matrix as a {@link DoubleArray}.
   */
  DoubleArray asDouble();

  /**
   * @return this matrix as an {@link IntArray}.
   */
  IntArray asInt();

  /**
   * @return return this matrix as a {@link LongArray}
   */
  LongArray asLong();

  /**
   * @return this matrix as an {@link BooleanArray}.
   */
  BooleanArray asBoolean();

  /**
   * @return this matrix as a {@link ComplexArray}.
   */
  ComplexArray asComplex();

  /**
   * Returns true if the array is contiguous in memory, which means that the array is in the default
   * order, i.e. fortran ordering in which the first dimension is varying faster.
   *
   * <p>
   * Example
   * 
   * <pre>
   * IntArray a = Arrays.newVector(new int[] {1, 2, 3, 4, 5, 6}).reshape(3, 3);
   * </pre>
   * 
   * produces
   * 
   * <pre>
   * array([[1, 3, 5]
   *        [2, 4, 6]] type: int);
   * </pre>
   * 
   * with {@code a.isContiguous();} returning {@code true} and also
   * {@code a.reshape(-1).isContiguous()} return {@code true}. However,
   * {@code a.transpose().isContiguous()} reverses the major stride and hence returns {@code false}.
   *
   * <p>
   * If a method specifies that it returns a view if possible and a copy otherwise, generally this
   * means that it returns a view if {@code isContiguous()} returns {@code true}.
   *
   * @return true if the array is fortran order contiguous
   */
  boolean isContiguous();

  /**
   * @return the transpose of {@code this}.
   */
  S transpose();

  /**
   * Create a copy of this matrix.
   *
   * @return the copy
   */
  S copy();

  BooleanArray lt(S other);

  BooleanArray gt(S other);

  BooleanArray eq(S other);

  BooleanArray lte(S other);

  BooleanArray gte(S other);

}
