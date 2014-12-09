package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;
import static org.briljantframework.matrix.Indexer.columnMajor;

import java.util.function.Consumer;
import java.util.function.Function;

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
  public Matrix copy() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Matrix mmul(double alpha, Matrix other, double beta) {
    if (this.columns() != other.rows()) {
      throw new NonConformantException(this, other);
    }


    throw new UnsupportedOperationException();
  }

  @Override
  public Matrix unsafeTransform(Function<double[], Matrix> op) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void unsafe(Consumer<double[]> consumer) {
    throw new UnsupportedOperationException();
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
  public Matrix newEmptyMatrix(int rows, int columns) {
    return new ArrayMatrix(rows, columns);
  }

  @Override
  public double get(int i, int j) {
    return parent.get(rowOffset + i, colOffset + j);
  }

  @Override
  public double get(int index) {
    return parent.get(computeLinearIndex(index));
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
