package org.briljantframework.matrix;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractMatrix implements Matrix {

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
  public Matrix slice(IntMatrix rows, IntMatrix columns) {
    Matrix matrix = newEmptyMatrix(rows.size(), columns.size());
    int j = 0;
    for (int column : columns) {
      int i = 0;
      for (int row : rows) {
        matrix.set(i++, j, this, row, column);
      }
      j += 1;
    }
    return matrix;
  }

  @Override
  public Matrix slice(IntMatrix indexes) {
    Matrix matrix = newEmptyVector(indexes.size());
    int i = 0;
    for (int index : indexes) {
      matrix.set(i++, this, index);
    }
    return matrix;
  }

  @Override
  public Matrix slice(Slice slice, Axis axis) {
    return slice((IntMatrix) slice, axis);
  }

  @Override
  public Matrix slice(IntMatrix indexes, Axis axis) {
    if (axis == Axis.ROW) {
      Matrix matrix = newEmptyMatrix(indexes.size(), columns());
      int i = 0;
      for (Number index : indexes) {
        for (int j = 0; j < columns(); j++) {
          matrix.set(i, j, this, index.intValue(), j);
        }
        i += 1;
      }
      return matrix;
    } else {
      Matrix matrix = newEmptyMatrix(rows(), indexes.size());
      int j = 0;
      for (Number index : indexes) {
        for (int i = 0; i < rows(); i++) {
          matrix.set(i, j, this, i, index.intValue());
        }
        j += 1;
      }
      return matrix;
    }
  }

  @Override
  public Matrix slice(BitMatrix bits) {
    IncrementalBuilder builder = newIncrementalBuilder();
    int r = 0, s = bits.size();
    for (int i = 0; i < size(); i++) {
      if (bits.get(r++)) {
        builder.add(this, i);
      }
      if (r >= s) {
        r = 0;
      }
    }

    return builder.build();
  }

  @Override
  public Matrix slice(Slice slice) {
    return slice((IntMatrix) slice);
  }

  @Override
  public Matrix slice(Slice rows, Slice columns) {
    return slice((IntMatrix) rows, columns);
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
  public int compare(int a, int b) {
    return compare(a, this, b);
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
  public BitMatrix lt(Matrix other) {
    Check.equalSize(this, other);
    BitMatrix bm = Matrices.newBitMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) < other.getAsDouble(i));
    }

    return bm;
  }

  @Override
  public BitMatrix lt(Number value) {
    BitMatrix bm = Matrices.newBitMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) < value.doubleValue());
    }
    return bm;
  }

  @Override
  public BitMatrix lte(Matrix other) {
    Check.equalSize(this, other);
    BitMatrix bm = Matrices.newBitMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) <= other.getAsDouble(i));
    }
    return bm;
  }

  @Override
  public BitMatrix lte(Number value) {
    BitMatrix bm = Matrices.newBitMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) <= value.doubleValue());
    }
    return bm;
  }

  @Override
  public BitMatrix gt(Matrix other) {
    Check.equalSize(this, other);
    BitMatrix bm = Matrices.newBitMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) > other.getAsDouble(i));
    }
    return bm;
  }

  @Override
  public BitMatrix gt(Number value) {
    BitMatrix bm = Matrices.newBitMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) > value.doubleValue());
    }
    return bm;
  }

  @Override
  public BitMatrix gte(Matrix other) {
    Check.equalSize(this, other);
    BitMatrix bm = Matrices.newBitMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) >= other.getAsDouble(i));
    }

    return bm;
  }

  @Override
  public BitMatrix gte(Number value) {
    BitMatrix bm = Matrices.newBitMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) >= value.doubleValue());
    }
    return bm;
  }

  @Override
  public BitMatrix eq(Matrix other) {
    Check.equalSize(this, other);
    BitMatrix bm = Matrices.newBitMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) == other.getAsDouble(i));
    }

    return bm;
  }

  @Override
  public BitMatrix eq(Number value) {
    BitMatrix bm = Matrices.newBitMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) == value.doubleValue());
    }
    return bm;
  }

}
