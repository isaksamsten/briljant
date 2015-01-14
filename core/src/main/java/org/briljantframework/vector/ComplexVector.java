package org.briljantframework.vector;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.io.DataEntry;
import org.briljantframework.matrix.ComplexMatrix;

import com.carrotsearch.hppc.DoubleArrayList;
import com.google.common.base.Preconditions;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Created by Isak Karlsson on 21/11/14.
 */
public class ComplexVector extends AbstractComplexVector {

  private final double[] values;
  private final int size;

  /**
   * Constructs a complex vector from a double array.
   *
   * @param values buffer of double values interpreted as complex numbers
   */
  public ComplexVector(double... values) {
    Preconditions.checkArgument(values.length % 2 == 0);
    this.values = Arrays.copyOf(values, values.length);
    this.size = values.length / 2;
  }

  public ComplexVector(Complex... values) {
    Preconditions.checkArgument(values.length > 0);
    this.values = new double[values.length * 2];
    this.size = values.length;
    for (int i = 0; i < values.length; i++) {
      Complex c = values[i];
      this.values[i] = c.real();
      this.values[i + 1] = c.imag();
    }
  }

  /**
   * Constructs a new complex vectors. The double buffer is interpreted as complex numbers where
   * position {@code i} is the real part and {@code i + 1} is the imaginary part. Size must be
   * unequal (i.e. {@code size % 2 == 1}) and less than or equal to {@code buffer.length / 2}.
   *
   * @param buffer buffer of double values interpreted as complex numbers
   * @param size
   */
  public ComplexVector(double[] buffer, int size) {
    Preconditions.checkArgument(size * 2 <= buffer.length, "Un-even number of doubles.");
    this.values = Arrays.copyOf(buffer, size * 2);
    this.size = size;
  }

  public ComplexVector(ComplexMatrix freq) {
    this.values = new double[freq.size() * 2];
    this.size = freq.size();
    for (int i = 0; i < freq.size(); i++) {
      Complex c = freq.get(i);
      this.values[i * 2] = c.real();
      this.values[i * 2 + 1] = c.imag();
    }
  }

  public static Vector.Builder newBuilderWithInitialValues(Object... values) {
    Builder builder = new Builder(0, values.length);
    builder.addAll(Arrays.asList(values));
    return builder;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getAsDouble(int index) {
    return values[index * 2];
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Complex getAsComplex(int index) {
    int pos = index * 2;
    double real = values[pos], imag = values[pos + 1];
    if (Double.isNaN(real) || Double.isNaN(imag)) {
      return Complex.NaN;
    } else {
      return new Complex(real, imag);
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int size() {
    return size;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Builder newCopyBuilder() {
    return new Builder(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Builder newBuilder() {
    return new Builder();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Builder newBuilder(int size) {
    return new Builder(size);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double[] toDoubleArray() {
    return values.clone();
  }

  /**
   * Returns the underlying array which represents complex numbers as two consecutive positions in
   * the array.
   * <p>
   * The number of complex numbers are {@code array.length / 2}. This array can be used in suitable
   * BLAS operations.
   *
   * @return the underlying array
   */
  @Override
  public double[] asDoubleArray() {
    return values;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    System.out.println(size);
    return IntStream.range(0, size()).mapToObj(this::toString).collect(Collectors.joining(","));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<Complex> iterator() {
    return new UnmodifiableIterator<Complex>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public Complex next() {
        return getAsComplex(current++);
      }
    };
  }

  public static final class Builder implements Vector.Builder {

    private DoubleArrayList buffer;

    public Builder() {
      this(0);
    }

    public Builder(int size) {
      this(size, Math.max(size, INITIAL_CAPACITY));
    }

    public Builder(int size, int capacity) {
      buffer = new DoubleArrayList(capacity * 2);
      for (int i = 0; i < size * 2; i++) {
        int pos = i * 2;
        buffer.buffer[pos] = DoubleVector.NA;
        buffer.buffer[pos + 1] = DoubleVector.NA;
      }
    }

    public Builder(ComplexVector copy) {
      this.buffer = DoubleArrayList.from(copy.toDoubleArray());
    }


    @Override
    public Builder setNA(int index) {
      int pos = index * 2;
      ensureCapacity(pos);
      buffer.buffer[pos] = DoubleVector.NA;
      buffer.buffer[pos + 1] = DoubleVector.NA;
      return this;
    }

    @Override
    public Builder addNA() {
      return setNA(size());
    }

    @Override
    public Builder add(Vector from, int fromIndex) {
      return set(size(), from, fromIndex);
    }

    @Override
    public Builder set(int atIndex, Vector from, int fromIndex) {
      int pos = atIndex * 2;
      ensureCapacity(pos);
      Complex complex = from.getAsComplex(fromIndex);

      buffer.buffer[pos] = complex.real();
      buffer.buffer[pos + 1] = complex.imag();
      return this;
    }

    @Override
    public Builder set(int index, Object value) {
      int pos = index * 2;
      ensureCapacity(pos);
      double real = Double.NaN, imag = Double.NaN;
      if (value instanceof Complex) {
        real = ((Complex) value).real();
        imag = ((Complex) value).imag();
      } else if (value instanceof Number) {
        real = ((Number) value).doubleValue();
        imag = 0;
      }
      buffer.buffer[pos] = real;
      buffer.buffer[pos + 1] = imag;
      return this;
    }

    @Override
    public Builder add(Object value) {
      return set(size(), value);
    }

    @Override
    public Builder addAll(Vector from) {
      for (int i = 0; i < from.size(); i++) {
        add(from.getAsComplex(i));
      }
      return this;
    }

    @Override
    public Vector.Builder remove(int index) {
      buffer.remove(index); // First remove index and buffer is shifted once to the left
      buffer.remove(index); // remove index + 1
      return this;
    }

    @Override
    public int compare(int a, int b) {
      throw new UnsupportedOperationException("Can't compare complex numbers");
    }

    @Override
    public void swap(int a, int b) {
      Preconditions.checkArgument(a >= 0 && a + 1 < size() && b >= 0 && b + 1 < size());
      Utils.swap(buffer.buffer, a * 2, b * 2);
      Utils.swap(buffer.buffer, a * 2 + 1, b * 2 + 1);
    }

    @Override
    public Vector.Builder read(DataEntry entry) throws IOException {
      return read(size(), entry);
    }

    @Override
    public Vector.Builder read(int index, DataEntry entry) throws IOException {
      Complex complex = entry.nextComplex();
      if (complex == null) {
        setNA(index);
      } else {
        set(index, complex);
      }
      return this;
    }

    @Override
    public int size() {
      return buffer.size() / 2;
    }

    @Override
    public VectorLike getVectorView() {
      return new VectorLike() {

        @Override
        public double getAsDouble(int index) {
          return buffer.get(index);
        }

        @Override
        public Complex getAsComplex(int index) {
          int pos = index * 2;
          double real = buffer.get(pos), imag = buffer.get(pos + 1);
          if (Double.isNaN(real) || Double.isNaN(imag)) {
            return Complex.NaN;
          } else {
            return new Complex(real, imag);
          }
        }

        @Override
        public int size() {
          return size();
        }
      };
    }

    @Override
    public ComplexVector build() {
      return new ComplexVector(buffer.buffer, size());
    }

    private void ensureCapacity(int index) {
      buffer.ensureCapacity(index + 2);
      int i = buffer.size();
      while (i <= index + 1) {
        buffer.buffer[i++] = DoubleVector.NA;
        buffer.elementsCount++;
      }
    }
  }


}
