package org.briljantframework.matrix.base;

import org.briljantframework.complex.Complex;
import org.briljantframework.linalg.api.LinearAlgebraRoutines;
import org.briljantframework.matrix.BitMatrix;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.LongMatrix;
import org.briljantframework.matrix.Range;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.api.MatrixRoutines;
import org.briljantframework.matrix.storage.DoubleStorage;
import org.briljantframework.matrix.storage.LongStorage;
import org.briljantframework.matrix.storage.Storage;

/**
 * @author Isak Karlsson
 */
public class BaseMatrixFactory implements MatrixFactory {

  private final MatrixRoutines matrixRoutines = new BaseMatrixRoutines(this);

  @Override
  public IntMatrix matrix(int[][] data) {
    int rows = data.length;
    int columns = data[0].length;
    IntMatrix x = intMatrix(rows, columns);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < columns; j++) {
        x.set(i, j, data[i][j]);
      }
    }
    return x;
  }

  @Override
  public IntMatrix matrix(int[] data) {
    return new BaseIntMatrix(this, data);
  }

  @Override
  public LongMatrix matrix(long[][] data) {
    return null;
  }

  @Override
  public LongMatrix matrix(long[] data) {
    return new BaseLongMatrix(this, new LongStorage(data));
  }

  @Override
  public DoubleMatrix matrix(double[][] data) {
    int rows = data.length;
    int cols = data[0].length;
    DoubleMatrix x = doubleMatrix(rows, cols);
    for (int i = 0; i < rows; i++) {
      for (int j = 0; j < cols; j++) {
        x.set(i, j, data[i][j]);
      }
    }
    return x;
  }

  @Override
  public DoubleMatrix matrix(double[] data) {
    return new BaseDoubleMatrix(this, new DoubleStorage(data));
  }

  @Override
  public DoubleMatrix diag(int n) {
    return new BaseDiagonal(this, new double[n], n, n);
  }

  @Override
  public DoubleMatrix diag(int m, int n) {
    return new BaseDiagonal(this, new double[Math.min(m, n)], m, n);
  }

  @Override
  public DoubleMatrix diag(double[] data) {
    return new BaseDiagonal(this, new DoubleStorage(data), data.length, 1);
  }

  @Override
  public ComplexMatrix matrix(Complex[][] data) {
    return null;
  }

  @Override
  public ComplexMatrix matrix(Complex[] data) {
    return new BaseComplexMatrix(this, data);
  }

  @Override
  public ComplexMatrix complexMatrix(double... data) {
    Complex[] c = new Complex[data.length];
    for (int i = 0; i < data.length; i++) {
      c[i] = Complex.valueOf(data[i]);
    }
    return matrix(c);
  }

  @Override
  public BitMatrix matrix(boolean[][] data) {
    return null;
  }

  @Override
  public BitMatrix matrix(boolean[] data) {
    return new BaseBitMatrix(this, data);
  }

  @Override
  public IntMatrix intMatrix(int rows, int columns) {
    return new BaseIntMatrix(this, rows, columns);
  }

  @Override
  public IntMatrix intVector(int size) {
    return new BaseIntMatrix(this, size);
  }

  @Override
  public IntMatrix intVector(Storage storage) {
    return new BaseIntMatrix(this, storage);
  }

  @Override
  public LongMatrix longMatrix(int rows, int columns) {
    return new BaseLongMatrix(this, rows, columns);
  }

  @Override
  public LongMatrix longVector(int size) {
    return new BaseLongMatrix(this, size);
  }

  @Override
  public LongMatrix longVector(Storage storage) {
    return new BaseLongMatrix(this, storage);
  }

  @Override
  public DoubleMatrix doubleMatrix(int rows, int columns) {
    return new BaseDoubleMatrix(this, rows, columns);
  }

  @Override
  public DoubleMatrix doubleVector(int size) {
    return new BaseDoubleMatrix(this, size);
  }

  @Override
  public DoubleMatrix doubleVector(Storage storage) {
    return new BaseDoubleMatrix(this, storage);
  }

  @Override
  public ComplexMatrix complexMatrix(int rows, int columns) {
    return new BaseComplexMatrix(this, rows, columns);
  }

  @Override
  public ComplexMatrix complexVector(int size) {
    return new BaseComplexMatrix(this, size);
  }

  @Override
  public ComplexMatrix complexVector(Storage storage) {
    return new BaseComplexMatrix(this, storage);
  }

  @Override
  public BitMatrix booleanMatrix(int rows, int columns) {
    return new BaseBitMatrix(this, rows, columns);
  }

  @Override
  public BitMatrix booleanVector(int size) {
    return new BaseBitMatrix(this, size);
  }

  @Override
  public BitMatrix booleanVector(Storage storage) {
    return new BaseBitMatrix(this, storage);
  }

  @Override
  public Range range(int start, int end, int step) {
    return new BaseRange(this, start, end, step);
  }

  @Override
  public Range range(int start, int end) {
    return range(start, end, 1);
  }

  @Override
  public Range range(int end) {
    return range(0, end);
  }

  @Override
  public DoubleMatrix linspace(double start, double end, int size) {
    DoubleMatrix values = doubleVector(size);
    double step = (end - start) / (size - 1);
    double value = start;
    for (int index = 0; index < size; index++) {
      values.set(index, value);
      value += step;
    }
    return values;
  }

  @Override
  public DoubleMatrix eye(int size) {
    DoubleMatrix eye = doubleMatrix(size, size);
    eye.getDiagonalView().assign(1);
    return eye;
  }

  @Override
  public MatrixRoutines getMatrixRoutines() {
    return matrixRoutines;
  }

  @Override
  public LinearAlgebraRoutines getLinearAlgebraRoutines() {
    throw new UnsupportedOperationException();
  }


}
