package org.briljantframework.matrix;

import static com.google.common.primitives.Ints.checkedCast;

import java.util.Map;
import java.util.Random;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntPredicate;

import org.briljantframework.IndexComparator;
import org.briljantframework.QuickSort;
import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.TypeConversionException;
import org.briljantframework.matrix.storage.Storage;

import com.google.common.collect.ImmutableMap;

/**
 * @author Isak Karlsson
 */
public final class Matrices {

  /**
   * The constant RANDOM.
   */
  public static final Random RANDOM = Utils.getRandom();
  /**
   * The constant LOG_2.
   */
  public static final double LOG_2 = Math.log(2);
  private final static Map<Class, MatrixFactory<?>> NATIVE_TO_FACTORY;
  static {
    NATIVE_TO_FACTORY = ImmutableMap.of(Double.class, new MatrixFactory<Double>() {
      @Override
      public Matrix newVector(int size) {
        return new DefaultDoubleMatrix(size);
      }

      @Override
      public Matrix newMatrix(int rows, int columns) {
        return new DefaultDoubleMatrix(rows, columns);
      }

      @Override
      public Matrix newVectorFilledWith(int size, Double fill) {
        return new DefaultDoubleMatrix(size).assign(fill);
      }

      @Override
      public Matrix newMatrixFilledWith(int rows, int columns, Double fill) {
        return new DefaultDoubleMatrix(rows, columns).assign(fill);
      }
    }, Integer.class, new MatrixFactory<Number>() {
      @Override
      public Matrix newVector(int size) {
        return new DefaultIntMatrix(size);
      }

      @Override
      public Matrix newMatrix(int rows, int columns) {
        return new DefaultIntMatrix(rows, columns);
      }

      @Override
      public Matrix newVectorFilledWith(int size, Number fill) {
        return new DefaultIntMatrix(size).assign(fill.intValue());
      }

      @Override
      public Matrix newMatrixFilledWith(int rows, int columns, Number fill) {
        return new DefaultIntMatrix(rows, columns).assign(fill.intValue());
      }
    }, Complex.class, new MatrixFactory<Complex>() {
      @Override
      public Matrix newVector(int size) {
        return new DefaultComplexMatrix(size);
      }

      @Override
      public Matrix newMatrix(int rows, int columns) {
        return new DefaultComplexMatrix(rows, columns);
      }

      @Override
      public Matrix newVectorFilledWith(int size, Complex fill) {
        return new DefaultComplexMatrix(size, fill);
      }

      @Override
      public Matrix newMatrixFilledWith(int rows, int columns, Complex fill) {
        return new DefaultComplexMatrix(rows, columns, fill);
      }
    }, Boolean.class, new MatrixFactory<Boolean>() {
      @Override
      public Matrix newVector(int size) {
        return new DefaultBitMatrix(size);
      }

      @Override
      public Matrix newMatrix(int rows, int columns) {
        return new DefaultBitMatrix(rows, columns);
      }

      @Override
      public Matrix newVectorFilledWith(int size, Boolean fill) {
        return new DefaultBitMatrix(size, fill);
      }

      @Override
      public Matrix newMatrixFilledWith(int rows, int columns, Boolean fill) {
        return new DefaultBitMatrix(rows, columns, fill);
      }
    }, Long.class, new MatrixFactory<Long>() {
      @Override
      public Matrix newVector(int size) {
        return new DefaultLongMatrix(size);
      }

      @Override
      public Matrix newMatrix(int rows, int columns) {
        return new DefaultLongMatrix(rows, columns);
      }

      @Override
      public Matrix newVectorFilledWith(int size, Long fill) {
        return new DefaultLongMatrix(size).assign(fill);
      }

      @Override
      public Matrix newMatrixFilledWith(int rows, int columns, Long fill) {
        return new DefaultLongMatrix(rows, columns).assign(fill);
      }
    });
  }

  private Matrices() {}

  @SuppressWarnings("unchecked")
  public static <T> MatrixFactory<T> getMatrixFactory(Class<T> type) {
    MatrixFactory<T> f = (MatrixFactory<T>) NATIVE_TO_FACTORY.get(type);
    if (f == null) {
      throw new TypeConversionException(type.toString());
    }
    return f;
  }

  /**
   * <p>
   * Returns a new {@code BitMatrix} with {@code values}.
   * </p>
   * 
   * <p>
   * For example
   * </p>
   * 
   * <pre>
   *  > BitMatrix a = Matrices.newBitMatrix(true, true, false, false, true, true).reshape(2, 3);
   *    
   *    true  false  true
   *    true  false  true  
   *    shape: 2x3 type: boolean
   * </pre>
   * 
   * @param values an array of booleans
   * @return a new boolean vector
   */
  public static BitMatrix newBitMatrix(boolean... values) {
    return new DefaultBitMatrix(values);
  }

  /**
   * Return a new empty (all elements are {@code false}), {@code BitMatrix} (column-vector) of
   * {@code size}.
   * 
   * @param size size
   * @return a new boolean vector
   */
  public static BitMatrix newBitVector(int size) {
    return new DefaultBitMatrix(size);
  }

  /**
   * Return a new empty (all elements are {@code false}) {@code BitMatrix} of {@code rows} and
   * {@code columns}.
   * 
   * @param rows the rows
   * @param cols the columns
   * @return a new boolean matrix
   */
  public static BitMatrix newBitMatrix(int rows, int cols) {
    return new DefaultBitMatrix(rows, cols);
  }

  public static DoubleMatrix newDoubleMatrix(double... values) {
    return new DefaultDoubleMatrix(values);
  }

  public static DoubleMatrix newDoubleVector(int size) {
    return new DefaultDoubleMatrix(size);
  }

  public static DoubleMatrix newDoubleMatrix(int rows, int columns) {
    return new DefaultDoubleMatrix(rows, columns);
  }

  public static IntMatrix newIntVector(int n) {
    return new DefaultIntMatrix(n);
  }

  public static IntMatrix newIntMatrix(int... values) {
    return new DefaultIntMatrix(values);
  }

  public static LongMatrix newLongMatrix(int rows, int columns) {
    return new DefaultLongMatrix(rows, columns);
  }

  public static LongMatrix newLongVector(int size) {
    return new DefaultLongMatrix(size);
  }

  public static Matrix zeros(int size, Class<?> type) {
    return getMatrixFactory(type).newVector(size);
  }

  public static Matrix zeros(int rows, int columns, Class<?> type) {
    return getMatrixFactory(type).newMatrix(rows, columns);
  }

  public static DoubleMatrix zeros(int size) {
    return newDoubleVector(size);
  }

  public static DoubleMatrix zeros(int rows, int columns) {
    return newDoubleMatrix(rows, columns);
  }

  public static DoubleMatrix ones(int size) {
    return zeros(size).assign(1);
  }

  public static DoubleMatrix ones(int rows, int columns) {
    return zeros(rows, columns).assign(1);
  }

  public static Matrix filledWith(int size, double value) {
    return zeros(size).assign(value);
  }

  public static Matrix filledWith(int rows, int columns, double value) {
    return zeros(rows, columns).assign(value);
  }

  public static <T> Matrix filledWith(int size, Class<T> type, T value) {
    return getMatrixFactory(type).newVectorFilledWith(size, value);
  }

  public static <T> Matrix filledWith(int rows, int columns, Class<T> type, T value) {
    return getMatrixFactory(type).newMatrixFilledWith(rows, columns, value);
  }

  public static DoubleMatrix randn(int rows, int cols) {
    return newDoubleMatrix(rows, cols).assign(RANDOM::nextGaussian);
  }

  public static DoubleMatrix randn(int size) {
    return newDoubleVector(size).assign(RANDOM::nextGaussian);
  }

  public static DoubleMatrix rand(int rows, int cols) {
    return newDoubleMatrix(rows, cols).assign(RANDOM::nextDouble);
  }

  public static DoubleMatrix rand(int size) {
    return newDoubleVector(size).assign(RANDOM::nextDouble);
  }

  /**
   * Diagonal identity matrix of {@code size}
   *
   * @param size the size
   * @return the identity matrix
   */
  public static DoubleMatrix eye(int size) {
    double[] diagonal = new double[size];
    for (int i = 0; i < size; i++) {
      diagonal[i] = 1;
    }
    return Diagonal.of(size, size, diagonal);
  }

  public static IntMatrix range(int start, int end) {
    return range(start, end, 1);
  }

  public static IntMatrix range(int start, int end, int step) {
    return Slice.slice(start, end, step).copy();
  }

  public static IntMatrix take(IntMatrix a, IntMatrix b) {
    return take((Matrix) a, b).asIntMatrix();
  }

  /**
   * <p>
   * Take values in {@code a}, using the indexes in {@code indexes}.
   * <p>
   * For example,
   * </p>
   * <p>
   *
   * <pre>
   *  > import org.briljantframework.matrix.*;
   *    IntMatrix a = Ints.newMatrix(1, 2, 3, 4);
   *    IntMatrix indexes = Ints.newMatrix(0, 0, 1, 2, 3);
   *    IntMatrix taken = Anys.take(a, indexes).asIntMatrix();
   *    1
   *    1
   *    2
   *    3
   *    4
   *    shape: 5x1 type: int
   * </pre>
   *
   * @param a the source matrix
   * @param indexes the indexes of the values to extract
   * @return a new matrix; the returned matrix has the same type as {@code a} (as returned by
   *         {@link org.briljantframework.matrix.Matrix#newEmptyMatrix(int, int)}).
   */
  public static Matrix take(Matrix a, IntMatrix indexes) {
    Matrix taken = a.newEmptyVector(indexes.size());
    for (int i = 0; i < indexes.size(); i++) {
      taken.set(i, a, indexes.get(i));
    }
    return taken;
  }

  /**
   * Changes the values of a copy of {@code a} according to the values of the {@code mask} and the
   * values in {@code values}. The value at {@code i} in a copy of {@code a} is set to value at
   * {@code i} from {@code values} if the boolean at {@code i} in {@code mask} is {@code true}.
   * <p>
   *
   * <pre>
   *  > import org.briljantframework.matrix.*;
   *    IntMatrix a = Ints.range(0, 10).reshape(5, 2)
   *    BitMatrix mask = a.greaterThan(5)
   *    IntMatrix values = a.mul(2)
   *    IntMatrix result = Anys.mask(a, mask, values).asIntMatrix()
   * 
   *    0   5
   *    1   12
   *    2   14
   *    3   16
   *    4   18
   *    shape: 5x2 type: int
   * </pre>
   *
   * @param a a source array
   * @param mask the mask; same shape as {@code a}
   * @param values the values; same shape as {@code a}
   * @return a new matrix; the returned matrix has the same type as {@code a}.
   */
  public static Matrix mask(Matrix a, BitMatrix mask, Matrix values) {
    Check.equalShape(a, mask);
    Check.equalShape(a, values);

    Matrix masked = a.copy();
    putMask(masked, mask, values);
    return masked;
  }

  /**
   * Changes the values of {@code a} according to the values of the {@code mask} and the values in
   * {@code values}.
   * <p>
   *
   * <pre>
   *  > import org.briljantframework.matrix.*;
   *    IntMatrix a = Ints.range(0, 10).reshape(5, 2)
   *    BitMatrix mask = a.greaterThan(5)
   *    IntMatrix values = a.mul(2)
   *    Anys.putMask(a, mask, values)
   *    System.out.println(a)
   * 
   *    0   5
   *    1   12
   *    2   14
   *    3   16
   *    4   18
   *    shape: 5x2 type: int
   * </pre>
   *
   * @param a the target matrix
   * @param mask the mask; same shape as {@code a}
   * @param values the mask; same shape as {@code a}
   * @see #mask(org.briljantframework.matrix.Matrix, org.briljantframework.matrix.BitMatrix,
   *      org.briljantframework.matrix.Matrix)
   */
  public static void putMask(Matrix a, BitMatrix mask, Matrix values) {
    Check.equalShape(a, mask);
    Check.equalShape(a, values);
    for (int i = 0; i < a.size(); i++) {
      if (mask.get(i)) {
        a.set(i, values, i);
      }
    }
  }

  /**
   * Selects the values in {@code a} according to the values in {@code where}, replacing those not
   * selected with {@code replace}.
   * <p>
   *
   * <pre>
   *  > import org.briljantframework.matrix.*;
   *    IntMatrix a = Ints.range(0, 10).reshape(5, 2)
   *    BitMatrix mask = a.greaterThan(5)
   *    IntMatrix b = Anys.select(a, mask, -1)
   * 
   *    -1  -1
   *    -1   6
   *    -1   7
   *    -1   8
   *    -1   9
   *    shape: 5x2 type: int
   * </pre>
   *
   * @param a the source matrix
   * @param where the selection matrix; same shape as {@code a}
   * @param replace the replacement value
   * @return a new matrix; the returned matrix has the same type as {@code a}.
   */
  public static Matrix select(Matrix a, BitMatrix where, Number replace) {
    Check.equalShape(a, where);
    Matrix copy = a.copy();
    Storage storage = copy.getStorage();
    for (int i = 0; i < a.size(); i++) {
      if (!where.get(i)) {
        // TODO: either do check on storage.getNativeType() or implement several select(..) methods.
        storage.setNumber(i, replace);
      }
    }
    return copy;
  }

  /**
   * Selects the values in {@code a} according to the values in {@code where}.
   * <p>
   *
   * <pre>
   *  > import org.briljantframework.matrix.*;
   *    IntMatrix a = Ints.range(0, 10).reshape(5, 2)
   *    BitMatrix mask = a.greaterThan(5)
   *    DoubleMatrix b = Anys.select(a.asDoubleMatrix(), mask).asDoubleMatrix()
   * 
   *    6.0000
   *    7.0000
   *    8.0000
   *    9.0000
   *    shape: 4x1 type: double
   * </pre>
   *
   * @param a the source matrix
   * @param where the selection matrix; same shape as {@code a}
   * @return a new matrix; the returned matrix has the same type as {@code a}
   */
  public static Matrix select(Matrix a, BitMatrix where) {
    Check.equalShape(a, where);
    Matrix.IncrementalBuilder builder = a.newIncrementalBuilder();
    for (int i = 0; i < a.size(); i++) {
      if (where.get(i)) {
        builder.add(a, i);
      }
    }
    return builder.build();
  }

  /**
   * <pre>
   *  > import org.briljantframework.matrix.*;
   *    DoubleMatrix a = Doubles.randn(10, 1)
   *    DoubleMatrix x = Anys.sort(a).asDoubleMatrix()
   * 
   *    -1.8718
   *    -0.8834
   *    -0.6161
   *    -0.0953
   *    0.0125
   *    0.3538
   *    0.4326
   *    0.4543
   *    1.0947
   *    1.1936
   *    shape: 10x1 type: double
   * </pre>
   *
   * @param matrix the source matrix
   * @return a new matrix; the returned matrix has the same type as {@code a}
   */
  public static Matrix sort(Matrix matrix) {
    return sort(matrix, Matrix::compare);
  }

  /**
   * <p>
   * Sorts the source matrix {@code a} in the order specified by {@code comparator}
   * </p>
   * For example, reversed sorted
   * <p>
   *
   * <pre>
   *  > import org.briljantframework.matrix.*;
   *    DoubleMatrix a = Doubles.randn(12, 1)
   *    DoubleMatrix x = Anys.sort(a, (c, i, j) -> -c.compare(a, b)).asDoubleMatrix()
   * </pre>
   * <p>
   * {@link org.briljantframework.complex.Complex} and {@link ComplexMatrix} do not have a natural
   * sort order.
   * <p>
   *
   * <pre>
   *  > import org.briljantframework.matrix.*;
   *    ComplexMatrix a = Doubles.randn(12, 1).asComplexMatrix().map(Complex::sqrt)
   *    ComplexMatrix x = Anys.sort(a, (c, i, j) -> Double.compare(c.getAsComplex(i).abs(),
   *        c.getAsComplex(j).abs()).asComplexMatrix()
   * 
   *    0.1499 + 0.0000i
   *    0.5478 + 0.0000i
   *    0.5725 + 0.0000i
   *    0.0000 + 0.5916i
   *    0.0000 + 0.6856i
   *    0.0000 + 0.8922i
   *    0.0000 + 0.9139i
   *    0.0000 + 1.0130i
   *    0.0000 + 1.1572i
   *    1.1912 + 0.0000i
   *    1.2493 + 0.0000i
   *    1.2746 + 0.0000i
   *    shape: 12x1 type: complex
   * </pre>
   *
   * @param a the source matrix
   * @param comparator the comparator; first argument is the container, and the next are indexes
   * @return a new sorted matrix; the returned matrix has the same type as {@code a}
   */
  public static Matrix sort(Matrix a, IndexComparator<? super Matrix> comparator) {
    Matrix out = a.copy();
    QuickSort.quickSort(0, checkedCast(out.size()), (x, y) -> comparator.compare(out, x, y), out);
    return out;
  }

  /**
   * Sort {@code a} each dimension, set by {@code axis}, in increasing order. For example, if
   * {@code axis == Axis.ROW}, each row is sorted in increasing order.
   * <p>
   * <p>
   *
   * <pre>
   *  > import org.briljantframework.matrix.*;
   *    DoubleMatrix a = Doubles.randn(12, 1).reshape(3,4)
   *    AnyMatrix x = Anys.sort(a, Axis.COLUMN)
   *    -0.2836   0.0603  -1.1870  -0.7840
   *    0.1644   0.2489   0.2159   0.6990
   *    0.4199   0.5131   0.9911   1.7952
   *    shape: 3x4 type: double
   * 
   *    AnyMatrix y = Anys.sort(a, Axis.ROW)
   *    -0.7840   0.0603   0.4199   0.9911
   *    -0.2836   0.2159   0.5131   1.7952
   *    -1.1870   0.1644   0.2489   0.6990
   *    shape: 3x4 type: double
   * </pre>
   *
   * @param a the source matrix
   * @param axis the axis to sort
   * @return a new matrix; the returned matrix has the same type as {@code a}
   */
  public static Matrix sort(Matrix a, Axis axis) {
    return sort(a, axis, Matrix::compare);
  }

  public static Matrix sort(Matrix a, Axis axis, IndexComparator<? super Matrix> comparator) {
    Matrix out = a.copy();
    if (axis == Axis.ROW) {
      for (int i = 0; i < a.rows(); i++) {
        Matrix row = out.getRowView(i);
        QuickSort.quickSort(0, checkedCast(row.size()), (x, y) -> comparator.compare(row, x, y),
            row);
      }
    } else {
      for (int i = 0; i < a.columns(); i++) {
        Matrix col = out.getColumnView(i);
        QuickSort.quickSort(0, checkedCast(col.size()), (x, y) -> comparator.compare(col, x, y),
            col);
      }
    }
    return out;
  }

  public static Matrix selectIndex(Matrix matrix, IntPredicate predicate) {
    Matrix.IncrementalBuilder builder = matrix.newIncrementalBuilder();
    for (int i = 0; i < matrix.size(); i++) {
      if (predicate.test(i)) {
        builder.add(matrix, i);
      }
    }
    return builder.build();
  }

  /**
   * <p>
   * Create a vector of length {@code num} with evenly spaced values between {@code start} and
   * {@code end}.
   * </p>
   *
   * @param start the start value
   * @param stop the end value
   * @param num the number of steps (i.e. intermediate values)
   * @return a vector
   */
  public static DoubleMatrix linspace(double start, double stop, int num) {
    DoubleMatrix values = newDoubleVector(num);
    double step = (stop - start) / (num - 1);
    double value = start;
    for (int index = 0; index < num; index++) {
      values.set(index, value);
      value += step;
    }
    return values;
  }

  public static DoubleMatrix map(DoubleMatrix in, DoubleUnaryOperator operator) {
    return in.newEmptyMatrix(in.rows(), in.columns()).assign(in, operator);
  }

  public static DoubleMatrix sqrt(DoubleMatrix matrix) {
    return map(matrix, Math::sqrt);
  }

  public static DoubleMatrix log(DoubleMatrix in) {
    return map(in, Math::log);
  }

  public static DoubleMatrix log2(DoubleMatrix in) {
    return map(in, x -> Math.log(x) / LOG_2);
  }

  public static DoubleMatrix pow(DoubleMatrix in, double power) {
    switch ((int) power) {
      case 2:
        return map(in, x -> x * x);
      case 3:
        return map(in, x -> x * x * x);
      case 4:
        return map(in, x -> x * x * x * x);
      default:
        return map(in, x -> Math.pow(x, power));
    }
  }

  public static DoubleMatrix log10(DoubleMatrix in) {
    return map(in, Math::log10);
  }

  public static DoubleMatrix signum(DoubleMatrix in) {
    return map(in, Math::signum);
  }

  public static DoubleMatrix abs(DoubleMatrix in) {
    return map(in, Math::abs);
  }

  public static LongMatrix round(DoubleMatrix in) {
    return newLongMatrix(in.rows(), in.columns()).assign(in, Math::round);
  }

  public static double trace(DoubleMatrix matrix) {
    int min = Math.min(matrix.rows(), matrix.columns());
    double sum = 0;
    for (int i = 0; i < min; i++) {
      sum += matrix.get(i, i);
    }
    return sum;
  }

  public static double sum(DoubleMatrix matrix) {
    double sum = 0;
    for (int i = 0; i < matrix.size(); i++) {
      sum += matrix.get(i);
    }
    return sum;
  }

  public static int sum(IntMatrix matrix) {
    int sum = 0;
    for (int i = 0; i < matrix.size(); i++) {
      sum += matrix.get(i);
    }
    return sum;
  }

  public static int sum(BitMatrix matrix) {
    int sum = 0;
    for (int i = 0; i < matrix.size(); i++) {
      sum += matrix.get(i) ? 1 : 0;
    }
    return sum;
  }

  /**
   * Sum t.
   *
   * @param m the m
   * @param axis the axis
   * @return the t
   */
  public static Matrix sum(Matrix m, Axis axis) {
    switch (axis) {
      case ROW:
        return rowSum(m);
      case COLUMN:
        return columnSum(m);
      default:
        throw new IllegalArgumentException();
    }
  }

  private static Matrix columnSum(Matrix m) {
    DoubleMatrix values = newDoubleMatrix(m.rows(), 1);
    for (int j = 0; j < m.columns(); j++) {
      for (int i = 0; i < m.rows(); i++) {
        values.set(i, values.get(i) + m.getAsDouble(i, j));
      }
    }
    return values;
  }

  private static Matrix rowSum(Matrix m) {
    DoubleMatrix values = newDoubleMatrix(1, m.columns());
    for (int j = 0; j < m.columns(); j++) {
      for (int i = 0; i < m.rows(); i++) {
        values.set(j, values.get(i) + m.getAsDouble(i, j));
      }
    }

    return values;
  }


  private static interface MatrixFactory<T> {
    Matrix newVector(int size);

    Matrix newMatrix(int rows, int columns);

    Matrix newVectorFilledWith(int size, T fill);

    Matrix newMatrixFilledWith(int rows, int columns, T fill);
  }
}
