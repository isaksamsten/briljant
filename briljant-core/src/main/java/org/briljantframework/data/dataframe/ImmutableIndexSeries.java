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
package org.briljantframework.data.dataframe;

import java.util.Objects;

import org.briljantframework.data.index.Index;
import org.briljantframework.data.series.AbstractSeries;
import org.briljantframework.data.series.Series;
import org.briljantframework.data.series.Type;

/**
 * A series which cannot be reindexed.
 */
final class ImmutableIndexSeries extends AbstractSeries {
  private final Series delegate;

  ImmutableIndexSeries(Series series, Index index) {
    super(index, series.getOffset(), series.getShape(), series.getStride());
    this.delegate = Objects.requireNonNull(series);
  }

  @Override
  protected boolean isElementNA(int i) {
    return delegate.loc().isNA(i);
  }

  @Override
  protected int getIntElement(int i) {
    return delegate.loc().getInt(i);
  }

  @Override
  protected double getDoubleElement(int i) {
    return delegate.loc().getDouble(i);
  }

  @Override
  protected <T> T getElement(Class<T> cls, int index) {
    return delegate.loc().get(cls, index);
  }

  @Override
  public Series reindex(Index index) {
    return new ImmutableIndexSeries(delegate, index);
  }

  @Override
  protected String getStringElement(int index) {
    return delegate.loc().toString(index);
  }

  @Override
  public Series asView(int offset, int[] shape, int[] stride) {
    return new ImmutableIndexSeries(delegate.asView(offset, shape, stride), getIndex());
  }

  @Override
  public Series newEmptyArray(int... shape) {
    return delegate.newEmptyArray(shape);
  }

  @Override
  protected int elementSize() {
    return delegate.size();
  }

  @Override
  public Type getType() {
    return delegate.getType();
  }
}
