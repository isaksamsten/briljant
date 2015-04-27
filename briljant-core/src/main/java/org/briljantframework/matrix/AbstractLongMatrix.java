package org.briljantframework.matrix;

import com.carrotsearch.hppc.LongArrayList;

import org.briljantframework.Check;
import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.function.LongBiPredicate;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.storage.Storage;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
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

  protected AbstractLongMatrix(MatrixFactory bj, int size) {
    super(bj, size);
  }

  protected AbstractLongMatrix(MatrixFactory bj, int rows, int cols) {
    super(bj, rows, cols);
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
  public LongMatrix assign(long value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
    return this;
  }

  @Override
  public LongMatrix assign(LongMatrix o) {
    Check.equalShape(this, o);
    for (int i = 0; i < size(); i++) {
      set(i, o.get(i));
    }
    return this;
  }

  @Override
  public DoubleMatrix asDoubleMatrix() {
    return new AsDoubleMatrix(getMatrixFactory(), rows(), columns()) {
      @Override
      public void set(int i, int j, double value) {
        AbstractLongMatrix.this.set(i, j, (long) value);
      }

      @Override
      public void set(int index, double value) {
        AbstractLongMatrix.this.set(index, (long) value);
      }

      @Override
      public double get(int i, int j) {
        return AbstractLongMatrix.this.get(i, j);
      }

      @Override
      public double get(int index) {
        return AbstractLongMatrix.this.get(index);
      }

      @Override
      public Storage getStorage() {
        return AbstractLongMatrix.this.getStorage();
      }
    };
  }

  @Override
  public LongMatrix assign(LongSupplier supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.getAsLong());
    }
    return this;
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
  public IntMatrix asIntMatrix() {
    return new AsIntMatrix(getMatrixFactory(), rows(), columns()) {
      @Override
      public int get(int i, int j) {
        return (int) AbstractLongMatrix.this.get(i, j);
      }

      @Override
      public int get(int index) {
        return (int) AbstractLongMatrix.this.get(index);
      }

      @Override
      public void set(int index, int value) {
        AbstractLongMatrix.this.set(index, value);
      }

      @Override
      public void set(int i, int j, int value) {
        AbstractLongMatrix.this.set(i, j, value);
      }

      @Override
      public Storage getStorage() {
        return AbstractLongMatrix.this.getStorage();
      }
    };
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
    IntMatrix matrix = bj.intMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, map.applyAsInt(get(i)));
    }
    return matrix;
  }

  @Override
  public DoubleMatrix mapToDouble(LongToDoubleFunction map) {
    DoubleMatrix matrix = bj.doubleMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, map.applyAsDouble(get(i)));
    }
    return matrix;
  }

  @Override
  public ComplexMatrix mapToComplex(LongFunction<Complex> map) {
    ComplexMatrix matrix = bj.complexMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, map.apply(get(i)));
    }
    return matrix;
  }

  @Override
  public BitMatrix satisfies(LongPredicate predicate) {
    BitMatrix bits = bj.booleanMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i)));
    }
    return bits;
  }

  @Override
  public BitMatrix satisfies(LongMatrix matrix, LongBiPredicate predicate) {
    Check.equalShape(this, matrix);
    BitMatrix bits = bj.booleanMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i), matrix.get(i)));
    }
    return bits;
  }

  @Override
  public LongMatrix asLongMatrix() {
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
  public BitMatrix asBitMatrix() {
    return new AsBitMatrix(getMatrixFactory(), rows(), columns()) {
      @Override
      public void set(int i, int j, boolean value) {
        AbstractLongMatrix.this.set(i, j, value ? 1 : 0);
      }

      @Override
      public void set(int index, boolean value) {
        AbstractLongMatrix.this.set(index, value ? 1 : 0);
      }

      @Override
      public boolean get(int i, int j) {
        return AbstractLongMatrix.this.get(i, j) == 1;
      }

      @Override
      public boolean get(int index) {
        return AbstractLongMatrix.this.get(index) == 1;
      }

      @Override
      public Storage getStorage() {
        return AbstractLongMatrix.this.getStorage();
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
        return AbstractLongMatrix.this.get(index);
      }

      @Override
      public Long set(int index, Long element) {
        Long old = get(index);
        AbstractLongMatrix.this.set(index, element);
        return old;
      }

      @Override
      public int size() {
        return 0;
      }
    };
  }

  @Override
  public LongMatrix mmul(LongMatrix other) {
    return mmul(1, other);
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
  public ComplexMatrix asComplexMatrix() {
    return new AsComplexMatrix(getMatrixFactory(), rows(), columns()) {
      @Override
      public void set(int index, Complex value) {
        AbstractLongMatrix.this.set(index, value.longValue());
      }

      @Override
      public void set(int i, int j, Complex value) {
        AbstractLongMatrix.this.set(i, j, value.longValue());
      }

      @Override
      public Complex get(int i, int j) {
        return Complex.valueOf(AbstractLongMatrix.this.get(i, j));
      }

      @Override
      public Complex get(int index) {
        return Complex.valueOf(AbstractLongMatrix.this.get(index));
      }

      @Override
      public Storage getStorage() {
        return AbstractLongMatrix.this.getStorage();
      }
    };
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
  public LongMatrix mul(LongMatrix other, Dim dim) {
    return mul(1, other, 1, dim);
  }

  @Override
  public LongMatrix mul(long alpha, LongMatrix other, long beta, Dim dim) {
    LongMatrix m = newEmptyMatrix(rows(), columns());
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
  public LongMatrix add(LongMatrix other, Dim dim) {
    return add(1, other, 1, dim);
  }

  @Override
  public LongMatrix add(long alpha, LongMatrix other, long beta, Dim dim) {
    LongMatrix m = newEmptyMatrix(rows(), columns());
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
  public LongMatrix sub(LongMatrix other, Dim dim) {
    return sub(1, other, 1, dim);
  }

  @Override
  public LongMatrix getRowView(int i) {
    return new LongMatrixView(getMatrixFactory(), this, i, 0, 1, columns());
  }

  @Override
  public LongMatrix sub(long alpha, LongMatrix other, long beta, Dim dim) {
    LongMatrix m = newEmptyMatrix(rows(), columns());
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
  public LongMatrix rsub(LongMatrix other, Dim dim) {
    return rsub(1, other, 1, dim);
  }

  @Override
  public LongMatrix rsub(long alpha, LongMatrix other, long beta, Dim dim) {
    LongMatrix m = newEmptyMatrix(rows(), columns());
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
  public LongMatrix div(LongMatrix other, Dim dim) {
    return div(1, other, 1, dim);
  }

  @Override
  public LongMatrix div(long alpha, LongMatrix other, long beta, Dim dim) {
    LongMatrix m = newEmptyMatrix(rows(), columns());
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
  public LongMatrix rdiv(long other) {
    LongMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, other / get(i));
    }
    return matrix;
  }

  @Override
  public LongMatrix rdiv(LongMatrix other, Dim dim) {
    return rdiv(1, other, 1, dim);
  }

  public LongMatrix getColumnView(int index) {
    return new LongMatrixView(getMatrixFactory(), this, 0, index, rows(), 1);
  }

  @Override
  public LongMatrix rdiv(long alpha, LongMatrix other, long beta, Dim dim) {
    LongMatrix m = newEmptyMatrix(rows(), columns());
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
    StringBuilder builder = new StringBuilder();
    try {
      MatrixPrinter.print(builder, this);
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

    public LongMatrix build() {
      LongMatrix n = newEmptyVector(buffer.size());
      for (int i = 0; i < n.size(); i++) {
        n.set(i, buffer.get(i));
      }
      return n;
    }

    public void add(long value) {
      buffer.add(value);
    }
  }

  protected static class SliceLongMatrix extends AbstractLongMatrix {

    private final Range row, column;
    private final LongMatrix parent;

    public SliceLongMatrix(MatrixFactory bj, LongMatrix parent, Range row, Range column) {
      this(bj, parent, checkNotNull(row).size(), row, checkNotNull(column).size(), column);
    }

    public SliceLongMatrix(MatrixFactory bj, LongMatrix parent, int rows, Range row, int columns,
                           Range column) {
      super(bj, rows, columns);
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
      return new SliceLongMatrix(getMatrixFactory(), parent, rows, row, columns, column);
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

    public LongMatrixView(MatrixFactory bj, LongMatrix parent, int rowOffset, int colOffset,
                          int rows, int cols) {
      super(bj, rows, cols);
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
      return new LongMatrixView(getMatrixFactory(), parent, rowOffset, colOffset, rows, columns);
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

    private FlatSliceLongMatrix(MatrixFactory bj, LongMatrix parent, int size, Range range) {
      super(bj, size);
      this.parent = checkNotNull(parent);
      this.range = checkNotNull(range);
    }

    public FlatSliceLongMatrix(MatrixFactory bj, LongMatrix parent, Range range) {
      this(bj, parent, checkNotNull(range).size(), range);
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

  @Override
  public LongMatrix getDiagonalView() {
    throw new UnsupportedOperationException();
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
  public LongMatrix getView(int rowOffset, int colOffset, int rows, int columns) {
    return new LongMatrixView(getMatrixFactory(), this, rowOffset, colOffset, rows, columns);
  }


  @Override
  public LongMatrix slice(Range rows, Range columns) {
    return new SliceLongMatrix(getMatrixFactory(), this, rows, columns);
  }


  @Override
  public LongMatrix slice(Range range) {
    return new FlatSliceLongMatrix(getMatrixFactory(), this, range);
  }


  @Override
  public LongMatrix slice(Range range, Dim dim) {
    if (dim == Dim.R) {
      return new SliceLongMatrix(getMatrixFactory(), this, range,
                                 getMatrixFactory().range(columns()));
    } else {
      return new SliceLongMatrix(getMatrixFactory(), this, getMatrixFactory().range(rows()), range);
    }
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
  public LongMatrix slice(Collection<Integer> indexes) {
    IncrementalBuilder builder = new IncrementalBuilder();
    indexes.forEach(index -> builder.add(get(index)));
    return builder.build();
  }


  @Override
  public LongMatrix slice(Collection<Integer> indexes, Dim dim) {
    LongMatrix matrix;
    if (dim == Dim.R) {
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
  public LongMatrix slice(BitMatrix indexes, Dim dim) {
    int size = sum(indexes);
    LongMatrix matrix;
    if (dim == Dim.R) {
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
  public LongMatrix copy() {
    LongMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, get(i));
    }
    return matrix;
  }


  @Override
  public LongMatrix newEmptyMatrix(int rows, int columns) {
    return null;
  }
}