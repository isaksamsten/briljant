/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.matrix;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableTable;

import com.carrotsearch.hppc.DoubleArrayList;

import org.briljantframework.Check;
import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.function.DoubleBiPredicate;
import org.briljantframework.matrix.storage.DoubleStorage;
import org.briljantframework.matrix.storage.Storage;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.PrimitiveIterator;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.DoubleBinaryOperator;
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoublePredicate;
import java.util.function.DoubleSupplier;
import java.util.function.DoubleToIntFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.DoubleUnaryOperator;
import java.util.function.IntToDoubleFunction;
import java.util.function.LongToDoubleFunction;
import java.util.function.ToDoubleFunction;
import java.util.stream.DoubleStream;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.briljantframework.matrix.Indexer.columnMajor;
import static org.briljantframework.matrix.Indexer.computeLinearIndex;
import static org.briljantframework.matrix.Indexer.rowMajor;
import static org.briljantframework.matrix.Indexer.sliceIndex;
import static org.briljantframework.matrix.Matrices.newComplexMatrix;
import static org.briljantframework.matrix.Matrices.sum;

/**
 * Created by Isak Karlsson on 20/08/14.
 */
public abstract class AbstractDoubleMatrix extends AbstractMatrix<DoubleMatrix>
    implements DoubleMatrix {

  protected AbstractDoubleMatrix(int size) {
    super(size);
  }

  public AbstractDoubleMatrix(int rows, int columns) {
    super(rows, columns);
  }

  @Override
  public DoubleMatrix assign(double value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
    return this;
  }

  @Override
  public DoubleMatrix assign(DoubleSupplier supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.getAsDouble());
    }
    return this;
  }

  @Override
  public DoubleMatrix assign(DoubleMatrix matrix) {
    return assign(matrix, DoubleUnaryOperator.identity());
  }

  @Override
  public DoubleMatrix assign(DoubleMatrix matrix, DoubleUnaryOperator operator) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsDouble(matrix.get(i)));
    }
    return this;
  }

  @Override
  public DoubleMatrix asDoubleMatrix() {
    return this;
  }

  @Override
  public DoubleMatrix assign(DoubleMatrix matrix, DoubleBinaryOperator combine) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, combine.applyAsDouble(get(i), matrix.get(i)));
    }
    return this;
  }

  @Override
  public DoubleMatrix assign(IntMatrix matrix, IntToDoubleFunction function) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsDouble(matrix.get(i)));
    }
    return this;
  }

  @Override
  public DoubleMatrix assign(LongMatrix matrix, LongToDoubleFunction function) {
    Check.size(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsDouble(matrix.get(i)));
    }
    return this;
  }

  @Override
  public DoubleMatrix assign(ComplexMatrix matrix, ToDoubleFunction<? super Complex> function) {
    Preconditions.checkArgument(matrix.size() == size());
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsDouble(matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntMatrix asIntMatrix() {
    return new AsIntMatrix(rows(), columns()) {
      @Override
      public int get(int i, int j) {
        return (int) AbstractDoubleMatrix.this.get(i, j);
      }

      @Override
      public int get(int index) {
        return (int) AbstractDoubleMatrix.this.get(index);
      }

      @Override
      public void set(int index, int value) {
        AbstractDoubleMatrix.this.set(index, value);
      }

      @Override
      public void set(int row, int column, int value) {
        AbstractDoubleMatrix.this.set(row, column, value);
      }

      @Override
      public Storage getStorage() {
        return null;
      }


    };
  }

  @Override
  public DoubleMatrix update(DoubleUnaryOperator operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsDouble(get(i)));
    }
    return this;
  }

  @Override
  public DoubleMatrix map(DoubleUnaryOperator operator) {
    DoubleMatrix mat = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      mat.set(i, operator.applyAsDouble(get(i)));
    }
    return mat;
  }

  @Override
  public IntMatrix mapToInt(DoubleToIntFunction function) {
    IntMatrix m = IntMatrix.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, function.applyAsInt(get(i)));
    }
    return m;
  }

  @Override
  public LongMatrix mapToLong(DoubleToLongFunction function) {
    LongMatrix m = LongMatrix.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, function.applyAsLong(get(i)));
    }
    return m;
  }

  @Override
  public LongMatrix asLongMatrix() {
    return new AsLongMatrix(rows(), columns()) {
      @Override
      public long get(int i, int j) {
        return (long) AbstractDoubleMatrix.this.get(i, j);
      }

      @Override
      public long get(int index) {
        return (long) AbstractDoubleMatrix.this.get(index);
      }

      @Override
      public void set(int index, long value) {
        AbstractDoubleMatrix.this.set(index, (int) value);
      }

      @Override
      public void set(int i, int j, long value) {
        AbstractDoubleMatrix.this.set(i, j, (int) value);
      }

      @Override
      public Storage getStorage() {
        return AbstractDoubleMatrix.this.getStorage();
      }
    };
  }

  @Override
  public ComplexMatrix mapToComplex(DoubleFunction<Complex> function) {
    ComplexMatrix m = newComplexMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, function.apply(get(i)));
    }
    return m;
  }

  @Override
  public DoubleMatrix filter(DoublePredicate predicate) {
    IncrementalBuilder builder = new IncrementalBuilder();
    for (int i = 0; i < size(); i++) {
      double value = get(i);
      if (predicate.test(value)) {
        builder.add(value);
      }
    }
    return builder.build();
  }

  @Override
  public BitMatrix satisfies(DoublePredicate predicate) {
    BitMatrix bits = BitMatrix.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i)));
    }
    return bits;
  }

  @Override
  public BitMatrix satisfies(DoubleMatrix matrix, DoubleBiPredicate predicate) {
    Check.equalShape(this, matrix);
    BitMatrix bits = BitMatrix.newMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bits.set(i, predicate.test(get(i), matrix.get(i)));
    }
    return bits;
  }

  @Override
  public BitMatrix asBitMatrix() {
    return new AsBitMatrix(rows(), columns()) {
      @Override
      public void set(int i, int j, boolean value) {
        AbstractDoubleMatrix.this.set(i, j, value ? 1 : 0);
      }

      @Override
      public void set(int index, boolean value) {
        AbstractDoubleMatrix.this.set(index, value ? 1 : 0);
      }

      @Override
      public boolean get(int i, int j) {
        return AbstractDoubleMatrix.this.get(i, j) == 1;
      }

      @Override
      public boolean get(int index) {
        return AbstractDoubleMatrix.this.get(index) == 1;
      }

      @Override
      public Storage getStorage() {
        return AbstractDoubleMatrix.this.getStorage();
      }
    };
  }

  @Override
  public void forEach(DoubleConsumer consumer) {
    for (int i = 0; i < size(); i++) {
      consumer.accept(get(i));
    }
  }

  @Override
  public double reduce(double identity, DoubleBinaryOperator reduce) {
    return reduce(identity, reduce, DoubleUnaryOperator.identity());
  }

  @Override
  public double reduce(double identity, DoubleBinaryOperator reduce, DoubleUnaryOperator map) {
    for (int i = 0; i < size(); i++) {
      identity = reduce.applyAsDouble(map.applyAsDouble(get(i)), identity);
    }
    return identity;
  }

  @Override
  public DoubleMatrix reduceColumns(ToDoubleFunction<? super DoubleMatrix> reduce) {
    DoubleMatrix mat = newEmptyMatrix(1, columns());
    for (int i = 0; i < columns(); i++) {
      mat.set(i, reduce.applyAsDouble(getColumnView(i)));
    }
    return mat;
  }

  @Override
  public ComplexMatrix asComplexMatrix() {
    return new AsComplexMatrix(rows(), columns()) {
      @Override
      public void set(int index, Complex value) {
        AbstractDoubleMatrix.this.set(index, value.intValue());
      }

      @Override
      public void set(int i, int j, Complex value) {
        AbstractDoubleMatrix.this.set(i, j, value.intValue());
      }

      @Override
      public Complex get(int i, int j) {
        return Complex.valueOf(AbstractDoubleMatrix.this.get(i, j));
      }

      @Override
      public Complex get(int index) {
        return Complex.valueOf(AbstractDoubleMatrix.this.get(index));
      }

      @Override
      public Storage getStorage() {
        return AbstractDoubleMatrix.this.getStorage();
      }
    };
  }

  @Override
  public DoubleMatrix reduceRows(ToDoubleFunction<? super DoubleMatrix> reduce) {
    DoubleMatrix mat = newEmptyMatrix(rows(), 1);
    for (int i = 0; i < rows(); i++) {
      mat.set(i, reduce.applyAsDouble(getRowView(i)));
    }
    return mat;
  }

  @Override
  public void update(int i, DoubleUnaryOperator update) {
    set(i, update.applyAsDouble(get(i)));
  }

  @Override
  public void update(int i, int j, DoubleUnaryOperator update) {
    set(i, j, update.applyAsDouble(get(i, j)));
  }

  @Override
  public void addTo(int i, double value) {
    set(i, get(i) + value);
  }

  @Override
  public void addTo(int i, int j, double value) {
    set(i, j, get(i, j) + value);
  }

  @Override
  public void set(int toIndex, DoubleMatrix from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void set(int toRow, int toColumn, DoubleMatrix from, int fromRow, int fromColumn) {
    set(toRow, toColumn, from.get(fromRow, fromColumn));
  }

  @Override
  public int compare(int a, int b) {
    return Double.compare(get(a), get(b));
  }

  @Override
  public int hashCode() {
    int result = 1;
    for (int i = 0; i < size(); i++) {
      long bits = Double.doubleToLongBits(get(i));
      result = 31 * result + (int) (bits ^ (bits >>> 32));
    }

    return Objects.hash(rows(), columns(), result);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof DoubleMatrix) {
      DoubleMatrix mat = (DoubleMatrix) obj;
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
  public DoubleMatrix getRowView(int i) {
    return new DoubleMatrixView(this, i, 0, 1, columns());
  }

  @Override
  public String toString() {
    ImmutableTable.Builder<Integer, Integer, String> builder = ImmutableTable.builder();
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        if (get(i, j) < 0) {
          builder.put(i, j, String.format("%1.4f", get(i, j)));
        } else {
          builder.put(i, j, String.format(" %1.4f", get(i, j)));
        }
      }
    }
    StringBuilder out = new StringBuilder();
    Utils.prettyPrintTable(out, builder.build(), 0, 2, false, false);
    out.append("shape: ").append(getShape()).append(" type: double");
    return out.toString();
  }

  @Override
  public DoubleMatrix newEmptyVector(int size) {
    return newEmptyMatrix(size, 1);
  }

  @Override
  public void swap(int a, int b) {
    double tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  public static class IncrementalBuilder {

    private DoubleArrayList buffer = new DoubleArrayList();

    public void add(double value) {
      buffer.add(value);
    }

    public DoubleMatrix build() {
      return new DefaultDoubleMatrix(buffer.toArray(), buffer.size(), 1);
    }
  }

  protected static class SliceDoubleMatrix extends AbstractDoubleMatrix {

    private final Range row, column;
    private final DoubleMatrix parent;

    public SliceDoubleMatrix(DoubleMatrix parent, Range row, Range column) {
      this(parent, checkNotNull(row).size(), row, checkNotNull(column).size(), column);
    }

    public SliceDoubleMatrix(DoubleMatrix parent, int rows, Range row, int columns, Range column) {
      super(rows, columns);
      this.row = checkNotNull(row);
      this.column = checkNotNull(column);
      this.parent = checkNotNull(parent);
    }

    @Override
    public DoubleMatrix reshape(int rows, int columns) {
      Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
      return copy().reshape(rows, columns);
    }

    @Override
    public void set(int i, int j, double value) {
      parent.set(sliceIndex(row.step(), i, parent.rows()),
                 sliceIndex(column.step(), j, parent.columns()), value);
    }

    @Override
    public void set(int index, double value) {
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
    public DoubleMatrix newEmptyMatrix(int rows, int columns) {
      return parent.newEmptyMatrix(rows, columns);
    }

    @Override
    public double get(int i, int j) {
      return parent.get(sliceIndex(row.step(), i, parent.rows()),
                        sliceIndex(column.step(), j, parent.columns()));
    }

    @Override
    public double get(int index) {
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
  public static class DoubleMatrixView extends AbstractDoubleMatrix {

    private final DoubleMatrix parent;

    private final int rowOffset, colOffset;

    public DoubleMatrixView(DoubleMatrix parent, int rowOffset, int colOffset, int rows, int cols) {
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
    public DoubleMatrix copy() {
      DoubleMatrix mat = parent.newEmptyMatrix(rows(), columns());
      for (int i = 0; i < size(); i++) {
        mat.set(i, get(i));
      }
      return mat;
    }

    @Override
    public DoubleMatrix mmul(double alpha, DoubleMatrix other) {
      if (this.columns() != other.rows()) {
        throw new NonConformantException(this, other);
      }
      if (isArrayBased() && other.isArrayBased()) {
        double[] tmp = new double[this.rows() * other.columns()];
        Matrices.mmul(this, alpha, other, 1.0, tmp);
        return new DefaultDoubleMatrix(new DoubleStorage(tmp), this.rows(), other.columns());
      } else {
        return super.mmul(alpha, other);
      }
    }

    @Override
    public DoubleMatrix reshape(int rows, int columns) {
      throw new UnsupportedOperationException("Unable to reshape view.");
      // return copy().reshape(rows, columns);
      // // TODO(isak): this might be strange..
      // return new DoubleMatrixView(parent.reshape(rows, columns), rowOffset, colOffset, rows,
      // columns);
    }


    @Override
    public boolean isView() {
      return false;
    }

    @Override
    public DoubleMatrix newEmptyMatrix(int rows, int columns) {
      return new DefaultDoubleMatrix(rows, columns);
    }

    @Override
    public double get(int i, int j) {
      return parent.get(rowOffset + i, colOffset + j);
    }

    @Override
    public double get(int index) {
      return parent.get(computeLinearIndex(index, rows(), colOffset, rowOffset, parent.rows(),
                                           parent.columns()));
    }

    @Override
    public boolean isArrayBased() {
      return parent.isArrayBased();
    }


    @Override
    public Storage getStorage() {
      return parent.getStorage();
    }


    @Override
    public void set(int i, int j, double value) {
      parent.set(rowOffset + i, colOffset + j, value);
    }

    @Override
    public void set(int index, double value) {
      parent.set(
          computeLinearIndex(index, rows(), colOffset, rowOffset, parent.rows(), parent.columns()),
          value);
    }
  }

  protected class FlatSliceDoubleMatrix extends AbstractDoubleMatrix {

    private final DoubleMatrix parent;
    private final Range range;

    public FlatSliceDoubleMatrix(DoubleMatrix parent, int size, Range range) {
      super(size);
      this.parent = checkNotNull(parent);
      this.range = checkNotNull(range);
    }

    public FlatSliceDoubleMatrix(DoubleMatrix parent, Range range) {
      this(parent, checkNotNull(range).size(), range);
    }

    @Override
    public void set(int i, int j, double value) {
      set(columnMajor(i, j, rows(), columns()), value);
    }

    @Override
    public void set(int index, double value) {
      parent.set(sliceIndex(range.step(), index, parent.size()), value);
    }

    @Override
    public DoubleMatrix reshape(int rows, int columns) {
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
    public DoubleMatrix newEmptyMatrix(int rows, int columns) {
      return parent.newEmptyMatrix(rows, columns);
    }

    @Override
    public double get(int i, int j) {
      return get(columnMajor(i, j, rows(), columns()));
    }

    @Override
    public double get(int index) {
      return parent.get(sliceIndex(range.step(), index, parent.size()));
    }

    @Override
    public boolean isArrayBased() {
      return parent.isArrayBased();
    }
  }

  private class DoubleListView extends AbstractList<Double> {

    @Override
    public Double get(int i) {
      return AbstractDoubleMatrix.this.get(i);
    }

    @Override
    public Double set(int i, Double value) {
      Double old = AbstractDoubleMatrix.this.get(i);
      AbstractDoubleMatrix.this.set(i, value);
      return old;
    }

    @Override
    public Iterator<Double> iterator() {
      return new Iterator<Double>() {
        private int index = 0;

        @Override
        public boolean hasNext() {
          return index < size();
        }

        @Override
        public Double next() {
          return get(index++);
        }
      };
    }

    @Override
    public int size() {
      return AbstractDoubleMatrix.this.size();
    }
  }

  public DoubleMatrix getColumnView(int index) {
    return new DoubleMatrixView(this, 0, index, rows(), 1);
  }


  @Override
  public Diagonal getDiagonalView() {
    throw new UnsupportedOperationException();
  }


  @Override
  public DoubleMatrix getView(int rowOffset, int colOffset, int rows, int columns) {
    return new DoubleMatrixView(this, rowOffset, colOffset, rows, columns);
  }


  @Override
  public DoubleMatrix slice(Range rows, Range columns) {
    return new SliceDoubleMatrix(this, rows, columns);
  }


  @Override
  public DoubleMatrix slice(Range range) {
    return new FlatSliceDoubleMatrix(this, range);
  }


  @Override
  public DoubleMatrix slice(Range range, Axis axis) {
    if (axis == Axis.ROW) {
      return new SliceDoubleMatrix(this, range, Range.range(columns()));
    } else {
      return new SliceDoubleMatrix(this, Range.range(rows()), range);
    }
  }


  @Override
  public DoubleMatrix slice(Collection<Integer> rows, Collection<Integer> columns) {
    DoubleMatrix m = newEmptyMatrix(rows.size(), columns.size());
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
  public DoubleMatrix slice(Collection<Integer> indexes) {
    DoubleMatrix m = newEmptyVector(indexes.size());
    int i = 0;
    for (int index : indexes) {
      m.set(i++, get(index));
    }
    return m;
  }


  @Override
  public DoubleMatrix slice(BitMatrix bits) {
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
  public DoubleMatrix slice(BitMatrix indexes, Axis axis) {
    int size = sum(indexes);
    DoubleMatrix matrix;
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
  public DoubleMatrix slice(Collection<Integer> indexes, Axis axis) {
    DoubleMatrix matrix;
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
  public void setRow(int index, DoubleMatrix row) {
    Check.size(columns(), row);
    for (int j = 0; j < columns(); j++) {
      set(index, j, row.get(j));
    }
  }


  @Override
  public void setColumn(int index, DoubleMatrix column) {
    Check.size(rows(), column);
    for (int i = 0; i < rows(); i++) {
      set(i, index, column.get(i));
    }
  }


  @Override
  public DoubleMatrix transpose() {
    DoubleMatrix matrix = newEmptyMatrix(this.columns(), this.rows());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(j, i, get(i, j));
      }
    }
    return matrix;
  }


  @Override
  public DoubleMatrix copy() {
    DoubleMatrix n = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      n.set(i, get(i));
    }
    return n;
  }


  @Override
  public DoubleStream stream() {
    PrimitiveIterator.OfDouble ofDouble = new PrimitiveIterator.OfDouble() {
      public int current = 0;

      @Override
      public double nextDouble() {
        return get(current++);
      }

      @Override
      public boolean hasNext() {
        return current < size();
      }
    };
    Spliterator.OfDouble spliterator =
        Spliterators.spliterator(ofDouble, size(), Spliterator.SIZED);
    return StreamSupport.doubleStream(spliterator, false);
  }


  @Override
  public List<Double> flat() {
    return new DoubleListView();
  }


  @Override
  public DoubleMatrix mmul(DoubleMatrix other) {
    /*
     * Sometimes this is a huge gain!
     */
    if (other instanceof Diagonal) {
      return mmul((Diagonal) other);
    }
    return mmul(1, other);
  }

  @Override
  public DoubleMatrix mmul(Diagonal diagonal) {
    if (columns() != diagonal.rows()) {
      throw new NonConformantException(this, diagonal);
    }
    DoubleMatrix matrix = newEmptyMatrix(this.rows(), diagonal.columns());
    long rows = this.rows(), columns = diagonal.columns();
    for (int column = 0; column < columns; column++) {
      if (column < this.columns()) {
        for (int row = 0; row < rows; row++) {
          double xv = this.get(row, column);
          double dv = diagonal.get(column);
          matrix.set(row, column, xv * dv);
        }
      } else {
        break;
      }
    }
    return matrix;
  }


  @Override
  public DoubleMatrix mmul(double alpha, DoubleMatrix other) {
    return mmul(alpha, Transpose.NO, other, Transpose.NO);
  }


  @Override
  public DoubleMatrix mmul(Transpose a, DoubleMatrix other, Transpose b) {
    return mmul(1, a, other, b);
  }


  @Override
  public DoubleMatrix mmul(double alpha, Transpose a, DoubleMatrix other, Transpose b) {
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

    DoubleMatrix result = newEmptyMatrix(thisRows, otherColumns);
    for (int row = 0; row < thisRows; row++) {
      for (int col = 0; col < otherColumns; col++) {
        double sum = 0.0;
        for (int k = 0; k < thisCols; k++) {
          int thisIndex = a.transpose() ?
                          rowMajor(row, k, thisRows, thisCols) :
                          columnMajor(row, k, thisRows, thisCols);
          int otherIndex = b.transpose() ?
                           rowMajor(k, col, otherRows, otherColumns) :
                           columnMajor(k, col, otherRows, otherColumns);
          sum += get(thisIndex) * other.get(otherIndex);
        }
        result.set(row, col, alpha * sum);
      }
    }
    return result;
  }


  @Override
  public DoubleMatrix mul(DoubleMatrix other) {
    return mul(1, other, 1);
  }


  @Override
  public DoubleMatrix mul(double alpha, DoubleMatrix other, double beta) {
    Check.size(this, other);
    DoubleMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, alpha * get(i) * other.get(i) * beta);
    }
    return m;
  }


  @Override
  public DoubleMatrix mul(DoubleMatrix other, Axis axis) {
    return mul(1, other, 1, axis);
  }


  @Override
  public DoubleMatrix mul(double alpha, DoubleMatrix other, double beta, Axis axis) {
    DoubleMatrix m = newEmptyMatrix(rows(), columns());
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
  public DoubleMatrix mul(double scalar) {
    DoubleMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.set(i, get(i) * scalar);
    }
    return m;
  }

  @Override
  public DoubleMatrix add(DoubleMatrix other) {
    return add(1, other, 1);
  }

  @Override
  public DoubleMatrix add(double scalar) {
    DoubleMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, get(i, j) + scalar);
      }
    }
    return matrix;
  }

  @Override
  public DoubleMatrix add(DoubleMatrix other, Axis axis) {
    return add(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix add(double alpha, DoubleMatrix other, double beta, Axis axis) {
    DoubleMatrix m = newEmptyMatrix(rows(), columns());
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
  public DoubleMatrix add(double alpha, DoubleMatrix other, double beta) {
    Check.size(this, other);
    DoubleMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, alpha * get(i) + other.get(i) * beta);
    }
    return matrix;
  }

  @Override
  public DoubleMatrix sub(DoubleMatrix other) {
    return sub(1, other, 1);
  }

  @Override
  public DoubleMatrix sub(double scalar) {
    return add(-scalar);
  }

  @Override
  public DoubleMatrix sub(DoubleMatrix other, Axis axis) {
    return sub(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix sub(double alpha, DoubleMatrix other, double beta, Axis axis) {
    DoubleMatrix m = newEmptyMatrix(rows(), columns());
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
  public DoubleMatrix sub(double alpha, DoubleMatrix other, double beta) {
    Check.size(this, other);
    DoubleMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, alpha * get(i) - other.get(i) * beta);
    }
    return matrix;
  }

  @Override
  public DoubleMatrix rsub(double scalar) {
    DoubleMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, scalar - get(i));
    }

    return matrix;
  }

  @Override
  public DoubleMatrix rsub(DoubleMatrix other, Axis axis) {
    return rsub(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix rsub(double alpha, DoubleMatrix other, double beta, Axis axis) {
    DoubleMatrix m = newEmptyMatrix(rows(), columns());
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
  public DoubleMatrix div(DoubleMatrix other) {
    Check.size(this, other);
    DoubleMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, get(i) / other.get(i));
    }
    return matrix;
  }

  @Override
  public DoubleMatrix div(double other) {
    return mul(1.0 / other);
  }

  @Override
  public DoubleMatrix div(DoubleMatrix other, Axis axis) {
    return div(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix div(double alpha, DoubleMatrix other, double beta, Axis axis) {
    DoubleMatrix m = newEmptyMatrix(rows(), columns());
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
  public DoubleMatrix rdiv(double other) {
    DoubleMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, other / get(i));
    }
    return matrix;
  }

  @Override
  public DoubleMatrix rdiv(DoubleMatrix other, Axis axis) {
    return rdiv(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix rdiv(double alpha, DoubleMatrix other, double beta, Axis axis) {
    DoubleMatrix m = newEmptyMatrix(rows(), columns());
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
  public DoubleMatrix negate() {
    DoubleMatrix n = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      n.set(i, -get(i));
    }
    return n;
  }

  @Override
  public double[] asDoubleArray() {
    return getStorage().asDoubleArray();
  }


}
