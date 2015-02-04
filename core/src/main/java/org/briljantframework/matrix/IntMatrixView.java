package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;
import static org.briljantframework.matrix.Indexer.columnMajor;

import org.briljantframework.matrix.storage.Storage;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public class IntMatrixView extends AbstractIntMatrix {

  private final int rowOffset, colOffset;
  private final IntMatrix parent;

  public IntMatrixView(IntMatrix parent, int rowOffset, int colOffset, int rows, int cols) {
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
  public IntMatrix reshape(int rows, int columns) {
    return new IntMatrixView(parent, rowOffset, colOffset, rows, columns);
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
  public IntMatrix newEmptyMatrix(int rows, int columns) {
    return null;
  }

  @Override
  public int get(int i, int j) {
    return parent.get(rowOffset + i, colOffset + j);
  }

  @Override
  public int get(int index) {
    return parent.get(computeLinearIndex(index));
  }

  @Override
  public IntMatrix copy() {
    IntMatrix mat = parent.newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      mat.set(i, get(i));
    }
    return mat;
  }

  @Override
  public void set(int i, int j, int value) {
    parent.set(rowOffset + i, colOffset + j, value);
  }

  @Override
  public void set(int index, int value) {
    parent.set(computeLinearIndex(index), value);
  }

  private int computeLinearIndex(int index) {
    int currentColumn = index / rows() + colOffset;
    int currentRow = index % rows() + rowOffset;
    return columnMajor(currentRow, currentColumn, parent.rows(), parent.columns());
  }
}
