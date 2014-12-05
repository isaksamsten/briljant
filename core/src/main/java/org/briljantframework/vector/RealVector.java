package org.briljantframework.vector;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.briljantframework.Utils;
import org.briljantframework.io.DataFrameInputStream;

import com.carrotsearch.hppc.DoubleArrayList;
import com.google.common.base.Preconditions;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Created by Isak Karlsson on 20/11/14.
 */
public class RealVector extends AbstractRealVector {

  public static final double NA = Double.NaN;

  private final double[] values;

  public RealVector(double[] values, int size) {
    Preconditions.checkArgument(values.length > 0);
    this.values = Arrays.copyOf(values, size);
  }

  public RealVector(double... values) {
    this(true, values);
  }

  protected RealVector(boolean copy, double... values) {
    if (copy) {
      this.values = Arrays.copyOf(values, values.length);
    } else {
      this.values = values;
    }
  }

  /**
   * Construct a new {@code Vector} using the supplied values. Performs no copying.
   * 
   * @param values the values
   * @return the double vector
   */
  public static RealVector wrap(double... values) {
    return new RealVector(false, values);
  }

  public static Vector.Builder newBuilderWithInitialValues(double... values) {
    Builder builder = new Builder(0, values.length);
    for (double value : values) {
      builder.add(value);
    }
    return builder;
  }

  @Override
  public double getAsReal(int index) {
    return values[index];
  }

  @Override
  public int size() {
    return values.length;
  }

  @Override
  public RealVector.Builder newCopyBuilder() {
    return new RealVector.Builder(this);
  }

  @Override
  public RealVector.Builder newBuilder() {
    return new RealVector.Builder();
  }

  @Override
  public RealVector.Builder newBuilder(int size) {
    return new RealVector.Builder(size);
  }

  @Override
  public double[] toDoubleArray() {
    return values.clone();
  }

  public double[] asDoubleArray() {
    return values;
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
        return getAsReal(current++);
      }
    };
  }

  @Override
  public String toString() {
    return IntStream.range(0, size()).mapToObj(this::toString).collect(Collectors.joining(","));
  }

  public static class Builder implements Vector.Builder {

    private DoubleArrayList buffer;

    public Builder() {
      this(0);
    }

    public Builder(int size) {
      this(size, Math.max(size, INITIAL_CAPACITY));
    }

    public Builder(int size, int capacity) {
      this.buffer = new DoubleArrayList(Math.max(size, capacity));
      for (int i = 0; i < size; i++) {
        buffer.add(NA);
      }
    }

    public Builder(RealVector vector) {
      this.buffer = DoubleArrayList.from(vector.asDoubleArray());
    }

    @Override
    public Builder setNA(int index) {
      ensureCapacity(index);
      buffer.buffer[index] = RealVector.NA;
      return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Builder addNA() {
      setNA(size());
      return this;
    }

    @Override
    public Builder add(Vector from, int fromIndex) {
      set(size(), from, fromIndex);
      return this;
    }

    @Override
    public Builder set(int atIndex, Vector from, int fromIndex) {
      ensureCapacity(atIndex);
      buffer.buffer[atIndex] = from.getAsReal(fromIndex);
      return this;
    }

    @Override
    public Builder set(int index, Object value) {
      ensureCapacity(index);
      double dval = RealVector.NA;
      if (value instanceof Number) {
        dval = ((Number) value).doubleValue();
      } else if (value instanceof Complex) {
        dval = ((Complex) value).getReal();
      }
      buffer.buffer[index] = dval;
      return this;
    }

    @Override
    public Builder add(Object value) {
      set(size(), value);
      return this;
    }

    @Override
    public Builder addAll(Vector from) {
      for (int i = 0; i < from.size(); i++) {
        add(from.getAsReal(i));
      }
      return this;
    }

    @Override
    public Vector.Builder swap(int a, int b) {
      Preconditions.checkArgument(a >= 0 && a < size() && b >= 0 && b < size());
      Utils.swap(buffer.buffer, a, b);
      return this;
    }

    @Override
    public Vector.Builder read(DataFrameInputStream inputStream) throws IOException {
      double value = inputStream.nextDouble();
      add(value);
      return this;
    }

    @Override
    public int size() {
      return buffer.size();
    }

    @Override
    public RealVector build() {
      RealVector vec = new RealVector(buffer.buffer, size());
      buffer = null;
      return vec;
    }

    public Builder add(double value) {
      return set(size(), value);
    }

    public Builder set(int index, double value) {
      ensureCapacity(index);
      buffer.buffer[index] = value;
      return this;
    }

    private void ensureCapacity(int index) {
      buffer.ensureCapacity(index + 1);
      int i = buffer.size();
      while (i <= index) {
        buffer.buffer[i++] = NA;
        buffer.elementsCount++;
      }
    }

  }


}
