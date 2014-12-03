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

import org.briljantframework.classification.Predictions;
import org.briljantframework.vector.Vector;

import com.google.common.base.Preconditions;

/**
 * Created by Isak Karlsson on 06/10/14.
 */
public class Accuracy extends AbstractMeasure {


  /**
   * Instantiates a new Accuracy.
   *
   * @param builder the producer
   */
  private Accuracy(AbstractMeasure.Builder builder) {
    super(builder);
  }

  /**
   * @return the factory
   */
  public static Factory getFactory() {
    return Builder::new;
  }

  @Override
  public String getName() {
    return "Accuracy";
  }

  public static final class Builder extends AbstractMeasure.Builder {

    @Override
    public void compute(Sample sample, Predictions predictions, Vector truth) {
      Preconditions.checkArgument(predictions.size() == truth.size());

      double accuracy = 0.0;
      for (int i = 0; i < predictions.size(); i++) {
        if (predictions.get(i).getPredictedValue().equals(truth.getAsString(i))) {
          accuracy++;
        }
      }

      addComputedValue(sample, accuracy / predictions.size());
    }

    @Override
    public Measure build() {
      return new Accuracy(this);
    }
  }
}
