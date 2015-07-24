package org.briljantframework.array;

import com.carrotsearch.hppc.LongArrayList;

import org.briljantframework.Check;
import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.function.LongBiPredicate;
import org.briljantframework.array.api.ArrayFactory;
import org.jetbrains.annotations.NotNull;

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

import static org.briljantframework.array.Indexer.columnMajor;
import static org.briljantframework.array.Indexer.rowMajor;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractLongArray extends AbstractArray<LongArray> implements LongArray {

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
  public final long get(int i, int j) {
    Check.argument(isMatrix());
    return getElement(getOffset() + i * stride(0) + j * stride(1));
  }

  @Override
  public final void set(int index, long value) {
    setElement(Indexer.linearized(index, getOffset(), stride, shape), value);
  }

  @Override
  public final long get(int index) {
    return getElement(Indexer.linearized(index, getOffset(), stride, shape));
  }

  protected void setElement(int i, long value) {
    throw new UnsupportedOperationException();
  }

  protected long getElement(int i) {
    throw new UnsupportedOperationException();
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
  public LongArray assign(long value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
    return this;
  }

  @Override
  public DoubleArray asDouble() {
    return new AsDoubleArray(
        getMatrixFactory(), getOffset(), getShape(), getStride(), getMajorStrideIndex()) {
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
  public IntArray asInt() {
    return new AsIntArray(
        getMatrixFactory(), getOffset(), getShape(), getStride(), getMajorStrideIndex()) {
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
  public LongArray update(LongUnaryOperator operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsLong(get(i)));
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
    IntArray matrix = bj.intArray(3, 3);
    for (int i = 0; i < size(); i++) {
      matrix.set(i, map.applyAsInt(get(i)));
    }
    return matrix;
  }

  @Override
  public DoubleArray mapToDouble(LongToDoubleFunction map) {
    DoubleArray matrix = bj.doubleArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, map.applyAsDouble(get(i)));
    }
    return matrix;
  }

  @Override
  public ComplexArray mapToComplex(LongFunction<Complex> map) {
    ComplexArray matrix = bj.complexArray();
    for (int i = 0; i < size(); i++) {
      matrix.set(i, map.apply(get(i)));
    }
    return matrix;
  }

  @Override
  public BitArray satisfies(LongPredicate predicate) {
    BitArray bits = bj.booleanArray();
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i)));
    }
    return bits;
  }

  @Override
  public BitArray satisfies(LongArray matrix, LongBiPredicate predicate) {
    Check.shape(this, matrix);
    BitArray bits = bj.booleanArray();
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i), matrix.get(i)));
    }
    return bits;
  }

  @Override
  public LongArray asLong() {
    return this;
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
  public LongArray reduceColumns(ToLongFunction<? super LongArray> reduce) {
    LongArray mat = newEmptyArray(1, columns());
    for (int i = 0; i < columns(); i++) {
      mat.set(i, reduce.applyAsLong(getColumn(i)));
    }
    return mat;
  }

  @Override
  public LongArray reduceRows(ToLongFunction<? super LongArray> reduce) {
    LongArray mat = newEmptyArray(rows(), 1);
    for (int i = 0; i < rows(); i++) {
      mat.set(i, reduce.applyAsLong(getRow(i)));
    }
    return mat;
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
  public BitArray asBit() {
    return new AsBitArray(
        getMatrixFactory(), getOffset(), getShape(), getStride(), getMajorStrideIndex()) {

      @Override
      public void setElement(int index, boolean value) {
        AbstractLongArray.this.setElement(index, value ? 1 : 0);
      }

      @Override
      public boolean getElement(int index) {
        return AbstractLongArray.this.getElement(index) == 1;
      }

      @Override
      protected int elementSize() {
        return AbstractLongArray.this.elementSize();
      }
    };
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
  public List<Long> asList() {
    return new AbstractList<Long>() {
      @NotNull
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

      @Override
      public int size() {
        return 0;
      }
    };
  }

  @Override
  public LongArray mmul(LongArray other) {
    return mmul(1, other);
  }

  @Override
  public LongArray mmul(long alpha, LongArray other) {
    return mmul(alpha, Op.KEEP, other, Op.KEEP);
  }

  @Override
  public LongArray mmul(Op a, LongArray other, Op b) {
    return mmul(1, a, other, b);
  }

  @Override
  public ComplexArray asComplex() {
    return new AsComplexArray(
        getMatrixFactory(), getOffset(), getShape(), getStride(), getMajorStrideIndex()) {
      @Override
      public void setElement(int index, Complex value) {
        AbstractLongArray.this.setElement(index, value.longValue());
      }

      @Override
      public Complex getElement(int index) {
        return Complex.valueOf(AbstractLongArray.this.getElement(index));
      }

      @Override
      protected int elementSize() {
        return AbstractLongArray.this.elementSize();
      }
    };
  }

  @Override
  public LongArray mmul(long alpha, Op a, LongArray other, Op b) {
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

    LongArray result = newEmptyArray(thisRows, otherColumns);
    for (int row = 0; row < thisRows; row++) {
      for (int col = 0; col < otherColumns; col++) {
        long sum = 0;
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
  public LongArray mul(LongArray other) {
    return mul(1, other, 1);
  }

  @Override
  public LongArray mul(long alpha, LongArray other, long beta) {
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
  public LongArray mul(long scalar) {
    LongArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i) * scalar);
    }
    return m;
  }

  @Override
  public LongArray add(LongArray other) {
    return add(1, other, 1);
  }

  @Override
  public LongArray add(long scalar) {
    LongArray matrix = newEmptyArray(getShape());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, get(i, j) + scalar);
      }
    }
    return matrix;
  }

  @Override
  public LongArray add(long alpha, LongArray other, long beta) {
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
  public LongArray sub(LongArray other) {
    return sub(1, other, 1);
  }

  @Override
  public LongArray sub(long scalar) {
    return add(-scalar);
  }

  @Override
  public LongArray sub(long alpha, LongArray other, long beta) {
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
  public LongArray rsub(long scalar) {
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
  public LongArray rdiv(long other) {
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
  public BitArray lt(LongArray other) {
    Check.size(this, other);
    BitArray bits = getMatrixFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) < other.get(i));
    }
    return bits;
  }

  @Override
  public BitArray gt(LongArray other) {
    Check.size(this, other);
    BitArray bits = getMatrixFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) > other.get(i));
    }
    return bits;
  }

  @Override
  public BitArray eq(LongArray other) {
    Check.size(this, other);
    BitArray bits = getMatrixFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) == other.get(i));
    }
    return bits;
  }

  @Override
  public BitArray lte(LongArray other) {
    Check.size(this, other);
    BitArray bits = getMatrixFactory().booleanArray(getShape());
    int m = size();
    for (int i = 0; i < m; i++) {
      bits.set(i, get(i) <= other.get(i));
    }
    return bits;
  }

  @Override
  public BitArray gte(LongArray other) {
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

    private LongArrayList buffer = new LongArrayList();

    public LongArray build() {
      LongArray n = newEmptyArray(buffer.size());
      for (int i = 0; i < n.size(); i++) {
        n.set(i, buffer.get(i));
      }
      return n;
    }

    public void add(long value) {
      buffer.add(value);
    }
  }

  @Override
  public LongArray slice(Collection<Integer> rows, Collection<Integer> columns) {
    LongArray m = newEmptyArray(rows.size(), columns.size());
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
  public LongArray slice(Collection<Integer> indexes) {
    IncrementalBuilder builder = new IncrementalBuilder();
    indexes.forEach(index -> builder.add(get(index)));
    return builder.build();
  }


  @Override
  public LongArray slice(BitArray bits) {
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
  public final LongArray copy() {
    LongArray matrix = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, get(i));
    }
    return matrix;
  }
}