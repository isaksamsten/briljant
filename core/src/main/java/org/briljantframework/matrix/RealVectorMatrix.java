package org.briljantframework.matrix;

import org.briljantframework.vector.RealVector;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 02/12/14.
 */
public class RealVectorMatrix extends AbstractRealMatrix {

  private final Vector vector;

  public RealVectorMatrix(int rows, int columns, Vector vector) {
    super(rows, columns);
    this.vector = vector;
  }

  public static RealMatrix wrap(RealVector vector) {
    return new RealVectorMatrix(1, vector.size(), vector);
  }

  @Override
  protected RealMatrix newMatrix(Shape shape, double[] array) {
    return new RealArrayMatrix(shape, array);
  }

  @Override
  protected RealMatrix newEmptyMatrix(int rows, int columns) {
    return new RealArrayMatrix(rows, columns);
  }

  @Override
  public RealMatrix copy() {
    return new RealArrayMatrix(getShape(), vector.toDoubleArray());
  }

  @Override
  public void put(int i, int j, double value) {
    throw new UnsupportedOperationException("Can't mutate RealVectorMatrices.");
  }

  @Override
  public void put(int index, double value) {
    throw new UnsupportedOperationException("Can't mutate RealVectorMatrices.");
  }

  @Override
  public double[] asDoubleArray() {
    return vector.toDoubleArray();
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
