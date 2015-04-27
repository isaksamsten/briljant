package org.briljantframework.vector;

import com.google.common.base.Preconditions;

import org.briljantframework.complex.Complex;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public class ComplexValue extends AbstractComplexVector implements Value {

  private final Complex complex;

  public ComplexValue(Complex complex) {
    this.complex = Preconditions.checkNotNull(complex);
  }

  public static Value valueOf(Complex complex) {
    return complex == NA ? Undefined.INSTANCE : new ComplexValue(complex);
  }

  @Override
  public int compareTo(Value o) {
    return isNA() && o.isNA() ? 0 : Double.compare(getAsDouble(), o.getAsDouble());
  }

  @Override
  public double getAsDouble(int index) {
    return complex.real();
  }

  @Override
  public Complex getAsComplex(int index) {
    return complex;
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public int hashCode() {
    return complex != null ? complex.hashCode() : 0;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    ComplexValue complexes = (ComplexValue) o;

    return !(complex != null ? !complex.equals(complexes.complex) : complexes.complex != null);
  }

  @Override
  public Builder newCopyBuilder() {
    return getType().newBuilder().add(complex);
  }

  @Override
  public Builder newBuilder() {
    return getType().newBuilder();
  }

  @Override
  public Builder newBuilder(int size) {
    return getType().newBuilder(size);
  }

  @Override
  public String toString() {
    return toString(0);
  }
}
