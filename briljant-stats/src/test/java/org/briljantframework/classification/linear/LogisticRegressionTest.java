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

package org.briljantframework.classification.linear;

import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.LogisticRegression;
import org.briljantframework.data.Is;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.dataframe.DataFrames;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.dataset.io.Datasets;
import org.briljantframework.evaluation.Validators;
import org.briljantframework.evaluation.result.Result;
import org.junit.Test;

public class LogisticRegressionTest {

  @Test
  public void testLogisticRegression() throws Exception {
    DataFrame iris = DataFrames.permuteRecords(Datasets.loadIris());
    DataFrame x = iris.drop("Class").map(Double.class, v -> !Is.NA(v) ? v : 0);
    Vector y = iris.get("Class");// .satisfies(String.class, v -> v.equals("Iris-setosa"));
    Classifier.Learner classifier =
        new LogisticRegression.Configurator(500).setRegularization(0.01).configure();
    LogisticRegression model = (LogisticRegression) classifier.fit(x, y);

    System.out.println(model.getOddsRatio("(Intercept)"));
    for (Object o : x.getColumnIndex().keySet()) {
      System.out.println(model.getOddsRatio(o));
    }

    // reg = RandomForest.withSize(100).withMaximumFeatures(1).build();
    System.out.println(classifier);
    long start = System.nanoTime();
    Result result = Validators.crossValidation(10).test(classifier, x, y);
    System.out.println((System.nanoTime() - start) / 1e6);
    System.out.println(result);
  }

  @Test
  public void testOdds() throws Exception {
    DataFrame x = DataFrame.of("Age", Vector.of(55, 28, 65, 46, 86, 56, 85, 33, 21, 42), "Smoker",
                               Vector.of(0, 0, 1, 0, 1, 1, 0, 0, 1, 1));
    Vector y = Vector.of(0, 0, 0, 1, 1, 1, 0, 0, 0, 1);
    System.out.println(x);

    LogisticRegression.Learner regression = new LogisticRegression.Learner();
    LogisticRegression model = regression.fit(x, y);
    System.out.println(model);

    System.out.println("(Intercept) " + model.getOddsRatio("(Intercept)"));
    for (Object o : x.getColumnIndex().keySet()) {
      System.out.println(o + " " + model.getOddsRatio(o));
    }
  }
}
