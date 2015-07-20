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
import org.briljantframework.vector.Vec;

/**
 * @author Isak Karlsson
 */
public class MeanImputer implements Transformer {

  @Override
  public Transformation fit(DataFrame frame) {
    DoubleArray means = Bj.doubleArray(frame.columns());
    for (int j = 0; j < frame.columns(); j++) {
      means.set(j, Vec.mean(frame.get(j)));
    }

    return x -> {
      Check.size(x.columns(), means.size());
      DataFrame.Builder builder = x.newBuilder();
//      builder.getColumnNames().putAll(x.getColumnNames());
      for (int j = 0; j < x.columns(); j++) {
        Check.type(x.getType(j), Vec.DOUBLE);
        for (int i = 0; i < x.rows(); i++) {
          if (x.isNA(i, j)) {
            builder.set(i, j, means.get(j));
          } else {
            builder.set(i, j, x, i, j);
          }
        }
      }
      return builder.build();
    };
  }
}
