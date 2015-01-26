package org.briljantframework.vector;

import org.briljantframework.matrix.Check;
import org.briljantframework.exceptions.ImmutableModificationException;
import org.briljantframework.matrix.AbstractBitMatrix;
import org.briljantframework.matrix.ArrayBitMatrix;
import org.briljantframework.matrix.BitMatrix;
import org.briljantframework.matrix.Indexer;

/**
 * Created by Isak Karlsson on 13/01/15.
 */
class VectorBitMatrixAdapter extends AbstractBitMatrix {
  private final Vector vector;

  protected VectorBitMatrixAdapter(int rows, int cols, Vector vector) {
    super(rows, cols);
    Check.size(vector.size(), this);
    this.vector = vector;
  }

  public VectorBitMatrixAdapter(Vector vector) {
    this(vector.size(), 1, vector);
  }

  @Override
  public void set(int i, int j, boolean value) {
    throw new ImmutableModificationException();
  }

  @Override
  public void set(int index, boolean value) {
    throw new ImmutableModificationException();
  }

  @Override
  public boolean get(int i, int j) {
    return get(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public boolean get(int index) {
    return vector.getAsBit(index) == Bit.TRUE;
  }

  @Override
  public BitMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new VectorBitMatrixAdapter(rows, columns, vector);
  }

  @Override
  public boolean isView() {
    return true;
  }

  @Override
  public BitMatrix newEmptyMatrix(int rows, int columns) {
    return new ArrayBitMatrix(rows, columns);
  }
}
