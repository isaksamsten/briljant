package org.briljantframework.dataseries;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.transform.Transformation;

import com.google.common.base.Preconditions;

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
    this.aggregator = Preconditions.checkNotNull(aggregator, "Requires an aggregator.");
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
