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
import java.util.function.DoubleToLongFunction;
import java.util.function.IntToLongFunction;
import java.util.function.LongBinaryOperator;
import java.util.function.LongFunction;
import java.util.function.LongPredicate;
import java.util.function.LongSupplier;
import java.util.function.LongToDoubleFunction;
import java.util.function.LongToIntFunction;
import java.util.function.LongUnaryOperator;
import java.util.function.ToLongFunction;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.function.LongBiPredicate;
import org.briljantframework.primitive.ArrayAllocations;

/**
 * This class provides a skeletal implementation of a long array.
 * 
 * @author Isak Karlsson
 */
public abstract class AbstractLongArray extends AbstractBaseArray<LongArray> implements LongArray {

  protected AbstractLongArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
  }

  protected AbstractLongArray(ArrayFactory bj, int offset, int[] shape, int[] stride,
      int majorStride) {
    super(bj, offset, shape, stride, majorStride);
  }

  @Override
  public void swap(int a, int b) {
    long tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  @Override
  public void set(int toIndex, LongArray from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void set(int toRow, int toColumn, LongArray from, int fromRow, int fromColumn) {
    set(toRow, toColumn, from.get(fromRow, fromColumn));
  }

  @Override
  public void set(int[] toIndex, LongArray from, int[] fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public int compare(int a, int b) {
    return Long.compare(get(a), get(b));
  }

  @Override
  public LongArray slice(BooleanArray indicator) {
    Check.shape(this, indicator);
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
      protected double getElement(int i) {
        return AbstractLongArray.this.getElement(i);
      }

      @Override
      protected void setElement(int i, double value) {
        AbstractLongArray.this.setElement(i, (long) value);
      }

      @Override
      protected int elementSize() {
        return AbstractLongArray.this.elementSize();
      }
    };
  }

  @Override
  public IntArray asInt() {
    return new AsIntArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {
      @Override
      public LongArray asLong() {
        return AbstractLongArray.this;
      }

      @Override
      public void setElement(int index, int value) {
        AbstractLongArray.this.setElement(index, value);
      }

      @Override
      public int getElement(int index) {
        return (int) AbstractLongArray.this.getElement(index);
      }

      @Override
      protected int elementSize() {
        return AbstractLongArray.this.elementSize();
      }


    };
  }

  @Override
  public LongArray asLong() {
    return this;
  }

  @Override
  public BooleanArray asBoolean() {
    return new AsBooleanArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {

      @Override
      public boolean getElement(int index) {
        return AbstractLongArray.this.getElement(index) == 1;
      }

      @Override
      public void setElement(int index, boolean value) {
        AbstractLongArray.this.setElement(index, value ? 1 : 0);
      }

      @Override
      protected int elementSize() {
        return AbstractLongArray.this.elementSize();
      }
    };
  }

  @Override
  public ComplexArray asComplex() {
    return new AsComplexArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {
      @Override
      public Complex getElement(int index) {
        return Complex.valueOf(AbstractLongArray.this.getElement(index));
      }

      @Override
      public void setElement(int index, Complex value) {
        AbstractLongArray.this.setElement(index, (long) value.getReal());
      }

      @Override
      protected int elementSize() {
        return AbstractLongArray.this.elementSize();
      }
    };
  }

  @Override
  public final LongArray copy() {
    LongArray matrix = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, get(i));
    }
    return matrix;
  }

  @Override
  public BooleanArray lt(LongArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) < other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray gt(LongArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) > other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray eq(LongArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) == other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray lte(LongArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) <= other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray gte(LongArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) >= other.get(i));
    }
    return bits;
  }

  @Override
  public LongArray assign(long value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
    return this;
  }

  @Override
  public void assign(long[] values) {
    Check.size(this.size(), values.length);
    for (int i = 0; i < values.length; i++) {
      set(i, values[i]);
    }
  }

  @Override
  public LongArray assign(LongSupplier supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.getAsLong());
    }
    return this;
  }

  @Override
  public LongArray assign(LongArray matrix, LongUnaryOperator operator) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsLong(matrix.get(i)));
    }
    return this;
  }

  @Override
  public LongArray assign(LongArray matrix, LongBinaryOperator combine) {
    Check.shape(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, combine.applyAsLong(get(i), matrix.get(i)));
    }
    return this;
  }

  @Override
  public LongArray assign(ComplexArray matrix, ToLongFunction<? super Complex> function) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsLong(matrix.get(i)));
    }
    return this;
  }

  @Override
  public LongArray assign(IntArray matrix, IntToLongFunction operator) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsLong(matrix.get(i)));
    }
    return this;
  }

  @Override
  public LongArray assign(DoubleArray matrix, DoubleToLongFunction function) {
    for (int i = 0; i < matrix.size(); i++) {
      set(i, function.applyAsLong(matrix.get(i)));
    }
    return this;
  }

  @Override
  public LongArray map(LongUnaryOperator operator) {
    LongArray mat = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      mat.set(i, operator.applyAsLong(get(i)));
    }
    return mat;
  }

  @Override
  public IntArray mapToInt(LongToIntFunction map) {
    IntArray matrix = factory.newIntArray(3, 3);
    for (int i = 0; i < size(); i++) {
      matrix.set(i, map.applyAsInt(get(i)));
    }
    return matrix;
  }

  @Override
  public DoubleArray mapToDouble(LongToDoubleFunction map) {
    DoubleArray matrix = factory.newDoubleArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, map.applyAsDouble(get(i)));
    }
    return matrix;
  }

  @Override
  public ComplexArray mapToComplex(LongFunction<Complex> map) {
    ComplexArray matrix = factory.newComplexArray();
    for (int i = 0; i < size(); i++) {
      matrix.set(i, map.apply(get(i)));
    }
    return matrix;
  }

  @Override
  public <T> Array<T> mapToObj(LongFunction<? extends T> mapper) {
    Array<T> array = getArrayFactory().newArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, mapper.apply(get(i)));
    }
    return array;
  }

  @Override
  public void apply(LongUnaryOperator operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsLong(get(i)));
    }
  }

  @Override
  public BooleanArray where(LongPredicate predicate) {
    BooleanArray bits = factory.newBooleanArray();
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i)));
    }
    return bits;
  }

  @Override
  public BooleanArray where(LongArray matrix, LongBiPredicate predicate) {
    Check.shape(this, matrix);
    BooleanArray bits = factory.newBooleanArray();
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i), matrix.get(i)));
    }
    return bits;
  }

  @Override
  public long reduce(long identity, LongBinaryOperator reduce) {
    return reduce(identity, reduce, LongUnaryOperator.identity());
  }

  @Override
  public long reduce(long identity, LongBinaryOperator reduce, LongUnaryOperator map) {
    for (int i = 0; i < size(); i++) {
      identity = reduce.applyAsLong(map.applyAsLong(get(i)), identity);
    }
    return identity;
  }

  @Override
  public LongArray reduceVector(int dim, ToLongFunction<? super LongArray> accumulator) {
    Check.argument(dim < dims(), INVALID_DIMENSION, dim, dims());
    LongArray reduced = newEmptyArray(Indexer.remove(getShape(), dim));
    int vectors = vectors(dim);
    for (int i = 0; i < vectors; i++) {
      long value = accumulator.applyAsLong(getVector(dim, i));
      reduced.set(i, value);
    }
    return reduced;
  }

  @Override
  public LongArray filter(LongPredicate operator) {
    IncrementalBuilder builder = new IncrementalBuilder();
    for (int i = 0; i < size(); i++) {
      long value = get(i);
      if (operator.test(value)) {
        builder.add(value);
      }
    }
    return builder.build();
  }

  @Override
  public final long get(int i, int j) {
    Check.argument(isMatrix());
    return getElement(getOffset() + i * stride(0) + j * stride(1));
  }

  @Override
  public final long get(int index) {
    return getElement(Indexer.linearized(index, getOffset(), stride, shape));
  }

  @Override
  public final void set(int index, long value) {
    setElement(Indexer.linearized(index, getOffset(), stride, shape), value);
  }

  public final void set(int[] ix, long value) {
    Check.argument(ix.length == dims());
    setElement(Indexer.columnMajorStride(ix, getOffset(), getStride()), value);
  }

  public final long get(int... ix) {
    Check.argument(ix.length == dims());
    return getElement(Indexer.columnMajorStride(ix, getOffset(), getStride()));
  }

  @Override
  public final void set(int i, int j, long value) {
    Check.argument(isMatrix());
    setElement(getOffset() + i * stride(0) + j * stride(1), value);
  }

  @Override
  public LongStream stream() {
    PrimitiveIterator.OfLong ofLong = new PrimitiveIterator.OfLong() {
      public int current = 0;

      @Override
      public long nextLong() {
        return get(current++);
      }

      @Override
      public boolean hasNext() {
        return current < size();
      }
    };
    Spliterator.OfLong spliterator = Spliterators.spliterator(ofLong, size(), Spliterator.SIZED);
    return StreamSupport.longStream(spliterator, false);
  }

  @Override
  public List<Long> toList() {
    return new AbstractList<Long>() {
      @Override
      public int size() {
        return 0;
      }

      @Override
      public Long get(int index) {
        return AbstractLongArray.this.get(index);
      }

      @Override
      public Long set(int index, Long element) {
        Long old = get(index);
        AbstractLongArray.this.set(index, element);
        return old;
      }


    };
  }

  @Override
  public Array<Long> boxed() {
    return new AsArray<Long>(this) {
      @Override
      protected void setElement(int i, Long value) {
        AbstractLongArray.this.set(i, value);
      }

      @Override
      protected Long getElement(int i) {
        return AbstractLongArray.this.getElement(i);
      }

      @Override
      protected int elementSize() {
        return AbstractLongArray.this.elementSize();
      }
    };
  }

  @Override
  public LongArray times(LongArray other) {
    return times(1, other, 1);
  }

  @Override
  public LongArray times(long alpha, LongArray other, long beta) {
    Check.size(this, other);
    LongArray m = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        m.set(i, j, alpha * get(i, j) * other.get(i, j) * beta);
      }
    }
    return m;
  }

  @Override
  public LongArray times(long scalar) {
    LongArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i) * scalar);
    }
    return m;
  }

  @Override
  public LongArray plus(LongArray other) {
    return plus(1, other, 1);
  }

  @Override
  public LongArray plus(long scalar) {
    LongArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, get(i, j) + scalar);
      }
    }
    return matrix;
  }

  @Override
  public LongArray plus(long alpha, LongArray other, long beta) {
    Check.size(this, other);
    LongArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) + other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  @Override
  public LongArray minus(LongArray other) {
    return minus(1, other, 1);
  }

  @Override
  public LongArray minus(long scalar) {
    return plus(-scalar);
  }

  @Override
  public LongArray minus(long alpha, LongArray other, long beta) {
    Check.size(this, other);
    LongArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) - other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  @Override
  public LongArray reverseMinus(long scalar) {
    LongArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, scalar - get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public LongArray div(LongArray other) {
    Check.size(this, other);
    LongArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, get(i, j) / other.get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public LongArray div(long other) {
    LongArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i) / other);
    }
    return m;
  }

  @Override
  public LongArray reverseDiv(long other) {
    LongArray matrix = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, other / get(i));
    }
    return matrix;
  }

  @Override
  public LongArray negate() {
    LongArray n = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      n.set(i, -get(i));
    }
    return n;
  }

  protected abstract void setElement(int i, long value);

  protected abstract long getElement(int i);

  public LongArray mmul(long alpha, LongArray other) {
    return mmul(alpha, ArrayOperation.KEEP, other, ArrayOperation.KEEP);
  }

  public LongArray mmul(long alpha, ArrayOperation a, LongArray other, ArrayOperation b) {
    int thisRows = rows();
    int thisCols = columns();
    if (a == ArrayOperation.TRANSPOSE) {
      thisRows = columns();
      thisCols = rows();
    }
    int otherRows = other.rows();
    int otherColumns = other.columns();
    if (b == ArrayOperation.TRANSPOSE) {
      otherRows = other.columns();
      otherColumns = other.rows();
    }

    if (thisCols != otherRows) {
      throw new NonConformantException(thisRows, thisCols, otherRows, otherColumns);
    }

    LongArray result = newEmptyArray(thisRows, otherColumns);
    for (int row = 0; row < thisRows; row++) {
      for (int col = 0; col < otherColumns; col++) {
        long sum = 0;
        for (int k = 0; k < thisCols; k++) {
          int thisIndex =
              a == ArrayOperation.TRANSPOSE ? rowMajor(row, k, thisRows, thisCols) : columnMajor(0,
                  row, k, thisRows, thisCols);
          int otherIndex =
              b == ArrayOperation.TRANSPOSE ? rowMajor(k, col, otherRows, otherColumns)
                  : columnMajor(0, k, col, otherRows, otherColumns);
          sum += get(thisIndex) * other.get(otherIndex);
        }
        result.set(row, col, alpha * sum);
      }
    }
    return result;
  }

  @Override
  public int hashCode() {
    int result = 1;
    for (int i = 0; i < size(); i++) {
      long bits = get(i);
      result = 31 * result + (int) (bits ^ (bits >>> 32));
    }

    return Objects.hash(shape, result);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof LongArray) {
      LongArray mat = (LongArray) obj;
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
  public Iterator<Long> iterator() {
    return new Iterator<Long>() {
      private int index = 0;

      @Override
      public boolean hasNext() {
        return index < size();
      }

      @Override
      public Long next() {
        return get(index++);
      }
    };
  }

  private class IncrementalBuilder {

    private long[] buffer = new long[10];
    private int size = 0;

    public void add(long a) {
      buffer = ArrayAllocations.ensureCapacity(buffer, size);
      buffer[size++] = a;
    }

    public LongArray build() {
      return factory.newVector(Arrays.copyOf(buffer, size));
    }
  }
}
