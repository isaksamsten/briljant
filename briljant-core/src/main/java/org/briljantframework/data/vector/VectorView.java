/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.data.vector;

import java.util.Objects;

/**
 * Provides a view over a vector.
 * 
 * @author Isak Karlsson
 */
public abstract class VectorView extends AbstractVector {

  public static final String OVERRIDE_TO_SUPPORT = "Override to support";
  protected final Vector parent;
  protected final int offset, length;

  protected VectorView(Vector parent) {
    this(parent, 0, 1);
  }

  public VectorView(Vector parent, int offset, int length) {
    this.parent = Objects.requireNonNull(parent);
    if (offset < 0 || offset > parent.size()) {
      throw new IndexOutOfBoundsException();
    }
    int len = offset + length;
    if (len < 0 || len > parent.size()) {
      throw new IndexOutOfBoundsException();
    }

    this.offset = offset;
    this.length = len;
  }

  @Override
  public boolean hasNA() {
    return parent.hasNA();
  }

  public Vector getDelegate() {
    return parent;
  }

  @Override
  public int size() {
    return length;
  }

  @Override
  public Type getType() {
    return parent.getType();
  }

  @Override
  public Builder newCopyBuilder() {
    throw new UnsupportedOperationException(OVERRIDE_TO_SUPPORT);
  }

  @Override
  public Builder newBuilder() {
    throw new UnsupportedOperationException(OVERRIDE_TO_SUPPORT);
  }

  @Override
  public Builder newBuilder(int size) {
    throw new UnsupportedOperationException(OVERRIDE_TO_SUPPORT);
  }

  @Override
  protected int compareAt(int a, Vector other, int b) {
    return parent.loc().compare(a, other, b);
  }

  @Override
  protected boolean isNaAt(int index) {
    return parent.loc().isNA(offset + index);
  }

  @Override
  protected int getAsIntAt(int i) {
    return parent.loc().getAsInt(offset + i);
  }

  @Override
  protected double getAsDoubleAt(int i) {
    return parent.loc().getAsDouble(offset + i);
  }

  @Override
  protected <T> T getAt(Class<T> cls, int index) {
    return parent.loc().get(cls, offset + index);
  }

  @Override
  protected String toStringAt(int index) {
    return parent.loc().toString(offset + index);
  }
}
