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
import org.briljantframework.Bj;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.exceptions.IllegalTypeException;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.resolver.Resolver;
import org.briljantframework.io.resolver.Resolvers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Isak Karlsson
 */
public class GenericVector extends AbstractVector {

  private final VectorType type;
  private final Class<?> cls;
  private final List<Object> values;
  private final int size;

  @SuppressWarnings("unchecked")
  public <T> GenericVector(Class<T> cls, List<? extends T> values) {
    this(cls, (List<Object>) values, true);
  }

  protected GenericVector(Class<?> cls, List<Object> values, boolean copy) {
    this(cls, values, values.size(), copy);
  }

  private GenericVector(Class<?> cls, List<Object> values, int size, boolean copy) {
    this.cls = cls;
    this.values = copy ? new ArrayList<>(values) : values;
    this.type = Vec.typeOf(cls);
    this.size = size;
  }

  @Override
  public <T> T get(Class<T> cls, int index) {
    Object obj = values.get(index);
    if (!cls.isInstance(obj)) {
      if (cls.equals(String.class)) {
        return cls.cast(obj.toString());
      } else {
        if (this.cls.equals(Number.class)) {
          Number num = Number.class.cast(obj);
          if (cls.equals(Double.class)) {
            return cls.cast(num.doubleValue());
          } else if (cls.equals(Integer.class)) {
            return cls.cast(num.intValue());
          }
        }
      }
      return Na.from(cls);
    }
    return cls.cast(obj);
  }

  @Override
  public String toString(int index) {
    Object o = values.get(index);
    return Is.NA(o) ? "NA" : o.toString();
  }

  @Override
  public Complex getAsComplex(int index) {
    Complex complex = get(Complex.class, index);
    if (Is.NA(complex)) {
      double v = getAsDouble(index);
      if (Is.NA(v)) {
        return Na.from(Complex.class);
      } else {
        return Complex.valueOf(v);
      }
    } else {
      return complex;
    }
  }

  @Override
  public double getAsDouble(int index) {
    Number number = get(Number.class, index);
    return Is.NA(number) ? Na.from(Double.class) : number.doubleValue();
  }

  @Override
  public int getAsInt(int index) {
    Number number = get(Number.class, index);
    return Is.NA(number) ? Na.from(Integer.class) : number.intValue();
  }

  @Override
  public boolean isNA(int index) {
    return values.get(index) == null;
  }

  @Override
  public Bit getAsBit(int index) {
    return Bit.valueOf(getAsInt(index));
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public VectorType getType() {
    return type;
  }

  @Override
  public DoubleArray asDoubleArray() throws IllegalTypeException {
    if (Number.class.isAssignableFrom(this.cls)) {
      return Bj.doubleArray(size())
          .assign(asList(Number.class).stream()
                      .mapToDouble(v -> Is.NA(v) ? Na.from(Double.class) : v.doubleValue())
                      .iterator()::next);
    }
    throw new IllegalTypeException(
        String.format("Can't convert vector of '%s' to matrix", getType()));
  }

  @Override
  @SuppressWarnings("unchecked")
  public int compare(int a, int b) {
    Comparable ca = get(Comparable.class, a);
    Comparable cb = get(Comparable.class, b);
    if (Is.NA(ca) && !Is.NA(cb)) {
      return -1;
    } else if (!Is.NA(ca) && Is.NA(cb)) {
      return 1;
    } else {
      return ca.compareTo(cb);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public int compare(int a, Vector other, int b) {
    Comparable ac = get(Comparable.class, a);
    Comparable bc = other.get(Comparable.class, b);
    boolean acNA = Is.NA(ac);
    boolean bcNA = Is.NA(bc);
    if (acNA && bcNA) {
      return 0;
    } else if (acNA) {
      return 1;
    } else if (bcNA) {
      return -1;
    } else {
      return ac.compareTo(bc);
    }
  }

  @Override
  public int hashCode() {
    return cls.hashCode() + values.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof GenericVector) {
      if (!this.cls.equals(((GenericVector) obj).cls)) {
        return false;
      } else {
        return values.equals(((GenericVector) obj).values);
      }
    }
    return super.equals(obj);
  }

  public static class Builder implements Vector.Builder {

    private static final Set<Class<?>> INVALID_CLASSES = new HashSet<>();

    static {
      INVALID_CLASSES.addAll(Arrays.asList(
          Integer.class, Integer.TYPE, Double.TYPE, Double.class, Complex.class, Bit.class
      ));
    }

    private final Class<?> cls;
    private List<Object> buffer;
    private Resolver<?> resolver = null;

    public <T> Builder(Class<T> cls, Resolver<T> resolver) {
      this.cls = ensureValidClass(cls);
      this.resolver = resolver;
    }

    public Builder(Class<?> cls) {
      this.cls = ensureValidClass(cls);
      buffer = new ArrayList<>();
    }

    private <T> Class<?> ensureValidClass(Class<T> cls) {
      if (INVALID_CLASSES.contains(cls)) {
        throw new IllegalArgumentException(
            String.format("GenericVector should not be used for: %s", cls));
      }
      return cls;
    }

    public Builder(Class<?> cls, int size) {
      this.cls = cls;
      buffer = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        buffer.add(null);
      }
    }

    @Override
    public Vector.Builder setNA(int index) {
      ensureCapacity(index);
      buffer.set(index, null);
      return this;
    }

    @Override
    public Vector.Builder addNA() {
      buffer.add(null);
      return this;
    }

    @Override
    public Vector.Builder add(Vector from, int fromIndex) {
      buffer.add(from.get(cls, fromIndex));
      return this;
    }

    @Override
    public Vector.Builder set(int atIndex, Vector from, int fromIndex) {
      ensureCapacity(atIndex);
      buffer.set(atIndex, from.get(cls, fromIndex));
      return this;
    }

    @Override
    public Vector.Builder set(int index, Object value) {
      ensureCapacity(index);
      if (value != null && cls.isInstance(value)) {
        buffer.set(index, value);
      } else if (value != null) {
        Resolver<?> resolver = this.resolver == null ? Resolvers.find(cls) : this.resolver;
        if (resolver == null) {
          buffer.set(index, null);
        } else {
          buffer.set(index, resolver.resolve(value));
        }
      } else {
        buffer.set(index, null);
      }
      return this;
    }

    @Override
    public Vector.Builder add(Object value) {
      return set(size(), value);
    }

    @Override
    public Vector.Builder addAll(Vector from) {
      for (int i = 0; i < from.size(); i++) {
        add(from.get(cls, i));
      }
      return this;
    }

    @Override
    public Vector.Builder remove(int index) {
      buffer.remove(index);
      return this;
    }

    @Override
    public int compare(int a, int b) {
      if (Comparable.class.isAssignableFrom(cls)) {
        Comparable va = (Comparable) buffer.get(a);
        Comparable vb = (Comparable) buffer.get(b);
        @SuppressWarnings("unchecked")
        int cmp = va.compareTo(vb);
        return cmp;
      }
      throw new UnsupportedOperationException();
    }

    @Override
    public void swap(int a, int b) {
      Collections.swap(buffer, a, b);
    }

    @Override
    public Vector.Builder read(DataEntry entry) throws IOException {
      return read(size(), entry);
    }

    @Override
    public Vector.Builder read(int index, DataEntry entry) throws IOException {
      ensureCapacity(index);
      buffer.set(index, entry.next(cls)); // TODO: do resolve here
      return this;
    }

    @Override
    public int size() {
      return buffer.size();
    }

    @Override
    public Vector getTemporaryVector() {
      return new GenericVector(cls, buffer, buffer.size(), false);
    }

    @Override
    public Vector build() {
      Vector vector = new GenericVector(cls, buffer, false);
      buffer = null;
      return vector;
    }

    private void ensureCapacity(int index) {
      while (index >= buffer.size()) {
        buffer.add(null);
      }
    }
  }
}
