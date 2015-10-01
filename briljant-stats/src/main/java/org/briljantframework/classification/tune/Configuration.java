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

package org.briljantframework.classification.tune;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Set;

import org.briljantframework.classification.Classifier;
import org.briljantframework.evaluation.measure.Measure;
import org.briljantframework.evaluation.result.Result;

/**
 * @author Isak Karlsson
 */
public class Configuration implements Comparable<Configuration> {

  private final Classifier classifier;
  private final Result result;

  private final Map<String, Object> parameters;

  public Configuration(Classifier classifier, Result result, Map<String, Object> parameters) {
    this.classifier = classifier;
    this.result = result;
    this.parameters = parameters;
  }

  public static <T extends Measure> Comparator<Configuration> measureComparator(Class<T> measure) {
    return (o1, o2) -> o1.getResult().get(measure).compareTo(o2.getResult().get(measure));
  }

  /**
   * Get the classifier configured according to the parameters
   *
   * @return the classifier
   */
  public Classifier getClassifier() {
    return classifier;
  }

  /**
   * Get the average error of this configuration
   *
   * @return the error
   * @see Result#getAverageError()
   */
  public double getAverageError() {
    return result.getAverageError();
  }

  /**
   * Get the result of this configuration
   *
   * @return the result
   */
  public Result getResult() {
    return result;
  }

  /**
   * Get the value for the parameter with the supplied key
   *
   * @param key the key
   * @return the object
   */
  public Object getParameterValue(String key) {
    return parameters.get(key);
  }

  /**
   * Get the parameter names
   *
   * @return a set of parameter names
   */
  public Set<String> getParameters() {
    return parameters.keySet();
  }

  /**
   * Values collection.
   *
   * @return the collection
   */
  public Collection<Object> getParameterValues() {
    return parameters.values();
  }

  /**
   * Gets parameters.
   *
   * @return the parameters
   */
  public Set<Map.Entry<String, Object>> parameterEntrySet() {
    return parameters.entrySet();
  }

  @Override
  public int compareTo(Configuration o) {
    return Double.compare(getAverageError(), o.getAverageError());
  }
}
