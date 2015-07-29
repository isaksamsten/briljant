package org.briljantframework;

import org.briljantframework.array.Array;
import org.briljantframework.array.BaseArray;
import org.briljantframework.array.BitArray;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.array.LongArray;
import org.briljantframework.array.Matrices;
import org.briljantframework.array.Op;
import org.briljantframework.array.Range;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.array.api.ArrayRoutines;
import org.briljantframework.complex.Complex;
import org.briljantframework.distribution.Distribution;
import org.briljantframework.distribution.NormalDistribution;
import org.briljantframework.linalg.api.LinearAlgebraRoutines;
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
  private static final ArrayFactory MATRIX_FACTORY;
  private static final ArrayRoutines MATRIX_ROUTINES;

  public static final LinearAlgebraRoutines linalg;

  static {
    ArrayBackend backend =
        StreamSupport.stream(ServiceLoader.load(ArrayBackend.class).spliterator(), false)
            .filter(ArrayBackend::isAvailable)
            .sorted((a, b) -> Integer.compare(b.getPriority(), a.getPriority()))
            .findFirst()
            .orElseThrow(() -> new UnsupportedOperationException(String.format(
                "Unable to load MatrixBackend. No available matrix backend registered.")));

    MATRIX_FACTORY = backend.getArrayFactory();
    MATRIX_ROUTINES = backend.getArrayRoutines();
    linalg = backend.getLinearAlgebraRoutines();
  }

  private Bj() {
  }

  public static <T> Array<T> referenceArray(int... shape) {
    return MATRIX_FACTORY.referenceArray(shape);
  }

  public static <T> Array<T> array(T[] data) {
    return MATRIX_FACTORY.array(data);
  }

  public static <T> Array<T> array(T[][] data) {
    return MATRIX_FACTORY.array(data);
  }

  public static DoubleArray ones(int... shape) {
    return MATRIX_FACTORY.ones(shape);
  }

  public static DoubleArray array(double[][] data) {
    return MATRIX_FACTORY.array(data);
  }

  public static ComplexArray array(Complex[][] data) {
    return MATRIX_FACTORY.array(data);
  }

  public static BitArray booleanArray(int... shape) {
    return MATRIX_FACTORY.booleanArray(shape);
  }

  public static IntArray array(int[][] data) {
    return MATRIX_FACTORY.array(data);
  }

  public static LongArray longArray(int... shape) {
    return MATRIX_FACTORY.longArray(shape);
  }

  public static DoubleArray randn(int size) {
    return MATRIX_FACTORY.randn(size);
  }

  public static DoubleArray doubleArray(int... shape) {
    return MATRIX_FACTORY.doubleArray(shape);
  }

  public static DoubleArray array(double[] data) {
    return MATRIX_FACTORY.array(data);
  }

  public static IntArray array(int[] data) {
    return MATRIX_FACTORY.array(data);
  }

  public static Range range(int start, int end, int step) {
    return MATRIX_FACTORY.range(start, end, step);
  }

  public static ComplexArray complexArray(int... shape) {
    return MATRIX_FACTORY.complexArray(shape);
  }

  public static Range range(int end) {
    return MATRIX_FACTORY.range(end);
  }

  public static Range range() {
    return MATRIX_FACTORY.range();
  }

  public static DoubleArray linspace(double start, double end, int size) {
    return MATRIX_FACTORY.linspace(start, end, size);
  }

  public static ComplexArray array(Complex[] data) {
    return MATRIX_FACTORY.array(data);
  }

  public static IntArray intArray(int... shape) {
    return MATRIX_FACTORY.intArray(shape);
  }

  public static DoubleArray rand(int size, Distribution distribution) {
    return MATRIX_FACTORY.rand(size, distribution);
  }

  public static IntArray randi(int size, int l, int u) {
    return MATRIX_FACTORY.randi(size, l, u);
  }

  public static LongArray array(long[] data) {
    return MATRIX_FACTORY.array(data);
  }

  public static ComplexArray complexArray(double[] data) {
    return MATRIX_FACTORY.complexArray(data);
  }

  public static IntArray randi(int size, Distribution distribution) {
    return MATRIX_FACTORY.randi(size, distribution);
  }

  public static DoubleArray diag(DoubleArray data) {
    return MATRIX_FACTORY.diag(data);
  }

  public static BitArray array(boolean[][] data) {
    return MATRIX_FACTORY.array(data);
  }

  public static DoubleArray eye(int size) {
    return MATRIX_FACTORY.eye(size);
  }

  public static LongArray array(long[][] data) {
    return MATRIX_FACTORY.array(data);
  }

  public static DoubleArray zero(int... shape) {
    return MATRIX_FACTORY.zero(shape);
  }

  public static BitArray array(boolean[] data) {
    return MATRIX_FACTORY.array(data);
  }

  public static Range range(int start, int end) {
    return MATRIX_FACTORY.range(start, end);
  }

  public static double mean(DoubleArray x) {
    return MATRIX_ROUTINES.mean(x);
  }

  public static double min(DoubleArray x) {
    return MATRIX_ROUTINES.min(x);
  }

  public static <T extends BaseArray<T>> List<T> vsplit(T matrix, int parts) {
    return MATRIX_ROUTINES.vsplit(matrix, parts);
  }

  public static double asum(ComplexArray a) {
    return MATRIX_ROUTINES.asum(a);
  }

  public static double max(DoubleArray x) {
    return MATRIX_ROUTINES.max(x);
  }

  public static Complex norm2(ComplexArray a) {
    return MATRIX_ROUTINES.norm2(a);
  }

  public static int iamax(ComplexArray x) {
    return MATRIX_ROUTINES.iamax(x);
  }

  public static <T extends BaseArray<T>> void copy(T from, T to) {
    MATRIX_ROUTINES.copy(from, to);
  }

  public static double var(DoubleArray x) {
    return MATRIX_ROUTINES.var(x);
  }

  public static <T extends BaseArray<T>> T take(T x, int num) {
    return MATRIX_ROUTINES.take(x, num);
  }

  public static <T extends BaseArray<T>> T repmat(T x, int r, int c) {
    return MATRIX_ROUTINES.repmat(x, r, c);
  }

  public static <T extends BaseArray<T>> void swap(T a, T b) {
    MATRIX_ROUTINES.swap(a, b);
  }

  public static void gemv(Op transA, double alpha, DoubleArray a, DoubleArray x, double beta,
                          DoubleArray y) {
    MATRIX_ROUTINES.gemv(transA, alpha, a, x, beta, y);
  }

  public static double norm2(DoubleArray a) {
    return MATRIX_ROUTINES.norm2(a);
  }

  public static double prod(DoubleArray x) {
    return MATRIX_ROUTINES.prod(x);
  }

  public static DoubleArray std(int dim, DoubleArray x) {
    return MATRIX_ROUTINES.std(dim, x);
  }

  public static double sum(DoubleArray x) {
    return MATRIX_ROUTINES.sum(x);
  }

  public static double dot(DoubleArray a, DoubleArray b) {
    return MATRIX_ROUTINES.dot(a, b);
  }

  public static Complex dotc(ComplexArray a, ComplexArray b) {
    return MATRIX_ROUTINES.dotc(a, b);
  }

  public static <T extends BaseArray<T>> T repmat(T x, int n) {
    return MATRIX_ROUTINES.repmat(x, n);
  }

  public static DoubleArray sum(int dim, DoubleArray x) {
    return MATRIX_ROUTINES.sum(dim, x);
  }

  public static <T extends BaseArray<T>> T sort(T x, IndexComparator<T> cmp, int dim) {
    return MATRIX_ROUTINES.sort(x, cmp, dim);
  }

  public static DoubleArray min(int dim, DoubleArray x) {
    return MATRIX_ROUTINES.min(dim, x);
  }

  public static void scal(double alpha, DoubleArray x) {
    MATRIX_ROUTINES.scal(alpha, x);
  }

  public static int iamax(DoubleArray x) {
    return MATRIX_ROUTINES.iamax(x);
  }

  public static DoubleArray var(int dim, DoubleArray x) {
    return MATRIX_ROUTINES.var(dim, x);
  }

  public static <T extends BaseArray<T>> T shuffle(T x) {
    return MATRIX_ROUTINES.shuffle(x);
  }

  public static <T extends BaseArray<T>> T vstack(Collection<T> matrices) {
    return MATRIX_ROUTINES.vstack(matrices);
  }

  public static double std(DoubleArray x) {
    return MATRIX_ROUTINES.std(x);
  }

  public static double trace(DoubleArray x) {
    return MATRIX_ROUTINES.trace(x);
  }

  public static DoubleArray cumsum(DoubleArray x) {
    return MATRIX_ROUTINES.cumsum(x);
  }

  public static void axpy(double alpha, DoubleArray x, DoubleArray y) {
    MATRIX_ROUTINES.axpy(alpha, x, y);
  }

  public static Complex dotu(ComplexArray a, ComplexArray b) {
    return MATRIX_ROUTINES.dotu(a, b);
  }

  public static DoubleArray mean(int dim, DoubleArray x) {
    return MATRIX_ROUTINES.mean(dim, x);
  }

  public static <T extends BaseArray<T>> T hstack(Collection<T> matrices) {
    return MATRIX_ROUTINES.hstack(matrices);
  }

  public static <T extends BaseArray<T>> T sort(T x, IndexComparator<T> cmp) {
    return MATRIX_ROUTINES.sort(x, cmp);
  }

  public static void ger(double alpha, DoubleArray x, DoubleArray y,
                         DoubleArray a) {
    MATRIX_ROUTINES.ger(alpha, x, y, a);
  }

  public static <T extends BaseArray<T>> T repeat(T x, int num) {
    return MATRIX_ROUTINES.repeat(x, num);
  }

  public static <T extends BaseArray<T>> List<T> hsplit(T matrix, int parts) {
    return MATRIX_ROUTINES.hsplit(matrix, parts);
  }

  public static DoubleArray cumsum(DoubleArray x, int dim) {
    return MATRIX_ROUTINES.cumsum(x, dim);
  }

  public static double asum(DoubleArray a) {
    return MATRIX_ROUTINES.asum(a);
  }

  public static void gemm(Op transA, Op transB, double alpha, DoubleArray a,
                          DoubleArray b, double beta, DoubleArray c) {
    MATRIX_ROUTINES.gemm(transA, transB, alpha, a, b, beta, c);
  }

  public static DoubleArray max(int dim, DoubleArray x) {
    return MATRIX_ROUTINES.max(dim, x);
  }

  public static DoubleArray prod(int dim, DoubleArray x) {
    return MATRIX_ROUTINES.prod(dim, x);
  }

  /**
   * @param matrix the matrix
   * @return the index of the maximum value
   */
  public static int argmax(DoubleArray matrix) {
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
  public static int argmin(DoubleArray matrix) {
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
   */
  public static <T extends BaseArray<T>> T take(T a, IntArray indexes) {
    T taken = a.newEmptyArray(indexes.size());
    for (int i = 0; i < indexes.size(); i++) {
      taken.set(i, a, indexes.get(i));
    }
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
  public static <T extends BaseArray<T>> T mask(T a, BitArray mask, T values) {
    Check.shape(a, mask);
    Check.shape(a, values);

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
  public static <T extends BaseArray<T>> void putMask(T a, BitArray mask, T values) {
    Check.shape(a, mask);
    Check.shape(a, values);
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
  public static IntArray select(IntArray a, BitArray where, int replace) {
    Check.shape(a, where);
    return a.copy().assign(where, (b, i) -> b ? replace : i);
  }

  public static DoubleArray map(DoubleArray in, DoubleUnaryOperator operator) {
    return in.newEmptyArray(in.rows(), in.columns()).assign(in, operator);
  }

  public static DoubleArray sqrt(DoubleArray matrix) {
    return map(matrix, Math::sqrt);
  }

  public static DoubleArray log(DoubleArray in) {
    return map(in, Math::log);
  }

  public static DoubleArray log2(DoubleArray in) {
    return map(in, x -> Math.log(x) / Matrices.LOG_2);
  }

  public static DoubleArray pow(DoubleArray in, double power) {
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

  public static DoubleArray log10(DoubleArray in) {
    return map(in, Math::log10);
  }

  public static DoubleArray signum(DoubleArray in) {
    return map(in, Math::signum);
  }

  public static DoubleArray abs(DoubleArray in) {
    return map(in, Math::abs);
  }

  public static LongArray round(DoubleArray in) {
    return longArray(in.getShape()).assign(in, Math::round);
  }

  public static int argmaxnot(DoubleArray m, int not) {
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

  public static double maxnot(DoubleArray m, int not) {
    double max = Double.NEGATIVE_INFINITY;
    for (int i = 0; i < m.size(); i++) {
      if (not != i && m.get(i) > max) {
        max = m.get(i);
      }
    }
    return max;
  }
}
