package org.briljantframework.vector;

import java.util.Arrays;
import java.util.Iterator;

import org.briljantframework.matrix.DefaultDoubleMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.storage.VectorStorage;

import com.google.common.collect.UnmodifiableIterator;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public abstract class AbstractDoubleVector extends AbstractVector implements Iterable<Double> {
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
      return Double.TYPE;
    }

    @Override
    public boolean isNA(Object value) {
      return value == null || (value instanceof Double && Is.NA((Double) value));
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

  private DoubleMatrix adapter;

  public double get(int index) {
    return getAsDouble(index);
  }

  @Override
  public Value getAsValue(int index) {
    double value = getAsDouble(index);
    return Is.NA(value) ? Undefined.INSTANCE : new DoubleValue(value);
  }

  @Override
  public String toString(int index) {
    double value = getAsDouble(index);
    return Is.NA(value) ? "NA" : String.format("%.3f", value);
  }

  @Override
  public boolean isNA(int index) {
    return Is.NA(getAsDouble(index));
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
  public String getAsString(int index) {
    double value = getAsDouble(index);
    return Is.NA(value) ? StringVector.NA : String.valueOf(value);
  }

  @Override
  public VectorType getType() {
    return TYPE;
  }

  @Override
  public DoubleMatrix asMatrix() {
    if (adapter == null) {
      adapter = new DefaultDoubleMatrix(new VectorStorage(this));
    }
    return adapter;
  }

  @Override
  public int compare(int a, int b) {
    double va = getAsDouble(a);
    double vb = getAsDouble(b);
    return !Is.NA(va) && !Is.NA(vb) ? Double.compare(va, vb) : 0;
  }

  @Override
  public int compare(int a, int b, Vector other) {
    double va = getAsDouble(a);
    double vb = other.getAsDouble(b);
    return !Is.NA(va) && !Is.NA(vb) ? Double.compare(va, vb) : 0;
  }

  @Override
  public int hashCode() {
    return 31 * size() + Arrays.hashCode(asDoubleArray());
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) {
      return true;
    }
    if (obj instanceof Vector) {
      Vector other = (Vector) obj;
      if (other.size() != this.size()) {
        return false;
      }
      for (int i = 0; i < size(); i++) {
        if (getAsDouble(i) != other.getAsDouble(i)) {
          return false;
        }
      }
      return true;
    }
    return false;
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
