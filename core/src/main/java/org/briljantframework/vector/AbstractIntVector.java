package org.briljantframework.vector;

import java.util.Iterator;

import org.briljantframework.matrix.DefaultIntMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.storage.VectorStorage;

import com.google.common.collect.UnmodifiableIterator;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public abstract class AbstractIntVector extends AbstractVector implements Iterable<Integer> {
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
      return value == null || (value instanceof Integer && (int) value == NA);
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

  private IntMatrix adapter;

  public int get(int index) {
    return getAsInt(index);
  }

  @Override
  public Value getAsValue(int index) {
    int value = getAsInt(index);
    return Is.NA(value) ? Undefined.INSTANCE : new IntValue(value);
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
    if (adapter == null) {
      adapter = new DefaultIntMatrix(new VectorStorage(this));
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

  @Override
  public Iterator<Integer> iterator() {
    return new UnmodifiableIterator<Integer>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public Integer next() {
        return getAsInt(current++);
      }
    };
  }
}
