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

import java.util.Comparator;

import org.briljantframework.Check;
import org.briljantframework.data.Is;
import org.briljantframework.data.Na;
import org.briljantframework.data.Transferable;
import org.briljantframework.data.index.Index;

/**
 * Representing vectors of a single (unique) value repeated n (0 <= n) times.
 * 
 * @author Isak Karlsson
 */
final class SingletonVector extends AbstractVector implements Transferable {

  private static final Vector EMPTY = new SingletonVector(null, 0);

  private final Object value;
  private final VectorType type;
  private final int size;

  SingletonVector(Object value, int size) {
    this(null, value, size);
  }

  SingletonVector(Index index, Object value, int size) {
    super(index);
    this.value = value;
    this.size = size;
    Class<?> cls = value != null ? value.getClass() : Object.class;
    type = VectorType.of(cls);
  }

  public static Vector empty() {
    return EMPTY;
  }

  @Override
  public <T> Vector sort(Class<T> cls, Comparator<T> cmp) {
    return shallowCopy(getIndex());
  }

  @Override
  public <T extends Comparable<T>> Vector sort(Class<T> cls) {
    return shallowCopy(getIndex());
  }

  @Override
  public boolean hasNA() {
    return Is.NA(value);
  }

  @Override
  protected boolean isNaAt(int index) {
    Check.validIndex(index, size());
    return Is.NA(value);
  }

  @Override
  protected int getAsIntAt(int i) {
    Check.validIndex(i, size());
    Number val = Convert.to(Number.class, value);
    return Is.NA(val) ? Na.INT : val.intValue();
  }

  @Override
  protected double getAsDoubleAt(int i) {
    Check.validIndex(i, size());
    Number val = Convert.to(Number.class, value);
    return Is.NA(val) ? Na.DOUBLE : val.doubleValue();
  }

  @Override
  protected <T> T getAt(Class<T> cls, int index) {
    Check.validIndex(index, size());
    return Convert.to(cls, value);
  }

  @Override
  protected Vector shallowCopy(Index index) {
    return new SingletonVector(index, value, size);
  }

  @Override
  protected String toStringAt(int index) {
    Check.validIndex(index, size());
    return Na.toString(value);
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public VectorType getType() {
    return type;
  }
}
