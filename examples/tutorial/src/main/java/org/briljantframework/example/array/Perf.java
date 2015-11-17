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

package org.briljantframework.example.array;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.briljantframework.Bj;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.Op;
import org.briljantframework.statistics.FastStatistics;

import java.util.Random;

/**
 * Created by Isak Karlsson on 05/01/15.
 */
public class Perf {

  private static final int NITER = 100;

  public static void main(String[] args) {
    int mandel_sum = 0;
    long tmin = Long.MAX_VALUE, t;
    for (int i = 0; i < NITER; ++i) {
      t = System.nanoTime();
      mandel_sum = mandelperf();
      t = System.nanoTime() - t;
      if (t < tmin) {
        tmin = t;
      }
    }
    assert (mandel_sum == 14720) : "value was " + mandel_sum;
    print_perf("mandel", tmin);

    tmin = Long.MAX_VALUE;
    for (int i = 0; i < NITER; ++i) {
      t = System.nanoTime();
      DoubleArray C = randmatmul_Briljant(1000);
      assert (0 <= C.get(0));
      t = System.nanoTime() - t;
      if (t < tmin) {
        tmin = t;
      }
    }
    print_perf("rand_mat_mul", tmin);

    double[] r;
    tmin = Long.MAX_VALUE;
    for (int i = 0; i < NITER; ++i) {
      t = System.nanoTime();
      r = randmatstat_Briljant(1000);
      t = System.nanoTime() - t;
      if (t < tmin) {
        tmin = t;
      }
    }
    print_perf("rand_mat_stat", tmin);
  }

  private static double[] randmatstat_Briljant(int t) {
    Random random = new Random();
    int n = 5;
    DoubleArray p = Bj.doubleArray(n, n * 4);
    DoubleArray q = Bj.doubleArray(n * 2, n * 2);
    DoubleArray v = Bj.doubleArray(t, 1);
    DoubleArray w = Bj.doubleArray(t, 1);

    for (int i = 0; i < t; i++) {
      p.getView(0, 0, n, n).assign(random::nextGaussian);
      p.getView(0, n, n, n).assign(random::nextGaussian);
      p.getView(0, n * 2, n, n).assign(random::nextGaussian);
      p.getView(0, n * 3, n, n).assign(random::nextGaussian);

      q.getView(0, 0, n, n).assign(random::nextGaussian);
      q.getView(0, n, n, n).assign(random::nextGaussian);
      q.getView(n, 0, n, n).assign(random::nextGaussian);
      q.getView(n, n, n, n).assign(random::nextGaussian);

      DoubleArray x = p.mmul(Op.TRANSPOSE, p, Op.KEEP);
      v.set(i, Bj.trace(x.mmul(x).mmul(x)));

      x = q.mmul(Op.TRANSPOSE, q, Op.KEEP);
      w.set(i, Bj.trace(x.mmul(x).mmul(x)));
    }
    StatisticalSummary statV = v.collect(FastStatistics::new, FastStatistics::addValue);
    StatisticalSummary statW = w.collect(FastStatistics::new, FastStatistics::addValue);
    double meanv = statV.getMean();
    double stdv = statV.getStandardDeviation();
    double meanw = statW.getMean();
    double stdw = statW.getStandardDeviation();

    return new double[]{meanv, stdv, meanw, stdw};
  }

  private static final Random random = new Random();

  private static DoubleArray randmatmul_Briljant(int i) {
    DoubleArray a = Bj.doubleArray(i * i).assign(random::nextGaussian).reshape(i, i);
    DoubleArray b = Bj.doubleArray(i * i).assign(random::nextGaussian).reshape(i, i);
    return a.mmul(b);
  }

  private static void print_perf(String name, long t) {
    System.out.printf("java,%s,%.6f\n", name, t / 1E6);
  }

  private static int mandelperf() {
    int mandel_sum = 0;
    for (double re = -2.0; re <= 0.5; re += 0.1) {
      for (double im = -1.0; im <= 1.0; im += 0.1) {
        int m = mandel(re, im);
        mandel_sum += m;
      }
    }
    return mandel_sum;
  }

  private static int mandel(double re, double im) {
    int n = 0;
    Complex z = new Complex(re, im);
    Complex c = new Complex(re, im);
    for (n = 0; n <= 79; ++n) {
      if (z.abs() > 2.0) {
        n -= 1;
        break;
      }

      // z = z*z + c
      z = z.multiply(z).add(c);
    }
    return n + 1;
  }
}
