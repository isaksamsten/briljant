package org.briljantframework.matrix;

import java.util.Objects;

import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.collect.ImmutableTable;

/**
 * Created by Isak Karlsson on 12/01/15.
 */
public abstract class AbstractBitMatrix extends AbstractMatrix implements BitMatrix {

  protected AbstractBitMatrix(int rows, int cols) {
    super(rows, cols);
  }

  @Override
  public DataType getDataType() {
    return DataType.BOOLEAN;
  }

  @Override
  public Complex getAsComplex(int i, int j) {
    return new Complex(getAsDouble(i, j));
  }

  @Override
  public Complex getAsComplex(int index) {
    return new Complex(getAsDouble(index));
  }

  @Override
  public void set(int i, int j, Complex value) {
    set(i, j, value.doubleValue());
  }

  @Override
  public void set(int index, Complex value) {
    set(index, value.doubleValue());
  }

  @Override
  public double getAsDouble(int i, int j) {
    return get(i, j) ? 1 : 0;
  }

  @Override
  public double getAsDouble(int index) {
    return get(index) ? 1 : 0;
  }

  @Override
  public void set(int i, int j, double value) {
    set(i, j, (int) value);
  }

  @Override
  public void set(int index, double value) {
    set(index, (int) value);
  }

  @Override
  public int getAsInt(int i, int j) {
    return get(i, j) ? 1 : 0;
  }

  @Override
  public int getAsInt(int index) {
    return get(index) ? 1 : 0;
  }

  @Override
  public void set(int i, int j, int value) {
    set(i, j, value == 1);
  }

  @Override
  public void set(int index, int value) {
    set(index, value == 1);
  }

  @Override
  public void set(int i, int j, Number number) {
    set(i, j, number.intValue());
  }

  @Override
  public void set(int index, Number number) {
    set(index, number.intValue());
  }

  @Override
  public void set(int atIndex, Matrix from, int fromIndex) {
    set(atIndex, from.getAsInt(fromIndex));
  }

  @Override
  public void set(int atRow, int atColumn, Matrix from, int fromRow, int fromColumn) {
    set(atRow, atColumn, from.getAsInt(fromRow, fromColumn));
  }

  @Override
  public int compare(int toIndex, Matrix from, int fromIndex) {
    return Boolean.compare(get(toIndex), from.getAsInt(fromIndex) == 1);
  }

  @Override
  public int compare(int toRow, int toColumn, Matrix from, int fromRow, int fromColumn) {
    return Boolean.compare(get(toRow, toColumn), from.getAsInt(fromRow, fromColumn) == 1);
  }

  @Override
  public BitMatrix getRowView(int i) {
    return new BitMatrixView(this, i, 0, 1, columns());
  }

  @Override
  public BitMatrix getColumnView(int index) {
    return new BitMatrixView(this, 0, index, rows(), 1);
  }

  @Override
  public BitMatrix getDiagonalView() {
    throw new UnsupportedOperationException();
  }

  @Override
  public BitMatrix getView(int rowOffset, int colOffset, int rows, int columns) {
    return new BitMatrixView(this, rowOffset, colOffset, rows, columns);
  }

  @Override
  public BitMatrix transpose() {
    BitMatrix matrix = newEmptyMatrix(columns(), rows());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(j, i, get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public BitMatrix copy() {
    BitMatrix n = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      n.set(i, get(i));
    }
    return n;
  }

  @Override
  public IncrementalBuilder newIncrementalBuilder() {
    return new IncrementalBuilder();
  }

  @Override
  public int hashCode() {
    int value = Objects.hash(rows(), columns());
    for (int i = 0; i < size(); i++) {
      value = value * 31 + Boolean.hashCode(get(i));
    }
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof BitMatrix) {
      BitMatrix o = (BitMatrix) obj;
      if (rows() == o.rows() && columns() == o.columns()) {
        for (int i = 0; i < size(); i++) {
          if (get(i) != o.get(i)) {
            return false;
          }
        }
      } else {
        return false;
      }

    } else {
      return false;
    }

    return true;
  }

  @Override
  public String toString() {
    ImmutableTable.Builder<Integer, Integer, String> builder = ImmutableTable.builder();
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        builder.put(i, j, String.format("%b", get(i, j)));
      }
    }
    StringBuilder out = new StringBuilder();
    Utils.prettyPrintTable(out, builder.build(), 0, 2, false, false);
    out.append("shape: ").append(getShape()).append(" type: boolean");
    return out.toString();
  }

  @Override
  public void swap(int a, int b) {
    boolean tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  @Override
  public BitMatrix xor(BitMatrix other) {
    Check.equalShape(this, other);
    BitMatrix bm = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      boolean otherHas = other.get(i);
      boolean thisHas = get(i);
      bm.set(i, (thisHas || otherHas) && !(thisHas && otherHas));
    }
    return bm;
  }

  @Override
  public BitMatrix or(BitMatrix other) {
    Check.equalShape(this, other);
    BitMatrix bm = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) || other.get(i));
    }
    return bm;
  }

  @Override
  public BitMatrix orNot(BitMatrix other) {
    Check.equalShape(this, other);
    BitMatrix bm = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) || !other.get(i));
    }
    return bm;
  }

  @Override
  public BitMatrix and(BitMatrix other) {
    Check.equalShape(this, other);
    BitMatrix bm = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) && other.get(i));
    }
    return bm;
  }

  @Override
  public BitMatrix andNot(BitMatrix other) {
    Check.equalShape(this, other);
    BitMatrix bm = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) && !other.get(i));
    }
    return bm;
  }

  @Override
  public BitMatrix not() {
    BitMatrix bm = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, !get(i));
    }
    return bm;
  }

  public static class IncrementalBuilder implements Matrix.IncrementalBuilder {

    private IntArrayList buffer = new IntArrayList();

    @Override
    public void add(Matrix from, int i, int j) {
      buffer.add(from.getAsInt(i, j));
    }

    @Override
    public void add(Matrix from, int index) {
      buffer.add(from.getAsInt(index));
    }

    @Override
    public Matrix build() {
      BitMatrix n = new ArrayBitMatrix(buffer.size(), 1);
      for (int i = 0; i < buffer.size(); i++) {
        n.set(i, n.get(i));
      }
      return n;
    }
  }
}
