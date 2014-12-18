package org.briljantframework.vector;

import java.io.IOException;

import org.briljantframework.io.DataEntry;

/**
 * Undefined is an immutable 1 size vector returning NA
 * <p>
 * Created by Isak Karlsson on 26/11/14.
 */
public class Undefined implements Value {

  public static final Undefined INSTANCE = new Undefined();
  protected static final String UNDEFINED_DOES_NOT_HAVE_A_SCALE =
      "Undefined does not have a scale.";

  public static final Type TYPE = new Type() {
    @Override
    public Builder newBuilder() {
      return Builder.INSTANCE;
    }

    @Override
    public Builder newBuilder(int size) {
      return Builder.INSTANCE;
    }

    @Override
    public Class<?> getDataClass() {
      return Object.class;
    }

    @Override
    public boolean isNA(Object value) {
      return true;
    }

    @Override
    public int compare(int a, Vector va, int b, Vector ba) {
      return 0;
    }

    @Override
    public Scale getScale() {
      throw new UnsupportedOperationException(UNDEFINED_DOES_NOT_HAVE_A_SCALE);
    }
  };
  protected static final String ILLEGAL = "Can't index undefined.";

  @Override
  public Value getAsValue(int index) {
    return this;
  }

  @Override
  public String toString(int index) {
    return "NA";
  }

  @Override
  public boolean isNA(int index) {
    return true;
  }

  @Override
  public double getAsDouble(int index) {
    return DoubleVector.NA;
  }

  @Override
  public int getAsInt(int index) {
    return IntVector.NA;
  }

  @Override
  public Binary getAsBinary(int index) {
    return BinaryVector.NA;
  }

  @Override
  public String getAsString(int index) {
    return StringVector.NA;
  }

  @Override
  public int size() {
    return 0;
  }

  @Override
  public Type getType() {
    return TYPE;
  }

  @Override
  public Builder newCopyBuilder() {
    return Builder.INSTANCE;
  }

  @Override
  public Builder newBuilder() {
    return Builder.INSTANCE;
  }

  @Override
  public Builder newBuilder(int size) {
    return Builder.INSTANCE;
  }

  @Override
  public int compare(int a, int b) {
    return 0;
  }

  @Override
  public int compare(int a, int b, Vector other) {
    return 0;
  }

  @Override
  public int compareTo(Value o) {
    return 0;
  }

  public static class Builder implements Vector.Builder {

    public static final Builder INSTANCE = new Builder();


    private Builder() {

    }

    @Override
    public Builder setNA(int index) {
      return this;
    }

    @Override
    public Vector.Builder addNA() {
      return this;
    }

    @Override
    public Vector.Builder add(Vector from, int fromIndex) {
      return this;
    }

    @Override
    public Vector.Builder set(int atIndex, Vector from, int fromIndex) {
      return this;
    }

    @Override
    public Vector.Builder set(int index, Object value) {
      return this;
    }

    @Override
    public Vector.Builder add(Object value) {
      return this;
    }

    @Override
    public Vector.Builder addAll(Vector from) {
      return this;
    }

    @Override
    public Vector.Builder remove(int index) {
      return this;
    }

    @Override
    public void swap(int a, int b) {}

    @Override
    public Vector.Builder read(DataEntry entry) {
      throw new UnsupportedOperationException(ILLEGAL);
    }

    @Override
    public Vector.Builder read(int index, DataEntry entry) throws IOException {
      throw new UnsupportedOperationException();
    }

    @Override
    public int size() {
      return 0;
    }

    @Override
    public Vector build() {
      return Undefined.INSTANCE;
    }
  }
}
