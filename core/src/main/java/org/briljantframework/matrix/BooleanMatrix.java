package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;

import java.util.Arrays;

import org.briljantframework.Utils;

import com.google.common.collect.ImmutableTable;

/**
 * Created by Isak Karlsson on 11/10/14.
 */
public class BooleanMatrix extends AbstractIntMatrix {

  private final boolean[] values;

  public BooleanMatrix(Shape shape) {
    this(shape.rows, shape.columns);
  }

  public BooleanMatrix(int rows, int cols) {
    super(rows, cols);
    this.values = new boolean[size()];
  }

  public BooleanMatrix(int rows, int columns, boolean[] values) {
    super(rows, columns);
    this.values = Arrays.copyOf(values, values.length);
  }

  public void set(int i, int j, boolean value) {
    values[Indexer.columnMajor(i, j, rows(), columns())] = value;
  }

  public void set(int index, boolean value) {
    values[index] = value;
  }

  public BooleanMatrix transpose() {
    BooleanMatrix matrix = new BooleanMatrix(columns(), rows());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, i, get(i, i));
      }
    }
    return matrix;
  }

  @Override
  public BooleanMatrix negate() {
    return not();
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder("BooleanMatrix\n");
    ImmutableTable.Builder<Integer, Integer, String> builder = ImmutableTable.builder();
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        builder.put(i, j, String.format("%s", has(i, j)));
      }
    }
    Utils.prettyPrintTable(out, builder.build(), 0, 2, false, false);
    out.append("shape: ").append(getShape()).append(" type: boolean");
    return out.toString();
  }

  @Override
  public BooleanMatrix reshape(int rows, int columns) {
    return new BooleanMatrix(rows, columns, values);
  }

  public BooleanMatrix copy() {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    System.arraycopy(values, 0, bm.values, 0, values.length);
    return bm;
  }

  @Override
  public BooleanMatrix newEmptyMatrix(int rows, int columns) {
    return new BooleanMatrix(rows, columns);
  }

  @Override
  public int get(int i, int j) {
    return values[Indexer.columnMajor(i, j, rows(), columns())] ? 1 : 0;
  }

  @Override
  public int get(int index) {
    return values[index] ? 1 : 0;
  }

  @Override
  public boolean isArrayBased() {
    return false;
  }

  public void set(int i, int j, int value) {
    values[Indexer.columnMajor(i, j, rows(), columns())] = value != 0;
  }

  public void set(int index, int value) {
    values[index] = value != 0;
  }

  public boolean has(int i, int j) {
    return values[Indexer.columnMajor(i, j, rows(), columns())];
  }

  public boolean has(int index) {
    return values[checkElementIndex(index, size())];
  }

  public BooleanMatrix and(BooleanMatrix other) {
    checkArgument(hasEqualShape(other));

    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        bm.set(i, j, has(i, j) && other.has(i, j));
      }
    }
    return bm;
  }

  public BooleanMatrix or(BooleanMatrix other) {
    checkArgument(hasEqualShape(other));

    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        bm.set(i, j, has(i, j) || other.has(i, j));
      }
    }
    return bm;
  }

  public BooleanMatrix not() {
    BooleanMatrix bm = new BooleanMatrix(getShape());
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        bm.set(i, j, !has(i, j));
      }
    }
    return bm;
  }
}
