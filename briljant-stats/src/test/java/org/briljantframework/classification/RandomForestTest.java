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

package org.briljantframework.classification;

import org.briljantframework.Bj;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrames;
import org.briljantframework.dataframe.Datasets;
import org.briljantframework.evaluation.Validators;
import org.briljantframework.evaluation.measure.Accuracy;
import org.briljantframework.evaluation.result.Evaluator;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.array.IntArray;
import org.briljantframework.vector.Convert;
import org.briljantframework.vector.Vector;
import org.junit.Test;

import java.util.List;

public class RandomForestTest {

  @Test
  public void testFit() throws Exception {
    DataFrame iris = DataFrames.permuteRows(Datasets.loadConnect4());
    DataFrame x = iris.drop(iris.columns() - 1);
    Vector y = Convert.toStringVector(iris.get(iris.columns() - 1));

    System.out.println(x);
    IntArray f = Bj.array(new int[]{10, 2, 3});
    for (int i = 0; i < f.size(); i++) {
      long start = System.nanoTime();
      RandomForest forest = RandomForest.withSize(100).withMaximumFeatures(f.get(i)).build();
      List<Evaluator> evaluators = Evaluator.getDefaultClassificationEvaluators();
      evaluators.add(Evaluator.foldOutput(System.out::println));
      Result result = Validators.crossValidation(evaluators, 10).test(forest, x, y);
      System.out.println((System.nanoTime() - start) / 1e6);
      System.out.println(
          result.getAverage(Ensemble.Correlation.class) + " "
          + result.getAverage(Ensemble.Strength.class) + " "
          + result.getAverage(Accuracy.class));
    }

  }
}
