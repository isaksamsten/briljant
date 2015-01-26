package org.briljantframework.vector;

import org.briljantframework.matrix.Check;
import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.ImmutableModificationException;
import org.briljantframework.matrix.AbstractComplexMatrix;
import org.briljantframework.matrix.ArrayComplexMatrix;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.Indexer;

/**
 * Created by Isak Karlsson on 04/01/15.
 */
class VectorComplexMatrixAdapter extends AbstractComplexMatrix {

  private final Vector vector;

  public VectorComplexMatrixAdapter(int rows, int cols, Vector vector) {
    super(rows, cols);
    this.vector = vector;
  }

  public VectorComplexMatrixAdapter(Vector vector) {
    this(vector.size(), 1, vector);
  }

  @Override
  public ComplexMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new VectorComplexMatrixAdapter(rows, columns, vector);
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public ComplexMatrix newEmptyMatrix(int rows, int columns) {
    return new ArrayComplexMatrix(rows, columns);
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
  public ComplexMatrix copy() {
    return new VectorComplexMatrixAdapter(rows(), columns(), vector.newCopyBuilder().build());
  }

  @Override
  public double[] asDoubleArray() {
    return vector.asDoubleArray();
  }

  @Override
  public void set(int i, int j, Complex value) {
    throw new ImmutableModificationException();
  }

  @Override
  public void set(int index, Complex value) {
    throw new ImmutableModificationException();
  }
}
