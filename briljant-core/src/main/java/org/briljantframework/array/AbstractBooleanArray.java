/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.array;

import java.util.*;
import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayBackend;

/**
 * This class provides a skeletal implementation of a boolean array.
 *
 * @author Isak Karlsson
 */
public abstract class AbstractBooleanArray extends AbstractBaseArray<BooleanArray>
    implements BooleanArray {

  protected AbstractBooleanArray(ArrayBackend backend, int size) {
    super(backend, new int[] {size});
  }

  public AbstractBooleanArray(ArrayBackend backend, int[] shape) {
    super(backend, shape);
  }

  public AbstractBooleanArray(ArrayBackend backend, int offset, int[] shape, int[] stride) {
    super(backend, offset, shape, stride);
  }

  @Override
  public void setFrom(int toIndex, BooleanArray from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void setFrom(int toRow, int toColumn, BooleanArray from, int fromRow, int fromColumn) {
    set(toRow, toColumn, from.get(fromRow, fromColumn));
  }

  @Override
  public void setFrom(int[] toIndex, BooleanArray from, int[] fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void setFrom(int[] toIndex, BooleanArray from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void setFrom(int toIndex, BooleanArray from, int[] fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public DoubleArray doubleArray() {
    return new AsDoubleArray(getArrayBackend(), getOffset(), getShape(), getStride()) {
      @Override
      public double getElement(int index) {
        return AbstractBooleanArray.this.getElement(index) ? 1 : 0;
      }

      @Override
      public void setElement(int index, double value) {
        AbstractBooleanArray.this.setElement(index, value == 1);
      }

      @Override
      protected int elementSize() {
        return AbstractBooleanArray.this.elementSize();
      }
    };
  }

  @Override
  public IntArray intArray() {
    return new AsIntArray(getArrayBackend(), getOffset(), getShape(), getStride()) {

      @Override
      public int getElement(int index) {
        return AbstractBooleanArray.this.getElement(index) ? 1 : 0;

      }

      @Override
      public void setElement(int index, int value) {
        AbstractBooleanArray.this.setElement(index, value == 1);
      }

      @Override
      protected int elementSize() {
        return AbstractBooleanArray.this.elementSize();
      }
    };
  }

  @Override
  public LongArray longArray() {
    return new AsLongArray(getArrayBackend(), getOffset(), getShape(), getStride()) {

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
  public BooleanArray booleanArray() {
    return this;
  }

  @Override
  public ComplexArray asComplexArray() {
    return new AsComplexArray(getArrayBackend(), getOffset(), getShape(), getStride()) {
      @Override
      public Complex getElement(int index) {
        return AbstractBooleanArray.this.getElement(index) ? Complex.ONE : Complex.ZERO;
      }

      @Override
      public void setElement(int index, Complex value) {
        AbstractBooleanArray.this.setElement(index, value.equals(Complex.ONE));
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
  public void assign(boolean value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
  }

  @Override
  public void set(int index, boolean value) {
    setElement(StrideUtils.index(index, getOffset(), stride, shape), value);
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
    setElement(StrideUtils.index(ix, getOffset(), getStride()), value);
  }

  @Override
  public boolean get(int index) {
    return getElement(StrideUtils.index(index, getOffset(), stride, shape));
  }

  @Override
  public boolean get(int i, int j) {
    Check.state(isMatrix(), REQUIRE_2D);
    return getElement(getOffset() + i * stride(0) + j * stride(1));
  }

  public final boolean get(int... ix) {
    Check.state(ix.length == dims(), REQUIRE_ND, dims());
    return getElement(StrideUtils.index(ix, getOffset(), getStride()));
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
    BooleanArray out = newEmptyArray(ArrayUtils.remove(getShape(), dim));
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
    return new AsArray<Boolean>(this) {

      @Override
      protected void setElement(int i, Boolean value) {
        AbstractBooleanArray.this.setElement(i, value);
      }

      @Override
      protected Boolean getElement(int i) {
        return AbstractBooleanArray.this.getElement(i);
      }

      @Override
      protected int elementSize() {
        return AbstractBooleanArray.this.elementSize();
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

  // public List<Boolean> asList() {
  // return new AbstractList<Boolean>() {
  // @Override
  // public int size() {
  // return AbstractBooleanArray.this.size();
  // }
  //
  // @Override
  // public Boolean get(int index) {
  // return AbstractBooleanArray.this.get(index);
  // }
  //
  // @Override
  // public Boolean set(int index, Boolean element) {
  // Boolean old = get(index);
  // AbstractBooleanArray.this.set(index, element);
  // return old;
  // }
  //
  //
  // };
  // }

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
    return ArrayPrinter.toString(this);
  }

  @Override
  public void swap(int a, int b) {
    boolean tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  @Override
  public Iterator<Boolean> iterator() {
    return new Iterator<Boolean>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public Boolean next() {
        if (current >= size()) {
          throw new NoSuchElementException();
        }
        return get(current++);
      }
    };
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public boolean contains(Object o) {
    if (!(o instanceof Integer)) {
      return false;
    }
    for (int i = 0; i < size(); i++) {
      if (o.equals(get(i))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public Object[] toArray() {
    Object[] data = new Object[size()];
    for (int i = 0; i < size(); i++) {
      data[i] = get(i);
    }
    return data;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> T[] toArray(T[] a) {
    T[] r = a.length >= size() ? a
        : (T[]) java.lang.reflect.Array.newInstance(a.getClass().getComponentType(), size());
    for (int i = 0; i < size(); i++) {
      r[i] = (T) Boolean.valueOf(get(i));
    }
    return r;
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean add(Boolean integer) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean containsAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean addAll(Collection<? extends Boolean> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }
}
