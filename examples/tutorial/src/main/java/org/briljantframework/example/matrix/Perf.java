package org.briljantframework.example.matrix;

import org.briljantframework.Bj;
import org.briljantframework.complex.Complex;
import org.briljantframework.array.DoubleMatrix;
import org.briljantframework.array.T;
import org.briljantframework.stat.DescriptiveStatistics;
import org.briljantframework.stat.RunningStatistics;

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
      DoubleMatrix C = randmatmul_Briljant(1000);
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
    DoubleMatrix p = Bj.doubleMatrix(n, n * 4);
    DoubleMatrix q = Bj.doubleMatrix(n * 2, n * 2);
    DoubleMatrix v = Bj.doubleMatrix(t, 1);
    DoubleMatrix w = Bj.doubleMatrix(t, 1);

    for (int i = 0; i < t; i++) {
      p.getView(0, 0, n, n).assign(random::nextGaussian);
      p.getView(0, n, n, n).assign(random::nextGaussian);
      p.getView(0, n * 2, n, n).assign(random::nextGaussian);
      p.getView(0, n * 3, n, n).assign(random::nextGaussian);

      q.getView(0, 0, n, n).assign(random::nextGaussian);
      q.getView(0, n, n, n).assign(random::nextGaussian);
      q.getView(n, 0, n, n).assign(random::nextGaussian);
      q.getView(n, n, n, n).assign(random::nextGaussian);

      DoubleMatrix x = p.mmul(T.YES, p, T.NO);
      v.set(i, Bj.trace(x.mmul(x).mmul(x)));

      x = q.mmul(T.YES, q, T.NO);
      w.set(i, Bj.trace(x.mmul(x).mmul(x)));
    }
    DescriptiveStatistics statV = v.collect(RunningStatistics::new, RunningStatistics::add);
    DescriptiveStatistics statW = w.collect(RunningStatistics::new, RunningStatistics::add);
    double meanv = statV.getMean();
    double stdv = statV.getStandardDeviation();
    double meanw = statW.getMean();
    double stdw = statW.getStandardDeviation();

    return new double[]{meanv, stdv, meanw, stdw};
  }

  private static final Random random = new Random();

  private static DoubleMatrix randmatmul_Briljant(int i) {
    DoubleMatrix a = Bj.doubleMatrix(i, i).assign(random::nextGaussian);
    DoubleMatrix b = Bj.doubleMatrix(i, i).assign(random::nextGaussian);
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
