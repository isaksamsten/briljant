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

package org.briljantframework.distribution;

import java.util.Random;

/**
 * @author Isak Karlsson
 */
public class TriangleDistribution extends Distribution {

  private final double lower, upper, mode;

  public TriangleDistribution(double lower, double upper, double mode) {
    this(new Random(), lower, upper, mode);
  }

  public TriangleDistribution(Random random, double lower, double upper, double mode) {
    super(random);
    this.lower = lower;
    this.upper = upper;
    this.mode = mode;
  }

  @Override
  public double sample() {
    double u = random.nextDouble();
    if (u < (mode - lower) / (upper - lower)) {
      return lower + Math.sqrt(u * (upper - lower) * (mode - lower));
    } else {
      return upper - Math.sqrt((1 - u) * (upper - lower) * (upper - mode));
    }
  }
}
