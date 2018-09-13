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
    return new Builder(type).setAll(this);
  }

  @Override
  public Storage values() {
    Storage st;
    return (st = storage) == null ? storage = new Stor() : st;
  }

  private class Stor extends AbstractStorage {
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

    private static final Set<Type> ILLEGAL_CLASS = new HashSet<>();

    static {
      ILLEGAL_CLASS.addAll(Arrays.asList(Types.INT, Types.DOUBLE));
    }

    private final Type type;
    private List<Object> buffer;

    public Builder(Type type, int size) {
      this.type = ensureValidClass(type);
      this.buffer = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        buffer.add(null);
      }
    }

    public Builder() {
      this(Types.OBJECT);
    }

    public Builder(Type cls) {
      this(cls, 0);
    }

    private Type ensureValidClass(Type type) {
      if (ILLEGAL_CLASS.contains(type)) {
        throw new IllegalArgumentException(
            String.format("ObjectSeries should not be used for: %s", type));
      }
      return type;
    }

    @Override
    protected void readAt(int index, DataEntry entry) {
      ensureCapacity(index);
      buffer.set(index, entry.next(type.getDataClass()));
    }

    @Override
    protected void setElement(int atIndex, Series from, Object f) {
      setElement(atIndex, from.get(type.getDataClass(), f));
    }

    @Override
    protected void setElementFrom(int t, Series from, int f) {
      ensureCapacity(t);
      buffer.set(t, from.values().get(type.getDataClass(), f));
    }

    @Override
    protected void setElement(int index, Object value) {
      ensureCapacity(index);
      if (value != null && type.isAssignableTo(value.getClass())) {
        buffer.set(index, value);
      } else if (value != null) {
        Resolver<?> resolver = Resolve.getResolver(type);
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
      Series series = new ObjectSeries(getIndex(), type, buffer);
      buffer = null;
      return series;
    }
  }
}
