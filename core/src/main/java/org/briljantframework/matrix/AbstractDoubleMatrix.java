/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.briljantframework.matrix.Indexer.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.*;

import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.vector.Vector;

import com.carrotsearch.hppc.DoubleArrayList;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableTable;

/**
 * Created by Isak Karlsson on 20/08/14.
 */
public abstract class AbstractDoubleMatrix extends AbstractMatrix implements DoubleMatrix {
  protected AbstractDoubleMatrix(int size) {
    super(size);
  }

  public AbstractDoubleMatrix(int rows, int columns) {
    super(rows, columns);
  }

  @Override
  public DataType getDataType() {
    return DataType.DOUBLE;
  }

  @Override
  public Complex getAsComplex(int i, int j) {
    return new Complex(get(i, j));
  }

  @Override
  public Complex getAsComplex(int index) {
    return new Complex(get(index));
  }

  @Override
  public void set(int i, int j, Complex value) {
    set(i, j, value.doubleValue());
  }

  @Override
  public void set(int index, Complex value) {
    set(index, value.doubleValue());
  }

  @Override
  public double getAsDouble(int i, int j) {
    return get(i, j);
  }

  @Override
  public double getAsDouble(int index) {
    return get(index);
  }

  @Override
  public int getAsInt(int i, int j) {
    return (int) get(i, j);
  }

  @Override
  public int getAsInt(int index) {
    return (int) get(index);
  }

  @Override
  public void set(int i, int j, int value) {
    set(i, j, (double) value);
  }

  @Override
  public void set(int index, int value) {
    set(index, (double) value);
  }

  @Override
  public void set(int i, int j, Number number) {
    set(i, j, number.doubleValue());
  }

  @Override
  public void set(int index, Number number) {
    set(index, number.doubleValue());
  }

  @Override
  public void set(int atIndex, Matrix from, int fromIndex) {
    set(atIndex, from.getAsDouble(fromIndex));
  }

  @Override
  public void set(int atRow, int atColumn, Matrix from, int fromRow, int fromColumn) {
    set(atRow, atColumn, from.getAsDouble(fromRow, fromColumn));
  }

  @Override
  public int compare(int toIndex, Matrix from, int fromIndex) {
    return Double.compare(get(toIndex), from.getAsDouble(fromIndex));
  }

  @Override
  public int compare(int toRow, int toColumn, Matrix from, int fromRow, int fromColumn) {
    return Double.compare(get(toRow, toColumn), from.getAsDouble(fromRow, fromColumn));
  }

  @Override
  public DoubleMatrix getRowView(int i) {
    return new DoubleMatrixView(this, i, 0, 1, columns());
  }

  public DoubleMatrix getColumnView(int index) {
    return new DoubleMatrixView(this, 0, index, rows(), 1);
  }

  @Override
  public Diagonal getDiagonalView() {
    throw new UnsupportedOperationException();
  }

  @Override
  public DoubleMatrix getView(int rowOffset, int colOffset, int rows, int columns) {
    return new DoubleMatrixView(this, rowOffset, colOffset, rows, columns);
  }

  public DoubleMatrix transpose() {
    DoubleMatrix matrix = newEmptyMatrix(this.columns(), this.rows());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(j, i, get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public DoubleMatrix copy() {
    DoubleMatrix n = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      set(i, get(i));
    }
    return n;
  }

  @Override
  public IncrementalBuilder newIncrementalBuilder() {
    return new IncrementalBuilder();
  }

  @Override
  public DoubleMatrix assign(DoubleSupplier supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.getAsDouble());
    }
    return this;
  }

  @Override
  public DoubleMatrix assign(double value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
    return this;
  }

  @Override
  public DoubleMatrix assign(Vector vector, Axis axis) {
    return assign(vector, (a, b) -> b, axis);
  }

  @Override
  public DoubleMatrix assign(Vector other, DoubleBinaryOperator operator, Axis axis) {
    /*
     * Due to cache-locality, put(i, ) is for most (at least array based) matrices a _big_ win.
     * Therefore, the straightforward implementation using two for-loops is not used below. This is
     * a big win since this.size() >= other.size().
     */
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        set(i, operator.applyAsDouble(get(i), other.getAsDouble(i % rows())));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        set(i, operator.applyAsDouble(get(i), other.getAsDouble(i / rows())));
      }
    }
    return this;
  }

  @Override
  public DoubleMatrix assign(DoubleMatrix matrix) {
    return assign(matrix, DoubleUnaryOperator.identity());
  }

  @Override
  public DoubleMatrix assign(DoubleMatrix matrix, DoubleUnaryOperator operator) {
    Check.equalSize(this, matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsDouble(matrix.get(i)));
    }
    return this;
  }

  @Override
  public DoubleMatrix assign(ComplexMatrix matrix, ToDoubleFunction<? super Complex> function) {
    Preconditions.checkArgument(matrix.size() == size());
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsDouble(matrix.get(i)));
    }
    return this;
  }

  @Override
  public DoubleMatrix assignStream(Iterable<? extends Number> numbers) {
    int index = 0;
    for (Number number : numbers) {
      set(index++, number.doubleValue());
    }
    return this;
  }

  @Override
  public <T> DoubleMatrix assignStream(Iterable<T> iterable, ToDoubleFunction<? super T> function) {
    int index = 0;
    for (T t : iterable) {
      set(index++, function.applyAsDouble(t));
    }
    return this;
  }

  @Override
  public DoubleMatrix assign(double[] values) {
    checkArgument(size() == values.length);
    for (int i = 0; i < size(); i++) {
      set(i, values[i]);
    }
    return this;
  }

  @Override
  public DoubleMatrix map(DoubleUnaryOperator operator) {
    DoubleMatrix mat = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      mat.set(i, operator.applyAsDouble(get(i)));
    }
    return mat;
  }

  @Override
  public DoubleMatrix mapi(DoubleUnaryOperator operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsDouble(get(i)));
    }
    return this;
  }

  @Override
  public DoubleMatrix filter(DoublePredicate operator) {
    IncrementalBuilder builder = newIncrementalBuilder();
    for (int i = 0; i < size(); i++) {
      double value = get(i);
      if (operator.test(value)) {
        builder.add(value);
      }
    }
    return builder.build();
  }

  @Override
  public double reduce(double identity, DoubleBinaryOperator reduce, DoubleUnaryOperator map) {
    for (int i = 0; i < size(); i++) {
      identity = reduce.applyAsDouble(identity, map.applyAsDouble(get(i)));
    }
    return identity;
  }

  @Override
  public DoubleMatrix reduceColumns(ToDoubleFunction<? super DoubleMatrix> reduce) {
    DoubleMatrix mat = newEmptyMatrix(1, columns());
    for (int i = 0; i < columns(); i++) {
      mat.set(i, reduce.applyAsDouble(getColumnView(i)));
    }
    return mat;
  }

  @Override
  public DoubleMatrix reduceRows(ToDoubleFunction<? super DoubleMatrix> reduce) {
    DoubleMatrix mat = newEmptyMatrix(rows(), 1);
    for (int i = 0; i < rows(); i++) {
      mat.set(i, reduce.applyAsDouble(getRowView(i)));
    }
    return mat;
  }

  @Override
  public DoubleMatrix mmul(DoubleMatrix other) {
    /*
     * Sometimes this is a huge gain!
     */
    if (other instanceof Diagonal) {
      return mmul((Diagonal) other);
    }
    return mmul(1, other, 1);
  }

  @Override
  public DoubleMatrix mmul(Diagonal diagonal) {
    if (columns() != diagonal.rows()) {
      throw new NonConformantException(this, diagonal);
    }
    DoubleMatrix matrix = newEmptyMatrix(this.rows(), diagonal.columns());
    int rows = this.rows(), columns = diagonal.columns();
    for (int column = 0; column < columns; column++) {
      if (column < this.columns()) {
        for (int row = 0; row < rows; row++) {
          double xv = this.get(row, column);
          double dv = diagonal.get(column);
          matrix.set(row, column, xv * dv);
        }
      } else {
        break;
      }
    }
    return matrix;
  }

  @Override
  public DoubleMatrix mmul(double alpha, DoubleMatrix other, double beta) {
    return mmul(alpha, Transpose.NO, other, beta, Transpose.NO);
  }

  @Override
  public DoubleMatrix mmul(Transpose a, DoubleMatrix other, Transpose b) {
    return mmul(1, a, other, 1, b);
  }

  @Override
  public DoubleMatrix mmul(double alpha, Transpose a, DoubleMatrix other, double beta, Transpose b) {
    int thisRows = rows();
    int thisCols = columns();
    if (a.transpose()) {
      thisRows = columns();
      thisCols = rows();
    }
    int otherRows = other.rows();
    int otherColumns = other.columns();
    if (b.transpose()) {
      otherRows = other.columns();
      otherColumns = other.rows();
    }

    if (thisCols != otherRows) {
      throw new NonConformantException(thisRows, thisCols, otherRows, otherColumns);
    }

    DoubleMatrix result = newEmptyMatrix(thisRows, otherColumns);
    for (int row = 0; row < thisRows; row++) {
      for (int col = 0; col < otherColumns; col++) {
        double sum = 0.0;
        for (int k = 0; k < thisCols; k++) {
          int thisIndex =
              a.transpose() ? rowMajor(row, k, thisRows, thisCols) : columnMajor(row, k, thisRows,
                  thisCols);
          int otherIndex =
              b.transpose() ? rowMajor(k, col, otherRows, otherColumns) : columnMajor(k, col,
                  otherRows, otherColumns);
          sum += alpha * get(thisIndex) * beta * other.get(otherIndex);
        }
        result.set(row, col, sum);
      }
    }
    return result;
  }

  @Override
  public DoubleMatrix mul(DoubleMatrix other) {
    return mul(1, other, 1);
  }

  @Override
  public DoubleMatrix mul(double alpha, DoubleMatrix other, double beta) {
    return copy().muli(alpha, other, beta);
  }

  @Override
  public DoubleMatrix mul(Vector other, Axis axis) {
    return mul(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix mul(double alpha, Vector other, double beta, Axis axis) {
    return copy().muli(alpha, other, beta, axis);
  }

  @Override
  public DoubleMatrix mul(double scalar) {
    return copy().muli(scalar);
  }

  @Override
  public DoubleMatrix muli(DoubleMatrix other) {
    return muli(1.0, other, 1.0);
  }

  @Override
  public DoubleMatrix muli(double scalar) {
    // TODO: fix loop
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        set(i, j, get(i, j) * scalar);
      }
    }
    return this;
  }

  @Override
  public DoubleMatrix muli(double alpha, DoubleMatrix other, double beta) {
    Check.equalSize(this, other);
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        set(i, j, alpha * get(i, j) * other.get(i, j) * beta);
      }
    }
    return this;
  }

  @Override
  public DoubleMatrix muli(Vector other, Axis axis) {
    return muli(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix muli(double alpha, Vector other, double beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (alpha * get(i)) * (other.getAsDouble(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (alpha * get(i)) * (other.getAsDouble(i / rows()) * beta));
      }
    }
    return this;
  }

  @Override
  public DoubleMatrix add(DoubleMatrix other) {
    return add(1, other, 1);
  }

  @Override
  public DoubleMatrix add(double scalar) {
    DoubleMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, get(i, j) + scalar);
      }
    }
    return matrix;
  }

  @Override
  public DoubleMatrix add(Vector other, Axis axis) {
    return add(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix add(double alpha, Vector other, double beta, Axis axis) {
    return copy().addi(alpha, other, beta, axis);
  }

  @Override
  public DoubleMatrix add(double alpha, DoubleMatrix other, double beta) {
    Check.equalSize(this, other);
    DoubleMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) + other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  @Override
  public DoubleMatrix addi(DoubleMatrix other) {
    addi(1, other, 1);
    return this;
  }

  @Override
  public DoubleMatrix addi(double scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        this.set(i, j, get(i, j) + scalar);
      }
    }
    return this;
  }

  @Override
  public DoubleMatrix addi(Vector other, Axis axis) {
    return addi(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix addi(double alpha, Vector other, double beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (alpha * get(i)) + (other.getAsDouble(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (alpha * get(i)) + (other.getAsDouble(i / rows()) * beta));
      }
    }
    return this;
  }

  @Override
  public DoubleMatrix addi(double alpha, DoubleMatrix other, double beta) {
    Check.equalSize(this, other);
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        set(i, j, alpha * get(i, j) + other.get(i, j) * beta);
      }
    }
    return this;
  }

  @Override
  public DoubleMatrix sub(DoubleMatrix other) {
    return sub(1, other, 1);
  }

  @Override
  public DoubleMatrix sub(double scalar) {
    return add(-scalar);
  }

  @Override
  public DoubleMatrix sub(Vector other, Axis axis) {
    return sub(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix sub(double alpha, Vector other, double beta, Axis axis) {
    return copy().subi(alpha, other, beta, axis);
  }

  @Override
  public DoubleMatrix sub(double alpha, DoubleMatrix other, double beta) {
    Check.equalSize(this, other);
    DoubleMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) - other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  @Override
  public DoubleMatrix slice(Collection<Integer> rows, Collection<Integer> columns) {
    return super.slice(rows, columns).asDoubleMatrix();
  }

  @Override
  public DoubleMatrix slice(Collection<Integer> indexes) {
    return super.slice(indexes).asDoubleMatrix();
  }

  @Override
  public DoubleMatrix slice(Range range) {
    return new FlatSliceDoubleMatrix(this, range);
  }

  @Override
  public DoubleMatrix slice(Range rows, Range columns) {
    return new SliceDoubleMatrix(this, rows, columns);
  }

  @Override
  public DoubleMatrix slice(Range range, Axis axis) {
    if (axis == Axis.ROW) {
      return new SliceDoubleMatrix(this, range, Range.range(columns()));
    } else {
      return new SliceDoubleMatrix(this, Range.range(rows()), range);
    }
  }

  @Override
  public DoubleMatrix slice(Collection<Integer> indexes, Axis axis) {
    return super.slice(indexes, axis).asDoubleMatrix();
  }

  @Override
  public DoubleMatrix subi(DoubleMatrix other) {
    addi(1, other, -1);
    return this;
  }

  @Override
  public DoubleMatrix subi(double scalar) {
    addi(-scalar);
    return this;
  }

  @Override
  public DoubleMatrix subi(Vector other, Axis axis) {
    return subi(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix subi(double alpha, Vector other, double beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (alpha * get(i)) - (other.getAsDouble(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (alpha * get(i)) - (other.getAsDouble(i / rows()) * beta));
      }
    }
    return this;
  }

  @Override
  public DoubleMatrix subi(double alpha, DoubleMatrix other, double beta) {
    addi(alpha, other, -1 * beta);
    return this;
  }

  @Override
  public DoubleMatrix rsub(double scalar) {
    DoubleMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, scalar - get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public DoubleMatrix rsub(Vector other, Axis axis) {
    return rsub(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix rsub(double alpha, Vector other, double beta, Axis axis) {
    return copy().rsubi(alpha, other, beta, axis);
  }

  @Override
  public DoubleMatrix rsubi(double scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        set(i, j, scalar - get(i, j));
      }
    }
    return this;
  }

  @Override
  public DoubleMatrix rsubi(Vector other, Axis axis) {
    return rsubi(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix rsubi(double alpha, Vector other, double beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (other.getAsDouble(i % rows()) * beta) - (alpha * get(i)));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (other.getAsDouble(i / rows()) * beta) - (alpha * get(i)));
      }
    }
    return this;
  }

  @Override
  public DoubleMatrix div(DoubleMatrix other) {
    Check.equalSize(this, other);
    DoubleMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, get(i, j) / other.get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public DoubleMatrix div(double other) {
    return mul(1.0 / other);
  }

  @Override
  public DoubleMatrix div(Vector other, Axis axis) {
    return div(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix div(double alpha, Vector other, double beta, Axis axis) {
    return copy().divi(alpha, other, beta, axis);
  }

  @Override
  public DoubleMatrix divi(DoubleMatrix other) {
    for (int i = 0; i < size(); i++) {
      set(i, get(i) / other.get(i));
    }
    return this;
  }

  @Override
  public DoubleMatrix divi(double other) {
    return muli(1 / other);
  }

  @Override
  public DoubleMatrix divi(Vector other, Axis axis) {
    return divi(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix divi(double alpha, Vector other, double beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (alpha * get(i)) / (other.getAsDouble(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (alpha * get(i)) / (other.getAsDouble(i / rows()) * beta));
      }
    }
    return this;
  }

  @Override
  public DoubleMatrix rdiv(double other) {
    DoubleMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, other / get(i));
    }
    return matrix;
  }

  @Override
  public DoubleMatrix rdiv(Vector other, Axis axis) {
    return rdiv(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix rdiv(double alpha, Vector other, double beta, Axis axis) {
    return copy().rdivi(alpha, other, beta, axis);
  }

  @Override
  public DoubleMatrix rdivi(double other) {
    for (int i = 0; i < size(); i++) {
      set(i, other / get(i));
    }
    return this;
  }

  @Override
  public DoubleMatrix rdivi(Vector other, Axis axis) {
    return rdivi(1, other, 1, axis);
  }

  @Override
  public DoubleMatrix rdivi(double alpha, Vector other, double beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows());
      for (int i = 0; i < size(); i++) {
        this.set(i, (other.getAsDouble(i % rows()) * beta) / (alpha * get(i)));
      }
    } else {
      checkArgument(other.size() == columns());
      for (int i = 0; i < size(); i++) {
        this.set(i, (other.getAsDouble(i / rows()) * beta) / (alpha * get(i)));
      }
    }
    return this;
  }

  @Override
  public DoubleMatrix negate() {
    DoubleMatrix n = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      n.set(i, -get(i));
    }
    return n;
  }

  @Override
  public double[] asDoubleArray() {
    double[] array = new double[size()];
    for (int i = 0; i < size(); i++) {
      array[i] = get(i);
    }
    return array;
  }

  @Override
  public int hashCode() {
    int result = 1;
    for (int i = 0; i < size(); i++) {
      long bits = Double.doubleToLongBits(get(i));
      result = 31 * result + (int) (bits ^ (bits >>> 32));
    }

    return Objects.hash(rows(), columns(), result);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof DoubleMatrix) {
      DoubleMatrix mat = (DoubleMatrix) obj;
      if (!mat.hasEqualShape(this)) {
        return false;
      }
      for (int i = 0; i < size(); i++) {
        if (get(i) != mat.get(i)) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  public DoubleMatrix newEmptyVector(int size) {
    return newEmptyMatrix(size, 1);
  }

  @Override
  public String toString() {
    ImmutableTable.Builder<Integer, Integer, String> builder = ImmutableTable.builder();
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        if (get(i, j) < 0) {
          builder.put(i, j, String.format("%1.4f", get(i, j)));
        } else {
          builder.put(i, j, String.format(" %1.4f", get(i, j)));
        }
      }
    }
    StringBuilder out = new StringBuilder();
    Utils.prettyPrintTable(out, builder.build(), 0, 2, false, false);
    out.append("shape: ").append(getShape()).append(" type: double");
    return out.toString();
  }

  @Override
  public void swap(int a, int b) {
    double tmp = get(a);
    set(a, get(b));
    set(b, tmp);
  }

  @Override
  public Iterator<Double> iterator() {
    return new Iterator<Double>() {
      private int index = 0;

      @Override
      public boolean hasNext() {
        return index < size();
      }

      @Override
      public Double next() {
        return get(index++);
      }
    };
  }

  public static class IncrementalBuilder implements Matrix.IncrementalBuilder {

    private DoubleArrayList buffer = new DoubleArrayList();

    @Override
    public void add(Matrix from, int i, int j) {
      buffer.add(from.getAsDouble(i, j));
    }

    @Override
    public void add(Matrix from, int index) {
      buffer.add(from.getAsDouble(index));
    }

    @Override
    public DoubleMatrix build() {
      return new ArrayDoubleMatrix(buffer.size(), 1, buffer.toArray());
    }

    public void add(double value) {
      buffer.add(value);
    }
  }

  protected static class SliceDoubleMatrix extends AbstractDoubleMatrix {

    private final Range row, column;
    private final DoubleMatrix parent;

    public SliceDoubleMatrix(DoubleMatrix parent, Range row, Range column) {
      this(parent, checkNotNull(row).size(), row, checkNotNull(column).size(), column);
    }

    public SliceDoubleMatrix(DoubleMatrix parent, int rows, Range row, int columns, Range column) {
      super(rows, columns);
      this.row = checkNotNull(row);
      this.column = checkNotNull(column);
      this.parent = checkNotNull(parent);
    }

    @Override
    public void set(int i, int j, double value) {
      parent.set(sliceIndex(row.step(), i, parent.rows()),
          sliceIndex(column.step(), j, parent.columns()), value);
    }

    @Override
    public void set(int index, double value) {
      int row = index % rows();
      int col = index / rows();
      set(row, col, value);
    }

    @Override
    public DoubleMatrix reshape(int rows, int columns) {
      Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
      return new SliceDoubleMatrix(parent, rows, row, columns, column);
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public DoubleMatrix newEmptyMatrix(int rows, int columns) {
      return parent.newEmptyMatrix(rows, columns);
    }

    @Override
    public double get(int i, int j) {
      return parent.get(sliceIndex(row.step(), i, parent.rows()),
          sliceIndex(column.step(), j, parent.columns()));
    }

    @Override
    public double get(int index) {
      int row = index % rows();
      int col = index / rows();
      return get(row, col);
    }

    @Override
    public boolean isArrayBased() {
      return parent.isArrayBased();
    }
  }

  protected class FlatSliceDoubleMatrix extends AbstractDoubleMatrix {
    private final DoubleMatrix parent;
    private final Range range;

    public FlatSliceDoubleMatrix(DoubleMatrix parent, int size, Range range) {
      super(size);
      this.parent = checkNotNull(parent);
      this.range = checkNotNull(range);
    }

    public FlatSliceDoubleMatrix(DoubleMatrix parent, Range range) {
      this(parent, checkNotNull(range).size(), range);
    }

    @Override
    public void set(int i, int j, double value) {
      set(columnMajor(i, j, rows(), columns()), value);
    }

    @Override
    public void set(int index, double value) {
      parent.set(sliceIndex(range.step(), index, parent.size()), value);
    }

    @Override
    public DoubleMatrix reshape(int rows, int columns) {
      return copy().reshape(rows, columns);
    }

    @Override
    public boolean isView() {
      return true;
    }

    @Override
    public DoubleMatrix newEmptyMatrix(int rows, int columns) {
      return parent.newEmptyMatrix(rows, columns);
    }

    @Override
    public double get(int i, int j) {
      return get(columnMajor(i, j, rows(), columns()));
    }

    @Override
    public double get(int index) {
      return parent.get(sliceIndex(range.step(), index, parent.size()));
    }

    @Override
    public boolean isArrayBased() {
      return parent.isArrayBased();
    }
  }
}
