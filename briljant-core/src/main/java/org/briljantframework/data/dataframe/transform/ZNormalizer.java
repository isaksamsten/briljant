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

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.briljantframework.Check;
import org.briljantframework.data.Is;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Vectors;

/**
 * Z normalization is also known as "Normalization to Zero Mean and Unit of Energy" first mentioned
 * in Goldin & Kanellakis. It ensures that all elements of the input vector are transformed
 * into the output vector whose mean is approximately 0 while the standard deviation are in a range
 * close to 1.
 *
 * @author Isak Karlsson
 */
public class ZNormalizer implements Transformation {

  @Override
  public Transformer fit(DataFrame df) {
    Vector.Builder meanBuilder = Vector.Builder.of(Double.class);
    Vector.Builder stdBuilder = Vector.Builder.of(Double.class);
    for (Object columnKey : df) {
      StatisticalSummary stats = Vectors.statisticalSummary(df.get(columnKey));
      if (stats.getN() <= 0 || Is.NA(stats.getMean()) || Is.NA(stats.getStandardDeviation())) {
        throw new IllegalArgumentException("Illegal value for column " + columnKey);
      }
      meanBuilder.set(columnKey, stats.getMean());
      stdBuilder.set(columnKey, stats.getStandardDeviation());
    }
    Vector mean = meanBuilder.build();
    Vector sigma = stdBuilder.build();

    return new ZNormalizerTransformer(mean, sigma);
  }

  @Override
  public String toString() {
    return "ZNormalizer";
  }

  private static class ZNormalizerTransformer implements Transformer {

    private final Vector mean;
    private final Vector sigma;

    public ZNormalizerTransformer(Vector mean, Vector sigma) {
      this.mean = mean;
      this.sigma = sigma;
    }

    @Override
    public DataFrame transform(DataFrame x) {
      Check.argument(mean.getIndex().equals(x.getColumnIndex()), "Columns must match.");
      DataFrame.Builder builder = x.newBuilder();
      for (Object columnKey : x) {
        Vector column = x.get(columnKey);
        double m = mean.getAsDouble(columnKey);
        double std = sigma.getAsDouble(columnKey);
        Vector.Builder normalized = column.newBuilder(column.size());
        for (int i = 0, size = column.size(); i < size; i++) {
          double v = column.loc().getAsDouble(i);
          if (Is.NA(v)) {
            normalized.addNA();
          } else if (std == 0) {
            normalized.add(0);
          } else {
            normalized.add((v - m) / std);
          }
        }
        builder.set(columnKey, normalized);
      }
      return builder.setIndex(x.getIndex()).build();
    }

    @Override
    public String toString() {
      return "ZNormalizerTransformer{" +
             "mean=" + mean +
             ", sigma=" + sigma +
             '}';
    }
  }
}
