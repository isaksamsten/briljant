package org.briljantframework.classification;

import static org.junit.Assert.assertEquals;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrames;
import org.briljantframework.dataframe.Datasets;
import org.briljantframework.evaluation.ClassificationEvaluators;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.vector.Convert;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class RandomForestTest {
  @Test
  public void testFit() throws Exception {
    DataFrame iris = DataFrames.permuteRows(Datasets.loadIris());
    assertEquals(5, iris.columns());

    DataFrame x = iris.dropColumn(4);
    Vector y = Convert.toStringVector(iris.getColumn(4));

    System.out.println(x);
    RandomForest f = RandomForest.withSize(100).build();
    Result result = ClassificationEvaluators.crossValidation(10).evaluate(f, x, y);
    System.out.println(result);
  }
}
