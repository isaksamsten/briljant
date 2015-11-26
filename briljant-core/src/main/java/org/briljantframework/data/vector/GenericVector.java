/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.briljantframework.data.Is;
import org.briljantframework.data.Na;
import org.briljantframework.data.Transferable;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.resolver.Resolve;
import org.briljantframework.data.resolver.Resolver;

/**
 * @author Isak Karlsson
 */
public class GenericVector extends AbstractVector implements Transferable {

  private final VectorType type;
  private final Class<?> cls;
  private final List<Object> values;
  private final int size;

  @SuppressWarnings("unchecked")
  protected  <T> GenericVector(Class<T> cls, List<? extends T> values) {
    this(cls, (List<Object>) values, true);
  }

  protected GenericVector(Class<?> cls, List<Object> values, boolean copy) {
    this(cls, values, values.size(), copy);
  }

  private GenericVector(Class<?> cls, List<Object> values, int size, boolean copy) {
    this.cls = cls;
    this.values = copy ? new ArrayList<>(values) : values;
    this.type = VectorType.of(cls);
    this.size = size;
  }

  protected GenericVector(Class<?> cls, List<Object> values, int size, Index index) {
    super(index);
    this.cls = cls;
    this.type = VectorType.of(cls);
    this.values = values;
    this.size = size;
  }

  @Override
  public <T> T getAt(Class<T> cls, int index) {
    return Convert.to(cls, values.get(index));
  }

  @Override
  public String toStringAt(int index) {
    Object o = values.get(index);
    return Is.NA(o) ? "NA" : o.toString();
  }

  @Override
  public double getAsDoubleAt(int i) {
    Number number = loc().get(Number.class, i);
    return Is.NA(number) ? Na.of(Double.class) : number.doubleValue();
  }

  @Override
  public int getAsIntAt(int i) {
    Number number = loc().get(Number.class, i);
    return Is.NA(number) ? Na.of(Integer.class) : number.intValue();
  }

  @Override
  public boolean isNaAt(int index) {
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
  protected Vector shallowCopy(Index index) {
    return new GenericVector(cls, values, size, index);
  }

//  @Override
//  public boolean equals(Object obj) {
//    if (obj instanceof GenericVector) {
//      if (!this.cls.equals(((GenericVector) obj).cls)) {
//        return false;
//      } else {
//        return values.equals(((GenericVector) obj).values);
//      }
//    }
//    return super.equals(obj);
//  }

  @Override
  public int hashCode() {
    return cls.hashCode() + values.hashCode();
  }

  static final class Builder extends AbstractBuilder {

    private static final Set<Class<?>> INVALID_CLASSES = new HashSet<>();

    static {
      INVALID_CLASSES.addAll(Arrays.asList(Integer.class, Integer.TYPE, Double.TYPE, Double.class));
    }

    private final Class<?> cls;
    private List<Object> buffer;
    private Resolver<?> resolver = null;

    public <T> Builder(Class<T> cls, Resolver<T> resolver) {
      this.cls = ensureValidClass(cls);
      this.resolver = resolver;
      this.buffer = new ArrayList<>();
    }

    public Builder(Class<?> cls) {
      this.cls = ensureValidClass(cls);
      buffer = new ArrayList<>();
    }

    public Builder(Class<?> cls, int size) {
      this.cls = cls;
      buffer = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        buffer.add(null);
      }
    }

    public Builder() {
      this(Object.class);
    }

    private <T> Class<?> ensureValidClass(Class<T> cls) {
      if (INVALID_CLASSES.contains(cls)) {
        throw new IllegalArgumentException(String.format(
            "GenericVector should not be used for: %s", cls));
      }
      return cls;
    }

    @Override
    protected void setNaAt(int index) {
      ensureCapacity(index);
      buffer.set(index, null);
    }

    @Override
    protected void setAt(int atIndex, Vector from, Object f) {
      setAt(atIndex, from.get(cls, f));
    }

    @Override
    protected void setAt(int index, Object value) {
      ensureCapacity(index);
      if (value != null && cls.isInstance(value)) {
        buffer.set(index, value);
      } else if (value != null) {
        Resolver<?> resolver = this.resolver == null ? Resolve.find(cls) : this.resolver;
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
    protected void setAt(int t, Vector from, int f) {
      ensureCapacity(t);
      buffer.set(t, from.loc().get(cls, f));
    }

    @Override
    protected void removeAt(int index) {
      buffer.remove(index);
    }

    @Override
    public void swapAt(int a, int b) {
      Collections.swap(buffer, a, b);
    }

    @Override
    protected void readAt(int index, DataEntry entry) {
      ensureCapacity(index);
      buffer.set(index, entry.next(cls));
    }

    @Override
    public int size() {
      return buffer.size();
    }

    @Override
    public Vector getTemporaryVector() {
      return new GenericVector(cls, buffer, buffer.size(), false) {
        @Override
        public Vector.Builder newCopyBuilder() {
          return GenericVector.Builder.this;
        }
      };
    }

    @Override
    public Vector build() {
      Vector vector = new GenericVector(cls, buffer, buffer.size(), getIndex());
      buffer = null;
      return vector;
    }

    private void ensureCapacity(int index) {
      int i = buffer.size();
      while (i <= index) {
        buffer.add(null);
        i++;
      }
    }
  }
}
