package org.briljantframework.matrix.base;

import org.briljantframework.Check;
import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.AbstractComplexMatrix;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.Indexer;
import org.briljantframework.matrix.api.MatrixFactory;

/**
 * Created by Isak Karlsson on 07/01/15.
 */
class BaseComplexMatrix extends AbstractComplexMatrix {

  private Complex[] values;
  private Complex defaultValue = Complex.ZERO;

  BaseComplexMatrix(MatrixFactory bj, int rows, int cols) {
    this(bj, new Complex[Math.multiplyExact(rows, cols)], rows, cols);
  }

  BaseComplexMatrix(MatrixFactory bj, int size) {
    this(bj, new Complex[size], size);
  }

  BaseComplexMatrix(MatrixFactory bj, Complex[] values, int rows, int columns) {
    super(bj, rows, columns);
    Check.size(values.length, Math.multiplyExact(rows, columns));
    this.values = values;
  }

  BaseComplexMatrix(MatrixFactory bj, Complex[] values, int size) {
    super(bj, size);
    this.values = values;
  }

  BaseComplexMatrix(MatrixFactory bj, Complex[] values) {
    this(bj, values, values.length, 1);
  }

  @Override
  public ComplexMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new BaseComplexMatrix(getMatrixFactory(), values, rows, columns);
  }

  @Override
  public ComplexMatrix newEmptyMatrix(int rows, int columns) {
    return new BaseComplexMatrix(getMatrixFactory(), rows, columns);
  }

  @Override
  public ComplexMatrix copy() {
    return new BaseComplexMatrix(getMatrixFactory(), values.clone(), rows(), columns());
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public Complex get(int i, int j) {
    final Complex value = values[Indexer.columnMajor(i, j, rows(), columns())];
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  @Override
  public Complex get(int index) {
    final Complex value = values[index];
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  @Override
  public void set(int i, int j, Complex value) {
    set(Indexer.columnMajor(i, j, rows(), columns()), value);
  }

  @Override
  public void set(int index, Complex value) {
    values[index] = value;
  }
}
