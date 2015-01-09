package org.briljantframework.example.matrix;

import static org.briljantframework.matrix.Doubles.*;

import java.util.Random;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Transpose;

/**
 * Created by Isak Karlsson on 05/01/15.
 */
public class Perf {
  private static final int NITER = 10;

  public static void main(String[] args) {
    int mandel_sum = 0;
    long tmin = Long.MAX_VALUE, t;
    for (int i = 0; i < NITER; ++i) {
      t = System.nanoTime();
      mandel_sum = mandelperf();
      t = System.nanoTime() - t;
      if (t < tmin)
        tmin = t;
    }
    assert (mandel_sum == 14720) : "value was " + mandel_sum;
    print_perf("mandel", tmin);

    tmin = Long.MAX_VALUE;
    for (int i = 0; i < NITER; ++i) {
      t = System.nanoTime();
      DoubleMatrix C = randmatmul_Briljant(1000);
      assert (0 <= C.get(0));
      t = System.nanoTime() - t;
      if (t < tmin)
        tmin = t;
    }
    print_perf("rand_mat_mul", tmin);

    double[] r;
    tmin = Long.MAX_VALUE;
    for (int i = 0; i < NITER; ++i) {
      t = System.nanoTime();
      r = randmatstat_Briljant(1000);
      t = System.nanoTime() - t;
      if (t < tmin)
        tmin = t;
    }
    print_perf("rand_mat_stat", tmin);
  }

  private static double[] randmatstat_Briljant(int t) {
    Random random = new Random();
    int n = 5;
    DoubleMatrix p = zeros(n, n * 4);
    DoubleMatrix q = zeros(n * 2, n * 2);
    DoubleMatrix v = zeros(t, 1);
    DoubleMatrix w = zeros(t, 1);

    for (int i = 0; i < t; i++) {
      p.getView(0, 0, n, n).assign(random::nextGaussian);
      p.getView(0, n, n, n).assign(random::nextGaussian);
      p.getView(0, n * 2, n, n).assign(random::nextGaussian);
      p.getView(0, n * 3, n, n).assign(random::nextGaussian);

      q.getView(0, 0, n, n).assign(random::nextGaussian);
      q.getView(0, n, n, n).assign(random::nextGaussian);
      q.getView(n, 0, n, n).assign(random::nextGaussian);
      q.getView(n, n, n, n).assign(random::nextGaussian);

      DoubleMatrix x = p.mmul(Transpose.YES, p, Transpose.NO);
      v.set(i, trace(x.mmul(x).mmul(x)));

      x = q.mmul(Transpose.YES, q, Transpose.NO);
      w.set(i, trace(x.mmul(x).mmul(x)));
    }
    double meanv = mean(v);
    double stdv = std(v, meanv);
    double meanw = mean(w);
    double stdw = std(w, meanw);
    return new double[] {meanv, stdv, meanw, stdw};
  }

  private static DoubleMatrix randmatmul_Briljant(int i) {
    DoubleMatrix a = randn(i, i);
    DoubleMatrix b = randn(i, i);
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
      z = z.multiply(z).plus(c);
    }
    return n + 1;
  }
}
