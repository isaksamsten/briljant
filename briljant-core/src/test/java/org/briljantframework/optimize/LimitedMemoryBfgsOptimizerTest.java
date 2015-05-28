package org.briljantframework.optimize;

import org.briljantframework.Bj;
import org.briljantframework.distribution.UniformDistribution;
import org.briljantframework.matrix.DoubleMatrix;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LimitedMemoryBfgsOptimizerTest {

  @Test
  public void testOptimize() throws Exception {
    DifferentialFunction function = new DifferentialFunction() {
//      @Override
//      public double gradientCost(DoubleMatrix x, DoubleMatrix gradient) {
//        double func = 100 * Math.pow(x.get(0) + 3, 4) + Math.pow(x.get(1) - 3, 4);
//        gradient.set(0, 400 * Math.pow(x.get(0) + 3, 3));
//        gradient.set(1, 4 * Math.pow(x.get(1) - 3, 3));
//        return func;
//      }

      @Override
      public double cost(DoubleMatrix x) {
        return 100 * Math.pow(x.get(0) + 3, 4) + Math.pow(x.get(1) - 3, 4);
      }
    };

    DoubleMatrix x = Bj.rand(2, new UniformDistribution());
    LimitedMemoryBfgsOptimizer optimizer = new LimitedMemoryBfgsOptimizer(1, 100, 0.000000001);
    System.out.println(optimizer.optimize(function, x));
    System.out.println(x);

    System.out.println(x.get(0));


  }

  @Test
  public void testOptimze() throws Exception {
    DifferentialFunction func = new DifferentialFunction() {

      @Override
      public double cost(DoubleMatrix x) {
        double f = 0.0;
        for (int j = 1; j <= x.size(); j += 2) {
          double t1 = 1.e0 - x.get(j - 1);
          double t2 = 1.e1 * (x.get(j) - x.get(j - 1) * x.get(j - 1));
          f = f + t1 * t1 + t2 * t2;
        }
        return f;
      }

      @Override
      public double gradientCost(DoubleMatrix x, DoubleMatrix g) {
        double f = 0.0;
        for (int j = 1; j <= x.size(); j += 2) {
          double t1 = 1.e0 - x.get(j - 1);
          double t2 = 1.e1 * (x.get(j) - x.get(j - 1) * x.get(j - 1));
          g.set(j + 1 - 1, 2.e1 * t2);
          g.set(j - 1, -2.e0 * (x.get(j - 1) * g.get(j + 1 - 1) + t1));
          f = f + t1 * t1 + t2 * t2;
        }
        return f;
      }
    };

    double[] x = new double[100];
    for (int j = 1; j <= x.length; j += 2) {
      x[j - 1] = -1.2e0;
      x[j + 1 - 1] = 1.e0;
    }
    double result = new LimitedMemoryBfgsOptimizer(5, 200, 0.0001).optimize(func, Bj.matrix(x));
    System.out.println(result);
    assertEquals(3.2760183604E-14, result, 1E-15);

  }
}