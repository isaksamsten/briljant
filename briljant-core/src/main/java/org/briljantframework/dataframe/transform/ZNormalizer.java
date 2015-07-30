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

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.stat.DescriptiveStatistics;
import org.briljantframework.vector.Vec;

/**
 * Z normalization is also known as "Normalization to Zero Mean and Unit of Energy" first mentioned
 * by found in Goldin & Kanellakis. It ensures that all elements of the input vector are
 * transformed
 * into the output vector whose mean is approximately 0 while the standard deviation are in a range
 * close to 1.
 *
 * @author Isak Karlsson
 */
public class ZNormalizer implements Transformer {

  @Override
  public Transformation fit(DataFrame frame) {
    DoubleArray mean = Bj.doubleArray(frame.columns());
    DoubleArray sigma = Bj.doubleArray(frame.columns());
    for (int i = 0; i < frame.columns(); i++) {
      DescriptiveStatistics stats = Vec.statistics(frame.get(i));
      mean.set(i, stats.getMean());
      sigma.set(i, stats.getStandardDeviation());
    }

    return x -> {
      Check.size(mean.size(), x.columns());
      DataFrame.Builder builder = x.newBuilder();
      for (int j = 0; j < x.columns(); j++) {
        Check.type(x.getType(j), Vec.DOUBLE);
        builder.addColumnBuilder(x.getType(j));
//        builder.getColumnNames().putFromIfPresent(j, x.getColumnNames(), j);
        double m = mean.get(j);
        double std = sigma.get(j);
        for (int i = 0; i < x.rows(); i++) {
          if (x.isNA(i, j)) {
            builder.setNA(i, j);
          } else {
            builder.set(i, j, (x.getAsDouble(i, j) - m) / std);
          }
        }
      }
      return builder.build();
    };
  }

}
