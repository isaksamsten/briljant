/*
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

import static org.briljantframework.array.Indexer.columnMajor;
import static org.briljantframework.array.Indexer.rowMajor;

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

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.function.DoubleBiPredicate;
import org.briljantframework.primitive.ArrayAllocations;
import org.briljantframework.primitive.DoubleList;

/**
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
  public DoubleArray assign(double value) {
    final int size = size();
    for (int i = 0; i < size; i++) {
      // setElement(i, value);
      set(i, value);
    }
    return this;
  }

  @Override
  public DoubleArray assign(double[] array) {
    Check.size(this.size(), array.length);
    for (int i = 0; i < array.length; i++) {
      set(i, array[i]);
    }
    return this;
  }

  @Override
  public DoubleArray assign(DoubleSupplier supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.getAsDouble());
    }
    return this;
  }

  @Override
  public DoubleArray assign(DoubleArray matrix, DoubleUnaryOperator operator) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsDouble(matrix.get(i)));
    }
    return this;
  }

  @Override
  public DoubleArray assign(DoubleArray matrix, DoubleBinaryOperator combine) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, combine.applyAsDouble(get(i), matrix.get(i)));
    }
    return this;
  }

  @Override
  public DoubleArray assign(IntArray matrix, IntToDoubleFunction function) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsDouble(matrix.get(i)));
    }
    return this;
  }

  @Override
  public DoubleArray assign(LongArray matrix, LongToDoubleFunction function) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsDouble(matrix.get(i)));
    }
    return this;
  }

  @Override
  public DoubleArray asDouble() {
    return this;
  }

  @Override
  public DoubleArray assign(ComplexArray other, ToDoubleFunction<? super Complex> function) {
    Check.size(this, other);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsDouble(other.get(i)));
    }
    return this;
  }

  @Override
  public DoubleArray update(DoubleUnaryOperator operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsDouble(get(i)));
    }
    return this;
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
    IntArray m = bj.intArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, function.applyAsInt(get(i)));
    }
    return m;
  }

  @Override
  public LongArray mapToLong(DoubleToLongFunction function) {
    LongArray m = bj.longArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, function.applyAsLong(get(i)));
    }
    return m;
  }

  @Override
  public ComplexArray mapToComplex(DoubleFunction<Complex> function) {
    ComplexArray m = bj.complexArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, function.apply(get(i)));
    }
    return m;
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
    return bj.array(Arrays.copyOf(builder.elementData, builder.size()));
  }

  @Override
  public BooleanArray satisfies(DoublePredicate predicate) {
    BooleanArray bits = bj.booleanArray(getShape());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i)));
    }
    return bits;
  }

  @Override
  public BooleanArray satisfies(DoubleArray matrix, DoubleBiPredicate predicate) {
    Check.shape(this, matrix);
    BooleanArray bits = bj.booleanArray(getShape());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i), matrix.get(i)));
    }
    return bits;
  }

  @Override
  public void forEach(DoubleConsumer consumer) {
    for (int i = 0; i < size(); i++) {
      consumer.accept(get(i));
    }
  }

  @Override
  public double reduce(double identity, DoubleBinaryOperator reduce) {
    return reduce(identity, reduce, DoubleUnaryOperator.identity());
  }

  @Override
  public double reduce(double identity, DoubleBinaryOperator reduce, DoubleUnaryOperator map) {
    for (int i = 0; i < size(); i++) {
      identity = reduce.applyAsDouble(map.applyAsDouble(get(i)), identity);
    }
    return identity;
  }

  @Override
  public DoubleArray reduceVectors(int dim, ToDoubleFunction<? super DoubleArray> reduce) {
    Check.argument(dim < dims(), INVALID_DIMENSION, dim, dims());
    DoubleArray reduced = newEmptyArray(Indexer.remove(getShape(), dim));
    int vectors = vectors(dim);
    for (int i = 0; i < vectors; i++) {
      double value = reduce.applyAsDouble(getVector(dim, i));
      reduced.set(i, value);
    }
    return reduced;
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
      public long getElement(int index) {
        return (long) AbstractDoubleArray.this.getElement(index);
      }

      @Override
      public void setElement(int index, long value) {
        AbstractDoubleArray.this.set(index, (int) value);
      }

      @Override
      protected int elementSize() {
        return AbstractDoubleArray.this.elementSize();
      }
    };
  }

  @Override
  public void addTo(int i, double value) {
    set(i, get(i) + value);
  }

  @Override
  public void addTo(int i, int j, double value) {
    set(i, j, get(i, j) + value);
  }

  @Override
  public final void set(int[] ix, double value) {
    Check.argument(ix.length == dims());
    setElement(Indexer.columnMajorStride(ix, getOffset(), stride), value);
  }

  @Override
  public final double get(int... ix) {
    Check.argument(ix.length == dims());
    return getElement(Indexer.columnMajorStride(ix, getOffset(), stride));
  }

  @Override
  public final void set(int i, int j, double value) {
    Check.argument(isMatrix());
    setElement(getOffset() + i * stride(0) + j * stride(1), value);
  }

  @Override
  public final double get(int i, int j) {
    Check.argument(isMatrix());
    return getElement(getOffset() + i * stride(0) + j * stride(1));
  }

  @Override
  public final void set(int index, double value) {
    setElement(Indexer.linearized(index, getOffset(), stride, shape), value);
  }

  @Override
  public final double get(int index) {
    return getElement(Indexer.linearized(index, getOffset(), stride, shape));
  }

  protected abstract void setElement(int i, double value);

  protected abstract double getElement(int i);

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
  public BooleanArray asBoolean() {
    return new AsBooleanArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {

      @Override
      protected void setElement(int index, boolean value) {
        AbstractDoubleArray.this.setElement(index, value ? 1 : 0);
      }

      @Override
      protected boolean getElement(int index) {
        return AbstractDoubleArray.this.get(index) == 1;
      }

      @Override
      protected int elementSize() {
        return AbstractDoubleArray.this.elementSize();
      }
    };
  }

  @Override
  public BooleanArray lt(DoubleArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) < other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray gt(DoubleArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) > other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray eq(DoubleArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) == other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray lte(DoubleArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) <= other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray gte(DoubleArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) >= other.get(i));
    }
    return bits;
  }

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
        if (get(i) != mat.get(i)) {
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
  public ComplexArray asComplex() {
    return new AsComplexArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {
      @Override
      public void setElement(int index, Complex value) {
        AbstractDoubleArray.this.setElement(index, value.getReal());
      }

      @Override
      public Complex getElement(int index) {
        return Complex.valueOf(AbstractDoubleArray.this.getElement(index));
      }

      @Override
      protected int elementSize() {
        return AbstractDoubleArray.this.elementSize();
      }
    };
  }

  private class IncrementalBuilder {

    private double[] buffer = new double[10];
    private int size = 0;

    public void add(double value) {
      buffer = ArrayAllocations.ensureCapacity(buffer, size);
      buffer[size++] = value;
    }

    public DoubleArray build() {
      return bj.array(Arrays.copyOf(buffer, size));
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

  @Override
  public DoubleArray slice(BooleanArray bits) {
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
  public DoubleArray copy() {
    DoubleArray n = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      n.set(i, get(i));
    }
    return n;
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
  public List<Double> list() {
    return new DoubleListView();
  }

  @Override
  public Array<Double> boxed() {
    return new AsArray<Double>(this) {
      @Override
      protected Double getElement(int i) {
        return AbstractDoubleArray.this.getElement(i);
      }

      @Override
      protected void setElement(int i, Double value) {
        AbstractDoubleArray.this.setElement(i, value);
      }

      @Override
      protected int elementSize() {
        return AbstractDoubleArray.this.elementSize();
      }

      @Override
      public DoubleArray asDouble() {
        return AbstractDoubleArray.this;
      }
    };
  }

  @Override
  public DoubleArray mmul(DoubleArray other) {
    return mmul(1, other);
  }

  @Override
  public DoubleArray mmul(double alpha, DoubleArray other) {
    return mmul(alpha, Op.KEEP, other, Op.KEEP);
  }

  @Override
  public DoubleArray mmul(Op a, DoubleArray other, Op b) {
    return mmul(1, a, other, b);
  }


  @Override
  public DoubleArray mmul(double alpha, Op a, DoubleArray other, Op b) {
    int thisRows = rows();
    int thisCols = columns();
    if (a.isTrue()) {
      thisRows = columns();
      thisCols = rows();
    }
    int otherRows = other.rows();
    int otherColumns = other.columns();
    if (b.isTrue()) {
      otherRows = other.columns();
      otherColumns = other.rows();
    }

    if (thisCols != otherRows) {
      throw new NonConformantException(thisRows, thisCols, otherRows, otherColumns);
    }

    DoubleArray result = newEmptyArray(thisRows, otherColumns);
    for (int row = 0; row < thisRows; row++) {
      for (int col = 0; col < otherColumns; col++) {
        double sum = 0.0;
        for (int k = 0; k < thisCols; k++) {
          int thisIndex =
              a.isTrue() ? rowMajor(row, k, thisRows, thisCols) : columnMajor(0, row, k, thisRows,
                  thisCols);
          int otherIndex =
              b.isTrue() ? rowMajor(k, col, otherRows, otherColumns) : columnMajor(0, k, col,
                  otherRows, otherColumns);
          sum += get(thisIndex) * other.get(otherIndex);
        }
        result.set(row, col, alpha * sum);
      }
    }
    return result;
  }

  @Override
  public DoubleArray mul(DoubleArray other) {
    return mul(1, other, 1);
  }

  @Override
  public DoubleArray mul(double alpha, DoubleArray other, double beta) {
    Check.size(this, other);
    DoubleArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, alpha * get(i) * other.get(i) * beta);
    }
    return m;
  }

  @Override
  public DoubleArray mul(double scalar) {
    DoubleArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i) * scalar);
    }
    return m;
  }

  @Override
  public DoubleArray add(DoubleArray other) {
    return add(1, other, 1);
  }

  @Override
  public DoubleArray add(double scalar) {
    DoubleArray x = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      x.set(i, get(i) + scalar);
    }
    return x;
  }

  @Override
  public DoubleArray add(double alpha, DoubleArray other, double beta) {
    Check.size(this, other);
    DoubleArray matrix = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, alpha * get(i) + other.get(i) * beta);
    }
    return matrix;
  }

  @Override
  public DoubleArray sub(DoubleArray other) {
    return sub(1, other, 1);
  }

  @Override
  public DoubleArray sub(double scalar) {
    return add(-scalar);
  }

  @Override
  public DoubleArray sub(double alpha, DoubleArray other, double beta) {
    Check.size(this, other);
    DoubleArray matrix = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, alpha * get(i) - other.get(i) * beta);
    }
    return matrix;
  }

  @Override
  public DoubleArray rsub(double scalar) {
    DoubleArray matrix = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, scalar - get(i));
    }

    return matrix;
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
  public DoubleArray div(double other) {
    return mul(1.0 / other);
  }

  @Override
  public DoubleArray rdiv(double other) {
    DoubleArray matrix = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, other / get(i));
    }
    return matrix;
  }

  @Override
  public DoubleArray negate() {
    DoubleArray n = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      n.set(i, -get(i));
    }
    return n;
  }
}
