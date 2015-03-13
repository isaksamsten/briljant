package org.briljantframework.vector;

import org.briljantframework.exceptions.TypeConversionException;
import org.briljantframework.io.DataEntry;
import org.briljantframework.matrix.Matrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by isak on 12/03/15.
 */
public class GenericVector extends AbstractVector {

  private final VectorType type;
  private final Class<?> cls;
  private final List<Object> values;

  protected GenericVector(Class<?> cls, List<Object> values, boolean copy) {
    this.cls = cls;
    this.values = copy ? new ArrayList<>(values) : values;
    this.type = VectorType.getInstance(cls);
  }

  @SuppressWarnings("unchecked")
  public <T> GenericVector(Class<T> cls, List<T> values) {
    this(cls, (List<Object>) values, true);
  }

  @Override
  public Value get(int index) {
    return new GenericValue(getAs(cls, index));
  }

  @Override
  public <T> T getAs(Class<T> cls, int index) {
    Object obj = values.get(index);
    if (obj == null || !cls.isInstance(obj)) {
      return Vectors.naValue(cls);
    }
    return cls.cast(obj);
  }

  @Override
  public String toString(int index) {
    Object o = values.get(index);
    return o == null ? "NA" : o.toString();
  }

  @Override
  public boolean isNA(int index) {
    return values.get(index) == null;
  }

  @Override
  public double getAsDouble(int index) {
    return getAs(Double.TYPE, index);
  }

  @Override
  public int getAsInt(int index) {
    return getAs(Integer.TYPE, index);
  }

  @Override
  public Bit getAsBit(int index) {
    return Bit.valueOf(getAsInt(index));
  }

  @Override
  public String getAsString(int index) {
    return toString(index);
  }

  @Override
  public int size() {
    return values.size();
  }

  @Override
  public VectorType getType() {
    return type;
  }

  @Override
  public Matrix asMatrix() throws TypeConversionException {
    throw new TypeConversionException(String.format("Can't convert vector(%s) to matrix", cls));
  }

  @Override
  @SuppressWarnings("unchecked")
  public int compare(int a, int b) {
    if (Comparable.class.isAssignableFrom(cls)) {
      return getAs(Comparable.class, a).compareTo(getAs(Comparable.class, b));
    }
    throw new UnsupportedOperationException();
  }

  @Override
  @SuppressWarnings("unchecked")
  public int compare(int a, Vector other, int b) {
    if (Comparable.class.isAssignableFrom(cls)) {
      return getAs(Comparable.class, a).compareTo(other.getAs(Comparable.class, b));
    }
    throw new UnsupportedOperationException();
  }

  public static class Builder implements Vector.Builder {

    private final Class<?> cls;
    private List<Object> buffer;

    public Builder(Class<?> cls) {
      this.cls = cls;
      buffer = new ArrayList<>();
    }

    public Builder(Class<?> cls, int size) {
      this.cls = cls;
      buffer = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        buffer.add(null);
      }
    }

    @Override
    public Vector.Builder setNA(int index) {
      ensureCapacity(index);
      buffer.set(index, null);
      return this;
    }

    @Override
    public Vector.Builder addNA() {
      buffer.add(null);
      return this;
    }

    @Override
    public Vector.Builder add(Vector from, int fromIndex) {
      buffer.add(from.getAs(cls, fromIndex));
      return this;
    }

    @Override
    public Vector.Builder set(int atIndex, Vector from, int fromIndex) {
      ensureCapacity(atIndex);
      buffer.set(atIndex, from.getAs(cls, fromIndex));
      return this;
    }

    @Override
    public Vector.Builder set(int index, Object value) {
      ensureCapacity(index);
      if (value != null && cls.isInstance(value)) {
        buffer.set(index, value);
      } else {
        buffer.set(index, null);
      }
      return this;
    }

    @Override
    public Vector.Builder add(Object value) {
      return set(size(), value);
    }

    @Override
    public Vector.Builder addAll(Vector from) {
      for (int i = 0; i < from.size(); i++) {
        add(from.getAs(cls, i));
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
      if (Comparable.class.isAssignableFrom(cls)) {
        Comparable va = (Comparable) buffer.get(a);
        Comparable vb = (Comparable) buffer.get(b);
        @SuppressWarnings("unchecked")
        int cmp = va.compareTo(vb);
        return cmp;
      }
      throw new UnsupportedOperationException();
    }

    @Override
    public void swap(int a, int b) {
      Collections.swap(buffer, a, b);
    }

    @Override
    public Vector.Builder read(DataEntry entry) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public Vector.Builder read(int index, DataEntry entry) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
      return buffer.size();
    }

    @Override
    public Vector getTemporaryVector() {
      return new GenericVector(cls, buffer, false);
    }

    @Override
    public Vector build() {
      Vector vector = new GenericVector(cls, buffer, false);
      buffer = null;
      return vector;
    }

    private void ensureCapacity(int index) {
      while (index >= buffer.size()) {
        buffer.add(null);
      }
    }
  }
}
