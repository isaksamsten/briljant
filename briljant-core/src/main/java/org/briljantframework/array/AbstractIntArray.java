/*
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

import com.carrotsearch.hppc.IntArrayList;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.function.IntBiPredicate;
import org.briljantframework.function.ToIntObjIntBiFunction;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.DoubleToIntFunction;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongToIntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.briljantframework.array.Indexer.columnMajor;
import static org.briljantframework.array.Indexer.rowMajor;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractIntArray extends AbstractBaseArray<IntArray> implements IntArray {

  protected AbstractIntArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
  }

  protected AbstractIntArray(ArrayFactory bj, int offset, int[] shape, int[] stride,
                             int majorStride) {
    super(bj, offset, shape, stride, majorStride);
  }

  @Override
  public void swap(int a, int b) {
    int tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  public final void set(int[] ix, int value) {
    Check.argument(ix.length == dims());
    setElement(Indexer.columnMajorStride(ix, getOffset(), stride), value);
  }

  public final int get(int... ix) {
    Check.argument(ix.length == dims());
    return getElement(Indexer.columnMajorStride(ix, getOffset(), stride));
  }

  @Override
  public final void set(int i, int j, int value) {
    Check.argument(isMatrix());
    setElement(getOffset() + i * stride(0) + j * stride(1), value);
  }

  @Override
  public final int get(int i, int j) {
    Check.argument(isMatrix());
    return getElement(getOffset() + i * stride(0) + j * stride(1));
  }

  @Override
  public final void set(int index, int value) {
    setElement(Indexer.linearized(index, getOffset(), stride, shape), value);
  }

  @Override
  public final int get(int index) {
    return getElement(Indexer.linearized(index, getOffset(), stride, shape));
  }

  protected abstract void setElement(int i, int value);

  protected abstract int getElement(int i);

  @Override
  public void set(int toIndex, IntArray from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void set(int toRow, int toColumn, IntArray from, int fromRow, int fromColumn) {
    set(toRow, toColumn, from.get(fromRow, fromColumn));
  }

  @Override
  public void set(int[] toIndex, IntArray from, int[] fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public int compare(int a, int b) {
    return Integer.compare(get(a), get(b));
  }

  @Override
  public DoubleArray asDouble() {
    return new AsDoubleArray(
        getArrayFactory(), getOffset(), getShape(), getStride(), getMajorStrideIndex()) {
      @Override
      protected double getElement(int i) {
        return AbstractIntArray.this.getElement(i);
      }

      @Override
      protected void setElement(int i, double value) {
        AbstractIntArray.this.setElement(i, (int) value);
      }

      @Override
      protected int elementSize() {
        return AbstractIntArray.this.elementSize();
      }
    };
  }

  @Override
  public IntArray assign(int value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
    return this;
  }

  @Override
  public IntArray assign(int[] data) {
    Check.size(this.size(), data.length);
    for (int i = 0; i < data.length; i++) {
      set(i, data[i]);
    }
    return this;
  }

  @Override
  public IntArray assign(IntSupplier supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.getAsInt());
    }
    return this;
  }

  @Override
  public IntArray assign(IntArray matrix, IntUnaryOperator operator) {
    Check.shape(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsInt(matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntArray assign(IntArray matrix, IntBinaryOperator combine) {
    Check.shape(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, combine.applyAsInt(get(i), matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntArray assign(ComplexArray matrix, ToIntFunction<? super Complex> function) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsInt(matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntArray asInt() {
    return this;
  }

  @Override
  public IntArray assign(DoubleArray matrix, DoubleToIntFunction function) {
    Check.size(this, matrix);
    for (int i = 0; i < matrix.size(); i++) {
      set(i, function.applyAsInt(matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntArray assign(LongArray matrix, LongToIntFunction operator) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsInt(matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntArray assign(BooleanArray matrix, ToIntObjIntBiFunction<Boolean> function) {
    Check.shape(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsInt(matrix.get(i), get(i)));
    }
    return this;
  }

  @Override
  public IntArray update(IntUnaryOperator operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsInt(get(i)));
    }
    return this;
  }

  @Override
  public IntArray map(IntUnaryOperator operator) {
    IntArray mat = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      mat.set(i, operator.applyAsInt(get(i)));
    }
    return mat;
  }

  @Override
  public LongArray mapToLong(IntToLongFunction function) {
    LongArray matrix = bj.longArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsLong(get(i)));
    }
    return matrix;
  }

  @Override
  public LongArray asLong() {
    return new AsLongArray(
        getArrayFactory(), getOffset(), getShape(), getStride(), getMajorStrideIndex()) {
      @Override
      public long getElement(int index) {
        return AbstractIntArray.this.getElement(index);
      }

      @Override
      public void setElement(int index, long value) {
        AbstractIntArray.this.setElement(index, (int) value);
      }

      @Override
      protected int elementSize() {
        return AbstractIntArray.this.elementSize();
      }
    };
  }

  @Override
  public DoubleArray mapToDouble(IntToDoubleFunction function) {
    DoubleArray matrix = bj.doubleArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsDouble(get(i)));
    }
    return matrix;
  }

  @Override
  public ComplexArray mapToComplex(IntFunction<Complex> function) {
    ComplexArray matrix = bj.complexArray();
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.apply(get(i)));
    }
    return matrix;
  }

  @Override
  public <U> Array<U> mapToObj(IntFunction<? extends U> function) {
    Array<U> array = getArrayFactory().referenceArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, function.apply(get(i)));
    }
    return array;
  }

  @Override
  public IntArray filter(IntPredicate operator) {
    Builder builder = new Builder();
    for (int i = 0; i < size(); i++) {
      int value = get(i);
      if (operator.test(value)) {
        builder.add(value);
      }
    }
    return builder.build();
  }

  @Override
  public BooleanArray satisfies(IntPredicate predicate) {
    BooleanArray bits = bj.booleanArray();
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i)));
    }
    return bits;
  }

  @Override
  public BooleanArray satisfies(IntArray matrix, IntBiPredicate predicate) {
    Check.shape(this, matrix);
    BooleanArray bits = bj.booleanArray();
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i), matrix.get(i)));
    }
    return bits;
  }

  @Override
  public void forEach(IntConsumer consumer) {
    for (int i = 0; i < size(); i++) {
      consumer.accept(get(i));
    }
  }

  @Override
  public BooleanArray asBoolean() {
    return new AsBooleanArray(
        getArrayFactory(), getOffset(), getShape(), getStride(), getMajorStrideIndex()) {

      @Override
      public void setElement(int index, boolean value) {
        AbstractIntArray.this.set(index, value ? 1 : 0);
      }

      @Override
      public boolean getElement(int index) {
        return AbstractIntArray.this.getElement(index) == 1;
      }

      @Override
      protected int elementSize() {
        return AbstractIntArray.this.elementSize();
      }
    };
  }

  @Override
  public int reduce(int identity, IntBinaryOperator reduce) {
    return reduce(identity, reduce, IntUnaryOperator.identity());
  }

  @Override
  public int reduce(int identity, IntBinaryOperator reduce, IntUnaryOperator map) {
    for (int i = 0; i < size(); i++) {
      identity = reduce.applyAsInt(map.applyAsInt(get(i)), identity);
    }
    return identity;
  }

  @Override
  public IntArray reduceVectors(int dim, ToIntFunction<? super IntArray> accumulator) {
    Check.argument(dim < dims(), INVALID_DIMENSION, dim, dims());
    IntArray reduced = newEmptyArray(Indexer.remove(getShape(), dim));
    int vectors = vectors(dim);
    for (int i = 0; i < vectors; i++) {
      int value = accumulator.applyAsInt(getVector(dim, i));
      reduced.set(i, value);
    }
    return reduced;
  }

  @Override
  public BooleanArray lt(IntArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) < other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray gt(IntArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) > other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray eq(IntArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) == other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray lte(IntArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) <= other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray gte(IntArray other) {
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
      int bits = get(i);
      result = 31 * result + bits;
    }

    return Objects.hash(rows(), columns(), result);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof IntArray) {
      IntArray mat = (IntArray) obj;
      boolean equalShape;
      // This saves one array copy
      if (mat instanceof AbstractBaseArray) {
        equalShape = Arrays.equals(shape, ((AbstractBaseArray) mat).shape);
      } else {
        equalShape = Arrays.equals(shape, mat.getShape());
      }
      if (!equalShape) {
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
  public ComplexArray asComplex() {
    return new AsComplexArray(
        getArrayFactory(), getOffset(), getShape(), getStride(), getMajorStrideIndex()) {
      @Override
      public void setElement(int index, Complex value) {
        AbstractIntArray.this.setElement(index, (int) value.getReal());
      }

      @Override
      public Complex getElement(int index) {
        return Complex.valueOf(AbstractIntArray.this.getElement(index));
      }

      @Override
      protected int elementSize() {
        return AbstractIntArray.this.elementSize();
      }
    };
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

  private class IntListView extends AbstractList<Integer> {

    @Override
    public Integer get(int i) {
      return AbstractIntArray.this.get(i);
    }

    @Override
    public Integer set(int i, Integer value) {
      int old = AbstractIntArray.this.get(i);
      AbstractIntArray.this.set(i, value);
      return old;
    }

    @Override
    public int size() {
      return AbstractIntArray.this.size();
    }
  }

  @Override
  public IntArray copy() {
    IntArray matrix = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, get(i));
    }
    return matrix;
  }

  @Override
  public void addTo(int index, int value) {
    set(index, get(index) + value);
  }

  @Override
  public void addTo(int i, int j, int value) {
    set(i, j, get(i, j) + value);
  }

  @Override
  public void update(int index, IntUnaryOperator operator) {
    set(index, operator.applyAsInt(get(index)));
  }

  @Override
  public void update(int i, int j, IntUnaryOperator operator) {
    set(i, j, operator.applyAsInt(get(i, j)));
  }

  @Override
  public IntStream stream() {
    PrimitiveIterator.OfInt ofInt = new PrimitiveIterator.OfInt() {
      private int current = 0;

      @Override
      public int nextInt() {
        return get(current++);
      }

      @Override
      public boolean hasNext() {
        return current < size();
      }


    };
    Spliterator.OfInt spliterator = Spliterators.spliterator(ofInt, size(), Spliterator.SIZED);
    return StreamSupport.intStream(spliterator, false);
  }

  @Override
  public List<Integer> list() {
    return new IntListView();
  }

  public Array<Integer> boxed() {
    return new AsArray<Integer>(this) {
      @Override
      protected Integer getElement(int i) {
        return AbstractIntArray.this.getElement(i);
      }

      @Override
      protected void setElement(int i, Integer value) {
        AbstractIntArray.this.setElement(i, value);
      }

      @Override
      protected int elementSize() {
        return AbstractIntArray.this.elementSize();
      }

      @Override
      public IntArray asInt() {
        return AbstractIntArray.this;
      }
    };
  }

  @Override
  public IntArray mmul(IntArray other) {
    return mmul(1, other);
  }

  @Override
  public IntArray mmul(int alpha, IntArray other) {
    return mmul(alpha, Op.KEEP, other, Op.KEEP);
  }

  @Override
  public IntArray mmul(Op a, IntArray other, Op b) {
    return mmul(1, a, other, b);
  }

  @Override
  public IntArray mmul(int alpha, Op a, IntArray other, Op b) {
    int thisRows = rows();
    int thisCols = columns();
    if (a == Op.TRANSPOSE) {
      thisRows = columns();
      thisCols = rows();
    }
    int otherRows = other.rows();
    int otherColumns = other.columns();
    if (b == Op.TRANSPOSE) {
      otherRows = other.columns();
      otherColumns = other.rows();
    }

    if (thisCols != otherRows) {
      throw new NonConformantException(thisRows, thisCols, otherRows, otherColumns);
    }

    IntArray result = newEmptyArray(thisRows, otherColumns);
    for (int row = 0; row < thisRows; row++) {
      for (int col = 0; col < otherColumns; col++) {
        int sum = 0;
        for (int k = 0; k < thisCols; k++) {
          int thisIndex =
              a == Op.TRANSPOSE ? rowMajor(row, k, thisRows, thisCols) : columnMajor(0, row, k,
                                                                                     thisRows,
                                                                                     thisCols);
          int otherIndex =
              b == Op.TRANSPOSE ? rowMajor(k, col, otherRows, otherColumns) : columnMajor(0, k, col,
                                                                                          otherRows,
                                                                                          otherColumns);
          sum += get(thisIndex) * other.get(otherIndex);
        }
        result.set(row, col, alpha * sum);
      }
    }
    return result;
  }

  @Override
  public IntArray mul(IntArray other) {
    return mul(1, other, 1);
  }

  @Override
  public IntArray mul(int alpha, IntArray other, int beta) {
    Check.size(this, other);
    IntArray m = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        m.set(i, j, alpha * get(i, j) * other.get(i, j) * beta);
      }
    }
    return m;
  }

  @Override
  public IntArray mul(int scalar) {
    IntArray m = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        m.set(i, j, get(i, j) * scalar);
      }
    }
    return m;
  }

  @Override
  public IntArray add(IntArray other) {
    return add(1, other, 1);
  }

  @Override
  public IntArray add(int scalar) {
    IntArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, get(i, j) + scalar);
      }
    }
    return matrix;
  }

  @Override
  public IntArray add(int alpha, IntArray other, int beta) {
    Check.size(this, other);
    IntArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) + other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  @Override
  public IntArray sub(IntArray other) {
    return sub(1, other, 1);
  }

  @Override
  public IntArray sub(int scalar) {
    return add(-scalar);
  }

  @Override
  public IntArray sub(int alpha, IntArray other, int beta) {
    Check.size(this, other);
    IntArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) - other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  @Override
  public IntArray rsub(int scalar) {
    IntArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, scalar - get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public IntArray div(IntArray other) {
    Check.size(this, other);
    IntArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, get(i, j) / other.get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public IntArray div(int other) {
    IntArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i) / other);
    }
    return m;
  }

  @Override
  public IntArray rdiv(int other) {
    IntArray matrix = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, other / get(i));
    }
    return matrix;
  }

  @Override
  public IntArray negate() {
    IntArray n = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      n.set(i, -get(i));
    }
    return n;
  }

  @Override
  public IntArray slice(BooleanArray bits) {
    Check.shape(this, bits);
    Builder builder = new Builder();
    for (int i = 0; i < size(); i++) {
      if (bits.get(i)) {
        builder.add(get(i));
      }
    }
    return builder.build();
  }

  private class Builder {

    private IntArrayList buffer = new IntArrayList();

    public void add(int value) {
      buffer.add(value);
    }

    public IntArray build() {
      return bj.array(buffer.toArray());
    }
  }

}
