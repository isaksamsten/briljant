package org.briljantframework.matrix;

import java.util.Arrays;

import org.briljantframework.complex.Complex;

/**
 * Created by Isak Karlsson on 07/01/15.
 */
public class ArrayComplexMatrix extends AbstractComplexMatrix {

  private final Complex[] values;

  public ArrayComplexMatrix(int rows, int cols) {
    super(rows, cols);
    values = new Complex[rows * cols];
    Arrays.fill(values, Complex.ZERO);
  }

  protected ArrayComplexMatrix(int rows, int cols, Complex[] values) {
    super(rows, cols);
    this.values = values;

  }

  @Override
  public ComplexMatrix reshape(int rows, int columns) {
    assertSameSize(rows * columns);
    return new ArrayComplexMatrix(rows, columns, values);
  }

  @Override
  public Complex get(int i, int j) {
    return values[Indexer.columnMajor(i, j, rows(), columns())];
  }

  @Override
  public Complex get(int index) {
    return values[index];
  }

  @Override
  public boolean isArrayBased() {
    return false;
  }

  @Override
  public ComplexMatrix newEmptyMatrix(int rows, int columns) {
    return new ArrayComplexMatrix(rows, columns);
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
