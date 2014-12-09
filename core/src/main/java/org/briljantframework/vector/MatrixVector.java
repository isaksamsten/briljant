package org.briljantframework.vector;

import java.util.Iterator;

import org.briljantframework.matrix.Matrix;

import com.google.common.collect.UnmodifiableIterator;

/**
 * Created by Isak Karlsson on 08/12/14.
 */
public class MatrixVector extends AbstractDoubleVector {
  private final Matrix matrix;

  public MatrixVector(Matrix matrix) {
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
    matrix.unsafe(x -> System.arraycopy(x, 0, copy, 0, x.length));
    // TODO(isak): optimze
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
