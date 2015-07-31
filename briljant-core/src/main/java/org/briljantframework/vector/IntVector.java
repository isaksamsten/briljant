package org.briljantframework.vector;

import com.google.common.base.Preconditions;

import com.carrotsearch.hppc.IntArrayList;

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.Utils;
import org.briljantframework.array.IntArray;
import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.IllegalTypeException;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.resolver.Resolver;
import org.briljantframework.io.resolver.Resolvers;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Created by Isak Karlsson on 20/11/14.
 */
public class IntVector extends AbstractVector {

  /**
   * The constant NA.
   */
  public static final int NA = Integer.MIN_VALUE;
  public static final VectorType TYPE = new VectorType() {
    @Override
    public Builder newBuilder() {
      return new Builder();
    }

    @Override
    public Builder newBuilder(int size) {
      return new Builder(size, size);
    }

    @Override
    public Class<?> getDataClass() {
      return Integer.class;
    }

    @Override
    public boolean isNA(Object value) {
      return value == null || (value instanceof Integer && (int) value == IntVector.NA);
    }

    @Override
    public int compare(int a, Vector va, int b, Vector ba) {
      int x = va.getAsInt(a);
      int y = ba.getAsInt(b);
      boolean aIsNa = Is.NA(x);
      boolean bIsNa = Is.NA(y);
      if (aIsNa && !bIsNa) {
        return -1;
      } else if (!aIsNa && bIsNa) {
        return 1;
      } else {
        return Integer.compare(x, y);
      }
//      return !va.isNA(a) && !ba.isNA(b) ? va.getAsInt(a) - ba.getAsInt(b) : 0;
    }

    @Override
    public Scale getScale() {
      return Scale.NUMERICAL;
    }

    @Override
    public String toString() {
      return "int";
    }
  };
  private final int[] values;
  private final int size;

  public IntVector(int... values) {
    this(values, values.length);
  }

  public IntVector(int[] values, int size) {
    this(values, size, true);
  }

  IntVector(int[] values, int size, boolean safe) {
    if (safe) {
      this.values = Arrays.copyOf(values, size);
    } else {
      this.values = values;
    }
    this.size = size;
  }

  public static Builder newBuilderWithInitialValues(int... values) {
    Builder builder = new Builder(0, values.length);
    for (int value : values) {
      builder.add(value);
    }
    return builder;
  }

  public static IntVector unsafe(int[] newLeftPool) {
    return new IntVector(newLeftPool, newLeftPool.length, false);
  }

  public static IntVector range(int end) {
    int[] v = new int[end];
    for (int i = 0; i < v.length; i++) {
      v[i] = i;
    }
    return new IntVector(v, v.length, false);
  }

  @Override
  public int getAsInt(int index) {
    return values[index];
  }

  @Override
  public Builder newCopyBuilder() {
    return new Builder(toIntArray());
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
  public <T> T get(Class<T> cls, int index) {
    Check.argument(!cls.isPrimitive(), "Use getAs[primitive]");
    int v = getAsInt(index);
    if (Is.NA(v)) {
      return Na.of(cls);
    }
    if (cls.isAssignableFrom(Integer.class)) {
      return cls.cast(v);
    } else {
      if (cls.isAssignableFrom(Double.class)) {
        return cls.cast(getAsDouble(index));
      } else if (cls.isAssignableFrom(Complex.class)) {
        return cls.cast(getAsComplex(index));
      } else if (cls.isAssignableFrom(Bit.class)) {
        return cls.cast(getAsBit(index));
      } else if (cls.isAssignableFrom(String.class)) {
        return cls.cast(Integer.toString(v));
      } else {
        return Na.of(cls);
      }
    }
  }

  @Override
  public String toString(int index) {
    int value = getAsInt(index);
    return value == NA ? "NA" : String.valueOf(value);
  }

  @Override
  public boolean isNA(int index) {
    return getAsInt(index) == NA;
  }

  @Override
  public double getAsDouble(int index) {
    int value = getAsInt(index);
    return value == NA ? DoubleVector.NA : value;
  }

  @Override
  public IntArray asIntArray() throws IllegalTypeException {
    IntArray array = Bj.intArray(size());
    for (int i = 0; i < size(); i++) {
      array.set(i, getAsInt(i));
    }
    return array;
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
  public int compare(int a, int b) {
    return compare(a, this, b);
  }

  @Override
  public int compare(int a, Vector other, int b) {
    return getType().compare(a, this, b, other);
  }

  @Override
  public int hashCode() {
    int code = 1;
    for (int i = 0; i < size(); i++) {
      code += 31 * getAsInt(i);
    }
    return code;
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
          if (getAsInt(i) != ov.getAsInt(i)) {
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

  @Override
  public int size() {
    return size;
  }

  @Override
  public Builder newBuilder() {
    return new Builder();
  }

  @Override
  public Builder newBuilder(int size) {
    return new Builder(size, size);
  }

  public int[] toIntArray() {
    return Arrays.copyOf(values, size());
  }

  @Override
  public IntStream intStream() {
    return Arrays.stream(values, 0, size());
  }

  public static final class Builder implements Vector.Builder {

    private IntArrayList buffer;

    public Builder() {
      this(0, INITIAL_CAPACITY);
    }

    public Builder(int size) {
      this(0, size);
    }

    public Builder(int size, int capacity) {
      buffer = new IntArrayList(Math.max(size, capacity));
      for (int i = 0; i < size; i++) {
        buffer.add(NA);
      }
    }

    Builder(int[] values) {
      buffer = IntArrayList.from(values);
    }

    @Override
    public Builder setNA(int index) {
      ensureCapacity(index);
      buffer.buffer[index] = IntVector.NA;
      return this;
    }

    @Override
    public Builder addNA() {
      return setNA(size());
    }

    @Override
    public Builder add(Vector from, int fromIndex) {
      return set(size(), from, fromIndex);
    }

    @Override
    public Builder set(int atIndex, Vector from, int fromIndex) {
      ensureCapacity(atIndex);
      buffer.buffer[atIndex] = from.getAsInt(fromIndex);
      return this;
    }

    @Override
    public Builder set(int index, Object value) {
      if (value == null) {
        return setNA(index);
      }
      if (value instanceof Number) {
        ensureCapacity(index);
        buffer.buffer[index] = ((Number) value).intValue();
      } else {
        Resolver<Integer> resolver = Resolvers.find(Integer.class);
        if (resolver != null) {
          ensureCapacity(index);
          buffer.buffer[index] = resolver.resolve(value);
        } else {
          setNA(index);
        }
      }

      return this;
    }

    @Override
    public Builder add(Object value) {
      return set(size(), value);
    }

    @Override
    public Builder addAll(Vector from) {
      for (int i = 0; i < from.size(); i++) {
        add(from.getAsInt(i));
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
      int x = buffer.get(a);
      int y = buffer.get(b);
      boolean aIsNa = Is.NA(x);
      boolean bIsNa = Is.NA(y);
      if (aIsNa && !bIsNa) {
        return 1;
      } else if (!aIsNa && bIsNa) {
        return -1;
      } else {
        return Integer.compare(x, y);
      }
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
      set(index, entry.nextInt());
      return this;
    }

    @Override
    public int size() {
      return buffer.size();
    }

    @Override
    public Vector getTemporaryVector() {
      return new IntVector(buffer.buffer, buffer.size(), false) {
        @Override
        public Builder newCopyBuilder() {
          return Builder.this;
        }
      };
    }

    @Override
    public IntVector build() {
      IntVector vector = new IntVector(buffer.buffer, buffer.size());
      buffer = null;
      return vector;
    }

    public Builder add(int value) {
      return set(size(), value);
    }

    public Builder set(int index, int value) {
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
