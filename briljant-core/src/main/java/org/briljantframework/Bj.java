package org.briljantframework;

import org.briljantframework.complex.Complex;
import org.briljantframework.distribution.Distribution;
import org.briljantframework.distribution.NormalDistribution;
import org.briljantframework.linalg.api.LinearAlgebraRoutines;
import org.briljantframework.matrix.BitMatrix;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.Dim;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.LongMatrix;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.Range;
import org.briljantframework.matrix.T;
import org.briljantframework.matrix.api.MatrixBackend;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.api.MatrixRoutines;
import org.briljantframework.sort.IndexComparator;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.StreamSupport;

/**
 * @author Isak Karlsson
 */
public final class Bj {

  private static final Distribution normalDistribution = new NormalDistribution(0, 1);
  private static final MatrixFactory MATRIX_FACTORY;
  private static final MatrixRoutines MATRIX_ROUTINES;

  public static final LinearAlgebraRoutines linalg;

  static {
    MatrixBackend backend =
        StreamSupport.stream(ServiceLoader.load(MatrixBackend.class).spliterator(), false)
            .filter(MatrixBackend::isAvailable)
            .sorted((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
            .findFirst()
            .orElseThrow(() -> new UnsupportedOperationException(String.format(
                "Unable to load MatrixBackend. No available matrix backend registered.")));

    MATRIX_FACTORY = backend.getMatrixFactory();
    MATRIX_ROUTINES = backend.getMatrixRoutines();
    linalg = backend.getLinearAlgebraRoutines();
  }

  private Bj() {
  }

  public static Matrix<?> matrix(Collection<? extends Number> values) {
    Check.argument(values.size() > 0);
    Iterator<? extends Number> it = values.iterator();
    Number v = it.next();
    if (v instanceof Double || v instanceof BigDecimal) {
      DoubleMatrix m = doubleVector(values.size());
      int i = 0;
      for (Number value : values) {
        m.set(i++, value.doubleValue());
      }
      return m;
    } else {
      LongMatrix m = longVector(values.size());
      int i = 0;
      for (Number value : values) {
        m.set(i++, value.longValue());
      }
      return m;
    }
  }

  public static DoubleMatrix ones(int size) {
    return doubleVector(size).assign(1);
  }

  public static double trace(DoubleMatrix x) {
    return MATRIX_ROUTINES.trace(x);
  }

  public static IntMatrix randi(int size, int l, int u) {
    return MATRIX_FACTORY.randi(size, l, u);
  }

  public static ComplexMatrix complexMatrix(double[] data) {
    return MATRIX_FACTORY.complexMatrix(data);
  }

  public static IntMatrix matrix(int[][] data) {
    return MATRIX_FACTORY.matrix(data);
  }

  public static LongMatrix matrix(long[][] data) {
    return MATRIX_FACTORY.matrix(data);
  }

  public static LongMatrix longMatrix(int rows, int columns) {
    return MATRIX_FACTORY.longMatrix(rows, columns);
  }

  public static DoubleMatrix matrix(double[] data) {
    return MATRIX_FACTORY.matrix(data);
  }

  public static Range range(int start, int end) {
    return MATRIX_FACTORY.range(start, end);
  }

  public static DoubleMatrix matrix(double[][] data) {
    return MATRIX_FACTORY.matrix(data);
  }

  public static LongMatrix matrix(long[] data) {
    return MATRIX_FACTORY.matrix(data);
  }

  public static BitMatrix matrix(boolean[][] data) {
    return MATRIX_FACTORY.matrix(data);
  }

  public static IntMatrix intVector(int size) {
    return MATRIX_FACTORY.intVector(size);
  }

  public static IntMatrix intMatrix(int rows, int columns) {
    return MATRIX_FACTORY.intMatrix(rows, columns);
  }

  public static DoubleMatrix eye(int size) {
    return MATRIX_FACTORY.eye(size);
  }

  public static DoubleMatrix doubleVector(int size) {
    return MATRIX_FACTORY.doubleVector(size);
  }

  public static DoubleMatrix linspace(double start, double end, int size) {
    return MATRIX_FACTORY.linspace(start, end, size);
  }

  public static ComplexMatrix matrix(
      Complex[][] data) {
    return MATRIX_FACTORY.matrix(data);
  }

  public static BitMatrix booleanVector(int size) {
    return MATRIX_FACTORY.booleanVector(size);
  }

  public static DoubleMatrix rand(int size,
                                  Distribution distribution) {
    return MATRIX_FACTORY.rand(size, distribution);
  }

  public static DoubleMatrix randn(int size) {
    return rand(size, normalDistribution);
  }

  public static ComplexMatrix complexMatrix(int rows, int columns) {
    return MATRIX_FACTORY.complexMatrix(rows, columns);
  }

  public static LongMatrix longVector(int size) {
    return MATRIX_FACTORY.longVector(size);
  }

  public static IntMatrix randi(int size,
                                Distribution distribution) {
    return MATRIX_FACTORY.randi(size, distribution);
  }

  public static ComplexMatrix matrix(Complex[] data) {
    return MATRIX_FACTORY.matrix(data);
  }

  public static DoubleMatrix diag(double[] data) {
    return MATRIX_FACTORY.diag(data);
  }

  public static ComplexMatrix complexVector(int size) {
    return MATRIX_FACTORY.complexVector(size);
  }

  public static IntMatrix matrix(int[] data) {
    return MATRIX_FACTORY.matrix(data);
  }

  public static BitMatrix matrix(boolean[] data) {
    return MATRIX_FACTORY.matrix(data);
  }

  public static BitMatrix booleanMatrix(int rows, int columns) {
    return MATRIX_FACTORY.booleanMatrix(rows, columns);
  }

  public static Range range(int end) {
    return MATRIX_FACTORY.range(end);
  }

  public static Range range(int start, int end, int step) {
    return MATRIX_FACTORY.range(start, end, step);
  }

  public static DoubleMatrix doubleMatrix(int rows, int columns) {
    return MATRIX_FACTORY.doubleMatrix(rows, columns);
  }

  public static MatrixFactory getMatrixFactory() {
    return MATRIX_FACTORY;
  }

  public static <T extends Matrix<T>> List<T> hsplit(T matrix,
                                                     int parts) {
    return MATRIX_ROUTINES.hsplit(matrix, parts);
  }

  public static DoubleMatrix max(DoubleMatrix x, Dim dim) {
    return MATRIX_ROUTINES.max(x, dim);
  }

  public static <T extends Matrix<T>> T hstack(Collection<T> matrices) {
    return MATRIX_ROUTINES.hstack(matrices);
  }

  public static int iamax(DoubleMatrix x) {
    return MATRIX_ROUTINES.iamax(x);
  }

  public static double sum(DoubleMatrix x) {
    return MATRIX_ROUTINES.sum(x);
  }

  public static double min(DoubleMatrix x) {
    return MATRIX_ROUTINES.min(x);
  }

  public static double std(DoubleMatrix x) {
    return MATRIX_ROUTINES.std(x);
  }

  public static double var(DoubleMatrix x) {
    return MATRIX_ROUTINES.var(x);
  }

  public static void ger(double alpha, DoubleMatrix x, DoubleMatrix y, DoubleMatrix a) {
    MATRIX_ROUTINES.ger(alpha, x, y, a);
  }

  public static double asum(DoubleMatrix a, DoubleMatrix b) {
    return MATRIX_ROUTINES.asum(a);
  }

  public static DoubleMatrix var(DoubleMatrix x, Dim dim) {
    return MATRIX_ROUTINES.var(x, dim);
  }

  public static void gemm(T transA,
                          T transB, double alpha,
                          DoubleMatrix a, DoubleMatrix b, double beta,
                          DoubleMatrix c) {
    MATRIX_ROUTINES.gemm(transA, transB, alpha, a, b, beta, c);
  }

  public static DoubleMatrix mean(DoubleMatrix x, Dim dim) {
    return MATRIX_ROUTINES.mean(x, dim);
  }

  public static <T extends Matrix<T>> List<T> vsplit(T matrix, int parts) {
    return MATRIX_ROUTINES.vsplit(matrix, parts);
  }

  public static double dot(DoubleMatrix a, DoubleMatrix b) {
    return MATRIX_ROUTINES.dot(a, b);
  }

  public static Complex norm2(ComplexMatrix a, ComplexMatrix b) {
    return MATRIX_ROUTINES.norm2(a);
  }

  public static double prod(DoubleMatrix x) {
    return MATRIX_ROUTINES.prod(x);
  }

  public static double max(DoubleMatrix x) {
    return MATRIX_ROUTINES.max(x);
  }

  public static DoubleMatrix std(DoubleMatrix x, Dim dim) {
    return MATRIX_ROUTINES.std(x, dim);
  }

  public static DoubleMatrix min(DoubleMatrix x, Dim dim) {
    return MATRIX_ROUTINES.min(x, dim);
  }

  public static double mean(DoubleMatrix x) {
    return MATRIX_ROUTINES.mean(x);
  }

  public static double norm2(DoubleMatrix a, DoubleMatrix b) {
    return MATRIX_ROUTINES.norm2(a);
  }

  public static DoubleMatrix sum(DoubleMatrix x, Dim dim) {
    return MATRIX_ROUTINES.sum(x, dim);
  }

  public static double asum(ComplexMatrix a, ComplexMatrix b) {
    return MATRIX_ROUTINES.asum(a);
  }

  public static DoubleMatrix prod(DoubleMatrix x, Dim dim) {
    return MATRIX_ROUTINES.prod(x, dim);
  }

  public static Complex dotc(ComplexMatrix a, ComplexMatrix b) {
    return MATRIX_ROUTINES.dotc(a, b);
  }

  public static void gemv(T transA, double alpha, DoubleMatrix a, DoubleMatrix x,
                          double beta,
                          DoubleMatrix y) {
    MATRIX_ROUTINES.gemv(transA, alpha, a, x, beta, y);
  }

  public static <T extends Matrix<T>> T repeat(T x, int num) {
    return MATRIX_ROUTINES.repeat(x, num);
  }

  public static DoubleMatrix cumsum(DoubleMatrix x) {
    return MATRIX_ROUTINES.cumsum(x);
  }

  public static <T extends Matrix<T>> void copy(T from, T to) {
    MATRIX_ROUTINES.copy(from, to);
  }

  public static <T extends Matrix<T>> T vstack(Collection<T> matrices) {
    return MATRIX_ROUTINES.vstack(matrices);
  }

  public static Complex dotu(ComplexMatrix a, ComplexMatrix b) {
    return MATRIX_ROUTINES.dotu(a, b);
  }

  public static int iamax(ComplexMatrix x) {
    return MATRIX_ROUTINES.iamax(x);
  }

  public static DoubleMatrix cumsum(DoubleMatrix x, Dim dim) {
    return MATRIX_ROUTINES.cumsum(x, dim);
  }

  public static <T extends Matrix<T>> void swap(T a, T b) {
    MATRIX_ROUTINES.swap(a, b);
  }

  public static <T extends Matrix<T>> T sort(T x, IndexComparator<T> cmp) {
    return MATRIX_ROUTINES.sort(x, cmp);
  }

  public static <T extends Matrix<T>> T sort(T x, IndexComparator<T> cmp, Dim dim) {
    return MATRIX_ROUTINES.sort(x, cmp, dim);
  }

  public static <T extends Matrix<T>> T shuffle(T x) {
    return MATRIX_ROUTINES.shuffle(x);
  }

  public static void transpose(DoubleMatrix x) {
    MATRIX_ROUTINES.transpose(x);
  }

  public static <T extends Matrix<T>> T repmat(T x, int n) {
    return MATRIX_ROUTINES.repmat(x, n);
  }

  public static double asum(DoubleMatrix a) {
    return MATRIX_ROUTINES.asum(a);
  }

  public static double norm2(DoubleMatrix a) {
    return MATRIX_ROUTINES.norm2(a);
  }

  public static double asum(ComplexMatrix a) {
    return MATRIX_ROUTINES.asum(a);
  }

  public static <T extends Matrix<T>> T take(T x, int num) {
    return MATRIX_ROUTINES.take(x, num);
  }

  public static void scal(double alpha, DoubleMatrix x) {
    MATRIX_ROUTINES.scal(alpha, x);
  }

  public static void axpy(double alpha, DoubleMatrix x, DoubleMatrix y) {
    MATRIX_ROUTINES.axpy(alpha, x, y);
  }

  public static <T extends Matrix<T>> T repmat(T x, int r, int c) {
    return MATRIX_ROUTINES.repmat(x, r, c);
  }

  public static Complex norm2(ComplexMatrix a) {
    return MATRIX_ROUTINES.norm2(a);
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
    Check.argument(matrix.size() > 0);
    int index = 0;
    double smallest = matrix.get(0);
    int n = matrix.size();
    for (int i = 1; i < n; i++) {
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
    return map(in, x -> Math.log(x) / Matrices.LOG_2);
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
    return longMatrix(in.rows(), in.columns()).assign(in, Math::round);
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
