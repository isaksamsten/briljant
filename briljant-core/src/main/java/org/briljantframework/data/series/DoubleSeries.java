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

import java.util.stream.DoubleStream;

import org.apache.commons.math3.stat.descriptive.moment.Mean;
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
 * Series of {@code double} primitives.
 *
 * @author Isak Karlsson
 */
public class DoubleSeries extends AbstractSeries {

  private final double[] buffer;
  private final int elementCount;

  private DoubleSeries(double[] buffer, int elementCount) {
    this(null, buffer, elementCount);
  }

  private DoubleSeries(Index index, double[] buffer, int elementCount) {
    this(index, 0, new int[] {elementCount}, new int[] {1}, buffer);
  }

  private DoubleSeries(Index index, int offset, int[] shape, int[] stride, double[] buffer) {
    super(index, offset, shape, stride);
    this.buffer = buffer;
    this.elementCount = ShapeUtils.size(shape);
  }

  private DoubleSeries(int offset, int[] shape, int[] stride, double[] buffer) {
    this(null, offset, shape, stride, buffer);
  }

  public DoubleSeries() {
    this(new double[0], 0);
  }

  public static DoubleSeries of(double... values) {
    return new DoubleSeries(java.util.Arrays.copyOf(values, values.length), values.length);
  }

  @Override
  public DoubleStream doubleStream() {
    return java.util.Arrays.stream(buffer, 0, size());
  }

  @Override
  public Series.Builder newBuilder() {
    return new DoubleSeries.Builder();
  }

  @Override
  public Series.Builder newBuilder(int size) {
    return new DoubleSeries.Builder(size);
  }

  @Override
  public int compareElement(int a, Series other, int b) {
    double va = getDoubleElement(a);
    double vb = other.loc().getDouble(b);
    return !Is.NA(va) && !Is.NA(vb) ? Double.compare(va, vb) : 0;
  }

  @Override
  protected boolean isElementNA(int i) {
    return Is.NA(getDoubleElement(i));
  }

  @Override
  protected int getIntElement(int i) {
    double value = getDoubleElement(i);
    return Is.NA(value) ? Na.INT : (int) value;
  }

  @Override
  protected final double getDoubleElement(int i) {
    return buffer[i];
  }

  @Override
  protected <T> T getElement(Class<T> cls, int index) {
    Check.argument(!cls.isPrimitive(), "can't get primitive values");
    return Convert.to(cls, getDoubleElement(index));
  }

  @Override
  protected void setElement(int i, Object value) {
    buffer[i] = Convert.to(Double.class, value);
  }

  @Override
  protected void setDoubleElement(int index, double value) {
    buffer[index] = value;
  }

  @Override
  protected void setIntElement(int index, int value) {
    buffer[index] = value;
  }

  @Override
  public Series reindex(Index index) {
    return new DoubleSeries(index, buffer, elementCount);
  }

  @Override
  public final int hashCode() {
    int result = 1;
    for (int i = 0; i < size(); i++) {
      long v = Double.doubleToLongBits(getDoubleElement(i));
      result = 31 * result + (int) (v ^ v >>> 32);
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
      double a = getDouble(key);
      double b = that.getDouble(key);
      if (!Is.NA(a) && !Is.NA(b) && a != b) {
        return false;
      }

    }
    return true;
  }

  @Override
  protected String getStringElement(int index) {
    double value = getDoubleElement(index);
    return Is.NA(value) ? "NA" : String.format("%.3f", value);
  }

  @Override
  protected boolean equalsElement(int a, Series other, int b) {
    double av = getDoubleElement(a);
    double ab = other.loc().getDouble(b);
    return (Is.NA(av) && Is.NA(ab)) || (Double.isNaN(av) && Double.isNaN(ab)) || av == ab;
  }

  @Override
  public Series newEmptyArray(int... shape) {
    return new DoubleSeries(null, 0, shape, StrideUtils.computeStride(shape),
        new double[ShapeUtils.size(shape)]);
  }

  @Override
  protected int elementSize() {
    return size();
  }

  @Override
  public void set(int toIndex, Series from, int fromIndex) {
    loc().set(toIndex, from.loc().getDouble(fromIndex));
  }

  @Override
  public void set(int toRow, int toColumn, Series from, int fromRow, int fromColumn) {
    loc().set(toRow, fromColumn, from.loc().getDouble(fromRow, fromColumn));
  }

  @Override
  public void set(int[] toIndex, Series from, int[] fromIndex) {
    loc().set(toIndex, from.loc().getDouble(fromIndex));
  }

  @Override
  public void set(int[] toIndex, Series from, int fromIndex) {
    loc().set(toIndex, from.loc().getDouble(fromIndex));
  }

  @Override
  public void set(int toIndex, Series from, int[] fromIndex) {
    loc().set(toIndex, from.loc().getDouble(fromIndex));
  }

  @Override
  public Series asView(int offset, int[] shape, int[] stride) {
    return new DoubleSeries(offset, shape, stride, buffer);
  }

  @Override
  public Type getType() {
    return Types.DOUBLE;
  }

  // Specialized double method

  @Override
  public double sum() {
    double sum = 0;
    for (int i = 0, size = size(); i < size; i++) {
      double v = getDoubleElement(i);
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
      double v = getDoubleElement(i);
      if (!Is.NA(v)) {
        mean.increment(v);
      }
    }
    return mean.getN() > 0 ? mean.getResult() : Na.DOUBLE;
  }

  public static final class Builder extends AbstractSeriesBuilder {

    private double[] buffer;
    private int size;

    public Builder() {
      this(0);
    }

    /**
     * Construct a double series builder filled with the specified number of <tt>NA</tt>s.
     * 
     * @param size the initial size
     */
    public Builder(int size) {
      this(size, Math.max(size, INITIAL_CAPACITY));
    }

    /**
     * Construct a double series builder filled with the specified number of <tt>NA</tt>s and the
     * specified initial capacity.
     * 
     * @param size the size
     * @param capacity the capacity
     */
    public Builder(int size, int capacity) {
      this.buffer = new double[Math.max(size, capacity)];
      for (int i = 0; i < size; i++) {
        buffer[i] = Na.DOUBLE;
      }
    }

    public Builder(DoubleSeries vector) {
      super(getIndexBuilder(vector));
      this.buffer = new double[vector.size()];
      System.arraycopy(vector.buffer, 0, this.buffer, 0, vector.size());
      this.size = buffer.length;
    }

    private static Index.Builder getIndexBuilder(DoubleSeries vector) {
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
      buffer[index] = Na.DOUBLE;
      extendIndex(index);
      return this;
    }
//
//    @Override
//    public Series.Builder addFrom(Series from, int fromIndex) {
//      return addDouble(from.loc().getDouble(fromIndex));
//    }

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
      buffer[index] = value;
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
      setElement(index, entry.nextDouble());
    }

    @Override
    protected void setElement(int atIndex, Series from, Object f) {
      setElement(atIndex, from.getDouble(f));
    }

    @Override
    protected void setElementFrom(int t, Series from, int f) {
      final int oldSize = size;
      ensureCapacity(t + 1);
      fillNa(oldSize, size, buffer);
      buffer[t] = from.loc().getDouble(f);
    }

    @Override
    protected void setElement(int index, Object value) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
      buffer[index] = convert(value);
    }

    @Override
    protected void setElementNA(int index) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
    }

    @Override
    protected void setElement(int index, int value) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
      buffer[index] = Is.NA(value) ? Na.DOUBLE : value;
    }

    @Override
    protected void setElement(int index, double value) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
      buffer[index] = value;
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
     * Alters the current size of the series if the supplied size is larger than the current.
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
    public final int size() {
      return size;
    }

    @Override
    public DoubleSeries build() {
      DoubleSeries vec = new DoubleSeries(getIndex(), buffer, size());
      buffer = null;
      return vec;
    }
  }
}
