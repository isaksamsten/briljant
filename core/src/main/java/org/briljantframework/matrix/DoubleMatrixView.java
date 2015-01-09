package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;
import static org.briljantframework.matrix.Indexer.columnMajor;

import org.briljantframework.exceptions.NonConformantException;

/**
 * Created by Isak Karlsson on 08/12/14.
 */
public class DoubleMatrixView extends AbstractDoubleMatrix {
  private static final int ROW = 0;
  private static final int COLUMN = 1;

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
    // TODO(isak): this might be strange..
    return new DoubleMatrixView(parent.reshape(rows, columns), rowOffset, colOffset, rows, columns);
  }

  @Override
  public double get(int i, int j) {
    return parent.get(rowOffset + i, colOffset + j);
  }

  @Override
  public double get(int index) {
    return parent.get(computeLinearIndex(index));
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
  public DoubleMatrix newEmptyMatrix(int rows, int columns) {
    return new ArrayDoubleMatrix(rows, columns);
  }

  @Override
  public DoubleMatrix copy() {
    DoubleMatrix mat = parent.newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      mat.put(i, get(i));
    }
    return mat;
  }

  @Override
  public DoubleMatrix mmul(double alpha, DoubleMatrix other, double beta) {
    if (this.columns() != other.rows()) {
      throw new NonConformantException(this, other);
    }
    if (isArrayBased() && other.isArrayBased()) {
      double[] tmp = new double[this.rows() * other.columns()];
      Matrices.mmul(this, alpha, other, beta, tmp);
      return new ArrayDoubleMatrix(other.columns(), tmp);
    } else {
      return super.mmul(alpha, other, beta);
    }
  }

  @Override
  public void put(int i, int j, double value) {
    parent.put(rowOffset + i, colOffset + j, value);
  }

  @Override
  public void put(int index, double value) {
    parent.put(computeLinearIndex(index), value);
  }

  @Override
  public int size() {
    return rows() * columns();
  }

  private int computeLinearIndex(int index) {
    int currentColumn = index / rows() + colOffset;
    int currentRow = index % rows() + rowOffset;
    return columnMajor(currentRow, currentColumn, parent.rows(), parent.columns());
  }
}
