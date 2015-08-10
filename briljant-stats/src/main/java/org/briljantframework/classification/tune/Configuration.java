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

package org.briljantframework.classification.tune;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import org.briljantframework.classification.Classifier;
import org.briljantframework.evaluation.measure.Measure;
import org.briljantframework.evaluation.result.ConfusionMatrix;
import org.briljantframework.evaluation.result.Result;

import com.google.common.base.Strings;

/**
 * The type Optimization result.
 */
public class Configuration implements Comparable<Configuration> {
  private final Classifier classifier;
  private final Result result;

  private final Map<String, Object> parameters;

  /**
   * Instantiates a new Optimization result.
   *
   * @param classifier the classifier
   * @param result the error
   * @param parameters the parameters
   */
  public Configuration(Classifier classifier, Result result, Map<String, Object> parameters) {
    this.classifier = classifier;
    this.result = result;
    this.parameters = parameters;
  }

  /**
   * Metric comparator.
   *
   * @param <T> the type parameter
   * @param metric the metric
   * @return the comparator
   */
  public static <T extends Measure> Comparator<Configuration> metricComparator(Class<T> metric) {
    return (o1, o2) -> o1.getMetric(metric).compareTo(o2.getMetric(metric));
  }

  /**
   * Create optimization result.
   *
   * @param classifier the classifier
   * @param error the error
   * @param map the map
   * @return the optimization result
   */
  public static Configuration create(Classifier classifier, Result error, Map<String, Object> map) {
    return new Configuration(classifier, error, map);
  }

  /**
   * Gets classifier.
   *
   * @return the classifier
   */
  public Classifier getClassifier() {
    return classifier;
  }

  /**
   * Gets error.
   *
   * @return the error
   */
  public double getError() {
    return result.getAverageError();
  }

  /**
   * Gets result.
   *
   * @return the result
   */
  public Result getResult() {
    return result;
  }

  /**
   * Gets average confusion matrix.
   *
   * @return the average confusion matrix
   */
  public ConfusionMatrix getAverageConfusionMatrix() {
    return result.getAverageConfusionMatrix();
  }

  /**
   * Gets average.
   *
   * @param key the key
   * @return the average
   */
  public double getAverage(Class<? extends Measure> key) {
    return result.getAverage(key);
  }

  /**
   * Gets standard deviation.
   *
   * @param key the key
   * @return the standard deviation
   */
  public double getStandardDeviation(Class<? extends Measure> key) {
    return result.getStandardDeviation(key);
  }

  /**
   * Get t.
   *
   * @param key the key
   * @return the t
   */
  public <T extends Measure> T getMetric(Class<T> key) {
    return result.get(key);
  }

  /**
   * Get object.
   *
   * @param key the key
   * @return the object
   */
  public Object get(String key) {
    return parameters.get(key);
  }

  /**
   * Keys set.
   *
   * @return the set
   */
  public Set<String> keys() {
    return parameters.keySet();
  }

  /**
   * Values collection.
   *
   * @return the collection
   */
  public Collection<Object> values() {
    return parameters.values();
  }

  /**
   * Gets parameters.
   *
   * @return the parameters
   */
  public Set<Map.Entry<String, Object>> entries() {
    return parameters.entrySet();
  }

  @Override
  public int compareTo(Configuration o) {
    return Double.compare(getError(), o.getError());
  }

  @Override
  public String toString() {
    StringBuilder out = new StringBuilder();
    out.append(getClassifier().toString()).append("\n\n");
    int longestParameter = entries().stream().mapToInt(x -> x.getKey().length()).max().getAsInt();
    if (longestParameter < 9) {
      longestParameter = 9;
    }

    out.append("Settings\n").append("Parameter")
        .append(Strings.repeat(" ", longestParameter > 12 ? longestParameter - 9 : 4))
        .append("Value\n");
    for (Map.Entry<String, Object> kv : entries()) {
      out.append("").append(kv.getKey())
          .append(Strings.repeat(" ", (longestParameter - kv.getKey().length()) + 4))
          .append(kv.getValue()).append("\n");
    }
    out.append("\n");
    out.append(getResult());
    return out.toString();
    // return String.format("Configuration(%s, %.2f, %s)", classifier, result.getAverageError(),
    // parameters);
  }
}
