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

package org.briljantframework.evaluation.result;

import java.util.EnumMap;
import java.util.Map;

import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vectors;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractMeasure implements Measure {

  protected final Vector domain;
  protected final DoubleVector zeroVector;

  private final EnumMap<Sample, DoubleVector> values;
  private final EnumMap<Sample, Double> min, max, mean, std;

  protected AbstractMeasure(Builder builder) {
    this.values = new EnumMap<>(Sample.class);
    for (Map.Entry<Sample, DoubleVector.Builder> entry : builder.values.entrySet()) {
      values.put(entry.getKey(), entry.getValue().build());
    }
    this.max = builder.max;
    this.min = builder.min;
    this.domain = builder.getDomain();
    this.mean = builder.computeMean();
    this.std = builder.computeStandardDeviation(mean);
    this.zeroVector = Vectors.newDoubleNA(size());
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
    return values.getOrDefault(sample, zeroVector).get(i);
  }

  @Override
  public DoubleVector get(Sample sample) {
    return values.getOrDefault(sample, zeroVector);
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
  public Vector getDomain() {
    return domain;
  }

  @Override
  public double getMean(Sample sample) {
    return mean.getOrDefault(sample, 0d);
  }

  @Override
  public String toString() {
    return String.format("Average %s: %.4f (std: %.4f) (of %d value(s))", getName(), getMean(),
        getStandardDeviation(), size());
  }

  protected abstract static class Builder implements Measure.Builder {

    protected final EnumMap<Sample, DoubleVector.Builder> values = new EnumMap<>(Sample.class);
    protected final EnumMap<Sample, Double> max = new EnumMap<>(Sample.class);
    protected final EnumMap<Sample, Double> min = new EnumMap<>(Sample.class);
    protected final EnumMap<Sample, Double> sum = new EnumMap<>(Sample.class);

    private final Vector domain;

    protected Builder(Vector domain) {
      this.domain = domain;
    }

    public Vector getDomain() {
      return domain;
    }

    /**
     * Add value while maintaining the minimum and the maximum value so far. And the sum.
     *
     * @param value the value
     */
    protected void addComputedValue(Sample sample, double value) {
      sum.compute(sample, (k, v) -> v == null ? value : value + v);
      this.values.computeIfAbsent(sample, x -> new DoubleVector.Builder()).add(value);
    }

    /**
     * Compute mean.
     *
     * @return the enum map
     */
    protected EnumMap<Sample, Double> computeMean() {
      double inSum = sum.getOrDefault(Sample.IN, 0d);
      double outSum = sum.getOrDefault(Sample.OUT, 0d);
      DoubleVector.Builder inValues = values.get(Sample.IN);
      DoubleVector.Builder outValues = values.get(Sample.OUT);

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
    protected EnumMap<Sample, Double> computeStandardDeviation(EnumMap<Sample, Double> means) {
      EnumMap<Sample, Double> std = new EnumMap<>(Sample.class);

      for (Map.Entry<Sample, DoubleVector.Builder> e : values.entrySet()) {
        double mean = means.getOrDefault(e.getKey(), 0d);
        // List<Double> sampleValues = values.get(sample);
        // double stdAcc = 0.0;
        //
        // if (sampleValues != null && sampleValues.size() > 1) {
        // for (double value : sampleValues) {
        // stdAcc += (value - mean) * (value - mean);
        // }
        // stdAcc = Math.sqrt(stdAcc / (sampleValues.size() - 1));
        // }
        std.put(e.getKey(), Vectors.std(e.getValue().getTemporaryVector(), mean));
      }

      return std;
    }
  }

}
