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

package org.briljantframework.evaluation.measure;

import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.result.Sample;

/**
 * @author Isak Karlsson
 */
public interface ClassMeasure {

  default Vector get(String value) {
    return get(Sample.OUT, value);
  }

  Vector get(Sample sample, String value);

  default double getAverage(String value) {
    return getAverage(Sample.OUT, value);
  }

  double getAverage(Sample sample, String value);

  default double getStandardDeviation(String value) {
    return getStandardDeviation(Sample.OUT, value);
  }

  double getStandardDeviation(Sample sample, String value);

  default double getMin(String value) {
    return getMin(Sample.OUT, value);
  }

  double getMin(Sample out, String value);

  default double getMax(String value) {
    return getMax(Sample.OUT, value);
  }

  double getMax(Sample out, String value);
}
