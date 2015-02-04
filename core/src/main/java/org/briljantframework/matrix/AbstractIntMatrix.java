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
import org.briljantframework.function.IntBiPredicate;
import org.briljantframework.matrix.storage.IntStorage;
import org.briljantframework.matrix.storage.Storage;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.collect.ImmutableTable;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public abstract class AbstractIntMatrix extends AbstractMatrix implements IntMatrix {

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
  public Matrix slice(IntMatrix rows, IntMatrix columns) {
    return null;
  }

  @Override
  public Matrix slice(IntMatrix indexes) {
    return null;
  }

  @Override
  public Matrix slice(IntMatrix indexes, Axis axis) {
    return null;
  }

  @Override
  public Matrix slice(BitMatrix bits) {
    return null;
  }

  @Override
  public Matrix slice(Slice rows, Slice columns) {
    return new SliceIntMatrix(this, rows, columns);
  }

  @Override
  public Matrix slice(Slice slice) {
    return new FlatSliceIntMatrix(this, slice);
  }

  @Override
  public Matrix slice(Slice slice, Axis axis) {
    if (axis == Axis.ROW) {
      return new SliceIntMatrix(this, slice, Slice.slice(columns()));
    } else {
      return new SliceIntMatrix(this, Slice.slice(rows()), slice);
    }
  }

  @Override
  public void swap(int a, int b) {
    int tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  @Override
  public int get(int i, int j) {
    return getStorage().getAsInt(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public int get(int index) {
    return getStorage().getAsInt(index);
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
  public IntMatrix assign(IntUnaryOperator operator) {
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
    Check.equalSize(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsInt(matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntMatrix assign(ComplexMatrix matrix, ToIntFunction<? super Complex> function) {
    Check.equalSize(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsInt(matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntMatrix assign(DoubleMatrix matrix, DoubleToIntFunction function) {
    Check.equalSize(this, matrix);
    for (int i = 0; i < matrix.size(); i++) {
      set(i, function.applyAsInt(matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntMatrix assign(LongMatrix matrix, LongToIntFunction operator) {
    Check.equalSize(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsInt(matrix.get(i)));
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
    DoubleMatrix matrix = Matrices.newDoubleMatrix(rows(), columns());
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
    IncrementalBuilder builder = new IncrementalBuilder();
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
  public IntMatrix mmul(IntMatrix other) {
    return mmul(1, other, 1);
  }

  @Override
  public IntMatrix mmul(int alpha, IntMatrix other, int beta) {
    return mmul(alpha, Transpose.NO, other, beta, Transpose.NO);
  }

  @Override
  public IntMatrix mmul(Transpose a, IntMatrix other, Transpose b) {
    return mmul(1, a, other, 1, b);
  }

  @Override
  public IntMatrix mmul(int alpha, Transpose a, IntMatrix other, int beta, Transpose b) {
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
          sum += alpha * get(thisIndex) * beta * other.get(otherIndex);
        }
        result.set(row, col, sum);
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
    return copy().muli(alpha, other, beta);
  }

  @Override
  public IntMatrix mul(IntMatrix other, Axis axis) {
    return mul(1, other, 1, axis);
  }

  @Override
  public IntMatrix mul(int alpha, IntMatrix other, int beta, Axis axis) {
    return copy().muli(alpha, other, beta, axis);
  }

  @Override
  public IntMatrix mul(int scalar) {
    return copy().muli(scalar);
  }

  @Override
  public IntMatrix muli(IntMatrix other) {
    return muli(1, other, 1);
  }

  @Override
  public IntMatrix muli(int scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        set(i, j, get(i, j) * scalar);
      }
    }
    return this;
  }

  @Override
  public IntMatrix muli(int alpha, IntMatrix other, int beta) {
    Check.equalSize(this, other);
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        set(i, j, alpha * get(i, j) * other.get(i, j) * beta);
      }
    }
    return this;
  }

  @Override
  public IntMatrix muli(IntMatrix other, Axis axis) {
    return muli(1, other, 1, axis);
  }

  @Override
  public IntMatrix muli(int alpha, IntMatrix other, int beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (alpha * get(i)) * (other.getAsInt(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (alpha * get(i)) * (other.getAsInt(i / rows()) * beta));
      }
    }
    return this;
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
    return copy().addi(alpha, other, beta, axis);
  }

  @Override
  public IntMatrix add(int alpha, IntMatrix other, int beta) {
    Check.equalSize(this, other);
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) + other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  @Override
  public IntMatrix addi(IntMatrix other) {
    addi(1, other, 1);
    return this;
  }

  @Override
  public IntMatrix addi(int scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        this.set(i, j, get(i, j) + scalar);
      }
    }
    return this;
  }

  @Override
  public IntMatrix addi(IntMatrix other, Axis axis) {
    return addi(1, other, 1, axis);
  }

  @Override
  public IntMatrix addi(int alpha, IntMatrix other, int beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (alpha * get(i)) + (other.getAsInt(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (alpha * get(i)) + (other.getAsInt(i / rows()) * beta));
      }
    }
    return this;
  }

  @Override
  public IntMatrix addi(int alpha, IntMatrix other, int beta) {
    Check.equalSize(this, other);
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        set(i, j, alpha * get(i, j) + other.get(i, j) * beta);
      }
    }
    return this;
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
    return copy().subi(alpha, other, beta, axis);
  }

  @Override
  public IntMatrix sub(int alpha, IntMatrix other, int beta) {
    Check.equalSize(this, other);
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) - other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  @Override
  public IntMatrix subi(IntMatrix other) {
    addi(1, other, -1);
    return this;
  }

  @Override
  public IntMatrix subi(int scalar) {
    addi(-scalar);
    return this;
  }

  @Override
  public IntMatrix subi(IntMatrix other, Axis axis) {
    return subi(1, other, 1, axis);
  }

  @Override
  public IntMatrix subi(int alpha, IntMatrix other, int beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (alpha * get(i)) - (other.getAsInt(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (alpha * get(i)) - (other.getAsInt(i / rows()) * beta));
      }
    }
    return this;
  }

  @Override
  public IntMatrix subi(int alpha, IntMatrix other, int beta) {
    addi(alpha, other, -1 * beta);
    return this;
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
    return copy().rsubi(alpha, other, beta, axis);
  }

  @Override
  public IntMatrix rsubi(int scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        set(i, j, scalar - get(i, j));
      }
    }
    return this;
  }

  @Override
  public IntMatrix rsubi(IntMatrix other, Axis axis) {
    return rsubi(1, other, 1, axis);
  }

  @Override
  public IntMatrix rsubi(int alpha, IntMatrix other, int beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (other.getAsInt(i % rows()) * beta) - (alpha * get(i)));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (other.getAsInt(i / rows()) * beta) - (alpha * get(i)));
      }
    }
    return this;
  }

  @Override
  public IntMatrix div(IntMatrix other) {
    Check.equalSize(this, other);
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
    return mul(1 / other);
  }

  @Override
  public IntMatrix div(IntMatrix other, Axis axis) {
    return div(1, other, 1, axis);
  }

  @Override
  public IntMatrix div(int alpha, IntMatrix other, int beta, Axis axis) {
    return copy().divi(alpha, other, beta, axis);
  }

  @Override
  public IntMatrix divi(IntMatrix other) {
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      set(i, get(i) / other.get(i));
    }
    return this;
  }

  @Override
  public IntMatrix divi(int other) {
    return muli(1 / other);
  }

  @Override
  public IntMatrix divi(IntMatrix other, Axis axis) {
    return divi(1, other, 1, axis);
  }

  @Override
  public IntMatrix divi(int alpha, IntMatrix other, int beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (alpha * get(i)) / (other.getAsInt(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        // this.set(i, (alpha * get(i)) / (other.getAsInt(i / rows()) * beta));
      }
    }
    return this;
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
    return copy().rdivi(alpha, other, beta, axis);
  }

  @Override
  public IntMatrix rdivi(int other) {
    for (int i = 0; i < size(); i++) {
      set(i, other / get(i));
    }
    return this;
  }

  @Override
  public IntMatrix rdivi(IntMatrix other, Axis axis) {
    return rdivi(1, other, 1, axis);
  }

  @Override
  public IntMatrix rdivi(int alpha, IntMatrix other, int beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows());
      for (int i = 0; i < size(); i++) {
        // this.set(i, (other.getAsInt(i % rows()) * beta) / (alpha * get(i)));
      }
    } else {
      checkArgument(other.size() == columns());
      for (int i = 0; i < size(); i++) {
        // this.set(i, (other.getAsInt(i / rows()) * beta) / (alpha * get(i)));
      }
    }
    return this;
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

  public static class IncrementalBuilder {

    private IntArrayList buffer = new IntArrayList();

    public void add(int value) {
      buffer.add(value);
    }

    public IntMatrix build() {
      return new DefaultIntMatrix(new IntStorage(buffer.toArray()), buffer.size());
    }
  }

  protected static class SliceIntMatrix extends AbstractIntMatrix {

    private final Slice row, column;
    private final IntMatrix parent;

    public SliceIntMatrix(IntMatrix parent, Slice row, Slice column) {
      this(parent, checkNotNull(row).size(), row, checkNotNull(column).size(), column);
    }

    public SliceIntMatrix(IntMatrix parent, int rows, Slice row, int columns, Slice column) {
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

  protected class FlatSliceIntMatrix extends AbstractIntMatrix {
    private final IntMatrix parent;
    private final Slice slice;

    private FlatSliceIntMatrix(IntMatrix parent, int size, Slice slice) {
      super(size);
      this.parent = checkNotNull(parent);
      this.slice = checkNotNull(slice);
    }

    public FlatSliceIntMatrix(IntMatrix parent, Slice slice) {
      this(parent, checkNotNull(slice).size(), slice);
    }

    @Override
    public void set(int i, int j, int value) {
      set(columnMajor(i, j, rows(), columns()), value);
    }

    @Override
    public void set(int index, int value) {
      parent.set(sliceIndex(slice.step(), index, parent.size()), value);
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
      return parent.get(sliceIndex(slice.step(), index, parent.size()));
    }


  }



}
