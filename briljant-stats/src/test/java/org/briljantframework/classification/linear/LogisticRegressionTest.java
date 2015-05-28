package org.briljantframework.classification.linear;

import org.briljantframework.Utils;
import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.LogisticRegression;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrames;
import org.briljantframework.dataframe.Datasets;
import org.briljantframework.evaluation.Validators;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.evaluation.result.Sample;
import org.briljantframework.function.Aggregates;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class LogisticRegressionTest {

  @Test
  public void testLogisticRegression() throws Exception {
    Utils.setRandomSeed(102);
    DataFrame iris = DataFrames.permuteRows(Datasets.loadIris());
    DataFrame x = iris.drop("Class").apply(Double.class, v -> !Is.NA(v) ? v : 0);
    Vector y = iris.get("Class");
    Classifier reg = LogisticRegression.withIterations(500)
        .withRegularization(6)
        .build();

//    reg = RandomForest.withSize(100).withMaximumFeatures(1).build();
    System.out.println(reg);
    long start = System.nanoTime();
    Result result = Validators.crossValidation(10).test(reg, x, y);
    System.out.println((System.nanoTime() - start) / 1e6);
//    System.out.println(result.toDataFrame());
    System.out.println(
        result.toDataFrame()
            .groupBy("Sample")
            .get(Sample.IN)
            .aggregate(Double.class, Aggregates.mean()));

  }
}
