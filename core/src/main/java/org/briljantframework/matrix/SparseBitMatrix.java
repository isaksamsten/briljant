package org.briljantframework.matrix;

import org.briljantframework.Check;
import org.briljantframework.matrix.storage.SparseBooleanStorage;
import org.briljantframework.matrix.storage.Storage;

import java.util.BitSet;

/**
 * @author Isak Karlsson
 */
class SparseBitMatrix extends AbstractBitMatrix {

  private final BitSet values;

  protected SparseBitMatrix(int size) {
    super(size);
    values = new BitSet(size);
  }

  protected SparseBitMatrix(int rows, int cols) {
    super(rows, cols);
    values = new BitSet(Math.multiplyExact(rows, cols));
  }

  private SparseBitMatrix(BitSet values, int rows, int columns) {
    super(rows, columns);
    this.values = values;
  }

  @Override
  public BitMatrix xor(BitMatrix other) {
    Check.equalShape(this, other);
    if (other instanceof SparseBitMatrix) {
      BitSet a = (BitSet) values.clone();
      BitSet b = (BitSet) ((SparseBitMatrix) other).values.clone();
      a.xor(b);
      return new SparseBitMatrix(a, rows(), columns());
    }
    return super.xor(other);
  }

  @Override
  public BitMatrix or(BitMatrix other) {
    Check.equalShape(this, other);
    if (other instanceof SparseBitMatrix) {
      BitSet a = (BitSet) values.clone();
      BitSet b = (BitSet) ((SparseBitMatrix) other).values.clone();
      a.or(b);
      return new SparseBitMatrix(a, rows(), columns());
    }
    return super.or(other);
  }

  @Override
  public BitMatrix orNot(BitMatrix other) {
    Check.equalShape(this, other);
    if (other instanceof SparseBitMatrix) {
      BitSet a = (BitSet) values.clone();
      BitSet b = (BitSet) ((SparseBitMatrix) other).values.clone();
      a.or(b);
      a.flip(0, a.length());
      return new SparseBitMatrix(a, rows(), columns());
    }
    return super.orNot(other);
  }

  @Override
  public BitMatrix and(BitMatrix other) {
    Check.equalShape(this, other);
    if (other instanceof SparseBitMatrix) {
      BitSet a = (BitSet) values.clone();
      BitSet b = (BitSet) ((SparseBitMatrix) other).values.clone();
      a.and(b);
      return new SparseBitMatrix(a, rows(), columns());
    }
    return super.and(other);
  }

  @Override
  public BitMatrix andNot(BitMatrix other) {
    Check.equalShape(this, other);
    if (other instanceof SparseBitMatrix) {
      BitSet a = (BitSet) values.clone();
      BitSet b = (BitSet) ((SparseBitMatrix) other).values.clone();
      a.andNot(b);
      return new SparseBitMatrix(a, rows(), columns());
    }
    return super.andNot(other);
  }

  @Override
  public BitMatrix not() {
    BitSet a = (BitSet) values.clone();
    a.flip(0, a.length());
    return new SparseBitMatrix(a, rows(), columns());
  }

  @Override
  public void set(int i, int j, boolean value) {
    set(Indexer.columnMajor(i, j, rows(), columns()), value);
  }

  @Override
  public void set(int index, boolean value) {
    values.set(index, value);
  }

  @Override
  public boolean get(int i, int j) {
    return get(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public boolean get(int index) {
    return values.get(index);
  }

  @Override
  public BitMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new SparseBitMatrix(values, rows, columns);
  }

  @Override
  public BitMatrix newEmptyMatrix(int rows, int columns) {
    return new SparseBitMatrix(rows, columns);
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public Storage getStorage() {
    return new SparseBooleanStorage(size(), values);
  }
}
