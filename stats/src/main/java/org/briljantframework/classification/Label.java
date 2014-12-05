/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.classification;

import java.util.*;

import com.google.common.base.Preconditions;
import com.google.common.math.DoubleMath;

/**
 * The type Prediction.
 */
public class Label {

  private final String target;
  private final double probability;

  private final Map<String, Double> posterior;

  /**
   * @param target the most probable target
   * @param probability the probability of the most probable target
   * @param posterior the the posterior probabilities for the possible targets
   */
  private Label(String target, double probability, Map<String, Double> posterior) {
    this.target = target;
    this.probability = probability;
    this.posterior = posterior;
  }

  /**
   * Creates a new unary prediction
   *
   * @param a the a
   * @return the prediction
   */
  public static Label unary(String a) {
    Map<String, Double> values = new HashMap<>();
    values.put(a, 1.0);
    return new Label(a, 1.0, values);
  }

  /**
   * Creates a new binary prediction
   *
   * @param a first class
   * @param pa first class posterior probability
   * @param b other class
   * @param pb other class posterior probability
   * @return the prediction
   */
  public static Label binary(String a, double pa, String b, double pb) {
    Preconditions.checkArgument(DoubleMath.fuzzyEquals(pa + pb, 1.0, 0.00001),
        "Probabilities have to sum to one (%s /= 1.0)", pa + pb);
    Map<String, Double> values = new HashMap<>();
    values.put(a, pa);
    values.put(b, pb);

    String major = a;
    double majorProb = pa;
    if (pb > pa) {
      major = b;
      majorProb = pb;
    }

    return new Label(major, majorProb, values);
  }

  /**
   * Creates a new nominal prediction. If the classifier does not produce any posterior
   * probabilities, use {@link #unary(String)}.
   *
   * @param targets the targets
   * @param probabilities the posterior probabilities
   * @return the prediction
   */
  public static Label nominal(List<String> targets, List<Double> probabilities) {
    Preconditions.checkArgument(targets.size() == probabilities.size() && targets.size() > 0);

    Map<String, Double> values = new HashMap<>();
    values.put(targets.get(0), probabilities.get(0));

    double maxProb = probabilities.get(0), sum = probabilities.get(0);
    String mostProbable = targets.get(0);
    for (int i = 1; i < targets.size(); i++) {
      double prob = probabilities.get(i);
      String value = targets.get(i);
      if (prob > maxProb) {
        maxProb = prob;
        mostProbable = value;
      }
      values.put(value, prob);
      sum += prob;
    }

    if (!DoubleMath.fuzzyEquals(sum, 1.0, 0.000001)) {
      throw new IllegalArgumentException(String.format(
          "Probabilities have to sum to one (%f /= 1.0)", sum));
    }

    return new Label(mostProbable, maxProb, values);
  }

  /**
   * Gets target.
   *
   * @return the target
   */
  public String getPredictedValue() {
    return target;
  }

  /**
   * Gets probability.
   *
   * @return the probability
   */
  public double getPredictedProbability() {
    return probability;
  }

  /**
   * Gets targets.
   *
   * @return the targets
   */
  public Set<String> getPredictedValues() {
    return Collections.unmodifiableSet(posterior.keySet());
  }

  /**
   * Alternative posterior.
   *
   * @return the set
   */
  public Set<Map.Entry<String, Double>> getPosteriorProbabilities() {
    return Collections.unmodifiableSet(posterior.entrySet());
  }

  /**
   * Gets probability.
   *
   * @param value the value
   * @return the probability
   */
  public double getPosteriorProbability(String value) {
    return posterior.getOrDefault(value, 0d);
  }

  /**
   * Returns true if
   *
   * @param value the value
   * @return the boolean
   */
  public boolean containsPrediction(String value) {
    return value != null && value.equals(target);
  }

  @Override
  public String toString() {
    return String.format("Prediction(%s, %.2f)", target, probability);
  }
}
