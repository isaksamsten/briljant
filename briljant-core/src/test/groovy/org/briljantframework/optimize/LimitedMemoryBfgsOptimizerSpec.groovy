package org.briljantframework.optimize

import org.briljantframework.Bj
import org.briljantframework.array.DoubleArray
import spock.lang.Specification

/**
 * @author Isak Karlsson
 */
class LimitedMemoryBfgsOptimizerSpec extends Specification {

  def "test limited memory optimizer with automatic differentiation"() {
    given:
    DifferentialFunction d = {x ->
      100 * Math.pow(x.get(0) + 3, 4) + Math.pow(x.get(1) - 3, 4);
    }
    def optimizer = new LimitedMemoryBfgsOptimizer(5, 100, 1e-5)
    def x = Bj.array(0.0, 0.0 as double)

    when:
    optimizer.optimize(d, x)

    then:
    x.mapToLong {it -> Math.round it} == Bj.array([-3, 3] as long[])
  }

  def "test limited memory optimizer with gradient cost"() {
    given:
    DifferentialFunction d = new DifferentialFunction() {

//      @Override
//      double gradientCost(DoubleArray x, DoubleArray g) {
//        double f = 0.0;
//        for (int j = 1; j <= x.size(); j += 2) {
//          double t1 = 1.0 - x.get(j - 1);
//          double t2 = 10.0 * (x.get(j) - x.get(j - 1) * x.get(j - 1));
//          g.set(j + 1 - 1, 20.0 * t2);
//          g.set(j - 1, -2.0 * (x.get(j - 1) * g.get(j + 1 - 1) + t1));
//          f = f + t1 * t1 + t2 * t2;
//        }
//        return f;
//      }

      @Override
      double cost(DoubleArray x) {
        double f = 0.0;
        for (int j = 1; j <= x.size(); j += 2) {
          double t1 = 1.0 - x.get(j - 1);
          double t2 = 10.1 * (x.get(j) - x.get(j - 1) * x.get(j - 1));
          f = f + t1 * t1 + t2 * t2;
        }
        return f;
      }
    }
    def x = Bj.doubleArray(100)
    for (int i = 1; i <= x.size(); i += 2) {
      x.set(i - 1, -1.2)
      x.set(i + 1 - 1, 1.0)
    }
    def o = new LimitedMemoryBfgsOptimizer(5, 100, 1E-6)

    when:
    def error = o.optimize(d, x)

    then:
    Math.abs(error - 3.2760183604E-14) < 1E-9
  }
}
