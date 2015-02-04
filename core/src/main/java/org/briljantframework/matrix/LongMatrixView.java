package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;

import org.briljantframework.matrix.storage.Storage;

public class LongMatrixView extends AbstractLongMatrix {

  private final int rowOffset, colOffset;
  private final LongMatrix parent;

  public LongMatrixView(LongMatrix parent, int rowOffset, int colOffset, int rows, int cols) {
    super(rows, cols);
    this.rowOffset = rowOffset;
    this.colOffset = colOffset;
    this.parent = parent;

    checkArgument(rowOffset >= 0 && rowOffset + rows() <= parent.rows(),
        "Requested row out of bounds.");
    checkArgument(colOffset >= 0 && colOffset + columns() <= parent.rows(),
        "Requested column out of bounds");
  }

  @Override
  public LongMatrix reshape(int rows, int columns) {
    return new LongMatrixView(parent, rowOffset, colOffset, rows, columns);
  }

  @Override
  public boolean isView() {
    return true;
  }

  @Override
  public Storage getStorage() {
    return parent.getStorage();
  }

  @Override
  public LongMatrix newEmptyMatrix(int rows, int columns) {
    return null;
  }

  @Override
  public long get(int i, int j) {
    return parent.get(rowOffset + i, colOffset + j);
  }

  @Override
  public long get(int index) {
    return parent.get(computeLinearIndex(index));
  }

  @Override
  public void set(int i, int j, long value) {
    parent.set(rowOffset + i, colOffset + j, value);
  }

  @Override
  public void set(int index, long value) {
    parent.set(computeLinearIndex(index), value);
  }

  private int computeLinearIndex(int index) {
    int currentColumn = index / rows() + colOffset;
    int currentRow = index % rows() + rowOffset;
    return Indexer.columnMajor(currentRow, currentColumn, parent.rows(), parent.columns());
  }
}
