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

package org.briljantframework.evaluation.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.briljantframework.classification.Label;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 06/10/14.
 */
public class AreaUnderCurve extends AbstractClassMeasure {

  /**
   * Instantiates a new Area under curve.
   *
   * @param producer the producer
   */
  protected AreaUnderCurve(AbstractClassMeasure.Builder producer) {
    super(producer);
  }

  /**
   * The constant FACTORY.
   */
  public static Factory getFactory() {
    return Builder::new;
  }

  @Override
  public String getName() {
    return "Area Under ROC Curve";
  }

  // TODO(isak): warn user if a numeric target is used
  public static final class Builder extends AbstractClassMeasure.Builder {

    @Override
    protected double calculateMetricForLabel(String value, List<Label> predictions, Vector truth) {
      return calculateAreaUnderCurve(predictions, truth, value);
    }

    private double calculateAreaUnderCurve(List<Label> predicted, Vector truth, String value) {
      double truePositives = 0, falsePositives = 0, positives = 0;

      List<PredictionProbability> pairs = new ArrayList<>(predicted.size());
      for (int i = 0; i < truth.size(); i++) {
        Label p = predicted.get(i);

        boolean positiveness = truth.getAsString(i).equals(value);
        if (positiveness) {
          positives++;
        }
        pairs.add(new PredictionProbability(positiveness, p.getPosteriorProbability(value)));
      }

      // Sort in decreasing order of posterior probability
      Collections.sort(pairs);

      double negatives = predicted.size() - positives;
      double previousProbability = -1;
      double auc = 0.0;
      double previousTruePositive = 0.0;
      double previousFalsePositive = 0.0;

      // Calculates the auc using trapezoidal rule
      for (PredictionProbability pair : pairs) {
        double probability = pair.probability;
        if (probability != previousProbability) {
          double falseChange = Math.abs(falsePositives - previousFalsePositive);
          double trueChange = truePositives + previousTruePositive;
          auc += falseChange * trueChange / 2;

          previousFalsePositive = falsePositives;
          previousTruePositive = truePositives;
          previousProbability = probability;
        }

        if (pair.positive) {
          truePositives++;
        } else {
          falsePositives++;
        }
      }
      if (positives * negatives == 0) {
        return 0;
      } else {
        double negChange = Math.abs(negatives - previousFalsePositive);
        double posChange = positives + previousTruePositive;
        return (auc + negChange * posChange / 2) / (positives * negatives);
      }
    }

    @Override
    public Measure build() {
      return new AreaUnderCurve(this);
    }

    private static final class PredictionProbability implements Comparable<PredictionProbability> {
      /**
       * True if the AucPair is positive
       */
      public final boolean positive;

      /**
       * The Probability.
       */
      public final double probability;

      private PredictionProbability(boolean positive, double probability) {
        this.positive = positive;
        this.probability = probability;
      }

      @Override
      public String toString() {
        return positive + " " + probability;
      }

      @Override
      public int compareTo(PredictionProbability o) {
        return Double.compare(o.probability, this.probability);
      }
    }
  }
}
