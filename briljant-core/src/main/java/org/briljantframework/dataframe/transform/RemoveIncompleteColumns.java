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
import org.briljantframework.dataframe.Index;
import org.briljantframework.vector.Vector;

/**
 * Transformation that removes columns with missing values.
 *
 * <p> Given the DataFrame {@code x} equal to
 *
 * <pre>
 *    1  2 3
 *    NA 1 3
 *    1  2 3,
 * </pre>
 *
 * the DataFrame {@code m} equal to
 *
 * <pre>
 *     1  3 NA
 *     1  1 3
 *     2  2 2
 * </pre>
 *
 * {@code t.transform(m)} returns a new DataFrame
 *
 * <pre>
 *     1 3
 *     1 1
 *     2 2
 * </pre>
 *
 * and {@code t.transform(x)} a new data frame
 *
 * <pre>
 *     2 3
 *     1 3
 *     2 3
 * </pre>
 *
 * </p>
 *
 * @author Isak Karlsson
 */
public class RemoveIncompleteColumns implements Transformation {

  @Override
  public DataFrame transform(DataFrame x) {
    DataFrame.Builder builder = x.newBuilder();
    Index columnIndex = x.getColumnIndex();
    Index.Builder index = columnIndex.newBuilder();

    int j = 0;
    for (int i = 0; i < x.columns(); i++) {
      Vector column = x.get(i);
      if (!column.hasNA()) {
        index.add(columnIndex.get(i));
        builder.addColumn(column);
      }
    }
    DataFrame df = builder.build();
    df.setRecordIndex(x.getRecordIndex());
    df.setColumnIndex(index.build());
    return df;
  }
}
