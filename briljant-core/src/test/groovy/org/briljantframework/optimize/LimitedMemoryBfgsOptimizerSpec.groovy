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

package org.briljantframework.optimize

import org.briljantframework.array.Arrays
import org.briljantframework.array.DoubleArray
import spock.lang.Specification

/**
 * @author Isak Karlsson
 */
class LimitedMemoryBfgsOptimizerSpec extends Specification {

  def "test limited memory optimizer with automatic differentiation"() {
    given:
    DifferentialMultivariateFunction d = {x ->
      100 * Math.pow(x.get(0) + 3, 4) + Math.pow(x.get(1) - 3, 4);
    }
    def optimizer = new LimitedMemoryBfgsOptimizer(5, 100, 1e-5)
    def x = DoubleArray.of(0.0, 0.0)

    when:
    optimizer.optimize(d, x)

    then:
    x.mapToLong {it -> Math.round it} == Arrays.newLongVector([-3, 3] as long[])
  }

  def "test limited memory optimizer with gradient cost"() {
    given:
    DifferentialMultivariateFunction d = new DifferentialMultivariateFunction() {

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
    def x = Arrays.newDoubleArray(100)
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
