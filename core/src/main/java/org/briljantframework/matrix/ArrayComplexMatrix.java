package org.briljantframework.matrix;

import java.util.Arrays;

import org.briljantframework.complex.Complex;

/**
 * Created by Isak Karlsson on 07/01/15.
 */
public class ArrayComplexMatrix extends AbstractComplexMatrix {

  private final Complex[] values;
  private Complex defaultValue = Complex.ZERO;

  public ArrayComplexMatrix(int rows, int cols) {
    super(rows, cols);
    values = new Complex[rows * cols];
    Arrays.fill(values, Complex.ZERO);
  }

  protected ArrayComplexMatrix(int rows, int cols, Complex[] values) {
    super(rows, cols);
    this.values = values;
  }

  protected ArrayComplexMatrix(int rows, int columns, Complex defaultValue) {
    super(rows, columns);
    this.values = new Complex[size()];
    this.defaultValue = defaultValue;
  }

  public static ArrayComplexMatrix withDefaultValue(int rows, int columns, Complex zero) {
    return new ArrayComplexMatrix(rows, columns, zero);
  }

  @Override
  public ComplexMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new ArrayComplexMatrix(rows, columns, values);
  }

  @Override
  public ComplexMatrix newEmptyMatrix(int rows, int columns) {
    return new ArrayComplexMatrix(rows, columns);
  }

  @Override
  public Complex get(int i, int j) {
    final Complex value = values[Indexer.columnMajor(i, j, rows(), columns())];
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  @Override
  public Complex get(int index) {
    final Complex value = values[index];
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  @Override
  public boolean isArrayBased() {
    return false;
  }

  @Override
  public ComplexMatrix copy() {
    return new ArrayComplexMatrix(rows(), columns(), Arrays.copyOf(values, values.length));
  }

  @Override
  public void set(int i, int j, Complex value) {
    values[Indexer.columnMajor(i, j, rows(), columns())] = value;
  }

  @Override
  public void set(int index, Complex value) {
    values[index] = value;
  }
}
