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
