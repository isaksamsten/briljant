package org.briljantframework.matrix;

import com.google.common.collect.UnmodifiableIterator;

import com.carrotsearch.hppc.IntArrayList;

import org.briljantframework.Check;
import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.api.MatrixFactory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.briljantframework.matrix.Indexer.columnMajor;
import static org.briljantframework.matrix.Indexer.sliceIndex;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractBitMatrix extends AbstractMatrix<BitMatrix> implements BitMatrix {

  protected AbstractBitMatrix(MatrixFactory bj, int size) {
    super(bj, size);
  }

  protected AbstractBitMatrix(MatrixFactory bj, int rows, int cols) {
    super(bj, rows, cols);
  }

  @Override
  public BitMatrix assign(BitMatrix o) {
    Check.equalShape(this, o);
    for (int i = 0; i < size(); i++) {
      set(i, o.get(i));
    }
    return this;
  }

  @Override
  public void set(int toIndex, BitMatrix from, int fromIndex) {
    set(toIndex, from.get(fromIndex));
  }

  @Override
  public void set(int toRow, int toColumn, BitMatrix from, int fromRow, int fromColumn) {
    set(toRow, toColumn, from.get(fromRow, fromColumn));
  }

  @Override
  public int compare(int a, int b) {
    return Boolean.compare(get(a), get(b));
  }

  @Override
  public void setRow(int index, BitMatrix row) {
    Check.size(columns(), row);
    for (int j = 0; j < columns(); j++) {
      set(index, j, row.get(j));
    }
  }

  @Override
  public void setColumn(int index, BitMatrix column) {
    Check.size(rows(), column.size());
    for (int i = 0; i < rows(); i++) {
      set(i, index, column.get(i));
    }
  }

  @Override
  public BitMatrix assign(Supplier<Boolean> supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.get());
    }
    return this;
  }

//  @Override
//  public DoubleMatrix asDoubleMatrix() {
//    return new AsDoubleMatrix(rows(), columns()) {
//      @Override
//      public void set(int i, int j, double value) {
//        AbstractBitMatrix.this.set(i, j, value == 1);
//      }
//
//      @Override
//      public void set(int index, double value) {
//        AbstractBitMatrix.this.set(index, value == 1);
//      }
//
//      @Override
//      public double get(int i, int j) {
//        return AbstractBitMatrix.this.get(i, j) ? 1 : 0;
//      }
//
//      @Override
//      public double get(int index) {
//        return AbstractBitMatrix.this.get(index) ? 1 : 0;
//      }
//
//      @Override
//      public Storage getStorage() {
//        return AbstractBitMatrix.this.getStorage();
//      }
//    };
//  }


  @Override
  public BitMatrix add(BitMatrix o) {
    return asIntMatrix().add(o.asIntMatrix()).asBitMatrix().copy();
  }

  @Override
  public BitMatrix sub(BitMatrix o) {
    return asIntMatrix().sub(o.asIntMatrix()).asBitMatrix().copy();
  }

  @Override
  public BitMatrix mul(BitMatrix o) {
    return asIntMatrix().mul(o.asIntMatrix()).asBitMatrix().copy();
  }

  @Override
  public BitMatrix div(BitMatrix o) {
    return asIntMatrix().div(o.asIntMatrix()).asBitMatrix().copy();
  }

  @Override
  public BitMatrix mmul(BitMatrix o) {
    return asIntMatrix().mmul(o.asIntMatrix()).asBitMatrix().copy();
  }

  @Override
  public BitMatrix assign(boolean value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
    return this;
  }

  @Override
  public int hashCode() {
    int value = Objects.hash(rows(), columns());
    for (int i = 0; i < size(); i++) {
      value = value * 31 + Boolean.hashCode(get(i));
    }
    return value;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof BitMatrix) {
      BitMatrix o = (BitMatrix) obj;
      if (rows() == o.rows() && columns() == o.columns()) {
        for (int i = 0; i < size(); i++) {
          if (get(i) != o.get(i)) {
            return false;
          }
        }
      } else {
        return false;
      }

    } else {
      return false;
    }

    return true;
  }

  @Override
  public String toString() {
    StringBuilder a = new StringBuilder();
    try {
      MatrixPrinter.print(a, this);
    } catch (IOException e) {
      return getClass().getSimpleName();
    }
    return a.toString();
  }

  @Override
  public Iterator<Boolean> iterator() {
    return new UnmodifiableIterator<Boolean>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public Boolean next() {
        return get(current++);
      }
    };
  }

//  @Override
//  public IntMatrix asIntMatrix() {
//    return new AsIntMatrix(rows(), columns()) {
//      @Override
//      public int get(int i, int j) {
//        return AbstractBitMatrix.this.get(i, j) ? 1 : 0;
//      }
//
//      @Override
//      public int get(int index) {
//        return AbstractBitMatrix.this.get(index) ? 1 : 0;
//
//      }
//
//      @Override
//      public void set(int index, int value) {
//        AbstractBitMatrix.this.set(index, value == 1);
//      }
//
//      @Override
//      public void set(int i, int j, int value) {
//        AbstractBitMatrix.this.set(i, j, value == 1);
//      }
//
//      @Override
//      public Storage getStorage() {
//        return AbstractBitMatrix.this.getStorage();
//      }
//    };
//  }

  @Override
  public void swap(int a, int b) {
    boolean tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  @Override
  public BitMatrix newEmptyVector(int size) {
    return newEmptyMatrix(size, 1);
  }

  public class IncrementalBuilder {

    private IntArrayList buffer = new IntArrayList();

    public void add(boolean a) {
      buffer.add(a ? 1 : 0);
    }

    public BitMatrix build() {
      BitMatrix n = newEmptyMatrix(buffer.size(), 1);
      for (int i = 0; i < buffer.size(); i++) {
        n.set(i, buffer.get(i) == 1);
      }
      return n;
    }
  }

  @Override
  public DoubleMatrix asDoubleMatrix() {
    return new AsDoubleMatrix(getMatrixFactory(), rows(), columns()) {
      @Override
      public void set(int i, int j, double value) {
        AbstractBitMatrix.this.set(i, j, value == 1);
      }

      @Override
      public void set(int index, double value) {
        AbstractBitMatrix.this.set(index, value == 1);
      }

      @Override
      public double get(int i, int j) {
        return AbstractBitMatrix.this.get(i, j) ? 1 : 0;
      }

      @Override
      public double get(int index) {
        return AbstractBitMatrix.this.get(index) ? 1 : 0;
      }
    };
  }

  @Override
  public IntMatrix asIntMatrix() {
    return new AsIntMatrix(getMatrixFactory(), rows(), columns()) {
      @Override
      public int get(int i, int j) {
        return AbstractBitMatrix.this.get(i, j) ? 1 : 0;
      }

      @Override
      public int get(int index) {
        return AbstractBitMatrix.this.get(index) ? 1 : 0;

      }

      @Override
      public void set(int index, int value) {
        AbstractBitMatrix.this.set(index, value == 1);
      }

      @Override
      public void set(int i, int j, int value) {
        AbstractBitMatrix.this.set(i, j, value == 1);
      }
    };
  }

  @Override
  public LongMatrix asLongMatrix() {
    return new AsLongMatrix(getMatrixFactory(), rows(), columns()) {
      @Override
      public long get(int i, int j) {
        return AbstractBitMatrix.this.get(i, j) ? 1 : 0;
      }

      @Override
      public long get(int index) {
        return AbstractBitMatrix.this.get(index) ? 1 : 0;

      }

      @Override
      public void set(int index, long value) {
        AbstractBitMatrix.this.set(index, value == 1);
      }

      @Override
      public void set(int i, int j, long value) {
        AbstractBitMatrix.this.set(i, j, value == 1);
      }
    };
  }

  @Override
  public ComplexMatrix asComplexMatrix() {
    return new AsComplexMatrix(getMatrixFactory(), rows(), columns()) {
      @Override
      public void set(int index, Complex value) {
        AbstractBitMatrix.this.set(index, value.equals(Complex.ONE));
      }

      @Override
      public void set(int i, int j, Complex value) {
        AbstractBitMatrix.this.set(i, j, value.equals(Complex.ONE));
      }

      @Override
      public Complex get(int i, int j) {
        return AbstractBitMatrix.this.get(i, j) ? Complex.ONE : Complex.ZERO;
      }

      @Override
      public Complex get(int index) {
        return AbstractBitMatrix.this.get(index) ? Complex.ONE : Complex.ZERO;
      }
    };
  }

  protected static class SliceBitMatrix extends AbstractBitMatrix {

    private final Range row, column;
    private final BitMatrix parent;

    public SliceBitMatrix(MatrixFactory bj, BitMatrix parent, Range row, Range column) {
      this(bj, parent, checkNotNull(row).size(), row, checkNotNull(column).size(), column);
    }

    public SliceBitMatrix(MatrixFactory bj, BitMatrix parent, int rows, Range row, int columns,
                          Range column) {
      super(bj, rows, columns);
      this.row = checkNotNull(row);
      this.column = checkNotNull(column);
      this.parent = checkNotNull(parent);
    }

    @Override
    public void set(int i, int j, boolean value) {
      parent.set(sliceIndex(row.step(), i, parent.rows()),
                 sliceIndex(column.step(), j, parent.columns()), value);
    }

    @Override
    public void set(int index, boolean value) {
      int row = index % rows();
      int col = index / rows();
      set(row, col, value);
    }

    @Override
    public BitMatrix reshape(int rows, int columns) {
      Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
      return new SliceBitMatrix(getMatrixFactory(), parent, rows, row, columns, column);
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public BitMatrix newEmptyMatrix(int rows, int columns) {
      return parent.newEmptyMatrix(rows, columns);
    }

    @Override
    public boolean get(int i, int j) {
      return parent.get(sliceIndex(row.step(), i, parent.rows()),
                        sliceIndex(column.step(), j, parent.columns()));
    }

    @Override
    public boolean get(int index) {
      int row = index % rows();
      int col = index / rows();
      return get(row, col);
    }
  }

  /**
   * Created by Isak Karlsson on 13/01/15.
   */
  public static class BitMatrixView extends AbstractBitMatrix {

    private final BitMatrix parent;

    private final int rowOffset, colOffset;

    public BitMatrixView(MatrixFactory bj, BitMatrix parent, int rowOffset, int colOffset, int rows,
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
    public BitMatrix copy() {
      BitMatrix mat = parent.newEmptyMatrix(rows(), columns());
      for (int i = 0; i < size(); i++) {
        mat.set(i, get(i));
      }
      return mat;
    }

    @Override
    public void set(int i, int j, boolean value) {
      parent.set(rowOffset + i, colOffset + j, value);
    }

    @Override
    public void set(int index, boolean value) {
      parent.set(
          Indexer.computeLinearIndex(index, rows(), colOffset, rowOffset, parent.rows(),
                                     parent.columns()), value);
    }

    @Override
    public boolean get(int i, int j) {
      return parent.get(rowOffset + i, colOffset + j);
    }

    @Override
    public boolean get(int index) {
      return parent.get(Indexer.computeLinearIndex(index, rows(), colOffset, rowOffset,
                                                   parent.rows(), parent.columns()));
    }

    @Override
    public BitMatrix reshape(int rows, int columns) {
      return copy().reshape(rows, columns);
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public BitMatrix newEmptyMatrix(int rows, int columns) {
      return parent.newEmptyMatrix(rows, columns);
    }
  }

  protected static class FlatSliceBitMatrix extends AbstractBitMatrix {

    private final BitMatrix parent;
    private final Range range;

    public FlatSliceBitMatrix(MatrixFactory bj, BitMatrix parent, int size, Range range) {
      super(bj, size);
      this.parent = checkNotNull(parent);
      this.range = checkNotNull(range);
    }

    public FlatSliceBitMatrix(MatrixFactory bj, BitMatrix parent, Range range) {
      this(bj, parent, checkNotNull(range).size(), range);
    }

    @Override
    public void set(int i, int j, boolean value) {
      set(columnMajor(i, j, rows(), columns()), value);
    }

    @Override
    public void set(int index, boolean value) {
      parent.set(sliceIndex(range.step(), index, parent.size()), value);
    }

    @Override
    public BitMatrix reshape(int rows, int columns) {
      return copy().reshape(rows, columns);
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public BitMatrix newEmptyMatrix(int rows, int columns) {
      return parent.newEmptyMatrix(rows, columns);
    }

    @Override
    public boolean get(int i, int j) {
      return get(columnMajor(i, j, rows(), columns()));
    }

    @Override
    public boolean get(int index) {
      return parent.get(sliceIndex(range.step(), index, parent.size()));
    }
  }

//  @Override
//  public LongMatrix asLongMatrix() {
//    return new AsLongMatrix(rows(), columns()) {
//      @Override
//      public long get(int i, int j) {
//        return AbstractBitMatrix.this.get(i, j) ? 1 : 0;
//      }
//
//      @Override
//      public long get(int index) {
//        return AbstractBitMatrix.this.get(index) ? 1 : 0;
//
//      }
//
//      @Override
//      public void set(int index, long value) {
//        AbstractBitMatrix.this.set(index, value == 1);
//      }
//
//      @Override
//      public void set(int i, int j, long value) {
//        AbstractBitMatrix.this.set(i, j, value == 1);
//      }
//
//      @Override
//      public Storage getStorage() {
//        return AbstractBitMatrix.this.getStorage();
//      }
//    };
//  }

  private class BitListView extends AbstractList<Boolean> {

    @Override
    public Boolean get(int i) {
      return AbstractBitMatrix.this.get(i);
    }

    @Override
    public Boolean set(int i, Boolean value) {
      boolean old = AbstractBitMatrix.this.get(i);
      AbstractBitMatrix.this.set(i, value);
      return old;
    }

    @Override
    public int size() {
      return AbstractBitMatrix.this.size();
    }
  }


  @Override
  public BitMatrix asBitMatrix() {
    return this;
  }

//
//  @Override
//  public ComplexMatrix asComplexMatrix() {
//    return new AsComplexMatrix(rows(), columns()) {
//      @Override
//      public void set(int index, Complex value) {
//        AbstractBitMatrix.this.set(index, value.equals(Complex.ONE));
//      }
//
//      @Override
//      public void set(int i, int j, Complex value) {
//        AbstractBitMatrix.this.set(i, j, value.equals(Complex.ONE));
//      }
//
//      @Override
//      public Complex get(int i, int j) {
//        return AbstractBitMatrix.this.get(i, j) ? Complex.ONE : Complex.ZERO;
//      }
//
//      @Override
//      public Complex get(int index) {
//        return AbstractBitMatrix.this.get(index) ? Complex.ONE : Complex.ZERO;
//      }
//
//      @Override
//      public Storage getStorage() {
//        return AbstractBitMatrix.this.getStorage();
//      }
//    };
//  }


  @Override
  public BitMatrix getRow(int i) {
    return new BitMatrixView(getMatrixFactory(), this, i, 0, 1, columns());
  }


  @Override
  public BitMatrix getColumn(int index) {
    return new BitMatrixView(getMatrixFactory(), this, 0, index, rows(), 1);
  }


  @Override
  public BitMatrix getDiagonal() {
    throw new UnsupportedOperationException();
  }


  @Override
  public BitMatrix getView(int rowOffset, int colOffset, int rows, int columns) {
    return new BitMatrixView(getMatrixFactory(), this, rowOffset, colOffset, rows, columns);
  }


  @Override
  public BitMatrix slice(Range rows, Range columns) {
    return new SliceBitMatrix(getMatrixFactory(), this, rows, columns);
  }


  @Override
  public BitMatrix slice(Range range, Dim dim) {
    if (dim == Dim.R) {
      return new SliceBitMatrix(getMatrixFactory(), this, range,
                                getMatrixFactory().range(columns()));
    } else {
      return new SliceBitMatrix(getMatrixFactory(), this, getMatrixFactory().range(rows()), range);
    }
  }


  @Override
  public BitMatrix slice(Range range) {
    return new FlatSliceBitMatrix(getMatrixFactory(), this, range);
  }


  @Override
  public BitMatrix slice(Collection<Integer> rows, Collection<Integer> columns) {
    BitMatrix m = newEmptyMatrix(rows.size(), columns.size());
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
  public BitMatrix slice(Collection<Integer> indexes) {
    IncrementalBuilder builder = new IncrementalBuilder();
    indexes.forEach(index -> builder.add(get(index)));
    return builder.build();
  }


  @Override
  public BitMatrix slice(Collection<Integer> indexes, Dim dim) {
    BitMatrix matrix;
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
  public BitMatrix slice(BitMatrix bits) {
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
  public BitMatrix slice(BitMatrix indexes, Dim dim) {
    int size = Matrices.sum(indexes);
    BitMatrix matrix;
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
  public Stream<Boolean> stream() {
    return StreamSupport.stream(Spliterators.spliterator(new Iterator<Boolean>() {
      int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public Boolean next() {
        return get(current++);
      }
    }, size(), Spliterator.SIZED), false);
  }


  @Override
  public List<Boolean> asList() {
    return new AbstractList<Boolean>() {
      @NotNull
      @Override
      public Boolean get(int index) {
        return AbstractBitMatrix.this.get(index);
      }

      @Override
      public Boolean set(int index, Boolean element) {
        Boolean old = get(index);
        AbstractBitMatrix.this.set(index, element);
        return old;
      }

      @Override
      public int size() {
        return AbstractBitMatrix.this.size();
      }
    };
  }


  @Override
  public BitMatrix transpose() {
    BitMatrix matrix = newEmptyMatrix(columns(), rows());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(j, i, get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public BitMatrix copy() {
    BitMatrix n = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      n.set(i, get(i));
    }
    return n;
  }


  @Override
  public BitMatrix xor(BitMatrix other) {
    Check.equalShape(this, other);
    BitMatrix bm = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      boolean otherHas = other.get(i);
      boolean thisHas = get(i);
      bm.set(i, (thisHas || otherHas) && !(thisHas && otherHas));
    }
    return bm;
  }

  @Override
  public BitMatrix or(BitMatrix other) {
    Check.equalShape(this, other);
    BitMatrix bm = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) || other.get(i));
    }
    return bm;
  }

  @Override
  public BitMatrix orNot(BitMatrix other) {
    Check.equalShape(this, other);
    BitMatrix bm = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) || !other.get(i));
    }
    return bm;
  }

  @Override
  public BitMatrix and(BitMatrix other) {
    Check.equalShape(this, other);
    BitMatrix bm = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) && other.get(i));
    }
    return bm;
  }

  @Override
  public BitMatrix andNot(BitMatrix other) {
    Check.equalShape(this, other);
    BitMatrix bm = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, get(i) && !other.get(i));
    }
    return bm;
  }

  @Override
  public BitMatrix not() {
    BitMatrix bm = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      bm.set(i, !get(i));
    }
    return bm;
  }


}
