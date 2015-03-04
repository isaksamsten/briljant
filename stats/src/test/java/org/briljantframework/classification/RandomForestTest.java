package org.briljantframework.classification;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrames;
import org.briljantframework.dataframe.Datasets;
import org.briljantframework.evaluation.ClassificationValidators;
import org.briljantframework.evaluation.measure.Accuracy;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.vector.Convert;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class RandomForestTest {
  @Test
  public void testFit() throws Exception {
    DataFrame iris = DataFrames.permuteRows(Datasets.loadIris());

    DataFrame x = iris.dropColumn(iris.columns() - 1);
    Vector y = Convert.toStringVector(iris.getColumn(iris.columns() - 1));

    IntMatrix f = IntMatrix.of(1, 2, 3);
    for (int i = 0; i < f.size(); i++) {
      RandomForest forest = RandomForest.withSize(1000).withMaximumFeatures(f.get(i)).build();
      Result result = ClassificationValidators.crossValidation(10).test(forest, x, y);
      System.out.println(result.getAverage(Ensemble.Correlation.class) + " "
          + result.getAverage(Ensemble.Strength.class) + " " + result.getAverage(Accuracy.class));
    }

  }
}
