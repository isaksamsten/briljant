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

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.Index;
import org.briljantframework.vector.Vec;
import org.briljantframework.vector.VectorType;

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
      Index.Builder columnIndex = x.getColumnIndex().newBuilder();
      Index.Builder recordIndex = x.getRecordIndex().newBuilder();

      x.getColumnIndex().entrySet().forEach(columnIndex::set);
      x.getRecordIndex().entrySet().forEach(recordIndex::set);
      for (int j = 0; j < x.columns(); j++) {
        Check.type(x.getType(j), VectorType.DOUBLE);
        for (int i = 1; i < x.rows(); i++) {
          if (x.isNA(i, j)) {
            builder.set(i, j, means.get(j));
          } else {
            builder.set(i, j, x, i, j);
          }
        }
      }
      DataFrame df = builder.build();
      df.setColumnIndex(columnIndex.build());
      df.setRecordIndex(recordIndex.build());
      return df;
    };
  }
}
