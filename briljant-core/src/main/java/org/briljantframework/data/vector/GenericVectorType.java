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

package org.briljantframework.data.vector;

import org.briljantframework.data.index.ObjectComparator;

import java.util.Comparator;

/**
 * @author Isak Karlsson
 */
class GenericVectorType extends VectorType {

  private final static Comparator<Object> CMP = ObjectComparator.getInstance();
  private Class<?> cls;

  public GenericVectorType(Class<?> cls) {
    this.cls = cls;
  }

  @Override
  public Vector.Builder newBuilder() {
    return new GenericVector.Builder(cls);
  }

  @Override
  public Vector.Builder newBuilder(int size) {
    return new GenericVector.Builder(cls, size);
  }

  @Override
  public Class<?> getDataClass() {
    return cls;
  }

  @Override
  public boolean isNA(Object value) {
    return value == null;
  }

  @Override
  public int compare(int a, Vector va, int b, Vector ba) {
    Object ca = va.loc().get(Object.class, a);
    Object cb = ba.loc().get(Object.class, b);
    return CMP.compare(ca, cb);
  }

  @Override
  public Scale getScale() {
    return Number.class.isAssignableFrom(cls) ? Scale.NUMERICAL : Scale.NOMINAL;
  }

  @Override
  public int hashCode() {
    return cls.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    return obj instanceof GenericVectorType && ((GenericVectorType) obj).cls.equals(cls);
  }

  @Override
  public String toString() {
    return String.format("%s", cls.getSimpleName());
  }
}
