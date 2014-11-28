package org.briljantframework.learning.lazy;

import org.briljantframework.matrix.distance.Distance;
import org.briljantframework.matrix.time.DynamicTimeWarping;
import org.junit.Test;

public class KNearestNeighborsTest {

  @Test
  public void testClassifier() throws Exception {
    KNearestNeighbors oneNearestNeighbours =
        KNearestNeighbors.withNeighbors(1)
            .withDistance(DynamicTimeWarping.withDistance(Distance.EUCLIDEAN).withConstraint(1))
            .create();

    String name = "Coffee";
    String datasetPath = String.format("/Users/isak/Downloads/dataset/%s/%s_", name, name);

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
    // System.out.printf("%s for %s (real %s)%n", p.getProbability(), p.getValue(),
    // testTarget.getValue(i));
    // }
    // }
    // System.out.printf("Error: %.5f ", (1 - ((double) correct) / test.rows()));


  }
}
