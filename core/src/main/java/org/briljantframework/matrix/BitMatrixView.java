package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Isak Karlsson on 13/01/15.
 */
public class BitMatrixView extends AbstractBitMatrix {
  private final BitMatrix parent;

  private final int rowOffset, colOffset;

  public BitMatrixView(BitMatrix parent, int rowOffset, int colOffset, int rows, int cols) {
    super(rows, cols);
    this.rowOffset = rowOffset;
    this.colOffset = colOffset;
    this.parent = parent;

    checkArgument(rowOffset >= 0 && rowOffset + rows() <= parent.rows(),
        "Requested row out of bounds.");
    checkArgument(colOffset >= 0 && colOffset + columns() <= parent.columns(),
        "Requested column out of bounds");
  }

  @Override
  public void set(int i, int j, boolean value) {
    parent.set(rowOffset + i, colOffset + j, value);
  }

  @Override
  public void set(int index, boolean value) {
    parent.set(
        Indexer.computeLinearIndex(index, rows(), colOffset, rowOffset, parent.rows(),
            parent.columns()), value);
  }

  @Override
  public boolean get(int i, int j) {
    return parent.get(rowOffset + i, colOffset + j);
  }

  @Override
  public boolean get(int index) {
    return parent.get(Indexer.computeLinearIndex(index, rows(), colOffset, rowOffset,
        parent.rows(), parent.columns()));
  }

  @Override
  public BitMatrix reshape(int rows, int columns) {
    throw new UnsupportedOperationException("Unable to reshape view.");
    // return copy().reshape(rows, columns);
    // // TODO(isak): this might be strange..
    // return new DoubleMatrixView(parent.reshape(rows, columns), rowOffset, colOffset, rows,
    // columns);
  }

  @Override
  public boolean isView() {
    return true;
  }

  @Override
  public BitMatrix newEmptyMatrix(int rows, int columns) {
    return new ArrayBitMatrix(rows, columns);
  }

  @Override
  public BitMatrix copy() {
    BitMatrix mat = parent.newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      mat.set(i, get(i));
    }
    return mat;
  }
}
