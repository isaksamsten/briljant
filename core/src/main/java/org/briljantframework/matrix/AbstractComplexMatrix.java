package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.primitives.Ints.checkedCast;
import static org.briljantframework.matrix.Indexer.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.*;

import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.complex.ComplexBuilder;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.matrix.storage.Storage;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Created by Isak Karlsson on 02/01/15.
 */
public abstract class AbstractComplexMatrix extends AbstractMatrix implements ComplexMatrix {

  public static final String INVALID_SORT = "Unable to sort Complex values";

  protected AbstractComplexMatrix(int size) {
    super(size);
  }

  protected AbstractComplexMatrix(int rows, int cols) {
    super(rows, cols);
  }

  @Override
  public ComplexMatrix getRowView(int i) {
    return new ComplexMatrixView(this, i, 0, 1, columns());
  }

  @Override
  public ComplexMatrix getColumnView(int index) {
    return new ComplexMatrixView(this, 0, index, rows(), 1);
  }

  @Override
  public ComplexMatrix getDiagonalView() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ComplexMatrix getView(int rowOffset, int colOffset, int rows, int columns) {
    return new ComplexMatrixView(this, rowOffset, colOffset, rows, columns);
  }

  @Override
  public Matrix slice(Slice rows, Slice columns) {
    return new SliceComplexMatrix(this, rows, columns);
  }

  @Override
  public Matrix slice(Slice slice) {
    return new FlatSliceComplexMatrix(this, slice);
  }

  @Override
  public Matrix slice(Slice slice, Axis axis) {
    if (axis == Axis.ROW) {
      return new SliceComplexMatrix(this, slice, Slice.slice(columns()));
    } else {
      return new SliceComplexMatrix(this, Slice.slice(rows()), slice);
    }
  }

  @Override
  public Matrix slice(IntMatrix rows, IntMatrix columns) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Matrix slice(IntMatrix indexes) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Matrix slice(IntMatrix indexes, Axis axis) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Matrix slice(BitMatrix bits) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ComplexMatrix assign(Complex value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
    return this;
  }

  @Override
  public ComplexMatrix assign(Supplier<Complex> supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.get());
    }
    return this;
  }

  @Override
  public ComplexMatrix assign(UnaryOperator<Complex> operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(get(i)));
    }
    return this;
  }

  @Override
  public ComplexMatrix assign(ComplexMatrix matrix) {
    return assign(matrix, UnaryOperator.identity());
  }

  @Override
  public ComplexMatrix assign(ComplexMatrix matrix, UnaryOperator<Complex> operator) {
    Check.equalSize(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(matrix.get(i)));
    }
    return this;
  }

  @Override
  public ComplexMatrix assign(DoubleMatrix matrix) {
    Preconditions.checkArgument(matrix.size() == size());
    for (int i = 0; i < size(); i++) {
      set(i, Complex.valueOf(matrix.get(i)));
    }
    return this;
  }

  @Override
  public ComplexMatrix assign(DoubleMatrix matrix, DoubleFunction<Complex> operator) {
    Preconditions.checkArgument(matrix.size() == size());
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(matrix.get(i)));
    }
    return this;
  }

  @Override
  public ComplexMatrix assign(LongMatrix matrix, LongFunction<Complex> operator) {
    Check.equalSize(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(matrix.get(i)));
    }
    return this;
  }

  @Override
  public ComplexMatrix assign(IntMatrix matrix, IntFunction<Complex> operator) {
    Check.equalSize(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.apply(matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntMatrix mapToInt(ToIntFunction<Complex> function) {
    IntMatrix matrix = Matrices.newIntMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsInt(get(i)));
    }
    return matrix;
  }

  @Override
  public LongMatrix mapToLong(ToLongFunction<Complex> function) {
    LongMatrix matrix = Matrices.newLongMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsLong(get(i)));
    }
    return matrix;
  }

  @Override
  public DoubleMatrix mapToDouble(ToDoubleFunction<Complex> function) {
    DoubleMatrix matrix = Matrices.newDoubleMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsDouble(get(i)));
    }
    return matrix;
  }

  @Override
  public Complex reduce(Complex identity, BinaryOperator<Complex> reduce) {
    return reduce(identity, reduce, UnaryOperator.identity());
  }

  @Override
  public ComplexMatrix map(UnaryOperator<Complex> operator) {
    ComplexMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, operator.apply(get(i)));
    }
    return m;
  }

  @Override
  public ComplexMatrix filter(Predicate<Complex> predicate) {
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
  public BitMatrix satisfies(Predicate<Complex> predicate) {
    BitMatrix bits = Matrices.newBitMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i)));
    }
    return bits;
  }

  public BitMatrix satisfies(ComplexMatrix other, BiPredicate<Complex, Complex> predicate) {
    Check.equalSize(this, other);
    BitMatrix bits = Matrices.newBitMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i), other.get(i)));
    }
    return bits;
  }

  @Override
  public Complex reduce(Complex identity, BinaryOperator<Complex> reduce, UnaryOperator<Complex> map) {
    for (int i = 0; i < size(); i++) {
      identity = reduce.apply(identity, map.apply(get(i)));
    }
    return identity;
  }

  @Override
  public ComplexMatrix reduceColumns(Function<? super ComplexMatrix, ? extends Complex> reduce) {
    ComplexMatrix mat = newEmptyMatrix(1, columns());
    for (int i = 0; i < columns(); i++) {
      mat.set(i, reduce.apply(getColumnView(i)));
    }
    return mat;
  }

  @Override
  public ComplexMatrix reduceRows(Function<? super ComplexMatrix, ? extends Complex> reduce) {
    ComplexMatrix mat = newEmptyMatrix(rows(), 1);
    for (int i = 0; i < rows(); i++) {
      mat.set(i, reduce.apply(getRowView(i)));
    }
    return mat;
  }

  @Override
  public ComplexMatrix transpose() {
    ComplexMatrix matrix = newEmptyMatrix(columns(), rows());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(j, i, get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public ComplexMatrix copy() {
    ComplexMatrix n = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      n.set(i, get(i));
    }
    return n;
  }

  @Override
  public ComplexMatrix conjugateTranspose() {
    ComplexMatrix matrix = newEmptyMatrix(columns(), rows());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(j, i, get(i, j).conjugate());
      }
    }
    return matrix;
  }

  @Override
  public ComplexMatrix newEmptyVector(int size) {
    return newEmptyMatrix(size, 1);
  }

  @Override
  public void swap(int a, int b) {
    Complex tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    ImmutableTable.Builder<Object, Object, Object> builder = new ImmutableTable.Builder<>();
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        builder.put(i, j, get(i, j));
      }
    }
    Utils.prettyPrintTable(str, builder.build(), 0, 2, false, false);
    str.append("shape: ").append(getShape()).append(" type: complex");
    return str.toString();
  }

  @Override
  public ComplexMatrix negate() {
    return map(Complex::negate);
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

  @Override
  public double[] asDoubleArray() {
    double[] array = new double[Math.multiplyExact(checkedCast(size()), 2)];
    for (int i = 0; i < size(); i++) {
      Complex complex = get(i);
      array[i] = complex.real();
      array[i + 1] = complex.imag();
    }
    return array;
  }

  @Override
  public ComplexMatrix mmul(ComplexMatrix other) {
    return mmul(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexMatrix mmul(Complex alpha, ComplexMatrix other, Complex beta) {
    return mmul(alpha, Transpose.NO, other, beta, Transpose.NO);
  }

  @Override
  public ComplexMatrix mmul(Transpose a, ComplexMatrix other, Transpose b) {
    return mmul(Complex.ONE, a, other, Complex.ONE, b);
  }

  @Override
  public ComplexMatrix mmul(Complex alpha, Transpose a, ComplexMatrix other, Complex beta,
      Transpose b) {
    int thisRows = rows();
    int thisCols = columns();
    if (a.transpose()) {
      thisRows = columns();
      thisCols = rows();
    }

    int otherRows = other.rows();
    int otherColumns = other.columns();
    if (b.transpose()) {
      otherRows = other.columns();
      otherColumns = other.rows();
    }

    if (thisCols != otherRows) {
      throw new NonConformantException(thisRows, thisCols, otherRows, otherColumns);
    }

    ComplexMatrix result = newEmptyMatrix(thisRows, otherColumns);
    for (int row = 0; row < thisRows; row++) {
      for (int col = 0; col < otherColumns; col++) {
        ComplexBuilder sumAcc = new ComplexBuilder(0);
        for (int k = 0; k < thisCols; k++) {
          int thisIndex;
          int otherIndex;
          if (a.transpose()) {
            thisIndex = rowMajor(row, k, thisRows, thisCols);
          } else {
            thisIndex = columnMajor(row, k, thisRows, thisCols);
          }
          if (b.transpose()) {
            otherIndex = rowMajor(k, col, otherRows, otherColumns);
          } else {
            otherIndex = columnMajor(k, col, otherRows, otherColumns);
          }

          Complex thisValue = get(thisIndex);
          Complex otherValue = other.get(otherIndex);
          thisValue = a == Transpose.CONJ ? thisValue.conjugate() : thisValue;
          otherValue = b == Transpose.CONJ ? otherValue.conjugate() : otherValue;

          if (alpha.equals(Complex.ONE) && beta.equals(Complex.ONE)) {
            sumAcc.plus(thisValue.multiply(otherValue));
          } else {
            sumAcc.plus(alpha.multiply(thisValue).multiply(beta).multiply(otherValue));
          }
        }
        result.set(row, col, sumAcc.toComplex());
      }
    }
    return result;
  }

  @Override
  public ComplexMatrix mul(ComplexMatrix other) {
    return mul(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexMatrix mul(Complex alpha, ComplexMatrix other, Complex beta) {
    return copy().muli(alpha, other, beta);
  }

  @Override
  public ComplexMatrix mul(Complex scalar) {
    return copy().muli(scalar);
  }

  @Override
  public ComplexMatrix muli(ComplexMatrix other) {
    return muli(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexMatrix muli(Complex scalar) {
    for (int i = 0; i < size(); i++) {
      set(i, get(i).multiply(scalar));
    }
    return this;
  }

  @Override
  public ComplexMatrix muli(Complex alpha, ComplexMatrix other, Complex beta) {
    Check.equalSize(this, other);
    for (int i = 0; i < size(); i++) {
      set(i, alpha.multiply(get(i)).multiply(beta).multiply(other.get(i)));
    }
    return this;
  }

  @Override
  public ComplexMatrix add(ComplexMatrix other) {
    return add(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexMatrix add(Complex scalar) {
    return copy().addi(scalar);
  }

  @Override
  public ComplexMatrix add(Complex alpha, ComplexMatrix other, Complex beta) {
    return copy().addi(alpha, other, beta);
  }

  @Override
  public ComplexMatrix addi(ComplexMatrix other) {
    return addi(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexMatrix addi(Complex scalar) {
    for (int i = 0; i < size(); i++) {
      set(i, get(i).plus(scalar));
    }
    return this;
  }

  @Override
  public ComplexMatrix addi(Complex alpha, ComplexMatrix other, Complex beta) {
    Check.equalSize(this, other);
    for (int i = 0; i < size(); i++) {
      set(i, get(i).multiply(alpha).plus(other.get(i).multiply(beta)));
    }
    return this;
  }

  @Override
  public ComplexMatrix sub(ComplexMatrix other) {
    return sub(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexMatrix sub(Complex scalar) {
    return copy().subi(scalar);
  }

  @Override
  public ComplexMatrix sub(Complex alpha, ComplexMatrix other, Complex beta) {
    return copy().subi(alpha, other, beta);
  }

  @Override
  public ComplexMatrix subi(ComplexMatrix other) {
    return subi(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexMatrix subi(Complex scalar) {
    for (int i = 0; i < size(); i++) {
      set(i, get(i).minus(scalar));
    }
    return this;
  }

  @Override
  public ComplexMatrix subi(Complex alpha, ComplexMatrix other, Complex beta) {
    Check.equalSize(this, other);
    for (int i = 0; i < size(); i++) {
      set(i, alpha.multiply(get(i)).minus(beta.multiply(other.get(i))));
    }
    return this;
  }

  @Override
  public ComplexMatrix rsub(Complex scalar) {
    return copy().rsubi(scalar);
  }

  @Override
  public ComplexMatrix rsubi(Complex scalar) {
    for (int i = 0; i < size(); i++) {
      set(i, scalar.minus(get(i)));
    }
    return this;
  }

  @Override
  public ComplexMatrix div(ComplexMatrix other) {
    return copy().divi(other);
  }

  @Override
  public ComplexMatrix div(Complex other) {
    return copy().divi(other);
  }

  @Override
  public ComplexMatrix divi(ComplexMatrix other) {
    for (int i = 0; i < size(); i++) {
      set(i, get(i).div(other.get(i)));
    }
    return this;
  }

  @Override
  public ComplexMatrix divi(Complex other) {
    for (int i = 0; i < size(); i++) {
      set(i, get(i).div(other));
    }
    return this;
  }

  @Override
  public ComplexMatrix rdiv(Complex other) {
    return copy().rdivi(other);
  }

  @Override
  public ComplexMatrix rdivi(Complex other) {
    for (int i = 0; i < size(); i++) {
      set(i, other.div(get(i)));
    }
    return this;
  }

  public static class IncrementalBuilder {

    private List<Complex> buffer = new ArrayList<>();

    public ComplexMatrix build() {
      return new DefaultComplexMatrix(buffer.toArray(new Complex[buffer.size()]), buffer.size(), 1);
    }

    public void add(Complex value) {
      buffer.add(value);
    }
  }

  protected static class SliceComplexMatrix extends AbstractComplexMatrix {

    private final Slice row, column;
    private final ComplexMatrix parent;

    public SliceComplexMatrix(ComplexMatrix parent, Slice row, Slice column) {
      this(parent, checkNotNull(row).size(), row, checkNotNull(column).size(), column);
    }

    public SliceComplexMatrix(ComplexMatrix parent, int rows, Slice row, int columns, Slice column) {
      super(rows, columns);
      this.row = checkNotNull(row);
      this.column = checkNotNull(column);
      this.parent = checkNotNull(parent);
    }

    @Override
    public void set(int i, int j, Complex value) {
      parent.set(sliceIndex(row.step(), i, parent.rows()),
          sliceIndex(column.step(), j, parent.columns()), value);
    }

    @Override
    public void set(int index, Complex value) {
      int row = index % rows();
      int col = index / rows();
      set(row, col, value);
    }

    @Override
    public ComplexMatrix reshape(int rows, int columns) {
      Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
      return new SliceComplexMatrix(parent, rows, row, columns, column);
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public Storage getStorage() {
      return parent.getStorage();
    }

    @Override
    public ComplexMatrix newEmptyMatrix(int rows, int columns) {
      return parent.newEmptyMatrix(rows, columns);
    }

    @Override
    public Complex get(int i, int j) {
      return parent.get(sliceIndex(row.step(), i, parent.rows()),
          sliceIndex(column.step(), j, parent.columns()));
    }

    @Override
    public Complex get(int index) {
      int row = index % rows();
      int col = index / rows();
      return get(row, col);
    }

    @Override
    public boolean isArrayBased() {
      return parent.isArrayBased();
    }
  }

  protected class FlatSliceComplexMatrix extends AbstractComplexMatrix {
    private final ComplexMatrix parent;
    private final Slice slice;

    public FlatSliceComplexMatrix(ComplexMatrix parent, int size, Slice slice) {
      super(size);
      this.parent = checkNotNull(parent);
      this.slice = checkNotNull(slice);
    }

    public FlatSliceComplexMatrix(ComplexMatrix parent, Slice slice) {
      this(parent, checkNotNull(slice).size(), slice);
    }

    @Override
    public void set(int i, int j, Complex value) {
      set(columnMajor(i, j, rows(), columns()), value);
    }

    @Override
    public void set(int index, Complex value) {
      parent.set(sliceIndex(slice.step(), index, parent.size()), value);
    }

    @Override
    public ComplexMatrix reshape(int rows, int columns) {
      return copy().reshape(rows, columns);
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public Storage getStorage() {
      return parent.getStorage();
    }

    @Override
    public ComplexMatrix newEmptyMatrix(int rows, int columns) {
      return parent.newEmptyMatrix(rows, columns);
    }

    @Override
    public Complex get(int i, int j) {
      return get(columnMajor(i, j, rows(), columns()));
    }

    @Override
    public Complex get(int index) {
      return parent.get(sliceIndex(slice.step(), index, parent.size()));
    }

    @Override
    public boolean isArrayBased() {
      return parent.isArrayBased();
    }
  }



}
