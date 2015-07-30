package org.briljantframework.array;

import com.carrotsearch.hppc.IntArrayList;

import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.complex.Complex;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
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
public abstract class AbstractBitArray extends AbstractBaseArray<BitArray> implements BitArray {

  protected AbstractBitArray(ArrayFactory bj, int size) {
    super(bj, new int[]{size});
  }

  public AbstractBitArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
  }

  public AbstractBitArray(ArrayFactory bj, int offset, int[] shape, int[] stride,
                          int majorStride) {
    super(bj, offset, shape, stride, majorStride);
  }

  @Override
  public void set(int toIndex, BitArray from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void set(int toRow, int toColumn, BitArray from, int fromRow, int fromColumn) {
    set(toRow, toColumn, from.get(fromRow, fromColumn));
  }

  @Override
  public void set(int[] toIndex, BitArray from, int[] fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  public final void set(int[] ix, boolean value) {
    Check.argument(ix.length == dims());
    setElement(Indexer.columnMajorStride(ix, getOffset(), getStride()), value);
  }

  public final boolean get(int... ix) {
    Check.argument(ix.length == dims());
    return getElement(Indexer.columnMajorStride(ix, getOffset(), getStride()));
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
    setElement(Indexer.linearized(index, getOffset(), stride, shape), value);
  }

  @Override
  public boolean get(int index) {
    return getElement(Indexer.linearized(index, getOffset(), stride, shape));
  }

  protected abstract void setElement(int i, boolean value);

  protected abstract boolean getElement(int i);

  @Override
  public int compare(int a, int b) {
    return Boolean.compare(get(a), get(b));
  }

  @Override
  public void setRow(int index, BitArray vec) {
    for (int j = 0; j < columns(); j++) {
      set(index, j, vec.get(j));
    }
  }

  @Override
  public void setColumn(int index, BitArray vec) {
    Check.size(rows(), vec.size());
    for (int i = 0; i < rows(); i++) {
      set(i, index, vec.get(i));
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
    BitArray bits = getArrayFactory().booleanArray(getShape());
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
    return asInt().add(o.asInt()).asBit().copy();
  }

  @Override
  public BitArray sub(BitArray o) {
    return asInt().sub(o.asInt()).asBit().copy();
  }

  @Override
  public BitArray mul(BitArray o) {
    return asInt().mul(o.asInt()).asBit().copy();
  }

  @Override
  public BitArray div(BitArray o) {
    return asInt().div(o.asInt()).asBit().copy();
  }

  @Override
  public BitArray mmul(BitArray o) {
    return asInt().mmul(o.asInt()).asBit().copy();
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
    int value = Objects.hash(getShape(), getStride());
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
      if (!Arrays.equals(shape, o.getShape())) {
        return false;
      }
      for (int i = 0; i < size(); i++) {
        if (get(i) != o.get(i)) {
          return false;
        }
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
  public DoubleArray asDouble() {
    return new AsDoubleArray(
        getArrayFactory(), getOffset(), getShape(), getStride(), getMajorStrideIndex()) {
      @Override
      public void setElement(int index, double value) {
        AbstractBitArray.this.setElement(index, value == 1);
      }

      @Override
      public double getElement(int index) {
        return AbstractBitArray.this.getElement(index) ? 1 : 0;
      }

      @Override
      protected int elementSize() {
        return AbstractBitArray.this.elementSize();
      }
    };
  }

  @Override
  public IntArray asInt() {
    return new AsIntArray(
        getArrayFactory(), getOffset(), getShape(), getStride(), getMajorStrideIndex()) {

      @Override
      public int getElement(int index) {
        return AbstractBitArray.this.getElement(index) ? 1 : 0;

      }

      @Override
      public void setElement(int index, int value) {
        AbstractBitArray.this.setElement(index, value == 1);
      }

      @Override
      protected int elementSize() {
        return AbstractBitArray.this.elementSize();
      }
    };
  }

  @Override
  public LongArray asLong() {
    return new AsLongArray(
        getArrayFactory(), getOffset(), getShape(), getStride(), getMajorStrideIndex()) {

      @Override
      public long getElement(int index) {
        return AbstractBitArray.this.getElement(index) ? 1 : 0;

      }

      @Override
      public void setElement(int index, long value) {
        AbstractBitArray.this.setElement(index, value == 1);
      }

      @Override
      protected int elementSize() {
        return AbstractBitArray.this.elementSize();
      }
    };
  }

  @Override
  public ComplexArray asComplex() {
    return new AsComplexArray(
        getArrayFactory(), getOffset(), getShape(), getStride(), getMajorStrideIndex()) {
      @Override
      public void setElement(int index, Complex value) {
        AbstractBitArray.this.setElement(index, value.equals(Complex.ONE));
      }

      @Override
      public Complex getElement(int index) {
        return AbstractBitArray.this.getElement(index) ? Complex.ONE : Complex.ZERO;
      }

      @Override
      protected int elementSize() {
        return AbstractBitArray.this.elementSize();
      }
    };
  }

  @Override
  public BitArray asBit() {
    return this;
  }

  @Override
  public BitArray slice(BitArray bits) {
    Check.shape(this, bits);
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
    BitArray n = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      n.set(i, get(i));
    }
    return n;
  }

  @Override
  public BitArray xor(BitArray other) {
    Check.shape(this, other);
    BitArray bm = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      boolean otherHas = other.get(i);
      boolean thisHas = get(i);
      bm.set(i, (thisHas || otherHas) && !(thisHas && otherHas));
    }
    return bm;
  }

  @Override
  public BitArray or(BitArray other) {
    Check.shape(this, other);
    BitArray bm = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) || other.get(i));
    }
    return bm;
  }

  @Override
  public BitArray orNot(BitArray other) {
    Check.shape(this, other);
    BitArray bm = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) || !other.get(i));
    }
    return bm;
  }

  @Override
  public BitArray and(BitArray other) {
    Check.shape(this, other);
    BitArray bm = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) && other.get(i));
    }
    return bm;
  }

  @Override
  public BitArray andNot(BitArray other) {
    Check.shape(this, other);
    BitArray bm = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) && !other.get(i));
    }
    return bm;
  }

  @Override
  public BitArray not() {
    BitArray bm = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, !get(i));
    }
    return bm;
  }


}
