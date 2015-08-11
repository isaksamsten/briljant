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

package org.briljantframework.dataseries;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.transform.Transformation;

import java.util.Objects;

/**
 * <p>
 * Transforms a DataFrame by, for each row, apply the injected
 * {@link org.briljantframework.dataseries.Aggregator} and reduce the dimensionality of the data
 * series.
 * </p>
 *
 * <p>
 * For example, a common choice is the Piecewise Aggregate Approximation which divides each time
 * series into {@code n} bins. Each bin is averaged to produce a new data series of a specified
 * length. Since this i very common transformation,
 * {@link org.briljantframework.dataseries.AggregateApproximation#AggregateApproximation(int)}
 * produces an aggregate approximation of length {@code size}.
 * </p>
 *
 * @author Isak Karlsson
 */
public class AggregateApproximation implements Transformation {

  private final Aggregator aggregator;

  /**
   * @param aggregator the aggregator
   */
  public AggregateApproximation(Aggregator aggregator) {
    this.aggregator = Objects.requireNonNull(aggregator, "Requires an aggregator.");
  }

  /**
   * Piecewise aggregate approximation (PAA) producing a new DataFrame with data series of length
   * {@code size}. Same as {@code new AggregateApproximation(new MeanAggregate(size))}.
   *
   * @param size the target size
   */
  public AggregateApproximation(int size) {
    this(new MeanAggregator(size));
  }

  /**
   * Gets the aggregator
   *
   * @return the aggregator
   */
  public Aggregator getAggregator() {
    return aggregator;
  }

  @Override
  public DataFrame transform(DataFrame x) {
    DataSeriesCollection.Builder builder =
        new DataSeriesCollection.Builder(aggregator.getAggregatedType());
    for (int i = 0; i < x.rows(); i++) {
      builder.addRecord(aggregator.partialAggregate(x.getRecord(i)));
    }
    return builder.build();
  }
}
