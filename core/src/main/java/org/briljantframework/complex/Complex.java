package org.briljantframework.complex;

import java.io.Serializable;

import com.google.common.base.Preconditions;

/**
 * Implementing complex
 *
 * @author Isak Karlsson
 */
public class Complex implements Serializable {

  public static final Complex I = new Complex(0, 1);

  public static final Complex POSITIVE_INFINITY = new Complex(Double.POSITIVE_INFINITY,
      Double.POSITIVE_INFINITY);

  public static final Complex NEGATIVE_INFINITY = new Complex(Double.NEGATIVE_INFINITY,
      Double.NEGATIVE_INFINITY);

  public static final Complex ONE = new Complex(1, 0);

  public static final Complex ZERO = new Complex(0, 0);

  public static final Complex NaN = new Complex(Double.NaN, Double.NaN);

  public static final Complex NEG_I = I.negate();
  protected static final double LOG_10 = Math.log(10);
  protected static final double LOG_2 = Math.log(2);


  private final double real, imag;
  private final boolean isNaN;
  private final boolean isInfinite;

  public Complex(double real, double imag) {
    this.real = real;
    this.imag = imag;
    this.isNaN = Double.isNaN(real) || Double.isNaN(imag);
    this.isInfinite = !isNaN && (Double.isInfinite(real) || Double.isInfinite(imag));
  }

  public Complex(double real) {
    this(real, 0);
  }

  public static Complex valueOf(double real) {
    if (Double.isNaN(real)) {
      return NaN;
    }
    return new Complex(real);
  }

  public static Complex valueOf(double real, double imag) {
    if (Double.isNaN(real) || Double.isNaN(imag)) {
      return NaN;
    }
    return new Complex(real, imag);
  }

  public static Complex sqrt(double real) {
    return new Complex(real).sqrt();
  }

  public static Complex log(double real) {
    return new Complex(real).log();
  }

  public static Complex log2(double real) {
    return new Complex(real).log2();
  }

  public static Complex log10(double real) {
    return new Complex(real).log10();
  }

  public Complex plus(Complex other) {
    if (isNaN() || other.isNaN()) {
      return NaN;
    }
    return new Complex(real + other.real, imag + other.imag);
  }

  public Complex plus(double real) {
    return isNaN() || Double.isNaN(real) ? NaN : new Complex(this.real + real, imag);
  }

  public Complex minus(Complex other) {
    if (isNaN() || other.isNaN()) {
      return NaN;
    }
    return new Complex(real - other.real, imag - other.imag);
  }

  public Complex minus(double real) {
    return isNaN() || Double.isNaN(real) ? NaN : new Complex(this.real - real, imag);
  }

  public Complex multiply(Complex other) {
    if (isNaN() || other.isNaN()) {
      return NaN;
    }
    return new Complex(real * other.real - imag * other.imag, real * other.imag + imag * other.real);
  }

  public Complex multiply(double other) {
    if (isNaN() || Double.isNaN(other)) {
      return NaN;
    }

    if (isInfinite() || Double.isInfinite(other)) {
      return POSITIVE_INFINITY;
    }

    return new Complex(real * other, imag * other);
  }

  public Complex div(Complex other) {
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

  public Complex div(double other) {
    if (isNaN() || Double.isNaN(other)) {
      return NaN;
    }

    if (other == 0) {
      return NaN;
    }

    if (Double.isInfinite(other)) {
      return !isInfinite() ? ZERO : NaN;
    }
    return new Complex(real / other, imag / other);
  }

  public Complex reciprocal() {
    if (isNaN()) {
      return NaN;
    }

    if (isZero()) {
      return POSITIVE_INFINITY;
    }

    if (isInfinite()) {
      return ZERO;
    }

    if (Math.abs(real) < Math.abs(imag)) {
      double q = real / imag;
      double scale = 1 / (real * q + real);
      return new Complex(scale * q, -scale);
    } else {
      double q = imag / real;
      double scale = 1 / (imag * q + real);
      return new Complex(scale, -scale * q);
    }
  }

  public Complex conjugate() {
    if (isNaN()) {
      return NaN;
    }
    return new Complex(real, -imag);
  }



  // sqrt(1-this<sup>2</sup>)
  private Complex sqrt1z() {
    return ONE.minus(multiply(this)).sqrt();
  }

  public Complex sqrt() {
    if (isNaN()) {
      return NaN;
    }

    if (isZero()) {
      return ZERO;
    }

    double t = Math.sqrt((Math.abs(real) + abs()) / 2.0);
    if (real >= 0) {
      return new Complex(t, imag / (2 * t));
    } else {
      return new Complex(Math.abs(imag) / (2 * t), Math.copySign(1, imag) * t);
    }
  }

  public Complex cos() {
    if (isNaN()) {
      return NaN;
    }

    return new Complex(Math.cos(real) * Math.cosh(imag), -Math.sin(real) * Math.sinh(imag));
  }

  public Complex acos() {
    if (isNaN()) {
      return NaN;
    }

    return plus(sqrt1z()).multiply(I).log().multiply(NEG_I);
  }

  public Complex cosh() {
    if (isNaN()) {
      return NaN;
    }

    return new Complex(Math.cosh(real) * Math.cos(imag), Math.sinh(real) * Math.sin(imag));
  }

  public Complex tan() {
    if (isNaN() || Double.isInfinite(real)) {
      return NaN;
    }

    if (imag > 20.0) {
      return I;
    }

    if (imag < -20.0) {
      return I.conjugate();
    }

    double r = 2.0 * real;
    double i = 2.0 * imag;
    double d = Math.cos(r) + Math.cosh(i);

    return new Complex(Math.sin(r) / d, Math.sinh(i) / d);
  }

  public Complex atan() {
    if (isNaN()) {
      return NaN;
    }

    return plus(I).div(I.minus(this)).log().multiply(I.div(Complex.valueOf(2)));
  }

  public Complex tanh() {
    if (isNaN || Double.isInfinite(imag)) {
      return NaN;
    }
    if (real > 20.0) {
      return Complex.valueOf(1.0, 0.0);
    }
    if (real < -20.0) {
      return Complex.valueOf(-1.0, 0.0);
    }

    double r = 2 * real;
    double i = 2 * imag;
    double d = Math.cosh(r) * Math.cos(i);
    return new Complex(Math.sinh(r) / d, Math.sin(i) / d);
  }

  public Complex sin() {
    if (isNaN()) {
      return NaN;
    }

    return new Complex(Math.sin(real) * Math.cosh(imag), Math.cos(real) * Math.sinh(imag));
  }

  public Complex sinh() {
    if (isNaN()) {
      return NaN;
    }

    return new Complex(Math.sinh(real) * Math.cos(imag), Math.cosh(real) * Math.sin(imag));
  }

  public Complex asin() {
    if (isNaN()) {
      return NaN;
    }

    return sqrt1z().plus(multiply(I)).log().multiply(NEG_I);
  }

  public Complex log() {
    if (isNaN()) {
      return NaN;
    }

    return new Complex(Math.log(abs()), Math.atan2(imag, real));
  }

  public Complex log2() {
    if (isNaN()) {
      return NaN;
    }
    return log().div(LOG_2);
  }

  public Complex log10() {
    if (isNaN()) {
      return NaN;
    }
    return log().div(LOG_10);
  }

  public double abs() {
    if (isNaN()) {
      return Double.NaN;
    }

    if (isInfinite()) {
      return Double.POSITIVE_INFINITY;
    }

    double realAbs = Math.abs(real);
    double imagAbs = Math.abs(imag);
    if (realAbs < imagAbs) {
      if (imag == 0) {
        return realAbs;
      }
      double x = real / imag;
      return imagAbs * Math.sqrt(1 + x * x);
    } else {
      if (real == 0) {
        return imagAbs;
      }
      double x = imag / real;
      return realAbs * Math.sqrt(1 + x * x);
    }
  }

  public Complex pow(Complex power) {
    Preconditions.checkNotNull(power);
    if (isNaN()) {
      return NaN;
    }

    return log().multiply(power).exp();
  }

  public Complex pow(double power) {
    return log().multiply(power).exp();
  }

  public Complex exp() {
    if (isNaN()) {
      return NaN;
    }
    double e = Math.exp(real);
    return new Complex(e * Math.cos(imag), e * Math.sin(imag));
  }

  public Complex negate() {
    if (isNaN()) {
      return NaN;
    }
    return new Complex(-real, -imag);
  }

  public boolean isZero() {
    return real == 0 && imag == 0;
  }

  public boolean isNaN() {
    return isNaN;
  }

  public boolean isInfinite() {
    return isInfinite;
  }

  public double imag() {
    return imag;
  }

  public double real() {
    return real;
  }

  @Override
  public int hashCode() {
    int result;
    long temp;
    temp = Double.doubleToLongBits(real);
    result = (int) (temp ^ (temp >>> 32));
    temp = Double.doubleToLongBits(imag);
    result = 31 * result + (int) (temp ^ (temp >>> 32));
    return result;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    Complex complex = (Complex) o;
    if (Double.compare(complex.imag, imag) != 0)
      return false;
    if (Double.compare(complex.real, real) != 0)
      return false;

    return true;
  }

  @Override
  public String toString() {
    if (imag >= 0) {
      return real + " + " + imag + "i";
    } else {
      return real + " - " + (-imag) + "i";
    }
  }
}
