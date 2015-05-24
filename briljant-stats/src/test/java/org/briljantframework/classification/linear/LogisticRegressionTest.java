package org.briljantframework.classification.linear;

import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.LogisticRegression;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrames;
import org.briljantframework.dataframe.Datasets;
import org.briljantframework.evaluation.Validators;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class LogisticRegressionTest {

  @Test
  public void testLogisticRegression() throws Exception {
    DataFrame iris = DataFrames.permuteRows(Datasets.loadIris());
    DataFrame x = iris.drop("Class").apply(Double.class, v -> !Is.NA(v) ? v : 2);
    Vector y = iris.get("Class").transform(String.class, Boolean.class,
                                           v -> v.equals("Iris-setosa"));
    Classifier reg = LogisticRegression.withIterations(100)
        .withRegularization(1)
        .withLearningRate(0.001)
        .withCostEpsilon(0.0000001)
        .build();
    long start = System.nanoTime();
    Result result = Validators.crossValidation(10).test(reg, x, y);
    System.out.println((System.nanoTime() - start) / 1e6);
    System.out.println(result.toDataFrame().get("Error", "Fold", "Accuracy"));

  }
}
