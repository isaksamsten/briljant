package org.briljantframework.vector;

import com.google.common.base.Preconditions;

import org.briljantframework.complex.Complex;
import org.briljantframework.io.DataEntry;
import org.briljantframework.matrix.Matrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @
 */
public class ObjectVector extends AbstractVector implements VariableVector {

  private final List<?> values;

  /**
   * Constructs a {@code VariableVector}
   *
   * @param values the values
   */
  public ObjectVector(List<?> values) {
    this.values = values;
  }

  @Override
  public <T> T get(Class<T> cls, int index) {
    Object value = values.get(index);
    if (cls.isInstance(value)) {
      return cls.cast(value);
    } else {
      return Na.of(cls);
    }
  }

  @Override
  public String toString(int index) {
    Object value = values.get(index);
    return value == null || Is.NA(value) ? "NA" : value.toString();
  }

  @Override
  public boolean isNA(int index) {
    return Is.NA(values.get(index));
  }

  @Override
  public double getAsDouble(int index) {
    return get(Double.class, index);
  }

  @Override
  public int getAsInt(int index) {
    return get(Integer.class, index);
  }

  @Override
  public Bit getAsBit(int index) {
    return Bit.valueOf(getAsInt(index));
  }

  @Override
  public Complex getAsComplex(int index) {
    return get(Complex.class, index);
  }

  @Override
  public String getAsString(int index) {
    return get(String.class, index);
  }

  @Override
  public int size() {
    return values.size();
  }

  @Override
  public Matrix toMatrix() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int compare(int a, int b) {
    throw new UnsupportedOperationException("TODO");
//    return getAsValue(a).compareTo(getAsValue(b));
  }

  @Override
  public int compare(int a, Vector other, int b) {
    throw new UnsupportedOperationException("TODO");
//    return getAsValue(a).compareTo(other.getAsValue(b));
  }

  @Override
  public VectorType getType(int index) {
    return Vec.inferTypeOf(get(Object.class, index));
  }

  @Override
  public Builder newCopyBuilder() {
    return new Builder(new ArrayList<>(values));
  }

  @Override
  public Builder newBuilder() {
    return new Builder();
  }

  @Override
  public Builder newBuilder(int size) {
    return new Builder(size);
  }

  // @Override
  // public Iterator<Value> iterator() {
  // return new UnmodifiableIterator<Value>() {
  // public int current = 0;
  //
  // @Override
  // public boolean hasNext() {
  // return current < size();
  // }
  //
  // @Override
  // public Value next() {
  // return get(current++);
  // }
  // };
  // }

  public static class Builder implements Vector.Builder {

    private List<Object> buffer;


    private Builder(List<Object> buffer) {
      this.buffer = buffer;
    }

    public Builder() {
      this(0, INITIAL_CAPACITY);
    }

    public Builder(int size) {
      this(size, size);
    }

    public Builder(int size, int capacity) {
      buffer = new ArrayList<>(Math.max(size, capacity));
      for (int i = 0; i < size; i++) {
        buffer.add(null);
      }
    }

    @Override
    public Builder setNA(int index) {
      ensureCapacity(index);
      buffer.set(index, null);
      return this;
    }

    @Override
    public Builder addNA() {
      return setNA(size());
    }

    @Override
    public Builder add(Vector from, int fromIndex) {
      set(size(), from, fromIndex);
      return this;
    }

    @Override
    public Builder set(int atIndex, Vector from, int fromIndex) {
      if (from == null) {
        setNA(atIndex);
      } else {
        ensureCapacity(atIndex);
//        buffer.set(atIndex, from.getAsValue(fromIndex));
      }
      return this;
    }

    @Override
    public Builder set(int index, Object obj) {
      if (Is.NA(obj)) {
        setNA(index);
      } else {
        ensureCapacity(index);
        buffer.set(index, obj);
      }
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
        buffer.add(from.get(Object.class, i));
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
      throw new UnsupportedOperationException("TODO");
//      return TYPE.compare(buffer.get(a), buffer.get(b));
    }

    @Override
    public void swap(int a, int b) {
      Preconditions.checkArgument(a >= 0 && a < size() && b >= 0 && b < size());
      Collections.swap(buffer, a, b);
    }

    @Override
    public Builder read(DataEntry entry) throws IOException {
      return read(size(), entry);
    }

    @Override
    public Builder read(int index, DataEntry entry) throws IOException {
      set(index, entry.nextString());
      return this;
    }

    @Override
    public int size() {
      return buffer.size();
    }

    @Override
    public Vector getTemporaryVector() {
      return new ObjectVector(this.buffer);
    }

    @Override
    public ObjectVector build() {
      return new ObjectVector(buffer);
    }

    private void ensureCapacity(int index) {
      while (buffer.size() <= index) {
        buffer.add(Na.of(Object.class));
      }
    }
  }
}
