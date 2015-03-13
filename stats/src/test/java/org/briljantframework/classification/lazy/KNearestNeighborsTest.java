package org.briljantframework.classification.lazy;

import org.briljantframework.classification.KNearestNeighbors;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.Datasets;
import org.briljantframework.distance.Euclidean;
import org.briljantframework.evaluation.Validators;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.vector.Vector;
import org.junit.Test;

public class KNearestNeighborsTest {

  @Test
  public void testClassifier() throws Exception {
    KNearestNeighbors oneNearestNeighbours =
        KNearestNeighbors.withNeighbors(4).withDistance(Euclidean.getInstance()).build();

    DataFrame iris = Datasets.loadIris();
    Vector y = iris.getColumn(4);
    DataFrame x = iris.removeColumn(4);

    Result res = Validators.crossValidation(10).test(oneNearestNeighbours, x, y);
    System.out.println(res);
    // ClassificationFrame train = DataSeriesInputStream.load(datasetPath + "TRAIN", 0,
    // Frame.FACTORY, DefaultTarget.FACTORY);
    // ClassificationFrame test = DataSeriesInputStream.load(datasetPath + "TEST", 0,
    // Frame.FACTORY, DefaultTarget.FACTORY);

    // System.out.println(train);
    // KNearestNeighbors.Model model = oneNearestNeighbours.fit(train);
    // Result result = Evaluators.holdOutValidation(oneNearestNeighbours, train, test);
    // System.out.println(result);
    //
    //
    // ZNormalizer<ClassificationFrame> normalizer = new ZNormalizer<>();
    // Transformation<ClassificationFrame> n = normalizer.fit(train);
    //
    // ClassificationFrame f = n.transform(train, ClassificationFrame.FACTORY);
    //
    //
    // Configurations<KNearestNeighbors> configurations =
    // Tuners.crossValidation(ClassificationFrame.FACTORY,
    // KNearestNeighbors.builder(), train, Comparator.naturalOrder(), 10,
    // Updaters.range("K", KNearestNeighbors.Builder::withNeighbors, 1, 10, 1)
    // );
    //
    // System.out.println(configurations);
    //
    //
    // ShapeletTree.Builder timeSeriesTree = ShapeletTree.withSplitter(RandomShapeletSplitter
    // .withDistance(EarlyAbandonSlidingDistance.create(Distance.EUCLIDEAN))
    // .withLowerLength(2)
    // .withInspectedShapelets(-1)
    // .withUpperLength(-1));
    //
    // Ensemble<Frame> ensemble = Ensemble.withMember(timeSeriesTree)
    // .withSampler(Bootstrap.create())
    // .withSize(100)
    // .create();
    //
    // long start = System.currentTimeMillis();
    // Ensemble.Model<Frame> model2 = ensemble.fit(train);
    // System.out.println("Fit: " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
    //
    //
    // Ensemble<Dataset> ensemble1 = Ensemble.withMember(DecisionTree.withSplitter(RandomSplitter
    // .withMaximumFeatures(2))).setRandomizer(Bootstrap.create()).create();
    // Ensemble.Model<Dataset> model2 = ensemble1.fit(train);
    // start = System.currentTimeMillis();
    // Predictions predictions = model2.predict(test.getDataset());
    // System.out.println("Predict: " + ((System.currentTimeMillis() - start) / 1000) + " seconds");
    // Target testTarget = test.getTarget();
    // int correct = 0;
    // for (int i = 0; i < predictions.size(); i++) {
    // Prediction p = predictions.get(i);
    // if (p.getValue().equals(testTarget.getValue(i))) {
    // correct += 1;
    // } else {
    // System.out.printf("%s for %s (real %s)%n", p.getPredictedProbability(), p.getValue(),
    // testTarget.getValue(i));
    // }
    // }
    // System.out.printf("Error: %.5f ", (1 - ((double) correct) / test.rows()));


  }
}
