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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.BiPredicate;
import java.util.function.BinaryOperator;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.LongFunction;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.function.ToIntFunction;
import java.util.function.ToLongFunction;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayBackend;

/**
 * This class provides a skeletal implementation of a comples array.
 *
 * @author Isak Karlsson
 */
public abstract class AbstractComplexArray extends AbstractBaseArray<ComplexArray> implements
    ComplexArray {

  protected AbstractComplexArray(ArrayBackend backend, int size) {
    super(backend, new int[] {size});
  }

  public AbstractComplexArray(ArrayBackend backend, int[] shape) {
    super(backend, shape);
  }

  public AbstractComplexArray(ArrayBackend backend, int offset, int[] shape, int[] stride) {
    super(backend, offset, shape, stride);
  }

  @Override
  public void assign(Complex value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
  }

  @Override
  public void assign(double[] value) {
    Check.argument(value.length == size() * 2);
    int j = 0;
    for (int i = 0; i < size(); i++) {
      Complex c = Complex.valueOf(value[j], value[j + 1]);
      j += 2;
    }
  }

  @Override
  public void assign(Complex[] value) {
    Check.dimension(size(), value.length);
    for (int i = 0; i < value.length; i++) {
      set(i, value[i]);
    }
  }

  @Override
  public void assign(Supplier<Complex> supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.get());
    }
  }

  @Override
  public void assign(ComplexArray array, UnaryOperator<Complex> operator) {
    array = ShapeUtils.broadcastIfSensible(this, array);
    Check.size(this, array);
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(array.get(i)));
    }
  }

  @Override
  public void combineAssign(ComplexArray array, BinaryOperator<Complex> combine) {
    array = ShapeUtils.broadcastIfSensible(this, array);
    Check.dimension(this, array);
    for (int i = 0; i < size(); i++) {
      set(i, combine.apply(get(i), array.get(i)));
    }
  }

  @Override
  public void assign(DoubleArray array) {
    array = ShapeUtils.broadcastIfSensible(this, array);
    Check.argument(array.size() == size());
    for (int i = 0; i < size(); i++) {
      set(i, Complex.valueOf(array.get(i)));
    }
  }

  @Override
  public void assign(DoubleArray array, DoubleFunction<Complex> operator) {
    array = ShapeUtils.broadcastIfSensible(this, array);
    Check.argument(array.size() == size());
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(array.get(i)));
    }
  }

  @Override
  public void assign(LongArray array, LongFunction<Complex> operator) {
    array = ShapeUtils.broadcastIfSensible(this, array);
    Check.size(this, array);
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(array.get(i)));
    }
  }

  @Override
  public void assign(IntArray array, IntFunction<Complex> operator) {
    array = ShapeUtils.broadcastIfSensible(this, array);
    Check.size(this, array);
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(array.get(i)));
    }
  }

  @Override
  public ComplexArray map(UnaryOperator<Complex> operator) {
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, operator.apply(get(i)));
    }
    return m;
  }

  @Override
  public IntArray mapToInt(ToIntFunction<Complex> function) {
    IntArray matrix = getArrayBackend().getArrayFactory().newIntArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsInt(get(i)));
    }
    return matrix;
  }

  @Override
  public LongArray mapToLong(ToLongFunction<Complex> function) {
    LongArray matrix = getArrayBackend().getArrayFactory().newLongArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsLong(get(i)));
    }
    return matrix;
  }

  @Override
  public DoubleArray mapToDouble(ToDoubleFunction<Complex> function) {
    DoubleArray matrix = getArrayBackend().getArrayFactory().newDoubleArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsDouble(get(i)));
    }
    return matrix;
  }

  @Override
  public <T> Array<T> mapToObj(Function<Complex, ? extends T> mapper) {
    Array<T> array = getArrayBackend().getArrayFactory().newArray(getShape());
    for (int i = 0; i < size(); i++) {
      array.set(i, mapper.apply(get(i)));
    }
    return array;
  }

  @Override
  public void apply(UnaryOperator<Complex> operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(get(i)));
    }
  }

  @Override
  public ComplexArray filter(Predicate<Complex> predicate) {
    IncrementalBuilder builder = new IncrementalBuilder();
    for (int i = 0; i < size(); i++) {
      Complex value = get(i);
      if (predicate.test(value)) {
        builder.add(value);
      }
    }
    return builder.build();
  }

  @Override
  public BooleanArray where(Predicate<Complex> predicate) {
    BooleanArray bits = getArrayBackend().getArrayFactory().newBooleanArray(getShape());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i)));
    }
    return bits;
  }

  public BooleanArray where(ComplexArray other, BiPredicate<Complex, Complex> predicate) {
    Check.size(this, other);
    BooleanArray bits = getArrayBackend().getArrayFactory().newBooleanArray(getShape());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i), other.get(i)));
    }
    return bits;
  }

  @Override
  public Complex reduce(Complex identity, BinaryOperator<Complex> reduce) {
    return reduce(identity, reduce, UnaryOperator.identity());
  }

  @Override
  public ComplexArray reduceVectors(int dim,
      Function<? super ComplexArray, ? extends Complex> reduce) {
    Check.argument(dim < dims(), INVALID_DIMENSION, dim, dims());
    ComplexArray reduced = newEmptyArray(ArrayUtils.remove(getShape(), dim));
    int vectors = vectors(dim);
    for (int i = 0; i < vectors; i++) {
      Complex value = reduce.apply(getVector(dim, i));
      reduced.set(i, value);
    }
    return reduced;
  }

  @Override
  public Complex reduce(Complex identity, BinaryOperator<Complex> reduce, UnaryOperator<Complex> map) {
    for (int i = 0; i < size(); i++) {
      identity = reduce.apply(map.apply(get(i)), identity);
    }
    return identity;
  }

  @Override
  public ComplexArray reduceColumns(Function<? super ComplexArray, ? extends Complex> reduce) {
    ComplexArray mat = newEmptyArray(1, columns());
    for (int i = 0; i < columns(); i++) {
      mat.set(i, reduce.apply(getColumn(i)));
    }
    return mat;
  }

  @Override
  public ComplexArray reduceRows(Function<? super ComplexArray, ? extends Complex> reduce) {
    ComplexArray mat = newEmptyArray(rows(), 1);
    for (int i = 0; i < rows(); i++) {
      mat.set(i, reduce.apply(getRow(i)));
    }
    return mat;
  }

  @Override
  public ComplexArray conjugateTranspose() {
    ComplexArray matrix = newEmptyArray(columns(), rows());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(j, i, get(i, j).conjugate());
      }
    }
    return matrix;
  }

  @Override
  public final void set(int i, int j, Complex value) {
    Check.argument(isMatrix(), REQUIRE_2D);
    setElement(getOffset() + i * stride(0) + j * stride(1), value);
  }

  @Override
  public final void set(int index, Complex value) {
    setElement(StrideUtils.index(index, getOffset(), stride, shape), value);
  }

  public final void set(int[] ix, Complex value) {
    Check.argument(ix.length == dims(), REQUIRE_ND, dims());
    setElement(StrideUtils.index(ix, getOffset(), getStride()), value);
  }

  @Override
  public final Complex get(int index) {
    return getElement(StrideUtils.index(index, getOffset(), stride, shape));
  }

  @Override
  public final Complex get(int i, int j) {
    Check.argument(isMatrix(), REQUIRE_2D);
    return getElement(getOffset() + i * stride(0) + j * stride(1));
  }

  public final Complex get(int... ix) {
    Check.argument(ix.length == dims(), REQUIRE_ND, dims());
    return getElement(StrideUtils.index(ix, getOffset(), getStride()));
  }

  @Override
  public Array<Complex> asArray() {
    return new AsArray<Complex>(this) {

      @Override
      protected int elementSize() {
        return AbstractComplexArray.this.elementSize();
      }

      @Override
      protected void setElement(int i, Complex value) {
        AbstractComplexArray.this.setElement(i, value);
      }

      @Override
      protected Complex getElement(int i) {
        return AbstractComplexArray.this.getElement(i);
      }
    };
  }

  @Override
  public Stream<Complex> stream() {
    return StreamSupport.stream(Spliterators.spliterator(new Iterator<Complex>() {
      int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public Complex next() {
        return get(current++);
      }
    }, size(), Spliterator.SIZED), false);
  }

  @Override
  public final List<Complex> asList() {
    return new AbstractList<Complex>() {
      @Override
      public int size() {
        return AbstractComplexArray.this.size();
      }

      @Override
      public Complex get(int index) {
        return AbstractComplexArray.this.get(index);
      }

      @Override
      public Complex set(int index, Complex element) {
        Complex old = get(index);
        AbstractComplexArray.this.set(index, element);
        return old;
      }


    };
  }

  @Override
  public ComplexArray times(ComplexArray other) {
    return times(Complex.ONE, other);
  }

  @Override
  public ComplexArray times(Complex alpha, ComplexArray other) {
    other = ShapeUtils.broadcastIfSensible(this, other);
    Check.dimension(this, other);
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, alpha.multiply(get(i)).multiply(other.get(i)));
    }
    return m;
  }

  @Override
  public ComplexArray times(Complex scalar) {
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).multiply(scalar));
    }
    return m;
  }

  @Override
  public ComplexArray plus(ComplexArray other) {
    return plus(Complex.ONE, other);
  }

  @Override
  public ComplexArray plus(Complex scalar) {
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).add(scalar));
    }
    return m;
  }

  @Override
  public ComplexArray plus(Complex alpha, ComplexArray other) {
    other = ShapeUtils.broadcastIfSensible(this, other);
    Check.dimension(this, other);
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).multiply(alpha).add(other.get(i)));
    }
    return m;
  }

  @Override
  public ComplexArray minus(ComplexArray other) {
    return minus(Complex.ONE, other);
  }

  @Override
  public ComplexArray minus(Complex scalar) {
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).subtract(scalar));
    }
    return m;
  }

  @Override
  public ComplexArray minus(Complex alpha, ComplexArray other) {
    other = ShapeUtils.broadcastIfSensible(this, other);
    Check.size(this, other);
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, alpha.multiply(get(i)).subtract(other.get(i)));
    }
    return m;
  }

  @Override
  public ComplexArray reverseMinus(Complex scalar) {
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, scalar.subtract(get(i)));
    }
    return m;
  }

  @Override
  public ComplexArray div(ComplexArray other) {
    other = ShapeUtils.broadcastIfSensible(this, other);
    Check.size(this, other);
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).divide(other.get(i)));
    }
    return m;
  }

  @Override
  public ComplexArray div(Complex other) {
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).divide(other));
    }
    return m;
  }

  @Override
  public ComplexArray reverseDiv(Complex other) {
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, other.divide(get(i)));
    }
    return m;
  }

  @Override
  public ComplexArray negate() {
    return map(Complex::negate);
  }

  @Override
  public double[] data() {
    double[] data = new double[size() * 2];
    int j = 0;
    for (int i = 0; i < size(); i++) {
      Complex c = get(i);
      data[j] = c.getReal();
      data[j + 1] = c.getImaginary();
      j += 2;
    }
    return data;
  }

  /**
   * Gets the element at index {@code i}, ignoring offsets and strides.
   *
   * @param i the index
   * @return the value at {@code i}
   */
  protected abstract Complex getElement(int i);

  /**
   * Sets the element at index {@code i}, ignoring offsets and strides.
   *
   * @param i the index
   * @param value the value
   */
  protected abstract void setElement(int i, Complex value);

  @Override
  public int hashCode() {
    int result = 1;
    for (int i = 0; i < size(); i++) {
      int bits = get(i).hashCode();
      result = 31 * result + bits;
    }

    return Objects.hash(shape, result);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof ComplexArray) {
      ComplexArray mat = (ComplexArray) obj;
      if (!Arrays.equals(shape, mat.getShape())) {
        return false;
      }
      for (int i = 0; i < size(); i++) {
        if (!get(i).equals(mat.get(i))) {
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
  public void set(int toIndex, ComplexArray from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void set(int toRow, int toColumn, ComplexArray from, int fromRow, int fromColumn) {
    set(toRow, toColumn, from.get(fromRow, fromColumn));
  }

  @Override
  public void set(int[] toIndex, ComplexArray from, int[] fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void set(int[] toIndex, ComplexArray from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void set(int toIndex, ComplexArray from, int[] fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public DoubleArray asDoubleArray() {
    return new AsDoubleArray(getArrayBackend(), getOffset(), getShape(), getStride()) {

      @Override
      protected double getElement(int i) {
        return AbstractComplexArray.this.getElement(i).getReal();
      }

      @Override
      protected void setElement(int i, double value) {
        AbstractComplexArray.this.setElement(i, Complex.valueOf(value));
      }

      @Override
      protected int elementSize() {
        return AbstractComplexArray.this.elementSize();
      }
    };
  }

  @Override
  public IntArray asIntArray() {
    return new AsIntArray(getArrayBackend(), getOffset(), getShape(), getStride()) {

      @Override
      public int getElement(int index) {
        return (int) AbstractComplexArray.this.getElement(index).getReal();
      }

      @Override
      public void setElement(int index, int value) {
        AbstractComplexArray.this.setElement(index, Complex.valueOf(value));
      }

      @Override
      protected int elementSize() {
        return AbstractComplexArray.this.elementSize();
      }
    };
  }

  @Override
  public LongArray asLongArray() {
    return new AsLongArray(getArrayBackend(), getOffset(), getShape(), getStride()) {
      @Override
      public void setElement(int index, long value) {
        AbstractComplexArray.this.setElement(index, Complex.valueOf(value));
      }

      @Override
      public long getElement(int index) {
        return (long) AbstractComplexArray.this.getElement(index).getReal();
      }

      @Override
      protected int elementSize() {
        return AbstractComplexArray.this.elementSize();
      }
    };
  }

  @Override
  public BooleanArray asBooleanArray() {
    return new AsBooleanArray(getArrayBackend(), getOffset(), getShape(), getStride()) {

      @Override
      public boolean getElement(int index) {
        return AbstractComplexArray.this.getElement(index).equals(Complex.ONE);
      }

      @Override
      public void setElement(int index, boolean value) {
        AbstractComplexArray.this.set(index, value ? Complex.ONE : Complex.ZERO);
      }

      @Override
      protected int elementSize() {
        return AbstractComplexArray.this.elementSize();
      }
    };
  }

  @Override
  public ComplexArray asComplexArray() {
    return this;
  }

  @Override
  public ComplexArray copy() {
    ComplexArray n = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      n.set(i, get(i));
    }
    return n;
  }

  @Override
  public void swap(int a, int b) {
    Complex tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  @Override
  public Iterator<Complex> iterator() {
    return new Iterator<Complex>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public Complex next() {
        return get(current++);
      }
    };
  }

  public class IncrementalBuilder {

    private List<Complex> buffer = new ArrayList<>();

    public ComplexArray build() {
      return getArrayBackend().getArrayFactory().newComplexVector(buffer.toArray(new Complex[buffer.size()]));
    }

    public void add(Complex value) {
      buffer.add(value);
    }
  }
}
