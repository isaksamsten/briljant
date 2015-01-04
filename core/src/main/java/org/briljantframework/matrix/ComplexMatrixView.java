package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;
import static org.briljantframework.matrix.Indexer.columnMajor;

import org.briljantframework.complex.Complex;

/**
 * Created by Isak Karlsson on 08/12/14.
 */
public class ComplexMatrixView extends AbstractComplexMatrix {
  private static final int ROW = 0;
  private static final int COLUMN = 1;

  private final ComplexMatrix parent;

  private final int rowOffset, colOffset;

  public ComplexMatrixView(ComplexMatrix parent, int rowOffset, int colOffset, int rows, int cols) {
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
  public ComplexMatrixView reshape(int rows, int columns) {
    // TODO(isak): this might be strange..
    return new ComplexMatrixView(parent.reshape(rows, columns), rowOffset, colOffset, rows, columns);
  }

  // @Override
  // public ComplexMatrixView mmul(double alpha, Matrix other, double beta) {
  // if (this.columns() != other.rows()) {
  // throw new NonConformantException(this, other);
  // }
  // if (isArrayBased() && other.isArrayBased()) {
  // double[] tmp = new double[this.rows() * other.columns()];
  // Matrices.mmul(this, alpha, other, beta, tmp);
  // return new ArrayMatrix(other.columns(), tmp);
  // } else {
  // return super.mmul(alpha, other, beta);
  // }
  // }

  @Override
  public void put(int i, int j, Complex value) {
    parent.put(rowOffset + i, colOffset + j, value);
  }

  @Override
  public Complex get(int i, int j) {
    return parent.get(rowOffset + i, colOffset + j);
  }

  @Override
  public void put(int index, Complex value) {
    parent.put(computeLinearIndex(index), value);
  }

  @Override
  public Complex get(int index) {
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
  public ComplexMatrix newEmptyMatrix(int rows, int columns) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ComplexMatrix copy() {
    ComplexMatrix mat = parent.newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      mat.put(i, get(i));
    }
    return mat;
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
