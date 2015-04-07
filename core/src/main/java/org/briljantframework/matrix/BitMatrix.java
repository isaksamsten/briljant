package org.briljantframework.matrix;

import org.briljantframework.matrix.storage.Storage;

import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Isak Karlsson
 */
public interface BitMatrix extends Matrix<BitMatrix>, Iterable<Boolean> {

  /**
   * <p> Returns a new {@code BitMatrix} with {@code values}. </p> <p> <p> For example </p> <p> <p>
   *
   * <pre>
   *  > BitMatrix a = BitMatrix.newMatrix(true, true, false, false, true, true).reshape(2, 3);
   *
   *    true  false  true
   *    true  false  true
   *    shape: 2x3 type: boolean
   * </pre>
   *
   * @param values an array of booleans
   * @return a new boolean vector
   */
  static BitMatrix newBitVector(boolean... values) {
    return new DefaultBitMatrix(values);
  }

  /**
   * Return a new empty (all elements are {@code false}), {@code BitMatrix} (column-vector) of
   * {@code size}.
   *
   * @param size size
   * @return a new boolean vector
   */
  static BitMatrix newBitVector(int size) {
    return new DefaultBitMatrix(size);
  }

  /**
   * Return a new empty (all elements are {@code false}) {@code BitMatrix} of {@code rows} and
   * {@code columns}.
   *
   * @param rows the rows
   * @param cols the columns
   * @return a new boolean matrix
   */
  static BitMatrix newMatrix(int rows, int cols) {
    return new DefaultBitMatrix(rows, cols);
  }

  static BitMatrix newSparseMatrix(int rows, int cols) {
    return new SparseBitMatrix(rows, cols);
  }

  static BitMatrix newSparseVector(int size) {
    return new SparseBitMatrix(size);
  }

  BitMatrix assign(Supplier<Boolean> supplier);

  BitMatrix assign(boolean value);

  BitMatrix assign(BitMatrix other);

  void set(int i, int j, boolean value);

  void set(int index, boolean value);

  void setRow(int index, BitMatrix row);

  void setColumn(int index, BitMatrix column);

  boolean get(int i, int j);

  boolean get(int index);

  BitMatrix xor(BitMatrix other);

  BitMatrix or(BitMatrix other);

  BitMatrix orNot(BitMatrix other);

  BitMatrix and(BitMatrix other);

  BitMatrix andNot(BitMatrix other);

  BitMatrix not();

  @Override
  BitMatrix reshape(int rows, int columns);

  Stream<Boolean> stream();

  List<Boolean> asList();

  /**
   * {@inheritDoc}
   */
  @Override
  BitMatrix getRowView(int i);

  /**
   * Returns an unmodifiable view of this matrix.
   *
   * @return an unmodifiable view
   */
  default BitMatrix frozen() {
    return new AbstractBitMatrix(rows(), columns()) {
      @Override
      public void set(int i, int j, boolean value) {
        throw new UnsupportedOperationException();
      }

      @Override
      public void set(int index, boolean value) {
        throw new UnsupportedOperationException();
      }

      @Override
      public boolean get(int i, int j) {
        return BitMatrix.this.get(i, j);
      }

      @Override
      public boolean get(int index) {
        return BitMatrix.this.get(index);
      }

      @Override
      public BitMatrix reshape(int rows, int columns) {
        return BitMatrix.this.reshape(rows, columns);
      }

      @Override
      public BitMatrix newEmptyMatrix(int rows, int columns) {
        return BitMatrix.this.newEmptyMatrix(rows, columns);
      }

      @Override
      public boolean isView() {
        return BitMatrix.this.isView();
      }

      @Override
      public Storage getStorage() {
        return BitMatrix.this.getStorage().frozen();
      }
    };
  }

  /**
   * {@inheritDoc}
   */
  @Override
  BitMatrix getColumnView(int index);


  /**
   * {@inheritDoc}
   */
  @Override
  BitMatrix getDiagonalView();

  /**
   * {@inheritDoc}
   */
  @Override
  BitMatrix getView(int rowOffset, int colOffset, int rows, int columns);

  @Override
  BitMatrix slice(Range rows, Range columns);

  @Override
  BitMatrix slice(Range range);

  @Override
  BitMatrix slice(Range range, Axis axis);

  @Override
  BitMatrix slice(Collection<Integer> rows, Collection<Integer> columns);

  @Override
  BitMatrix slice(Collection<Integer> indexes);

  @Override
  BitMatrix slice(Collection<Integer> indexes, Axis axis);

  @Override
  BitMatrix slice(BitMatrix bits);

  @Override
  BitMatrix slice(BitMatrix indexes, Axis axis);

  @Override
  BitMatrix newEmptyMatrix(int rows, int columns);

  BitMatrix newEmptyVector(int size);

  @Override
  BitMatrix transpose();

  @Override
  BitMatrix copy();


}
