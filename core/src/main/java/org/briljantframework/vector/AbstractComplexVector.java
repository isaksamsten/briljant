package org.briljantframework.vector;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.DefaultComplexMatrix;
import org.briljantframework.matrix.storage.VectorStorage;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractComplexVector extends AbstractVector {
  public static final VectorType TYPE = new VectorType() {
    @Override
    public ComplexVector.Builder newBuilder() {
      return new ComplexVector.Builder();
    }

    @Override
    public ComplexVector.Builder newBuilder(int size) {
      return new ComplexVector.Builder(size);
    }

    @Override
    public Class<?> getDataClass() {
      return Complex.class;
    }

    @Override
    public boolean isNA(Object value) {
      return value == null || (value instanceof Complex && ((Complex) value).isNaN());
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

  public Complex get(int index) {
    return getAsComplex(index);
  }

  @Override
  public Value getAsValue(int index) {
    Complex complex = getAsComplex(index);
    return complex.isNaN() ? Undefined.INSTANCE : new ComplexValue(complex);
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
  public String getAsString(int index) {
    Complex complex = getAsComplex(index);
    if (complex.isNaN()) {
      return StringVector.NA;
    } else {
      return complex.toString();
    }
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VectorType getType() {
    return TYPE;
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

  @Override
  public int hashCode() {
    int code = 1;
    for (int i = 0; i < size(); i++) {
      code += 31 * getAsComplex(i).hashCode();
    }
    return code;
  }

  @Override
  public ComplexMatrix asMatrix() {
    return new DefaultComplexMatrix(new VectorStorage(this));
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
  public int compare(int a, int b, Vector other) {
    throw new UnsupportedOperationException("Can't compare complex number.");
  }
}
