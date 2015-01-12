package org.briljantframework.matrix;

import java.util.Arrays;

import org.briljantframework.Check;

/**
 * @author Isak Karlsson
 */
public class ArrayBitMatrix extends AbstractBitMatrix {

  private final boolean[] values;

  public ArrayBitMatrix(int rows, int cols) {
    super(rows, cols);
    this.values = new boolean[size()];
  }

  public ArrayBitMatrix(int rows, int columns, boolean... values) {
    this(rows, columns, values, true);
  }

  protected ArrayBitMatrix(int rows, int columns, boolean[] values, boolean copy) {
    super(rows, columns);
    this.values = values;
  }

  public ArrayBitMatrix(boolean... values) {
    super(values.length, 1);
    this.values = Arrays.copyOf(values, values.length);
  }

  public void set(int i, int j, boolean value) {
    values[Indexer.columnMajor(i, j, rows(), columns())] = value;
  }

  public void set(int index, boolean value) {
    values[index] = value;
  }

  public boolean get(int i, int j) {
    return values[Indexer.columnMajor(i, j, rows(), columns())];
  }

  public boolean get(int index) {
    return values[index];
  }

  @Override
  public BitMatrix reshape(int rows, int columns) {
    Check.size(UNCHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new ArrayBitMatrix(rows, columns, values, false);
  }

  public BitMatrix copy() {
    ArrayBitMatrix bm = new ArrayBitMatrix(rows(), columns());
    System.arraycopy(values, 0, bm.values, 0, values.length);
    return bm;
  }

  @Override
  public BitMatrix newEmptyMatrix(int rows, int columns) {
    return new ArrayBitMatrix(rows, columns);
  }
}
