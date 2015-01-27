 package org.briljantframework.matrix;

import java.util.Collection;

import org.briljantframework.Range;
import org.briljantframework.complex.Complex;

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
  public Matrix slice(Collection<Integer> rows, Collection<Integer> columns) {
    Matrix matrix = newEmptyMatrix(rows.size(), columns.size());
    int j = 0;
    for (Number column : columns) {
      int i = 0;
      for (Number row : rows) {
        matrix.set(i++, j, this, row.intValue(), column.intValue());
      }
      j += 1;
    }
    return matrix;
  }

  @Override
  public Matrix slice(Collection<Integer> indexes) {
    Matrix matrix = newEmptyVector(indexes.size());
    int i = 0;
    for (Number index : indexes) {
      matrix.set(i++, this, index.intValue());
    }
    return matrix;
  }

  @Override
  public Matrix slice(Range range, Axis axis) {
    return slice((Collection<Integer>) range, axis);
  }

  @Override
  public Matrix slice(Collection<Integer> indexes, Axis axis) {
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
  public Matrix slice(Range range) {
    return slice((Collection<Integer>) range);
  }

  @Override
  public Matrix slice(Range rows, Range columns) {
    return slice((Collection<Integer>) rows, columns);
  }

  @Override
  public DoubleMatrix asDoubleMatrix() {
    return this instanceof DoubleMatrix ? (DoubleMatrix) this : new DoubleMatrixAdapter(this);
  }

  @Override
  public IntMatrix asIntMatrix() {
    return this instanceof IntMatrix ? (IntMatrix) this : new IntMatrixAdapter(this);
  }

  @Override
  public BitMatrix asBitMatrix() {
    return this instanceof BitMatrix ? (BitMatrix) this : new BitMatrixAdapter(this);
  }

  @Override
  public ComplexMatrix asComplexMatrix() {
    return this instanceof ComplexMatrix ? (ComplexMatrix) this : new ComplexMatrixAdapter(this);
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
  public BitMatrix lessThan(Matrix other) {
    Check.equalSize(this, other);
    BitMatrix bm = Bits.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) < other.getAsDouble(i));
    }

    return bm;
  }

  @Override
  public BitMatrix lessThan(Number value) {
    BitMatrix bm = Bits.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) < value.doubleValue());
    }
    return bm;
  }

  @Override
  public BitMatrix lessThanEqual(Matrix other) {
    Check.equalSize(this, other);

    BitMatrix bm = Bits.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) <= other.getAsDouble(i));
    }
    return bm;
  }

  @Override
  public BitMatrix lessThanEqual(Number value) {
    BitMatrix bm = Bits.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) <= value.doubleValue());
    }
    return bm;
  }

  @Override
  public BitMatrix greaterThan(Matrix other) {
    Check.equalSize(this, other);
    BitMatrix bm = Bits.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) > other.getAsDouble(i));
    }
    return bm;
  }

  @Override
  public BitMatrix greaterThan(Number value) {
    BitMatrix bm = Bits.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) > value.doubleValue());
    }
    return bm;
  }

  @Override
  public BitMatrix greaterThanEquals(Matrix other) {
    Check.equalSize(this, other);
    BitMatrix bm = Bits.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) >= other.getAsDouble(i));
    }

    return bm;
  }

  @Override
  public BitMatrix greaterThanEquals(Number value) {
    BitMatrix bm = Bits.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) >= value.doubleValue());
    }
    return bm;
  }

  @Override
  public BitMatrix equalsTo(Matrix other) {
    Check.equalSize(this, other);
    BitMatrix bm = Bits.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) == other.getAsDouble(i));
    }

    return bm;
  }

  @Override
  public BitMatrix equalsTo(Number value) {
    BitMatrix bm = Bits.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) == value.doubleValue());
    }
    return bm;
  }

  /**
   * Adapts {@link Matrix} as a {@link org.briljantframework.matrix.DoubleMatrix}.
   */
  static class DoubleMatrixAdapter extends AbstractDoubleMatrix {

    private final Matrix parent;

    public DoubleMatrixAdapter(Matrix parent) {
      super(parent.rows(), parent.columns());
      this.parent = parent;
    }

    @Override
    public DoubleMatrix reshape(int rows, int columns) {
      return parent instanceof DoubleMatrix ? (DoubleMatrix) parent.reshape(rows, columns)
          : new DoubleMatrixAdapter(parent.reshape(rows, columns));
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public DoubleMatrix newEmptyMatrix(int rows, int columns) {
      return new ArrayDoubleMatrix(rows, columns);
    }

    @Override
    public double get(int i, int j) {
      return parent.getAsDouble(i, j);
    }

    @Override
    public double get(int index) {
      return parent.getAsDouble(index);
    }

    @Override
    public boolean isArrayBased() {
      if (parent instanceof DoubleMatrix) {
        return ((DoubleMatrix) parent).isArrayBased();
      } else if (parent instanceof IntMatrix) {
        return ((IntMatrix) parent).isArrayBased();
      } else if (parent instanceof ComplexMatrix) {
        return ((ComplexMatrix) parent).isArrayBased();
      } else {
        return false;
      }
    }

    @Override
    public double[] asDoubleArray() {
      if (isArrayBased() && parent instanceof DoubleMatrix) {
        return ((DoubleMatrix) parent).asDoubleArray();
      } else {
        return super.asDoubleArray();
      }
    }

    @Override
    public DoubleMatrix asDoubleMatrix() {
      return parent instanceof DoubleMatrix ? (DoubleMatrix) parent : this;
    }

    @Override
    public void set(int i, int j, double value) {
      parent.set(i, j, value);
    }

    @Override
    public void set(int index, double value) {
      parent.set(index, value);
    }

    @Override
    public BitMatrix asBitMatrix() {
      return parent.asBitMatrix();
    }

    @Override
    public IntMatrix asIntMatrix() {
      return parent.asIntMatrix();
    }

    @Override
    public ComplexMatrix asComplexMatrix() {
      return parent.asComplexMatrix();
    }

  }

  /**
   * Adapts an {@link Matrix} as a {@link org.briljantframework.matrix.ComplexMatrix}
   */
  static class ComplexMatrixAdapter extends AbstractComplexMatrix {

    private final Matrix parent;

    ComplexMatrixAdapter(Matrix parent) {
      super(parent.rows(), parent.columns());
      this.parent = parent;
    }

    @Override
    public ComplexMatrix reshape(int rows, int columns) {
      return parent instanceof ComplexMatrix ? (ComplexMatrix) parent.reshape(rows, columns)
          : new ComplexMatrixAdapter(parent.reshape(rows, columns));
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public ComplexMatrix newEmptyMatrix(int rows, int columns) {
      return new ArrayComplexMatrix(rows, columns);
    }

    @Override
    public Complex get(int i, int j) {
      return parent.getAsComplex(i, j);
    }

    @Override
    public Complex get(int index) {
      return parent.getAsComplex(index);
    }

    @Override
    public boolean isArrayBased() {
      if (parent instanceof DoubleMatrix) {
        return ((DoubleMatrix) parent).isArrayBased();
      } else if (parent instanceof IntMatrix) {
        return ((IntMatrix) parent).isArrayBased();
      } else if (parent instanceof ComplexMatrix) {
        return ((ComplexMatrix) parent).isArrayBased();
      } else {
        return false;
      }
    }

    @Override
    public void set(int i, int j, Complex value) {
      parent.set(i, j, value);
    }

    @Override
    public void set(int index, Complex value) {
      parent.set(index, value);
    }

    @Override
    public DoubleMatrix asDoubleMatrix() {
      return parent.asDoubleMatrix();
    }

    @Override
    public BitMatrix asBitMatrix() {
      return parent.asBitMatrix();
    }



    @Override
    public IntMatrix asIntMatrix() {
      return parent.asIntMatrix();
    }

    @Override
    public ComplexMatrix asComplexMatrix() {
      return parent instanceof ComplexMatrix ? (ComplexMatrix) parent : this;
    }
  }

  /**
   * Adapts an {@link Matrix} as an {@link org.briljantframework.matrix.IntMatrix}.
   */
  static class IntMatrixAdapter extends AbstractIntMatrix {
    private final Matrix parent;

    protected IntMatrixAdapter(Matrix parent) {
      super(parent.rows(), parent.columns());
      this.parent = parent;

    }

    @Override
    public IntMatrix reshape(int rows, int columns) {
      return parent instanceof IntMatrix ? (IntMatrix) parent.reshape(rows, columns)
          : new IntMatrixAdapter(parent.reshape(rows, columns));
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public IntMatrix newEmptyMatrix(int rows, int columns) {
      return new ArrayIntMatrix(rows, columns);
    }

    @Override
    public int get(int i, int j) {
      return parent.getAsInt(i, j);
    }

    @Override
    public int get(int index) {
      return parent.getAsInt(index);
    }

    @Override
    public boolean isArrayBased() {
      return parent instanceof IntMatrix && ((IntMatrix) parent).isArrayBased();
    }

    @Override
    public int[] asIntArray() {
      return parent instanceof IntMatrix ? ((IntMatrix) parent).asIntArray() : super.asIntArray();
    }

    @Override
    public void set(int i, int j, int value) {
      parent.set(i, j, value);
    }

    @Override
    public void set(int index, int value) {
      parent.set(index, value);
    }

    @Override
    public BitMatrix asBitMatrix() {
      return parent.asBitMatrix();
    }

    @Override
    public DoubleMatrix asDoubleMatrix() {
      return parent.asDoubleMatrix();
    }

    @Override
    public IntMatrix asIntMatrix() {
      return parent instanceof IntMatrix ? (IntMatrix) parent : this;
    }

    @Override
    public ComplexMatrix asComplexMatrix() {
      return parent.asComplexMatrix();
    }
  }

  static class BitMatrixAdapter extends AbstractBitMatrix {

    private final Matrix parent;

    BitMatrixAdapter(Matrix parent) {
      super(parent.rows(), parent.columns());
      this.parent = parent;
    }

    @Override
    public void set(int i, int j, boolean value) {
      parent.set(i, j, value ? 1 : 0);
    }

    @Override
    public void set(int index, boolean value) {
      parent.set(index, value ? 1 : 0);
    }

    @Override
    public boolean get(int i, int j) {
      return getAsInt(i, j) == 1;
    }

    @Override
    public boolean get(int index) {
      return getAsInt(index) == 1;
    }

    @Override
    public BitMatrix reshape(int rows, int columns) {
      return parent instanceof BitMatrix ? (BitMatrix) parent.reshape(rows, columns)
          : new BitMatrixAdapter(parent.reshape(rows, columns));
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public BitMatrix newEmptyMatrix(int rows, int columns) {
      return new ArrayBitMatrix(rows, columns);
    }

    @Override
    public BitMatrix asBitMatrix() {
      return parent instanceof BitMatrix ? (BitMatrix) parent : this;
    }

    @Override
    public DoubleMatrix asDoubleMatrix() {
      return parent.asDoubleMatrix();
    }

    @Override
    public IntMatrix asIntMatrix() {
      return parent.asIntMatrix();
    }

    @Override
    public ComplexMatrix asComplexMatrix() {
      return parent.asComplexMatrix();
    }
  }
}
