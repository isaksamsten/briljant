/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
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

import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntToDoubleFunction;
import java.util.function.LongToDoubleFunction;
import java.util.function.ObjDoubleConsumer;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;

import net.mintern.primitive.comparators.DoubleComparator;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.util.Precision;
import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.function.DoubleBiPredicate;
import org.briljantframework.primitive.ArrayAllocations;
import org.briljantframework.primitive.DoubleList;
import org.briljantframework.sort.QuickSort;

/**
 * This class provides a skeletal implementation of a double array.
 *
 * @author Isak Karlsson
 */
public abstract class AbstractDoubleArray extends AbstractBaseArray<DoubleArray> implements
    DoubleArray {

  protected AbstractDoubleArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
  }

  protected AbstractDoubleArray(ArrayFactory bj, int offset, int[] shape, int[] stride,
      int majorStride) {
    super(bj, offset, shape, stride, majorStride);
  }

  @Override
  public void set(int toIndex, DoubleArray from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void set(int toRow, int toColumn, DoubleArray from, int fromRow, int fromColumn) {
    set(toRow, toColumn, from.get(fromRow, fromColumn));
  }

  @Override
  public void set(int[] toIndex, DoubleArray from, int[] fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public int compare(int a, int b) {
    return Double.compare(get(a), get(b));
  }

  @Override
  public DoubleArray slice(BooleanArray indicator) {
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
    return this;
  }

  @Override
  public IntArray asInt() {
    return new AsIntArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {
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
  public LongArray asLong() {
    return new AsLongArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {

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
  public BooleanArray asBoolean() {
    return new AsBooleanArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {

      @Override
      protected boolean getElement(int index) {
        return AbstractDoubleArray.this.get(index) == 1;
      }

      @Override
      protected void setElement(int index, boolean value) {
        AbstractDoubleArray.this.setElement(index, value ? 1 : 0);
      }

      @Override
      protected int elementSize() {
        return AbstractDoubleArray.this.elementSize();
      }
    };
  }

  @Override
  public ComplexArray asComplex() {
    return new AsComplexArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {
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
  public BooleanArray lt(DoubleArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) < other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray gt(DoubleArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) > other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray eq(DoubleArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) == other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray lte(DoubleArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) <= other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray gte(DoubleArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) >= other.get(i));
    }
    return bits;
  }

  @Override
  public final void set(int index, double value) {
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
  public void assign(DoubleArray matrix, DoubleUnaryOperator operator) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsDouble(matrix.get(i)));
    }
  }

  @Override
  public void assign(IntArray matrix, IntToDoubleFunction function) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsDouble(matrix.get(i)));
    }
  }

  @Override
  public void assign(LongArray matrix, LongToDoubleFunction function) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsDouble(matrix.get(i)));
    }
  }

  @Override
  public void assign(ComplexArray other, ToDoubleFunction<? super Complex> function) {
    Check.size(this, other);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsDouble(other.get(i)));
    }
  }

  @Override
  public void combineAssign(DoubleArray array, DoubleBinaryOperator combine) {
    Check.size(this, array);
    for (int i = 0; i < size(); i++) {
      set(i, combine.applyAsDouble(get(i), array.get(i)));
    }
  }

  @Override
  public DoubleArray combine(DoubleArray array, DoubleBinaryOperator combine) {
    Check.size(this, array);
    DoubleArray empty = newEmptyArray(getShape());
    empty.combineAssign(array, combine);
    return empty;
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
    IntArray m = factory.newIntArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, function.applyAsInt(get(i)));
    }
    return m;
  }

  @Override
  public LongArray mapToLong(DoubleToLongFunction function) {
    LongArray m = factory.newLongArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, function.applyAsLong(get(i)));
    }
    return m;
  }

  @Override
  public ComplexArray mapToComplex(DoubleFunction<Complex> function) {
    ComplexArray m = factory.newComplexArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, function.apply(get(i)));
    }
    return m;
  }

  @Override
  public <T> Array<T> mapToObj(DoubleFunction<? extends T> mapper) {
    Array<T> array = getArrayFactory().newArray(getShape());
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
    return factory.newDoubleVector(Arrays.copyOf(builder.elementData, builder.size()));
  }

  @Override
  public BooleanArray where(DoubleArray array, DoubleBiPredicate predicate) {
    Check.dimension(this, array);
    BooleanArray bits = factory.newBooleanArray(getShape());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i), array.get(i)));
    }
    return bits;
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
    Check.argument(isMatrix());
    setElement(getOffset() + i * stride(0) + j * stride(1), value);
  }

  @Override
  public final void set(int[] ix, double value) {
    Check.argument(ix.length == dims());
    setElement(StrideUtils.index(ix, getOffset(), stride), value);
  }

  @Override
  public final double get(int index) {
    return getElement(StrideUtils.index(index, getOffset(), stride, shape));
  }

  @Override
  public final double get(int i, int j) {
    Check.argument(isMatrix());
    return getElement(getOffset() + i * stride(0) + j * stride(1));
  }

  @Override
  public final double get(int... ix) {
    Check.argument(ix.length == dims());
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
    return factory.newDoubleVector(Arrays.copyOf(data, idx));
  }

  @Override
  public void sort(DoubleComparator cmp) {
    QuickSort.quickSort(0, size(), (left, right) -> cmp.compare(get(left), get(right)), this);
  }

  @Override
  public DoubleStream stream() {
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
  public List<Double> toList() {
    return new DoubleListView();
  }

  @Override
  public Array<Double> boxed() {
    return new AsArray<Double>(this) {
      @Override
      public DoubleArray asDouble() {
        return AbstractDoubleArray.this;
      }

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
  public DoubleArray times(DoubleArray other) {
    return times(1, other, 1);
  }

  @Override
  public DoubleArray times(double alpha, DoubleArray other, double beta) {
    Check.size(this, other);
    DoubleArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, alpha * get(i) * other.get(i) * beta);
    }
    return m;
  }

  @Override
  public DoubleArray times(double scalar) {
    DoubleArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i) * scalar);
    }
    return m;
  }

  @Override
  public void timesAssign(double scalar) {
    apply(v -> v * scalar);
  }

  @Override
  public void timesAssign(DoubleArray array) {
    combineAssign(array, (a, b) -> a * b);
  }

  @Override
  public DoubleArray plus(DoubleArray other) {
    return plus(1, other, 1);
  }

  @Override
  public DoubleArray plus(double scalar) {
    DoubleArray x = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      x.set(i, get(i) + scalar);
    }
    return x;
  }

  @Override
  public void plusAssign(DoubleArray other) {
    combineAssign(other, (a, b) -> a + b);
  }

  @Override
  public void plusAssign(double scalar) {
    apply(v -> v + scalar);
  }

  @Override
  public DoubleArray plus(double alpha, DoubleArray other, double beta) {
    Check.size(this, other);
    DoubleArray matrix = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, alpha * get(i) + other.get(i) * beta);
    }
    return matrix;
  }

  @Override
  public DoubleArray minus(double scalar) {
    return plus(-scalar);
  }

  @Override
  public DoubleArray minus(DoubleArray other) {
    return minus(1, other, 1);
  }

  @Override
  public void minusAssign(double scalar) {
    apply(v -> v - scalar);
  }

  @Override
  public void minusAssign(DoubleArray array) {
    combineAssign(array, (a, b) -> a - b);
  }

  @Override
  public DoubleArray minus(double alpha, DoubleArray other, double beta) {
    Check.size(this, other);
    DoubleArray matrix = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, alpha * get(i) - other.get(i) * beta);
    }
    return matrix;
  }

  @Override
  public DoubleArray reverseMinus(double scalar) {
    DoubleArray matrix = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, scalar - get(i));
    }

    return matrix;
  }

  @Override
  public void reverseMinusAssign(double scalar) {
    apply(v -> scalar - v);
  }

  @Override
  public DoubleArray div(double other) {
    return times(1.0 / other);
  }

  @Override
  public DoubleArray div(DoubleArray other) {
    Check.size(this, other);
    DoubleArray matrix = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, get(i) / other.get(i));
    }
    return matrix;
  }

  @Override
  public void divAssign(DoubleArray other) {
    combineAssign(other, (x, y) -> x / y);
  }

  @Override
  public void divAssign(double value) {
    apply(v -> v / value);
  }

  @Override
  public DoubleArray reverseDiv(double other) {
    DoubleArray matrix = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, other / get(i));
    }
    return matrix;
  }

  @Override
  public void reverseDivAssign(double other) {
    apply(v -> other / v);
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
    BooleanArray bits = factory.newBooleanArray(getShape());
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
    StringBuilder builder = new StringBuilder();
    try {
      ArrayPrinter.print(builder, this);
    } catch (IOException e) {
      return getClass().getSimpleName();
    }
    return builder.toString();
  }

  @Override
  public void swap(int a, int b) {
    double tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  @Override
  public Iterator<Double> iterator() {
    return toList().iterator();
  }

  private class IncrementalBuilder {

    private double[] buffer = new double[10];
    private int size = 0;

    public void add(double value) {
      buffer = ArrayAllocations.ensureCapacity(buffer, size);
      buffer[size++] = value;
    }

    public DoubleArray build() {
      return factory.newDoubleVector(Arrays.copyOf(buffer, size));
    }
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
