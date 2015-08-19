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

package org.briljantframework.vector;

import com.carrotsearch.hppc.DoubleArrayList;

import org.apache.commons.math3.complex.Complex;
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
import java.util.stream.Collector;
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
      double dva = va.getAsDouble(a);
      double dba = ba.getAsDouble(b);

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

  private final double[] values;
  private final int size;

  /**
   * Construct a new {@code DoubleVector} of the values in {@code values} using {@code size}
   * elements.
   *
   * @param values the array of values
   * @param size   the size of values to take
   */
  public DoubleVector(double[] values, int size) {
    Check.argument(size <= values.length);
    this.values = Arrays.copyOf(values, size);
    this.size = this.values.length;
  }

  /**
   * Construct a new {@code DoubleVector}.
   *
   * @param values the values
   */
  public DoubleVector(double... values) {
    this(values, true);
  }

  /**
   * Construct a new {@code DoubleVector}. If {@code copy} is true, the values of {@code values}
   * are copied otherwise not. To presever the mutable nature of
   */
  protected DoubleVector(double[] values, boolean copy) {
    if (copy) {
      this.values = Arrays.copyOf(values, values.length);
    } else {
      this.values = values;
    }
    this.size = this.values.length;
  }

  private DoubleVector(double[] buffer, int size, boolean copy) {
    this.values = buffer;
    this.size = size;
  }

  public DoubleVector(double[] values, int size, Index index) {
    super(index);
    this.values = values;
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

  public static Collector<Double, ?, Builder> collector() {
    return Collector.of(Builder::new, Builder::add, (builder, builder2) -> {
      builder.addAll(builder2.build());
      return builder;
    });
  }

  @Override
  public double getAsDouble(int index) {
    return values[index];
  }

  @Override
  public DoubleVector.Builder newCopyBuilder() {
    return new DoubleVector.Builder(this);
  }

  @Override
  public <T> T get(Class<T> cls, int index) {
    Check.argument(!cls.isPrimitive(), "can't get primitive values");
    return Convert.to(cls, getAsDouble(index));
  }

  @Override
  public String toString(int index) {
    double value = getAsDouble(index);
    return Is.NA(value) ? "NA" : String.format("%.3f", value);
  }

  @Override
  public boolean isNA(int index) {
    return Is.NA(getAsDouble(index));
  }

  public Complex getAsComplex(int index) {
    double v = getAsDouble(index);
    if (Is.NA(v)) {
      return Complex.NaN;
    } else {
      return Complex.valueOf(v);
    }
  }

  @Override
  public int getAsInt(int index) {
    double value = getAsDouble(index);
    return Is.NA(value) ? Na.INT : (int) value;
  }

  public Logical getAsBit(int index) {
    return Logical.valueOf(getAsInt(index));
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
    return Bj.array(Arrays.copyOf(values, size()));
  }

  @Override
  public int compare(int a, int b) {
    double va = getAsDouble(a);
    double vb = getAsDouble(b);
    return !Is.NA(va) && !Is.NA(vb) ? Double.compare(va, vb) : 0;
  }

  @Override
  public int compare(int a, Vector other, int b) {
    double va = getAsDouble(a);
    double vb = other.getAsDouble(b);
    return !Is.NA(va) && !Is.NA(vb) ? Double.compare(va, vb) : 0;
  }

  @Override
  public int hashCode() {
    int code = 1;
    for (int i = 0; i < size(); i++) {
      long v = Double.doubleToLongBits(getAsDouble(i));
      code += 31 * (int) (v ^ v >>> 32);
    }
    return code;
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
    return Arrays.stream(values, 0, size());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof Vector) {
      Vector ov = (Vector) o;
      if (size() == ov.size()) {
        for (int i = 0; i < size(); i++) {
          if (getAsDouble(i) != ov.getAsDouble(i)) {
            return false;
          }
        }
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  public static class Builder extends AbstractBuilder {

    private DoubleArrayList buffer;

    public Builder() {
      this(0);
    }

    public Builder(int size) {
      this(size, Math.max(size, INITIAL_CAPACITY));
    }

    public Builder(int size, int capacity) {
      super(new IntIndex.Builder(size));
      this.buffer = new DoubleArrayList(Math.max(size, capacity));
      for (int i = 0; i < size; i++) {
        buffer.add(Na.DOUBLE);
      }
    }

    public Builder(DoubleVector vector) {
      super(vector.getIndex().newCopyBuilder());
      this.buffer = new DoubleArrayList(vector.size());
      for (int i = 0; i < vector.size(); i++) {
        this.buffer.add(vector.getAsDouble(i));
      }
    }

    @Override
    public Builder setNA(int index) {
      ensureCapacity(index);
      buffer.buffer[index] = Na.DOUBLE;
      this.indexer.set(index, index);
      return this;
    }

    @Override
    public Vector.Builder set(int atIndex, Vector from, Object fromKey) {
      setAt(atIndex, from.getAsDouble(fromKey));
      return this;
    }

    @Override
    public Builder add(double value) {
      return set(size(), value);
    }

    @Override
    public Builder set(int index, double value) {
      ensureCapacity(index);
      buffer.buffer[index] = value;
      this.indexer.set(index, index);
      return this;
    }

    @Override
    void setAt(int index, Object value) {
      ensureCapacity(index);
      double dval = Na.DOUBLE;
      if (value instanceof Number) {
        dval = ((Number) value).doubleValue();
      } else if (value != null) {
        Resolver<Double> resolver = Resolvers.find(Double.class);
        if (resolver != null) {
          Double resolve = resolver.resolve(value);
          if (resolve != null) {
            dval = resolve;
          }
        }
      }
      buffer.buffer[index] = dval;
    }

    @Override
    void setAt(int atIndex, Vector from, int fromIndex) {
      ensureCapacity(atIndex);
      buffer.buffer[atIndex] = from.getAsDouble(fromIndex);
    }

    @Override
    public Builder addAll(Vector from) {
      for (int i = 0; i < from.size(); i++) {
        add(from.getAsDouble(i));
      }
      return this;
    }

    @Override
    public Vector.Builder remove(int index) {
      buffer.remove(index);
      return this;
    }

    @Override
    public int compare(int a, int b) {
      return Double.compare(buffer.get(a), buffer.get(b));
    }

    @Override
    public void swap(int a, int b) {
      Check.argument(a >= 0 && a < size() && b >= 0 && b < size());
      Utils.swap(buffer.buffer, a, b);
    }

    @Override
    public Vector.Builder read(int index, DataEntry entry) throws IOException {
      double value = entry.nextDouble();
      set(index, value);
      return this;
    }

    @Override
    public int size() {
      return buffer.size();
    }

    @Override
    public Vector getTemporaryVector() {
      return new DoubleVector(buffer.buffer, buffer.size(), false) {
        @Override
        public Builder newCopyBuilder() {
          return Builder.this;
        }
      };
    }

    @Override
    public DoubleVector build() {
      DoubleVector vec = new DoubleVector(buffer.buffer, size(), indexer.build());
      buffer = null;
      return vec;
    }

    private void ensureCapacity(int index) {
      buffer.ensureCapacity(index + 1);
      int i = buffer.size();
      while (i <= index) {
        if (i < index) {
          this.indexer.set(i, i);
        }
        buffer.buffer[i++] = Na.DOUBLE;
        buffer.elementsCount++;
      }
    }
  }
}
