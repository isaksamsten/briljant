/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package org.briljantframework.data.dataframe.transform;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.briljantframework.Check;
import org.briljantframework.data.Is;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;

/**
 * Class to fit a min max normalizer to a data frame. Calculate, for each column {@code j}, the min
 * <i>min</i><minus>j</minus> and max <i>max</i><minus>j</minus>. Then, for each value x
 * <minus>i,j</minus> is given by (x<minus>i,j</minus>-min<minus>j</minus>)/(max<minus>j</minus> -
 * min<minus>j</minus>). This normalizes the data frame in the range {@code [0, 1]} (under the
 * assumption that min and max are representative for the transformed dataframe).
 *
 * @author Isak Karlsson
 */
public class MinMaxNormalizer implements Transformation {


  @Override
  public Transformer fit(DataFrame df) {
    Vector.Builder min = Vector.Builder.of(Double.class);
    Vector.Builder max = Vector.Builder.of(Double.class);
    for (Object columnKey : df) {
      StatisticalSummary summary = df.get(columnKey).statisticalSummary();
      min.set(columnKey, summary.getMin());
      max.set(columnKey, summary.getMax());
    }

    return new MinMaxNormalizeTransformer(max.build(), min.build());
  }

  @Override
  public String toString() {
    return "MinMaxNormalizer";
  }

  private static class MinMaxNormalizeTransformer implements Transformer {

    private final Vector max;
    private final Vector min;

    public MinMaxNormalizeTransformer(Vector max, Vector min) {
      this.max = max;
      this.min = min;
    }

    @Override
    public DataFrame transform(DataFrame x) {
      Check.argument(max.getIndex().equals(x.getColumnIndex()), "Index does not match");
      DataFrame.Builder builder = x.newBuilder();
      for (Object columnKey : x) {
        double min = this.min.getAsDouble(columnKey);
        double max = this.max.getAsDouble(columnKey);
        Vector.Builder normalized = Vector.Builder.of(Double.class);
        Vector column = x.get(columnKey);
        for (int i = 0, size = column.size(); i < size; i++) {
          double v = column.loc().getAsDouble(i);
          if (Is.NA(v)) {
            normalized.addNA();
          } else {
            normalized.add((column.loc().getAsDouble(i) - min) / (max - min));
          }
        }
        builder.set(columnKey, normalized);
      }
      return builder.setIndex(x.getIndex()).build();
    }

    @Override
    public String toString() {
      return "MinMaxNormalizeTransformer{" + "max=" + max + ", min=" + min + '}';
    }
  }
}
