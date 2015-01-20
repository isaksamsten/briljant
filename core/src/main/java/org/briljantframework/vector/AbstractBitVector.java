package org.briljantframework.vector;

import org.briljantframework.matrix.AnyMatrix;
import org.briljantframework.matrix.BitMatrix;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public abstract class AbstractBitVector implements Vector, Iterable<Bit> {
  public static final Bit NA = Bit.NA;
  public static VectorType TYPE = new VectorType() {
    @Override
    public BitVector.Builder newBuilder() {
      return null;
    }

    @Override
    public BitVector.Builder newBuilder(int size) {
      return null;
    }

    @Override
    public Class<?> getDataClass() {
      return Bit.class;
    }

    @Override
    public boolean isNA(Object value) {
      return value == null || (value instanceof Bit && value == NA)
          || (value instanceof Integer && (int) value == IntVector.NA);
    }

    @Override
    public int compare(int a, Vector va, int b, Vector ba) {
      return va.getAsInt(a) - ba.getAsInt(b);
    }

    @Override
    public Scale getScale() {
      return Scale.CATEGORICAL;
    }

    @Override
    public String toString() {
      return "binary";
    }
  };

  private BitMatrix adapter;

  @Override
  public Value getAsValue(int index) {
    Bit bit = getAsBit(index);
    return bit == NA ? Undefined.INSTANCE : new BitValue(bit);
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
  public AnyMatrix asMatrix() {
    if (adapter == null) {
      adapter = new VectorBitMatrixAdapter(this);
    }
    return adapter;
  }

  @Override
  public int compare(int a, int b) {
    return getAsInt(a) - getAsInt(b);
  }

  @Override
  public int compare(int a, int b, Vector other) {
    return getAsInt(a) - other.getAsInt(b);
  }
}
