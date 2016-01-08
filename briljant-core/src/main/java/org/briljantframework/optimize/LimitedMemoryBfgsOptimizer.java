/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
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
package org.briljantframework.optimize;

import org.briljantframework.Check;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;

/**
 * @author Isak Karlsson
 */
public class LimitedMemoryBfgsOptimizer implements NonlinearOptimizer {

  private static final double TOLERANCE = 4 * Math.ulp(1.0);
  private static final double MAXIMUM_STEP = 100.0;

  private final int memory;
  private final int maxIterations;
  private final double gradientTolerance;
  private final BacktrackingLineSearch lineSearch = new BacktrackingLineSearch();

  public LimitedMemoryBfgsOptimizer(int memory, int maxIterations, double gradientTolerance) {
    Check.argument(memory > 0, "Invalid m: " + memory);
    Check.argument(maxIterations > 0, "Invalid maxIter: " + maxIterations);
    this.memory = memory;
    this.maxIterations = maxIterations;
    this.gradientTolerance = gradientTolerance;
  }

  @Override
  public double optimize(DifferentialMultivariateFunction function, DoubleArray x) {
    int n = x.size();
    DoubleArray currentSolution = DoubleArray.zeros(n);
    DoubleArray currentGradient = DoubleArray.zeros(n);
    DoubleArray direction = DoubleArray.zeros(n);
    DoubleArray solutions = DoubleArray.zeros(memory, n);
    DoubleArray gradients = DoubleArray.zeros(memory, n);
    DoubleArray gradient = DoubleArray.zeros(n);
    DoubleArray scales = DoubleArray.zeros(memory);
    DoubleArray a = DoubleArray.zeros(memory);

    double f = function.gradientCost(x, gradient);
    double sum = 0;
    for (int i = 0; i < n; i++) {
      direction.set(i, -gradient.get(i));
      double tmp = x.get(i);
      sum += tmp * tmp;
    }

    double maxStepSize = MAXIMUM_STEP + Math.max(Math.sqrt(sum), n);
    int iter = 1, k = 0;
    while (iter <= maxIterations) {
      if (Double.isNaN(lineSearch.optimize(function, x, f, gradient, direction, currentSolution,
          maxStepSize))) {
        break;
      }
      f = function.gradientCost(currentSolution, currentGradient);
      for (int i = 0; i < n; i++) {
        solutions.set(k, i, currentSolution.get(i) - x.get(i));
        gradients.set(k, i, currentGradient.get(i) - gradient.get(i));
        x.set(i, currentSolution.get(i));
        gradient.set(i, currentGradient.get(i));
      }

      double test = 0.0;
      for (int i = 0; i < n; i++) {
        double temp = Math.abs(solutions.get(k, i)) / Math.max(Math.abs(x.get(i)), 1.0);
        if (temp > test) {
          test = temp;
        }
      }

      if (test < TOLERANCE) {
        return f;
      }

      test = 0;
      double den = Math.max(f, 1);
      for (int i = 0; i < n; i++) {
        double temp = Math.abs(gradient.get(i)) * Math.max(Math.abs(x.get(i)), 1.0) / den;
        if (temp > test) {
          test = temp;
        }
      }

      if (test < gradientTolerance) {
        return f;
      }

      DoubleArray kGradient = gradients.getRow(k);
      double ys = Arrays.inner(kGradient, solutions.getRow(k));
      double yy = Arrays.inner(kGradient, kGradient);
      double scalingFactor = ys / yy;

      scales.set(k, 1.0 / ys);
      direction.assign(gradient, v -> -v);

      int cp = k;
      int bound = iter > memory ? memory : iter;
      for (int i = 0; i < bound; i++) {
        a.set(cp, scales.get(cp) * Arrays.inner(solutions.getRow(cp), direction));
        Arrays.axpy(-a.get(cp), gradients.getRow(cp), direction);
        if (--cp == -1) {
          cp = memory - 1;
        }
      }
      Arrays.scal(scalingFactor, direction);

      for (int i = 0; i < bound; i++) {
        if (++cp == memory) {
          cp = 0;
        }
        double b = scales.get(cp) * Arrays.inner(gradients.getRow(cp), direction);
        Arrays.axpy(a.get(cp) - b, solutions.getRow(cp), direction);
      }

      if (++k == memory) {
        k = 0;
      }
      iter++;
    }
    return f;
  }

  @Override
  public String toString() {
    return "LimitedMemoryBfgsOptimizer{" + "memory=" + memory + ", maxIterations=" + maxIterations
        + ", gradientTolerance=" + gradientTolerance + '}';
  }
}
