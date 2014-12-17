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


import org.briljantframework.dataframe.DataFrame;

/**
 * Removes incomplete cases, i.e. rows with missing values.
 * 
 * @author Isak Karlsson
 */
public class RemoveIncompleteCases implements Transformation {

  @Override
  public DataFrame transform(DataFrame x) {
    DataFrame.Builder builder = x.newBuilder();
    for (int i = 0; i < x.rows(); i++) {
      boolean hasNA = false;
      for (int j = 0; j < x.columns(); j++) {
        if (x.isNA(i, j)) {
          hasNA = true;
          break;
        }
      }
      if (!hasNA) {
        for (int j = 0; j < x.columns(); j++) {
          builder.set(i, j, x, i, j);
        }
      }
    }
    return builder.build();
  }
}
