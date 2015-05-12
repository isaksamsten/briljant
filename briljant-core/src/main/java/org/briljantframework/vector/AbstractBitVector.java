package org.briljantframework.vector;

import org.briljantframework.Bj;
import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.BitMatrix;
import org.briljantframework.matrix.Matrix;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractBitVector extends AbstractVector {

  public static final Bit NA = Bit.NA;
  public static VectorType TYPE = new VectorType() {
    @Override
    public BitVector.Builder newBuilder() {
      return new BitVector.Builder();
    }

    @Override
    public BitVector.Builder newBuilder(int size) {
      return new BitVector.Builder(size);
    }

    @Override
    public Class<?> getDataClass() {
      return Bit.class;
    }

    @Override
    public boolean isNA(Object value) {
      return value == null ||
             (value instanceof Bit && value.equals(NA)) ||
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

  @Override
  public Value getAsValue(int index) {
    Bit bit = getAsBit(index);
    return bit == NA ? Undefined.INSTANCE : new BitValue(bit);
  }

  @Override
  public <T> T get(Class<T> cls, int index) {
    if (cls.isAssignableFrom(Bit.class)) {
      return cls.cast(getAsBit(index));
    } else {
      return Na.of(cls);
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
  public String getAsString(int index) {
    Bit bin = Bit.valueOf(index);
    if (bin == Bit.NA) {
      return StringVector.NA;
    } else {
      return bin.name();
    }
  }

  @Override
  public VectorType getType() {
    return TYPE;
  }

  @Override
  public Matrix toMatrix() {
    BitMatrix n = Bj.booleanVector(size());
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
}
