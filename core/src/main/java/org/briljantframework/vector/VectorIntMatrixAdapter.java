package org.briljantframework.vector;

import org.briljantframework.matrix.Check;
import org.briljantframework.exceptions.ImmutableModificationException;
import org.briljantframework.matrix.AbstractIntMatrix;
import org.briljantframework.matrix.ArrayIntMatrix;
import org.briljantframework.matrix.Indexer;
import org.briljantframework.matrix.IntMatrix;

/**
 * Created by Isak Karlsson on 13/01/15.
 */
class VectorIntMatrixAdapter extends AbstractIntMatrix {
  private final Vector vector;

  public VectorIntMatrixAdapter(int rows, int cols, Vector vector) {
    super(rows, cols);
    Check.size(vector.size(), this);
    this.vector = vector;
  }

  public VectorIntMatrixAdapter(Vector vector) {
    this(vector.size(), 1, vector);
  }

  @Override
  public IntMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new VectorIntMatrixAdapter(rows, columns, vector);
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public IntMatrix newEmptyMatrix(int rows, int columns) {
    return new ArrayIntMatrix(rows, columns);
  }

  @Override
  public int get(int i, int j) {
    return get(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public int get(int index) {
    return vector.getAsInt(index);
  }

  @Override
  public boolean isArrayBased() {
    return true;
  }

  @Override
  public int[] asIntArray() {
    return vector.asIntArray();
  }

  @Override
  public void set(int i, int j, int value) {
    throw new ImmutableModificationException();
  }

  @Override
  public void set(int index, int value) {
    throw new ImmutableModificationException();
  }
}
