package org.briljantframework.vector;

import com.google.common.base.Preconditions;

import com.carrotsearch.hppc.DoubleArrayList;

import org.briljantframework.Bj;
import org.briljantframework.Utils;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.complex.Complex;
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

  /**
   * The value denoting {@code NA} for a {@code double}. The value lies in
   * a valid IEEE 754 floating point range for {@code NaN} values. Since no floating point
   * operations can distinguish between values in the {@code NaN} range (see {@link
   * Double#longBitsToDouble(long)}, a mask {@link #NA_MASK} in conjunction with an expected
   * return value {@link #NA_RES} can be used to find if a particular {@code NaN} value is also
   * {@code NA}. The most straight forward way is
   * <pre>{@code
   *  Double.isNaN(value) && Double.doubleToRawLongBits(value) & NA_MASK == NA_RES
   * }</pre>
   *
   * <p>This implementation is provided by {@link Is#NA(double)}.
   */
  public static final double NA = Double.longBitsToDouble(0x7ff0000000000009L);

  /**
   * The mask used in conjunction with {@link #NA} and and {@link #NA_RES} to recognize
   * a {@code NA} value from {@link Double#NaN}.
   */
  public static final long NA_MASK = 0x000000000000000FL;
  public static final int NA_RES = 9;
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
    Preconditions.checkArgument(size <= values.length);
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
      builder.addAll(builder2);
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
    double v = getAsDouble(index);
    if (Is.NA(v)) {
      return Na.of(cls);
    }

    if (cls.isAssignableFrom(Double.class)) {
      return cls.cast(v);
    } else {
      if (cls.isAssignableFrom(Integer.class)) {
        return cls.cast(getAsInt(index));
      } else if (cls.isAssignableFrom(Complex.class)) {
        return cls.cast(getAsComplex(index));
      } else if (cls.isAssignableFrom(Bit.class)) {
        return cls.cast(getAsBit(index));
      } else if (cls.isAssignableFrom(String.class)) {
        return cls.cast(Double.toString(v));
      } else {
        return Na.of(cls);
      }
    }
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

  @Override
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
    return Is.NA(value) ? IntVector.NA : (int) value;
  }

  @Override
  public Bit getAsBit(int index) {
    return Bit.valueOf(getAsInt(index));
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
  public DoubleArray asDoubleArray() {
    DoubleArray x = Bj.doubleArray(size());
    for (int i = 0; i < size(); i++) {
      x.set(i, getAsDouble(i));
    }
    return x;
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
  public double[] toDoubleArray() {
    return Arrays.copyOf(values, size());
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

  public static class Builder implements Vector.Builder {

    private DoubleArrayList buffer;

    public Builder() {
      this(0);
    }

    public Builder(int size) {
      this(size, Math.max(size, INITIAL_CAPACITY));
    }

    public Builder(int size, int capacity) {
      this.buffer = new DoubleArrayList(Math.max(size, capacity));
      for (int i = 0; i < size; i++) {
        buffer.add(NA);
      }
    }

    public Builder(DoubleVector vector) {
      this.buffer = new DoubleArrayList(vector.size());
      for (int i = 0; i < vector.size(); i++) {
        this.buffer.add(vector.getAsDouble(i));
      }
    }

    @Override
    public Builder setNA(int index) {
      ensureCapacity(index);
      buffer.buffer[index] = DoubleVector.NA;
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Builder addNA() {
      setNA(size());
      return this;
    }

    @Override
    public Builder add(Vector from, int fromIndex) {
      set(size(), from, fromIndex);
      return this;
    }

    @Override
    public Builder set(int atIndex, Vector from, int fromIndex) {
      ensureCapacity(atIndex);
      buffer.buffer[atIndex] = from.getAsDouble(fromIndex);
      return this;
    }

    @Override
    public Builder set(int index, Object value) {
      if (value == null) {
        return setNA(index);
      }
      ensureCapacity(index);
      double dval = DoubleVector.NA;
      if (value instanceof Number) {
        dval = ((Number) value).doubleValue();
      } else {
        Resolver<Double> resolver = Resolvers.find(Double.class);
        if (resolver != null) {
          Double resolve = resolver.resolve(value);
          if (resolve != null) {
            dval = resolve;
          }
        }
      }
      buffer.buffer[index] = dval;
      return this;
    }

    @Override
    public Builder add(Object value) {
      set(size(), value);
      return this;
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
      Preconditions.checkArgument(a >= 0 && a < size() && b >= 0 && b < size());
      Utils.swap(buffer.buffer, a, b);
    }

    @Override
    public Vector.Builder read(DataEntry entry) throws IOException {
      return read(size(), entry);
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
      DoubleVector vec = new DoubleVector(buffer.buffer, size());
      buffer = null;
      return vec;
    }

    public void addAll(Builder builder) {
      for (int i = 0; i < builder.buffer.size(); i++) {
        add(builder.buffer.get(i));
      }
    }

    public Builder add(double value) {
      return set(size(), value);
    }

    public Builder set(int index, double value) {
      ensureCapacity(index);
      buffer.buffer[index] = value;
      return this;
    }

    private void ensureCapacity(int index) {
      buffer.ensureCapacity(index + 1);
      int i = buffer.size();
      while (i <= index) {
        buffer.buffer[i++] = NA;
        buffer.elementsCount++;
      }
    }

  }


}
