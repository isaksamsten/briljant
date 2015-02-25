package org.briljantframework.classification.ensemble;

import org.briljantframework.classification.KNearestNeighbors;
import org.briljantframework.classification.RandomShapeletForest;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.Datasets;
import org.briljantframework.dataframe.MatrixDataFrame;
import org.briljantframework.distance.Euclidean;
import org.briljantframework.evaluation.ClassificationValidators;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.vector.Convert;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class EnsembleTest {

  @Test
  public void testFit() throws Exception {
    // DataFrame iris = Datasets.loadIris();
    //
    // iris = DataFrames.shuffle(iris);
    //
    // Vector y = iris.getColumn(iris.columns() - 1);
    // DataFrame x = iris.dropColumn(iris.columns() - 1);
    //
    // RandomForest forest = RandomForest.withSize(100).withMaximumFeatures(7).build();
    // ClassificationEvaluator cv = ClassificationEvaluators.crossValidation(10);
    // long start = System.currentTimeMillis();
    // System.out.println(cv.evaluate(forest, x, y));
    // System.out.println(System.currentTimeMillis() - start);
    //
    DataFrame synt = Datasets.loadSyntheticControl(MatrixDataFrame.HashBuilder::new);
    System.out.println(synt.getClass().getName());
    Vector ytrain = Convert.toStringVector(synt.getColumn(0));

    DataFrame xtrain = synt.dropColumn(0);

    System.out.println(xtrain);

    // System.exit(0);

    // synt =
    // Datasets.load(MixedDataFrame.Builder::new, MatlabTextInputStream::new,
    // "synthetic_control_TEST");
    // Vector ytest = As.stringVector(synt.getColumn(0));
    // DataFrame xtest = synt.dropColumn(0);
    //
    RandomShapeletForest f =
        RandomShapeletForest.withSize(100).withInspectedShapelets(100).withUpperLength(-1)
            .withLowerLength(2).build();
    //
    // Result result =
    // new HoldOutValidation(Measures.getDefaultClassificationMeasures(), xtest, ytest).evaluate(
    // f, xtrain, ytrain);
    // System.out.println(result);

    KNearestNeighbors knn =
        KNearestNeighbors.withNeighbors(1).withDistance(Euclidean.getInstance()).build();

    DataFrame syntheticControl = Datasets.loadSyntheticControl();
    Vector y = Convert.toStringVector(syntheticControl.getColumn(0));
    DataFrame x = syntheticControl.dropColumn(0);
    Result result = ClassificationValidators.crossValidation(5).test(f, x, y);
    System.out.println(result);

  }
}
