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

package org.briljantframework.data.dataframe.transform;

import org.briljantframework.Check;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vectors;
import org.briljantframework.data.vector.Vector;

/**
 * @author Isak Karlsson
 */
public class MeanImputer implements Transformer {

  @Override
  public Transformation fit(DataFrame frame) {
//    DoubleArray means = Bj.doubleArray(frame.columns());
    Vector.Builder builder = Vector.Builder.of(Double.class);
    for (Object key : frame.getColumnIndex().keySet()) {
      builder.set(key, Vectors.mean(frame.get(key)));
    }
    Vector means = builder.build();
    return x -> {
      Check.size(x.columns(), means.size());
      DataFrame.Builder df = x.newBuilder();
      for (Object colKey : x.getColumnIndex().keySet()) {
        Vector column = frame.get(colKey);
        for (Object rowKey : x.getRecordIndex().keySet()) {
          if (column.isNA(rowKey)) {
            df.set(rowKey, colKey, means, colKey);
          } else {
            df.set(rowKey, colKey, column, rowKey);
          }
        }
      }
      return df.build();
//      for (int j = 0; j < x.columns(); j++) {
//        Check.type(x.loc().get(j).getType(), VectorType.DOUBLE);
//        for (int i = 1; i < x.rows(); i++) {
//          if (x.loc().isNA(i, j)) {
//            builder.loc().set(i, j, means.get(j));
//          } else {
//            builder.loc().set(i, j, x, i, j);
//          }
//        }
//      }
//      DataFrame df = builder.build();
//      df.setColumnIndex(columnIndex.build());
//      df.setRecordIndex(recordIndex.build());
//      return df;
    };
  }
}
