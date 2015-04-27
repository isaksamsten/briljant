package org.briljantframework.complex;

/**
 * <p>
 * Similar to {@link java.lang.StringBuilder}, the
 * {@link org.briljantframework.complex.ComplexBuilder} performs
 * {@link org.briljantframework.complex.Complex} operations by mutating the receiver to avoid
 * creating unnecessary copies.
 * </p>
 * 
 * <pre>
 *     Complex sum = Complex.ZERO;
 *     List<Complex> complexes = ...;
 *     for(Complex c : complexes) {
 *         sum = sum.plus(c);
 *     }
 * </pre>
 * <p>
 * In the example above, {@code complexes.size()} garbage instances of temporary sums are created
 * and garbage collected, which negatively impact the performance.
 * </p>
 * 
 * <p>
 * Instead, the {@code ComplexBuilder} can be used as a drop-in replacement
 * </p>
 * 
 * <pre>
 *     ComplexBuilder accSum = new ComplexBuilder(0);
 *     List<Complex> complexes = ...;
 *     for(Complex c : complexes) {
 *         accSum = accSum.plus(c);
 *     }
 *     Complex sum = accSum.toComplex();
 * </pre>
 * 
 * <p>
 * {@link #plus(Complex)} above return the receiver mutated, hence {@code accSum.plus(c);} (without
 * reassignment) would be equally fine.
 * </p>
 *
 * @author Isak Karlsson
 */
public class ComplexBuilder {
  private double real;
  private double imag;

  private boolean isNaN;
  private boolean isInfinite;

  public ComplexBuilder(double real, double imag) {
    this.real = real;
    this.imag = imag;
    this.isNaN = Double.isNaN(real) || Double.isNaN(imag);
    this.isInfinite = !isNaN && (Double.isInfinite(real) || Double.isInfinite(imag));
  }

  public ComplexBuilder(double real) {
    this(real, 0);
  }

  public ComplexBuilder(Complex complex) {
    this(complex.real(), complex.imag());
  }

  public ComplexBuilder plus(Complex other) {
    if (isNaN) {
      return this;
    }
    // else if (other.isNaN()) {
    // setNaN();
    // }
    real += other.real();
    imag += other.imag();
    return this;
  }

  public ComplexBuilder minus(Complex other) {
    if (isNaN) {
      return this;
    }
    // else if (other.isNaN()) {
    // setNaN();
    // }
    real += other.real();
    imag += other.imag();
    return this;
  }

  public ComplexBuilder multiply(Complex other) {
    if (isNaN) {
      return this;
    }
    // } else if (other.isNaN()) {
    // setNaN();
    // }
    double oreal = other.real();
    double oimag = other.imag();
    real = real * oreal - imag * oimag;
    imag = real * oimag + imag * oreal;
    return this;
  }

  public ComplexBuilder div(Complex other) {
    if (isNaN) {
      return this;
    }
    // else if (other.isNaN()) {
    // setNaN();
    // }
    double oreal = other.real();
    double oimag = other.imag();
    if (Math.abs(oreal) < Math.abs(oimag)) {
      double q = oreal / oimag;
      double denominator = oreal * q + oimag;
      real = (real * q + imag) / denominator;
      imag = (imag * q - real) / denominator;
    } else {
      double q = oimag / oreal;
      double denominator = oimag * q + oreal;
      real = (imag * q + real) / denominator;
      imag = (imag - real * q) / denominator;
    }
    return this;
  }

  public double real() {
    return real;
  }

  public double imag() {
    return imag;
  }

  public Complex toComplex() {
    return Double.isNaN(real) || Double.isNaN(imag) ? Complex.NaN : Complex.valueOf(real, imag);
  }
}
