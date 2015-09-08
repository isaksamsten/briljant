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

package org.briljantframework.evaluation.measure;

import org.briljantframework.data.Is;
import org.briljantframework.data.Na;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.Vectors;
import org.briljantframework.evaluation.result.Sample;

import java.util.EnumMap;
import java.util.Map;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractMeasure implements Measure {

  public static final double NA = Na.DOUBLE;
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
    this.naVector = Vector.singleton(NA, size());
    for (Map.Entry<Sample, Vector.Builder> entry : builder.values.entrySet()) {
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
    return values.getOrDefault(sample, naVector).loc().getAsDouble(i);
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
    return "Measure{name=" + getName() + ", mean=" + getMean() + "}";
  }

  protected abstract static class Builder<T extends Measure> implements Measure.Builder<T> {

    protected final EnumMap<Sample, Vector.Builder> values = new EnumMap<>(Sample.class);
    protected final EnumMap<Sample, Double> max = new EnumMap<>(Sample.class);
    protected final EnumMap<Sample, Double> min = new EnumMap<>(Sample.class);
    protected final EnumMap<Sample, Double> sum = new EnumMap<>(Sample.class);
    protected int size = 0;

    @Override
    public final void add(Sample sample, double value) {
      size++;
      sum.compute(sample, (k, v) -> v == null ? value : value + v);
      this.values.computeIfAbsent(sample, x -> Vector.Builder.of(Double.class)).add(value);
    }

    @Override
    public void add(Sample sample, Map<Object, Double> values) {
      add(sample, values.values().stream().mapToDouble(Double::doubleValue).average().orElse(0));
    }

    protected EnumMap<Sample, Double> computeMean() {
      double inSum = sum.getOrDefault(Sample.IN, Na.of(Double.class));
      double outSum = sum.getOrDefault(Sample.OUT, Na.of(Double.class));
      Vector.Builder inValues = values.get(Sample.IN);
      Vector.Builder outValues = values.get(Sample.OUT);

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

      for (Map.Entry<Sample, Vector.Builder> e : values.entrySet()) {
        double mean = means.getOrDefault(e.getKey(), Na.of(Double.class));
        if (Is.NA(mean)) {
          std.put(e.getKey(), mean);
        }
        std.put(e.getKey(), Vectors.std(e.getValue().getTemporaryVector(), mean));
      }

      return std;
    }
  }
}
