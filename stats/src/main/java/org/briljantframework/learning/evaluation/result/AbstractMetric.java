/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.learning.evaluation.result;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

/**
 * Created by Isak Karlsson on 06/10/14.
 */
public abstract class AbstractMetric implements Metric {

  private final EnumMap<Sample, List<Double>> values;
  private final EnumMap<Sample, Double> min, max, mean, std;

  /**
   * Instantiates a new Abstract metric.
   *
   * @param producer the producer
   */
  protected AbstractMetric(Producer producer) {
    this.values = producer.values;
    this.max = producer.max;
    this.min = producer.min;
    this.mean = producer.computeMean();
    this.std = producer.computeStandardDeviation(mean);
  }

  @Override
  public double getStandardDeviation(Sample sample) {
    return std.getOrDefault(sample, 0d);
  }

  @Override
  public double getMin(Sample sample) {
    return min.getOrDefault(sample, 0d);
  }

  @Override
  public double getMax(Sample sample) {
    return max.getOrDefault(sample, 0d);
  }

  @Override
  public double get(Sample sample, int i) {
    return values.containsKey(sample) ? values.get(sample).get(i) : 0;
  }

  @Override
  public List<Double> get(Sample sample) {
    return values.get(sample);
  }

  @Override
  public int size() {
    if (values.size() > 0) {
      return values.get(Sample.OUT).size();
    } else {
      return 0;
    }
  }

  @Override
  public double getAverage(Sample sample) {
    return mean.getOrDefault(sample, 0d);
  }

  @Override
  public String toString() {
    return String.format("Average %s: %.4f (std: %.4f) (of %d value(s))", getName(), getAverage(),
        getStandardDeviation(), size());
  }

  /**
   * The type Producer.
   */
  protected abstract static class Producer implements Metric.Producer {

    /**
     * The Values.
     */
    protected final EnumMap<Sample, List<Double>> values = new EnumMap<>(Sample.class);

    /**
     * The Max.
     */
    protected final EnumMap<Sample, Double> max = new EnumMap<>(Sample.class);

    /**
     * The Min.
     */
    protected final EnumMap<Sample, Double> min = new EnumMap<>(Sample.class);

    /**
     * The Sum.
     */
    protected final EnumMap<Sample, Double> sum = new EnumMap<>(Sample.class);

    /**
     * Add value while maintaining the minimum and the maximum value so far. And the sum.
     *
     * @param value the value
     */
    public Producer add(Sample sample, double value) {
      sum.compute(sample, (k, v) -> v == null ? value : value + v);
      List<Double> values = this.values.get(sample);
      if (values == null) {
        values = new ArrayList<>();
        this.values.put(sample, values);
      }
      values.add(value);

      return this;
    }

    /**
     * Compute mean.
     *
     * @return the enum map
     */
    public EnumMap<Sample, Double> computeMean() {
      double inSum = sum.getOrDefault(Sample.IN, 0d);
      double outSum = sum.getOrDefault(Sample.OUT, 0d);
      List<Double> inValues = values.get(Sample.IN);
      List<Double> outValues = values.get(Sample.OUT);

      EnumMap<Sample, Double> mean = new EnumMap<>(Sample.class);
      if (inValues != null && inValues.size() > 0) {
        mean.put(Sample.IN, inSum / inValues.size());
      }

      if (outValues != null && outValues.size() > 0) {
        mean.put(Sample.OUT, outSum / outValues.size());
      }

      return mean;
    }

    /**
     * Compute standard deviation.
     *
     * @param means the means
     * @return the enum map
     */
    public EnumMap<Sample, Double> computeStandardDeviation(EnumMap<Sample, Double> means) {
      EnumMap<Sample, Double> std = new EnumMap<>(Sample.class);

      for (Sample sample : Sample.values()) {
        double mean = means.getOrDefault(sample, 0d);
        List<Double> sampleValues = values.get(sample);
        double stdAcc = 0.0;

        if (sampleValues != null && sampleValues.size() > 1) {
          for (double value : sampleValues) {
            stdAcc += (value - mean) * (value - mean);
          }
          stdAcc = Math.sqrt(stdAcc / (sampleValues.size() - 1));
        }
        std.put(sample, stdAcc);
      }

      return std;
    }
  }

}
