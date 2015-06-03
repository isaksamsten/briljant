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
