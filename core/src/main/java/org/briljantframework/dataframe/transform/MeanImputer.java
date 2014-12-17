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

package org.briljantframework.dataframe.transform;

import java.util.Set;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.exceptions.TypeMismatchException;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Type;

import com.google.common.base.Preconditions;
import com.google.common.collect.Sets;

/**
 * @author Isak Karlsson
 */
public class MeanImputer implements Transformer {

  private static final Set<Type> ALLOWED_TYPES = Sets.newIdentityHashSet();
  static {
    ALLOWED_TYPES.add(DoubleVector.TYPE);
    ALLOWED_TYPES.add(IntVector.TYPE);
  }

  @Override
  public Transformation fit(DataFrame frame) {
    double[] means = new double[frame.columns()];

    for (int j = 0; j < frame.columns(); j++) {
      double mean = 0.0;
      int rows = 0;
      for (int i = 0; i < frame.rows(); i++) {
        double value = frame.getAsDouble(i, j);
        if (!Is.NA(value)) {
          mean += value;
          rows += 1;
        }
      }
      means[j] = mean / rows;
    }

    return new MeanImputation(means);
  }

  private static class MeanImputation implements Transformation {

    private final double[] means;

    private MeanImputation(double[] means) {
      this.means = means;
    }

    @Override
    public DataFrame transform(DataFrame x) {
      Preconditions.checkArgument(x.columns() == means.length);

      DataFrame.Builder builder = x.newCopyBuilder();
      for (int j = 0; j < x.columns(); j++) {
        if (!ALLOWED_TYPES.contains(x.getColumnType(j))) {
          throw new TypeMismatchException(DoubleVector.TYPE, x.getColumnType(j));
        }

        for (int i = 0; i < x.rows(); i++) {
          if (x.isNA(i, j)) {
            builder.set(i, j, means[j]);
          }
        }
      }
      return builder.build();
    }
  }


}
