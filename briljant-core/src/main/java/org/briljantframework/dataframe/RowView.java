/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.dataframe;

import org.briljantframework.vector.AbstractVector;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

/**
 * @author Isak Karlsson
 */
class RowView extends AbstractVector {

  private final DataFrame parent;
  private final int row;
  private final VectorType type;

  public RowView(DataFrame parent, int row, VectorType type) {
    super(parent.getColumnIndex());
    this.parent = parent;
    this.type = type;
    this.row = row;
  }

  public RowView(DataFrame parent, int row) {
    this(parent, row, VectorType.OBJECT);
  }

  @Override
  public VectorType getType() {
    return type;
  }

  @Override
  public <T> T getAt(Class<T> cls, int index) {
    return parent.loc().get(cls, row, index);
  }

  @Override
  public String toStringAt(int index) {
    return parent.loc().get(index).loc().toString(row);
  }

  @Override
  public boolean isNaAt(int index) {
    return parent.loc().isNA(row, index);
  }

  @Override
  public double getAsDoubleAt(int i) {
    return parent.loc().getAsDouble(row, i);
  }

  @Override
  public int getAsIntAt(int i) {
    return parent.loc().getAsInt(row, i);
  }

  @Override
  public int size() {
    return parent.columns();
  }

  @Override
  public VectorType getType(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Builder newCopyBuilder() {
    return newBuilder().addAll(this);
  }

  @Override
  public Builder newBuilder() {
    return getType().newBuilder();
  }

  @Override
  public Builder newBuilder(int size) {
    return getType().newBuilder(size);
  }

  @Override
  public int compareAt(int a, Vector other, int b) {
    throw new UnsupportedOperationException("TODO");
  }
}
