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
import org.briljantframework.vector.AbstractVector;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

/**
 * View into a DataFrame.
 *
 * @author Isak Karlsson
 */
class ColumnView extends AbstractVector {

  private final DataFrame parent;
  private final VectorType type;
  private final int column;

  public ColumnView(DataFrame parent, VectorType type, int column) {
    super(parent.getRecordIndex());
    this.parent = parent;
    this.type = type;
    this.column = column;
  }

  @Override
  public <T> T getAt(Class<T> cls, int index) {
    return parent.loc().get(cls, index, column);
  }

  @Override
  public String toStringAt(int index) {
    return parent.loc().toString(index, column);
  }

  @Override
  public boolean isNaAt(int index) {
    return parent.loc().isNA(index, column);
  }

  @Override
  public double getAsDoubleAt(int i) {
    return parent.loc().getAsDouble(i, column);
  }

  @Override
  public int getAsIntAt(int i) {
    return parent.loc().getAsInt(i, column);
  }

  @Override
  public int size() {
    return parent.rows();
  }

  @Override
  public VectorType getType() {
    return type;
  }

  @Override
  public VectorType getType(int index) {
    Check.size(index, size());
    return getType();
  }

  @Override
  public int hashCode() {
    return column;
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
    return getType().compare(a, this, b, other);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("[");
    builder.append(toStringAt(0));
    for (int i = 1; i < size(); i++) {
      builder.append(",").append(toStringAt(i));
    }
    return builder.append("]").toString();
  }
}
