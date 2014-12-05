package org.briljantframework.matrix;

import org.briljantframework.vector.RealVector;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 02/12/14.
 */
public class VectorMatrix extends AbstractMatrix {

  private final Vector vector;

  public VectorMatrix(int rows, int columns, Vector vector) {
    super(rows, columns);
    this.vector = vector;
  }

  public static Matrix wrap(RealVector vector) {
    return new VectorMatrix(1, vector.size(), vector);
  }

  @Override
  protected Matrix newEmptyMatrix(int rows, int columns) {
    return new ArrayMatrix(rows, columns);
  }

  @Override
  public Matrix copy() {
    return new ArrayMatrix(getShape(), vector.toDoubleArray());
  }

  @Override
  public void put(int i, int j, double value) {
    throw new UnsupportedOperationException("Can't mutate VectorMatrices.");
  }

  @Override
  public void put(int index, double value) {
    throw new UnsupportedOperationException("Can't mutate VectorMatrices.");
  }

  @Override
  public double[] asDoubleArray() {
    return vector.asDoubleArray();
  }

  @Override
  public double get(int i, int j) {
    return vector.getAsReal(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public double get(int index) {
    return vector.getAsReal(index);
  }

  @Override
  public int size() {
    return vector.size();
  }
}
