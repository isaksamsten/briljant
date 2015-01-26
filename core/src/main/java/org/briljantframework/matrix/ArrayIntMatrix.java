package org.briljantframework.matrix;

import java.util.Arrays;

/**
 * @author Isak Karlsson
 */
public class ArrayIntMatrix extends AbstractIntMatrix {
  private final int[] values;

  protected ArrayIntMatrix(int rows, int cols, int[] values) {
    super(rows, cols);
    this.values = values;
  }

  public ArrayIntMatrix(int rows, int columns) {
    super(rows, columns);
    this.values = new int[size()];
  }

  /**
   * Creates an unsafe {@link IntMatrix}. Modifications of {@code array}, propagates to the return
   * matrix.
   * 
   * @param array an int array
   * @return a new matrix
   */
  public static IntMatrix wrap(int... array) {
    return new ArrayIntMatrix(array.length, 1, array);
  }

  @Override
  public IntMatrix reshape(int rows, int columns) {
    return new ArrayIntMatrix(rows, columns, values);
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public IntMatrix copy() {
    return new ArrayIntMatrix(rows(), columns(), Arrays.copyOf(values, values.length));
  }

  @Override
  public IntMatrix newEmptyMatrix(int rows, int columns) {
    return new ArrayIntMatrix(rows, columns);
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
  public int[] asIntArray() {
    return values;
  }

  @Override
  public void set(int i, int j, int value) {
    values[Indexer.columnMajor(i, j, rows(), columns())] = value;
  }

  @Override
  public void set(int index, int value) {
    values[index] = value;
  }
}
