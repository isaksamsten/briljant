package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;
import static org.briljantframework.matrix.Indexer.columnMajor;

import org.briljantframework.exception.NonConformantException;

/**
 * Created by Isak Karlsson on 08/12/14.
 */
public class MatrixView extends AbstractMatrix {
  private static final int ROW = 0;
  private static final int COLUMN = 1;

  private final Matrix parent;

  private final int rowOffset, colOffset;

  public MatrixView(Matrix parent, int rowOffset, int colOffset, int rows, int cols) {
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
  public Matrix mmul(double alpha, Matrix other, double beta) {
    if (this.columns() != other.rows()) {
      throw new NonConformantException(this, other);
    }
    if (isArrayBased() && other.isArrayBased()) {
      double[] tmp = new double[this.rows() * other.columns()];
      Matrices.mmul(this, alpha, other, beta, tmp);
      return new ArrayMatrix(other.columns(), tmp);
    } else {
      return super.mmul(alpha, other, beta);
    }
  }

  @Override
  public void put(int i, int j, double value) {
    parent.put(rowOffset + i, colOffset + j, value);
  }

  @Override
  public double get(int i, int j) {
    return parent.get(rowOffset + i, colOffset + j);
  }

  @Override
  public void put(int index, double value) {
    parent.put(computeLinearIndex(index), value);
  }

  @Override
  public double get(int index) {
    return parent.get(computeLinearIndex(index));
  }

  @Override
  public int size() {
    return rows() * columns();
  }

  /**
   * {@inheritDoc}
   * 
   * Note, not entirely true, but it appears that copying the array is faster than brute-force
   * implementing mmul if the underlying matrix {@code isArrayBased()}
   */
  @Override
  public boolean isArrayBased() {
    return parent.isArrayBased();
  }

  @Override
  public Matrix newEmptyMatrix(int rows, int columns) {
    return new ArrayMatrix(rows, columns);
  }

  @Override
  public Matrix copy() {
    Matrix mat = parent.newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      mat.put(i, get(i));
    }
    return mat;
  }

  private int computeLinearIndex(int index) {
    int currentColumn = index / rows() + colOffset;
    int currentRow = index % rows() + rowOffset;
    return columnMajor(currentRow, currentColumn, parent.rows(), parent.columns());
  }
}
