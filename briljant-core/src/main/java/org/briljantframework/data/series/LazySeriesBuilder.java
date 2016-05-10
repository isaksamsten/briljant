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

import org.briljantframework.data.reader.DataEntry;

/**
 * Created by isak on 4/22/16.
 */
final class LazySeriesBuilder implements Series.Builder {
  private final Series data;
  private Series.Builder builder = null;

  LazySeriesBuilder(Series data) {
    this.data = data;
  }

  private void initLazyBuilder() {
    if (builder == null) {
      builder = data.newBuilder();
      builder.addAll(data);
    }
  }

  @Override
  public Series.Builder addNA() {
    initLazyBuilder();
    builder.addNA();
    return this;
  }

  @Override
  public Series.Builder setNA(Object key) {
    initLazyBuilder();
    builder.setNA(key);
    return this;
  }

  @Override
  public Series.Builder addFrom(Series from, Object key) {
    initLazyBuilder();
    builder.addFrom(from, key);
    return this;
  }

  @Override
  public Series.Builder addFromLocation(Series from, int pos) {
    initLazyBuilder();
    builder.addFromLocation(from, pos);
    return this;
  }

  @Override
  public Series.Builder setFromLocation(Object atKey, Series from, int fromIndex) {
    initLazyBuilder();
    builder.setFromLocation(atKey, from, fromIndex);
    return this;
  }

  @Override
  public Series.Builder setFrom(Object atKey, Series from, Object fromIndex) {
    initLazyBuilder();
    builder.setFrom(atKey, from, fromIndex);
    return this;
  }

  @Override
  public Series.Builder set(Object key, Object value) {
    initLazyBuilder();
    builder.set(key, value);
    return this;
  }

  @Override
  public Series.Builder setInt(Object key, int value) {
    initLazyBuilder();
    builder.setInt(key, value);
    return this;
  }

  @Override
  public Series.Builder setDouble(Object key, double value) {
    initLazyBuilder();
    builder.setDouble(key, value);
    return this;
  }

  @Override
  public Series.Builder add(Object value) {
    initLazyBuilder();
    builder.add(value);
    return this;
  }

  @Override
  public Series.Builder addDouble(double value) {
    initLazyBuilder();
    builder.addDouble(value);
    return this;
  }

  @Override
  public Series.Builder addInt(int value) {
    initLazyBuilder();
    builder.addInt(value);
    return this;
  }

  @Override
  public Series.Builder addAll(Series from) {
    initLazyBuilder();
    builder.addAll(from);
    return this;
  }

  @Override
  public Series.Builder remove(Object key) {
    initLazyBuilder();
    builder.remove(key);
    return this;
  }

  @Override
  public Series.Builder readAll(DataEntry entry) {
    initLazyBuilder();
    builder.readAll(entry);
    return this;
  }

  @Override
  public LocationSetter loc() {
    initLazyBuilder();
    return builder.loc();
  }

  @Override
  public Series.Builder read(DataEntry entry) {
    initLazyBuilder();
    builder.read(entry);
    return this;
  }

  @Override
  public int size() {
    return builder != null ? builder.size() : data.size();
  }

  @Override
  public Series build() {
    return builder != null ? builder.build() : data;
  }
}
