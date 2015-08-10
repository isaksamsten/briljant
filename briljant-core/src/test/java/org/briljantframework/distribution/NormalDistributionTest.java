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

import org.junit.Test;

import static org.briljantframework.distribution.NormalDistribution.cdf;
import static org.briljantframework.distribution.NormalDistribution.ppf;
import static org.junit.Assert.assertEquals;

public class NormalDistributionTest {

  @Test
  public void testOt() throws Exception {
    double s = NormalDistribution.pdf(2, 0, 1);
    double e = NormalDistribution.pdf(2, 0, 1);
    assertEquals(0, s - e, 0);
  }

  @Test
  public void testPpf() throws Exception {
    assertEquals(Double.POSITIVE_INFINITY, ppf(1), 0.0001);
    assertEquals(Double.NEGATIVE_INFINITY, ppf(0), 0.0001);
    assertEquals(5.199337, ppf(0.9999999), 0.0001);
    assertEquals(0.2, cdf(ppf(0.2)), 0.0001);
  }
}
