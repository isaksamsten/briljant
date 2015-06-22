package org.briljantframework.classification;

import org.briljantframework.Bj;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrames;
import org.briljantframework.dataframe.Datasets;
import org.briljantframework.evaluation.Validators;
import org.briljantframework.evaluation.measure.Accuracy;
import org.briljantframework.evaluation.result.Evaluator;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.matrix.IntArray;
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
    IntArray f = Bj.matrix(new int[]{10, 2, 3});
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
