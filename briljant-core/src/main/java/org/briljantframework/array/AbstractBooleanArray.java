/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.briljantframework.array;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.primitive.ArrayAllocations;

/**
 * This class provides a skeletal implementation of a boolean array.
 *
 * @author Isak Karlsson
 */
public abstract class AbstractBooleanArray extends AbstractBaseArray<BooleanArray> implements
    BooleanArray {

  protected AbstractBooleanArray(ArrayFactory bj, int size) {
    super(bj, new int[] {size});
  }

  public AbstractBooleanArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
  }

  public AbstractBooleanArray(ArrayFactory bj, int offset, int[] shape, int[] stride,
      int majorStride) {
    super(bj, offset, shape, stride, majorStride);
  }

  @Override
  public void set(int toIndex, BooleanArray from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void set(int toRow, int toColumn, BooleanArray from, int fromRow, int fromColumn) {
    set(toRow, toColumn, from.get(fromRow, fromColumn));
  }

  @Override
  public void set(int[] toIndex, BooleanArray from, int[] fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public int compare(int a, int b) {
    return Boolean.compare(get(a), get(b));
  }

  @Override
  public BooleanArray slice(BooleanArray indicator) {
    Check.dimension(this, indicator);
    IncrementalBuilder builder = new IncrementalBuilder();
    for (int i = 0; i < size(); i++) {
      if (indicator.get(i)) {
        builder.add(get(i));
      }
    }
    return builder.build();
  }

  @Override
  public DoubleArray asDouble() {
    return new AsDoubleArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {
      @Override
      public void setElement(int index, double value) {
        AbstractBooleanArray.this.setElement(index, value == 1);
      }

      @Override
      public double getElement(int index) {
        return AbstractBooleanArray.this.getElement(index) ? 1 : 0;
      }

      @Override
      protected int elementSize() {
        return AbstractBooleanArray.this.elementSize();
      }
    };
  }

  @Override
  public IntArray asInt() {
    return new AsIntArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {

      @Override
      public void setElement(int index, int value) {
        AbstractBooleanArray.this.setElement(index, value == 1);
      }

      @Override
      public int getElement(int index) {
        return AbstractBooleanArray.this.getElement(index) ? 1 : 0;

      }

      @Override
      protected int elementSize() {
        return AbstractBooleanArray.this.elementSize();
      }
    };
  }

  @Override
  public LongArray asLong() {
    return new AsLongArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {

      @Override
      public void setElement(int index, long value) {
        AbstractBooleanArray.this.setElement(index, value == 1);
      }

      @Override
      public long getElement(int index) {
        return AbstractBooleanArray.this.getElement(index) ? 1 : 0;

      }

      @Override
      protected int elementSize() {
        return AbstractBooleanArray.this.elementSize();
      }
    };
  }

  @Override
  public BooleanArray asBoolean() {
    return this;
  }

  @Override
  public ComplexArray asComplex() {
    return new AsComplexArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {
      @Override
      public void setElement(int index, Complex value) {
        AbstractBooleanArray.this.setElement(index, value.equals(Complex.ONE));
      }

      @Override
      public Complex getElement(int index) {
        return AbstractBooleanArray.this.getElement(index) ? Complex.ONE : Complex.ZERO;
      }

      @Override
      protected int elementSize() {
        return AbstractBooleanArray.this.elementSize();
      }
    };
  }

  @Override
  public BooleanArray copy() {
    BooleanArray n = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      n.set(i, get(i));
    }
    return n;
  }

  @Override
  public BooleanArray lt(BooleanArray other) {
    return eq(other);
  }

  @Override
  public BooleanArray gt(BooleanArray other) {
    return eq(other);
  }

  @Override
  public BooleanArray eq(BooleanArray other) {
    BooleanArray bits = getArrayFactory().newBooleanArray(getShape());
    for (int i = 0; i < size(); i++) {
      bits.set(i, get(i) == other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray lte(BooleanArray other) {
    return eq(other);
  }

  @Override
  public BooleanArray gte(BooleanArray other) {
    return eq(other);
  }

  @Override
  public void assign(boolean value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
  }

  @Override
  public void set(int index, boolean value) {
    setElement(Indexer.linearized(index, getOffset(), stride, shape), value);
  }

  @Override
  public void assign(Supplier<Boolean> supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.get());
    }
  }

  @Override
  public void set(int i, int j, boolean value) {
    Check.argument(isMatrix(), REQUIRE_2D);
    setElement(getOffset() + i * stride(0) + j * stride(1), value);
  }

  public final void set(int[] ix, boolean value) {
    Check.argument(ix.length == dims(), REQUIRE_ND, dims());
    setElement(Indexer.columnMajorStride(ix, getOffset(), getStride()), value);
  }

  @Override
  public boolean get(int index) {
    return getElement(Indexer.linearized(index, getOffset(), stride, shape));
  }

  @Override
  public boolean get(int i, int j) {
    Check.argument(isMatrix(), REQUIRE_2D);
    return getElement(getOffset() + i * stride(0) + j * stride(1));
  }

  public final boolean get(int... ix) {
    Check.argument(ix.length == dims(), REQUIRE_ND, dims());
    return getElement(Indexer.columnMajorStride(ix, getOffset(), getStride()));
  }

  @Override
  public BooleanArray map(Function<Boolean, Boolean> mapper) {
    BooleanArray empty = newEmptyArray(getShape());
    for (int i = 0, size = size(); i < size; i++) {
      empty.set(i, mapper.apply(get(i)));
    }
    return empty;
  }

  @Override
  public void apply(UnaryOperator<Boolean> operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(get(i)));
    }
  }

  @Override
  public BooleanArray xor(BooleanArray other) {
    Check.dimension(this, other);
    BooleanArray bm = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      boolean otherHas = other.get(i);
      boolean thisHas = get(i);
      bm.set(i, (thisHas || otherHas) && !(thisHas && otherHas));
    }
    return bm;
  }

  @Override
  public BooleanArray or(BooleanArray other) {
    Check.dimension(this, other);
    BooleanArray bm = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) || other.get(i));
    }
    return bm;
  }

  @Override
  public BooleanArray orNot(BooleanArray other) {
    Check.dimension(this, other);
    BooleanArray bm = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) || !other.get(i));
    }
    return bm;
  }

  @Override
  public BooleanArray and(BooleanArray other) {
    Check.dimension(this, other);
    BooleanArray bm = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) && other.get(i));
    }
    return bm;
  }

  @Override
  public BooleanArray andNot(BooleanArray other) {
    Check.dimension(this, other);
    BooleanArray bm = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) && !other.get(i));
    }
    return bm;
  }

  @Override
  public BooleanArray not() {
    BooleanArray bm = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      bm.set(i, !get(i));
    }
    return bm;
  }

  @Override
  public boolean reduce(boolean identity, BinaryOperator<Boolean> accumulator) {
    return stream().reduce(identity, accumulator);
  }

  @Override
  public BooleanArray reduceAlong(int dim, Function<? super BooleanArray, Boolean> function) {
    Check.argument(0 <= dim && dim < dims(), INVALID_DIMENSION, dim, dims());
    BooleanArray out = newEmptyArray(Indexer.remove(getShape(), dim));
    int vectors = vectors(dim);
    for (int i = 0; i < vectors; i++) {
      out.set(i, function.apply(getVector(dim, i)));
    }
    return out;
  }

  @Override
  public BooleanArray any(int dim) {
    return reduceAlong(dim, BooleanArray::any);
  }

  @Override
  public boolean any() {
    for (int i = 0; i < size(); i++) {
      if (get(i)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public BooleanArray all(int dim) {
    return reduceAlong(dim, BooleanArray::all);
  }

  @Override
  public boolean all() {
    for (int i = 0; i < size(); i++) {
      if (!get(i)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public Array<Boolean> boxed() {
    return new AsArray<Boolean>(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStride()) {
      @Override
      public BooleanArray asBoolean() {
        return AbstractBooleanArray.this;
      }      @Override
      protected int elementSize() {
        return AbstractBooleanArray.this.elementSize();
      }

      @Override
      protected void setElement(int i, Boolean value) {
        AbstractBooleanArray.this.setElement(i, value);
      }

      @Override
      protected Boolean getElement(int i) {
        return AbstractBooleanArray.this.getElement(i);
      }


    };
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
  public List<Boolean> toList() {
    return new AbstractList<Boolean>() {
      @Override
      public int size() {
        return AbstractBooleanArray.this.size();
      }

      @Override
      public Boolean get(int index) {
        return AbstractBooleanArray.this.get(index);
      }

      @Override
      public Boolean set(int index, Boolean element) {
        Boolean old = get(index);
        AbstractBooleanArray.this.set(index, element);
        return old;
      }


    };
  }

  protected abstract boolean getElement(int i);

  protected abstract void setElement(int i, boolean value);

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
    if (obj instanceof BooleanArray) {
      BooleanArray o = (BooleanArray) obj;
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

  @Override
  public Iterator<Boolean> iterator() {
    return toList().iterator();
  }

  public class IncrementalBuilder {

    private boolean[] buffer = new boolean[10];
    private int size = 0;

    public void add(boolean a) {
      buffer = ArrayAllocations.ensureCapacity(buffer, size);
      buffer[size++] = a;
    }

    public BooleanArray build() {
      return factory.newVector(Arrays.copyOf(buffer, size));
    }
  }


}
