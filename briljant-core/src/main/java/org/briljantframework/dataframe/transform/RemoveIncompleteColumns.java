/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.dataframe.transform;


import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.index.Index;
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
      Vector column = x.loc().get(i);
      if (!column.hasNA()) {
        index.add(columnIndex.get(i));
        builder.add(column);
      }
    }
    DataFrame df = builder.build();
    df.setRecordIndex(x.getRecordIndex());
    df.setColumnIndex(index.build());
    return df;
  }
}
