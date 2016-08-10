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
import org.briljantframework.array.ShapeUtils;
import org.briljantframework.array.StrideUtils;
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

  private final int[] buffer;
  private final int elementCount;

  private IntSeries(int... values) {
    this(values, values.length, false);
  }

  private IntSeries(int[] buffer, int elementCount, boolean safe) {
    super(0, new int[] {elementCount}, new int[] {1});
    if (safe) {
      this.buffer = java.util.Arrays.copyOf(buffer, elementCount);
    } else {
      this.buffer = buffer;
    }
    this.elementCount = elementCount;
  }

  private IntSeries(Index index, int[] buffer, int elementCount) {
    this(index, 0, new int[] {elementCount}, new int[] {1}, buffer);

  }

  private IntSeries(int offset, int[] shape, int[] stride, int[] buffer) {
    this(null, offset, shape, stride, buffer);

  }

  private IntSeries(Index index, int offset, int[] shape, int[] stride, int[] buffer) {
    super(index, offset, shape, stride);
    this.buffer = buffer;
    this.elementCount = ShapeUtils.size(shape);
  }

  private IntSeries(int offset, int[] shape) {
    this(offset, shape, StrideUtils.computeStride(shape), new int[ShapeUtils.size(shape)]);
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
  public void set(int toIndex, Series from, int fromIndex) {
    loc().setInt(toIndex, from.loc().getInt(fromIndex));
  }

  @Override
  public void set(int toRow, int toColumn, Series from, int fromRow, int fromColumn) {
    loc().setInt(toRow, fromColumn, from.loc().getInt(fromRow, fromColumn));
  }

  @Override
  public void set(int[] toIndex, Series from, int[] fromIndex) {
    loc().setInt(toIndex, from.loc().getInt(fromIndex));
  }

  @Override
  public void set(int[] toIndex, Series from, int fromIndex) {
    loc().setInt(toIndex, from.loc().getInt(fromIndex));
  }

  @Override
  public void set(int toIndex, Series from, int[] fromIndex) {
    loc().setInt(toIndex, from.loc().getInt(fromIndex));
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
  public Series asView(int offset, int[] shape, int[] stride) {
    return new IntSeries(getIndex(), offset, shape, stride, buffer);
  }

  @Override
  protected final int compareElement(int a, Series other, int b) {
    int x = loc().getInt(a);
    int y = other.loc().getInt(b);
    boolean aIsNa = Is.NA(x);
    boolean bIsNa = Is.NA(y);
    if (aIsNa && !bIsNa) {
      return -1;
    } else if (!aIsNa && bIsNa) {
      return 1;
    } else {
      return Integer.compare(x, y);
    }
  }

  @Override
  protected final boolean isElementNA(int i) {
    return getIntElement(i) == Na.INT;
  }

  @Override
  protected void setElement(int index, Object value) {
    setIntElement(index, Convert.to(Integer.class, value));
  }

  @Override
  protected void setDoubleElement(int index, double value) {
    setIntElement(index, (int) value);
  }

  @Override
  protected void setIntElement(int index, int value) {
    buffer[index] = value;
  }

  @Override
  protected final int getIntElement(int i) {
    return buffer[i];
  }

  @Override
  protected final double getDoubleElement(int i) {
    int value = getIntElement(i);
    return value == Na.INT ? Na.DOUBLE : value;
  }

  @Override
  protected final <T> T getElement(Class<T> cls, int index) {
    Check.argument(!cls.isPrimitive(), "can't get primitive values");
    int v = getIntElement(index);
    return Convert.to(cls, v);
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
      result = 31 * result + getIntElement(i);
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
    if (!getIndex().equals(that.getIndex())) {
      return false;
    }
    for (Object key : getIndex().keySet()) {
      int a = getInt(key);
      int b = that.getInt(key);
      if (!Is.NA(a) && !Is.NA(b) && a != b) {
        return false;
      }

    }
    return true;
  }

  @Override
  protected final String getStringElement(int index) {
    int value = getIntElement(index);
    return value == Na.INT ? "NA" : String.valueOf(value);
  }

  @Override
  protected boolean equalsElement(int a, Series other, int b) {
    return getIntElement(a) == other.loc().getInt(b);
  }

  @Override
  public final Type getType() {
    return Types.INT;
  }

  @Override
  protected int elementSize() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Series newEmptyArray(int... shape) {
    return new IntSeries(0, shape);
  }

  public static final class Builder extends AbstractSeriesBuilder {

    private int[] buffer;
    private int size;

    public Builder() {
      this(0, INITIAL_CAPACITY);
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
      Index.Builder builder = vector.getIndex().newCopyBuilder();
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
      buffer[t] = from.loc().getInt(f);
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
        Resolver<Integer> resolver = Resolve.find(Integer.class);
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
