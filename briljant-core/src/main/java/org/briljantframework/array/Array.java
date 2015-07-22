package org.briljantframework.array;

import org.briljantframework.sort.Swappable;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * <p>
 * The {@code Matrix} interface is a base interface for several different matrix implementations.
 *
 * There are four supported matrix types {@code double}, {@code int}, {@code boolean} and
 * {@link org.briljantframework.complex.Complex}, specialized in
 * {@link DoubleArray}, {@link IntArray}
 * , {@link BitArray} and
 * {@link ComplexArray} respectively.
 * </p>
 *
 * <p>
 * The {@code Matrix} interface provides ways to
 *
 * <ul>
 * <li>adapt one implementation to another.</li>
 * <li>get values of any type.</li>
 * <li>set values of any type.</li>
 * <li>set values of any type from another {@code Matrix}, possibly without boxing.</li>
 * <li>compare values of unknown types.</li>
 * </ul>
 * </p>
 *
 * <h1>Adapt {@code Matrix} to another matrix type</h1>
 * <p>
 * {@code Matrix} defines four methods for adapting the current implementation to any of the four
 * specialized types. However, there are some caveats when adapting matrices and perform mutations.
 *
 * For example, given a {@code DoubleMatrix d} which is adapted to a
 * {@code ComplexMatrix c = d.asComplexMatrix()}, then setting a position to a new {@code Complex}
 * with an imaginary part, e.g., {@code c.set(0, Complex.I)}, would just propagate the real part to
 * the underlying {@code DoubleMatrix}. Likewise, given an {@code IntMatrix} adapted to a
 * {@code DoubleMatrix}, setting a position to a double converts it to an {@code int} (using
 * {@link Math#round(double)}).
 *
 * Finally, if receiver is
 * <ul>
 * <li>{@link DoubleArray}, {@link #asDouble()} must return
 * {@code this}</li>
 * <li>{@link IntArray}, {@link #asInt()} must return
 * {@code this}</li>
 * <li>{@link BitArray}, {@link #asBit()} must return
 * {@code this}</li>
 * <li>{@link ComplexArray}, {@link #asComplex()} must return
 * {@code this}</li>
 * </ul>
 * </p>
 * <h1>Implicit conversions</h1>
 * <ul>
 * <li>{@code Complex => double}: {@code value.real()}</li>
 * <li>{@code double => int}: {@code (int) Math.round(value)}</li>
 * <li>{@code int => boolean}: {@code value == 1 ? true : false}</li>
 * <li>{@code boolean => int}; {@code value ? 1 : 0}</li>
 * <li>{@code int => double}: {@code value}</li>
 * <li>{@code double => Complex}: {@code Complex.valueOf(value)}</li>
 * </ul>
 *
 * <p>
 * Remember that most subclasses provide, {@code get(int, int)} and {@code get(int)}, returning the
 * specialized type. For example, {@link DoubleArray#get(int, int)}.
 * </p>
 *
 * <h1>Avoid unboxing/type checking/truncating when transferring values between {@code
 * Matrix}es</h1>
 * <p>
 * Prefer:
 *
 * <pre>
 *   Matrix a = Doubles.randn(10, 1)
 *   Matrix b = Doubles.zeros(10, 1)
 *   a.set(3, b, 0)
 * </pre>
 *
 * to:
 *
 * <pre>
 *   swith(b.getDataType()) {
 *       DataType.COMPLEX: a.set(3, b.getAsComplex(0); break;
 *       ...
 *       ...
 *       ...
 *       default: ...;
 *   }
 * </pre>
 *
 * </p>
 *
 * @author Isak Karlsson
 */
public interface Array<E extends Array> extends Swappable {

  /**
   * Set the value at {@code toIndex} using the value at {@code fromIndex} in {@code from},
   * enabling transferring of (primitive) values between arrays without knowing the field type.
   *
   * @param toIndex   the index in {@code this}
   * @param from      the other array
   * @param fromIndex the index in {@code from}
   */
  void set(int toIndex, E from, int fromIndex);

  /**
   *
   * @param toRow
   * @param toColumn
   * @param from
   * @param fromRow
   * @param fromColumn
   */
  void set(int toRow, int toColumn, E from, int fromRow, int fromColumn);

  void set(int[] toIndex, E from, int[] fromIndex);

  /**
   * Compares the values at index {@code a} and {@code b} in ascending order, i.e. if the value at
   * index {@code a} is smaller than the value at {@code b}, a value smaller than {@code 0} is
   * returned, if the values are equal {@code 0} is returned and if {@code b} is larger that {@code
   * a} a value larger than {@code 0} is returned.
   *
   * @param a the index of the first value
   * @param b the index of the second value
   * @return an indicator whether the value at {@code a} is smaller, larger or equal to {@code b}
   */
  int compare(int a, int b);

  /**
   * Assign {@code o} to {@code this}.
   * <p>
   * <pre>{@code
   *  > DoubleArray arr = Bj.array(new double[]{1,2,3,4});
   *  > DoubleArray zero = Bj.doubleArray(4);
   *  > zero.assign(arr);
   *  array([1.000, 2.000, 3.000, 4.000])
   * }</pre>
   *
   * @param o the matrix
   * @return receiver modified
   */
  E assign(E o);

  /**
   * Iterate over each vector of this array along the specified dimension.
   * <p>
   * Example:
   * <pre>{@code
   * > DoubleArray a = Bj.linspace(0, 1, 2 * 2 * 3).reshape(2, 2, 3)
   * > a.forEach(0, x -> System.out.println(x))
   *
   * array([0.000, 0.091])
   * array([0.182, 0.273])
   * array([0.364, 0.455])
   * array([0.545, 0.636])
   * array([0.727, 0.818])
   * array([0.909, 1.000])
   * }</pre>
   */
  void forEach(int dim, Consumer<E> consumer);

  /**
   * For 2d-arrays, sets the column at position {@code i} to the values supplied.
   *
   * <p>
   * Example
   * <pre>{@code
   * > DoubleArray a = Bj.linspace(0, 1, 3 * 3).reshape(3, 3);
   * > a.setColumn(0, Bj.zero(3));
   *
   * array([[0.000, 0.375, 0.750],
   *        [0.000, 0.500, 0.875],
   *        [0.000, 0.625, 1.000]])
   * }</pre>
   *
   * @param i   the column index
   * @param vec the vector of values
   * @throws java.lang.IllegalStateException if array is not 2d
   */
  void setColumn(int i, E vec);

  /**
   * For 2d-arrays, gets the (column) vector at {@code index}. This method returns a column vector,
   * i.e. a 2d-array with shape {@code n x 1}.
   *
   * @param index the index
   * @return a vector of shape {@code n x 1}
   * @throws java.lang.IllegalStateException if array is not 2d
   */
  E getColumn(int index);

  /**
   * For 2d-arrays, sets the row at position {@code i} to the supplied values.
   *
   * <p>
   * Example
   * <pre>{@code
   *
   * }</pre>
   *
   * @param i   the row index
   * @param vec a vector of size {@code m}
   * @throws java.lang.IllegalStateException    if array is not 2d
   * @throws java.lang.IllegalArgumentException if {@code i > n || i < 0}
   */
  void setRow(int i, E vec);

  /**
   * For 2d-arrays, gets the (row) vector at {@code i}. This method returns a row-vector, i.e. a
   * 2d-array with shape {@code 1 x m}.
   *
   * @param i the row index
   * @return a vector of shape {@code 1 x m}
   * @throws java.lang.IllegalStateException    if array is not 2d
   * @throws java.lang.IllegalArgumentException if {@code i > n || i < 0}
   */
  E getRow(int i);

  E reshape(int... shape);

  E select(int index);

  E select(int dimension, int index);

  void setVector(int dim, int index, E other);

  E getVector(int dimension, int index);

  /**
   * <p> Gets a view of the diagonal of a 2-d array
   *
   * <pre>{@code
   *  >>> DoubleArray b = Bj.array(new double[]{1,2,3,4}).reshape(2, 2);
   *  >>> b.getDiagonal();
   *  [1,4]
   * }</pre>
   *
   * @return a diagonal view
   */
  E getDiagonal();

  /**
   * Basic slicing. Returns a view of the underlying matrix. Subclasses should specialize the
   * return type.
   *
   * @param ranges the rows to include
   * @return a view
   */
  E get(Range... ranges);

  /**
   * Get a view of row starting at {@code rowOffset} until {@code rowOffset + rows} and columns
   * starting at {@code colOffset} until {@code colOffset + columns}.
   *
   * For example,
   *
   * <pre>
   *   1 2 3
   *   4 5 6
   *   7 8 9
   * </pre>
   *
   * and {@code matrix.getView(1, 1, 2, 2)} produces
   *
   * <pre>
   *   5 6
   *   8 9
   * </pre>
   *
   * Please note that modifications of the view, mutates the original.
   *
   * @param rowOffset the row offset
   * @param colOffset the column offset
   * @param rows      number of rows after row offset
   * @param columns   number of columns after column offset
   * @return the matrix view
   */
  E getView(int rowOffset, int colOffset, int rows, int columns);

  /**
   * Complex slicing. Returns a copy of the matrix.
   *
   * @param rows    the rows to include
   * @param columns the columns to include
   * @return a new matrix with the same size as {@code this}
   */
  E slice(Collection<Integer> rows, Collection<Integer> columns);

  E slice(Collection<Integer> indexes);

  E slice(BitArray bits);

  /**
   * Element wise addition.
   *
   * @param o the other matrix
   * @return a new matrix
   */
  E add(E o);

  /**
   * Element wise subtraction. {@code this - other}.
   *
   * @param o the other matrix
   * @return a new matrix
   */
  E sub(E o);

  /**
   * Element wise <u>m</u>ultiplication
   *
   * @param o the matrix
   * @return a new matrix
   */
  E mul(E o);

  /**
   * Element wise division. {@code this / other}.
   *
   * @param o the other
   * @return a new matrix
   * @throws java.lang.ArithmeticException if {@code other} contains {@code 0}
   */
  E div(E o);

  /**
   * <u>m</u>atrix<u>m</u>ultiplication
   *
   * @param o the other
   * @return r r
   */
  E mmul(E o);

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
   * Returns the linearized size of this matrix. If {@code dims() == } 1,
   * then {@code size()} is intuitive. However, if not, size is {@code shape[1] * shape[2] * ... *
   * shape[dims[]-1]} and used when iterating using {@code get(int)}. For matrices, to avoid cache
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

  int size(int dim);

  /**
   * Get the number of of vectors along the {@code i}:th dimension.
   *
   * @param i the dimension
   * @return the number of vectors along the dimension
   */
  int vectors(int i);

  int dims();

  int stride(int i);

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

  int getMajorStride();

  /**
   * @return true if rows() == columns()
   */
  default boolean isSquare() {
    return rows() == columns();
  }

  boolean isVector();

  boolean isMatrix();

  E newEmptyArray(int... shape);

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
   * @return this matrix as an {@link BitArray}.
   */
  BitArray asBit();

  /**
   * @return this matrix as a {@link ComplexArray}.
   */
  ComplexArray asComplex();

  /**
   * @return the transpose of {@code this}.
   */
  E transpose();

  /**
   * Create a copy of this matrix.
   *
   * @return the copy
   */
  E copy();

  BitArray lt(E other);

  BitArray gt(E other);

  BitArray eq(E other);

  BitArray lte(E other);

  BitArray gte(E other);

}
