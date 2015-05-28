package org.briljantframework.matrix.base;

import org.briljantframework.Check;
import org.briljantframework.matrix.AbstractIntMatrix;
import org.briljantframework.matrix.Indexer;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.api.MatrixFactory;

/**
 * @author Isak Karlsson
 */
class BaseIntMatrix extends AbstractIntMatrix {

  private final int[] data;

  BaseIntMatrix(MatrixFactory bj, int size) {
    this(bj, new int[size], size);
  }

  BaseIntMatrix(MatrixFactory bj, int rows, int columns) {
    this(bj, new int[Math.multiplyExact(rows, columns)], rows, columns);
  }

  BaseIntMatrix(MatrixFactory bj, int[] data, int size) {
    super(bj, size);
    this.data = data;
  }

  BaseIntMatrix(MatrixFactory bj, int[] data, int rows, int cols) {
    super(bj, rows, cols);
    this.data = data;
  }

  BaseIntMatrix(MatrixFactory bj, int[] values) {
    this(bj, values, values.length);
  }

  @Override
  public IntMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new BaseIntMatrix(getMatrixFactory(), data, rows, columns);
  }

  @Override
  public IntMatrix newEmptyMatrix(int rows, int columns) {
    return new BaseIntMatrix(getMatrixFactory(), rows, columns);
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public int get(int i, int j) {
    return data[Indexer.columnMajor(i, j, rows(), columns())];
  }

  @Override
  public int get(int index) {
    return data[index];
  }

  @Override
  public void set(int index, int value) {
    data[index] = value;
  }

  @Override
  public void set(int i, int j, int value) {
    data[Indexer.columnMajor(i, j, rows(), columns())] = value;
  }

  @Override
  public IntMatrix copy() {
    return new BaseIntMatrix(getMatrixFactory(), data.clone(), rows(), columns());
  }

  @Override
  public int[] data() {
    return data;
  }
}
