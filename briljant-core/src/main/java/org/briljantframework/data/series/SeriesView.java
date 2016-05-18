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
package org.briljantframework.data.series;

import java.util.Objects;

/**
 * Provides a view over a series.
 *
 * @author Isak Karlsson
 */
public abstract class SeriesView extends AbstractSeries {

  public static final String OVERRIDE_TO_SUPPORT = "Override to support";
  protected final Series parent;
  protected final int offset, length;

  protected SeriesView(Series parent) {
    this(parent, 0, 1);
  }

  public SeriesView(Series parent, int offset, int length) {
    super(parent.getOffset(), parent.getShape(), parent.getStride()); // TODO: 5/2/16 FIXME
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
    throw new UnsupportedOperationException("UNSUPPORTED: FIX ME");
  }

  @Override
  public Series asView(int offset, int[] shape, int[] stride) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Series newEmptyArray(int... shape) {
    return parent.newEmptyArray(shape);
  }

  @Override
  protected int elementSize() {
    return size();
  }

  @Override
  public boolean hasNA() {
    return parent.hasNA();
  }

  public Series getDelegate() {
    return parent;
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
  protected int compareElement(int a, Series other, int b) {
    return parent.loc().compare(a, other, b);
  }

  @Override
  protected boolean isElementNA(int i) {
    return parent.loc().isNA(offset + i);
  }

  @Override
  protected int getIntElement(int i) {
    return parent.loc().getInt(offset + i);
  }

  @Override
  protected double getDoubleElement(int i) {
    return parent.loc().getDouble(offset + i);
  }

  @Override
  protected <T> T getElement(Class<T> cls, int index) {
    return parent.loc().get(cls, offset + index);
  }

  @Override
  protected String getStringElement(int index) {
    return parent.loc().toString(offset + index);
  }
}
