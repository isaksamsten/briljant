package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;

import org.briljantframework.complex.Complex;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 04/01/15.
 */
public class VectorComplexMatrix extends AbstractComplexMatrix {

  private final Vector vector;

  public VectorComplexMatrix(int rows, int cols, Vector vector) {
    super(rows, cols);
    this.vector = vector;
  }

  @Override
  public ComplexMatrix reshape(int rows, int columns) {
    checkArgument(rows * columns == size());
    return new VectorComplexMatrix(rows, columns, vector);
  }

  @Override
  public Complex get(int i, int j) {
    return vector.getAsComplex(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public Complex get(int index) {
    return vector.getAsComplex(index);
  }

  @Override
  public boolean isArrayBased() {
    return true;
  }

  @Override
  public ComplexMatrix newEmptyMatrix(int rows, int columns) {
    return new ArrayComplexMatrix(rows, columns);
  }

  @Override
  public ComplexMatrix copy() {
    return new VectorComplexMatrix(rows(), columns(), vector.newCopyBuilder().build());
  }

  @Override
  public void set(int i, int j, Complex value) {
    throw new UnsupportedOperationException("Can't mutate VectorComplexMatrix.");
  }

  @Override
  public void set(int index, Complex value) {
    throw new UnsupportedOperationException("Can't mutate VectorComplexMatrix.");
  }
}
