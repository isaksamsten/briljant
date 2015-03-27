package org.briljantframework.vector;

import org.briljantframework.matrix.DefaultIntMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.storage.VectorStorage;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractIntVector extends AbstractVector {

  /**
   * The constant NA.
   */
  public static final int NA = Integer.MIN_VALUE;
  public static final VectorType TYPE = new VectorType() {
    @Override
    public IntVector.Builder newBuilder() {
      return new IntVector.Builder();
    }

    @Override
    public IntVector.Builder newBuilder(int size) {
      return new IntVector.Builder(size, size);
    }

    @Override
    public Class<?> getDataClass() {
      return Integer.TYPE;
    }

    @Override
    public boolean isNA(Object value) {
      return value == null || (value instanceof Integer && (int) value == IntVector.NA);
    }

    @Override
    public int compare(int a, Vector va, int b, Vector ba) {
      return !va.isNA(a) && !ba.isNA(b) ? va.getAsInt(a) - ba.getAsInt(b) : 0;
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

  @Override
  public Value getAsValue(int index) {
    int value = getAsInt(index);
    return Is.NA(value) ? Undefined.INSTANCE : new IntValue(value);
  }

  @Override
  public <T> T get(Class<T> cls, int index) {
    if (cls.isAssignableFrom(Integer.class)) {
      return cls.cast(getAsInt(index));
    } else {
      return Vectors.naValue(cls);
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
  public Bit getAsBit(int index) {
    return Bit.valueOf(getAsInt(index));
  }

  @Override
  public String getAsString(int index) {
    int value = getAsInt(index);
    return value == NA ? StringVector.NA : String.valueOf(value);
  }

  @Override
  public VectorType getType() {
    return TYPE;
  }

  @Override
  public IntMatrix asMatrix() {
    return new DefaultIntMatrix(new VectorStorage(this));
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
}
