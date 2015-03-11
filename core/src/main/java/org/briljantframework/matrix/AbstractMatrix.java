package org.briljantframework.matrix;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractMatrix<T extends Matrix> implements Matrix<T> {

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
  public DoubleMatrix asDoubleMatrix() {
    return this instanceof DoubleMatrix ? (DoubleMatrix) this : new DefaultDoubleMatrix(this);
  }

  @Override
  public IntMatrix asIntMatrix() {
    return this instanceof IntMatrix ? (IntMatrix) this : new DefaultIntMatrix(this);
  }

  @Override
  public LongMatrix asLongMatrix() {
    return this instanceof LongMatrix ? (LongMatrix) this : new DefaultLongMatrix(this);
  }

  @Override
  public BitMatrix asBitMatrix() {
    return this instanceof BitMatrix ? (BitMatrix) this : new DefaultBitMatrix(this);
  }

  @Override
  public ComplexMatrix asComplexMatrix() {
    return this instanceof ComplexMatrix ? (ComplexMatrix) this : new DefaultComplexMatrix(this);
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

  @Override
  public T newEmptyVector(int size) {
    return newEmptyMatrix(size, 1);
  }
}
