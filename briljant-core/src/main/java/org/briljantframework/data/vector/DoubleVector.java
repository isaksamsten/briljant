/**
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

import java.util.stream.DoubleStream;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.briljantframework.Check;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.Is;
import org.briljantframework.data.Na;
import org.briljantframework.data.Transferable;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.IntIndex;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.resolver.Resolve;
import org.briljantframework.data.resolver.Resolver;
import org.briljantframework.primitive.ArrayAllocations;

/**
 * Vector of {@code double} primitives.
 *
 * @author Isak Karlsson
 */
public class DoubleVector extends AbstractVector implements Transferable {

  private final double[] buffer;
  private final int size;

  private DoubleVector(double[] buffer, int size) {
    this.buffer = buffer;
    this.size = size;
  }

  private DoubleVector(double[] buffer, int size, Index index) {
    super(index);
    this.buffer = buffer;
    this.size = size;
  }

  public static DoubleVector of(double... values) {
    return new DoubleVector(java.util.Arrays.copyOf(values, values.length), values.length);
  }

  @Override
  public DoubleStream doubleStream() {
    return java.util.Arrays.stream(buffer, 0, size());
  }

  @Override
  public Vector.Builder newCopyBuilder() {
    return new DoubleVector.Builder(this);
  }

  @Override
  public Vector.Builder newBuilder() {
    return new DoubleVector.Builder();
  }

  @Override
  public Vector.Builder newBuilder(int size) {
    return new DoubleVector.Builder(size);
  }

  @Override
  public int compareAt(int a, Vector other, int b) {
    double va = getAsDoubleAt(a);
    double vb = other.loc().getAsDouble(b);
    return !Is.NA(va) && !Is.NA(vb) ? Double.compare(va, vb) : 0;
  }

  @Override
  protected boolean isNaAt(int index) {
    return Is.NA(getAsDoubleAt(index));
  }

  @Override
  protected int getAsIntAt(int i) {
    double value = getAsDoubleAt(i);
    return Is.NA(value) ? Na.INT : (int) value;
  }

  @Override
  protected final double getAsDoubleAt(int i) {
    return buffer[i];
  }

  @Override
  protected <T> T getAt(Class<T> cls, int index) {
    Check.argument(!cls.isPrimitive(), "can't get primitive values");
    return Convert.to(cls, getAsDoubleAt(index));
  }

  @Override
  protected Vector shallowCopy(Index index) {
    return new DoubleVector(buffer, size, index);
  }

  @Override
  public final int hashCode() {
    int result = 1;
    for (int i = 0; i < size(); i++) {
      long v = Double.doubleToLongBits(getAsDoubleAt(i));
      result = 31 * result + (int) (v ^ v >>> 32);
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
      double a = getAsDouble(key);
      double b = that.getAsDouble(key);
      if (!Is.NA(a) && !Is.NA(b) && a != b) {
        return false;
      }

    }
    return true;
  }

  @Override
  protected String toStringAt(int index) {
    double value = getAsDoubleAt(index);
    return Is.NA(value) ? "NA" : String.format("%.3f", value);
  }

  @Override
  protected boolean equalsAt(int a, Vector other, int b) {
    double av = getAsDoubleAt(a);
    double ab = other.loc().getAsDouble(b);
    return (Is.NA(av) && Is.NA(ab)) || (Double.isNaN(av) && Double.isNaN(ab)) || av == ab;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public VectorType getType() {
    return VectorType.DOUBLE;
  }

  // Specialized double method

  @Override
  public DoubleArray toDoubleArray() {
    return Arrays.doubleVector(java.util.Arrays.copyOf(buffer, size()));
  }

  @Override
  public double sum() {
    double sum = 0;
    for (int i = 0, size = size(); i < size; i++) {
      double v = getAsDoubleAt(i);
      if (!Is.NA(v)) {
        sum += v;
      }
    }
    return sum;
  }

  @Override
  public double mean() {
    Mean mean = new Mean();
    for (int i = 0, size = size(); i < size; i++) {
      double v = getAsDoubleAt(i);
      if (!Is.NA(v)) {
        mean.increment(v);
      }
    }
    return mean.getN() > 0 ? mean.getResult() : Na.DOUBLE;
  }

  public static final class Builder extends AbstractBuilder {

    private double[] buffer;
    private int size;

    public Builder() {
      this(0);
    }

    public Builder(int size) {
      this(size, Math.max(size, INITIAL_CAPACITY));
    }

    public Builder(int size, int capacity) {
      this.buffer = new double[Math.max(size, capacity)];
      for (int i = 0; i < size; i++) {
        buffer[i] = Na.DOUBLE;
      }
    }

    public Builder(DoubleVector vector) {
      super(getIndexBuilder(vector));
      this.buffer = new double[vector.size()];
      System.arraycopy(vector.buffer, 0, this.buffer, 0, vector.size());
      this.size = buffer.length;
    }

    protected static Index.Builder getIndexBuilder(DoubleVector vector) {
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
      buffer[index] = Na.DOUBLE;
      extendIndex(index);
      return this;
    }

    @Override
    public Vector.Builder add(Vector from, int fromIndex) {
      return add(from.loc().getAsDouble(fromIndex));
    }

    @Override
    public Vector.Builder add(Vector from, Object key) {
      return add(from.getAsInt(key));
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
    public Vector.Builder add(double value) {
      final int index = size;
      ensureCapacity(size + 1); // sets the size
      buffer[index] = value;
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
    protected void readAt(int index, DataEntry entry) {
      setAt(index, entry.nextDouble());
    }

    @Override
    protected void setAt(int atIndex, Vector from, Object f) {
      setAt(atIndex, from.getAsDouble(f));
    }

    @Override
    protected void setAt(int t, Vector from, int f) {
      final int oldSize = size;
      ensureCapacity(t + 1);
      fillNa(oldSize, size, buffer);
      buffer[t] = from.loc().getAsDouble(f);
    }

    @Override
    protected void setAt(int index, Object value) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
      buffer[index] = convert(value);
    }

    @Override
    protected void setNaAt(int index) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
    }

    @Override
    protected void setAt(int index, int value) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
      buffer[index] = Is.NA(value) ? Na.DOUBLE : value;
    }

    @Override
    protected void setAt(int index, double value) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
      buffer[index] = value;
    }

    @Override
    protected void removeAt(int index) {
      rangeCheck(index);
      int numMoved = size - index - 1;
      if (numMoved > 0) {
        System.arraycopy(buffer, index + 1, buffer, index, numMoved);
        size--;
      }
    }

    @Override
    public void swapAt(int a, int b) {
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

    /**
     * Fill with NA from {@code index} until {@code size}
     */
    private static void fillNa(final int from, final int until, double[] buffer) {
      for (int i = from; i < until; i++) {
        buffer[i] = Na.DOUBLE;
      }
    }

    private double convert(Object value) {
      double dval = Na.DOUBLE;
      if (value instanceof Number && !Is.NA(value)) {
        dval = ((Number) value).doubleValue();
      } else if (value != null && !Is.NA(value)) {
        Resolver<Double> resolver = Resolve.find(Double.class);
        if (resolver != null) {
          Double resolve = resolver.resolve(value);
          if (resolve != null) {
            dval = resolve;
          }
        }
      }
      return dval;
    }

    /**
     * Alters the current size of the vector if the supplied size is larger than the current.
     */
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
    public Vector getView() {
      return new DoubleVector(buffer, size()) {
        @Override
        public Builder newCopyBuilder() {
          return Builder.this;
        }
      };
    }

    @Override
    public final int size() {
      return size;
    }

    @Override
    public DoubleVector build() {
      DoubleVector vec = new DoubleVector(buffer, size(), getIndex());
      buffer = null;
      return vec;
    }
  }
}
