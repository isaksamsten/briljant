package org.briljantframework.matrix;

import java.util.Arrays;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public class ArrayIntMatrix extends AbstractIntMatrix {
  private final int[] values;

  public ArrayIntMatrix(int rows, int cols, int[] values) {
    super(rows, cols);
    this.values = values;
  }

  public ArrayIntMatrix(int rows, int columns) {
    super(rows, columns);
    this.values = new int[size()];
  }

  @Override
  public IntMatrix reshape(int rows, int columns) {
    return new ArrayIntMatrix(rows, columns, values);
  }

  @Override
  public int get(int i, int j) {
    return values[Indexer.columnMajor(i, j, rows(), columns())];
  }

  @Override
  public int get(int index) {
    return values[index];
  }

  @Override
  public boolean isArrayBased() {
    return true;
  }

  @Override
  public IntMatrix newEmptyMatrix(int rows, int columns) {
    return new ArrayIntMatrix(rows, columns);
  }

  @Override
  public IntMatrix copy() {
    return new ArrayIntMatrix(rows(), columns(), Arrays.copyOf(values, values.length));
  }

  @Override
  public void put(int i, int j, int value) {
    values[Indexer.columnMajor(i, j, rows(), columns())] = value;
  }

  @Override
  public void put(int index, int value) {
    values[index] = value;
  }
}
