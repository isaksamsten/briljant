package org.briljantframework.matrix.base;

import org.briljantframework.Check;
import org.briljantframework.matrix.AbstractLongMatrix;
import org.briljantframework.matrix.Indexer;
import org.briljantframework.matrix.LongMatrix;
import org.briljantframework.matrix.api.MatrixFactory;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Isak Karlsson
 */
class BaseLongMatrix extends AbstractLongMatrix {

  private long[] values;

  BaseLongMatrix(MatrixFactory bj, int rows, int cols) {
    super(bj, rows, cols);
    this.values = new long[Math.multiplyExact(rows, cols)];
  }

  BaseLongMatrix(MatrixFactory bj, int size) {
    this(bj, new long[size], size);
  }

  BaseLongMatrix(MatrixFactory bj, long[] values, int size) {
    super(bj, size);
    this.values = checkNotNull(values);
  }

  BaseLongMatrix(MatrixFactory bj, long[] values, int rows, int cols) {
    super(bj, rows, cols);
    this.values = checkNotNull(values);
  }

  public BaseLongMatrix(MatrixFactory bj, long[] values) {
    super(bj, checkNotNull(values).length);
    this.values = values;
  }

  @Override
  public LongMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new BaseLongMatrix(getMatrixFactory(), values, rows, columns);
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public LongMatrix newEmptyMatrix(int rows, int columns) {
    return new BaseLongMatrix(getMatrixFactory(), rows, columns);
  }

  @Override
  public long get(int i, int j) {
    return values[Indexer.columnMajor(i, j, rows(), columns())];
  }

  @Override
  public long get(int index) {
    return values[index];
  }

  @Override
  public void set(int index, long value) {
    values[index] = value;
  }

  @Override
  public void set(int i, int j, long value) {
    values[Indexer.columnMajor(i, j, rows(), columns())] = value;
  }
}