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

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.Utils;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.index.Index;
import org.briljantframework.index.IntIndex;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.resolver.Resolver;
import org.briljantframework.io.resolver.Resolvers;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.DoubleStream;

/**
 * Vector of {@code double} primitives.
 *
 * <p>{@code NA} is represented by the value {@code Double.longBitsToDouble(0x7ff0000000000009L)}
 * which is in the {@code NaN} range, but distinctive from {@code Double.NaN}.
 *
 * @author Isak Karlsson
 */
public class DoubleVector extends AbstractVector {

  public static final VectorType TYPE = new VectorType() {
    @Override
    public Builder newBuilder() {
      return new Builder();
    }

    @Override
    public Builder newBuilder(int size) {
      return new Builder(size);
    }

    @Override
    public Class<?> getDataClass() {
      return Double.class;
    }

    @Override
    public boolean isNA(Object value) {
      return Is.NA(value);
    }

    @Override
    public int compare(int a, Vector va, int b, Vector ba) {
      double dva = va.loc().getAsDouble(a);
      double dba = ba.loc().getAsDouble(b);

      return !Is.NA(dva) && !Is.NA(dba) ? Double.compare(dva, dba) : 0;
    }

    @Override
    public Scale getScale() {
      return Scale.NUMERICAL;
    }

    @Override
    public String toString() {
      return "double";
    }
  };

  private final double[] buffer;
  private final int size;

  /**
   * Construct a new {@code DoubleVector} of the values in {@code values} using {@code size}
   * elements.
   *
   * @param buffer the array of values
   * @param size   the size of values to take
   */
  public DoubleVector(double[] buffer, int size) {
    Check.argument(size <= buffer.length);
    this.buffer = Arrays.copyOf(buffer, size);
    this.size = this.buffer.length;
  }

  /**
   * Construct a new {@code DoubleVector}.
   *
   * @param buffer the values
   */
  public DoubleVector(double... buffer) {
    this(buffer, true);
  }

  /**
   * Construct a new {@code DoubleVector}. If {@code copy} is true, the values of {@code values}
   * are copied otherwise not. To presever the mutable nature of
   */
  protected DoubleVector(double[] buffer, boolean copy) {
    if (copy) {
      this.buffer = Arrays.copyOf(buffer, buffer.length);
    } else {
      this.buffer = buffer;
    }
    this.size = this.buffer.length;
  }

  private DoubleVector(double[] buffer, int size, boolean copy) {
    this.buffer = buffer;
    this.size = size;
  }

  public DoubleVector(double[] buffer, int size, Index index) {
    super(index);
    this.buffer = buffer;
    this.size = size;
  }

  /**
   * Construct a new {@code Vector} using the supplied values.
   *
   * @param values the values
   * @return the double vector
   */
  public static DoubleVector wrap(double... values) {
    return new DoubleVector(values, false);
  }

  public static Vector.Builder newBuilderWithInitialValues(double... values) {
    Builder builder = new Builder(0, values.length);
    for (double value : values) {
      builder.add(value);
    }
    return builder;
  }

  @Override
  protected final double getAsDoubleAt(int i) {
    return buffer[i];
  }

  @Override
  public <T> T getAt(Class<T> cls, int index) {
    Check.argument(!cls.isPrimitive(), "can't get primitive values");
    return Convert.to(cls, getAsDoubleAt(index));
  }

  @Override
  public String toStringAt(int index) {
    double value = getAsDoubleAt(index);
    return Is.NA(value) ? "NA" : String.format("%.3f", value);
  }

  @Override
  public boolean isNaAt(int index) {
    return Is.NA(getAsDoubleAt(index));
  }

  public Complex getAsComplex(int index) {
    double v = getAsDoubleAt(index);
    if (Is.NA(v)) {
      return Complex.NaN;
    } else {
      return Complex.valueOf(v);
    }
  }

  @Override
  public int getAsIntAt(int i) {
    double value = getAsDoubleAt(i);
    return Is.NA(value) ? Na.INT : (int) value;
  }

  @Override
  public VectorType getType() {
    return TYPE;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public DoubleArray toDoubleArray() {
    return Bj.array(Arrays.copyOf(buffer, size()));
  }

  @Override
  public int compareAt(int a, Vector other, int b) {
    double va = getAsDoubleAt(a);
    double vb = other.loc().getAsDouble(b);
    return !Is.NA(va) && !Is.NA(vb) ? Double.compare(va, vb) : 0;
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
      double b = getAsDouble(key);
      if (!Is.NA(a) && !Is.NA(b) && a != b) {
        return false;
      }

    }
    return true;
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

  // Specialized double method

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

  @Override
  public DoubleVector.Builder newCopyBuilder() {
    return new DoubleVector.Builder(this);
  }

  @Override
  public DoubleVector.Builder newBuilder() {
    return new DoubleVector.Builder();
  }

  @Override
  public DoubleVector.Builder newBuilder(int size) {
    return new DoubleVector.Builder(size);
  }

  @Override
  public DoubleStream doubleStream() {
    return Arrays.stream(buffer, 0, size());
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
      super(getIndexer(vector));
      this.buffer = new double[vector.size()];
      System.arraycopy(vector.buffer, 0, this.buffer, 0, vector.size());
      this.size = buffer.length;
    }

    protected static Index.Builder getIndexer(DoubleVector vector) {
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
    public Vector.Builder add(int value) {
      final int index = size;
      ensureCapacity(size + 1); // sets the size
      buffer[index] = value;
      extendIndex(index);
      return this;
    }

    @Override
    public Vector.Builder add(double value) {
      final int index = size();
      ensureCapacity(size + 1); // sets the size
      buffer[index] = (int) value;
      extendIndex(index);
      return this;
    }

    @Override
    public Vector.Builder add(Object value) {
      final int index = size();
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

    @Override
    protected void setNaAt(int index) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
    }

    @Override
    protected void setAt(int atIndex, Vector from, Object f) {
      setAt(atIndex, from.getAsDouble(f));
    }

    @Override
    protected void setAt(int index, double value) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
      buffer[index] = value;
    }

    @Override
    protected void setAt(int index, int value) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
      buffer[index] = Is.NA(value) ? Na.DOUBLE : value;
    }

    @Override
    protected void setAt(int index, Object value) {
      final int oldSize = size;
      ensureCapacity(index + 1);
      fillNa(oldSize, size, buffer);
      buffer[index] = convert(value);
    }

    private double convert(Object value) {
      double dval = Na.DOUBLE;
      if (value instanceof Number && !Is.NA(value)) {
        dval = ((Number) value).doubleValue();
      } else if (value != null && !Is.NA(value)) {
        Resolver<Double> resolver = Resolvers.find(Double.class);
        if (resolver != null) {
          Double resolve = resolver.resolve(value);
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
      buffer[t] = from.loc().getAsDouble(f);
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
      Check.argument(a >= 0 && a < size() && b >= 0 && b < size());
      Utils.swap(buffer, a, b);
    }

    @Override
    protected void readAt(int index, DataEntry entry) throws IOException {
      setAt(index, entry.nextDouble());
    }

    @Override
    public int size() {
      return size;
    }

    @Override
    public Vector getTemporaryVector() {
      return new DoubleVector(buffer, size(), false) {
        @Override
        public Builder newCopyBuilder() {
          return Builder.this;
        }
      };
    }

    @Override
    public DoubleVector build() {
      DoubleVector vec = new DoubleVector(buffer, size(), getIndex());
      buffer = null;
      return vec;
    }

    /**
     * Fill with NA from {@code index} until {@code size}
     */
    private static void fillNa(final int from, final int until, double[] buffer) {
      for (int i = from; i < until; i++) {
        buffer[i] = Na.DOUBLE;
      }
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

    private void rangeCheck(int index) {
      if (index >= size) {
        throw new IndexOutOfBoundsException(outOfBoundsMsg(index));
      }
    }

    private String outOfBoundsMsg(int index) {
      return "Index: " + index + ", Size: " + size;
    }
  }
}
