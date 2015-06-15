package org.briljantframework.stat;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RunningStatisticsTest {

  private RunningStatistics s;

  @Before
  public void setUp() throws Exception {
    s = new RunningStatistics();
    s.add(10);
    s.add(20);
    s.add(30);
  }

  @Test
  public void testAddAll() throws Exception {
    RunningStatistics gold = new RunningStatistics();
    gold.addAll(new double[]{10, 20, 30, 40, 50, 60});
    RunningStatistics x = new RunningStatistics();
    x.addAll(new double[]{40, 50, 60});
    s.addAll(x);

    assertEquals(gold.getMean(), s.getMean(), 0);

  }

  @Test
  public void testMean() throws Exception {
    assertEquals(20.0, s.getMean(), 0);
  }

  @Test
  public void testVariance() throws Exception {
    assertEquals(66.66, s.getVariance(), 0.1);
  }

  @Test
  public void testStd() throws Exception {
    assertEquals(Math.sqrt(66.66), s.getStandardDeviation(), 0.1);
  }
}