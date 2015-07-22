package org.briljantframework.array;

import com.google.common.base.Preconditions;
import com.google.common.collect.UnmodifiableIterator;

import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.complex.Complex;
import org.briljantframework.complex.MutableComplex;
import org.briljantframework.exceptions.NonConformantException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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

import static org.briljantframework.array.Indexer.columnMajor;
import static org.briljantframework.array.Indexer.rowMajor;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractComplexArray extends AbstractArray<ComplexArray>
    implements ComplexArray {

  protected AbstractComplexArray(ArrayFactory bj, int size) {
    super(bj, new int[]{size});
  }

  public AbstractComplexArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
  }

  public AbstractComplexArray(ArrayFactory bj, int offset, int[] shape, int[] stride,
                              int majorStride) {
    super(bj, offset, shape, stride, majorStride);
  }

  public final void set(int[] ix, Complex value) {
    Check.argument(ix.length == dims(), ILLEGAL_INDEX);
    setElement(Indexer.columnMajorStride(ix, getOffset(), getStride()), value);
  }

  public final Complex get(int... ix) {
    Check.argument(ix.length == dims(), ILLEGAL_INDEX);
    return getElement(Indexer.columnMajorStride(ix, getOffset(), getStride()));
  }

  @Override
  public final void set(int i, int j, Complex value) {
    Check.argument(isMatrix(), ILLEGAL_INDEX);
    setElement(getOffset() + i * stride(0) + j * stride(1), value);
  }

  @Override
  public final Complex get(int i, int j) {
    Check.argument(isMatrix(), ILLEGAL_INDEX);
    return getElement(getOffset() + i * stride(0) + j * stride(1));
  }

  @Override
  public final void set(int index, Complex value) {
    setElement(Indexer.linearized(index, getOffset(), stride, shape, majorStride), value);
  }

  @Override
  public final Complex get(int index) {
    return getElement(Indexer.linearized(index, getOffset(), stride, shape, majorStride));
  }

  /**
   * Sets the element at index {@code i}, ignoring offsets and strides.
   *
   * @param i     the index
   * @param value the value
   */
  protected abstract void setElement(int i, Complex value);

  /**
   * Gets the element at index {@code i}, ignoring offsets and strides.
   *
   * @param i the index
   * @return the value at {@code i}
   */
  protected abstract Complex getElement(int i);

  @Override
  public ComplexArray assign(Complex value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
    return this;
  }

  @Override
  public ComplexArray assign(ComplexArray o) {
    Check.shape(this, o);
    for (int i = 0; i < size(); i++) {
      set(i, o.get(i));
    }
    return this;
  }

  @Override
  public ComplexArray assign(Supplier<Complex> supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.get());
    }
    return this;
  }

  @Override
  public ComplexArray assign(ComplexArray matrix, UnaryOperator<Complex> operator) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(matrix.get(i)));
    }
    return this;
  }

  @Override
  public ComplexArray assign(ComplexArray matrix, BinaryOperator<Complex> combine) {
    Check.shape(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, combine.apply(get(i), matrix.get(i)));
    }
    return this;
  }

  @Override
  public ComplexArray assign(DoubleArray matrix) {
    Preconditions.checkArgument(matrix.size() == size());
    for (int i = 0; i < size(); i++) {
      set(i, Complex.valueOf(matrix.get(i)));
    }
    return this;
  }

  @Override
  public ComplexArray assign(DoubleArray matrix, DoubleFunction<Complex> operator) {
    Preconditions.checkArgument(matrix.size() == size());
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(matrix.get(i)));
    }
    return this;
  }

  @Override
  public ComplexArray assign(LongArray matrix, LongFunction<Complex> operator) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(matrix.get(i)));
    }
    return this;
  }

  @Override
  public ComplexArray assign(IntArray matrix, IntFunction<Complex> operator) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(matrix.get(i)));
    }
    return this;
  }

  @Override
  public ComplexArray update(UnaryOperator<Complex> operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(get(i)));
    }
    return this;
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
    IntArray matrix = bj.intArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsInt(get(i)));
    }
    return matrix;
  }

  @Override
  public DoubleArray asDouble() {
    return new AsDoubleArray(
        getMatrixFactory(), getOffset(), getShape(), getStride(), getMajorStrideIndex()) {

      @Override
      protected void setElement(int i, double value) {
        AbstractComplexArray.this.setElement(i, Complex.valueOf(value));
      }

      @Override
      protected double getElement(int i) {
        return AbstractComplexArray.this.getElement(i).doubleValue();
      }

      @Override
      protected int elementSize() {
        return AbstractComplexArray.this.elementSize();
      }
    };
  }

  @Override
  public IntArray asInt() {
    return new AsIntArray(
        getMatrixFactory(), getOffset(), getShape(), getStride(), getMajorStrideIndex()) {

      @Override
      public int getElement(int index) {
        return AbstractComplexArray.this.getElement(index).intValue();
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
  public LongArray mapToLong(ToLongFunction<Complex> function) {
    LongArray matrix = bj.longArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsLong(get(i)));
    }
    return matrix;
  }

  @Override
  public DoubleArray mapToDouble(ToDoubleFunction<Complex> function) {
    DoubleArray matrix = bj.doubleArray(getShape());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsDouble(get(i)));
    }
    return matrix;
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
  public BitArray satisfies(Predicate<Complex> predicate) {
    BitArray bits = bj.booleanArray(getShape());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i)));
    }
    return bits;
  }

  public BitArray satisfies(ComplexArray other, BiPredicate<Complex, Complex> predicate) {
    Check.size(this, other);
    BitArray bits = bj.booleanArray(getShape());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i), other.get(i)));
    }
    return bits;
  }

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
  public BitArray lt(ComplexArray other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BitArray gt(ComplexArray other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BitArray eq(ComplexArray other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BitArray lte(ComplexArray other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BitArray gte(ComplexArray other) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Complex reduce(Complex identity, BinaryOperator<Complex> reduce) {
    return reduce(identity, reduce, UnaryOperator.identity());
  }

  @Override
  public LongArray asLong() {
    return new AsLongArray(
        getMatrixFactory(), getOffset(), getShape(), getStride(), getMajorStrideIndex()) {
      @Override
      public long getElement(int index) {
        return AbstractComplexArray.this.getElement(index).longValue();
      }

      @Override
      public void setElement(int index, long value) {
        AbstractComplexArray.this.setElement(index, Complex.valueOf(value));
      }

      @Override
      protected int elementSize() {
        return AbstractComplexArray.this.elementSize();
      }
    };
  }

  @Override
  public Complex reduce(Complex identity, BinaryOperator<Complex> reduce,
                        UnaryOperator<Complex> map) {
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
  public BitArray asBit() {
    return new AsBitArray(
        getMatrixFactory(), getOffset(), getShape(), getStride(), getMajorStrideIndex()) {

      @Override
      public void setElement(int index, boolean value) {
        AbstractComplexArray.this.set(index, value ? Complex.ONE : Complex.ZERO);
      }

      @Override
      public boolean getElement(int index) {
        return AbstractComplexArray.this.getElement(index).equals(Complex.ONE);
      }

      @Override
      protected int elementSize() {
        return AbstractComplexArray.this.elementSize();
      }
    };
  }

  @Override
  public int compare(int a, int b) {
    return Double.compare(get(a).abs(), get(b).abs());
  }

  @Override
  public void swap(int a, int b) {
    Complex tmp = get(a);
    set(a, get(b));
    set(b, tmp);
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
  public ComplexArray asComplex() {
    return this;
  }

  @Override
  public Iterator<Complex> iterator() {
    return new UnmodifiableIterator<Complex>() {
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
      return bj.array(buffer.toArray(new Complex[buffer.size()]));
      //new BaseComplexMatrix(buffer.toArray(new Complex[buffer.size()]), buffer.size(), 1);
    }

    public void add(Complex value) {
      buffer.add(value);
    }
  }

  @Override
  public ComplexArray slice(Collection<Integer> rows, Collection<Integer> columns) {
    ComplexArray m = newEmptyArray(rows.size(), columns.size());
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
  public ComplexArray slice(Collection<Integer> indexes) {
    IncrementalBuilder builder = new IncrementalBuilder();
    indexes.forEach(index -> builder.add(get(index)));
    return builder.build();
  }

  @Override
  public ComplexArray slice(BitArray bits) {
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
  public ComplexArray copy() {
    ComplexArray n = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      n.set(i, get(i));
    }
    return n;
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
      @NotNull
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

      @Override
      public int size() {
        return AbstractComplexArray.this.size();
      }
    };
  }

  @Override
  public ComplexArray negate() {
    return map(Complex::negate);
  }

  @Override
  public ComplexArray mmul(ComplexArray other) {
    return mmul(Complex.ONE, other);
  }

  @Override
  public ComplexArray mmul(Complex alpha, ComplexArray other) {
    return mmul(alpha, Op.KEEP, other, Op.KEEP);
  }

  @Override
  public ComplexArray mmul(Op a, ComplexArray other, Op b) {
    return mmul(Complex.ONE, a, other, b);
  }

  @Override
  public ComplexArray mmul(Complex alpha, Op a, ComplexArray other, Op b) {
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

    ComplexArray result = newEmptyArray(thisRows, otherColumns);
    for (int row = 0; row < thisRows; row++) {
      for (int col = 0; col < otherColumns; col++) {
        MutableComplex sumAcc = new MutableComplex(0);
        for (int k = 0; k < thisCols; k++) {
          int thisIndex;
          int otherIndex;
          if (a.isTrue()) {
            thisIndex = rowMajor(row, k, thisRows, thisCols);
          } else {
            thisIndex = columnMajor(0, row, k, thisRows, thisCols);
          }
          if (b.isTrue()) {
            otherIndex = rowMajor(k, col, otherRows, otherColumns);
          } else {
            otherIndex = columnMajor(0, k, col, otherRows, otherColumns);
          }

          Complex thisValue = get(thisIndex);
          Complex otherValue = other.get(otherIndex);
          thisValue = a == Op.CONJUGATE_TRANSPOSE ? thisValue.conjugate() : thisValue;
          otherValue = b == Op.CONJUGATE_TRANSPOSE ? otherValue.conjugate() : otherValue;
          sumAcc.plus(thisValue.multiply(otherValue));
        }
        result.set(row, col, sumAcc.multiply(alpha).toComplex());
      }
    }
    return result;
  }

  @Override
  public ComplexArray mul(ComplexArray other) {
    return mul(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexArray mul(Complex alpha, ComplexArray other, Complex beta) {
    Check.shape(this, other);
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, alpha.multiply(get(i)).multiply(beta).multiply(other.get(i)));
    }
    return m;
  }

  @Override
  public ComplexArray mul(Complex scalar) {
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).multiply(scalar));
    }
    return m;
  }

  @Override
  public ComplexArray add(ComplexArray other) {
    return add(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexArray add(Complex scalar) {
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).plus(scalar));
    }
    return m;
  }

  @Override
  public ComplexArray add(Complex alpha, ComplexArray other, Complex beta) {
    Check.shape(this, other);
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).multiply(alpha).plus(other.get(i).multiply(beta)));
    }
    return m;
  }

  @Override
  public ComplexArray sub(ComplexArray other) {
    return sub(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexArray sub(Complex scalar) {
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).minus(scalar));
    }
    return m;
  }

  @Override
  public ComplexArray sub(Complex alpha, ComplexArray other, Complex beta) {
    Check.size(this, other);
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, alpha.multiply(get(i)).minus(beta.multiply(other.get(i))));
    }
    return m;
  }

  @Override
  public ComplexArray rsub(Complex scalar) {
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, scalar.minus(get(i)));
    }
    return m;
  }

  @Override
  public ComplexArray div(ComplexArray other) {
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).div(other.get(i)));
    }
    return m;
  }

  @Override
  public ComplexArray div(Complex other) {
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).div(other));
    }
    return m;
  }

  @Override
  public ComplexArray rdiv(Complex other) {
    ComplexArray m = newEmptyArray(getShape());
    for (int i = 0; i < size(); i++) {
      m.set(i, other.div(get(i)));
    }
    return m;
  }


}
