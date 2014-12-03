package org.briljantframework.vector;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public abstract class AbstractBinaryVector implements Vector, Iterable<Binary> {
  public static final Binary NA = Binary.NA;
  public static Type TYPE = new Type() {
    @Override
    public BinaryVector.Builder newBuilder() {
      return null;
    }

    @Override
    public BinaryVector.Builder newBuilder(int size) {
      return null;
    }

    @Override
    public Class<?> getDataClass() {
      return Binary.class;
    }

    @Override
    public boolean isNA(Object value) {
      return value == null || (value instanceof Binary && value == NA)
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

  @Override
  public double getAsReal(int index) {
    int i = getAsInt(index);
    if (i == IntVector.NA) {
      return RealVector.NA;
    } else {
      return i;
    }
  }

  @Override
  public Binary getAsBinary(int index) {
    return Binary.valueOf(getAsInt(index));
  }

  @Override
  public String getAsString(int index) {
    Binary bin = Binary.valueOf(index);
    if (bin == Binary.NA) {
      return StringVector.NA;
    } else {
      return bin.name();
    }
  }

  @Override
  public Value getAsValue(int index) {
    Binary binary = getAsBinary(index);
    return binary == NA ? Undefined.INSTANCE : new BinaryValue(binary);
  }

  @Override
  public String toString(int index) {
    return getAsBinary(index).name();
  }

  @Override
  public boolean isNA(int index) {
    return getAsInt(index) == IntVector.NA;
  }

  @Override
  public Type getType() {
    return TYPE;
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
