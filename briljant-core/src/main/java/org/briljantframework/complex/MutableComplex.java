/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.complex;

import org.apache.commons.math3.complex.Complex;

/**
 * <p>
 * Similar to {@link java.lang.StringBuilder}, the
 * {@link MutableComplex} performs
 * {@link org.apache.commons.math3.complex.Complex} operations by mutating the receiver to avoid
 * creating unnecessary copies.
 * </p>
 *
 * <pre>{@code
 * Complex sum = Complex.ZERO;
 * List<Complex> complexes = ...;
 * for(Complex c : complexes) {
 *   sum = sum.plus(c);
 * }}</pre>
 * <p>
 * In the example above, {@code complexes.size()} garbage instances of temporary sums are created
 * and garbage collected, which negatively impact the performance.
 * </p>
 *
 * <p>
 * Instead, the {@code MutableComplex} can be used as a drop-in replacement
 * </p>
 *
 * <pre>{@code
 * MutableComplex accSum = new MutableComplex(0);
 * List<Complex> complexes = ...;
 * for(Complex c : complexes) {
 *   accSum = accSum.plus(c);
 * }
 * Complex sum = accSum.toComplex();}</pre>
 *
 * <p>
 * {@link #plus(Complex)} above return the receiver mutated, hence {@code accSum.plus(c);} (without
 * reassignment) would be equally fine.
 * </p>
 *
 * @author Isak Karlsson
 */
public class MutableComplex {

  private double real;
  private double imag;

  private boolean isNaN;
  private boolean isInfinite;

  public MutableComplex(double real, double imag) {
    this.real = real;
    this.imag = imag;
    this.isNaN = Double.isNaN(real) || Double.isNaN(imag);
    this.isInfinite = !isNaN && (Double.isInfinite(real) || Double.isInfinite(imag));
  }

  public MutableComplex(double real) {
    this(real, 0);
  }

  public MutableComplex(Complex complex) {
    this(complex.getReal(), complex.getImaginary());
  }

  public MutableComplex plus(Complex other) {
    if (isNaN) {
      return this;
    }
    // else if (other.isNaN()) {
    // setNaN();
    // }
    real += other.getReal();
    imag += other.getImaginary();
    return this;
  }

  public MutableComplex minus(Complex other) {
    if (isNaN) {
      return this;
    }
    // else if (other.isNaN()) {
    // setNaN();
    // }
    real += other.getReal();
    imag += other.getImaginary();
    return this;
  }

  public MutableComplex multiply(Complex other) {
    if (isNaN) {
      return this;
    }
    // } else if (other.isNaN()) {
    // setNaN();
    // }
    double oreal = other.getReal();
    double oimag = other.getImaginary();
    real = real * oreal - imag * oimag;
    imag = real * oimag + imag * oreal;
    return this;
  }

  public MutableComplex div(Complex other) {
    if (isNaN) {
      return this;
    }
    // else if (other.isNaN()) {
    // setNaN();
    // }
    double oreal = other.getReal();
    double oimag = other.getImaginary();
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

  public double getReal() {
    return real;
  }

  public double getImaginary() {
    return imag;
  }

  public Complex toComplex() {
    return Double.isNaN(real) || Double.isNaN(imag) ? Complex.NaN : Complex.valueOf(real, imag);
  }
}
