package org.briljantframework.matrix;

import com.carrotsearch.hppc.IntArrayList;

import org.briljantframework.Check;
import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.function.IntBiPredicate;
import org.briljantframework.function.ToIntObjIntBiFunction;
import org.briljantframework.matrix.api.MatrixFactory;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Collection;
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.briljantframework.matrix.Indexer.columnMajor;
import static org.briljantframework.matrix.Indexer.rowMajor;
import static org.briljantframework.matrix.Indexer.sliceIndex;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public abstract class AbstractIntMatrix extends AbstractMatrix<IntMatrix> implements IntMatrix {

  protected AbstractIntMatrix(MatrixFactory bj, int size) {
    super(bj, size);
  }

  protected AbstractIntMatrix(MatrixFactory bj, int rows, int cols) {
    super(bj, rows, cols);
  }

  @Override
  public IntMatrix assign(IntMatrix o) {
    Check.equalShape(this, o);
    for (int i = 0; i < size(); i++) {
      set(i, o.get(i));
    }
    return this;
  }

  @Override
  public void swap(int a, int b) {
    int tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  @Override
  public void set(int toIndex, IntMatrix from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void set(int toRow, int toColumn, IntMatrix from, int fromRow, int fromColumn) {
    set(toRow, toColumn, from.get(fromRow, fromColumn));
  }

  @Override
  public int compare(int a, int b) {
    return Integer.compare(get(a), get(b));
  }

  @Override
  public void setRow(int index, IntMatrix row) {
    Check.size(columns(), row);
    for (int j = 0; j < columns(); j++) {
      set(index, j, row.get(j));
    }
  }

  @Override
  public void setColumn(int index, IntMatrix column) {
    Check.size(rows(), column.size());
    for (int i = 0; i < rows(); i++) {
      set(i, index, column.get(i));
    }
  }

  @Override
  public DoubleMatrix asDoubleMatrix() {
    return new AsDoubleMatrix(getMatrixFactory(), rows(), columns()) {
      @Override
      public void set(int i, int j, double value) {
        AbstractIntMatrix.this.set(i, j, (int) value);
      }

      @Override
      public void set(int index, double value) {
        AbstractIntMatrix.this.set(index, (int) value);
      }

      @Override
      public double get(int i, int j) {
        return AbstractIntMatrix.this.get(i, j);
      }

      @Override
      public double get(int index) {
        return AbstractIntMatrix.this.get(index);
      }

//      @Override
//      public Storage getStorage() {
//        return AbstractIntMatrix.this.getStorage();
//      }
    };
  }

  @Override
  public IntMatrix assign(int value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
    return this;
  }

  @Override
  public IntMatrix assign(int[] data) {
    Check.size(this.size(), data.length);
    for (int i = 0; i < data.length; i++) {
      set(i, data[i]);
    }
    return this;
  }

  @Override
  public IntMatrix assign(IntSupplier supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.getAsInt());
    }
    return this;
  }

  @Override
  public IntMatrix assign(IntMatrix matrix, IntUnaryOperator operator) {
    Check.equalShape(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsInt(matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntMatrix assign(IntMatrix matrix, IntBinaryOperator combine) {
    Check.equalShape(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, combine.applyAsInt(get(i), matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntMatrix assign(ComplexMatrix matrix, ToIntFunction<? super Complex> function) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsInt(matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntMatrix asIntMatrix() {
    return this;
  }

  @Override
  public IntMatrix assign(DoubleMatrix matrix, DoubleToIntFunction function) {
    Check.size(this, matrix);
    for (int i = 0; i < matrix.size(); i++) {
      set(i, function.applyAsInt(matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntMatrix assign(LongMatrix matrix, LongToIntFunction operator) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsInt(matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntMatrix assign(BitMatrix matrix, ToIntObjIntBiFunction<Boolean> function) {
    Check.equalShape(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsInt(matrix.get(i), get(i)));
    }
    return this;
  }

  @Override
  public IntMatrix update(IntUnaryOperator operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsInt(get(i)));
    }
    return this;
  }

  @Override
  public IntMatrix map(IntUnaryOperator operator) {
    IntMatrix mat = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      mat.set(i, operator.applyAsInt(get(i)));
    }
    return mat;
  }

  @Override
  public LongMatrix mapToLong(IntToLongFunction function) {
    LongMatrix matrix = bj.longMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsLong(get(i)));
    }
    return matrix;
  }

  @Override
  public LongMatrix asLongMatrix() {
    return new AsLongMatrix(getMatrixFactory(), rows(), columns()) {
      @Override
      public long get(int i, int j) {
        return AbstractIntMatrix.this.get(i, j);
      }

      @Override
      public long get(int index) {
        return AbstractIntMatrix.this.get(index);
      }

      @Override
      public void set(int index, long value) {
        AbstractIntMatrix.this.set(index, (int) value);
      }

      @Override
      public void set(int i, int j, long value) {
        AbstractIntMatrix.this.set(i, j, (int) value);
      }

    };
  }

  @Override
  public DoubleMatrix mapToDouble(IntToDoubleFunction function) {
    DoubleMatrix matrix = bj.doubleMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsDouble(get(i)));
    }
    return matrix;
  }

  @Override
  public ComplexMatrix mapToComplex(IntFunction<Complex> function) {
    ComplexMatrix matrix = bj.complexMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.apply(get(i)));
    }
    return matrix;
  }

  @Override
  public IntMatrix filter(IntPredicate operator) {
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
  public BitMatrix satisfies(IntPredicate predicate) {
    BitMatrix bits = bj.booleanMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i)));
    }
    return bits;
  }

  @Override
  public BitMatrix satisfies(IntMatrix matrix, IntBiPredicate predicate) {
    Check.equalShape(this, matrix);
    BitMatrix bits = bj.booleanMatrix(rows(), columns());
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
  public BitMatrix asBitMatrix() {
    return new AsBitMatrix(getMatrixFactory(), rows(), columns()) {
      @Override
      public void set(int i, int j, boolean value) {
        AbstractIntMatrix.this.set(i, j, value ? 1 : 0);
      }

      @Override
      public void set(int index, boolean value) {
        AbstractIntMatrix.this.set(index, value ? 1 : 0);
      }

      @Override
      public boolean get(int i, int j) {
        return AbstractIntMatrix.this.get(i, j) == 1;
      }

      @Override
      public boolean get(int index) {
        return AbstractIntMatrix.this.get(index) == 1;
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
  public IntMatrix reduceColumns(ToIntFunction<? super IntMatrix> reduce) {
    IntMatrix mat = newEmptyMatrix(1, columns());
    for (int i = 0; i < columns(); i++) {
      mat.set(i, reduce.applyAsInt(getColumn(i)));
    }
    return mat;
  }

  @Override
  public IntMatrix reduceRows(ToIntFunction<? super IntMatrix> reduce) {
    IntMatrix mat = newEmptyMatrix(rows(), 1);
    for (int i = 0; i < rows(); i++) {
      mat.set(i, reduce.applyAsInt(getRow(i)));
    }
    return mat;
  }

  @Override
  public int hashCode() {
    int result = 1;
    for (int i = 0; i < size(); i++) {
      long bits = get(i);
      result = 31 * result + (int) (bits ^ (bits >>> 32));
    }

    return Objects.hash(rows(), columns(), result);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof IntMatrix) {
      IntMatrix mat = (IntMatrix) obj;
      if (!mat.hasEqualShape(this)) {
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
  public ComplexMatrix asComplexMatrix() {
    return new AsComplexMatrix(getMatrixFactory(), rows(), columns()) {
      @Override
      public void set(int index, Complex value) {
        AbstractIntMatrix.this.set(index, value.intValue());
      }

      @Override
      public void set(int i, int j, Complex value) {
        AbstractIntMatrix.this.set(i, j, value.intValue());
      }

      @Override
      public Complex get(int i, int j) {
        return Complex.valueOf(AbstractIntMatrix.this.get(i, j));
      }

      @Override
      public Complex get(int index) {
        return Complex.valueOf(AbstractIntMatrix.this.get(index));
      }

    };
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    try {
      MatrixPrinter.print(builder, this);
    } catch (IOException e) {
      return getClass().getSimpleName();
    }
    return builder.toString();
  }

  @Override
  public IntMatrix newEmptyVector(int size) {
    return newEmptyMatrix(size, 1);
  }

  protected static class SliceIntMatrix extends AbstractIntMatrix {

    private final Range row, column;
    private final IntMatrix parent;

    public SliceIntMatrix(MatrixFactory bj, IntMatrix parent, Range row, Range column) {
      this(bj, parent, checkNotNull(row).size(), row, checkNotNull(column).size(), column);
    }

    public SliceIntMatrix(MatrixFactory bj, IntMatrix parent, int rows, Range row, int columns,
                          Range column) {
      super(bj, rows, columns);
      this.row = checkNotNull(row);
      this.column = checkNotNull(column);
      this.parent = parent;
    }

    @Override
    public IntMatrix reshape(int rows, int columns) {
      Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
      return new SliceIntMatrix(getMatrixFactory(), parent, rows, row, columns, column);
    }

    @Override
    public int get(int i, int j) {
      return parent.get(sliceIndex(row.step(), i, parent.rows()),
                        sliceIndex(column.step(), j, parent.columns()));
    }

    @Override
    public void set(int i, int j, int value) {
      parent.set(sliceIndex(row.step(), i, parent.rows()),
                 sliceIndex(column.step(), j, parent.columns()), value);
    }

    @Override
    public void set(int index, int value) {
      int row = index % rows();
      int col = index / rows();
      set(row, col, value);
    }


    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public IntMatrix newEmptyMatrix(int rows, int columns) {
      return parent.newEmptyMatrix(rows, columns);
    }

    @Override
    public int get(int index) {
      int row = index % rows();
      int col = index / rows();
      return get(row, col);
    }
  }

  /**
   * Created by Isak Karlsson on 09/01/15.
   */
  public static class IntMatrixView extends AbstractIntMatrix {

    private final int rowOffset, colOffset;
    private final IntMatrix parent;

    public IntMatrixView(MatrixFactory bj, IntMatrix parent, int rowOffset, int colOffset, int rows,
                         int cols) {
      super(bj, rows, cols);
      this.rowOffset = rowOffset;
      this.colOffset = colOffset;
      this.parent = parent;

      checkArgument(rowOffset >= 0 && rowOffset + rows() <= parent.rows(),
                    "Requested row out of bounds.");
      checkArgument(colOffset >= 0 && colOffset + columns() <= parent.columns(),
                    "Requested column out of bounds");
    }

    @Override
    public IntMatrix copy() {
      IntMatrix mat = parent.newEmptyMatrix(rows(), columns());
      for (int i = 0; i < size(); i++) {
        mat.set(i, get(i));
      }
      return mat;
    }

    private int computeLinearIndex(int index) {
      int currentColumn = index / rows() + colOffset;
      int currentRow = index % rows() + rowOffset;
      return columnMajor(currentRow, currentColumn, parent.rows(), parent.columns());
    }

    @Override
    public IntMatrix reshape(int rows, int columns) {
      return new IntMatrixView(getMatrixFactory(), parent, rowOffset, colOffset, rows, columns);
    }


    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public IntMatrix newEmptyMatrix(int rows, int columns) {
      return parent.newEmptyMatrix(rows, columns);
    }

    @Override
    public int get(int i, int j) {
      return parent.get(rowOffset + i, colOffset + j);
    }

    @Override
    public int get(int index) {
      return parent.get(computeLinearIndex(index));
    }


    @Override
    public void set(int i, int j, int value) {
      parent.set(rowOffset + i, colOffset + j, value);
    }

    @Override
    public void set(int index, int value) {
      parent.set(computeLinearIndex(index), value);
    }


  }

  protected class FlatSliceIntMatrix extends AbstractIntMatrix {

    private final IntMatrix parent;
    private final Range range;

    private FlatSliceIntMatrix(MatrixFactory bj, IntMatrix parent, int size, Range range) {
      super(bj, size);
      this.parent = checkNotNull(parent);
      this.range = checkNotNull(range);
    }

    public FlatSliceIntMatrix(MatrixFactory bj, IntMatrix parent, Range range) {
      this(bj, parent, checkNotNull(range).size(), range);
    }

    @Override
    public void set(int i, int j, int value) {
      set(columnMajor(i, j, rows(), columns()), value);
    }

    @Override
    public void set(int index, int value) {
      parent.set(sliceIndex(range.step(), index, parent.size()), value);
    }

    @Override
    public IntMatrix reshape(int rows, int columns) {
      return copy().reshape(rows, columns);
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public IntMatrix newEmptyMatrix(int rows, int columns) {
      return parent.newEmptyMatrix(rows, columns);
    }

    @Override
    public int get(int i, int j) {
      return get(columnMajor(i, j, rows(), columns()));
    }

    @Override
    public int get(int index) {
      return parent.get(sliceIndex(range.step(), index, parent.size()));
    }
  }

  private class IntListView extends AbstractList<Integer> {

    @Override
    public Integer get(int i) {
      return AbstractIntMatrix.this.get(i);
    }

    @Override
    public Integer set(int i, Integer value) {
      int old = AbstractIntMatrix.this.get(i);
      AbstractIntMatrix.this.set(i, value);
      return old;
    }

    @Override
    public Iterator<Integer> iterator() {
      return new Iterator<Integer>() {
        private int index = 0;

        @Override
        public boolean hasNext() {
          return index < size();
        }

        @Override
        public Integer next() {
          return get(index++);
        }
      };
    }

    @Override
    public int size() {
      return AbstractIntMatrix.this.size();
    }
  }

  @Override
  public IntMatrix getRow(int i) {
    return new IntMatrixView(getMatrixFactory(), this, i, 0, 1, columns());
  }

  @Override
  public IntMatrix copy() {
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, get(i));
    }
    return matrix;
  }

  @Override
  public IntMatrix getColumn(int index) {
    return new IntMatrixView(getMatrixFactory(), this, 0, index, rows(), 1);
  }


  @Override
  public IntMatrix getDiagonal() {
    throw new UnsupportedOperationException();
  }


  @Override
  public void addTo(int index, int value) {
    set(index, get(index) + value);
  }

  @Override
  public IntMatrix getView(int rowOffset, int colOffset, int rows, int columns) {
    return new IntMatrixView(getMatrixFactory(), this, rowOffset, colOffset, rows, columns);
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
  public List<Integer> flat() {
    return new IntListView();
  }

  @Override
  public IntMatrix mmul(IntMatrix other) {
    return mmul(1, other);
  }

  @Override
  public IntMatrix slice(Collection<Integer> rows, Collection<Integer> columns) {
    IntMatrix m = newEmptyMatrix(rows.size(), columns.size());
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
  public IntMatrix mmul(int alpha, IntMatrix other) {
    return mmul(alpha, T.NO, other, T.NO);
  }

  @Override
  public IntMatrix mmul(T a, IntMatrix other, T b) {
    return mmul(1, a, other, b);
  }

  @Override
  public IntMatrix mmul(int alpha, T a, IntMatrix other, T b) {
    int thisRows = rows();
    int thisCols = columns();
    if (a == T.YES) {
      thisRows = columns();
      thisCols = rows();
    }
    int otherRows = other.rows();
    int otherColumns = other.columns();
    if (b == T.YES) {
      otherRows = other.columns();
      otherColumns = other.rows();
    }

    if (thisCols != otherRows) {
      throw new NonConformantException(thisRows, thisCols, otherRows, otherColumns);
    }

    IntMatrix result = newEmptyMatrix(thisRows, otherColumns);
    for (int row = 0; row < thisRows; row++) {
      for (int col = 0; col < otherColumns; col++) {
        int sum = 0;
        for (int k = 0; k < thisCols; k++) {
          int thisIndex =
              a == T.YES ? rowMajor(row, k, thisRows, thisCols) : columnMajor(row, k,
                                                                              thisRows,
                                                                              thisCols);
          int otherIndex =
              b == T.YES ? rowMajor(k, col, otherRows, otherColumns) : columnMajor(k, col,
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
  public IntMatrix mul(IntMatrix other) {
    return mul(1, other, 1);
  }

  @Override
  public IntMatrix mul(int alpha, IntMatrix other, int beta) {
    Check.size(this, other);
    IntMatrix m = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        m.set(i, j, alpha * get(i, j) * other.get(i, j) * beta);
      }
    }
    return m;
  }

  @Override
  public IntMatrix mul(IntMatrix other, Dim dim) {
    return mul(1, other, 1, dim);
  }

  @Override
  public IntMatrix slice(Collection<Integer> indexes, Dim dim) {
    IntMatrix matrix;
    if (dim == Dim.R) {
      matrix = newEmptyMatrix(indexes.size(), columns());
      int i = 0;
      for (int index : indexes) {
        matrix.setRow(i++, getRow(index));
      }
    } else {
      matrix = newEmptyMatrix(rows(), indexes.size());
      int i = 0;
      for (int index : indexes) {
        matrix.setColumn(i++, getColumn(index));
      }
    }
    return matrix;
  }

  @Override
  public IntMatrix mul(int alpha, IntMatrix other, int beta, Dim dim) {
    IntMatrix m = newEmptyMatrix(rows(), columns());
    if (dim == Dim.C) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        m.set(i, (alpha * get(i)) * (other.get(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        m.set(i, (alpha * get(i)) * (other.get(i / rows()) * beta));
      }
    }
    return m;
  }

  @Override
  public IntMatrix mul(int scalar) {
    IntMatrix m = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        m.set(i, j, get(i, j) * scalar);
      }
    }
    return m;
  }

  @Override
  public IntMatrix add(IntMatrix other) {
    return add(1, other, 1);
  }

  @Override
  public IntMatrix add(int scalar) {
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, get(i, j) + scalar);
      }
    }
    return matrix;
  }

  @Override
  public IntMatrix add(IntMatrix other, Dim dim) {
    return add(1, other, 1, dim);
  }

  @Override
  public IntMatrix add(int alpha, IntMatrix other, int beta, Dim dim) {
    IntMatrix m = newEmptyMatrix(rows(), columns());
    if (dim == Dim.C) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        m.set(i, (alpha * get(i)) + (other.get(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        m.set(i, (alpha * get(i)) + (other.get(i / rows()) * beta));
      }
    }
    return m;
  }

  @Override
  public IntMatrix slice(Collection<Integer> indexes) {
    Builder builder = new Builder();
    indexes.forEach(index -> builder.add(get(index)));
    return builder.build();
  }

  @Override
  public IntMatrix add(int alpha, IntMatrix other, int beta) {
    Check.size(this, other);
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) + other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  @Override
  public IntMatrix sub(IntMatrix other) {
    return sub(1, other, 1);
  }

  @Override
  public IntMatrix sub(int scalar) {
    return add(-scalar);
  }

  @Override
  public IntMatrix sub(IntMatrix other, Dim dim) {
    return sub(1, other, 1, dim);
  }

  @Override
  public IntMatrix sub(int alpha, IntMatrix other, int beta, Dim dim) {
    IntMatrix m = newEmptyMatrix(rows(), columns());
    if (dim == Dim.C) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        m.set(i, (alpha * get(i)) - (other.get(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        m.set(i, (alpha * get(i)) - (other.get(i / rows()) * beta));
      }
    }
    return m;
  }

  @Override
  public IntMatrix sub(int alpha, IntMatrix other, int beta) {
    Check.size(this, other);
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) - other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  @Override
  public IntMatrix slice(Range rows, Range columns) {
    return new SliceIntMatrix(getMatrixFactory(), this, rows, columns);
  }

  @Override
  public IntMatrix rsub(int scalar) {
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, scalar - get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public IntMatrix rsub(IntMatrix other, Dim dim) {
    return rsub(1, other, 1, dim);
  }

  @Override
  public IntMatrix rsub(int alpha, IntMatrix other, int beta, Dim dim) {
    IntMatrix m = newEmptyMatrix(rows(), columns());
    if (dim == Dim.C) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        m.set(i, (other.get(i % rows()) * beta) - (alpha * get(i)));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        m.set(i, (other.get(i / rows()) * beta) - (alpha * get(i)));
      }
    }
    return m;
  }

  @Override
  public IntMatrix div(IntMatrix other) {
    Check.size(this, other);
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, get(i, j) / other.get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public IntMatrix div(int other) {
    IntMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i) / other);
    }
    return m;
  }

  @Override
  public IntMatrix div(IntMatrix other, Dim dim) {
    return div(1, other, 1, dim);
  }

  @Override
  public IntMatrix slice(Range range) {
    return new FlatSliceIntMatrix(getMatrixFactory(), this, range);
  }

  @Override
  public IntMatrix div(int alpha, IntMatrix other, int beta, Dim dim) {
    IntMatrix m = newEmptyMatrix(rows(), columns());
    if (dim == Dim.C) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        m.set(i, (alpha * get(i)) / (other.get(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        m.set(i, (alpha * get(i)) / (other.get(i / rows()) * beta));
      }
    }
    return m;
  }

  @Override
  public IntMatrix rdiv(int other) {
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, other / get(i));
    }
    return matrix;
  }

  @Override
  public IntMatrix rdiv(IntMatrix other, Dim dim) {
    return rdiv(1, other, 1, dim);
  }

  @Override
  public IntMatrix rdiv(int alpha, IntMatrix other, int beta, Dim dim) {
    IntMatrix m = newEmptyMatrix(rows(), columns());
    if (dim == Dim.C) {
      checkArgument(other.size() == rows());
      for (int i = 0; i < size(); i++) {
        m.set(i, (other.get(i % rows()) * beta) / (alpha * get(i)));
      }
    } else {
      checkArgument(other.size() == columns());
      for (int i = 0; i < size(); i++) {
        m.set(i, (other.get(i / rows()) * beta) / (alpha * get(i)));
      }
    }
    return m;
  }

  @Override
  public IntMatrix negate() {
    IntMatrix n = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      n.set(i, -get(i));
    }
    return n;
  }

  @Override
  public int[] data() {
    int[] array = new int[size()];
    for (int i = 0; i < size(); i++) {
      array[i] = get(i);
    }
    return array;
  }


  @Override
  public IntMatrix slice(Range range, Dim dim) {
    if (dim == Dim.R) {
      return new SliceIntMatrix(getMatrixFactory(), this, range,
                                getMatrixFactory().range(columns()));
    } else {
      return new SliceIntMatrix(getMatrixFactory(), this, getMatrixFactory().range(rows()), range);
    }
  }


  @Override
  public IntMatrix slice(BitMatrix bits) {
    Check.equalShape(this, bits);
    Builder builder = new Builder();
    for (int i = 0; i < size(); i++) {
      if (bits.get(i)) {
        builder.add(get(i));
      }
    }
    return builder.build();
  }


  @Override
  public IntMatrix slice(BitMatrix indexes, Dim dim) {
    int size = Matrices.sum(indexes);
    IntMatrix matrix;
    if (dim == Dim.R) {
      Check.size(rows(), indexes);
      matrix = newEmptyMatrix(size, columns());
      int index = 0;
      for (int i = 0; i < rows(); i++) {
        if (indexes.get(i)) {
          matrix.setRow(index++, getRow(i));
        }
      }
    } else {
      Check.size(columns(), indexes);
      matrix = newEmptyMatrix(rows(), size);
      int index = 0;
      for (int j = 0; j < columns(); j++) {
        if (indexes.get(j)) {
          matrix.setColumn(index++, getColumn(j));
        }
      }
    }
    return matrix;
  }


  @Override
  public IntMatrix transpose() {
    IntMatrix matrix = newEmptyMatrix(this.columns(), this.rows());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(j, i, get(i, j));
      }
    }
    return matrix;
  }

  private class Builder {

    private IntArrayList buffer = new IntArrayList();

    public void add(int value) {
      buffer.add(value);
    }

    public IntMatrix build() {
      return bj.matrix(buffer.toArray());
    }
  }

}
