package org.briljantframework.vector;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.io.DataEntry;

import com.carrotsearch.hppc.DoubleArrayList;
import com.google.common.base.Preconditions;

/**
 * Created by Isak Karlsson on 20/11/14.
 */
public class DoubleVector extends AbstractDoubleVector {

  public static final double NA = Double.longBitsToDouble(0x7ff0000000000009L); // Double.NaN;

  public static final long NA_MASK = 0x000000000000000FL;
  public static final int NA_RES = 9;

  private final double[] values;

  public DoubleVector(double[] values, int size) {
    Preconditions.checkArgument(values.length > 0);
    this.values = Arrays.copyOf(values, size);
  }

  public DoubleVector(double... values) {
    this(true, values);
  }

  protected DoubleVector(boolean copy, double... values) {
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
  public static DoubleVector wrap(double... values) {
    return new DoubleVector(false, values);
  }

  public static Vector.Builder newBuilderWithInitialValues(double... values) {
    Builder builder = new Builder(0, values.length);
    for (double value : values) {
      builder.add(value);
    }
    return builder;
  }

  public static Collector<Double, ?, Builder> collector() {
    return Collector.of(Builder::new, Builder::add, (builder, builder2) -> {
      builder.addAll(builder2);
      return builder;
    });
  }

  @Override
  public double getAsDouble(int index) {
    return values[index];
  }

  @Override
  public int size() {
    return values.length;
  }

  @Override
  public DoubleVector.Builder newCopyBuilder() {
    return new DoubleVector.Builder(this);
  }

  @Override
  public DoubleVector.Builder newBuilder() {
    return new DoubleVector.Builder();
  }

  @Override
  public DoubleVector.Builder newBuilder(int size) {
    return new DoubleVector.Builder(size);
  }

  @Override
  public double[] toDoubleArray() {
    return values.clone();
  }

  public double[] asDoubleArray() {
    return values;
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

    public Builder(DoubleVector vector) {
      this.buffer = DoubleArrayList.from(vector.asDoubleArray());
    }

    @Override
    public Builder setNA(int index) {
      ensureCapacity(index);
      buffer.buffer[index] = DoubleVector.NA;
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
      buffer.buffer[atIndex] = from.getAsDouble(fromIndex);
      return this;
    }

    @Override
    public Builder set(int index, Object value) {
      ensureCapacity(index);
      double dval = DoubleVector.NA;
      if (value instanceof Number) {
        dval = ((Number) value).doubleValue();
      } else if (value instanceof Complex) {
        dval = ((Complex) value).real();
      } else if (value instanceof Value) {
        dval = ((Value) value).getAsDouble();
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
        add(from.getAsDouble(i));
      }
      return this;
    }

    @Override
    public Vector.Builder remove(int index) {
      buffer.remove(index);
      return this;
    }

    @Override
    public int compare(int a, int b) {
      return Double.compare(buffer.get(a), buffer.get(b));
    }

    @Override
    public void swap(int a, int b) {
      Preconditions.checkArgument(a >= 0 && a < size() && b >= 0 && b < size());
      Utils.swap(buffer.buffer, a, b);
    }

    @Override
    public Vector.Builder read(DataEntry entry) throws IOException {
      return read(size(), entry);
    }

    @Override
    public Vector.Builder read(int index, DataEntry entry) throws IOException {
      double value = entry.nextDouble();
      set(index, value);
      return this;
    }

    @Override
    public int size() {
      return buffer.size();
    }

    @Override
    public VectorLike getVectorView() {
      return null;
    }

    @Override
    public DoubleVector build() {
      DoubleVector vec = new DoubleVector(buffer.buffer, size());
      return vec;
    }

    public void addAll(Builder builder) {
      for (int i = 0; i < builder.buffer.size(); i++) {
        add(builder.buffer.get(i));
      }
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
