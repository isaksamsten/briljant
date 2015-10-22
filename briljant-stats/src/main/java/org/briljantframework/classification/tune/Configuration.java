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

import org.briljantframework.classification.Classifier;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.Result;
import org.briljantframework.supervised.Predictor;

/**
 * @author Isak Karlsson
 */
public class Configuration<P extends Predictor> {

  private final Classifier.Learner<? extends P> classifier;
  private final Result<? extends P> result;
  private final Vector parameters;

  public Configuration(Predictor.Learner<? extends P> classifier, Result<? extends P> result,
      Vector parameters) {
    this.classifier = classifier;
    this.result = result;
    this.parameters = parameters;
  }

  /**
   * Get the classifier configured according to the parameters
   *
   * @return the classifier
   */
  public Classifier.Learner<? extends P> getClassifier() {
    return classifier;
  }

  /**
   * Get the result of this configuration
   *
   * @return the result
   */
  public Result<? extends P> getResult() {
    return result;
  }

  public Vector getParameters() {
    return parameters;
  }

  @Override
  public String toString() {
    return result.toString();
  }

}
