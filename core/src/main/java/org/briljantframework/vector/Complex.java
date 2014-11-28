package org.briljantframework.vector;

/**
 * Created by Isak Karlsson on 21/11/14.
 */
public class Complex {

  public static final Complex I = new Complex(0, 1);
  public static final Complex POSITIVE_INFINITY = new Complex(Double.POSITIVE_INFINITY,
      Double.POSITIVE_INFINITY);
  public static final Complex NEGATIVE_INFINITY = new Complex(Double.NEGATIVE_INFINITY,
      Double.NEGATIVE_INFINITY);
  public static final Complex ONE = new Complex(1, 0);
  public static final Complex ZERO = new Complex(0, 0);
  public static final Complex NaN = new Complex(Double.NaN, Double.NaN);

  private final double real, imag;
  private final boolean isNaN;
  private final boolean isInfinite;

  public Complex(double real, double imag) {
    this.real = real;
    this.imag = imag;
    this.isNaN = Double.isNaN(real) || Double.isNaN(imag);
    this.isInfinite = !isNaN && (Double.isInfinite(real) || Double.isInfinite(imag));
  }

  public Complex plus(Complex other) {
    if (isNaN() || other.isNaN()) {
      return NaN;
    }
    return new Complex(real + other.real, imag + other.imag);
  }

  public Complex minus(Complex other) {
    if (isNaN() || other.isNaN()) {
      return NaN;
    }
    return new Complex(real - other.real, imag - other.imag);
  }

  public Complex multiply(Complex other) {
    if (isNaN() || other.isNaN()) {
      return NaN;
    }
    return new Complex(real * other.real - imag * other.imag, real * other.imag + imag * other.real);
  }

  public Complex divide(Complex other) {
    if (isNaN() || other.isNaN()) {
      return NaN;
    }

    if (other.real == 0 && other.imag == 0) {
      return ZERO;
    }

    if (Math.abs(other.real) < Math.abs(other.imag)) {
      double q = other.real / other.imag;
      double denominator = other.real * q + other.imag;
      return new Complex((real * q + imag) / denominator, (imag * q - real) / denominator);
    } else {
      double q = other.imag / other.real;
      double denominator = other.imag * q + other.real;
      return new Complex((imag * q + real) / denominator, (imag - real * q) / denominator);
    }
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (other instanceof Complex) {
      Complex c = (Complex) other;
      if (c.isNaN) {
        return isNaN;
      } else {
        return real == c.real && imag == c.imag;
      }
    }
    return false;
  }

  @Override
  public String toString() {
    if (imag >= 0) {
      return real + " + " + imag + "i";
    } else {
      return real + " - " + (-imag) + "i";
    }
  }

  public boolean isNaN() {
    return isNaN;
  }

  public boolean isInfinite() {
    return isInfinite;
  }

  public double getImag() {
    return imag;
  }

  public double getReal() {
    return real;
  }
}
