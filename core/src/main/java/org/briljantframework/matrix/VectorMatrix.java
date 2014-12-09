package org.briljantframework.matrix;

import java.util.function.Consumer;
import java.util.function.Function;

import org.briljantframework.exception.NonConformantException;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vector;

import com.google.common.base.Preconditions;

/**
 * Created by Isak Karlsson on 02/12/14.
 */
public class VectorMatrix extends AbstractMatrix {

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

  @Override
  public Matrix copy() {
    return new ArrayMatrix(getShape(), vector.toDoubleArray());
  }

  @Override
  public Matrix mmul(double alpha, Matrix other, double beta) {
    if (this.columns() != other.rows()) {
      throw new NonConformantException(this, other);
    }

    double[] tmp = new double[this.rows() * other.columns()];
    if (other instanceof ArrayMatrix) {
      ArrayMatrix b = (ArrayMatrix) other;
      blas.dgemm("n", "n", this.rows(), b.columns(), b.rows(), alpha, this.vector.asDoubleArray(),
          this.rows(), b.values, b.rows(), beta, tmp, this.rows());
    } else if (other instanceof VectorMatrix) {
      VectorMatrix b = (VectorMatrix) other;
      blas.dgemm("n", "n", this.rows(), b.columns(), b.rows(), alpha, this.vector.asDoubleArray(),
          this.rows(), b.vector.asDoubleArray(), b.rows(), beta, tmp, this.rows());
    } else {
      other.unsafe(b -> blas.dgemm("n", "n", this.rows(), other.columns(), other.rows(), alpha,
          this.vector.asDoubleArray(), this.rows(), b, other.rows(), beta, tmp, this.rows()));
    }
    return new ArrayMatrix(other.columns(), tmp);
  }

  @Override
  public Matrix unsafeTransform(Function<double[], Matrix> op) {
    return op.apply(vector.asDoubleArray());
  }

  @Override
  public void unsafe(Consumer<double[]> consumer) {
    consumer.accept(vector.asDoubleArray());
  }

  @Override
  public void put(int i, int j, double value) {
    throw new UnsupportedOperationException("Can't mutate VectorMatrix.");
  }

  @Override
  public void put(int index, double value) {
    throw new UnsupportedOperationException("Can't mutate VectorMatrix.");
  }

  @Override
  public Matrix newEmptyMatrix(int rows, int columns) {
    return new ArrayMatrix(rows, columns);
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
  public int size() {
    return vector.size();
  }
}
