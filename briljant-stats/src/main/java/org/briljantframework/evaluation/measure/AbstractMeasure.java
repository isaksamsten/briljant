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

package org.briljantframework.evaluation.measure;

import org.briljantframework.evaluation.result.Sample;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Na;
import org.briljantframework.vector.Vec;
import org.briljantframework.vector.Vector;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractMeasure implements Measure {

  public static final double NA = Na.of(Double.class);
  protected final Vector naVector;

  private final EnumMap<Sample, Vector> values;
  private final EnumMap<Sample, Double> min, max, mean, std;
  private final int size;

  protected AbstractMeasure(Builder<? extends Measure> builder) {
    this.values = new EnumMap<>(Sample.class);
    this.max = builder.max;
    this.min = builder.min;
    this.mean = builder.computeMean();
    this.std = builder.computeStandardDeviation(mean);
    this.size = builder.size;
    this.naVector = Vector.singleton(Na.of(Double.class), size());
    for (Map.Entry<Sample, DoubleVector.Builder> entry : builder.values.entrySet()) {
      values.put(entry.getKey(), entry.getValue().build());
    }
  }

  @Override
  public double getStandardDeviation(Sample sample) {
    return std.getOrDefault(sample, NA);
  }

  @Override
  public double getMin(Sample sample) {
    return min.getOrDefault(sample, NA);
  }

  @Override
  public double getMax(Sample sample) {
    return max.getOrDefault(sample, NA);
  }

  @Override
  public double get(Sample sample, int i) {
    return values.getOrDefault(sample, naVector).getAsDouble(i);
  }

  @Override
  public Vector get(Sample sample) {
    return values.getOrDefault(sample, naVector);
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public double getMean(Sample sample) {
    return mean.getOrDefault(sample, NA);
  }

  @Override
  public String toString() {
    return String.format("Average %s: %.4f (std: %.4f) (of %d value(s))", getName(), getMean(),
                         getStandardDeviation(), size());
  }

  protected abstract static class Builder<T extends Measure> implements Measure.Builder<T> {

    protected final EnumMap<Sample, DoubleVector.Builder> values = new EnumMap<>(Sample.class);
    protected final EnumMap<Sample, Double> max = new EnumMap<>(Sample.class);
    protected final EnumMap<Sample, Double> min = new EnumMap<>(Sample.class);
    protected final EnumMap<Sample, Double> sum = new EnumMap<>(Sample.class);
    protected int size = 0;

    @Override
    public final void add(Sample sample, double value) {
      size++;
      sum.compute(sample, (k, v) -> v == null ? value : value + v);
      this.values.computeIfAbsent(sample, x -> new DoubleVector.Builder()).add(value);
    }

    @Override
    public void add(Sample sample, Map<Object, Double> values) {
      add(sample, values.values().stream().mapToDouble(Double::doubleValue).average().orElse(0));
    }

    protected EnumMap<Sample, Double> computeMean() {
      double inSum = sum.getOrDefault(Sample.IN, Na.of(Double.class));
      double outSum = sum.getOrDefault(Sample.OUT, Na.of(Double.class));
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

    protected EnumMap<Sample, Double> computeStandardDeviation(EnumMap<Sample, Double> means) {
      EnumMap<Sample, Double> std = new EnumMap<>(Sample.class);

      for (Map.Entry<Sample, DoubleVector.Builder> e : values.entrySet()) {
        double mean = means.getOrDefault(e.getKey(), Na.of(Double.class));
        if (Is.NA(mean)) {
          std.put(e.getKey(), mean);
        }
        std.put(e.getKey(), Vec.std(e.getValue().getTemporaryVector(), mean));
      }

      return std;
    }
  }
}
