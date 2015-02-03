package org.briljantframework.matrix;

/**
 * Created by Isak Karlsson on 12/01/15.
 */
public interface BitMatrix extends Matrix, Iterable<Boolean> {

  void set(int i, int j, boolean value);

  void set(int index, boolean value);

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
  BitMatrix transpose();

  @Override
  BitMatrix copy();

  @Override
  BitMatrix newEmptyMatrix(int rows, int columns);

  BitMatrix newEmptyVector(int size);
}
