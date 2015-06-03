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

package org.briljantframework.matrix.base;

import org.briljantframework.Check;
import org.briljantframework.matrix.AbstractDoubleMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.api.MatrixFactory;

import static org.briljantframework.matrix.Indexer.columnMajor;

/**
 * Implementation of {@link org.briljantframework.matrix.DoubleMatrix} using a single {@code
 * double}
 * array. Indexing is
 * calculated in column-major order, hence varying column faster than row is preferred when
 * iterating.
 *
 * @author Isak Karlsson
 */
class BaseDoubleMatrix extends AbstractDoubleMatrix {

  private double[] values;

  BaseDoubleMatrix(MatrixFactory bj, int size) {
    super(bj, size);
    values = new double[size];
  }

  BaseDoubleMatrix(MatrixFactory bj, int rows, int columns) {
    this(bj, new double[Math.multiplyExact(rows, columns)], rows, columns);
  }

  BaseDoubleMatrix(MatrixFactory bj, double[] values) {
    super(bj, values.length);
    this.values = values;
  }

  private BaseDoubleMatrix(MatrixFactory bj, double[] values, int rows, int columns) {
    super(bj, rows, columns);
    Check.size(values.length, Math.multiplyExact(rows, columns));
    this.values = values;
  }

  @Override
  public DoubleMatrix reshape(int rows, int columns) {
    Check.size(CHANGED_TOTAL_SIZE, Math.multiplyExact(rows, columns), this);
    return new BaseDoubleMatrix(getMatrixFactory(), values, rows, columns);
  }

  @Override
  public DoubleMatrix newEmptyMatrix(int rows, int columns) {
    return new BaseDoubleMatrix(getMatrixFactory(), rows, columns);
  }

  @Override
  public boolean isView() {
    return false;
  }

  public DoubleMatrix copy() {
    return new BaseDoubleMatrix(getMatrixFactory(), values.clone(), rows(), columns());
  }

  @Override
  public void set(int i, int j, double value) {
    values[columnMajor(i, j, rows(), columns())] = value;
  }

  @Override
  public void set(int index, double value) {
    values[index] = value;
  }

  @Override
  public double get(int i, int j) {
    return values[columnMajor(i, j, rows(), columns())];
  }

  @Override
  public double get(int index) {
    return values[index];
  }

}
