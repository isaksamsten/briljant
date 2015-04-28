package org.briljantframework.distribution;

import java.util.Random;

/**
 * Created by isak on 27/02/15.
 */
public class UniformDistribution extends Distribution {

  private final double min, max;

  public UniformDistribution(Random random, double min, double max) {
    super(random);
    this.min = min;
    this.max = max;
  }

  public UniformDistribution(double min, double max) {
    this.min = min;
    this.max = max;
  }

  public UniformDistribution() {
    this(0, 1);
  }

  @Override
  public double sample() {
    return min + (max - min) * random.nextDouble();
  }
}
