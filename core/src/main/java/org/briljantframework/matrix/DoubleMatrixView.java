package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;

import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.matrix.storage.DoubleStorage;
import org.briljantframework.matrix.storage.Storage;

/**
 * Created by Isak Karlsson on 08/12/14.
 */
public class DoubleMatrixView extends AbstractDoubleMatrix {
  private final DoubleMatrix parent;

  private final int rowOffset, colOffset;

  public DoubleMatrixView(DoubleMatrix parent, int rowOffset, int colOffset, int rows, int cols) {
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
  public DoubleMatrix reshape(int rows, int columns) {
    throw new UnsupportedOperationException("Unable to reshape view.");
    // return copy().reshape(rows, columns);
    // // TODO(isak): this might be strange..
    // return new DoubleMatrixView(parent.reshape(rows, columns), rowOffset, colOffset, rows,
    // columns);
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public DoubleMatrix newEmptyMatrix(int rows, int columns) {
    return new DefaultDoubleMatrix(rows, columns);
  }

  @Override
  public double get(int i, int j) {
    return parent.get(rowOffset + i, colOffset + j);
  }

  @Override
  public double get(int index) {
    return parent.get(Indexer.computeLinearIndex(index, rows(), colOffset, rowOffset,
        parent.rows(), parent.columns()));
  }

  @Override
  public boolean isArrayBased() {
    return parent.isArrayBased();
  }

  @Override
  public DoubleMatrix copy() {
    DoubleMatrix mat = parent.newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      mat.set(i, get(i));
    }
    return mat;
  }

  @Override
  public Storage getStorage() {
    return parent.getStorage();
  }

  @Override
  public DoubleMatrix mmul(double alpha, DoubleMatrix other, double beta) {
    if (this.columns() != other.rows()) {
      throw new NonConformantException(this, other);
    }
    if (isArrayBased() && other.isArrayBased()) {
      double[] tmp = new double[(int) this.rows() * (int) other.columns()];
      Doubles.mmul(this, alpha, other, beta, tmp);
      return new DefaultDoubleMatrix(new DoubleStorage(tmp), this.rows(), other.columns());
    } else {
      return super.mmul(alpha, other, beta);
    }
  }

  @Override
  public void set(int i, int j, double value) {
    parent.set(rowOffset + i, colOffset + j, value);
  }

  @Override
  public void set(int index, double value) {
    parent.set(
        Indexer.computeLinearIndex(index, rows(), colOffset, rowOffset, parent.rows(),
            parent.columns()), value);
  }
}
