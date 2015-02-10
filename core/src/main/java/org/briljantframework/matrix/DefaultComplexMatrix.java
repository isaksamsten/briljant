package org.briljantframework.matrix;

import org.briljantframework.Check;
import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.storage.ComplexStorage;
import org.briljantframework.matrix.storage.Storage;

/**
 * Created by Isak Karlsson on 07/01/15.
 */
public class DefaultComplexMatrix extends AbstractComplexMatrix {

  private final Storage storage;
  private Complex defaultValue = Complex.ZERO;

  public DefaultComplexMatrix(Storage storage, int rows, int columns) {
    super(rows, columns);
    this.storage = storage;
  }

  public DefaultComplexMatrix(int rows, int cols) {
    this(new ComplexStorage(new Complex[Math.multiplyExact(rows, cols)]), rows, cols);
  }

  protected DefaultComplexMatrix(Complex[] values, int rows, int cols) {
    this(new ComplexStorage(values), rows, cols);
  }

  public DefaultComplexMatrix(int rows, int columns, Complex defaultValue) {
    this(rows, columns);
    this.defaultValue = defaultValue;
  }

  public DefaultComplexMatrix(int size, Complex defaultValue) {
    this(size);
    this.defaultValue = defaultValue;
  }

  public DefaultComplexMatrix(Matrix matrix) {
    this(matrix.getStorage(), matrix.rows(), matrix.columns());
  }

  public DefaultComplexMatrix(Storage storage, int size) {
    super(size);
    this.storage = storage;
  }

  public DefaultComplexMatrix(Storage storage) {
    this(storage, storage.size());
  }

  public DefaultComplexMatrix(int rows, int columns, Complex... complexes) {
    this(new ComplexStorage(complexes), rows, columns);
  }

  public DefaultComplexMatrix(int size) {
    this(new ComplexStorage(new Complex[size]), size);
  }

  public DefaultComplexMatrix(Complex[] values) {
    this(values, values.length, 1);
  }


  public static DefaultComplexMatrix withDefaultValue(int rows, int columns, Complex zero) {
    return new DefaultComplexMatrix(rows, columns, zero);
  }

  @Override
  public ComplexMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new DefaultComplexMatrix(storage, rows, columns);
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public ComplexMatrix newEmptyMatrix(int rows, int columns) {
    return new DefaultComplexMatrix(rows, columns);
  }

  @Override
  public Complex get(int i, int j) {
    final Complex value = getStorage().getComplex(Indexer.columnMajor(i, j, rows(), columns()));
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
  public boolean isArrayBased() {
    return false;
  }

  @Override
  public ComplexMatrix copy() {
    return new DefaultComplexMatrix(storage.copy(), rows(), columns());
  }

  @Override
  public Storage getStorage() {
    return storage;
  }

  @Override
  public void set(int i, int j, Complex value) {
    getStorage().setComplex(Indexer.columnMajor(i, j, rows(), columns()), value);
  }

  @Override
  public void set(int index, Complex value) {
    getStorage().setComplex(index, value);
  }
}
