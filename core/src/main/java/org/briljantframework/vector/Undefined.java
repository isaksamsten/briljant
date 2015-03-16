package org.briljantframework.vector;

import org.briljantframework.io.DataEntry;
import org.briljantframework.matrix.Matrix;

import java.io.IOException;

/**
 * Undefined is an immutable 1 size vector returning NA
 * <p>
 * Created by Isak Karlsson on 26/11/14.
 */
public class Undefined implements Value {

  public static final Undefined INSTANCE = new Undefined();

  protected static final String UNDEFINED_DOES_NOT_HAVE_A_SCALE =
      "Undefined does not have a scale.";

  public static final VectorType TYPE = new VectorType() {
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
  public Value get(int index) {
    return this;
  }

  @Override
  public <T> T get(Class<T> cls, int index) {
    return Vectors.naValue(cls);
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
  public Bit getAsBit(int index) {
    return BitVector.NA;
  }

  @Override
  public String getAsString(int index) {
    return StringVector.NA;
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public VectorType getType() {
    return TYPE;
  }

  @Override
  public VectorType getType(int index) {
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
  public Matrix asMatrix() {
    throw new UnsupportedOperationException();
  }

  @Override
  public int compare(int a, int b) {
    return 0;
  }

  @Override
  public int compare(int a, Vector other, int b) {
    return 0;
  }

  @Override
  public int compareTo(Value o) {
    return 0;
  }

  @Override
  public String toString() {
    return "NA";
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
    public int compare(int a, int b) {
      return 0;
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
    public Vector getTemporaryVector() {
      return Undefined.INSTANCE;
    }

    @Override
    public Vector build() {
      return Undefined.INSTANCE;
    }
  }
}
