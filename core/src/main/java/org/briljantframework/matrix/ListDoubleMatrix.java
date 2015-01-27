package org.briljantframework.matrix;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Preconditions;

/**
 * Created by isak on 1/27/15.
 */
public class ListDoubleMatrix extends AbstractDoubleMatrix {

  private final List<Number> numbers;

  protected ListDoubleMatrix(int size) {
    super(size);
    numbers = new ArrayList<>(size);
  }

  public ListDoubleMatrix(int rows, int columns) {
    super(rows, columns);
    numbers = new ArrayList<>(Math.multiplyExact(rows, columns));
  }

  public ListDoubleMatrix(List<Number> values) {
    super(values.size());
    numbers = values;
  }

  protected ListDoubleMatrix(List<Number> values, int rows, int columns) {
    super(rows, columns);
    this.numbers = values;
  }

  @Override
  public void set(int i, int j, double value) {
    set(Indexer.columnMajor(i, j, rows(), columns()), value);
  }

  @Override
  public void set(int index, double value) {
    numbers.set(Preconditions.checkElementIndex(index, size()), value);
  }

  @Override
  public DoubleMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new ListDoubleMatrix(numbers, rows, columns);
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public DoubleMatrix newEmptyMatrix(int rows, int columns) {
    return new ArrayDoubleMatrix(rows, columns);
  }

  @Override
  public double get(int i, int j) {
    return get(Indexer.columnMajor(i, j, rows(), columns()));
  }

  @Override
  public double get(int index) {
    return numbers.get(Preconditions.checkElementIndex(index, size())).doubleValue();
  }

  @Override
  public boolean isArrayBased() {
    return false;
  }
}
