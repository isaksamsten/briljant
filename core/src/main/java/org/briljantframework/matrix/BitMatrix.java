package org.briljantframework.matrix;

import java.util.Collection;

/**
 * Created by Isak Karlsson on 12/01/15.
 */
public interface BitMatrix extends Matrix, Iterable<Boolean> {

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

  /**
   * {@inheritDoc}
   *
   * @param i
   */
  @Override
  BitMatrix getRowView(int i);

  /**
   * {@inheritDoc}
   *
   * @param index
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
   *
   * @param rowOffset
   * @param colOffset
   * @param rows
   * @param columns
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
  BitMatrix transpose();

  @Override
  BitMatrix copy();

  @Override
  BitMatrix newEmptyMatrix(int rows, int columns);

  BitMatrix newEmptyVector(int size);
}
