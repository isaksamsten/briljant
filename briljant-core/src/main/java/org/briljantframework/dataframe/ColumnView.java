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

import org.briljantframework.Check;
import org.briljantframework.complex.Complex;
import org.briljantframework.vector.AbstractVector;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

/**
 * View into a DataFrame.
 *
 * @author Isak Karlsson
 */
class ColumnView extends AbstractVector {

  private final DataFrame parent;
  private final int column;

  public ColumnView(DataFrame parent, int column) {
    super(parent.getRecordIndex());
    this.parent = parent;
    this.column = column;
  }

  @Override
  public Complex getAsComplex(int index) {
    return parent.getAsComplex(index, column);
  }

  @Override
  public <T> T get(Class<T> cls, int index) {
    return parent.get(cls, index, column);
  }

  @Override
  public String toString(int index) {
    return parent.toString(index, column);
  }

  @Override
  public boolean isNA(int index) {
    return parent.isNA(index, column);
  }

  @Override
  public double getAsDouble(int index) {
    return parent.getAsDouble(index, column);
  }

  @Override
  public int getAsInt(int index) {
    return parent.getAsInt(index, column);
  }

  @Override
  public Bit getAsBit(int index) {
    return parent.getAsBit(index, column);
  }

  @Override
  public int size() {
    return parent.rows();
  }

  @Override
  public VectorType getType() {
    return parent.getType(column);
  }

  @Override
  public VectorType getType(int index) {
    Check.size(index, size());
    return getType();
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
  public int compare(int a, int b) {
    return getType().compare(a, this, b, this);
  }

  @Override
  public int compare(int a, Vector other, int b) {
    return getType().compare(a, this, b, other);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("[");
    builder.append(toString(0));
    for (int i = 1; i < size(); i++) {
      builder.append(",").append(toString(i));
    }
    return builder.append("]").toString();
  }
}
