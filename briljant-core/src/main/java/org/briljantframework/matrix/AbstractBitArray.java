package org.briljantframework.matrix;

import com.google.common.collect.UnmodifiableIterator;

import com.carrotsearch.hppc.IntArrayList;

import org.briljantframework.Check;
import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.api.ArrayFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractBitArray extends AbstractArray<BitArray> implements BitArray {

  protected AbstractBitArray(ArrayFactory bj, int size) {
    super(bj, size);
  }

  public AbstractBitArray(ArrayFactory bj, int... shape) {
    super(bj, shape);
  }

  public AbstractBitArray(ArrayFactory bj, int offset, int[] shape, int[] stride) {
    super(bj, offset, shape, stride);
  }

  @Override
  public BitArray assign(BitArray o) {
    Check.equalShape(this, o);
    for (int i = 0; i < size(); i++) {
      set(i, o.get(i));
    }
    return this;
  }

  @Override
  public void set(int toIndex, BitArray from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void set(int toRow, int toColumn, BitArray from, int fromRow, int fromColumn) {
    set(toRow, toColumn, from.get(fromRow, fromColumn));
  }

  public final void set(int[] ix, boolean value) {
    Check.argument(ix.length == dims());
    setElement(Indexer.columnMajorStride(getOffset(), ix, getStride()), value);
  }

  public final boolean get(int... ix) {
    Check.argument(ix.length == dims());
    return getElement(Indexer.columnMajorStride(getOffset(), ix, getStride()));
  }

  @Override
  public void set(int i, int j, boolean value) {
    Check.argument(isMatrix());
    setElement(getOffset() + i * stride(0) + j * stride(1), value);
  }

  @Override
  public boolean get(int i, int j) {
    Check.argument(isMatrix());
    return getElement(getOffset() + i * stride(0) + j * stride(1));
  }

  @Override
  public void set(int index, boolean value) {
    setElement(index * stride(0) + getOffset(), value);
  }

  @Override
  public boolean get(int index) {
    return getElement(index * stride(0) + getOffset());
  }

  protected abstract void setElement(int i, boolean value);

  protected abstract boolean getElement(int i);

  @Override
  public int compare(int a, int b) {
    return Boolean.compare(get(a), get(b));
  }

  @Override
  public void setRow(int index, BitArray row) {
    Check.size(columns(), row);
    for (int j = 0; j < columns(); j++) {
      set(index, j, row.get(j));
    }
  }

  @Override
  public void setColumn(int index, BitArray column) {
    Check.size(rows(), column.size());
    for (int i = 0; i < rows(); i++) {
      set(i, index, column.get(i));
    }
  }

  @Override
  public BitArray assign(Supplier<Boolean> supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.get());
    }
    return this;
  }

  @Override
  public BitArray lt(BitArray other) {
    return eq(other);
  }

  @Override
  public BitArray gt(BitArray other) {
    return eq(other);
  }

  @Override
  public BitArray eq(BitArray other) {
    BitArray bits = getMatrixFactory().booleanArray(getShape().clone());
    for (int i = 0; i < size(); i++) {
      bits.set(i, get(i) == other.get(i));
    }
    return bits;
  }

  @Override
  public BitArray lte(BitArray other) {
    return eq(other);
  }

  @Override
  public BitArray gte(BitArray other) {
    return eq(other);
  }

  @Override
  public BitArray add(BitArray o) {
    return asIntMatrix().add(o.asIntMatrix()).asBitMatrix().copy();
  }

  @Override
  public BitArray sub(BitArray o) {
    return asIntMatrix().sub(o.asIntMatrix()).asBitMatrix().copy();
  }

  @Override
  public BitArray mul(BitArray o) {
    return asIntMatrix().mul(o.asIntMatrix()).asBitMatrix().copy();
  }

  @Override
  public BitArray div(BitArray o) {
    return asIntMatrix().div(o.asIntMatrix()).asBitMatrix().copy();
  }

  @Override
  public BitArray mmul(BitArray o) {
    return asIntMatrix().mmul(o.asIntMatrix()).asBitMatrix().copy();
  }

  @Override
  public BitArray assign(boolean value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
    return this;
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
    if (obj instanceof BitArray) {
      BitArray o = (BitArray) obj;
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
    StringBuilder a = new StringBuilder();
    try {
      ArrayPrinter.print(a, this);
    } catch (IOException e) {
      return getClass().getSimpleName();
    }
    return a.toString();
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

  public class IncrementalBuilder {

    private IntArrayList buffer = new IntArrayList();

    public void add(boolean a) {
      buffer.add(a ? 1 : 0);
    }

    public BitArray build() {
      BitArray n = newEmptyArray(buffer.size(), 1);
      for (int i = 0; i < buffer.size(); i++) {
        n.set(i, buffer.get(i) == 1);
      }
      return n;
    }
  }

  @Override
  public DoubleArray asDoubleMatrix() {
    return new AsDoubleArray(
        getMatrixFactory(), getOffset(), getShape().clone(), getStride().clone()) {
      @Override
      public void setElement(int index, double value) {
        AbstractBitArray.this.setElement(index, value == 1);
      }

      @Override
      public double getElement(int index) {
        return AbstractBitArray.this.getElement(index) ? 1 : 0;
      }
    };
  }

  @Override
  public IntArray asIntMatrix() {
    return new AsIntArray(
        getMatrixFactory(), getOffset(), getShape().clone(), getStride().clone()) {

      @Override
      public int getElement(int index) {
        return AbstractBitArray.this.getElement(index) ? 1 : 0;

      }

      @Override
      public void setElement(int index, int value) {
        AbstractBitArray.this.setElement(index, value == 1);
      }
    };
  }

  @Override
  public LongArray asLongMatrix() {
    return new AsLongArray(
        getMatrixFactory(), getOffset(), getShape().clone(), getStride().clone()) {

      @Override
      public long getElement(int index) {
        return AbstractBitArray.this.getElement(index) ? 1 : 0;

      }

      @Override
      public void setElement(int index, long value) {
        AbstractBitArray.this.setElement(index, value == 1);
      }
    };
  }

  @Override
  public ComplexArray asComplexMatrix() {
    return new AsComplexArray(
        getMatrixFactory(), getOffset(), getShape().clone(), getStride().clone()) {
      @Override
      public void setElement(int index, Complex value) {
        AbstractBitArray.this.setElement(index, value.equals(Complex.ONE));
      }

      @Override
      public Complex getElement(int index) {
        return AbstractBitArray.this.getElement(index) ? Complex.ONE : Complex.ZERO;
      }
    };
  }

  private class BitListView extends AbstractList<Boolean> {

    @Override
    public Boolean get(int i) {
      return AbstractBitArray.this.get(i);
    }

    @Override
    public Boolean set(int i, Boolean value) {
      boolean old = AbstractBitArray.this.get(i);
      AbstractBitArray.this.set(i, value);
      return old;
    }

    @Override
    public int size() {
      return AbstractBitArray.this.size();
    }
  }

  @Override
  public BitArray asBitMatrix() {
    return this;
  }

  @Override
  public BitArray slice(Range rows, Range columns) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BitArray slice(Range range) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BitArray slice(Collection<Integer> rows, Collection<Integer> columns) {
    BitArray m = newEmptyArray(rows.size(), columns.size());
    int i = 0;
    for (int row : rows) {
      int j = 0;
      for (int column : columns) {
        m.set(i, j++, get(row, column));
      }
      i++;
    }
    return m;
  }

  @Override
  public BitArray slice(Collection<Integer> indexes) {
    IncrementalBuilder builder = new IncrementalBuilder();
    indexes.forEach(index -> builder.add(get(index)));
    return builder.build();
  }

  @Override
  public BitArray slice(BitArray bits) {
    Check.equalShape(this, bits);
    IncrementalBuilder builder = new IncrementalBuilder();
    for (int i = 0; i < size(); i++) {
      if (bits.get(i)) {
        builder.add(get(i));
      }
    }
    return builder.build();
  }

  @Override
  public Stream<Boolean> stream() {
    return StreamSupport.stream(Spliterators.spliterator(new Iterator<Boolean>() {
      int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public Boolean next() {
        return get(current++);
      }
    }, size(), Spliterator.SIZED), false);
  }

  @Override
  public List<Boolean> asList() {
    return new AbstractList<Boolean>() {
      @NotNull
      @Override
      public Boolean get(int index) {
        return AbstractBitArray.this.get(index);
      }

      @Override
      public Boolean set(int index, Boolean element) {
        Boolean old = get(index);
        AbstractBitArray.this.set(index, element);
        return old;
      }

      @Override
      public int size() {
        return AbstractBitArray.this.size();
      }
    };
  }

  @Override
  public BitArray copy() {
    BitArray n = newEmptyArray(rows(), columns());
    for (int i = 0; i < size(); i++) {
      n.set(i, get(i));
    }
    return n;
  }

  @Override
  public BitArray xor(BitArray other) {
    Check.equalShape(this, other);
    BitArray bm = newEmptyArray(rows(), columns());
    for (int i = 0; i < size(); i++) {
      boolean otherHas = other.get(i);
      boolean thisHas = get(i);
      bm.set(i, (thisHas || otherHas) && !(thisHas && otherHas));
    }
    return bm;
  }

  @Override
  public BitArray or(BitArray other) {
    Check.equalShape(this, other);
    BitArray bm = newEmptyArray(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) || other.get(i));
    }
    return bm;
  }

  @Override
  public BitArray orNot(BitArray other) {
    Check.equalShape(this, other);
    BitArray bm = newEmptyArray(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) || !other.get(i));
    }
    return bm;
  }

  @Override
  public BitArray and(BitArray other) {
    Check.equalShape(this, other);
    BitArray bm = newEmptyArray(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) && other.get(i));
    }
    return bm;
  }

  @Override
  public BitArray andNot(BitArray other) {
    Check.equalShape(this, other);
    BitArray bm = newEmptyArray(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) && !other.get(i));
    }
    return bm;
  }

  @Override
  public BitArray not() {
    BitArray bm = newEmptyArray(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, !get(i));
    }
    return bm;
  }


}
