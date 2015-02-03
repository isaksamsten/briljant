package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.briljantframework.matrix.Indexer.columnMajor;
import static org.briljantframework.matrix.Indexer.sliceIndex;

import java.util.Iterator;
import java.util.Objects;

import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.storage.Storage;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Created by Isak Karlsson on 12/01/15.
 */
public abstract class AbstractBitMatrix extends AbstractMatrix implements BitMatrix {

  protected AbstractBitMatrix(int size) {
    super(size);
  }

  protected AbstractBitMatrix(int rows, int cols) {
    super(rows, cols);
  }

  @Override
  public Complex getAsComplex(int i, int j) {
    return get(i, j) ? Complex.ONE : Complex.ZERO;
  }

  @Override
  public Complex getAsComplex(int index) {
    return get(index) ? Complex.ONE : Complex.ZERO;
  }

  @Override
  public double getAsDouble(int i, int j) {
    return getAsInt(i, j);
  }

  @Override
  public double getAsDouble(int index) {
    return getAsInt(index);
  }

  @Override
  public long getAsLong(int i, int j) {
    return getAsInt(i, j);
  }

  @Override
  public long getAsLong(int index) {
    return getAsInt(index);
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
  public boolean getAsBit(int i, int j) {
    return get(i, j);
  }

  @Override
  public boolean getAsBit(int index) {
    return get(index);
  }

  public void set(int i, int j, boolean value) {
    getStorage().setBoolean(Indexer.columnMajor(i, j, rows(), columns()), value);
  }

  public void set(int index, boolean value) {
    getStorage().setBoolean(index, value);
  }

  public boolean get(int i, int j) {
    return getStorage().getBoolean(Indexer.columnMajor(i, j, rows(), columns()));
  }

  public boolean get(int index) {
    return getStorage().getBoolean(index);
  }

  @Override
  public void set(int atIndex, Matrix from, int fromIndex) {
    set(atIndex, from.getAsBit(fromIndex));
  }

  @Override
  public void set(int atRow, int atColumn, Matrix from, int fromRow, int fromColumn) {
    set(atRow, atColumn, from.getAsBit(fromRow, fromColumn));
  }

  @Override
  public int compare(int toIndex, Matrix from, int fromIndex) {
    return Boolean.compare(get(toIndex), from.getAsInt(fromIndex) == 1);
  }

  @Override
  public int compare(int toRow, int toColumn, Matrix from, int fromRow, int fromColumn) {
    return Boolean.compare(get(toRow, toColumn), from.getAsBit(fromRow, fromColumn));
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
  public Matrix slice(Slice rows, Slice columns) {
    return new SliceBitMatrix(this, rows, columns);
  }

  @Override
  public Matrix slice(Slice slice, Axis axis) {
    if (axis == Axis.ROW) {
      return new SliceBitMatrix(this, slice, Slice.slice(columns()));
    } else {
      return new SliceBitMatrix(this, Slice.slice(rows()), slice);
    }
  }

  @Override
  public Matrix slice(Slice slice) {
    return new FlatSliceBitMatrix(this, slice);
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
  public Iterator<Boolean> iterator() {
    return new UnmodifiableIterator<Boolean>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public Boolean next() {
        return get(current++);
      }
    };
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

  @Override
  public BitMatrix newEmptyVector(int size) {
    return newEmptyMatrix(size, 1);
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
      BitMatrix n = new DefaultBitMatrix(buffer.size(), 1);
      for (int i = 0; i < buffer.size(); i++) {
        n.set(i, buffer.get(i) == 1);
      }
      return n;
    }
  }

  protected static class SliceBitMatrix extends AbstractBitMatrix {

    private final Slice row, column;
    private final BitMatrix parent;

    public SliceBitMatrix(BitMatrix parent, Slice row, Slice column) {
      this(parent, checkNotNull(row).size(), row, checkNotNull(column).size(), column);
    }

    public SliceBitMatrix(BitMatrix parent, int rows, Slice row, int columns, Slice column) {
      super(rows, columns);
      this.row = checkNotNull(row);
      this.column = checkNotNull(column);
      this.parent = checkNotNull(parent);
    }

    @Override
    public void set(int i, int j, boolean value) {
      parent.set(sliceIndex(row.step(), i, parent.rows()),
          sliceIndex(column.step(), j, parent.columns()), value);
    }

    @Override
    public void set(int index, boolean value) {
      int row = index % rows();
      int col = index / rows();
      set(row, col, value);
    }

    @Override
    public BitMatrix reshape(int rows, int columns) {
      Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
      return new SliceBitMatrix(parent, rows, row, columns, column);
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public BitMatrix newEmptyMatrix(int rows, int columns) {
      return parent.newEmptyMatrix(rows, columns);
    }

    @Override
    public boolean get(int i, int j) {
      return parent.get(sliceIndex(row.step(), i, parent.rows()),
          sliceIndex(column.step(), j, parent.columns()));
    }

    @Override
    public Storage getStorage() {
      return parent.getStorage();
    }

    @Override
    public boolean get(int index) {
      int row = index % rows();
      int col = index / rows();
      return get(row, col);
    }
  }

  protected class FlatSliceBitMatrix extends AbstractBitMatrix {
    private final BitMatrix parent;
    private final Slice slice;

    public FlatSliceBitMatrix(BitMatrix parent, int size, Slice slice) {
      super(size);
      this.parent = checkNotNull(parent);
      this.slice = checkNotNull(slice);
    }

    public FlatSliceBitMatrix(BitMatrix parent, Slice slice) {
      this(parent, checkNotNull(slice).size(), slice);
    }

    @Override
    public void set(int i, int j, boolean value) {
      set(columnMajor(i, j, rows(), columns()), value);
    }

    @Override
    public void set(int index, boolean value) {
      parent.set(sliceIndex(slice.step(), index, parent.size()), value);
    }

    @Override
    public BitMatrix reshape(int rows, int columns) {
      return copy().reshape(rows, columns);
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public Storage getStorage() {
      return parent.getStorage();
    }

    @Override
    public BitMatrix newEmptyMatrix(int rows, int columns) {
      return parent.newEmptyMatrix(rows, columns);
    }

    @Override
    public boolean get(int i, int j) {
      return get(columnMajor(i, j, rows(), columns()));
    }

    @Override
    public boolean get(int index) {
      return parent.get(sliceIndex(slice.step(), index, parent.size()));
    }
  }
}
