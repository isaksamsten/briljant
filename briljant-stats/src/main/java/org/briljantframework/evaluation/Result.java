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

package org.briljantframework.evaluation;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.dataframe.DataFrames;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.supervised.Predictor;

/**
 * @author Isak Karlsson
 */
public class Result<P extends Predictor> {

  private final DataFrame measures;
  private final Vector predictions;
  private final Vector actual;

  public Result(EvaluationContext<? extends P> ctx, Vector t, Vector p) {
    this.measures = ctx.getMeasureCollection().toDataFrame();
    this.actual = t;
    this.predictions = p;
  }

  public DataFrame getMeasures() {
    return measures;
  }

  public Vector getMeasure(PredictionMeasure<? super P> measure) {
    return measures.get(measure);
  }

  public Vector getPredictions() {
    return predictions;
  }

  public Vector getActual() {
    return actual;
  }

  public DataFrame getConfusionMatrix() {
    return DataFrames.table(actual, predictions);
  }

  @Override
  public String toString() {
    return getMeasures().toString();
  }
}
