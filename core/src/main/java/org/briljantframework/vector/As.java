package org.briljantframework.vector;

import java.util.Iterator;

import org.briljantframework.matrix.Matrix;

import com.google.common.collect.UnmodifiableIterator;

/**
 * @author Isak Karlsson
 */
public final class As {

  private As() {}

  /**
   * Treat {@code matrix} as a vector. Since the returned vector is a view of the underlying
   * {@code matrix} will mutations to {@code matrix} cause the returned vector to change.
   * 
   * @param matrix the matrix
   * @return a vector representation of {@code matrix}
   */
  public static Vector vector(Matrix matrix) {
    return new MatrixVectorAdapter(matrix);
  }

  /**
   * Convert {@code vector} to a StringVector.
   * 
   * @param vector the vector
   * @return a new StringVector
   */
  public static StringVector stringVector(Vector vector) {
    if (vector instanceof StringVector) {
      return (StringVector) vector;
    }
    return new StringVector.Builder().addAll(vector).build();
  }

  public static DoubleVector doubleVector(Vector vector) {
    if (vector instanceof DoubleVector) {
      return (DoubleVector) vector;
    }
    return new DoubleVector.Builder().addAll(vector).build();
  }

  public static IntVector intVector(Vector vector) {
    if (vector instanceof IntVector) {
      return (IntVector) vector;
    }
    return new IntVector.Builder().addAll(vector).build();
  }

  public static BinaryVector binaryVector(Vector vector) {
    if (vector instanceof BinaryVector) {
      return (BinaryVector) vector;
    }
    return new BinaryVector.Builder().addAll(vector).build();
  }

  public static ComplexVector complexVector(Vector vector) {
    if (vector instanceof ComplexVector) {
      return (ComplexVector) vector;
    }
    return new ComplexVector.Builder().addAll(vector).build();
  }

  /*
   * adapts a matrix to the vector interface
   */
  private static class MatrixVectorAdapter extends AbstractDoubleVector {
    private final Matrix matrix;

    public MatrixVectorAdapter(Matrix matrix) {
      this.matrix = matrix;
    }

    @Override
    public Iterator<Double> iterator() {
      return new UnmodifiableIterator<Double>() {
        private int current = 0;

        @Override
        public boolean hasNext() {
          return current < matrix.size();
        }

        @Override
        public Double next() {
          return matrix.get(current++);
        }
      };
    }

    @Override
    public double getAsDouble(int index) {
      return matrix.get(index);
    }

    @Override
    public int size() {
      return matrix.size();
    }

    @Override
    public Builder newCopyBuilder() {
      double[] copy = new double[matrix.size()];
      System.arraycopy(matrix.asDoubleArray(), 0, copy, 0, copy.length);
      return DoubleVector.newBuilderWithInitialValues(copy);
    }

    @Override
    public Builder newBuilder() {
      return new DoubleVector.Builder();
    }

    @Override
    public Builder newBuilder(int size) {
      return new DoubleVector.Builder(size);
    }
  }
}
