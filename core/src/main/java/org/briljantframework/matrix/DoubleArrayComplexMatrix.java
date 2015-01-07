package org.briljantframework.matrix;

import java.util.Arrays;
import java.util.Collection;

import org.briljantframework.complex.Complex;

import com.google.common.base.Preconditions;

/**
 * Created by Isak Karlsson on 02/01/15.
 */
public class DoubleArrayComplexMatrix extends AbstractComplexMatrix {

  private final double[] values;

  public DoubleArrayComplexMatrix(int rows, int cols) {
    this(rows, cols, new double[rows * cols * 2]);
  }

  public DoubleArrayComplexMatrix(ComplexMatrix other) {
    this(other.rows(), other.columns());
    if (other.isArrayBased()) {
      System.arraycopy(other.asDoubleArray(), 0, values, 0, values.length);
    } else {
      int index = 0;
      for (int i = 0; i < other.size(); i++) {
        Complex complex = other.get(i);
        values[index++] = complex.real();
        values[index++] = complex.imag();
      }
    }
  }

  public DoubleArrayComplexMatrix(Matrix matrix) {
    this(matrix.rows(), matrix.columns());
    int index = 0;
    for (int i = 0; i < matrix.size(); i++) {
      values[index++] = matrix.get(i);
      index++; // imag is zero
    }
  }

  public DoubleArrayComplexMatrix(int rows, int cols, Collection<? extends Complex> values) {
    this(rows, cols);
    Preconditions.checkArgument(rows * cols == values.size());
    int index = 0;
    for (Complex value : values) {
      this.values[index++] = value.real();
      this.values[index++] = value.imag();
    }
  }

  protected DoubleArrayComplexMatrix(int rows, int cols, double[] values) {
    super(rows, cols);
    this.values = values;
  }

  @Override
  public ComplexMatrix reshape(int rows, int columns) {
    Preconditions.checkArgument(rows * columns == size(),
        "Total size of new matrix must be unchanged.");
    return new DoubleArrayComplexMatrix(rows, columns, values);
  }

  @Override
  public void put(int i, int j, Complex value) {
    int index = Indexer.columnMajor(i, j, rows(), columns()) * 2;
    values[index] = value.real();
    values[index + 1] = value.imag();
  }

  @Override
  public Complex get(int i, int j) {
    int index = Indexer.columnMajor(i, j, rows(), columns()) * 2;
    return new Complex(values[index], values[index + 1]);
  }

  @Override
  public void put(int index, Complex value) {
    index = index * 2;
    values[index] = value.real();
    values[index + 1] = value.imag();
  }

  @Override
  public Complex get(int index) {
    index = index * 2;
    return new Complex(values[index], values[index + 1]);
  }

  @Override
  public boolean isArrayBased() {
    return true;
  }

  @Override
  public ComplexMatrix newEmptyMatrix(int rows, int columns) {
    return new DoubleArrayComplexMatrix(rows, columns);
  }

  @Override
  public ComplexMatrix copy() {
    return new DoubleArrayComplexMatrix(rows(), columns(), Arrays.copyOf(values, values.length));
  }

}
