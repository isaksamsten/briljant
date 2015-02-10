package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.primitives.Ints.checkedCast;
import static org.briljantframework.matrix.Indexer.*;

import java.util.*;
import java.util.function.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.briljantframework.Check;
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

  private ComplexListView listView = null;

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
  public ComplexMatrix slice(Range rows, Range columns) {
    return new SliceComplexMatrix(this, rows, columns);
  }

  @Override
  public ComplexMatrix slice(Range range) {
    return new FlatSliceComplexMatrix(this, range);
  }

  @Override
  public ComplexMatrix slice(Range range, Axis axis) {
    if (axis == Axis.ROW) {
      return new SliceComplexMatrix(this, range, Range.range(columns()));
    } else {
      return new SliceComplexMatrix(this, Range.range(rows()), range);
    }
  }

  @Override
  public ComplexMatrix slice(Collection<Integer> rows, Collection<Integer> columns) {
    ComplexMatrix m = newEmptyMatrix(rows.size(), columns.size());
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
  public ComplexMatrix slice(Collection<Integer> indexes) {
    IncrementalBuilder builder = new IncrementalBuilder();
    indexes.forEach(index -> builder.add(get(index)));
    return builder.build();
  }

  @Override
  public ComplexMatrix slice(Collection<Integer> indexes, Axis axis) {
    ComplexMatrix matrix;
    if (axis == Axis.ROW) {
      matrix = newEmptyMatrix(indexes.size(), columns());
      int i = 0;
      for (int index : indexes) {
        matrix.setRow(i++, getRowView(index));
      }
    } else {
      matrix = newEmptyMatrix(rows(), indexes.size());
      int i = 0;
      for (int index : indexes) {
        matrix.setColumn(i++, getColumnView(index));
      }
    }
    return matrix;
  }

  @Override
  public ComplexMatrix slice(BitMatrix bits) {
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
  public ComplexMatrix slice(BitMatrix indexes, Axis axis) {
    int size = Matrices.sum(indexes);
    ComplexMatrix matrix;
    if (axis == Axis.ROW) {
      Check.size(rows(), indexes);
      matrix = newEmptyMatrix(size, columns());
      int index = 0;
      for (int i = 0; i < rows(); i++) {
        if (indexes.get(i)) {
          matrix.setRow(index++, getRowView(i));
        }
      }
    } else {
      Check.size(columns(), indexes);
      matrix = newEmptyMatrix(rows(), size);
      int index = 0;
      for (int j = 0; j < columns(); j++) {
        if (indexes.get(j)) {
          matrix.setColumn(index++, getColumnView(j));
        }
      }
    }
    return matrix;
  }

  @Override
  public void setRow(int index, ComplexMatrix row) {
    Check.size(columns(), row);
    for (int j = 0; j < columns(); j++) {
      set(index, j, row.get(j));
    }
  }

  @Override
  public void setColumn(int index, ComplexMatrix column) {
    Check.size(rows(), column);
    for (int i = 0; i < rows(); i++) {
      set(i, index, column.get(i));
    }
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
  public ComplexMatrix update(UnaryOperator<Complex> operator) {
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
  public ComplexMatrix assign(ComplexMatrix matrix, BinaryOperator<Complex> combine) {
    Check.equalShape(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, combine.apply(get(i), matrix.get(i)));
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
    if (listView == null) {
      listView = new ComplexListView();
    }
    return listView;
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
    return mmul(Complex.ONE, other);
  }

  @Override
  public ComplexMatrix mmul(Complex alpha, ComplexMatrix other) {
    return mmul(alpha, Transpose.NO, other, Transpose.NO);
  }

  @Override
  public ComplexMatrix mmul(Transpose a, ComplexMatrix other, Transpose b) {
    return mmul(Complex.ONE, a, other, b);
  }

  @Override
  public ComplexMatrix mmul(Complex alpha, Transpose a, ComplexMatrix other, Transpose b) {
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
          sumAcc.plus(thisValue.multiply(otherValue));
        }
        result.set(row, col, sumAcc.multiply(alpha).toComplex());
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
    Check.equalShape(this, other);
    ComplexMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, alpha.multiply(get(i)).multiply(beta).multiply(other.get(i)));
    }
    return m;
  }

  @Override
  public ComplexMatrix mul(Complex scalar) {
    ComplexMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).multiply(scalar));
    }
    return m;
  }

  @Override
  public ComplexMatrix add(ComplexMatrix other) {
    return add(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexMatrix add(Complex scalar) {
    ComplexMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).plus(scalar));
    }
    return m;
  }

  @Override
  public ComplexMatrix add(Complex alpha, ComplexMatrix other, Complex beta) {
    Check.equalShape(this, other);
    ComplexMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).multiply(alpha).plus(other.get(i).multiply(beta)));
    }
    return m;
  }

  @Override
  public ComplexMatrix sub(ComplexMatrix other) {
    return sub(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexMatrix sub(Complex scalar) {
    ComplexMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).minus(scalar));
    }
    return m;
  }

  @Override
  public ComplexMatrix sub(Complex alpha, ComplexMatrix other, Complex beta) {
    Check.equalSize(this, other);
    ComplexMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, alpha.multiply(get(i)).minus(beta.multiply(other.get(i))));
    }
    return m;
  }

  @Override
  public ComplexMatrix rsub(Complex scalar) {
    ComplexMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, scalar.minus(get(i)));
    }
    return m;
  }

  @Override
  public ComplexMatrix rsub(ComplexMatrix matrix, Axis axis) {
    return null;
  }

  @Override
  public ComplexMatrix div(ComplexMatrix other) {
    ComplexMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).div(other.get(i)));
    }
    return m;
  }

  @Override
  public ComplexMatrix div(Complex other) {
    ComplexMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i).div(other));
    }
    return m;
  }

  @Override
  public ComplexMatrix rdiv(Complex other) {
    ComplexMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, other.div(get(i)));
    }
    return m;
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

    private final Range row, column;
    private final ComplexMatrix parent;

    public SliceComplexMatrix(ComplexMatrix parent, Range row, Range column) {
      this(parent, checkNotNull(row).size(), row, checkNotNull(column).size(), column);
    }

    public SliceComplexMatrix(ComplexMatrix parent, int rows, Range row, int columns, Range column) {
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

  /**
   * Created by Isak Karlsson on 08/12/14.
   */
  public static class ComplexMatrixView extends AbstractComplexMatrix {
    private static final int ROW = 0;
    private static final int COLUMN = 1;

    private final ComplexMatrix parent;

    private final int rowOffset, colOffset;

    public ComplexMatrixView(ComplexMatrix parent, int rowOffset, int colOffset, int rows, int cols) {
      super(rows, cols);
      this.rowOffset = rowOffset;
      this.colOffset = colOffset;
      this.parent = parent;

      checkArgument(rowOffset >= 0 && rowOffset + rows() <= parent.rows(),
          "Requested row out of bounds.");
      checkArgument(colOffset >= 0 && colOffset + columns() <= parent.columns(),
          "Requested column out of bounds");
    }

    @Override
    public ComplexMatrix reshape(int rows, int columns) {
      return new ComplexMatrixView(parent.reshape(rows, columns), rowOffset, colOffset, rows,
          columns);
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public ComplexMatrix copy() {
      ComplexMatrix mat = parent.newEmptyMatrix(rows(), columns());
      for (int i = 0; i < size(); i++) {
        mat.set(i, get(i));
      }
      return mat;
    }

    @Override
    public Storage getStorage() {
      return parent.getStorage();
    }

    @Override
    public ComplexMatrix newEmptyMatrix(int rows, int columns) {
      return new DefaultComplexMatrix(rows, columns);
    }

    @Override
    public Complex get(int i, int j) {
      return parent.get(rowOffset + i, colOffset + j);
    }

    @Override
    public Complex get(int index) {
      return parent.get(computeLinearIndex(index));
    }

    @Override
    public boolean isArrayBased() {
      return parent.isArrayBased();
    }

    @Override
    public void set(int i, int j, Complex value) {
      parent.set(rowOffset + i, colOffset + j, value);
    }

    @Override
    public void set(int index, Complex value) {
      parent.set(computeLinearIndex(index), value);
    }

    private int computeLinearIndex(int index) {
      int currentColumn = index / rows() + colOffset;
      int currentRow = index % rows() + rowOffset;
      return columnMajor(currentRow, currentColumn, parent.rows(), parent.columns());
    }
  }

  protected class FlatSliceComplexMatrix extends AbstractComplexMatrix {
    private final ComplexMatrix parent;
    private final Range range;

    public FlatSliceComplexMatrix(ComplexMatrix parent, int size, Range range) {
      super(size);
      this.parent = checkNotNull(parent);
      this.range = checkNotNull(range);
    }

    public FlatSliceComplexMatrix(ComplexMatrix parent, Range range) {
      this(parent, checkNotNull(range).size(), range);
    }

    @Override
    public void set(int i, int j, Complex value) {
      set(columnMajor(i, j, rows(), columns()), value);
    }

    @Override
    public void set(int index, Complex value) {
      parent.set(sliceIndex(range.step(), index, parent.size()), value);
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
      return parent.get(sliceIndex(range.step(), index, parent.size()));
    }

    @Override
    public boolean isArrayBased() {
      return parent.isArrayBased();
    }
  }

  private class ComplexListView extends AbstractList<Complex> {

    @Override
    public Complex get(int i) {
      return AbstractComplexMatrix.this.get(i);
    }

    @Override
    public Complex set(int i, Complex value) {
      Complex old = AbstractComplexMatrix.this.get(i);
      AbstractComplexMatrix.this.set(i, value);
      return old;
    }

    @Override
    public Iterator<Complex> iterator() {
      return AbstractComplexMatrix.this.iterator();
    }

    @Override
    public int size() {
      return AbstractComplexMatrix.this.size();
    }
  }
}
