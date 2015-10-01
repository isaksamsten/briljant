/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
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

package org.briljantframework.array.random;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.array.IntArray;

/**
 * @author Isak Karlsson
 */
public final class Sample {

  private Sample() {}

  /**
   * Sample {@code sample} elements from the set {@code [0, population)}.
   *
   * @param population the population size
   * @param samples the sample size
   * @return an int matrix with values sampled from the population
   */
  public static IntArray withoutReplacement(int population, int samples) {
    return withoutReplacement(ThreadLocalRandom.current(), population, samples);
  }

  /**
   * Sample {@code sample} elements from the set {@code [0, population)}.
   *
   * @param rng the random number generator
   * @param population the population size
   * @param samples the sample size
   * @return an int matrix with values sampled from the population
   */
  public static IntArray withoutReplacement(Random rng, int population, int samples) {
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
