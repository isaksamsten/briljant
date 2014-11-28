package org.briljantframework.learning.evaluation.result;

import java.util.List;

import org.briljantframework.chart.Chartable;
import org.briljantframework.learning.Predictions;
import org.briljantframework.vector.Vector;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Metrics are produced from evaluators to contain the performance of algorithms.
 * <p>
 * Created by isak on 02/10/14.
 */
public interface Metric extends Comparable<Metric>, Chartable {

  /**
   * Gets standard deviation.
   *
   * @return the standard deviation
   */
  default double getStandardDeviation() {
    return getStandardDeviation(Sample.OUT);
  }

  /**
   * Gets standard deviation.
   *
   * @param sample the sample
   * @return the standard deviation
   */
  double getStandardDeviation(Sample sample);

  /**
   * Gets min.
   *
   * @return the min
   */
  default double getMin() {
    return getMin(Sample.OUT);
  }

  /**
   * Gets min.
   *
   * @param out the out
   * @return the min
   */
  double getMin(Sample out);

  /**
   * Gets max.
   *
   * @return the max
   */
  default double getMax() {
    return getMax(Sample.OUT);
  }

  /**
   * Gets max.
   *
   * @param out the out
   * @return the max
   */
  double getMax(Sample out);

  /**
   * Get double.
   *
   * @param i the i
   * @return the double
   */
  default double get(int i) {
    return get(Sample.OUT, i);
  }

  /**
   * Get double.
   *
   * @param sample the sample
   * @param i the i
   * @return the double
   */
  double get(Sample sample, int i);

  /**
   * Get list.
   *
   * @return the list
   */
  default List<Double> get() {
    return get(Sample.OUT);
  }

  /**
   * Get list.
   *
   * @param sample the sample
   * @return the list
   */
  List<Double> get(Sample sample);

  /**
   * Size int.
   *
   * @return the int
   */
  int size();

  @Override
  default JFreeChart getChart() {
    return Chartable.create(getName(), getPlot());
  }

  @Override
  default Plot getPlot() {
    DefaultCategoryDataset dataset = new DefaultCategoryDataset();
    List<Double> outValues = get(Sample.OUT);
    List<Double> inValues = get(Sample.IN);

    String outSampleName = "Out-sample";
    String inSampleName = "In-sample";

    for (int i = 0; i < outValues.size(); i++) {
      dataset.addValue(outValues.get(i), outSampleName, String.valueOf(i));
      dataset.addValue(inValues.get(i), inSampleName, String.valueOf(i));
    }
    dataset.addValue(getAverage(Sample.OUT), outSampleName, "Average");
    dataset.addValue(getAverage(Sample.IN), inSampleName, "Average");
    NumberAxis numberAxis = new NumberAxis(getName());
    BarRenderer barRenderer = new BarRenderer();
    return new CategoryPlot(dataset, new CategoryAxis("Result"), numberAxis, barRenderer);
  }

  /**
   * Gets name.
   *
   * @return the name
   */
  String getName();

  /**
   * Gets mean.
   *
   * @param sample the sample
   * @return the mean
   */
  double getAverage(Sample sample);

  /**
   * Default order is descending order
   *
   * @param other metric
   * @return comparison
   */
  @Override
  default int compareTo(Metric other) {
    return Double.compare(other.getAverage(), getAverage());
  }

  /**
   * Gets average.
   *
   * @return the average
   */
  default double getAverage() {
    return getAverage(Sample.OUT);
  }


  /**
   * If a metric is calculated in or out of sample
   */
  public enum Sample {

    /**
     * Used to denote metrics calculated using the training sample
     */
    IN,

    /**
     * Used to denote metrics calculated out of the training sample
     */
    OUT
  }


  /**
   * Constructs a new metric producer.
   * <p>
   * Created by isak on 02/10/14.
   */
  interface Factory {

    /**
     * Create producer.
     *
     * @return the producer
     */
    Producer newProducer();
  }

  /**
   * Metrics can be produced either in sample (denoted by
   * {@link org.briljantframework.learning.evaluation.result.Metric .Sample#IN}) or out of sample
   * (denoted by {@link org.briljantframework.learning.evaluation.result.Metric.Sample#OUT})
   * <p>
   * Created by isak on 02/10/14.
   */
  interface Producer {

    /**
     * Add producer.
     *
     * @param sample the sample
     * @param predictions the predictions
     * @param column the target
     * @return the producer
     */
    public Producer add(Sample sample, Predictions predictions, Vector column);

    /**
     * Add producer.
     *
     * @param sample the sample
     * @param value the value
     * @return the producer
     */
    public Producer add(Sample sample, double value);

    /**
     * Gets performance metric.
     *
     * @return the performance metric
     */
    Metric produce();
  }
}
