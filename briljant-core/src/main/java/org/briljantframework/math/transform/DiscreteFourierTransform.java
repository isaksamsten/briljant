/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.briljantframework.math.transform;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;

/**
 * @author Isak Karlsson
 */
public final class DiscreteFourierTransform {

  private DiscreteFourierTransform() {}

  private static void fftInplace(ComplexArray a) {
    int n = a.size();
    if ((n & (n - 1)) == 0) { // n is a power of 2?
      transformRadix2(a);
    } else {
      transformBluestein(a);
    }
  }

  public static ComplexArray fft(ComplexArray a) {
    ComplexArray copy = a.copy();
    fftInplace(copy);
    return copy;
  }

  public static ComplexArray ifft(ComplexArray a) {
    ComplexArray copy = Arrays.newComplexArray(a.size());
    for (int i = 0; i < a.size(); i++) {
      Complex c = a.get(i);
      copy.set(i, new Complex(c.getImaginary(), c.getReal()));
    }
    fftInplace(copy);

    int n = copy.size();

    // Reversing and scaling
    for (int i = 0; i < n; i++) {
      Complex c = copy.get(i);
      copy.set(i, new Complex(c.getImaginary() / n, c.getReal() / n));
    }
    return copy;
  }

  public static ComplexArray fft(DoubleArray a) {
    return fft(a.asComplex());
  }

  private static void transformBluestein(ComplexArray a) {
    // Find a power-of-2 convolution length m such that m >= n * 2 + 1
    int n = a.size();
    if (n >= 0x20000000) { // n >= 536870912
      throw new IllegalArgumentException("");
    }
    int m = Integer.highestOneBit(n * 2 + 1) << 1;

    // Trigonometric tables
    DoubleArray cosTable = Arrays.newDoubleArray(n);
    DoubleArray sinTable = Arrays.newDoubleArray(n);
    for (int i = 0; i < n; i++) {
      int j = (int) ((long) i * i % (n * 2));
      cosTable.set(i, Math.cos(Math.PI * j / n));
      sinTable.set(i, Math.sin(Math.PI * j / n));
    }

    ComplexArray an = Arrays.newComplexArray(m);
    ComplexArray bn = Arrays.newComplexArray(m);

    bn.set(0, new Complex(cosTable.get(0), sinTable.get(0)));
    for (int i = 0; i < n; i++) {
      double cos = cosTable.get(i);
      double sin = sinTable.get(i);
      Complex complex = a.get(i);
      double real = complex.getReal() * cos + complex.getImaginary() * sin;
      double imag = -complex.getReal() * sin + complex.getImaginary() * cos;
      an.set(i, new Complex(real, imag));

      int j = i + 1;
      if (j < n) {
        Complex bcVal = Complex.valueOf(cosTable.get(j), sinTable.get(j));
        bn.set(j, bcVal);
        bn.set(m - j, bcVal);
      }
    }

    // Convolution
    ComplexArray cc = convolve(an, bn);
    for (int i = 0; i < n; i++) {
      double cos = cosTable.get(i);
      double sin = sinTable.get(i);

      Complex cv = cc.get(i);
      double real = cv.getReal() * cos + cv.getImaginary() * sin;
      double imag = -cv.getReal() * sin + cv.getImaginary() * cos;
      a.set(i, new Complex(real, imag));
    }
  }

  /*
   * Computes the circular convolution of the given complex vectors. Each vector's length must be
   * the same.
   */
  private static ComplexArray convolve(ComplexArray x, ComplexArray y) {
    int n = x.size();
    ComplexArray xt = fft(x);
    ComplexArray yt = fft(y);

    for (int i = 0; i < n; i++) {
      xt.set(i, xt.get(i).multiply(yt.get(i)));
    }

    // TODO: do not 'hand-reverse'
    for (int i = 0; i < n; i++) {
      Complex complex = xt.get(i);
      xt.set(i, new Complex(complex.getImaginary(), complex.getReal()));
    }

    fftInplace(xt); // inverse transform, since xt is reversed above
    for (int i = 0; i < n; i++) {
      Complex c = xt.get(i);
      xt.set(i, Complex.valueOf(c.getImaginary() / n, c.getReal() / n));
    }
    return xt;
  }

  private static void transformRadix2(ComplexArray a) {
    final int n = a.size();
    int levels = (int) Math.floor(Math.log(n) / Math.log(2));
    if (1 << levels != n) {
      throw new IllegalArgumentException();
    }

    DoubleArray cosTable = Arrays.newDoubleArray(n / 2);
    DoubleArray sinTable = Arrays.newDoubleArray(n / 2);
    final double v = 2 * Math.PI;
    for (int i = 0; i < n / 2; i++) {
      cosTable.set(i, Math.cos(v * i / n));
      sinTable.set(i, Math.sin(v * i / n));
    }

    // Bit-reversed addressing permutation (i.e. even addresses in the first half and odd in the
    // second half)
    for (int i = 0; i < n; i++) {
      int j = Integer.reverse(i) >>> (32 - levels);
      if (j > i) {
        a.swap(j, i);
      }
    }

    // Cooley-Tukey decimation-in-time radix-2 FFT
    for (int size = 2; size <= n; size *= 2) {
      int halfSize = size / 2;
      int tableStep = n / size;
      for (int i = 0; i < n; i += size) {
        for (int j = i, k = 0; j < i + halfSize; j++, k += tableStep) {
          Complex hjVal = a.get(j + halfSize);
          Complex jVal = a.get(j);
          double cos = cosTable.get(k);
          double sin = sinTable.get(k);
          double tpre = hjVal.getReal() * cos + hjVal.getImaginary() * sin;
          double tpim = -hjVal.getReal() * sin + hjVal.getImaginary() * cos;
          a.set(j + halfSize, new Complex(jVal.getReal() - tpre, jVal.getImaginary() - tpim));
          a.set(j, new Complex(jVal.getReal() + tpre, jVal.getImaginary() + tpim));
        }
      }
      if (size == n) {
        break;
      }
    }
  }
}
