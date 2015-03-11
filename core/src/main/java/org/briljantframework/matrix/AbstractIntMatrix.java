package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.briljantframework.matrix.Indexer.columnMajor;
import static org.briljantframework.matrix.Indexer.rowMajor;
import static org.briljantframework.matrix.Indexer.sliceIndex;

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

import org.briljantframework.Check;
import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.function.IntBiPredicate;
import org.briljantframework.function.ToIntIntObjBiFunction;
import org.briljantframework.matrix.storage.Storage;

import com.google.common.collect.ImmutableTable;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public abstract class AbstractIntMatrix extends AbstractMatrix<IntMatrix> implements IntMatrix {

  private IntListView listView = null;

  protected AbstractIntMatrix(int size) {
    super(size);
  }

  protected AbstractIntMatrix(int rows, int cols) {
    super(rows, cols);
  }

  @Override
  public IntMatrix getRowView(int i) {
    return new IntMatrixView(this, i, 0, 1, columns());
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
  public IntMatrix getColumnView(int index) {
    return new IntMatrixView(this, 0, index, rows(), 1);
  }

  @Override
  public IntMatrix getDiagonalView() {
    throw new UnsupportedOperationException();
  }

  @Override
  public IntMatrix getView(int rowOffset, int colOffset, int rows, int columns) {
    return new IntMatrixView(this, rowOffset, colOffset, rows, columns);
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
  public IntMatrix slice(Collection<Integer> indexes, Axis axis) {
    IntMatrix matrix;
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
  public IntMatrix slice(Collection<Integer> indexes) {
    Builder builder = new Builder();
    indexes.forEach(index -> builder.add(get(index)));
    return builder.build();
  }

  @Override
  public IntMatrix slice(Range rows, Range columns) {
    return new SliceIntMatrix(this, rows, columns);
  }

  @Override
  public IntMatrix slice(Range range) {
    return new FlatSliceIntMatrix(this, range);
  }

  @Override
  public IntMatrix slice(Range range, Axis axis) {
    if (axis == Axis.ROW) {
      return new SliceIntMatrix(this, range, Range.range(columns()));
    } else {
      return new SliceIntMatrix(this, Range.range(rows()), range);
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
  public IntMatrix slice(BitMatrix indexes, Axis axis) {
    int size = Matrices.sum(indexes);
    IntMatrix matrix;
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
  public IntStream stream() {
    PrimitiveIterator.OfInt ofInt = new PrimitiveIterator.OfInt() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public int nextInt() {
        return get(current++);
      }
    };
    Spliterator.OfInt spliterator = Spliterators.spliterator(ofInt, size(), Spliterator.SIZED);
    return StreamSupport.intStream(spliterator, false);
  }

  @Override
  public List<Integer> flat() {
    if (listView == null) {
      listView = new IntListView();
    }
    return listView;
  }

  @Override
  public void swap(int a, int b) {
    int tmp = get(a);
    set(a, get(b));
    set(b, tmp);
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
  public int get(int i, int j) {
    return getStorage().getInt(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public int get(int index) {
    return getStorage().getInt(index);
  }

  @Override
  public void set(int index, int value) {
    getStorage().setInt(index, value);
  }

  @Override
  public void set(int i, int j, int value) {
    getStorage().setInt(Indexer.columnMajor(i, j, rows(), columns()), value);
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
  public IntMatrix assign(int value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
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
  public IntMatrix update(IntUnaryOperator operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsInt(get(i)));
    }
    return this;
  }

  @Override
  public IntMatrix assign(IntMatrix matrix) {
    return assign(matrix, IntUnaryOperator.identity());
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
  public IntMatrix assign(BitMatrix matrix, ToIntIntObjBiFunction<Boolean> function) {
    Check.equalShape(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsInt(matrix.get(i), get(i)));
    }
    return this;
  }

  @Override
  public LongMatrix mapToLong(IntToLongFunction function) {
    LongMatrix matrix = Matrices.newLongMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsLong(get(i)));
    }
    return matrix;
  }

  @Override
  public DoubleMatrix mapToDouble(IntToDoubleFunction function) {
    DoubleMatrix matrix = DoubleMatrix.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.applyAsDouble(get(i)));
    }
    return matrix;
  }

  @Override
  public ComplexMatrix mapToComplex(IntFunction<Complex> function) {
    ComplexMatrix matrix = Matrices.newComplexMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, function.apply(get(i)));
    }
    return matrix;
  }

  @Override
  public int reduce(int identity, IntBinaryOperator reduce) {
    return reduce(identity, reduce, IntUnaryOperator.identity());
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
    BitMatrix bits = Matrices.newBitMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i)));
    }
    return bits;
  }

  @Override
  public BitMatrix satisfies(IntMatrix matrix, IntBiPredicate predicate) {
    Check.equalShape(this, matrix);
    BitMatrix bits = Matrices.newBitMatrix(rows(), columns());
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
  public int reduce(int identity, IntBinaryOperator reduce, IntUnaryOperator map) {
    for (int i = 0; i < size(); i++) {
      identity = reduce.applyAsInt(identity, map.applyAsInt(get(i)));
    }
    return identity;
  }

  @Override
  public IntMatrix reduceColumns(ToIntFunction<? super IntMatrix> reduce) {
    IntMatrix mat = newEmptyMatrix(1, columns());
    for (int i = 0; i < columns(); i++) {
      mat.set(i, reduce.applyAsInt(getColumnView(i)));
    }
    return mat;
  }

  @Override
  public IntMatrix reduceRows(ToIntFunction<? super IntMatrix> reduce) {
    IntMatrix mat = newEmptyMatrix(rows(), 1);
    for (int i = 0; i < rows(); i++) {
      mat.set(i, reduce.applyAsInt(getRowView(i)));
    }
    return mat;
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
    out.append("shape: ").append(getShape()).append(" type: int");
    return out.toString();
  }


  @Override
  public IntMatrix mmul(IntMatrix other) {
    return mmul(1, other);
  }

  @Override
  public IntMatrix mmul(int alpha, IntMatrix other) {
    return mmul(alpha, Transpose.NO, other, Transpose.NO);
  }

  @Override
  public IntMatrix mmul(Transpose a, IntMatrix other, Transpose b) {
    return mmul(1, a, other, b);
  }

  @Override
  public IntMatrix mmul(int alpha, Transpose a, IntMatrix other, Transpose b) {
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

    IntMatrix result = newEmptyMatrix(thisRows, otherColumns);
    for (int row = 0; row < thisRows; row++) {
      for (int col = 0; col < otherColumns; col++) {
        int sum = 0;
        for (int k = 0; k < thisCols; k++) {
          int thisIndex =
              a == Transpose.YES ? rowMajor(row, k, thisRows, thisCols) : columnMajor(row, k,
                  thisRows, thisCols);
          int otherIndex =
              b == Transpose.YES ? rowMajor(k, col, otherRows, otherColumns) : columnMajor(k, col,
                  otherRows, otherColumns);
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
  public IntMatrix mul(IntMatrix other, Axis axis) {
    return mul(1, other, 1, axis);
  }

  @Override
  public IntMatrix mul(int alpha, IntMatrix other, int beta, Axis axis) {
    IntMatrix m = newEmptyMatrix(rows(), columns());
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
  public IntMatrix add(IntMatrix other, Axis axis) {
    return add(1, other, 1, axis);
  }

  @Override
  public IntMatrix add(int alpha, IntMatrix other, int beta, Axis axis) {
    IntMatrix m = newEmptyMatrix(rows(), columns());
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
  public IntMatrix sub(IntMatrix other, Axis axis) {
    return sub(1, other, 1, axis);
  }

  @Override
  public IntMatrix sub(int alpha, IntMatrix other, int beta, Axis axis) {
    IntMatrix m = newEmptyMatrix(rows(), columns());
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
  public IntMatrix rsub(IntMatrix other, Axis axis) {
    return rsub(1, other, 1, axis);
  }

  @Override
  public IntMatrix rsub(int alpha, IntMatrix other, int beta, Axis axis) {
    IntMatrix m = newEmptyMatrix(rows(), columns());
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
  public IntMatrix div(IntMatrix other, Axis axis) {
    return div(1, other, 1, axis);
  }

  @Override
  public IntMatrix div(int alpha, IntMatrix other, int beta, Axis axis) {
    IntMatrix m = newEmptyMatrix(rows(), columns());
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
  public IntMatrix rdiv(int other) {
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, other / get(i));
    }
    return matrix;
  }

  @Override
  public IntMatrix rdiv(IntMatrix other, Axis axis) {
    return rdiv(1, other, 1, axis);
  }

  @Override
  public IntMatrix rdiv(int alpha, IntMatrix other, int beta, Axis axis) {
    IntMatrix m = newEmptyMatrix(rows(), columns());
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
  public IntMatrix negate() {
    IntMatrix n = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      n.set(i, -get(i));
    }
    return n;
  }

  @Override
  public IntMatrix newEmptyVector(int size) {
    return newEmptyMatrix(size, 1);
  }

  protected static class SliceIntMatrix extends AbstractIntMatrix {

    private final Range row, column;
    private final IntMatrix parent;

    public SliceIntMatrix(IntMatrix parent, Range row, Range column) {
      this(parent, checkNotNull(row).size(), row, checkNotNull(column).size(), column);
    }

    public SliceIntMatrix(IntMatrix parent, int rows, Range row, int columns, Range column) {
      super(rows, columns);
      this.row = checkNotNull(row);
      this.column = checkNotNull(column);
      this.parent = parent;
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
    public IntMatrix reshape(int rows, int columns) {
      Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
      return new SliceIntMatrix(parent, rows, row, columns, column);
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

    public IntMatrixView(IntMatrix parent, int rowOffset, int colOffset, int rows, int cols) {
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
    public IntMatrix reshape(int rows, int columns) {
      return new IntMatrixView(parent, rowOffset, colOffset, rows, columns);
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
    public IntMatrix copy() {
      IntMatrix mat = parent.newEmptyMatrix(rows(), columns());
      for (int i = 0; i < size(); i++) {
        mat.set(i, get(i));
      }
      return mat;
    }

    @Override
    public void set(int i, int j, int value) {
      parent.set(rowOffset + i, colOffset + j, value);
    }

    @Override
    public void set(int index, int value) {
      parent.set(computeLinearIndex(index), value);
    }

    private int computeLinearIndex(int index) {
      int currentColumn = index / rows() + colOffset;
      int currentRow = index % rows() + rowOffset;
      return columnMajor(currentRow, currentColumn, parent.rows(), parent.columns());
    }
  }

  protected class FlatSliceIntMatrix extends AbstractIntMatrix {
    private final IntMatrix parent;
    private final Range range;

    private FlatSliceIntMatrix(IntMatrix parent, int size, Range range) {
      super(size);
      this.parent = checkNotNull(parent);
      this.range = checkNotNull(range);
    }

    public FlatSliceIntMatrix(IntMatrix parent, Range range) {
      this(parent, checkNotNull(range).size(), range);
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
    public Storage getStorage() {
      return parent.getStorage();
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
}
