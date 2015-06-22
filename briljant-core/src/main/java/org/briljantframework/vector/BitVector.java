package org.briljantframework.vector;

import com.google.common.base.Preconditions;

import com.carrotsearch.hppc.IntArrayList;

import org.briljantframework.Bj;
import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.resolver.Resolver;
import org.briljantframework.io.resolver.Resolvers;
import org.briljantframework.matrix.BitArray;
import org.briljantframework.matrix.Array;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Created by Isak Karlsson on 20/11/14.
 */
public class BitVector extends AbstractVector {

  public static final Bit NA = Bit.NA;
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
      return Bit.class;
    }

    @Override
    public boolean isNA(Object value) {
      return value == null ||
             (value instanceof Bit && value.equals(BitVector.NA)) ||
             (value instanceof Integer && (int) value == IntVector.NA);
    }

    @Override
    public int compare(int a, Vector va, int b, Vector ba) {
      return va.getAsInt(a) - ba.getAsInt(b);
    }

    @Override
    public Scale getScale() {
      return Scale.NOMINAL;
    }

    @Override
    public String toString() {
      return "binary";
    }
  };
  private int[] values;
  private final int size;

  protected BitVector(IntArrayList values) {
    this.values = values.toArray();
    this.size = this.values.length;
  }

  public BitVector(boolean... values) {
    this.values = new int[values.length];
    for (int i = 0; i < values.length; i++) {
      this.values[i] = values[i] ? 1 : 0;
    }
    this.size = values.length;
  }

  public BitVector(int... values) {
    this.values = Arrays.copyOf(values, values.length);
    this.size = this.values.length;
  }

  private BitVector(int[] buffer, int size) {
    this.values = buffer;
    this.size = size;
  }

  public static Builder newBuilderWithInitialValues(Object... values) {
    Builder builder = new Builder(0, values.length);
    builder.addAll(Arrays.asList(values));
    return builder;
  }

  @Override
  public int getAsInt(int index) {
    return values[index];
  }

  @Override
  public Builder newCopyBuilder() {
    return new Builder(toIntArray());
  }

  public int[] asIntArray() {
    return values;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public <T> T get(Class<T> cls, int index) {
    if (cls.isAssignableFrom(Bit.class)) {
      return cls.cast(getAsBit(index));
    } else {
      if (cls.isAssignableFrom(Double.class)) {
        return cls.cast(getAsDouble(index));
      } else if (cls.isAssignableFrom(Complex.class)) {
        return cls.cast(getAsComplex(index));
      } else if (cls.isAssignableFrom(Integer.class)) {
        return cls.cast(getAsInt(index));
      } else if (cls.isAssignableFrom(String.class)) {
        return cls.cast(getAsBit(index).toString());
      } else {
        return Na.of(cls);
      }
    }
  }

  @Override
  public String toString(int index) {
    return getAsBit(index).name();
  }

  @Override
  public boolean isNA(int index) {
    return getAsInt(index) == IntVector.NA;
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
  public double getAsDouble(int index) {
    int i = getAsInt(index);
    if (i == IntVector.NA) {
      return DoubleVector.NA;
    } else {
      return i;
    }
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
  public Array toMatrix() {
    BitArray n = Bj.booleanArray(size());
    for (int i = 0; i < size(); i++) {
      n.set(i, getAsBit(i) == Bit.TRUE);
    }
    return n;
  }

  @Override
  public int compare(int a, int b) {
    return getAsInt(a) - getAsInt(b);
  }

  @Override
  public int compare(int a, Vector other, int b) {
    return getAsInt(a) - other.getAsInt(b);
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

  public static class Builder implements Vector.Builder {

    private IntArrayList buffer;

    public Builder() {
      this(0);
    }

    public Builder(int size) {
      this(size, Math.max(INITIAL_CAPACITY, size));
    }

    public Builder(int size, int capacity) {
      buffer = new IntArrayList(capacity);
      for (int i = 0; i < size; i++) {
        buffer.add(IntVector.NA);
      }
    }

    public Builder(int[] ints) {
      this.buffer = IntArrayList.from(ints);
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
      ensureCapacity(index);
      int intValue = IntVector.NA;
      if (value instanceof Number) {
        intValue = ((Number) value).intValue();
      } else if (value instanceof Bit) {
        intValue = ((Bit) value).asInt();
      } else if (value instanceof Boolean) {
        intValue = (boolean) value ? 1 : 0;
      } else {
        Resolver<Bit> resolver = Resolvers.find(Bit.class);
        if (resolver != null) {
          Bit bit = resolver.resolve(value);
          if (bit != null) {
            intValue = bit.asInt();
          }
        }
      }
      buffer.buffer[index] = intValue;
      return this;
    }

    @Override
    public Builder add(Object value) {
      return set(size(), value);
    }

    @Override
    public Builder addAll(Vector from) {
      for (int i = 0; i < from.size(); i++) {
        add(from.getAsBit(i));
      }
      return this;
    }

    @Override
    public Builder remove(int index) {
      buffer.remove(index);
      return this;
    }

    @Override
    public int compare(int a, int b) {
      return Integer.compare(buffer.get(a), buffer.get(b));
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
      Bit bit = entry.nextBinary();
      if (bit == null) {
        setNA(index);
      } else {
        set(index, bit);
      }
      return this;
    }

    @Override
    public int size() {
      return buffer.size();
    }

    @Override
    public Vector getTemporaryVector() {
      return new BitVector(buffer.buffer, buffer.size()) {
        @Override
        public Builder newCopyBuilder() {
          return BitVector.Builder.this;
        }
      };
//      return new AbstractBitVector() {
//        @Override
//        public int getAsInt(int index) {
//          return buffer.get(index);
//        }
//
//        @Override
//        public int size() {
//          return buffer.size();
//        }
//
//        @Override
//        public Builder newCopyBuilder() {
//          return BitVector.Builder.this;
//        }
//
//        @Override
//        public Builder newBuilder() {
//          return getType().newBuilder();
//        }
//
//        @Override
//        public Builder newBuilder(int size) {
//          return getType().newBuilder(size);
//        }
//      };
    }

    @Override
    public BitVector build() {
      BitVector vector = new BitVector(buffer.buffer, buffer.size());
      buffer = null;
      return vector;
    }

    public Builder set(int index, Bit value) {
      return set(index, value.asInt());
    }

    public Builder set(int index, int value) {
      ensureCapacity(index);
      buffer.buffer[index] = value;
      return this;
    }

    public Builder set(int index, boolean value) {
      return set(index, value ? 1 : 0);
    }

    public Builder add(Bit bit) {
      return add(bit.asInt());
    }

    public Builder add(int value) {
      return set(size(), value);
    }

    private void ensureCapacity(int index) {
      buffer.ensureCapacity(index + 1);
      int i = buffer.size();
      while (i <= index) {
        buffer.buffer[i++] = IntVector.NA;
        buffer.elementsCount++;
      }
    }
  }

  @Override
  public Builder newBuilder() {
    return new Builder();
  }

  @Override
  public Builder newBuilder(int size) {
    return new Builder(size);
  }

  public int[] toIntArray() {
    return values.clone();
  }


  @Override
  public IntStream intStream() {
    return IntStream.of(values);
  }


}
