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

import org.briljantframework.evaluation.result.Sample;
import org.briljantframework.data.vector.DoubleVector;
import org.briljantframework.data.vector.Vectors;
import org.briljantframework.data.vector.Vector;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractClassMeasure extends AbstractMeasure implements ClassMeasure {

  protected final EnumMap<Sample, Map<Object, Vector>> valueForValue;

  protected AbstractClassMeasure(Builder<? extends Measure> producer) {
    super(producer);
    this.valueForValue = new EnumMap<>(Sample.class);
    for (Map.Entry<Sample, Map<Object, Vector.Builder>> e : producer.sampleMetricValues
        .entrySet()) {
      Map<Object, Vector> values = new HashMap<>();
      for (Map.Entry<Object, Vector.Builder> ve : e.getValue().entrySet()) {
        values.put(ve.getKey(), ve.getValue().build());
      }
      valueForValue.put(e.getKey(), values);
    }
  }

  @Override
  public Vector get(Sample sample, String value) {
    return valueForValue.get(sample).getOrDefault(value, naVector);
  }

  @Override
  public double getAverage(Sample sample, String value) {
    return Vectors.mean(get(sample, value));
  }

  @Override
  public double getStandardDeviation(Sample sample, String value) {
    double mean = getAverage(sample, value);
    return Vectors.std(get(sample, value), mean);
  }

  @Override
  public double getMin(Sample sample, String value) {
    return get(sample, value).stream(Number.class).mapToDouble(Number::doubleValue).min().orElse(0);
  }

  @Override
  public double getMax(Sample sample, String value) {
    return get(sample, value).stream(Number.class).mapToDouble(Number::doubleValue).min().orElse(0);
  }

  protected static abstract class Builder<T extends Measure> extends AbstractMeasure.Builder<T> {

    protected final EnumMap<Sample, Map<Object, Vector.Builder>> sampleMetricValues =
        new EnumMap<>(Sample.class);

    /**
     * Adds values for each value. Callers must ensure that an average value is added using {@link
     * #add(org.briljantframework.evaluation.result.Sample, double)}
     *
     * @param sample the sample
     * @param values the values
     */
    public void add(Sample sample, Map<Object, Double> values) {
      Map<Object, Vector.Builder> all =
          sampleMetricValues.computeIfAbsent(sample, x -> new HashMap<>());
      for (Map.Entry<Object, Double> entry : values.entrySet()) {
        all.computeIfAbsent(entry.getKey(), x -> new DoubleVector.Builder()).add(
            entry.getValue());
      }
      sampleMetricValues.put(sample, all);
    }
  }
}
