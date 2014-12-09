package org.briljantframework.vector;

import java.util.Iterator;

import com.google.common.collect.UnmodifiableIterator;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public abstract class AbstractDoubleVector implements Vector, Iterable<Double> {
  public static final Type TYPE = new Type() {
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
      return Double.TYPE;
    }

    @Override
    public boolean isNA(Object value) {
      return value == null || (value instanceof Double && Double.isNaN((Double) value));
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
  public int getAsInt(int index) {
    double value = getAsDouble(index);
    return Double.isNaN(value) ? IntVector.NA : (int) value;
  }

  @Override
  public Binary getAsBinary(int index) {
    return Binary.valueOf(getAsInt(index));
  }

  @Override
  public String getAsString(int index) {
    double value = getAsDouble(index);
    return Double.isNaN(value) ? StringVector.NA : String.valueOf(value);
  }

  @Override
  public Value getAsValue(int index) {
    double value = getAsDouble(index);
    return Is.NA(value) ? Undefined.INSTANCE : new DoubleValue(value);
  }

  @Override
  public String toString(int index) {
    String value = getAsString(index);
    return value == StringVector.NA ? "NA" : value;
  }

  @Override
  public boolean isNA(int index) {
    return Double.isNaN(getAsDouble(index));
  }

  @Override
  public Type getType() {
    return TYPE;
  }

  @Override
  public int compare(int a, int b) {
    double va = getAsDouble(a);
    double vb = getAsDouble(b);
    return !Double.isNaN(va) && !Double.isNaN(vb) ? Double.compare(va, vb) : 0;
  }

  @Override
  public int compare(int a, int b, Vector other) {
    double va = getAsDouble(a);
    double vb = other.getAsDouble(b);
    return !Double.isNaN(va) && !Double.isNaN(vb) ? Double.compare(va, vb) : 0;
  }

  @Override
  public Iterator<Double> iterator() {
    return new UnmodifiableIterator<Double>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public Double next() {
        return getAsDouble(current++);
      }
    };
  }
}
