package org.briljantframework.matrix;

import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.vector.Vector;

import com.google.common.base.Preconditions;

/**
 * Creates a matrix using an {@link org.briljantframework.vector.Vector}. While any vector is
 * allowed, generally {@link org.briljantframework.vector.DoubleVector} is the only suitable option.
 * 
 * For simplicity, new matrices created using {@link #newEmptyMatrix(int, int)} is not vector
 * matrices. Hence, most operations (e.g., {@link #mmul(DoubleMatrix)}) does not return matrices with
 * {@code this.getClass()}.
 * 
 * @author Isak Karlsson
 */
public class VectorDoubleMatrix extends AbstractDoubleMatrix {

  private final Vector vector;

  /**
   * Construct a new matrix, backed by {@code vector}. Asserts that
   * {@code rows * columns == vector.size()}.
   * 
   * @param rows the rows
   * @param columns the columns
   * @param vector the vector
   */
  public VectorDoubleMatrix(int rows, int columns, Vector vector) {
    super(rows, columns);
    Preconditions.checkArgument(rows * columns == vector.size(), "Invalid size.");
    this.vector = vector;
  }

  /**
   * Constructs a new matrix, backed by {@code vector}. Asserts that {@code columns} is evenly
   * dividable by {@code vector.size()}.
   * 
   * @param columns the columns
   * @param vector the vector
   */
  public VectorDoubleMatrix(int columns, Vector vector) {
    this(vector.size() / columns, columns, vector);
  }

  /**
   * Wrap vector as a matrix based column vector
   * 
   * @param vector the vector to wrap
   * @return a 1 x vector.size() column matrix
   */
  public static DoubleMatrix wrap(Vector vector) {
    return new VectorDoubleMatrix(1, vector.size(), vector);
  }

  @Override
  public DoubleMatrix reshape(int rows, int columns) {
    assertSameSize(rows * columns);
    return new VectorDoubleMatrix(rows, columns, vector);
  }

  @Override
  public double get(int i, int j) {
    return vector.getAsDouble(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public double get(int index) {
    return vector.getAsDouble(index);
  }

  @Override
  public boolean isArrayBased() {
    return true;
  }

  @Override
  public DoubleMatrix newEmptyMatrix(int rows, int columns) {
    return new ArrayDoubleMatrix(rows, columns);
  }

  @Override
  public DoubleMatrix copy() {
    return new ArrayDoubleMatrix(getShape(), vector.toDoubleArray());
  }

  @Override
  public void set(int i, int j, double value) {
    throw new UnsupportedOperationException("Can't mutate VectorMatrix.");
  }

  @Override
  public void set(int index, double value) {
    throw new UnsupportedOperationException("Can't mutate VectorMatrix.");
  }

  @Override
  public int size() {
    return vector.size();
  }

  /**
   * {@inheritDoc}
   *
   * Please note that this won't return a {@code VectorMatrix} but an other matrix with
   * {@code isArrayBased() == true}
   */
  @Override
  public DoubleMatrix mmul(double alpha, DoubleMatrix other, double beta) {
    if (this.columns() != other.rows()) {
      throw new NonConformantException(this, other);
    }

    if (other.isArrayBased()) {
      double[] tmp = new double[this.rows() * other.columns()];
      Doubles.mmul(this, alpha, other, beta, tmp);
      return new ArrayDoubleMatrix(other.columns(), tmp);
    } else {
      return super.mmul(alpha, other, beta);
    }
  }

  @Override
  public double[] asDoubleArray() {
    return vector.asDoubleArray();
  }
}
