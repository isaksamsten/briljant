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

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.exceptions.TypeMismatchException;
import org.briljantframework.data.Is;
import org.briljantframework.data.vector.VectorType;

/**
 * Class to fit a min max normalizer to a data frame. Calculate, for each column {@code j}, the min
 * <i>min</i><sub>j</sub> and max <i>max</i><sub>j</sub>. Then, for each value x<sub>i,j</sub> is
 * given by (x<sub>i,j</sub>-min<sub>j</sub>)/(max<sub>j</sub> - min<sub>j</sub>). This normalizes
 * the data frame in the range {@code [0, 1]} (under the assumption that min and max are
 * representative for the transformed dataframe).
 *
 * @author Isak Karlsson
 */
public class MinMaxNormalizer implements Transformer {


  @Override
  public Transformation fit(DataFrame frame) {
    DoubleArray min = Bj.doubleArray(frame.columns());
    DoubleArray max = Bj.doubleArray(frame.columns());
    for (int j = 0; j < frame.columns(); j++) {
      if (!frame.loc().get(j).getType().equals(VectorType.DOUBLE)) {
        throw new TypeMismatchException(VectorType.DOUBLE, frame.loc().get(j).getType());
      }

      double minTemp = Double.POSITIVE_INFINITY, maxTemp = Double.NEGATIVE_INFINITY;
      for (int i = 0; i < frame.rows(); i++) {
        double value = frame.loc().getAsDouble(i, j);
        if (Is.NA(value)) {
          continue;
        }
        if (value > maxTemp) {
          maxTemp = value;
        }

        if (value < minTemp) {
          minTemp = value;
        }
      }

      min.set(j, minTemp);
      max.set(j, maxTemp);
    }

    return x -> {
      Check.size(x.columns(), max.size());
      DataFrame.Builder builder = x.newBuilder();
      for (int j = 0; j < x.columns(); j++) {
        Check.type(x.loc().get(j).getType(), VectorType.DOUBLE);

        double mi = min.get(j);
        double ma = max.get(j);
        for (int i = 0; i < x.rows(); i++) {
          if (x.loc().isNA(i, j) || isSane(mi) || isSane(ma)) {
            builder.loc().setNA(i, j);
          } else {
            builder.loc().set(i, j, (x.loc().getAsDouble(i, j) - mi) / (ma - mi));
          }
        }
      }
      return builder.build();
    };
  }

  private static boolean isSane(double value) {
    return !Is.NA(value) && !Double.isNaN(value) && !Double.isInfinite(value);
  }

}
