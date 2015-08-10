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

package org.briljantframework.stat;

/**
 * A safe and
 *
 * @author Isak Karlsson
 */
public class RunningStatistics implements DescriptiveStatistics {

  private int n = 0;
  private double om, nm, os, ns;
  private double min = Double.NEGATIVE_INFINITY, max = Double.POSITIVE_INFINITY;

  public void add(double x) {
    n += 1;
    if (n == 1) {
      om = x;
      nm = x;
      os = 0;
      min = x;
      max = x;
    } else {
      nm = om + (x - om) / n;
      ns = os + (x - om) * (x - nm);
      om = nm;
      os = ns;

      if (x < min) {
        min = x;
      } else if (x > max) {
        max = x;
      }
    }
  }

  public void addAll(double[] arr) {
    for (double v : arr) {
      add(v);
    }
  }

  @Override
  public int size() {
    return n;
  }

  @Override
  public double getMax() {
    return max;
  }

  @Override
  public double getMin() {
    return min;
  }

  @Override
  public double getMean() {
    return n > 0 ? nm : 0;
  }

  @Override
  public double getVariance() {
    return n > 1 ? ns / n : 0;
  }

  @Override
  public double getStandardDeviation() {
    return Math.sqrt(getVariance());
  }
}
