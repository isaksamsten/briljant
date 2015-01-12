package org.briljantframework.matrix;

import java.util.Objects;

import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;

import com.google.common.collect.ImmutableTable;

/**
 * Created by Isak Karlsson on 12/01/15.
 */
public abstract class AbstractBitMatrix extends AbstractAnyMatrix implements BitMatrix {

  protected AbstractBitMatrix(int rows, int cols) {
    super(rows, cols);
  }

  @Override
  public Type getType() {
    return Type.BOOLEAN;
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
  public void set(int atIndex, AnyMatrix from, int fromIndex) {
    set(atIndex, from.getAsInt(fromIndex));
  }

  @Override
  public void set(int atRow, int atColumn, AnyMatrix from, int fromRow, int fromColumn) {
    set(atRow, atColumn, from.getAsInt(fromRow, fromColumn));
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
  public BitMatrix xor(BitMatrix other) {
    assertEqualSize(other);
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
    assertEqualSize(other);
    BitMatrix bm = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) || other.get(i));
    }
    return bm;
  }

  @Override
  public BitMatrix orNot(BitMatrix other) {
    assertEqualSize(other);
    BitMatrix bm = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) || !other.get(i));
    }
    return bm;
  }

  @Override
  public BitMatrix and(BitMatrix other) {
    assertEqualSize(other);
    BitMatrix bm = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) && other.get(i));
    }
    return bm;
  }

  @Override
  public BitMatrix andNot(BitMatrix other) {
    assertEqualSize(other);
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
}
