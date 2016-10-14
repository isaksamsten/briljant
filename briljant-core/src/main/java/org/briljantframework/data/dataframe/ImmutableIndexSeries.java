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
import org.briljantframework.data.series.Storage;
import org.briljantframework.data.series.Type;

/**
 * A series for which the index cannot be changed
 */
// TODO: delegate all methods.
final class ImmutableIndexSeries extends AbstractSeries {
  private final Series delegate;
  private final Index index;

  private ImmutableIndexSeries(Series series, Index index) {
    this.index = Objects.requireNonNull(index);
    this.delegate = Objects.requireNonNull(series);
  }

  static Series newInstance(Series series, Index index) {
    if (series instanceof ImmutableIndexSeries && series.index() == index) {
      return series;
    }
    return new ImmutableIndexSeries(series, index);
  }

  // @Override
  // protected void setElement(int index, Object value) {
  // delegate.values().set(index, value);
  // }
  //
  // @Override
  // protected void setDoubleElement(int index, double value) {
  // delegate.values().setDouble(index, value);
  // }
  //
  // @Override
  // protected void setIntElement(int index, int value) {
  // delegate.values().setInt(index, value);
  // }
  //
  // @Override
  // protected int getIntElement(int i) {
  // return delegate.values().getInt(i);
  // }
  //
  // @Override
  // protected double getDoubleElement(int i) {
  // return delegate.values().getDouble(i);
  // }
  //
  // @Override
  // protected <T> T getElement(Class<T> cls, int index) {
  // return delegate.values().get(cls, index);
  // }

  @Override
  public Series reindex(Index index) {
    return newInstance(delegate, index);
  }

  @Override
  public int size() {
    return delegate.size();
  }

  @Override
  public Index index() {
    return index;
  }


  @Override
  public Object get(Object key) {
    return delegate.values().get(index().getLocation(key));
  }

  @Override
  public void set(Object key, Object value) {
    delegate.values().set(index().getLocation(key), value);
  }

  @Override
  public Storage values() {
    return delegate.values();
  }

  @Override
  public Builder newCopyBuilder() {
    return newBuilder().setAll(this);
  }

  @Override
  public Type getType() {
    return delegate.getType();
  }
}
