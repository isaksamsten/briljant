package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.briljantframework.matrix.Indexer.*;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.*;

import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.matrix.storage.Storage;
import org.briljantframework.vector.Vector;

import com.carrotsearch.hppc.LongArrayList;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableTable;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public abstract class AbstractLongMatrix extends AbstractMatrix implements LongMatrix {

  protected AbstractLongMatrix(int size) {
    super(size);
  }

  protected AbstractLongMatrix(int rows, int cols) {
    super(rows, cols);
  }


  @Override
  public boolean getAsBit(int index) {
    return getAsLong(index) == 1;
  }

  @Override
  public boolean getAsBit(int i, int j) {
    return getAsLong(i, j) == 1;
  }

  @Override
  public int getAsInt(int index) {
    return (int) getAsLong(index);
  }

  @Override
  public int getAsInt(int i, int j) {
    return (int) getAsLong(i, j);
  }

  @Override
  public long getAsLong(int index) {
    return get(index);
  }

  @Override
  public long getAsLong(int i, int j) {
    return get(i, j);
  }

  @Override
  public double getAsDouble(int index) {
    return getAsLong(index);
  }

  @Override
  public double getAsDouble(int i, int j) {
    return getAsLong(i, j);
  }

  @Override
  public Complex getAsComplex(int index) {
    return Complex.valueOf(getAsDouble(index));
  }

  @Override
  public Complex getAsComplex(int i, int j) {
    return Complex.valueOf(getAsDouble(i, j));
  }

  @Override
  public LongMatrix copy() {
    LongMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, get(i));
    }
    return matrix;
  }

  @Override
  public long get(int i, int j) {
    return getStorage().getAsLong(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public long get(int index) {
    return getStorage().getAsLong(index);
  }

  @Override
  public void set(int i, int j, long value) {
    getStorage().setLong(Indexer.columnMajor(i, j, rows(), columns()), value);
  }

  @Override
  public void set(int index, long value) {
    getStorage().setLong(index, value);
  }

  @Override
  public void set(int atIndex, Matrix from, int fromIndex) {
    set(atIndex, from.getAsLong(fromIndex));
  }

  @Override
  public void set(int atRow, int atColumn, Matrix from, int fromRow, int fromColumn) {
    set(atRow, atColumn, from.getAsLong(fromRow, fromColumn));
  }

  @Override
  public int compare(int toIndex, Matrix from, int fromIndex) {
    return Long.compare(get(toIndex), from.getAsInt(fromIndex));
  }

  @Override
  public int compare(int toRow, int toColumn, Matrix from, int fromRow, int fromColumn) {
    return Long.compare(get(toRow, toColumn), from.getAsLong(fromRow, fromColumn));
  }

  @Override
  public LongMatrix getRowView(int i) {
    return new LongMatrixView(this, i, 0, 1, columns());
  }

  public LongMatrix getColumnView(int index) {
    return new LongMatrixView(this, 0, index, rows(), 1);
  }

  @Override
  public LongMatrix getDiagonalView() {
    throw new UnsupportedOperationException();
  }

  @Override
  public LongMatrix getView(int rowOffset, int colOffset, int rows, int columns) {
    return new LongMatrixView(this, rowOffset, colOffset, rows, columns);
  }

  @Override
  public Matrix slice(Range rows, Range columns) {
    return new SliceLongMatrix(this, rows, columns);
  }

  @Override
  public Matrix slice(Range range, Axis axis) {
    if (axis == Axis.ROW) {
      return new SliceLongMatrix(this, range, Range.range(columns()));
    } else {
      return new SliceLongMatrix(this, Range.range(rows()), range);
    }
  }

  @Override
  public Matrix slice(Range range) {
    return new FlatSliceLongMatrix(this, range);
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
  public IncrementalBuilder newIncrementalBuilder() {
    return new IncrementalBuilder();
  }

  @Override
  public void swap(int a, int b) {
    long tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  @Override
  public LongMatrix assign(LongSupplier supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.getAsLong());
    }
    return this;
  }

  @Override
  public LongMatrix assign(long value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
    return this;
  }

  @Override
  public LongMatrix assign(Vector vector, Axis axis) {
    return assign(vector, (a, b) -> b, axis);
  }

  @Override
  public LongMatrix assign(Vector other, LongBinaryOperator operator, Axis axis) {
    /*
     * Due to cache-locality, put(i, ) is for most (at least array based) matrices a _big_ win.
     * Therefore, the straightforward implementation using two for-loops is not used below. This is
     * a big win since this.size() >= other.size().
     */
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // set(i, operator.applyAsLong(get(i), other.getAsLong(i % rows())));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // set(i, operator.applyAsLong(get(i), other.getAsLong(i / rows())));
      }
    }
    return this;
  }

  @Override
  public LongMatrix assign(LongMatrix matrix) {
    return assign(matrix, LongUnaryOperator.identity());
  }

  @Override
  public LongMatrix assign(LongMatrix matrix, LongUnaryOperator operator) {
    Check.equalSize(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsLong(matrix.get(i)));
    }
    return this;
  }

  @Override
  public LongMatrix assign(ComplexMatrix matrix, ToLongFunction<? super Complex> function) {
    Preconditions.checkArgument(matrix.size() == size());
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsLong(matrix.get(i)));
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
  public LongMatrix assignStream(Iterable<? extends Number> numbers) {
    int index = 0;
    for (Number number : numbers) {
      set(index++, number.longValue());
    }
    return this;
  }

  @Override
  public <T> LongMatrix assignStream(Iterable<T> iterable, ToLongFunction<? super T> function) {
    int index = 0;
    for (T t : iterable) {
      set(index++, function.applyAsLong(t));
    }
    return this;
  }

  @Override
  public LongMatrix assign(long[] values) {
    checkArgument(size() == values.length);
    for (int i = 0; i < size(); i++) {
      set(i, values[i]);
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
  public LongMatrix mapi(LongUnaryOperator operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsLong(get(i)));
    }
    return this;
  }

  @Override
  public LongMatrix filter(LongPredicate operator) {
    IncrementalBuilder builder = newIncrementalBuilder();
    for (int i = 0; i < size(); i++) {
      long value = get(i);
      if (operator.test(value)) {
        builder.add(value);
      }
    }
    return builder.build();
  }

  @Override
  public long reduce(long identity, LongBinaryOperator reduce, LongUnaryOperator map) {
    for (int i = 0; i < size(); i++) {
      identity = reduce.applyAsLong(identity, map.applyAsLong(get(i)));
    }
    return identity;
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
  public LongMatrix mmul(LongMatrix other) {
    return mmul(1, other, 1);
  }

  @Override
  public LongMatrix mmul(long alpha, LongMatrix other, long beta) {
    return mmul(alpha, Transpose.NO, other, beta, Transpose.NO);
  }

  @Override
  public LongMatrix mmul(Transpose a, LongMatrix other, Transpose b) {
    return mmul(1, a, other, 1, b);
  }

  @Override
  public LongMatrix mmul(long alpha, Transpose a, LongMatrix other, long beta, Transpose b) {
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
                  thisRows, thisCols);
          int otherIndex =
              b == Transpose.YES ? rowMajor(k, col, otherRows, otherColumns) : columnMajor(k, col,
                  otherRows, otherColumns);
          sum += alpha * get(thisIndex) * beta * other.get(otherIndex);
        }
        result.set(row, col, sum);
      }
    }
    return result;
  }

  @Override
  public LongMatrix mul(LongMatrix other) {
    return mul(1, other, 1);
  }

  @Override
  public LongMatrix mul(long alpha, LongMatrix other, long beta) {
    return copy().muli(alpha, other, beta);
  }

  @Override
  public LongMatrix mul(Vector other, Axis axis) {
    return mul(1, other, 1, axis);
  }

  @Override
  public LongMatrix mul(long alpha, Vector other, long beta, Axis axis) {
    return copy().muli(alpha, other, beta, axis);
  }

  @Override
  public LongMatrix mul(long scalar) {
    return copy().muli(scalar);
  }

  @Override
  public LongMatrix muli(LongMatrix other) {
    return muli(1, other, 1);
  }

  @Override
  public LongMatrix muli(long scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        set(i, j, get(i, j) * scalar);
      }
    }
    return this;
  }

  @Override
  public LongMatrix muli(long alpha, LongMatrix other, long beta) {
    Check.equalSize(this, other);
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        set(i, j, alpha * get(i, j) * other.get(i, j) * beta);
      }
    }
    return this;
  }

  @Override
  public LongMatrix muli(Vector other, Axis axis) {
    return muli(1, other, 1, axis);
  }

  @Override
  public LongMatrix muli(long alpha, Vector other, long beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (alpha * get(i)) * (other.getAsLong(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (alpha * get(i)) * (other.getAsLong(i / rows()) * beta));
      }
    }
    return this;
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
  public LongMatrix add(Vector other, Axis axis) {
    return add(1, other, 1, axis);
  }

  @Override
  public LongMatrix add(long alpha, Vector other, long beta, Axis axis) {
    return copy().addi(alpha, other, beta, axis);
  }

  @Override
  public LongMatrix add(long alpha, LongMatrix other, long beta) {
    Check.equalSize(this, other);
    LongMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) + other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  @Override
  public LongMatrix addi(LongMatrix other) {
    addi(1, other, 1);
    return this;
  }

  @Override
  public LongMatrix addi(long scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        this.set(i, j, get(i, j) + scalar);
      }
    }
    return this;
  }

  @Override
  public LongMatrix addi(Vector other, Axis axis) {
    return addi(1, other, 1, axis);
  }

  @Override
  public LongMatrix addi(long alpha, Vector other, long beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (alpha * get(i)) + (other.getAsLong(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (alpha * get(i)) + (other.getAsLong(i / rows()) * beta));
      }
    }
    return this;
  }

  @Override
  public LongMatrix addi(long alpha, LongMatrix other, long beta) {
    Check.equalSize(this, other);
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        set(i, j, alpha * get(i, j) + other.get(i, j) * beta);
      }
    }
    return this;
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
  public LongMatrix sub(Vector other, Axis axis) {
    return sub(1, other, 1, axis);
  }

  @Override
  public LongMatrix sub(long alpha, Vector other, long beta, Axis axis) {
    return copy().subi(alpha, other, beta, axis);
  }

  @Override
  public LongMatrix sub(long alpha, LongMatrix other, long beta) {
    Check.equalSize(this, other);
    LongMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) - other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  @Override
  public LongMatrix subi(LongMatrix other) {
    addi(1, other, -1);
    return this;
  }

  @Override
  public LongMatrix subi(long scalar) {
    addi(-scalar);
    return this;
  }

  @Override
  public LongMatrix subi(Vector other, Axis axis) {
    return subi(1, other, 1, axis);
  }

  @Override
  public LongMatrix subi(long alpha, Vector other, long beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (alpha * get(i)) - (other.getAsLong(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (alpha * get(i)) - (other.getAsLong(i / rows()) * beta));
      }
    }
    return this;
  }

  @Override
  public LongMatrix subi(long alpha, LongMatrix other, long beta) {
    addi(alpha, other, -1 * beta);
    return this;
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
  public LongMatrix rsub(Vector other, Axis axis) {
    return rsub(1, other, 1, axis);
  }

  @Override
  public LongMatrix rsub(long alpha, Vector other, long beta, Axis axis) {
    return copy().rsubi(alpha, other, beta, axis);
  }

  @Override
  public LongMatrix rsubi(long scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        set(i, j, scalar - get(i, j));
      }
    }
    return this;
  }

  @Override
  public LongMatrix rsubi(Vector other, Axis axis) {
    return rsubi(1, other, 1, axis);
  }

  @Override
  public LongMatrix rsubi(long alpha, Vector other, long beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (other.getAsLong(i % rows()) * beta) - (alpha * get(i)));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (other.getAsLong(i / rows()) * beta) - (alpha * get(i)));
      }
    }
    return this;
  }

  @Override
  public LongMatrix div(LongMatrix other) {
    Check.equalSize(this, other);
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
    return mul(1 / other);
  }

  @Override
  public LongMatrix div(Vector other, Axis axis) {
    return div(1, other, 1, axis);
  }

  @Override
  public LongMatrix div(long alpha, Vector other, long beta, Axis axis) {
    return copy().divi(alpha, other, beta, axis);
  }

  @Override
  public LongMatrix divi(LongMatrix other) {
    LongMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      set(i, get(i) / other.get(i));
    }
    return this;
  }

  @Override
  public LongMatrix divi(long other) {
    return muli(1 / other);
  }

  @Override
  public LongMatrix divi(Vector other, Axis axis) {
    return divi(1, other, 1, axis);
  }

  @Override
  public LongMatrix divi(long alpha, Vector other, long beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (alpha * get(i)) / (other.getAsLong(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (alpha * get(i)) / (other.getAsLong(i / rows()) * beta));
      }
    }
    return this;
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
  public LongMatrix rdiv(Vector other, Axis axis) {
    return rdiv(1, other, 1, axis);
  }

  @Override
  public LongMatrix rdiv(long alpha, Vector other, long beta, Axis axis) {
    return copy().rdivi(alpha, other, beta, axis);
  }

  @Override
  public LongMatrix rdivi(long other) {
    for (int i = 0; i < size(); i++) {
      set(i, other / get(i));
    }
    return this;
  }

  @Override
  public LongMatrix rdivi(Vector other, Axis axis) {
    return rdivi(1, other, 1, axis);
  }

  @Override
  public LongMatrix rdivi(long alpha, Vector other, long beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows());
      for (int i = 0; i < size(); i++) {
        // this.set(i, (other.getAsLong(i % rows()) * beta) / (alpha * get(i)));
      }
    } else {
      checkArgument(other.size() == columns());
      for (int i = 0; i < size(); i++) {
        // this.set(i, (other.getAsLong(i / rows()) * beta) / (alpha * get(i)));
      }
    }
    return this;
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

  public static class IncrementalBuilder implements Matrix.IncrementalBuilder {

    private LongArrayList buffer = new LongArrayList();

    @Override
    public void add(Matrix from, int i, int j) {
      buffer.add(from.getAsLong(i, j));
    }

    @Override
    public void add(Matrix from, int index) {
      buffer.add(from.getAsLong(index));
    }

    @Override
    public LongMatrix build() {
      return null; // TODO
    }

    public void add(long value) {
      buffer.add(value);
    }
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
    public LongMatrix reshape(int rows, int columns) {
      Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
      return new SliceLongMatrix(parent, rows, row, columns, column);
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
      return parent.get(sliceIndex(row.step(), i, parent.rows()),
          sliceIndex(column.step(), j, parent.columns()));
    }

    @Override
    public long get(int index) {
      int row = index % rows();
      int col = index / rows();
      return get(row, col);
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
}
