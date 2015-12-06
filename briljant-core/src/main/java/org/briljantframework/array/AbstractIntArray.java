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

import net.mintern.primitive.Primitive;
import net.mintern.primitive.comparators.IntComparator;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.function.IntBiPredicate;
import org.briljantframework.function.ToIntObjIntBiFunction;
import org.briljantframework.primitive.IntList;
import org.briljantframework.sort.QuickSort;

/**
 * This class provides a skeletal implementation of an int array.
 * 
 * @author Isak Karlsson
 */
public abstract class AbstractIntArray extends AbstractBaseArray<IntArray> implements IntArray {

  protected AbstractIntArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
  }

  protected AbstractIntArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride) {
    super(bj, offset, shape, stride, majorStride);
  }

  @Override
  public void swap(int a, int b) {
    int tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

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
  public IntArray slice(BooleanArray indicator) {
    Check.dimension(this, indicator);
    IntList list = new IntList();
    for (int i = 0; i < size(); i++) {
      if (indicator.get(i)) {
        list.add(get(i));
      }
    }
    return factory.newVector(Arrays.copyOf(list.elementData, list.size()));
  }

  @Override
  public DoubleArray asDouble() {
    return new AsDoubleArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {
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
  public IntArray asInt() {
    return this;
  }

  @Override
  public LongArray asLong() {
    return new AsLongArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {
      @Override
      public void setElement(int index, long value) {
        AbstractIntArray.this.setElement(index, (int) value);
      }

      @Override
      public long getElement(int index) {
        return AbstractIntArray.this.getElement(index);
      }

      @Override
      protected int elementSize() {
        return AbstractIntArray.this.elementSize();
      }
    };
  }

  @Override
  public BooleanArray asBoolean() {
    return new AsBooleanArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {

      @Override
      public boolean getElement(int index) {
        return AbstractIntArray.this.getElement(index) == 1;
      }

      @Override
      public void setElement(int index, boolean value) {
        AbstractIntArray.this.set(index, value ? 1 : 0);
      }

      @Override
      protected int elementSize() {
        return AbstractIntArray.this.elementSize();
      }
    };
  }

  @Override
  public ComplexArray asComplex() {
    return new AsComplexArray(getArrayFactory(), getOffset(), getShape(), getStride(),
        getMajorStrideIndex()) {
      @Override
      public Complex getElement(int index) {
        return Complex.valueOf(AbstractIntArray.this.getElement(index));
      }

      @Override
      public void setElement(int index, Complex value) {
        AbstractIntArray.this.setElement(index, (int) value.getReal());
      }

      @Override
      protected int elementSize() {
        return AbstractIntArray.this.elementSize();
      }
    };
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
  public BooleanArray lt(IntArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) < other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray gt(IntArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) > other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray eq(IntArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) == other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray lte(IntArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) <= other.get(i));
    }
    return bits;
  }

  @Override
  public BooleanArray gte(IntArray other) {
    Check.size(this, other);
    BooleanArray bits = getArrayFactory().newBooleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) >= other.get(i));
    }
    return bits;
  }

  @Override
  public void assign(int value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
  }

  @Override
  public void assign(int[] data) {
    Check.dimension(this.size(), data.length);
    for (int i = 0; i < data.length; i++) {
      set(i, data[i]);
    }
  }

  @Override
  public void assign(IntSupplier supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.getAsInt());
    }
  }

  @Override
  public void assign(IntArray matrix, IntUnaryOperator operator) {
    Check.dimension(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsInt(matrix.get(i)));
    }
  }

  @Override
  public void assign(IntArray matrix, IntBinaryOperator combine) {
    Check.dimension(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, combine.applyAsInt(get(i), matrix.get(i)));
    }
  }

  @Override
  public void assign(ComplexArray matrix, ToIntFunction<? super Complex> function) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsInt(matrix.get(i)));
    }
  }

  @Override
  public void assign(DoubleArray matrix, DoubleToIntFunction function) {
    Check.size(this, matrix);
    for (int i = 0; i < matrix.size(); i++) {
      set(i, function.applyAsInt(matrix.get(i)));
    }
  }

  @Override
  public void assign(LongArray matrix, LongToIntFunction operator) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsInt(matrix.get(i)));
    }
  }

  @Override
  public void assign(BooleanArray matrix, ToIntObjIntBiFunction<Boolean> function) {
    Check.dimension(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsInt(matrix.get(i), get(i)));
    }
  }

  @Override
  public void apply(IntUnaryOperator operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsInt(get(i)));
    }
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
    LongArray matrix = factory.newLongArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsLong(get(i)));
    }
    return matrix;
  }

  @Override
  public DoubleArray mapToDouble(IntToDoubleFunction function) {
    DoubleArray matrix = factory.newDoubleArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsDouble(get(i)));
    }
    return matrix;
  }

  @Override
  public ComplexArray mapToComplex(IntFunction<Complex> function) {
    ComplexArray matrix = factory.newComplexArray();
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.apply(get(i)));
    }
    return matrix;
  }

  @Override
  public <U> Array<U> mapToObj(IntFunction<? extends U> function) {
    Array<U> array = getArrayFactory().newArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, function.apply(get(i)));
    }
    return array;
  }

  @Override
  public IntArray filter(IntPredicate operator) {
    IntList builder = new IntList();
    for (int i = 0; i < size(); i++) {
      int value = get(i);
      if (operator.test(value)) {
        builder.add(value);
      }
    }
    return factory.newVector(Arrays.copyOf(builder.elementData, builder.size()));
  }

  @Override
  public BooleanArray where(IntPredicate predicate) {
    BooleanArray bits = factory.newBooleanArray();
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i)));
    }
    return bits;
  }

  @Override
  public BooleanArray where(IntArray matrix, IntBiPredicate predicate) {
    Check.dimension(this, matrix);
    BooleanArray bits = factory.newBooleanArray();
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i), matrix.get(i)));
    }
    return bits;
  }

  @Override
  public void forEachPrimitive(IntConsumer consumer) {
    for (int i = 0; i < size(); i++) {
      consumer.accept(get(i));
    }
  }

  @Override
  public int reduce(int identity, IntBinaryOperator reduce) {
    return reduce(identity, reduce, IntUnaryOperator.identity());
  }

  @Override
  public int reduce(int identity, IntBinaryOperator reduce, IntUnaryOperator map) {
    for (int i = 0, size = size(); i < size; i++) {
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
  public final int get(int index) {
    return getElement(Indexer.linearized(index, getOffset(), stride, shape));
  }

  @Override
  public final void set(int index, int value) {
    setElement(Indexer.linearized(index, getOffset(), stride, shape), value);
  }

  @Override
  public final int get(int i, int j) {
    Check.argument(isMatrix());
    return getElement(getOffset() + i * stride(0) + j * stride(1));
  }

  @Override
  public final void set(int i, int j, int value) {
    Check.argument(isMatrix());
    setElement(getOffset() + i * stride(0) + j * stride(1), value);
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
  public void apply(int index, IntUnaryOperator operator) {
    set(index, operator.applyAsInt(get(index)));
  }

  @Override
  public void apply(int i, int j, IntUnaryOperator operator) {
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
  public List<Integer> toList() {
    return new IntListView();
  }

  public Array<Integer> boxed() {
    return new AsArray<Integer>(this) {
      @Override
      public IntArray asInt() {
        return AbstractIntArray.this;
      }

      @Override
      protected void setElement(int i, Integer value) {
        AbstractIntArray.this.setElement(i, value);
      }

      @Override
      protected Integer getElement(int i) {
        return AbstractIntArray.this.getElement(i);
      }

      @Override
      protected int elementSize() {
        return AbstractIntArray.this.elementSize();
      }


    };
  }

  @Override
  public void sort() {
    sort(Integer::compare);
  }

  @Override
  public void sort(IntComparator cmp) {
    if (stride(0) == 1 && dims() == 1) {
      Primitive.sort(data(), 0, size(), cmp);
    } else {
      QuickSort.quickSort(0, size(), cmp::compare, this);
    }
  }

  @Override
  public IntArray times(IntArray other) {
    return times(1, other, 1);
  }

  @Override
  public IntArray times(int alpha, IntArray other, int beta) {
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
  public IntArray times(int scalar) {
    IntArray m = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        m.set(i, j, get(i, j) * scalar);
      }
    }
    return m;
  }

  @Override
  public IntArray plus(IntArray other) {
    return plus(1, other);
  }

  @Override
  public IntArray plus(int scalar) {
    IntArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, get(i, j) + scalar);
      }
    }
    return matrix;
  }

  @Override
  public void plusAssign(IntArray other) {
    assign(other, Integer::sum);
  }

  @Override
  public void plusAssign(int scalar) {
    apply(i -> i + scalar);
  }

  @Override
  public IntArray plus(int alpha, IntArray other) {
    Check.size(this, other);
    IntArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) + other.get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public IntArray minus(IntArray other) {
    return minus(1, other);
  }

  @Override
  public IntArray minus(int scalar) {
    return plus(-scalar);
  }

  @Override
  public IntArray minus(int alpha, IntArray other) {
    Check.size(this, other);
    IntArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) - other.get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public void minusAssign(IntArray other) {
    assign(other, (a, b) -> a - b);
  }

  @Override
  public void minusAssign(int scalar) {
    apply(i -> i - scalar);
  }

  @Override
  public IntArray reverseMinus(int scalar) {
    IntArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, scalar - get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public void reverseMinusAssign(int scalar) {
    apply(i -> scalar - i);
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
  public void divAssign(IntArray other) {
    assign(other, (a, b) -> a / b);
  }

  @Override
  public void divAssign(int other) {
    apply(i -> i / other);
  }

  @Override
  public IntArray reverseDiv(int other) {
    IntArray matrix = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, other / get(i));
    }
    return matrix;
  }

  @Override
  public void reverseDivAssign(int other) {
    apply(i -> other / i);
  }

  @Override
  public IntArray negate() {
    IntArray n = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      n.set(i, -get(i));
    }
    return n;
  }

  protected abstract void setElement(int i, int value);

  protected abstract int getElement(int i);

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
  public Iterator<Integer> iterator() {
    return toList().iterator();
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
}
