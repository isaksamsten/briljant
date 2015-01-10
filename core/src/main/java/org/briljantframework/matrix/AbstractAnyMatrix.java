package org.briljantframework.matrix;

import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.exceptions.SizeMismatchException;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public abstract class AbstractAnyMatrix implements AnyMatrix {

  protected static final String ARG_DIFF_SIZE = "Arguments imply different size.";

  private final int rows, cols, size;

  protected AbstractAnyMatrix(int rows, int cols) {
    this.rows = rows;
    this.cols = cols;
    this.size = Math.multiplyExact(rows, cols);
  }

  /**
   * Asserts that {@code rows() == other.rows() && columns() == other.columns()}. If not, throws
   * {@link org.briljantframework.exceptions.NonConformantException}
   * 
   * @param other another matrix
   */
  protected void assertEqualSize(AnyMatrix other) {
    if (this.rows() != other.rows() || this.columns() != other.columns()) {
      throw new NonConformantException(this, other);
    }
  }

  /**
   * Asserts that {@code size() == size}. If not, throws
   * {@link org.briljantframework.exceptions.SizeMismatchException}.
   * 
   * @param size the size
   */
  protected void assertSameSize(int size) {
    if (size() != size) {
      throw new SizeMismatchException("Total size of new matrix must be unchanged.", size(), size);
    }
  }

  /**
   * Asserts that {@code size() == other.size()}.
   * 
   * @param other other matrix
   * @see #assertSameSize(int)
   */
  protected void assertSameSize(AnyMatrix other) {
    assertSameSize(other.size());
  }

  /**
   * Adapts {@link org.briljantframework.matrix.AnyMatrix} as a
   * {@link org.briljantframework.matrix.DoubleMatrix}.
   */
  static class DoubleMatrixAdapter extends AbstractDoubleMatrix {
    private final AnyMatrix parent;

    public DoubleMatrixAdapter(AnyMatrix parent) {
      super(parent.rows(), parent.columns());
      this.parent = parent;
    }

    @Override
    public DoubleMatrix reshape(int rows, int columns) {
      return parent instanceof DoubleMatrix ? (DoubleMatrix) parent.reshape(rows, columns)
          : new DoubleMatrixAdapter(parent.reshape(rows, columns));
    }

    @Override
    public DoubleMatrix copy() {
      DoubleMatrix matrix = newEmptyMatrix(rows(), columns());
      for (int i = 0; i < size(); i++) {
        matrix.set(i, get(i));
      }
      return matrix;
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
    public IntMatrix asIntMatrix() {
      return parent instanceof IntMatrix ? (IntMatrix) parent : new IntMatrixAdapter(parent);
    }

    @Override
    public ComplexMatrix asComplexMatrix() {
      return parent instanceof ComplexMatrix ? (ComplexMatrix) parent : new ComplexMatrixAdapter(
          parent);
    }

  }

  /**
   * Adapts an {@link org.briljantframework.matrix.AnyMatrix} as a
   * {@link org.briljantframework.matrix.ComplexMatrix}
   */
  static class ComplexMatrixAdapter extends AbstractComplexMatrix {

    private final AnyMatrix parent;

    ComplexMatrixAdapter(AnyMatrix parent) {
      super(parent.rows(), parent.columns());
      this.parent = parent;
    }

    @Override
    public ComplexMatrix reshape(int rows, int columns) {
      return parent instanceof ComplexMatrix ? (ComplexMatrix) parent.reshape(rows, columns)
          : new ComplexMatrixAdapter(parent.reshape(rows, columns));
    }

    @Override
    public ComplexMatrix copy() {
      ComplexMatrix matrix = newEmptyMatrix(rows(), columns());
      for (int i = 0; i < size(); i++) {
        matrix.set(i, get(i));
      }
      return matrix;
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
      return parent instanceof DoubleMatrix ? (DoubleMatrix) parent : new DoubleMatrixAdapter(
          parent);
    }

    @Override
    public IntMatrix asIntMatrix() {
      return parent instanceof IntMatrix ? (IntMatrix) parent : new IntMatrixAdapter(parent);
    }

    @Override
    public ComplexMatrix asComplexMatrix() {
      return parent instanceof ComplexMatrix ? (ComplexMatrix) parent : this;
    }
  }

  /**
   * Adapts an {@link org.briljantframework.matrix.AnyMatrix} as an
   * {@link org.briljantframework.matrix.IntMatrix}.
   */
  static class IntMatrixAdapter extends AbstractIntMatrix {
    private final AnyMatrix parent;

    protected IntMatrixAdapter(AnyMatrix parent) {
      super(parent.rows(), parent.columns());
      this.parent = parent;
    }

    @Override
    public IntMatrix reshape(int rows, int columns) {
      return parent instanceof IntMatrix ? (IntMatrix) parent.reshape(rows, columns)
          : new IntMatrixAdapter(parent.reshape(rows, columns));
    }

    @Override
    public IntMatrix copy() {
      IntMatrix matrix = newEmptyMatrix(rows(), columns());
      for (int i = 0; i < size(); i++) {
        matrix.set(i, get(i));
      }
      return matrix;
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
    public DoubleMatrix asDoubleMatrix() {
      return parent instanceof DoubleMatrix ? (DoubleMatrix) parent : new DoubleMatrixAdapter(
          parent);
    }

    @Override
    public IntMatrix asIntMatrix() {
      return parent instanceof IntMatrix ? (IntMatrix) parent : this;
    }

    @Override
    public ComplexMatrix asComplexMatrix() {
      return parent instanceof ComplexMatrix ? (ComplexMatrix) parent : new ComplexMatrixAdapter(
          parent);
    }
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
  public ComplexMatrix asComplexMatrix() {
    return this instanceof ComplexMatrix ? (ComplexMatrix) this : new ComplexMatrixAdapter(this);
  }

  @Override
  public int rows() {
    return rows;
  }

  @Override
  public int columns() {
    return cols;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public BooleanMatrix lessThan(AnyMatrix other) {
    assertEqualSize(other);
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) < other.getAsDouble(i));
    }

    return bm;
  }

  @Override
  public BooleanMatrix lessThan(Number value) {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) < value.doubleValue());
    }
    return bm;
  }

  @Override
  public BooleanMatrix lessThanEqual(AnyMatrix other) {
    assertEqualSize(other);

    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) <= other.getAsDouble(i));
    }
    return bm;
  }

  @Override
  public BooleanMatrix lessThanEqual(Number value) {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) <= value.doubleValue());
    }
    return bm;
  }

  @Override
  public BooleanMatrix greaterThan(AnyMatrix other) {
    assertEqualSize(other);
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) > other.getAsDouble(i));
    }
    return bm;
  }

  @Override
  public BooleanMatrix greaterThan(Number value) {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) > value.doubleValue());
    }
    return bm;
  }

  @Override
  public BooleanMatrix greaterThanEquals(AnyMatrix other) {
    assertEqualSize(other);
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) >= other.getAsDouble(i));
    }

    return bm;
  }

  @Override
  public BooleanMatrix greaterThanEquals(Number value) {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) >= value.doubleValue());
    }
    return bm;
  }

  @Override
  public BooleanMatrix equalsTo(AnyMatrix other) {
    assertEqualSize(other);

    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) == other.getAsDouble(i));
    }

    return bm;
  }

  @Override
  public BooleanMatrix equalsTo(Number value) {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, getAsDouble(i) == value.doubleValue());
    }
    return bm;
  }
}
