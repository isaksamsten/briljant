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
import java.util.function.*;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Precision;
import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.function.DoubleBiPredicate;
import org.briljantframework.util.primitive.DoubleList;
import org.briljantframework.util.sort.QuickSort;

import net.mintern.primitive.comparators.DoubleComparator;

/**
 * This class provides a skeletal implementation of a double array.
 *
 * @author Isak Karlsson
 */
public abstract class AbstractDoubleArray extends AbstractBaseArray<DoubleArray>
    implements DoubleArray {

  protected AbstractDoubleArray(ArrayBackend bj, int[] shape) {
    super(bj, shape);
  }

  protected AbstractDoubleArray(ArrayBackend bj, int offset, int[] shape, int[] stride) {
    super(bj, offset, shape, stride);
  }

  @Override
  public void setFrom(int toIndex, DoubleArray from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void setFrom(int toRow, int toColumn, DoubleArray from, int fromRow, int fromColumn) {
    set(toRow, toColumn, from.get(fromRow, fromColumn));
  }

  @Override
  public void setFrom(int[] toIndex, DoubleArray from, int[] fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void setFrom(int[] toIndex, DoubleArray from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void setFrom(int toIndex, DoubleArray from, int[] fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public DoubleArray doubleArray() {
    return this;
  }

  @Override
  public IntArray intArray() {
    return new AsIntArray(getArrayBackend(), getOffset(), getShape(), getStride()) {
      @Override
      protected int getElement(int index) {
        return (int) AbstractDoubleArray.this.getElement(index);
      }

      @Override
      protected void setElement(int index, int value) {
        AbstractDoubleArray.this.setElement(index, value);
      }

      @Override
      protected int elementSize() {
        return AbstractDoubleArray.this.elementSize();
      }
    };
  }

  @Override
  public LongArray longArray() {
    return new AsLongArray(getArrayBackend(), getOffset(), getShape(), getStride()) {

      @Override
      public void setElement(int index, long value) {
        AbstractDoubleArray.this.set(index, (int) value);
      }

      @Override
      public long getElement(int index) {
        return (long) AbstractDoubleArray.this.getElement(index);
      }

      @Override
      protected int elementSize() {
        return AbstractDoubleArray.this.elementSize();
      }
    };
  }

  @Override
  public ComplexArray complexArray() {
    return new AsComplexArray(getArrayBackend(), getOffset(), getShape(), getStride()) {
      @Override
      public Complex getElement(int index) {
        return Complex.valueOf(AbstractDoubleArray.this.getElement(index));
      }

      @Override
      public void setElement(int index, Complex value) {
        AbstractDoubleArray.this.setElement(index, value.getReal());
      }

      @Override
      protected int elementSize() {
        return AbstractDoubleArray.this.elementSize();
      }
    };
  }

  @Override
  public DoubleArray copy() {
    DoubleArray n = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      n.set(i, get(i));
    }
    return n;
  }

  @Override
  public final void set(int index, double value) {
    Check.index(index, size());
    setElement(StrideUtils.index(index, getOffset(), stride, shape), value);
  }

  @Override
  public void assign(double value) {
    final int size = size();
    for (int i = 0; i < size; i++) {
      set(i, value);
    }
  }

  @Override
  public void assign(double[] array) {
    Check.dimension(this.size(), array.length);
    for (int i = 0; i < array.length; i++) {
      set(i, array[i]);
    }
  }

  @Override
  public void assign(DoubleSupplier supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.getAsDouble());
    }
  }

  @Override
  public void assign(DoubleArray other, DoubleUnaryOperator operator) {
    org.briljantframework.array.Arrays.broadcastWith(this, other, (a, b) -> {
      Check.size(a, b);
      for (int i = 0, size = a.size(); i < size; i++) {
        a.set(i, operator.applyAsDouble(b.get(i)));
      }
    });
  }

  @Override
  public void assign(IntArray other, IntToDoubleFunction function) {
    org.briljantframework.array.Arrays.broadcastWith(this, other, (a, b) -> {
      Check.size(a, b);
      for (int i = 0, size = a.size(); i < size; i++) {
        a.set(i, function.applyAsDouble(b.get(i)));
      }
    });
  }

  @Override
  public void assign(LongArray other, LongToDoubleFunction function) {
    org.briljantframework.array.Arrays.broadcastWith(this, other, (a, b) -> {
      Check.size(a, b);
      for (int i = 0, size = a.size(); i < size; i++) {
        a.set(i, function.applyAsDouble(b.get(i)));
      }
    });
  }

  @Override
  public void assign(ComplexArray other, ToDoubleFunction<? super Complex> function) {
    org.briljantframework.array.Arrays.broadcastWith(this, other, (a, b) -> {
      Check.size(a, b);
      for (int i = 0, size = a.size(); i < size; i++) {
        a.set(i, function.applyAsDouble(b.get(i)));
      }
    });
  }

  @Override
  public void combineAssign(DoubleArray other, DoubleBinaryOperator combine) {
    org.briljantframework.array.Arrays.broadcastWith(this, other, (a, b) -> {
      Check.size(a, b);
      for (int i = 0, size = a.size(); i < size; i++) {
        a.set(i, combine.applyAsDouble(a.get(i), b.get(i)));
      }
    });
  }

  @Override
  public DoubleArray combine(DoubleArray other, DoubleBinaryOperator combine) {
    return org.briljantframework.array.Arrays.broadcastCombine(this, other, (a, b) -> {
      DoubleArray out = newEmptyArray(a.getShape());
      for (int i = 0, size = a.size(); i < size; i++) {
        out.set(i, combine.applyAsDouble(a.get(i), b.get(i)));
      }
      return out;
    });
  }

  @Override
  public <R, C> R collect(Collector<? super Double, C, R> collector) {
    C accum = collector.supplier().get();
    for (int i = 0; i < size(); i++) {
      collector.accumulator().accept(accum, get(i));
    }
    return collector.finisher().apply(accum);
  }

  @Override
  public <T> T collect(Supplier<T> supplier, ObjDoubleConsumer<T> consumer) {
    T accumulator = supplier.get();
    for (int i = 0; i < size(); i++) {
      consumer.accept(accumulator, get(i));
    }
    return accumulator;
  }

  @Override
  public DoubleArray map(DoubleUnaryOperator operator) {
    DoubleArray mat = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      mat.set(i, operator.applyAsDouble(get(i)));
    }
    return mat;
  }

  @Override
  public IntArray mapToInt(DoubleToIntFunction function) {
    IntArray m = getArrayBackend().getArrayFactory().newIntArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, function.applyAsInt(get(i)));
    }
    return m;
  }

  @Override
  public LongArray mapToLong(DoubleToLongFunction function) {
    LongArray m = getArrayBackend().getArrayFactory().newLongArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, function.applyAsLong(get(i)));
    }
    return m;
  }

  @Override
  public ComplexArray mapToComplex(DoubleFunction<Complex> function) {
    ComplexArray m = getArrayBackend().getArrayFactory().newComplexArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, function.apply(get(i)));
    }
    return m;
  }

  @Override
  public <T> Array<T> mapToObj(DoubleFunction<? extends T> mapper) {
    Array<T> array = getArrayBackend().getArrayFactory().newArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, mapper.apply(get(i)));
    }
    return array;
  }

  @Override
  public void apply(DoubleUnaryOperator operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsDouble(get(i)));
    }
  }

  @Override
  public DoubleArray filter(DoublePredicate predicate) {
    DoubleList builder = new DoubleList();
    for (int i = 0; i < size(); i++) {
      double value = get(i);
      if (predicate.test(value)) {
        builder.add(value);
      }
    }
    return getArrayBackend().getArrayFactory()
        .newDoubleVector(Arrays.copyOf(builder.elementData, builder.size()));
  }

  @Override
  public BooleanArray where(DoubleArray other, DoubleBiPredicate predicate) {
    return org.briljantframework.array.Arrays.broadcastCombine(this, other, (a, b) -> {
      BooleanArray out = getArrayBackend().getArrayFactory().newBooleanArray(a.getShape());
      for (int i = 0, size = a.size(); i < size; i++) {
        out.set(i, predicate.test(a.get(i), b.get(i)));
      }
      return out;
    });
  }

  @Override
  public void forEachDouble(DoubleConsumer consumer) {
    for (int i = 0; i < size(); i++) {
      consumer.accept(get(i));
    }
  }

  @Override
  public double reduce(double identity, DoubleBinaryOperator reduce) {
    for (int i = 0; i < size(); i++) {
      identity = reduce.applyAsDouble(identity, get(i));
    }
    return identity;
  }

  @Override
  public DoubleArray reduceVectors(int dim, ToDoubleFunction<? super DoubleArray> reduce) {
    Check.argument(dim < dims(), INVALID_DIMENSION, dim, dims());
    DoubleArray reduced = newEmptyArray(ArrayUtils.remove(getShape(), dim));
    int vectors = vectors(dim);
    for (int i = 0; i < vectors; i++) {
      double value = reduce.applyAsDouble(getVector(dim, i));
      reduced.set(i, value);
    }
    return reduced;
  }

  @Override
  public final void set(int i, int j, double value) {
    Check.index(i, rows(), j, columns());
    setElement(getOffset() + i * stride(0) + j * stride(1), value);
  }

  @Override
  public final void set(int[] ix, double value) {
    Check.index(ix, shape);
    setElement(StrideUtils.index(ix, getOffset(), stride), value);
  }

  @Override
  public final double get(int index) {
    Check.index(index, size());
    return getElement(StrideUtils.index(index, getOffset(), stride, shape));
  }

  @Override
  public final double get(int i, int j) {
    Check.index(i, rows(), j, columns());
    return getElement(getOffset() + i * stride(0) + j * stride(1));
  }

  @Override
  public final double get(int... ix) {
    Check.index(ix, shape);
    return getElement(StrideUtils.index(ix, getOffset(), stride));
  }

  @Override
  public void set(BooleanArray array, double value) {
    Check.dimension(array, this);
    for (int i = 0; i < size(); i++) {
      set(i, array.get(i) ? value : get(i));
    }
  }

  @Override
  public DoubleArray get(BooleanArray array) {
    Check.dimension(array, this);
    double[] data = new double[size()];
    int idx = 0;
    for (int i = 0; i < size(); i++) {
      if (array.get(i)) {
        data[idx++] = get(i);
      }
    }
    return getArrayBackend().getArrayFactory().newDoubleVector(Arrays.copyOf(data, idx));
  }

  @Override
  public void sort(DoubleComparator cmp) {
    QuickSort.quickSort(0, size(), (left, right) -> cmp.compare(get(left), get(right)), this);
  }

  @Override
  public DoubleStream doubleStream() {
    PrimitiveIterator.OfDouble ofDouble = new PrimitiveIterator.OfDouble() {
      public int current = 0;

      @Override
      public double nextDouble() {
        return get(current++);
      }

      @Override
      public boolean hasNext() {
        return current < size();
      }
    };

    Spliterator.OfDouble spliterator =
        Spliterators.spliterator(ofDouble, size(), Spliterator.SIZED);
    return StreamSupport.doubleStream(spliterator, false);
  }

  @Override
  public List<Double> asList() {
    return new DoubleListView();
  }

  @Override
  public Array<Double> asArray() {
    return new AsArray<Double>(this) {

      @Override
      protected void setElement(int i, Double value) {
        AbstractDoubleArray.this.setElement(i, value);
      }

      @Override
      protected Double getElement(int i) {
        return AbstractDoubleArray.this.getElement(i);
      }

      @Override
      protected int elementSize() {
        return AbstractDoubleArray.this.elementSize();
      }
    };
  }

  @Override
  public DoubleArray negate() {
    DoubleArray n = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      n.set(i, -get(i));
    }
    return n;
  }

  @Override
  public BooleanArray where(DoublePredicate predicate) {
    BooleanArray bits = getArrayBackend().getArrayFactory().newBooleanArray(getShape());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i)));
    }
    return bits;
  }

  protected abstract double getElement(int i);

  protected abstract void setElement(int i, double value);

  @Override
  public int hashCode() {
    int result = 1;
    for (int i = 0; i < size(); i++) {
      long bits = Double.doubleToLongBits(get(i));
      result = 31 * result + (int) (bits ^ (bits >>> 32));
    }

    return Objects.hash(getShape(), getStride(), result);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof DoubleArray) {
      DoubleArray mat = (DoubleArray) obj;
      if (!Arrays.equals(shape, mat.getShape())) {
        return false;
      }

      for (int i = 0; i < size(); i++) {
        if (!Precision.equalsIncludingNaN(get(i), mat.get(i))) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    return ArrayPrinter.toString(this);
  }

  @Override
  public void swap(int a, int b) {
    double tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  @Override
  public Iterator<Double> iterator() {
    return asList().iterator();
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public boolean contains(Object o) {
    return asList().contains(o);
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
  public <T> T[] toArray(T[] a) {
    return asList().toArray(a);
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
  public final boolean add(Double integer) {
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
  public final boolean addAll(Collection<? extends Double> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public final boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  private class DoubleListView extends AbstractList<Double> {

    @Override
    public Double get(int i) {
      return AbstractDoubleArray.this.get(i);
    }

    @Override
    public Double set(int i, Double value) {
      Double old = AbstractDoubleArray.this.get(i);
      AbstractDoubleArray.this.set(i, value);
      return old;
    }

    @Override
    public Iterator<Double> iterator() {
      return new Iterator<Double>() {
        private int index = 0;

        @Override
        public boolean hasNext() {
          return index < size();
        }

        @Override
        public Double next() {
          return get(index++);
        }
      };
    }

    @Override
    public int size() {
      return AbstractDoubleArray.this.size();
    }
  }



}
