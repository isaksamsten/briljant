package org.briljantframework.matrix;

import org.briljantframework.exception.NonConformantException;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vector;

import com.github.fommil.netlib.BLAS;
import com.google.common.base.Preconditions;

/**
 * Created by Isak Karlsson on 02/12/14.
 */
public class VectorMatrix extends AbstractMatrix {

  private static final BLAS blas = BLAS.getInstance();

  private final Vector vector;

  public VectorMatrix(Vector vector, int rows, int columns) {
    super(rows, columns);
    Preconditions.checkArgument(rows * columns == vector.size(), "Invalid size.");
    this.vector = vector;
  }

  public VectorMatrix(Vector vector, int columns) {
    this(vector, vector.size() / columns, columns);
  }

  public static Matrix wrap(DoubleVector vector) {
    return new VectorMatrix(vector, 1, vector.size());
  }

  /**
   * {@inheritDoc}
   * 
   * Please note that this won't return a {@code VectorMatrix} but an other matrix with
   * {@code isArrayBased() == true}
   */
  @Override
  public Matrix mmul(double alpha, Matrix other, double beta) {
    if (this.columns() != other.rows()) {
      throw new NonConformantException(this, other);
    }

    double[] tmp = new double[this.rows() * other.columns()];
    if (other.isArrayBased()) {
      blas.dgemm("n", "n", this.rows(), other.columns(), other.rows(), alpha,
          vector.asDoubleArray(), this.rows(), other.asDoubleArray(), other.rows(), beta, tmp,
          this.rows());
    } else {
      return super.mmul(alpha, other, beta);
    }

    return new ArrayMatrix(other.columns(), tmp);
  }

  @Override
  public double[] asDoubleArray() {
    return vector.asDoubleArray();
  }

  @Override
  public void put(int i, int j, double value) {
    throw new UnsupportedOperationException("Can't mutate VectorMatrix.");
  }

  @Override
  public double get(int i, int j) {
    return vector.getAsDouble(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public void put(int index, double value) {
    throw new UnsupportedOperationException("Can't mutate VectorMatrix.");
  }

  @Override
  public double get(int index) {
    return vector.getAsDouble(index);
  }

  @Override
  public int size() {
    return vector.size();
  }

  @Override
  public boolean isArrayBased() {
    return true;
  }

  @Override
  public Matrix newEmptyMatrix(int rows, int columns) {
    return new ArrayMatrix(rows, columns);
  }

  @Override
  public Matrix copy() {
    return new ArrayMatrix(getShape(), vector.toDoubleArray());
  }
}
