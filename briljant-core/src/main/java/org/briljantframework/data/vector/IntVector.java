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

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.Utils;
import org.briljantframework.array.IntArray;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.Is;
import org.briljantframework.data.Na;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.IntIndex;
import org.briljantframework.data.resolver.Resolver;
import org.briljantframework.data.resolver.Resolvers;
import org.briljantframework.exceptions.IllegalTypeException;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * @author Isak Karlsson
 */
class IntVector extends AbstractVector implements Transferable {

  private final int[] buffer;
  private final int size;

  public IntVector(int... values) {
    this(values, values.length);
  }

  public IntVector(int[] values, int size) {
    this(values, size, true);
  }

  private IntVector(int[] values, int size, boolean safe) {
    if (safe) {
      this.buffer = Arrays.copyOf(values, size);
    } else {
      this.buffer = values;
    }
    this.size = size;
  }

  private IntVector(int[] buffer, int size, Index index) {
    super(index);
    this.buffer = buffer;
    this.size = size;
  }

  public static IntVector range(int end) {
    int[] v = new int[end];
    for (int i = 0; i < v.length; i++) {
      v[i] = i;
    }
    return new IntVector(v, v.length, false);
  }

  @Override
  protected final int getAsIntAt(int i) {
    Check.elementIndex(i, size);
    return buffer[i];
  }

  @Override
  protected final <T> T getAt(Class<T> cls, int index) {
    Check.argument(!cls.isPrimitive(), "can't get primitive values");
    int v = getAsIntAt(index);
    return Convert.to(cls, v);
  }

  @Override
  protected final String toStringAt(int index) {
    int value = getAsIntAt(index);
    return value == Na.INT ? "NA" : String.valueOf(value);
  }

  @Override
  protected final boolean isNaAt(int index) {
    return getAsIntAt(index) == Na.INT;
  }

  @Override
  protected final double getAsDoubleAt(int i) {
    int value = getAsIntAt(i);
    return value == Na.INT ? Na.DOUBLE : value;
  }

  @Override
  protected final int compareAt(int a, Vector other, int b) {
    return getType().compare(a, this, b, other);
  }

  @Override
  public IntArray toIntArray() throws IllegalTypeException {
    return Bj.array(Arrays.copyOf(buffer, size()));
  }

  @Override
  public final VectorType getType() {
    return VectorType.INT;
  }

  @Override
  public final int hashCode() {
    int result = 1;
    for (int i = 0; i < size(); i++) {
      result = 31 * result + getAsIntAt(i);
    }
    return result;
  }

  @Override
  public final boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    if (object == null || !(object instanceof Vector)) {
      return false;
    }

    Vector that = (Vector) object;
    if (size() != that.size()) {
      return false;
    }
    if (!getIndex().equals(that.getIndex())) {
      return false;
    }
    for (Object key : getIndex().keySet()) {
      int a = getAsInt(key);
      int b = getAsInt(key);
      if (!Is.NA(a) && !Is.NA(b) && a != b) {
        return false;
      }

    }
    return true;
  }

  @Override
  public final int size() {
    return size;
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
  public Builder newCopyBuilder() {
    return new Builder(this);
  }

  @Override
  public final IntStream intStream() {
    return Arrays.stream(buffer, 0, size());
  }

  static final class Builder extends AbstractBuilder {

    private int[] buffer;
    private int size;

    public Builder() {
      this(0, INITIAL_CAPACITY);
    }

    public Builder(int size) {
      this(size, size);
    }

    public Builder(int size, int capacity) {
      this.size = size;
      buffer = new int[Math.max(size, capacity)];
      for (int i = 0; i < size; i++) {
        buffer[i] = Na.INT;
      }
    }

    private Builder(IntVector vector) {
      super(getIndexer(vector));
      this.size = vector.size;
      this.buffer = Arrays.copyOf(vector.buffer, vector.size);
    }

    private static Index.Builder getIndexer(IntVector vector) {
      Index.Builder builder = vector.getIndex().newCopyBuilder();
      if (builder instanceof IntIndex.Builder) {
        return null;
      }
      return builder;
    }

    @Override
    public Vector.Builder addNA() {
      final int index = size;
      ensureCapacity(size + 1); // sets the size
      buffer[index] = Na.INT;
      extendIndex(index);
      return this;
    }

    @Override
    public Vector.Builder add(int value) {
      final int index = size;
      ensureCapacity(size + 1); // sets the size
      buffer[index] = value;
      extendIndex(index);
      return this;
    }

    @Override
    public Vector.Builder add(double value) {
      final int index = size;
      ensureCapacity(size + 1); // sets the size
      buffer[index] = (int) value;
      extendIndex(index);
      return this;
    }

    @Override
    public Vector.Builder add(Object value) {
      final int index = size;
      ensureCapacity(size + 1); // sets the size
      buffer[index] = convert(value);
      extendIndex(index);
      return this;
    }

    @Override
    public Vector.Builder add(Vector from, int fromIndex) {
      return add(from.loc().getAsInt(fromIndex));
    }

    @Override
    public Vector.Builder add(Vector from, Object key) {
      return add(from.getAsInt(key));
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
    public void setNaAt(int index) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
    }

    @Override
    protected void setAt(int atIndex, Vector from, Object f) {
      setAt(atIndex, from.getAsInt(f));
    }

    @Override
    protected void setAt(int index, Object value) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      int dval = convert(value);
      fillNa(oldSize, size, buffer);
      buffer[index] = dval;
    }

    private int convert(Object value) {
      int dval = Na.INT;
      if (value instanceof Number && !Is.NA(value)) {
        dval = ((Number) value).intValue();
      } else if (value != null && !Is.NA(value)) {
        Resolver<Integer> resolver = Resolvers.find(Integer.class);
        if (resolver != null) {
          Integer resolve = resolver.resolve(value);
          if (resolve != null) {
            dval = resolve;
          }
        }
      }
      return dval;
    }

    @Override
    protected void setAt(int t, Vector from, int f) {
      final int oldSize = size;
      ensureCapacity(t + 1);
      fillNa(oldSize, size, buffer);
      buffer[t] = from.loc().getAsInt(f);
    }

    protected void setAt(int index, int value) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
      buffer[index] = value;
    }

    @Override
    protected void setAt(int index, double value) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
      buffer[index] = Is.NA(value) ? Na.INT : (int) value;
    }

    @Override
    protected void removeAt(int index) {
      rangeCheck(index);
      int numMoved = size - index - 1;
      if (numMoved > 0) {
        System.arraycopy(buffer, index + 1, buffer, index, numMoved);
      }
    }

    @Override
    public void swapAt(int a, int b) {
      rangeCheck(a);
      rangeCheck(b);
      Check.argument(a >= 0 && a < size() && b >= 0 && b < size());
      Utils.swap(buffer, a, b);
    }

    @Override
    protected void readAt(int index, DataEntry entry) throws IOException {
      setAt(index, entry.nextInt());
    }

    @Override
    public int size() {
      return size;
    }

    @Override
    public Vector getTemporaryVector() {
      return new IntVector(buffer, size(), false) {
        @Override
        public Builder newCopyBuilder() {
          return Builder.this;
        }
      };
    }

    @Override
    public IntVector build() {
      IntVector vector = new IntVector(buffer, size(), getIndex());
      buffer = null;
      return vector;
    }

    private void ensureCapacity(final int newSize) {
      if (newSize - buffer.length > 0) {
        grow(newSize);
      }
      if (newSize > size) {
        size = newSize;
      }
    }

    private void rangeCheck(int index) {
      if (index >= size) {
        throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
      }
    }

    private String outOfBoundsMsg(int index) {
      return "Index: " + index + ", Size: " + size;
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
      buffer = Arrays.copyOf(buffer, newCapacity);
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
  }

}
