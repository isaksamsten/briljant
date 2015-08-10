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
