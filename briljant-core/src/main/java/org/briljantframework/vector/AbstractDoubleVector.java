package org.briljantframework.vector;

import org.briljantframework.Bj;
import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.DoubleMatrix;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public abstract class AbstractDoubleVector extends AbstractVector {

  public static final VectorType TYPE = new VectorType() {
    @Override
    public DoubleVector.Builder newBuilder() {
      return new DoubleVector.Builder();
    }

    @Override
    public DoubleVector.Builder newBuilder(int size) {
      return new DoubleVector.Builder(size);
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
      return "real";
    }
  };

  @Override
  public <T> T get(Class<T> cls, int index) {
    if (cls.isAssignableFrom(Double.class)) {
      return cls.cast(getAsDouble(index));
    } else {
      if (cls.isAssignableFrom(Integer.class)) {
        return cls.cast(getAsInt(index));
      } else if (cls.isAssignableFrom(Complex.class)) {
        return cls.cast(getAsComplex(index));
      } else if (cls.isAssignableFrom(Bit.class)) {
        return cls.cast(getAsBit(index));
      } else {
        return Na.of(cls);
      }
    }
  }

  @Override
  public String toString(int index) {
    double value = getAsDouble(index);
    return Is.NA(value) ? "NA" : Double.toString(value);
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
  public DoubleMatrix toMatrix() {
    DoubleMatrix x = Bj.doubleVector(size());
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

}
