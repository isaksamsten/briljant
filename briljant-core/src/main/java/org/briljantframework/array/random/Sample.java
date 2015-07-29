package org.briljantframework.array.random;

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.array.IntArray;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author Isak Karlsson
 */
public final class Sample {

  private final Random rng = new Random();

  private Sample() {
  }

  /**
   * Sample {@code sample} elements from the set {@code [0, population)}.
   *
   * @param population the population size
   * @param samples    the sample size
   * @return an int matrix with values sampled from the population
   */
  public static IntArray withoutReplacement(int population, int samples) {
    return withoutReplacement(population, samples, ThreadLocalRandom.current());
  }

  /**
   * Sample {@code sample} elements from the set {@code [0, population)}.
   *
   * @param population the population size
   * @param samples    the sample size
   * @param rng        the random number generator
   * @return an int matrix with values sampled from the population
   */
  public static IntArray withoutReplacement(int population, int samples, Random rng) {
    Check.argument(population > 0, "Population should be larger than 0");
    Check.argument(samples < population, "The population should be larger than the sample");

    IntArray out = Bj.intArray(samples);
    for (int i = 0; i < samples; i++) {
      out.set(i, i);
    }

    for (int i = samples; i < population; i++) {
      int j = rng.nextInt(i + 1);
      if (j < samples) {
        out.set(j, i);
      }
    }
    return out;
  }

}
