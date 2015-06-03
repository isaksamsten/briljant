package org.briljantframework.distribution;

import java.util.Random;

/**
 * @author Isak Karlsson
 */
public abstract class Distribution {

  protected final Random random;

  protected Distribution(Random random) {
    this.random = random;
  }

  /**
   * Return a new pseudo-randomly generated number sampled from this distribution.
   *
   * @return a new random number
   */
  public abstract double sample();
}
