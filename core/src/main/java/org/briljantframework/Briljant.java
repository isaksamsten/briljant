package org.briljantframework;

import org.briljantframework.complex.Complex;
import org.briljantframework.distribution.Distribution;
import org.briljantframework.linalg.api.LinearAlgebraRoutines;
import org.briljantframework.matrix.BitMatrix;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.Dim;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.LongMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.Range;
import org.briljantframework.matrix.storage.Storage;
import org.briljantframework.matrix.Transpose;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.api.MatrixRoutines;

import java.util.Collection;
import java.util.List;

/**
 * @author Isak Karlsson
 */
public final class Briljant {

  private static MatrixFactory MATRIX_FACTORY;
  public static LinearAlgebraRoutines linalg;
  private static MatrixRoutines MATRIX_ROUTINES;

  static {
    String matrixFactoryClassName = "org.briljantframework.matrix.netlib.NetlibMatrixFactory";
    try {
      Class<?> matrixFactoryClass = Class.forName(matrixFactoryClassName);
      MATRIX_FACTORY = (MatrixFactory) matrixFactoryClass.newInstance();
      MATRIX_ROUTINES = MATRIX_FACTORY.getMatrixRoutines();
      linalg = MATRIX_FACTORY.getLinearAlgebraRoutines();
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    } catch (InstantiationException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
  }


  private Briljant() {
  }

  public static BitMatrix booleanVector(Storage storage) {
    return MATRIX_FACTORY.booleanVector(storage);
  }

  public static IntMatrix intVector(Storage storage) {
    return MATRIX_FACTORY.intVector(storage);
  }

  public static DoubleMatrix doubleVector(Storage storage) {
    return MATRIX_FACTORY.doubleVector(storage);
  }

  public static ComplexMatrix complexVector(Storage storage) {
    return MATRIX_FACTORY.complexVector(storage);
  }

  public static LongMatrix longVector(Storage storage) {
    return MATRIX_FACTORY.longVector(storage);
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

  public static void gemm(Transpose transA,
                          Transpose transB, double alpha,
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
    return MATRIX_ROUTINES.nrm2(a);
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

  public static void gemv(Transpose transA, double alpha, DoubleMatrix a, DoubleMatrix x,
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
}
