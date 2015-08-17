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

package org.briljantframework.vector;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.array.Array;
import org.briljantframework.exceptions.IllegalTypeException;

import java.util.Objects;

/**
 * Created by isak on 1/21/15.
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
  public <T> T get(Class<T> cls, int index) {
    return parent.get(cls, offset + index);
  }

  @Override
  public String toString(int index) {
    return parent.toString(offset + index);
  }

  @Override
  public boolean isTrue(int index) {
    return parent.isTrue(offset + index);
  }

  @Override
  public boolean isNA(int index) {
    return parent.isNA(offset + index);
  }

  @Override
  public boolean hasNA() {
    return parent.hasNA();
  }

  @Override
  public double getAsDouble(int index) {
    return parent.getAsDouble(offset + index);
  }

  @Override
  public int getAsInt(int index) {
    return parent.getAsInt(offset + index);
  }

  @Override
  public Bit getAsBit(int index) {
    return parent.getAsBit(offset + index);
  }

  @Override
  public Complex getAsComplex(int index) {
    return parent.getAsComplex(offset + index);
  }

  @Override
  public int size() {
    return length;
  }

  @Override
  public VectorType getType() {
    return parent.getType();
  }

  @Override
  public <U> Array<U> toArray(Class<U> cls) throws IllegalTypeException {
    return parent.toArray(cls);
  }

  @Override
  public int compare(int a, int b) {
    return parent.compare(a, b);
  }

  @Override
  public int compare(int a, Vector other, int b) {
    return parent.compare(a, other, b);
  }

  @Override
  public VectorType getType(int index) {
    return parent.getType(offset + index);
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
}
