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

import static org.briljantframework.array.StrideUtils.columnMajor;
import static org.briljantframework.array.StrideUtils.rowMajor;

import java.util.*;
import java.util.Arrays;
import java.util.function.*;
import java.util.stream.LongStream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.exceptions.MultiDimensionMismatchException;
import org.briljantframework.util.primitive.ArrayAllocations;
import org.briljantframework.util.sort.QuickSort;

import net.mintern.primitive.comparators.LongComparator;

/**
 * This class provides a skeletal implementation of a long array.
 * 
 * @author Isak Karlsson
 */
public abstract class AbstractLongArray extends AbstractBaseArray<LongArray> implements LongArray {

  protected AbstractLongArray(ArrayBackend backend, int[] shape) {
    super(backend, shape);
  }

  protected AbstractLongArray(ArrayBackend backend, int offset, int[] shape, int[] stride) {
    super(backend, offset, shape, stride);
  }

  @Override
  public void swap(int a, int b) {
    long tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  @Override
  public void setFrom(int toIndex, LongArray from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void setFrom(int toRow, int toColumn, LongArray from, int fromRow, int fromColumn) {
    set(toRow, toColumn, from.get(fromRow, fromColumn));
  }

  @Override
  public void setFrom(int[] toIndex, LongArray from, int[] fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void setFrom(int[] toIndex, LongArray from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void setFrom(int toIndex, LongArray from, int[] fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public DoubleArray doubleArray() {
    return new AsDoubleArray(getArrayBackend(), getOffset(), getShape(), getStride()) {
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
  public IntArray intArray() {
    return new AsIntArray(getArrayBackend(), getOffset(), getShape(), getStride()) {
      @Override
      public LongArray longArray() {
        return AbstractLongArray.this;
      }

      @Override
      public int getElement(int index) {
        return (int) AbstractLongArray.this.getElement(index);
      }

      @Override
      public void setElement(int index, int value) {
        AbstractLongArray.this.setElement(index, value);
      }

      @Override
      protected int elementSize() {
        return AbstractLongArray.this.elementSize();
      }


    };
  }

  @Override
  public LongArray longArray() {
    return this;
  }

  @Override
  public ComplexArray complexArray() {
    return new AsComplexArray(getArrayBackend(), getOffset(), getShape(), getStride()) {
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
    BooleanArray bits = getArrayBackend().getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) < other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray gt(LongArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayBackend().getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) > other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray eq(LongArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayBackend().getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) == other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray leq(LongArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayBackend().getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) <= other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray geq(LongArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayBackend().getArrayFactory().newBooleanArray(getShape());
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
    Check.dimension(this.size(), values.length);
    for (int i = 0; i < values.length; i++) {
      set(i, values[i]);
    }
  }

  @Override
  public void assign(LongSupplier supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.getAsLong());
    }
  }

  @Override
  public void assign(LongArray other, LongUnaryOperator operator) {
    org.briljantframework.array.Arrays.broadcastWith(this, other, (a, b) -> {
      Check.size(a, b);
      for (int i = 0, size = a.size(); i < size; i++) {
        a.set(i, operator.applyAsLong(b.get(i)));
      }
    });
  }

  @Override
  public void combineAssign(LongArray other, LongBinaryOperator combine) {
    org.briljantframework.array.Arrays.broadcastWith(this, other, (a, b) -> {
      Check.size(a, b);
      for (int i = 0, size = a.size(); i < size; i++) {
        a.set(i, combine.applyAsLong(a.get(i), b.get(i)));
      }
    });
  }

  @Override
  public void assign(ComplexArray other, ToLongFunction<? super Complex> function) {
    org.briljantframework.array.Arrays.broadcastWith(this, other, (a, b) -> {
      Check.size(a, b);
      for (int i = 0, size = a.size(); i < size; i++) {
        a.set(i, function.applyAsLong(b.get(i)));
      }
    });
  }

  @Override
  public void assign(IntArray other, IntToLongFunction function) {
    org.briljantframework.array.Arrays.broadcastWith(this, other, (a, b) -> {
      Check.size(a, b);
      for (int i = 0, size = a.size(); i < size; i++) {
        a.set(i, function.applyAsLong(b.get(i)));
      }
    });
  }

  @Override
  public void assign(DoubleArray other, DoubleToLongFunction function) {
    org.briljantframework.array.Arrays.broadcastWith(this, other, (a, b) -> {
      Check.size(a, b);
      for (int i = 0, size = a.size(); i < size; i++) {
        a.set(i, function.applyAsLong(b.get(i)));
      }
    });
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
    IntArray matrix = getArrayBackend().getArrayFactory().newIntArray(3, 3);
    for (int i = 0; i < size(); i++) {
      matrix.set(i, map.applyAsInt(get(i)));
    }
    return matrix;
  }

  @Override
  public DoubleArray mapToDouble(LongToDoubleFunction map) {
    DoubleArray matrix = getArrayBackend().getArrayFactory().newDoubleArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, map.applyAsDouble(get(i)));
    }
    return matrix;
  }

  @Override
  public ComplexArray mapToComplex(LongFunction<Complex> map) {
    ComplexArray matrix = getArrayBackend().getArrayFactory().newComplexArray();
    for (int i = 0; i < size(); i++) {
      matrix.set(i, map.apply(get(i)));
    }
    return matrix;
  }

  @Override
  public <T> Array<T> mapToObj(LongFunction<? extends T> mapper) {
    Array<T> array = getArrayBackend().getArrayFactory().newArray(getShape());
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
    BooleanArray bits = getArrayBackend().getArrayFactory().newBooleanArray();
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i)));
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
    LongArray reduced = newEmptyArray(ArrayUtils.remove(getShape(), dim));
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
    return getElement(StrideUtils.index(index, getOffset(), stride, shape));
  }

  @Override
  public final void set(int index, long value) {
    setElement(StrideUtils.index(index, getOffset(), stride, shape), value);
  }

  public final void set(int[] ix, long value) {
    Check.argument(ix.length == dims());
    setElement(StrideUtils.index(ix, getOffset(), getStride()), value);
  }

  public final long get(int... ix) {
    Check.argument(ix.length == dims());
    return getElement(StrideUtils.index(ix, getOffset(), getStride()));
  }

  @Override
  public final void set(int i, int j, long value) {
    Check.argument(isMatrix());
    setElement(getOffset() + i * stride(0) + j * stride(1), value);
  }

  @Override
  public void sort(LongComparator cmp) {
    QuickSort.quickSort(0, size(), (left, right) -> cmp.compare(get(left), get(right)), this);
  }

  @Override
  public LongStream longStream() {
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
  public List<Long> asList() {
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
  public Array<Long> asArray() {
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
    return times(1, other);
  }

  @Override
  public LongArray times(long alpha, LongArray other) {
    Check.size(this, other);
    LongArray m = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        m.set(i, j, alpha * get(i, j) * other.get(i, j));
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
    return plus(1, other);
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
  public LongArray plus(long alpha, LongArray other) {
    Check.size(this, other);
    LongArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) + other.get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public LongArray minus(LongArray other) {
    return minus(1, other);
  }

  @Override
  public LongArray minus(long scalar) {
    return plus(-scalar);
  }

  @Override
  public LongArray minus(long alpha, LongArray other) {
    Check.size(this, other);
    LongArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) - other.get(i, j));
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

  @Override
  public long[] data() {
    long[] data = new long[size()];
    for (int i = 0; i < size(); i++) {
      data[i] = get(i);
    }
    return data;
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
      throw new MultiDimensionMismatchException(thisRows, thisCols, otherRows, otherColumns);
    }

    LongArray result = newEmptyArray(thisRows, otherColumns);
    for (int row = 0; row < thisRows; row++) {
      for (int col = 0; col < otherColumns; col++) {
        long sum = 0;
        for (int k = 0; k < thisCols; k++) {
          int thisIndex = a == ArrayOperation.TRANSPOSE ? rowMajor(row, k, thisRows, thisCols)
              : columnMajor(0, row, k, thisRows, thisCols);
          int otherIndex = b == ArrayOperation.TRANSPOSE ? rowMajor(k, col, otherRows, otherColumns)
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
    return ArrayPrinter.toString(this);
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
      return getArrayBackend().getArrayFactory().newLongVector(Arrays.copyOf(buffer, size));
    }
  }
}
