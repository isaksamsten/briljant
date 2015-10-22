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
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.partition.Partitioner;
import org.briljantframework.supervised.Predictor;

/**
 * An Evaluator is used to evaluate an algorithm on a particular dataset
 * <p>
 * Created by Isak Karlsson on 20/08/14.
 */
public interface Validator<P extends Predictor> {

  /**
   * Evaluate {@code classifier} using the data {@code x} and {@code y}
   * 
   * @param learner classifier to use for classification
   * @param x the data frame to use during evaluation
   */
  Result<P> test(Predictor.Learner<? extends P> learner, DataFrame x, Vector y);

  /**
   * Add an evaluator to the validator for computing additional measures.
   * 
   * <pre>
   * Validator cv = Validators.crossValidation(10);
   * cv.add((ctx) -&gt; System.out.println(&quot;New round&quot;));
   * // For each fold, print &quot;New round&quot; to std-out
   * </pre>
   * 
   * @param evaluator the evaluator
   */
  void add(Evaluator<? super P> evaluator);

  /**
   * Gets the partitioner used for this validator. The partitioner partitions the data into training
   * and validation folds. For example,
   * {@link org.briljantframework.evaluation.partition.FoldPartitioner} partitions the data into
   * {@code k} folds and {@link org.briljantframework.evaluation.partition.SplitPartitioner}
   * partitions the data into one fold.
   * 
   * @return the partitioner used by this validator
   */
  Partitioner getPartitioner();
}
