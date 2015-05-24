package org.briljantframework.vector;

import com.google.common.base.Preconditions;

import com.carrotsearch.hppc.DoubleArrayList;

import org.briljantframework.Bj;
import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.resolver.Resolver;
import org.briljantframework.io.resolver.Resolvers;
import org.briljantframework.matrix.ComplexMatrix;

import java.io.IOException;
import java.util.Arrays;

import static com.google.common.primitives.Ints.checkedCast;

/**
 * @author Isak Karlsson
 */
public class ComplexVector extends AbstractVector {

  public static final VectorType TYPE = new VectorType() {
    @Override
    public Builder newBuilder() {
      return new Builder();
    }

    @Override
    public Builder newBuilder(int size) {
      return new Builder(size);
    }

    @Override
    public Class<?> getDataClass() {
      return Complex.class;
    }

    @Override
    public boolean isNA(Object value) {
      return Is.NA(value);
    }

    @Override
    public int compare(int a, Vector va, int b, Vector ba) {
      throw new UnsupportedOperationException("Can't compare complex numbers");
    }

    @Override
    public Scale getScale() {
      return Scale.NUMERICAL;
    }

    @Override
    public String toString() {
      return "complex";
    }
  };
  public static final Complex NA = Complex.NaN;
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
    for (int i = 0; i < values.length; i += 2) {
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
   */
  public ComplexVector(double[] buffer, int size) {
    Preconditions.checkArgument(size * 2 <= buffer.length, "Un-even number of doubles.");
    this.values = Arrays.copyOf(buffer, size * 2);
    this.size = size;
  }

  public ComplexVector(ComplexMatrix freq) {
    this.values = new double[checkedCast(freq.size()) * 2];
    this.size = freq.size();
    for (int i = 0; i < freq.size(); i++) {
      Complex c = freq.get(i);
      this.values[i * 2] = c.real();
      this.values[i * 2 + 1] = c.imag();
    }
  }

  private ComplexVector(double[] values, int size, boolean ignore) {
    this.values = values;
    this.size = size;
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

  @Override
  public <T> T get(Class<T> cls, int index) {
    if (cls.isAssignableFrom(Complex.class)) {
      return cls.cast(getAsInt(index));
    } else {
      if (cls.isAssignableFrom(Double.class)) {
        return cls.cast(getAsDouble(index));
      } else if (cls.isAssignableFrom(Integer.class)) {
        return cls.cast(getAsInt(index));
      } else if (cls.isAssignableFrom(Bit.class)) {
        return cls.cast(getAsBit(index));
      } else if (cls.isAssignableFrom(String.class)) {
        return cls.cast(getAsComplex(index).toString());
      } else {
        return Na.of(cls);
      }
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString(int index) {
    Complex complex = getAsComplex(index);
    return complex.isNaN() ? "NA" : complex.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isNA(int index) {
    return Double.isNaN(getAsDouble(index));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getAsInt(int index) {
    double value = getAsDouble(index);
    if (Double.isNaN(value)) {
      return IntVector.NA;
    } else {
      return (int) value;
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Bit getAsBit(int index) {
    return Bit.valueOf(getAsInt(index));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VectorType getType() {
    return TYPE;
  }

  @Override
  public ComplexMatrix toMatrix() {
    ComplexMatrix x = Bj.complexVector(size());
    for (int i = 0; i < size(); i++) {
      x.set(i, getAsComplex(i));
    }
    return x;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compare(int a, int b) {
    throw new UnsupportedOperationException("Can't compare complex numbers.");
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int compare(int a, Vector other, int b) {
    throw new UnsupportedOperationException("Can't compare complex number.");
  }

  @Override
  public int hashCode() {
    int code = 1;
    for (int i = 0; i < size(); i++) {
      code += 31 * getAsComplex(i).hashCode();
    }
    return code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof Vector) {
      Vector ov = (Vector) o;
      if (size() == ov.size()) {
        for (int i = 0; i < size(); i++) {
          if (getAsComplex(i).equals(ov.getAsComplex(i))) {
            return false;
          }
        }
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
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
      } else if (value != null) {
        Resolver<Complex> resolver = Resolvers.find(Complex.class);
        if (resolver != null) {
          Complex obj = resolver.resolve(value);
          if (obj != null) {
            real = obj.real();
            imag = obj.imag();
          }
        }
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
      int i = a * 2;
      int j = b * 2;
      Utils.swap(buffer.buffer, i, j);
      Utils.swap(buffer.buffer, i + 1, j + 1);
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
    public Vector getTemporaryVector() {
      return new ComplexVector(buffer.buffer, buffer.size(), false) {
        @Override
        public Builder newCopyBuilder() {
          return ComplexVector.Builder.this;
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


}
