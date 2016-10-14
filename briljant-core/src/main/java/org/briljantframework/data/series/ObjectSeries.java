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

import java.util.*;

import org.briljantframework.data.index.Index;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.resolver.Resolve;
import org.briljantframework.data.resolver.Resolver;

/**
 * A series of reference values.
 * 
 * @author Isak Karlsson
 */
public class ObjectSeries extends AbstractSeries {

  private final Index index;
  private final Type type;
  private final List<Object> buffer;

  private ObjectSeries(Index index, Class<?> cls, List<Object> buffer) {
    this(index, Types.from(cls), buffer);
  }

  private ObjectSeries(Index index, Type type, List<Object> buffer) {
    this.index = index;
    this.type = type;
    this.buffer = buffer;
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public int size() {
    return buffer.size();
  }

  @Override
  public Index index() {
    return index;
  }

  @Override
  public Object get(Object key) {
    return buffer.get(index().getLocation(key));
  }

  @Override
  public void set(Object key, Object value) {
    buffer.set(index().getLocation(key), value);
  }

  @Override
  public Series reindex(Index index) {
    return new ObjectSeries(index, type, buffer);
  }

  @Override
  public Series.Builder newCopyBuilder() {
    return new Builder(type.getDataClass()).setAll(this);
  }

  @Override
  public Storage values() {
    Storage st;
    return (st = storage) == null ? storage = new Stor() : st;
  }

  private class Stor extends AbstractStorage{
    @Override
    public Object get(int index) {
      return buffer.get(index);
    }

    @Override
    public Object set(int index, Object element) {
      Object oldValue = get(index);
      buffer.set(index, element);
      return oldValue;
    }

    @Override
    public int size() {
      return buffer.size();
    }
  }

  static final class Builder extends AbstractSeriesBuilder {

    private static final Set<Class<?>> INVALID_CLASSES = new HashSet<>();

    static {
      INVALID_CLASSES.addAll(Arrays.asList(Integer.class, Integer.TYPE, Double.TYPE, Double.class));
    }

    private final Class<?> cls;
    private List<Object> buffer;
    private Resolver<?> resolver = null;

    public <T> Builder(Class<T> cls, Resolver<? extends T> resolver) {
      this.cls = ensureValidClass(cls);
      this.resolver = resolver;
      this.buffer = new ArrayList<>();
    }

    public <T> Builder(Class<T> cls, int size) {
      this(cls, Resolve.find(cls));
      buffer = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        buffer.add(null);
      }
    }

    public Builder() {
      this(Object.class);
    }

    public Builder(Class<?> cls) {
      this(cls, 0);
    }

    private <T> Class<?> ensureValidClass(Class<T> cls) {
      if (INVALID_CLASSES.contains(cls)) {
        throw new IllegalArgumentException(
            String.format("ObjectSeries should not be used for: %s", cls));
      }
      return cls;
    }

    @Override
    protected void readAt(int index, DataEntry entry) {
      ensureCapacity(index);
      buffer.set(index, entry.next(cls));
    }

    @Override
    protected void setElement(int atIndex, Series from, Object f) {
      setElement(atIndex, from.get(cls, f));
    }

    @Override
    protected void setElementFrom(int t, Series from, int f) {
      ensureCapacity(t);
      buffer.set(t, from.values().get(cls, f));
    }

    @Override
    protected void setElement(int index, Object value) {
      ensureCapacity(index);
      if (value != null && cls.isInstance(value)) {
        buffer.set(index, value);
      } else if (value != null) {
        // Resolver<?> resolver = this.resolver == null ? Resolve.find(cls) : this.resolver;
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
    protected void setElementNA(int index) {
      ensureCapacity(index);
      buffer.set(index, null);
    }

    @Override
    protected void removeElement(int index) {
      buffer.remove(index);
    }

    @Override
    public void swapAt(int a, int b) {
      Collections.swap(buffer, a, b);
    }

    private void ensureCapacity(int index) {
      int i = buffer.size();
      while (i <= index) {
        buffer.add(null);
        i++;
      }
    }

    @Override
    public int size() {
      return buffer.size();
    }

    @Override
    public Series build() {
      Series series = new ObjectSeries(getIndex(), cls, buffer);
      buffer = null;
      return series;
    }
  }
}
