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

import org.briljantframework.Bj;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.dataframe.Index;
import org.briljantframework.dataframe.IntIndex;
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
    this.type = VectorType.from(cls);
    this.size = size;
  }

  public GenericVector(Class<?> cls, List<Object> values, int size, Index index) {
    super(index);
    this.cls = cls;
    this.type = VectorType.from(cls);
    this.values = values;
    this.size = size;
  }

  @Override
  public <T> T get(Class<T> cls, int index) {
    return Convert.to(cls, values.get(index));
  }

  @Override
  public String toString(int index) {
    Object o = values.get(index);
    return Is.NA(o) ? "NA" : o.toString();
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
    return Is.NA(values.get(index));
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
  public DoubleArray toDoubleArray() throws IllegalTypeException {
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
  public int compare(int a, Vector other, int b) {
    return getType().compare(a, this, b, other);
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

  public static class Builder extends AbstractBuilder {

    private static final Set<Class<?>> INVALID_CLASSES = new HashSet<>();

    static {
      INVALID_CLASSES.addAll(Arrays.asList(
          Integer.class, Integer.TYPE, Double.TYPE, Double.class
      ));
    }

    private final Class<?> cls;
    private List<Object> buffer;
    private Resolver<?> resolver = null;

    public <T> Builder(Class<T> cls, Resolver<T> resolver) {
      super(new IntIndex.Builder(0));
      this.cls = ensureValidClass(cls);
      this.resolver = resolver;
      this.buffer = new ArrayList<>();
    }

    public Builder(Class<?> cls) {
      super(new IntIndex.Builder(0));
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
      super(new IntIndex.Builder(size));
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
      indexer.set(index, index);
      return this;
    }

    @Override
    public Vector.Builder set(int atIndex, Vector from, Object fromKey) {
      setAt(atIndex, from.get(cls, fromKey));
      return this;
    }

    @Override
    void setAt(int index, Object value) {
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
    }

    @Override
    void setAt(int atIndex, Vector from, int fromIndex) {
      ensureCapacity(atIndex);
      buffer.set(atIndex, from.get(cls, fromIndex));
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
      this.indexer.remove(index);
      return this;
    }

    @Override
    public int compare(int a, int b) {
      Object va = buffer.get(a);
      Object vb = buffer.get(b);
      if (!Is.NA(va) && !Is.NA(vb) && va instanceof Comparable && va.getClass().isInstance(vb)) {
        @SuppressWarnings("unchecked")
        int cmp = ((Comparable) va).compareTo(vb);
        return cmp;
      } else {
        return !Is.NA(vb) && !Is.NA(va) && va.equals(vb) ? 1 : -1;
      }
    }

    @Override
    public void swap(int a, int b) {
      indexer.swap(a, b);
      Collections.swap(buffer, a, b);
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
      Vector vector = new GenericVector(cls, buffer, buffer.size(), indexer.build());
      buffer = null;
      return vector;
    }

    private void ensureCapacity(int index) {
      int i = buffer.size();
      while (i <= index) {
        if (i < index) {
          this.indexer.set(i, i);
        }
        buffer.add(null);
        i++;
      }
    }
  }
}
