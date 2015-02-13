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

import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vector;

import com.google.common.base.Preconditions;

/**
 * @author Isak Karlsson
 */
public class Accuracy extends AbstractMeasure {

  private Accuracy(AbstractMeasure.Builder builder) {
    super(builder);
  }

  @Override
  public String getName() {
    return "Accuracy";
  }

  public static final class Builder extends AbstractMeasure.Builder {

    public Builder(Vector domain) {
      super(domain);
    }

    @Override
    public void compute(Sample sample, Predictor predictor, DataFrame dataFrame, Vector predicted,
        DoubleMatrix probabilities, Vector truth) {
      Preconditions.checkArgument(predicted.size() == truth.size());

      double accuracy = 0.0;
      for (int i = 0; i < predicted.size(); i++) {
        if (predicted.getAsString(i).equals(truth.getAsString(i))) {
          accuracy++;
        }
      }

      addComputedValue(sample, accuracy / predicted.size());
    }

    @Override
    public Measure build() {
      return new Accuracy(this);
    }
  }
}
