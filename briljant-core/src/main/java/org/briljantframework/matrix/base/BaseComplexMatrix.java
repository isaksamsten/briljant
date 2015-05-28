package org.briljantframework.matrix.base;

import org.briljantframework.Check;
import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.AbstractComplexMatrix;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.Indexer;
import org.briljantframework.matrix.storage.ComplexStorage;
import org.briljantframework.matrix.storage.Storage;
import org.briljantframework.matrix.api.MatrixFactory;

/**
 * Created by Isak Karlsson on 07/01/15.
 */
class BaseComplexMatrix extends AbstractComplexMatrix {

  private Storage storage;
  private Complex defaultValue = Complex.ZERO;

  BaseComplexMatrix(MatrixFactory bj, int rows, int cols) {
    this(bj, new ComplexStorage(new Complex[Math.multiplyExact(rows, cols)]), rows, cols);
  }

  BaseComplexMatrix(MatrixFactory bj, int size) {
    this(bj, new ComplexStorage(size), size);
  }

  BaseComplexMatrix(MatrixFactory bj, Storage storage, int rows, int columns) {
    super(bj, rows, columns);
    Check.size(storage.size(), Math.multiplyExact(rows, columns));
    this.storage = storage;
  }

  private BaseComplexMatrix(MatrixFactory bj, Complex[] values, int rows, int cols) {
    this(bj, new ComplexStorage(values), rows, cols);
  }

  BaseComplexMatrix(MatrixFactory bj, Storage storage, int size) {
    super(bj, size);
    this.storage = storage;
  }

  BaseComplexMatrix(MatrixFactory bj, Storage storage) {
    this(bj, storage, storage.size());
  }

  BaseComplexMatrix(MatrixFactory bj, Complex[] values) {
    this(bj, values, values.length, 1);
  }

  @Override
  public ComplexMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new BaseComplexMatrix(getMatrixFactory(), storage, rows, columns);
  }

  @Override
  public ComplexMatrix newEmptyMatrix(int rows, int columns) {
    return new BaseComplexMatrix(getMatrixFactory(), rows, columns);
  }

  @Override
  public ComplexMatrix copy() {
    return new BaseComplexMatrix(getMatrixFactory(), storage.copy(), rows(), columns());
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public Complex get(int i, int j) {
    // TODO: move this logic to the Storage
    final Complex value = storage.getComplex(Indexer.columnMajor(i, j, rows(), columns()));
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  @Override
  public Complex get(int index) {
    final Complex value = getStorage().getComplex(index);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  @Override
  public void set(int i, int j, Complex value) {
    storage.setComplex(Indexer.columnMajor(i, j, rows(), columns()), value);
  }

  @Override
  public void set(int index, Complex value) {
    storage.setComplex(index, value);
  }

  public Storage getStorage() {
    return storage;
  }
}
