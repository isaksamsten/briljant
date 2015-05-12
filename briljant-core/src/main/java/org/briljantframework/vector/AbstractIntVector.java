package org.briljantframework.vector;

import org.briljantframework.Bj;
import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.IntMatrix;

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
    if (cls.isAssignableFrom(Integer.class)) {
      return cls.cast(getAsInt(index));
    } else {
      return Na.of(cls);
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
  public IntMatrix toMatrix() {
    IntMatrix m = Bj.intVector(size());
    for (int i = 0; i < size(); i++) {
      m.set(i, getAsInt(i));
    }
    return m;
  }

  @Override
  public int compare(int a, int b) {
    return compare(a, this, b);
  }

  @Override
  public int compare(int a, Vector other, int b) {
//    int x = getAsInt(a);
//    int y = other.getAsInt(b);
//    boolean aIsNa = Is.NA(x);
//    boolean bIsNa = Is.NA(y);
//    if (aIsNa && !bIsNa) {
//      return -1;
//    } else if (!aIsNa && bIsNa) {
//      return 1;
//    } else {
//      return Integer.compare(x, y);
//    }
    return getType().compare(a, this, b, other);
//    return getAsInt(a) - other.getAsInt(b);
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
