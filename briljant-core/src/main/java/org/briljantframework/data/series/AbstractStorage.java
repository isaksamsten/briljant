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

import java.util.AbstractList;
import java.util.function.Supplier;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.data.Is;
import org.briljantframework.data.index.NaturalOrdering;

/**
 * Created by isak on 2016-10-12.
 */
public abstract class AbstractStorage extends AbstractList<Object> implements Storage {

  @Override
  public <T> T get(Class<T> cls, int i) {
    return Convert.to(cls, get(i));
  }

  @Override
  public double setDouble(int index, double value) {
    double oldValue = getDouble(index);
    set(index, value);
    return oldValue;
  }

  @Override
  public double getDouble(int i) {
    return Convert.to(Double.class, get(i));
  }

  @Override
  public int setInt(int index, int value) {
    int oldValue = getInt(index);
    set(index, value);
    return oldValue;
  }

  @Override
  public int getInt(int i) {
    return Convert.to(Integer.class, get(i));
  }

  @Override
  public <T> T get(Class<T> cls, int index, Supplier<T> defaultValue) {
    T value = get(cls, index);
    return Is.NA(value) ? defaultValue.get() : value;
  }

  @Override
  public Complex getComplex(int i) {
    return Convert.to(Complex.class, get(i));
  }

  @Override
  public boolean isNA(int i) {
    return Is.NA(get(i));
  }

  @Override
  public int compare(int a, int b) {
    Object ca = get(a);
    Object cb = get(b);
    return NaturalOrdering.ascending().compare(ca, cb);
  }

  @Override
  public int compare(int a, Series other, int b) {
    Object ca = get(a);
    Object cb = other.values().get(b);
    return NaturalOrdering.ascending().compare(ca, cb);
  }

  @Override
  public boolean equals(int a, Storage other, int b) {
    return Is.equal(get(a), other.get(b));
  }

  @Override
  public void setFrom(int to, Storage source, int from) {
    set(to, source.get(from));
  }
}
