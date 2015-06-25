/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.matrix;

import com.carrotsearch.hppc.DoubleArrayList;

import org.briljantframework.Check;
import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.function.Aggregator;
import org.briljantframework.function.DoubleBiPredicate;
import org.briljantframework.matrix.api.ArrayFactory;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.Collection;
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
import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;

import static org.briljantframework.matrix.Indexer.columnMajor;
import static org.briljantframework.matrix.Indexer.rowMajor;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractDoubleArray extends AbstractArray<DoubleArray>
    implements DoubleArray {

  protected AbstractDoubleArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
  }

  protected AbstractDoubleArray(ArrayFactory bj, int offset, int[] shape, int[] stride,
                                int majorStride) {
    super(bj, offset, shape, stride, majorStride);
  }

  @Override
  public DoubleArray assign(double value) {
    for (int i = 0; i < size(); i++) {
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
  public DoubleArray assign(DoubleArray o) {
    Check.size(this, o);
    for (int i = 0; i < size(); i++) {
      set(i, o.get(i));
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
  public <R, C> R aggregate(Aggregator<? super Double, R, C> aggregator) {
    C accum = aggregator.supplier().get();
    for (int i = 0; i < size(); i++) {
      aggregator.accumulator().accept(accum, get(i));
    }
    return aggregator.finisher().apply(accum);
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
    LongArray m = bj.longArray(getShape());//TODO
    for (int i = 0; i < size(); i++) {
      m.set(i, function.applyAsLong(get(i)));
    }
    return m;
  }

  @Override
  public ComplexArray mapToComplex(DoubleFunction<Complex> function) {
    ComplexArray m = bj.complexArray();//TODO
    for (int i = 0; i < size(); i++) {
      m.set(i, function.apply(get(i)));
    }
    return m;
  }

  @Override
  public DoubleArray filter(DoublePredicate predicate) {
    IncrementalBuilder builder = new IncrementalBuilder();
    for (int i = 0; i < size(); i++) {
      double value = get(i);
      if (predicate.test(value)) {
        builder.add(value);
      }
    }
    return builder.build();
  }

  @Override
  public BitArray satisfies(DoublePredicate predicate) {
    BitArray bits = bj.booleanArray();//TODO
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i)));
    }
    return bits;
  }

  @Override
  public BitArray satisfies(DoubleArray matrix, DoubleBiPredicate predicate) {
    Check.equalShape(this, matrix);
    BitArray bits = bj.booleanArray(); //TODO
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
    return new AsIntArray(
        getMatrixFactory(), getOffset(), getShape(), getStride(), getMajorStride()) {
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
    return new AsLongArray(
        getMatrixFactory(), getOffset(), getShape(), getStride(), getMajorStride()) {

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
  public void update(int i, DoubleUnaryOperator update) {
    set(i, update.applyAsDouble(get(i)));
  }

  @Override
  public void update(int i, int j, DoubleUnaryOperator update) {
    set(i, j, update.applyAsDouble(get(i, j)));
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
    setElement(index * stride(0) + getOffset(), value);
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
  public int compare(int a, int b) {
    return Double.compare(get(a), get(b));
  }

  @Override
  public BitArray asBit() {
    return new AsBitArray(
        getMatrixFactory(), getOffset(), getShape(), getStride(), getMajorStride()) {

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
  public void setRow(int index, DoubleArray row) {
    Check.size(columns(), row);
    for (int j = 0; j < columns(); j++) {
      set(index, j, row.get(j));
    }
  }

  @Override
  public void setColumn(int index, DoubleArray column) {
    Check.size(rows(), column);
    for (int i = 0; i < rows(); i++) {
      set(i, index, column.get(i));
    }
  }

  @Override
  public BitArray lt(DoubleArray other) {
    Check.size(this, other);
    BitArray bits = getMatrixFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) < other.get(i));
    }
    return bits;
  }

  @Override
  public BitArray gt(DoubleArray other) {
    Check.size(this, other);
    BitArray bits = getMatrixFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) > other.get(i));
    }
    return bits;
  }

  @Override
  public BitArray eq(DoubleArray other) {
    Check.size(this, other);
    BitArray bits = getMatrixFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) == other.get(i));
    }
    return bits;
  }

  @Override
  public BitArray lte(DoubleArray other) {
    Check.size(this, other);
    BitArray bits = getMatrixFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) <= other.get(i));
    }
    return bits;
  }

  @Override
  public BitArray gte(DoubleArray other) {
    Check.size(this, other);
    BitArray bits = getMatrixFactory().booleanArray(getShape());
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
    return new AsComplexArray(
        getMatrixFactory(), getOffset(), getShape(), getStride(), getMajorStride()) {
      @Override
      public void setElement(int index, Complex value) {
        AbstractDoubleArray.this.setElement(index, value.intValue());
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

  public class IncrementalBuilder {

    private DoubleArrayList buffer = new DoubleArrayList();

    public void add(double value) {
      buffer.add(value);
    }

    public DoubleArray build() {
      return bj.array(buffer.toArray());
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
  public DoubleArray slice(Collection<Integer> rows, Collection<Integer> columns) {
    DoubleArray m = newEmptyArray(rows.size(), columns.size());
    int i = 0;
    for (int row : rows) {
      int j = 0;
      for (int column : columns) {
        m.set(i, j++, get(row, column));
      }
      i++;
    }
    return m;
  }

  @Override
  public DoubleArray slice(Collection<Integer> indexes) {
    DoubleArray m = newEmptyArray(indexes.size());
    int i = 0;
    for (int index : indexes) {
      m.set(i++, get(index));
    }
    return m;
  }


  @Override
  public DoubleArray slice(BitArray bits) {
    Check.equalShape(this, bits);
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

    Spliterator.OfDouble spliterator = Spliterators.spliterator(
        ofDouble, size(), Spliterator.SIZED
    );
    return StreamSupport.doubleStream(spliterator, false);
  }

  @Override
  public List<Double> flat() {
    return new DoubleListView();
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
          int thisIndex = a.isTrue() ?
                          rowMajor(row, k, thisRows, thisCols) :
                          columnMajor(0, row, k, thisRows, thisCols);
          int otherIndex = b.isTrue() ?
                           rowMajor(k, col, otherRows, otherColumns) :
                           columnMajor(0, k, col, otherRows, otherColumns);
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
