package org.briljantframework.matrix;

import org.briljantframework.Check;
import org.briljantframework.Utils;
import org.briljantframework.matrix.netlib.NetlibMatrixFactory;

import java.util.function.DoubleUnaryOperator;

/**
 * @author Isak Karlsson
 */
public final class Matrices {

  public static final double LOG_2 = Math.log(2);

  private final static org.briljantframework.matrix.api.MatrixFactory
      bj = NetlibMatrixFactory.getInstance();


  private Matrices() {
  }


  /**
   * @param matrix the matrix
   * @return the index of the maximum value
   */
  public static int argmax(DoubleMatrix matrix) {
    int index = 0;
    double largest = matrix.get(0);
    for (int i = 1; i < matrix.size(); i++) {
      double v = matrix.get(i);
      if (v > largest) {
        index = i;
        largest = v;
      }
    }
    return index;
  }

  /**
   * @param matrix the matrix
   * @return the index of the minimum value
   */
  public static int argmin(DoubleMatrix matrix) {
    int index = 0;
    double smallest = matrix.get(0);
    for (int i = 1; i < matrix.size(); i++) {
      double v = matrix.get(i);
      if (v < smallest) {
        smallest = v;
        index = i;
      }
    }
    return index;
  }

  /**
   * <p>
   * Take values in {@code a}, using the indexes in {@code indexes}. For example,
   * </p>
   *
   * @param a       the source matrix
   * @param indexes the indexes of the values to extract
   * @return a new matrix; the returned matrix has the same type as {@code a} (as returned by
   * {@link org.briljantframework.matrix.Matrix#newEmptyMatrix(int, int)}).
   */
  public static <T extends Matrix<T>> T take(T a, IntMatrix indexes) {
    T taken = a.newEmptyVector(indexes.size());
    for (int i = 0; i < indexes.size(); i++) {
      taken.set(i, a, indexes.get(i));
    }
    a.slice(indexes.flat());
    return taken;
  }

  /**
   * <p>
   * Changes the values of a copy of {@code a} according to the values of the {@code mask} and the
   * values in {@code values}. The value at {@code i} in a copy of {@code a} is set to value at
   * {@code i} from {@code values} if the boolean at {@code i} in {@code mask} is {@code true}.
   * </p>
   *
   * @param a      a source array
   * @param mask   the mask; same shape as {@code a}
   * @param values the values; same shape as {@code a}
   * @return a new matrix; the returned matrix has the same type as {@code a}.
   */
  public static <T extends Matrix<T>> T mask(T a, BitMatrix mask, T values) {
    Check.equalShape(a, mask);
    Check.equalShape(a, values);

    T masked = a.copy();
    putMask(masked, mask, values);
    return masked;
  }

  /**
   * <p>
   * Changes the values of {@code a} according to the values of the {@code mask} and the values in
   * {@code values}.
   * </p>
   *
   * @param a      the target matrix
   * @param mask   the mask; same shape as {@code a}
   * @param values the mask; same shape as {@code a}
   */
  public static <T extends Matrix<T>> void putMask(T a, BitMatrix mask, T values) {
    Check.equalShape(a, mask);
    Check.equalShape(a, values);
    for (int i = 0; i < a.size(); i++) {
      if (mask.get(i)) {
        a.set(i, values, i);
      }
    }
  }

  /**
   * <p>
   * Selects the values in {@code a} according to the values in {@code where}, replacing those not
   * selected with {@code replace}.
   * </p>
   *
   * @param a       the source matrix
   * @param where   the selection matrix; same shape as {@code a}
   * @param replace the replacement value
   * @return a new matrix; the returned matrix has the same type as {@code a}.
   */
  public static IntMatrix select(IntMatrix a, BitMatrix where, int replace) {
    Check.equalShape(a, where);
    return a.copy().assign(where, (b, i) -> b ? replace : i);
  }

  // /**
  // * <pre>
  // * > import org.briljantframework.matrix.*;
  // * DoubleMatrix a = Doubles.randn(10, 1)
  // * DoubleMatrix x = Anys.sort(a).asDoubleMatrix()
  // *
  // * -1.8718
  // * -0.8834
  // * -0.6161
  // * -0.0953
  // * 0.0125
  // * 0.3538
  // * 0.4326
  // * 0.4543
  // * 1.0947
  // * 1.1936
  // * shape: 10x1 type: double
  // * </pre>
  // *
  // * @param matrix the source matrix
  // * @return a new matrix; the returned matrix has the same type as {@code a}
  // */
  // public static Matrix sort(Matrix matrix) {
  // return sort(matrix, Matrix::compare);
  // }

  /**
   * <p>
   * Create a vector of length {@code num} with evenly spaced values between {@code start} and
   * {@code end}.
   * </p>
   *
   * @param start the start value
   * @param stop  the end value
   * @param num   the number of steps (i.e. intermediate values)
   * @return a vector
   */
  public static DoubleMatrix linspace(double start, double stop, int num) {
    DoubleMatrix values = bj.doubleVector(num);
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
    return bj.longMatrix(in.rows(), in.columns()).assign(in, Math::round);
  }

  public static double trace(DoubleMatrix matrix) {
    int min = Math.min(matrix.rows(), matrix.columns());
    double sum = 0;
    for (int i = 0; i < min; i++) {
      sum += matrix.get(i, i);
    }
    return sum;
  }

  public static long sum(LongMatrix matrix) {
    return matrix.reduce(0, Long::sum);
  }

  public static double sum(DoubleMatrix matrix) {
    return matrix.reduce(0, Double::sum);
  }

  public static int sum(IntMatrix matrix) {
    return matrix.reduce(0, Integer::sum);
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
   * @param m   the m
   * @param dim the axis
   * @return the t
   */
  public static DoubleMatrix sum(DoubleMatrix m, Dim dim) {
    switch (dim) {
      case R:
        return m.reduceRows(Matrices::sum);
      case C:
        return m.reduceColumns(Matrices::sum);
      default:
        throw new IllegalArgumentException();
    }
  }

  public static <T extends Matrix> T shuffle(T matrix) {
    Utils.permute(matrix.size(), matrix);
    return matrix;
  }

  /**
   * Computes the mean of the matrix.
   *
   * @param matrix the matrix
   * @return the mean
   */
  public static double mean(DoubleMatrix matrix) {
    return matrix.reduce(0, Double::sum) / matrix.size();
  }

  /**
   * @param matrix the matrix
   * @param dim    the axis
   * @return a mean matrix; if {@code axis == ROW} with shape = {@code [1, columns]}; or
   * {@code axis == COLUMN} with shape {@code [rows, 1]}.
   */
  public static DoubleMatrix mean(DoubleMatrix matrix, Dim dim) {
    if (dim == Dim.R) {
      return matrix.reduceRows(Matrices::mean);
    } else {
      return matrix.reduceColumns(Matrices::mean);
    }
  }

  /**
   * @param vector the vector
   * @return the standard deviation
   */
  public static double std(DoubleMatrix vector) {
    return std(vector, mean(vector));
  }

  /**
   * @param vector the vector
   * @param mean   the mean
   * @return the standard deviation
   */
  public static double std(DoubleMatrix vector, double mean) {
    double var = var(vector, mean);
    return Math.sqrt(var / (vector.size() - 1));
  }

  public static DoubleMatrix std(DoubleMatrix matrix, Dim dim) {
    if (dim == Dim.R) {
      return matrix.reduceRows(Matrices::std);
    } else {
      return matrix.reduceColumns(Matrices::std);
    }
  }

  /**
   * @param matrix the vector
   * @param mean   the mean
   * @return the variance
   */
  public static double var(DoubleMatrix matrix, double mean) {
    return matrix.reduce(0, (v, acc) -> acc + (v - mean) * (v - mean));
  }

  /**
   * @param vector the vector
   * @return the variance
   */
  public static double var(DoubleMatrix vector) {
    return var(vector, mean(vector));
  }

  public static DoubleMatrix var(DoubleMatrix matrix, Dim dim) {
    if (dim == Dim.R) {
      return matrix.reduceRows(Matrices::var);
    } else {
      return matrix.reduceColumns(Matrices::var);
    }
  }

  public static int argmaxnot(DoubleMatrix m, int not) {
    double max = Double.NEGATIVE_INFINITY;
    int argMax = -1;
    for (int i = 0; i < m.size(); i++) {
      if (not != i && m.get(i) > max) {
        argMax = i;
        max = m.get(i);
      }
    }
    return argMax;
  }

  public static double maxnot(DoubleMatrix m, int not) {
    double max = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < m.size(); i++) {
      if (not != i && m.get(i) > max) {
        max = m.get(i);
      }
    }
    return max;
  }
}
