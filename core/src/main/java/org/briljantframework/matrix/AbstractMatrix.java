package org.briljantframework.matrix;

import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractMatrix<T extends Matrix<T>> implements Matrix<T> {

  protected static final String CHANGED_TOTAL_SIZE =
      "Total size of new matrix must be unchanged. (%d, %d)";

  protected static final String ARG_DIFF_SIZE = "Arguments imply different size.";

  private final int rows, cols, size;

  protected AbstractMatrix(int size) {
    this(size, 1);
  }

  protected AbstractMatrix(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    this.size = Math.multiplyExact(rows, cols);
  }

  @Override
  public T map(Axis axis, UnaryOperator<T> mapper) {
    T matrix = newEmptyMatrix(rows(), columns());
    if (axis == Axis.ROW) {
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
  public void forEach(Axis axis, Consumer<T> consumer) {
    if (axis == Axis.ROW) {
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
  public final int rows() {
    return rows;
  }

  @Override
  public final int columns() {
    return cols;
  }

  @Override
  public int size() {
    return size;
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
