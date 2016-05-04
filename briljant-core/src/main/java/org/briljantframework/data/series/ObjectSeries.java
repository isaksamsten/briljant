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

import org.briljantframework.array.ShapeUtils;
import org.briljantframework.array.StrideUtils;
import org.briljantframework.data.Is;
import org.briljantframework.data.Na;
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

  private final Type type;
  private final List<Object> elements;

  public ObjectSeries(Type type) {
    this(type, new int[] {0});
  }

  public ObjectSeries(Type type, int... shape) {
    super(0, shape, StrideUtils.computeStride(shape));
    int size = ShapeUtils.size(shape);
    this.elements = new ArrayList<>(size);
    for (int i = 0; i < size; i++) {
      elements.add(null);
    }
    this.type = type;
  }

  private ObjectSeries(Index index, Class<?> cls, List<Object> elements) {
    this(index, 0, new int[] {elements.size()}, new int[] {1}, Type.of(cls), elements);
  }

  private ObjectSeries(Index index, int offset, int[] shape, int[] stride, Type type,
      List<Object> elements) {
    super(index, offset, shape, stride);
    this.type = type;
    this.elements = elements;
  }

  private ObjectSeries(int offset, int[] shape, int[] stride, Type type, List<Object> elements) {
    this(null, offset, shape, stride, type, elements);
  }

  @Override
  public Type getType() {
    return type;
  }

  @Override
  public Series asView(int offset, int[] shape, int[] stride) {
    return new ObjectSeries(getIndex(), offset, shape, stride, type, elements);
  }

  @Override
  protected int elementSize() {
    return elements.size();
  }

  @Override
  public Series newEmptyArray(int... shape) {
    return new ObjectSeries(type, shape);
  }

  @Override
  public void set(int toIndex, Series from, int fromIndex) {
    loc().set(toIndex, from.loc().get(type.getDataClass(), fromIndex));
  }

  @Override
  public void set(int toRow, int toColumn, Series from, int fromRow, int fromColumn) {
    loc().set(toRow, fromColumn, from.loc().get(type.getDataClass(), fromRow, fromColumn));
  }

  @Override
  public void set(int[] toIndex, Series from, int[] fromIndex) {
    loc().set(toIndex, from.loc().get(type.getDataClass(), fromIndex));
  }

  @Override
  public void set(int[] toIndex, Series from, int fromIndex) {
    loc().set(toIndex, from.loc().get(type.getDataClass(), fromIndex));
  }

  @Override
  public void set(int toIndex, Series from, int[] fromIndex) {
    loc().set(toIndex, from.loc().get(type.getDataClass(), fromIndex));
  }

  @Override
  protected void setElement(int index, Object value) {
    elements.set(index, Convert.to(type.getDataClass(), value));
  }

  @Override
  public <T> T getElement(Class<T> cls, int index) {
    return Convert.to(cls, elements.get(index));
  }

  @Override
  public String getStringElement(int index) {
    Object o = elements.get(index);
    return Is.NA(o) ? "NA" : o.toString();
  }

  @Override
  public double getDoubleElement(int i) {
    Number number = loc().get(Number.class, i);
    return Is.NA(number) ? Na.of(Double.class) : number.doubleValue();
  }


  @Override
  public int getIntElement(int i) {
    Number number = loc().get(Number.class, i);
    return Is.NA(number) ? Na.of(Integer.class) : number.intValue();
  }

  @Override
  public boolean isElementNA(int i) {
    return Is.NA(elements.get(i));
  }

  @Override
  public Series reindex(Index index) {
    return new ObjectSeries(index, offset, getShape(), getStride(), type, elements);
  }

  @Override
  public int hashCode() {
    return type.hashCode() + elements.hashCode();
  }

  static final class Builder extends AbstractSeriesBuilder {

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

    private <T> Class<?> ensureValidClass(Class<T> cls) {
      if (INVALID_CLASSES.contains(cls)) {
        throw new IllegalArgumentException(
            String.format("ObjectSeries should not be used for: %s", cls));
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

    public Builder() {
      this(Object.class);
    }

    public Builder(Class<?> cls) {
      this.cls = ensureValidClass(cls);
      buffer = new ArrayList<>();
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
    protected void setElement(int t, Series from, int f) {
      ensureCapacity(t);
      buffer.set(t, from.loc().get(cls, f));
    }

    @Override
    protected void setElement(int index, Object value) {
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
    protected void setNaAt(int index) {
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
