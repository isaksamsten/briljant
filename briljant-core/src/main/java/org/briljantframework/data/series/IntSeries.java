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

import java.util.stream.IntStream;

import org.briljantframework.Check;
import org.briljantframework.data.Is;
import org.briljantframework.data.Na;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.RangeIndex;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.resolver.Resolve;
import org.briljantframework.data.resolver.Resolver;
import org.briljantframework.util.primitive.ArrayAllocations;

/**
 * A series of primitive int values.
 * 
 * @author Isak Karlsson
 */
public class IntSeries extends AbstractSeries {

  private final Index index;
  private int[] buffer;
  private int elementCount;

  private IntSeries(int... values) {
    this(values, values.length, false);
  }

  private IntSeries(int[] buffer, int elementCount, boolean safe) {
    if (safe) {
      this.buffer = java.util.Arrays.copyOf(buffer, elementCount);
    } else {
      this.buffer = buffer;
    }
    this.elementCount = elementCount;
    this.index = new RangeIndex(0, elementCount);
  }

  private IntSeries(Index index, int[] buffer, int elementCount) {
    this.index = index;
    this.buffer = buffer;
    this.elementCount = elementCount;
  }

  public static IntSeries of(int... values) {
    return new IntSeries(java.util.Arrays.copyOf(values, values.length));
  }

  public static IntSeries range(int end) {
    int[] v = new int[end];
    for (int i = 0; i < v.length; i++) {
      v[i] = i;
    }
    return new IntSeries(v, v.length, false);
  }

  @Override
  public int size() {
    return elementCount;
  }

  @Override
  public Index index() {
    return index;
  }

  @Override
  public Object get(Object key) {
    return buffer[index().getLocation(key)];
  }

  @Override
  public final IntStream intStream() {
    return java.util.Arrays.stream(buffer, 0, size());
  }

  @Override
  public final Builder newBuilder() {
    return new Builder();
  }

  @Override
  public final Builder newBuilder(int size) {
    return new Builder(size, size);
  }

  @Override
  public void set(Object index, Object value) {
    buffer[index().getLocation(index)] = Convert.to(Integer.class, value);
  }

  // @Override
  // protected void addElement(Object value) {
  // buffer = ArrayAllocations.ensureCapacity(buffer, elementCount + 1);
  // buffer[elementCount++] = Convert.to(Integer.class, value);
  // }

  @Override
  public void setInt(Object index, int value) {
    buffer[index().getLocation(index)] = value;
  }

  @Override
  public int getInt(Object key) {
    return buffer[index().getLocation(key)];
  }

  @Override
  public double getDouble(Object key) {
    return values().getDouble(index().getLocation(key));
  }

  @Override
  public void setDouble(Object key, double value) {
    values().setDouble(index().getLocation(key), value);
  }

  @Override
  public Series reindex(Index index) {
    return new IntSeries(index, buffer, elementCount);
  }

  @Override
  public Series.Builder newCopyBuilder() {
    return new Builder(this);
  }

  @Override
  public final int hashCode() {
    int result = 1;
    for (int i = 0; i < size(); i++) {
      result = 31 * result + values().getInt(i);
    }
    return result;
  }

  @Override
  public final boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || !(object instanceof Series)) {
      return false;
    }

    Series that = (Series) object;
    if (size() != that.size()) {
      return false;
    }
    if (!index().equals(that.index())) {
      return false;
    }
    for (Object key : index().keySet()) {
      int a = getInt(key);
      int b = that.getInt(key);
      if (!Is.NA(a) && !Is.NA(b) && a != b) {
        return false;
      }

    }
    return true;
  }

  @Override
  public final Type getType() {
    return Types.INT;
  }

  @Override
  public Storage values() {
    Storage st;
    return (st = storage) == null ? storage = new IntStr() : st;
  }

  private final class IntStr extends AbstractStorage {

    @Override
    public void setFrom(int to, Storage source, int from) {
      setInt(to, source.getInt(from));
    }

    @Override
    public Object set(int index, Object element) {
      return setInt(index, Convert.to(Integer.class, element));
    }

    @Override
    public Object get(int index) {
      return buffer[index];
    }

    @Override
    public int getInt(int i) {
      return buffer[i];
    }

    @Override
    public double getDouble(int i) {
      int retVal = buffer[i];
      return Is.NA(retVal) ? Na.DOUBLE : retVal;
    }

    @Override
    public int setInt(int index, int value) {
      int oldValue = getInt(index);
      buffer[index] = value;
      return oldValue;
    }

    @Override
    public double setDouble(int index, double value) {
      double oldValue = getDouble(index);
      buffer[index] = Is.NA(value) ? Na.INT : (int) value;
      return oldValue;
    }

    @Override
    public int size() {
      return elementCount;
    }
  }

  public static final class Builder extends AbstractSeriesBuilder {

    private int[] buffer;
    private int size;

    public Builder() {
      this(0, TypeInferenceBuilder.INITIAL_CAPACITY);
    }

    public Builder(int size, int capacity) {
      this.size = size;
      buffer = new int[Math.max(size, capacity)];
      for (int i = 0; i < size; i++) {
        buffer[i] = Na.INT;
      }
    }

    public Builder(int size) {
      this(size, size);
    }

    private Builder(IntSeries vector) {
      super(getIndexer(vector));
      this.size = vector.elementCount;
      this.buffer = java.util.Arrays.copyOf(vector.buffer, vector.elementCount);
    }

    private static Index.Builder getIndexer(IntSeries vector) {
      Index.Builder builder = vector.index().newCopyBuilder();
      if (builder instanceof RangeIndex.Builder) {
        return null;
      }
      return builder;
    }

    @Override
    public Series.Builder addNA() {
      final int index = size;
      ensureCapacity(size + 1); // sets the size
      buffer[index] = Na.INT;
      extendIndex(index);
      return this;
    }

    @Override
    public Series.Builder addFrom(Series from, Object key) {
      return addInt(from.getInt(key));
    }

    @Override
    public Series.Builder add(Object value) {
      final int index = size;
      ensureCapacity(size + 1); // sets the size
      buffer[index] = convert(value);
      extendIndex(index);
      return this;
    }

    @Override
    public Series.Builder addDouble(double value) {
      final int index = size;
      ensureCapacity(size + 1); // sets the size
      buffer[index] = (int) value;
      extendIndex(index);
      return this;
    }

    @Override
    public Series.Builder addInt(int value) {
      final int index = size;
      ensureCapacity(size + 1); // sets the size
      buffer[index] = value;
      extendIndex(index);
      return this;
    }

    @Override
    protected void readAt(int index, DataEntry entry) {
      setElement(index, entry.nextInt());
    }

    @Override
    protected void setElement(int atIndex, Series from, Object f) {
      setElement(atIndex, from.getInt(f));
    }

    @Override
    protected void setElementFrom(int t, Series from, int f) {
      final int oldSize = size;
      ensureCapacity(t + 1);
      fillNa(oldSize, size, buffer);
      buffer[t] = from.values().getInt(f);
    }

    @Override
    protected void setElement(int index, Object value) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      int dval = convert(value);
      fillNa(oldSize, size, buffer);
      buffer[index] = dval;
    }

    @Override
    public void setElementNA(int index) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
    }

    protected void setElement(int index, int value) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
      buffer[index] = value;
    }

    /**
     * Fill with NA from {@code index} until {@code size}
     */
    private static void fillNa(final int from, final int until, int[] buffer) {
      for (int i = from; i < until; i++) {
        buffer[i] = Na.INT;
      }
    }

    @Override
    protected void setElement(int index, double value) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
      buffer[index] = Is.NA(value) ? Na.INT : (int) value;
    }

    @Override
    protected void removeElement(int index) {
      rangeCheck(index);
      int numMoved = size - index - 1;
      if (numMoved > 0) {
        System.arraycopy(buffer, index + 1, buffer, index, numMoved);
        size--;
      }
    }

    @Override
    public void swapAt(int a, int b) {
      rangeCheck(a);
      rangeCheck(b);
      Check.argument(a >= 0 && a < size() && b >= 0 && b < size());
      ArrayAllocations.swap(buffer, a, b);
    }

    private void rangeCheck(int index) {
      if (index >= size) {
        throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
      }
    }

    private String outOfBoundsMsg(int index) {
      return "Index: " + index + ", Size: " + size;
    }

    private int convert(Object value) {
      int dval = Na.INT;
      if (value instanceof Number && !Is.NA(value)) {
        dval = ((Number) value).intValue();
      } else if (value != null && !Is.NA(value)) {
        Resolver<Integer> resolver = Resolve.getResolver(Integer.class);
        if (resolver != null) {
          Integer resolve = resolver.resolve(value);
          if (resolve != null) {
            dval = resolve;
          }
        }
      }
      return dval;
    }

    private void ensureCapacity(final int newSize) {
      if (newSize - buffer.length > 0) {
        grow(newSize);
      }
      if (newSize > size) {
        size = newSize;
      }
    }

    /**
     * From {@link java.util.ArrayList}
     */
    private void grow(int minCapacity) {
      // overflow-conscious code
      int oldCapacity = buffer.length;
      int newCapacity = oldCapacity + (oldCapacity >> 1);
      if (newCapacity - minCapacity < 0) {
        newCapacity = minCapacity;
      }
      if (newCapacity - MAX_ARRAY_SIZE > 0) {
        newCapacity = hugeCapacity(minCapacity);
      }
      // minCapacity is usually close to size, so this is a win:
      buffer = java.util.Arrays.copyOf(buffer, newCapacity);
    }

    /**
     * From {@link java.util.ArrayList}
     */
    private static int hugeCapacity(int minCapacity) {
      if (minCapacity < 0) { // overflow
        throw new OutOfMemoryError();
      }
      return (minCapacity > MAX_ARRAY_SIZE) ? Integer.MAX_VALUE : MAX_ARRAY_SIZE;
    }

    @Override
    public int size() {
      return size;
    }

    @Override
    public IntSeries build() {
      IntSeries vector = new IntSeries(getIndex(), buffer, size());
      buffer = null;
      return vector;
    }
  }



}
