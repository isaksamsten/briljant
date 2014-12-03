package org.briljantframework.classification.ensemble;

import org.briljantframework.classification.RandomForest;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrames;
import org.briljantframework.dataframe.Datasets;
import org.briljantframework.evaluation.Evaluator;
import org.briljantframework.evaluation.Evaluators;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class AbstractEnsembleTest {

  @Test
  public void testFit() throws Exception {
    DataFrame iris = Datasets.loadConnect4();

    iris = DataFrames.shuffle(iris);

    Vector y = iris.getColumn(iris.columns() - 1);
    DataFrame x = iris.dropColumn(iris.columns() - 1);

    RandomForest forest = RandomForest.withSize(100).withMaximumFeatures(7).build();
    Evaluator cv = Evaluators.crossValidation(10);
    long start = System.currentTimeMillis();
    System.out.println(cv.evaluate(forest, x, y));
    System.out.println(System.currentTimeMillis() - start);

    // DataFrame synt = Datasets.loadSyntheticControl();
    // Vector ytrain = synt.getColumn(0);
    // DataFrame xtrain = synt.dropColumn(0);
    //
    // synt = Datasets.load(MixedDataFrame.Builder::new, MatlabTextInputStream::new,
    // "synthetic_control_TEST");
    // Vector ytest = synt.getColumn(0);
    // DataFrame xtest = synt.dropColumn(0);
    //
    // RandomShapeletForest f = RandomShapeletForest
    // .withSize(100)
    // .withInspectedShapelets(100)
    // .withUpperLength(-1)
    // .withLowerLength(2)
    // .build();
    // Result result = new HoldOutValidation(xtest, ytest).evaluate(f, xtrain, ytrain);
    // System.out.println(result);
  }
}
