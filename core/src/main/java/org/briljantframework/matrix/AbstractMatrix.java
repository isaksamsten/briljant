package org.briljantframework.matrix;

import com.google.common.base.Preconditions;

import org.briljantframework.Check;
import org.briljantframework.matrix.api.MatrixFactory;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractMatrix<T extends Matrix<T>> implements Matrix<T> {

  protected static final String CHANGED_TOTAL_SIZE =
      "Total size of new matrix must be unchanged. (%d, %d)";

  protected static final String ARG_DIFF_SIZE = "Arguments imply different size.";

  protected final MatrixFactory bj;
  private final int rows, cols, size;

  protected AbstractMatrix(MatrixFactory bj, int size) {
    this(bj, size, 1);
  }

  protected AbstractMatrix(MatrixFactory bj, int rows, int cols) {
    this.bj = Preconditions.checkNotNull(bj);
    this.rows = rows;
    this.cols = cols;
    this.size = Math.multiplyExact(rows, cols);
  }

  protected MatrixFactory getMatrixFactory() {
    return bj;
  }

  @Override
  public T map(Dim dim, UnaryOperator<T> mapper) {
    T matrix = newEmptyMatrix(rows(), columns());
    if (dim == Dim.R) {
      for (int i = 0; i < rows(); i++) {
        matrix.setRow(i, mapper.apply(getRowView(i)));
      }
    } else {
      for (int i = 0; i < columns(); i++) {
        matrix.setColumn(i, mapper.apply(getColumnView(i)));
      }
    }

    return matrix;
  }

  @Override
  public void forEach(Dim dim, Consumer<T> consumer) {
    if (dim == Dim.R) {
      for (int i = 0; i < rows(); i++) {
        consumer.accept(getRowView(i));
      }
    } else {
      for (int i = 0; i < columns(); i++) {
        consumer.accept(getColumnView(i));
      }
    }
  }

  @Override
  public void setVectorAlong(Dim dim, int i, T vector) {
    if (dim == Dim.R) {
      setRow(i, vector);
    } else {
      setColumn(i, vector);
    }
  }

  @Override
  public T getVectorAlong(Dim dim, int index) {
    return dim == Dim.R ? getRowView(index) : getColumnView(index);
  }

  @Override
  public final int rows() {
    return rows;
  }

  @Override
  public final int columns() {
    return cols;
  }

  @Override
  public int size(Dim dim) {
    return dim == Dim.R ? rows() : columns();
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isVector() {
    return rows() == 1 || columns() == 1;
  }

  @Override
  public T newEmptyVector(int size) {
    return newEmptyMatrix(size, 1);
  }

  @Override
  public BitMatrix lt(T other) {
    return null;
  }

  @Override
  public BitMatrix gt(T other) {
    return null;
  }

  @Override
  public BitMatrix eq(T other) {
    return null;
  }

  @Override
  public BitMatrix lte(T other) {
    return null;
  }

  @Override
  public BitMatrix gte(T other) {
    return null;
  }
}
