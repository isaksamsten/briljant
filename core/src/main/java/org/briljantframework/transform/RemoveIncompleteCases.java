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

package org.briljantframework.transform;


import org.briljantframework.dataframe.DataFrame;

/**
 * Created by Isak Karlsson on 15/08/14.
 */
public class RemoveIncompleteCases implements Transformer {

  @Override
  public Transformation fit(DataFrame dataFrame) {
    return new DoRemoveIncompleteCases();
  }

  private static final class DoRemoveIncompleteCases implements Transformation {

    @Override
    public DataFrame transform(DataFrame dataFrame) {
      DataFrame.Builder builder = dataFrame.newBuilder();
      for (int i = 0; i < dataFrame.rows(); i++) {
        boolean hasNA = false;
        for (int j = 0; j < dataFrame.columns(); j++) {
          if (dataFrame.isNA(i, j)) {
            hasNA = true;
            break;
          }
        }
        if (!hasNA) {
          for (int j = 0; j < dataFrame.columns(); j++) {
            builder.add(j, dataFrame, i, j);
          }
        }
      }
      return builder.create();
    }
  }
}
