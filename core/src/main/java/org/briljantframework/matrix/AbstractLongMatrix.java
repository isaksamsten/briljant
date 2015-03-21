package org.briljantframework.matrix;

import com.google.common.collect.ImmutableTable;

import com.carrotsearch.hppc.LongArrayList;

import org.briljantframework.Check;
import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.function.LongBiPredicate;
import org.briljantframework.matrix.storage.LongStorage;
import org.briljantframework.matrix.storage.Storage;

import java.util.AbstractList;
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.briljantframework.matrix.Indexer.columnMajor;
import static org.briljantframework.matrix.Indexer.rowMajor;
import static org.briljantframework.matrix.Indexer.sliceIndex;
import static org.briljantframework.matrix.Matrices.sum;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractLongMatrix extends AbstractMatrix<LongMatrix> implements LongMatrix {

  private LongListView listView = null;

  protected AbstractLongMatrix(int size) {
    super(size);
  }

  protected AbstractLongMatrix(int rows, int cols) {
    super(rows, cols);
  }

  @Override
  public void swap(int a, int b) {
    long tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  @Override
  public void set(int toIndex, LongMatrix from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void set(int toRow, int toColumn, LongMatrix from, int fromRow, int fromColumn) {
    set(toRow, toColumn, from.get(fromRow, fromColumn));
  }

  @Override
  public int compare(int a, int b) {
    return Long.compare(get(a), get(b));
  }

  @Override
  public LongMatrix assign(long value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
    return this;
  }

  @Override
  public LongMatrix assign(LongSupplier supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.getAsLong());
    }
    return this;
  }

  @Override
  public LongMatrix getRowView(int i) {
    return new LongMatrixView(this, i, 0, 1, columns());
  }

  @Override
  public LongMatrix assign(LongMatrix matrix) {
    return assign(matrix, LongUnaryOperator.identity());
  }

  @Override
  public LongMatrix assign(LongMatrix matrix, LongUnaryOperator operator) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsLong(matrix.get(i)));
    }
    return this;
  }

  @Override
  public LongMatrix assign(LongMatrix matrix, LongBinaryOperator combine) {
    Check.equalShape(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, combine.applyAsLong(get(i), matrix.get(i)));
    }
    return this;
  }

  public LongMatrix getColumnView(int index) {
    return new LongMatrixView(this, 0, index, rows(), 1);
  }

  @Override
  public LongMatrix assign(ComplexMatrix matrix, ToLongFunction<? super Complex> function) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsLong(matrix.get(i)));
    }
    return this;
  }

  @Override
  public LongMatrix assign(IntMatrix matrix, IntToLongFunction operator) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsLong(matrix.get(i)));
    }
    return this;
  }

  @Override
  public LongMatrix assign(DoubleMatrix matrix, DoubleToLongFunction function) {
    for (int i = 0; i < matrix.size(); i++) {
      set(i, function.applyAsLong(matrix.get(i)));
    }
    return this;
  }

  @Override
  public LongMatrix getDiagonalView() {
    throw new UnsupportedOperationException();
  }

  @Override
  public LongMatrix update(LongUnaryOperator operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsLong(get(i)));
    }
    return this;
  }

  @Override
  public LongMatrix map(LongUnaryOperator operator) {
    LongMatrix mat = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      mat.set(i, operator.applyAsLong(get(i)));
    }
    return mat;
  }

  @Override
  public IntMatrix mapToInt(LongToIntFunction map) {
    IntMatrix matrix = IntMatrix.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, map.applyAsInt(get(i)));
    }
    return matrix;
  }

  @Override
  public LongMatrix getView(int rowOffset, int colOffset, int rows, int columns) {
    return new LongMatrixView(this, rowOffset, colOffset, rows, columns);
  }

  @Override
  public DoubleMatrix mapToDouble(LongToDoubleFunction map) {
    DoubleMatrix matrix = DoubleMatrix.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, map.applyAsDouble(get(i)));
    }
    return matrix;
  }

  @Override
  public ComplexMatrix mapToComplex(LongFunction<Complex> map) {
    ComplexMatrix matrix = Matrices.newComplexMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, map.apply(get(i)));
    }
    return matrix;
  }

  @Override
  public BitMatrix satisfies(LongPredicate predicate) {
    BitMatrix bits = BitMatrix.newBitMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i)));
    }
    return bits;
  }

  @Override
  public LongMatrix slice(Range rows, Range columns) {
    return new SliceLongMatrix(this, rows, columns);
  }

  @Override
  public BitMatrix satisfies(LongMatrix matrix, LongBiPredicate predicate) {
    Check.equalShape(this, matrix);
    BitMatrix bits = BitMatrix.newBitMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i), matrix.get(i)));
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
  public LongMatrix slice(Range range) {
    return new FlatSliceLongMatrix(this, range);
  }

  @Override
  public LongMatrix reduceColumns(ToLongFunction<? super LongMatrix> reduce) {
    LongMatrix mat = newEmptyMatrix(1, columns());
    for (int i = 0; i < columns(); i++) {
      mat.set(i, reduce.applyAsLong(getColumnView(i)));
    }
    return mat;
  }

  @Override
  public LongMatrix reduceRows(ToLongFunction<? super LongMatrix> reduce) {
    LongMatrix mat = newEmptyMatrix(rows(), 1);
    for (int i = 0; i < rows(); i++) {
      mat.set(i, reduce.applyAsLong(getRowView(i)));
    }
    return mat;
  }

  @Override
  public LongMatrix filter(LongPredicate operator) {
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
  public LongMatrix slice(Range range, Axis axis) {
    if (axis == Axis.ROW) {
      return new SliceLongMatrix(this, range, Range.range(columns()));
    } else {
      return new SliceLongMatrix(this, Range.range(rows()), range);
    }
  }

  @Override
  public long get(int i, int j) {
    return getStorage().getLong(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public long get(int index) {
    return getStorage().getLong(index);
  }

  @Override
  public void set(int index, long value) {
    getStorage().setLong(index, value);
  }

  @Override
  public LongMatrix slice(Collection<Integer> rows, Collection<Integer> columns) {
    LongMatrix m = newEmptyMatrix(rows.size(), columns.size());
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
  public void set(int i, int j, long value) {
    getStorage().setLong(Indexer.columnMajor(i, j, rows(), columns()), value);
  }

  @Override
  public void setRow(int index, LongMatrix row) {
    Check.size(columns(), row);
    for (int j = 0; j < columns(); j++) {
      set(index, j, row.get(j));
    }
  }

  @Override
  public void setColumn(int index, LongMatrix column) {
    Check.size(rows(), column);
    for (int i = 0; i < rows(); i++) {
      set(i, index, column.get(i));
    }
  }

  @Override
  public LongMatrix slice(Collection<Integer> indexes) {
    IncrementalBuilder builder = new IncrementalBuilder();
    indexes.forEach(index -> builder.add(get(index)));
    return builder.build();
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
    if (this.listView == null) {
      this.listView = new LongListView();
    }
    return this.listView;
  }

  @Override
  public LongMatrix mmul(LongMatrix other) {
    return mmul(1, other);
  }

  @Override
  public LongMatrix slice(Collection<Integer> indexes, Axis axis) {
    LongMatrix matrix;
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
  public LongMatrix mmul(long alpha, LongMatrix other) {
    return mmul(alpha, Transpose.NO, other, Transpose.NO);
  }

  @Override
  public LongMatrix mmul(Transpose a, LongMatrix other, Transpose b) {
    return mmul(1, a, other, b);
  }

  @Override
  public LongMatrix mmul(long alpha, Transpose a, LongMatrix other, Transpose b) {
    int thisRows = rows();
    int thisCols = columns();
    if (a == Transpose.YES) {
      thisRows = columns();
      thisCols = rows();
    }
    int otherRows = other.rows();
    int otherColumns = other.columns();
    if (b == Transpose.YES) {
      otherRows = other.columns();
      otherColumns = other.rows();
    }

    if (thisCols != otherRows) {
      throw new NonConformantException(thisRows, thisCols, otherRows, otherColumns);
    }

    LongMatrix result = newEmptyMatrix(thisRows, otherColumns);
    for (int row = 0; row < thisRows; row++) {
      for (int col = 0; col < otherColumns; col++) {
        long sum = 0;
        for (int k = 0; k < thisCols; k++) {
          int thisIndex =
              a == Transpose.YES ? rowMajor(row, k, thisRows, thisCols) : columnMajor(row, k,
                                                                                      thisRows,
                                                                                      thisCols);
          int otherIndex =
              b == Transpose.YES ? rowMajor(k, col, otherRows, otherColumns) : columnMajor(k, col,
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
  public LongMatrix slice(BitMatrix bits) {
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
  public LongMatrix mul(LongMatrix other) {
    return mul(1, other, 1);
  }

  @Override
  public LongMatrix mul(long alpha, LongMatrix other, long beta) {
    Check.size(this, other);
    LongMatrix m = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        m.set(i, j, alpha * get(i, j) * other.get(i, j) * beta);
      }
    }
    return m;
  }

  @Override
  public LongMatrix mul(LongMatrix other, Axis axis) {
    return mul(1, other, 1, axis);
  }

  @Override
  public LongMatrix slice(BitMatrix indexes, Axis axis) {
    int size = sum(indexes);
    LongMatrix matrix;
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
  public LongMatrix mul(long alpha, LongMatrix other, long beta, Axis axis) {
    LongMatrix m = newEmptyMatrix(rows(), columns());
    if (axis == Axis.COLUMN) {
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
  public LongMatrix mul(long scalar) {
    LongMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i) * scalar);
    }
    return m;
  }

  @Override
  public LongMatrix add(LongMatrix other) {
    return add(1, other, 1);
  }

  @Override
  public LongMatrix add(long scalar) {
    LongMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, get(i, j) + scalar);
      }
    }
    return matrix;
  }

  @Override
  public LongMatrix add(LongMatrix other, Axis axis) {
    return add(1, other, 1, axis);
  }

  @Override
  public LongMatrix add(long alpha, LongMatrix other, long beta, Axis axis) {
    LongMatrix m = newEmptyMatrix(rows(), columns());
    if (axis == Axis.COLUMN) {
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
  public LongMatrix add(long alpha, LongMatrix other, long beta) {
    Check.size(this, other);
    LongMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) + other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  @Override
  public LongMatrix sub(LongMatrix other) {
    return sub(1, other, 1);
  }

  @Override
  public LongMatrix sub(long scalar) {
    return add(-scalar);
  }

  @Override
  public LongMatrix sub(LongMatrix other, Axis axis) {
    return sub(1, other, 1, axis);
  }

  @Override
  public LongMatrix sub(long alpha, LongMatrix other, long beta, Axis axis) {
    LongMatrix m = newEmptyMatrix(rows(), columns());
    if (axis == Axis.COLUMN) {
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
  public LongMatrix sub(long alpha, LongMatrix other, long beta) {
    Check.size(this, other);
    LongMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) - other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  @Override
  public LongMatrix rsub(long scalar) {
    LongMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, scalar - get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public LongMatrix rsub(LongMatrix other, Axis axis) {
    return rsub(1, other, 1, axis);
  }

  @Override
  public LongMatrix rsub(long alpha, LongMatrix other, long beta, Axis axis) {
    LongMatrix m = newEmptyMatrix(rows(), columns());
    if (axis == Axis.COLUMN) {
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
  public LongMatrix div(LongMatrix other) {
    Check.size(this, other);
    LongMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, get(i, j) / other.get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public LongMatrix div(long other) {
    LongMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i) / other);
    }
    return m;
  }

  @Override
  public LongMatrix div(LongMatrix other, Axis axis) {
    return div(1, other, 1, axis);
  }

  @Override
  public LongMatrix div(long alpha, LongMatrix other, long beta, Axis axis) {
    LongMatrix m = newEmptyMatrix(rows(), columns());
    if (axis == Axis.COLUMN) {
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
  public LongMatrix rdiv(long other) {
    LongMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, other / get(i));
    }
    return matrix;
  }

  @Override
  public LongMatrix rdiv(LongMatrix other, Axis axis) {
    return rdiv(1, other, 1, axis);
  }

  @Override
  public LongMatrix rdiv(long alpha, LongMatrix other, long beta, Axis axis) {
    LongMatrix m = newEmptyMatrix(rows(), columns());
    if (axis == Axis.COLUMN) {
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
  public LongMatrix negate() {
    LongMatrix n = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      n.set(i, -get(i));
    }
    return n;
  }

  @Override
  public LongMatrix newEmptyVector(int size) {
    return newEmptyMatrix(size, 1);
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
    if (obj instanceof LongMatrix) {
      LongMatrix mat = (LongMatrix) obj;
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
  public LongMatrix transpose() {
    LongMatrix matrix = newEmptyMatrix(this.columns(), this.rows());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(j, i, get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public String toString() {
    ImmutableTable.Builder<Integer, Integer, String> builder = ImmutableTable.builder();
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        if (get(i, j) < 0) {
          builder.put(i, j, String.format("%d", get(i, j)));
        } else {
          builder.put(i, j, String.format(" %d", get(i, j)));
        }
      }
    }
    StringBuilder out = new StringBuilder();
    Utils.prettyPrintTable(out, builder.build(), 0, 2, false, false);
    out.append("shape: ").append(getShape()).append(" type: long");
    return out.toString();
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

  private static class IncrementalBuilder {

    private LongArrayList buffer = new LongArrayList();

    public LongMatrix build() {
      return new DefaultLongMatrix(new LongStorage(buffer.toArray()));
    }

    public void add(long value) {
      buffer.add(value);
    }
  }

  @Override
  public LongMatrix copy() {
    LongMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, get(i));
    }
    return matrix;
  }

  protected static class SliceLongMatrix extends AbstractLongMatrix {

    private final Range row, column;
    private final LongMatrix parent;

    public SliceLongMatrix(LongMatrix parent, Range row, Range column) {
      this(parent, checkNotNull(row).size(), row, checkNotNull(column).size(), column);
    }

    public SliceLongMatrix(LongMatrix parent, int rows, Range row, int columns, Range column) {
      super(rows, columns);
      this.row = checkNotNull(row);
      this.column = checkNotNull(column);
      this.parent = parent;
    }

    @Override
    public long get(int i, int j) {
      return parent.get(sliceIndex(row.step(), i, parent.rows()),
                        sliceIndex(column.step(), j, parent.columns()));
    }

    @Override
    public LongMatrix reshape(int rows, int columns) {
      Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
      return new SliceLongMatrix(parent, rows, row, columns, column);
    }


    @Override
    public void set(int i, int j, long value) {
      parent.set(sliceIndex(row.step(), i, parent.rows()),
                 sliceIndex(column.step(), j, parent.columns()), value);
    }

    @Override
    public void set(int index, long value) {
      int row = index % rows();
      int col = index / rows();
      set(row, col, value);
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
    public LongMatrix newEmptyMatrix(int rows, int columns) {
      return parent.newEmptyMatrix(rows, columns);
    }


    @Override
    public long get(int index) {
      int row = index % rows();
      int col = index / rows();
      return get(row, col);
    }
  }

  public static class LongMatrixView extends AbstractLongMatrix {

    private final int rowOffset, colOffset;
    private final LongMatrix parent;

    public LongMatrixView(LongMatrix parent, int rowOffset, int colOffset, int rows, int cols) {
      super(rows, cols);
      this.rowOffset = rowOffset;
      this.colOffset = colOffset;
      this.parent = parent;

      checkArgument(rowOffset >= 0 && rowOffset + rows() <= parent.rows(),
                    "Requested row out of bounds.");
      checkArgument(colOffset >= 0 && colOffset + columns() <= parent.columns(),
                    "Requested column out of bounds");
    }

    private int computeLinearIndex(int index) {
      int currentColumn = index / rows() + colOffset;
      int currentRow = index % rows() + rowOffset;
      return columnMajor(currentRow, currentColumn, parent.rows(), parent.columns());
    }

    @Override
    public LongMatrix reshape(int rows, int columns) {
      return new LongMatrixView(parent, rowOffset, colOffset, rows, columns);
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
    public LongMatrix newEmptyMatrix(int rows, int columns) {
      return null;
    }

    @Override
    public long get(int i, int j) {
      return parent.get(rowOffset + i, colOffset + j);
    }

    @Override
    public long get(int index) {
      return parent.get(computeLinearIndex(index));
    }

    @Override
    public void set(int i, int j, long value) {
      parent.set(rowOffset + i, colOffset + j, value);
    }

    @Override
    public void set(int index, long value) {
      parent.set(computeLinearIndex(index), value);
    }


  }

  protected class FlatSliceLongMatrix extends AbstractLongMatrix {

    private final LongMatrix parent;
    private final Range range;

    private FlatSliceLongMatrix(LongMatrix parent, int size, Range range) {
      super(size);
      this.parent = checkNotNull(parent);
      this.range = checkNotNull(range);
    }

    public FlatSliceLongMatrix(LongMatrix parent, Range range) {
      this(parent, checkNotNull(range).size(), range);
    }

    @Override
    public void set(int i, int j, long value) {
      set(columnMajor(i, j, rows(), columns()), value);
    }

    @Override
    public void set(int index, long value) {
      parent.set(sliceIndex(range.step(), index, parent.size()), value);
    }

    @Override
    public LongMatrix reshape(int rows, int columns) {
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
    public LongMatrix newEmptyMatrix(int rows, int columns) {
      return parent.newEmptyMatrix(rows, columns);
    }

    @Override
    public long get(int i, int j) {
      return get(columnMajor(i, j, rows(), columns()));
    }

    @Override
    public long get(int index) {
      return parent.get(sliceIndex(range.step(), index, parent.size()));
    }
  }

  private class LongListView extends AbstractList<Long> {

    @Override
    public Long get(int i) {
      return AbstractLongMatrix.this.get(i);
    }

    @Override
    public Long set(int i, Long value) {
      long old = AbstractLongMatrix.this.get(i);
      AbstractLongMatrix.this.set(i, value);
      return old;
    }

    @Override
    public int size() {
      return AbstractLongMatrix.this.size();
    }
  }


  @Override
  public LongMatrix newEmptyMatrix(int rows, int columns) {
    return null;
  }






















}
