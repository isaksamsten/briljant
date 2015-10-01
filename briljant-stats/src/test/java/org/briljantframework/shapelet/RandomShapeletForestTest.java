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

package org.briljantframework.shapelet;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.briljantframework.Bj;
import org.briljantframework.array.BooleanArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.KNearestNeighbors;
import org.briljantframework.classification.Predictor;
import org.briljantframework.classification.RandomShapeletForest;
import org.briljantframework.classification.ShapeletTree;
import org.briljantframework.data.Collectors;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.dataframe.DataFrames;
import org.briljantframework.data.dataseries.DataSeriesCollection;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.reader.EntryReader;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.VectorType;
import org.briljantframework.data.vector.Vectors;
import org.briljantframework.dataset.io.Datasets;
import org.briljantframework.dataset.io.MatlabDatasetReader;
import org.briljantframework.dataset.io.SequenceDatasetReader;
import org.briljantframework.distance.EditDistance;
import org.briljantframework.evaluation.Validator;
import org.briljantframework.evaluation.Validators;
import org.briljantframework.evaluation.result.Evaluator;
import org.briljantframework.evaluation.result.Result;
import org.junit.Test;

public class RandomShapeletForestTest {

  // @Test
  // public void testLOOCV() throws Exception {
  // String name = "BirdChicken";
  // String trainFile = String.format("/Users/isak-kar/Downloads/dataset3/%s/%s.arff", name, name);
  // try (DataInputStream train = new ArffInputStream(new FileInputStream(trainFile))) {
  // DataFrame trainingSet = MixedDataFrame.read(train);
  // Transformer znorm = new DataSeriesNormalization();
  // // DataFrame xTrain =
  // // znorm.transform(new DataSeriesCollection.Builder(DoubleVector.TYPE)
  // // .stack(0, trainingSet.drop(trainingSet.columns() - 1))
  // // .build());
  // Vector yTrain = Convert.toStringVector(trainingSet.get(trainingSet.columns() - 1));
  //
  // RandomShapeletForest f =
  // RandomShapeletForest
  // .withSize(100)
  // .withInspectedShapelets(50)
  // .withLowerLength(0.025)
  // .withUpperLength(0.3)
  // .withAssessment(ShapeletTree.Assessment.FSTAT)
  // .build();
  // List<Evaluator> evaluatorList = Evaluator.getDefaultClassificationEvaluators();
  // evaluatorList.add(new Evaluator() {
  // private int fold = 0;
  //
  // @Override
  // public void accept(EvaluationContext ctx) {
  // System.out.printf("Fold %d\n", fold++);
  // }
  // });
  // Result re = Validators.leaveOneOutValidation().test(f, xTrain, yTrain);
  // System.out.println(re);
  // }
  // throw new UnsupportedOperationException();

  // }

  // @Test
  // public void testClassifiy2() throws Exception {
  // String name = "DP_Middle";
  // String trainFile =
  // String.format("/Users/isak-kar/Downloads/dataset3/%s/%s_TRAIN.arff", name, name);
  // String testFile =
  // String.format("/Users/isak-kar/Downloads/dataset3/%s/%s_TEST.arff", name, name);
  // try (DataInputStream train = new ArffInputStream(new FileInputStream(trainFile));
  // DataInputStream test = new ArffInputStream(new FileInputStream(testFile))) {
  // train.readColumnIndex(); // TODO!
  // DataFrame trainingSet =
  // new MixedDataFrame.Builder(train.readColumnTypes()).read(train)
  // .build();
  //
  // test.readColumnIndex(); // TODO:
  // DataFrame validationSet =
  // new MixedDataFrame.Builder(test.readColumnTypes()).read(test)
  // .build();
  //
  // Transformer znorm = new DataSeriesNormalization();
  // DataFrame xTrain =
  // znorm.transform(new DataSeriesCollection.Builder(DoubleVector.TYPE).stack(
  // 0, trainingSet.drop(trainingSet.columns() - 1)).build());
  // Vector yTrain = Convert.toStringVector(trainingSet.get(trainingSet.columns() - 1));
  //
  // DataFrame xTest =
  // znorm.transform(new DataSeriesCollection.Builder(DoubleVector.TYPE).stack(
  // 0, validationSet.drop(validationSet.columns() - 1)).build());
  // Vector yTest = Convert.toStringVector(validationSet.get(validationSet.columns() - 1));
  //
  // long start = System.nanoTime();
  // DoubleArray upper = Bj.array(new double[]{0.05, 0.1, 0.3, 0.5, 0.7, 1});
  // IntArray sizes = Bj.array(new int[]{100});
  // // IntMatrix sizes = IntMatrix.of(500);
  // System.out.println("Size,Correlation,Strength,Quality,Expected Error,"
  // + "Accuracy,OOB Accuracy,Variance,Bias,Brier,Depth");
  // for (int i = 0; i < sizes.size(); i++) {
  // RandomShapeletForest forest =
  // RandomShapeletForest.withSize(1000).withInspectedShapelets(sizes.get(i))
  // .withLowerLength(0.025).withUpperLength(0.5)
  // // .withSampleMode(ShapeletTree.SampleMode.RANDOMIZE)
  // .withAssessment(ShapeletTree.Assessment.FSTAT).build();
  // Result result = HoldoutValidator.withHoldout(xTest, yTest).test(forest, xTrain, yTrain);
  // // System.out.println(result);
  // System.out.println(sizes.get(i) + ", "
  // + result.getAverage(Ensemble.Correlation.class) + ", "
  // + result.getAverage(Ensemble.Strength.class) + ", "
  // + result.getAverage(Ensemble.Quality.class) + ", "
  // + result.getAverage(Ensemble.ErrorBound.class) + ", "
  // + result.getAverage(Accuracy.class) + ", "
  // + result.getAverage(Ensemble.OobAccuracy.class) + ", "
  // + result.getAverage(Ensemble.Variance.class) + ", "
  // + result.getAverage(Ensemble.MeanSquareError.class) + ", "
  // + result.getAverage(Brier.class) + ", "
  // + result.getAverage(RandomShapeletForest.Depth.class));
  // }
  // System.out.println((System.nanoTime() - start) / 1e6);
  //
  // }
  // }


  @Test
  public void testSynticControl() throws Exception {
    // DataFrame train = Datasets.load(
    // (i) -> new DataSeriesCollection.Builder(double.class), new MatlabDatasetReader(
    // new FileInputStream(
    // "/Users/isak-kar/Downloads/dataset/SwedishLeaf/SwedishLeaf_TRAIN")));
    // DataFrame test = Datasets.load(
    // (i) -> new DataSeriesCollection.Builder(double.class), new MatlabDatasetReader(
    // new FileInputStream(
    // "/Users/isak-kar/Downloads/dataset/SwedishLeaf/SwedishLeaf_TEST")));

    // DataFrame train = Datasets.load(
    // (i) -> new DataSeriesCollection.Builder(double.class), new MatlabDatasetReader(
    // new FileInputStream(
    // "/Users/isak-kar/Downloads/dataset/Gun_Point/Gun_Point_TRAIN")));
    // DataFrame test = Datasets.load(
    // (i) -> new DataSeriesCollection.Builder(double.class), new MatlabDatasetReader(
    // new FileInputStream(
    // "/Users/isak-kar/Downloads/dataset/Gun_Point/Gun_Point_TEST")));
    //
    // DataFrame train = Datasets.load(
    // (i) -> new DataSeriesCollection.Builder(double.class), new MatlabDatasetReader(
    // new FileInputStream(
    // "/Users/isak-kar/Downloads/dataset2/SonyAIBORobotSurfaceII/SonyAIBORobotSurfaceII_TRAIN")));
    // DataFrame test = Datasets.load(
    // (i) -> new DataSeriesCollection.Builder(double.class), new MatlabDatasetReader(
    // new FileInputStream(
    // "/Users/isak-kar/Downloads/dataset2/SonyAIBORobotSurfaceII/SonyAIBORobotSurfaceII_TEST")));
    //
    // DataFrame train = Datasets.load(
    // (i) -> new DataSeriesCollection.Builder(double.class), new MatlabDatasetReader(
    // new FileInputStream(
    // "/Users/isak-kar/Downloads/dataset2/ECGFiveDays/ECGFiveDays_TRAIN")));
    // DataFrame test = Datasets.load(
    // (i) -> new DataSeriesCollection.Builder(double.class), new MatlabDatasetReader(
    // new FileInputStream(
    // "/Users/isak-kar/Downloads/dataset2/ECGFiveDays/ECGFiveDays_TEST")));

    // DataFrame train = Datasets.load(
    // (i) -> new DataSeriesCollection.Builder(double.class), new MatlabDatasetReader(
    // new FileInputStream(
    // "/Users/isak-kar/Downloads/dataset/Coffee/Coffee_TRAIN")));
    // DataFrame test = Datasets.load(
    // (i) -> new DataSeriesCollection.Builder(double.class), new MatlabDatasetReader(
    // new FileInputStream(
    // "/Users/isak-kar/Downloads/dataset/Coffee/Coffee_TEST")));

    // DataFrame train = Datasets.load(
    // (i) -> new DataSeriesCollection.Builder(double.class), new MatlabDatasetReader(
    // new FileInputStream("/Users/isak-kar/Downloads/dataset2/wafer/wafer_TRAIN")));
    // DataFrame test = Datasets.load(
    // (i) -> new DataSeriesCollection.Builder(double.class), new MatlabDatasetReader(
    // new FileInputStream("/Users/isak-kar/Downloads/dataset2/wafer/wafer_TEST")));

    // DataFrame train = Datasets.load(
    // (i) -> new DataSeriesCollection.Builder(double.class), new MatlabDatasetReader(
    // new FileInputStream(
    // "/Users/isak-kar/Downloads/dataset2/MoteStrain/MoteStrain_TRAIN")));
    // DataFrame test = Datasets.load(
    // (i) -> new DataSeriesCollection.Builder(double.class), new MatlabDatasetReader(
    // new FileInputStream(
    // "/Users/isak-kar/Downloads/dataset2/MoteStrain/MoteStrain_TEST")));
    //
    // DataFrame train = Datasets.load(
    // (i) -> new DataSeriesCollection.Builder(double.class), new MatlabDatasetReader(
    // new FileInputStream(
    // "/Users/isak-kar/Downloads/dataset/Two_Patterns/Two_Patterns_TRAIN")));
    // DataFrame test = Datasets.load(
    // (i) -> new DataSeriesCollection.Builder(double.class), new MatlabDatasetReader(
    // new FileInputStream(
    // "/Users/isak-kar/Downloads/dataset/Two_Patterns/Two_Patterns_TEST")));

    String fileName = "MedicalImages";
    String path = "/Users/isak-kar/Downloads/dataset2";
    DataFrame train =
        Datasets.load(
            (i) -> new DataSeriesCollection.Builder(double.class),
            new MatlabDatasetReader(new FileInputStream(String.format("%s/%s/%s_TRAIN", path,
                fileName, fileName))));
    DataFrame test =
        Datasets.load(
            (i) -> new DataSeriesCollection.Builder(double.class),
            new MatlabDatasetReader(new FileInputStream(String.format("%s/%s/%s_TEST", path,
                fileName, fileName))));

    train.setColumnIndex(Index.range(train.columns()));
    test.setColumnIndex(Index.range(test.columns()));
    System.out.println(test.get(0).valueCounts());
    System.out.println(train.get(0).valueCounts());

    // System.out.println(train.head(5));
    // System.out.println(train.getRecord(0).asList(Double.class).subList(1, 85));
    // System.out.println(train.drop(0).getRecord(0).asList(Double.class));
    // System.out.println(train.drop(0).columns());
    Classifier rsf =
        RandomShapeletForest.withSize(100).withAssessment(ShapeletTree.Assessment.IG)
            .withInspectedShapelets(100).withUpperLength(1).build();
    // Classifier knn = KNearestNeighbors.withNeighbors(1).build();
    // System.out.println(HoldoutValidator.withHoldout(test.drop(0), test.get(0))
    // .test(classifier, train.drop(0), train.get(0))
    // .getAverage(Accuracy.class));
    StringBuilder builder = new StringBuilder();
    // testEarlyClassification(train, test, knn, fileName, "knn", builder);
    // builder.append("\n");
    testEarlyClassification(train, test, rsf, fileName, "rsf", builder);
    // builder.append("\n");
    // builder.append("plot(knn_").append(fileName).append("[5:], 'g')").append("\n");
    // builder.append("plot(rsf_").append(fileName).append("[5:], 'g')").append("\n");
    // plt.title("Mote rsf={} knn={}".format(auac(mote[5:]), auac(mote_1nn[5:])))
    // builder.append("");
    System.out.println(builder);

  }

  private void testEarlyClassification(DataFrame train, DataFrame test, Classifier classifier,
      String fileName, String variable, StringBuilder builder) {
    Predictor predictor = classifier.fit(train.drop(0), train.get(0));

    // DoubleArray oobAccuracyPerLength = computeOobAccuracy(predictor, train.drop(0),
    // train.get(0));

    Vector classes = predictor.getClasses();
    DataFrame xTest = test.drop(0);
    Vector yTest = test.get(0);

    IntArray decisionTime = Bj.intArray(xTest.rows());
    IntArray correctAt = Bj.intArray(xTest.columns());
    double correct = 0;
    for (int i = 0; i < xTest.rows(); i++) {
      if (i % 100 == 0) {
        System.out.printf("Processing test instance %d/%d\n", i, xTest.rows());
      }
      Vector record = xTest.loc().getRecord(i);
      Object trueLabel = yTest.loc().get(Object.class, i);
      boolean found = false;
      for (int j = 5; j < record.size() && !found; j++) {
        DoubleArray estimation = predictor.estimate(record.select(0, j));
        int max = Bj.argmax(estimation);
        correctAt.addTo(j, classes.loc().get(Object.class, max).equals(trueLabel) ? 1 : 0);
        if (estimation.get(max) > 0.8) {
          decisionTime.set(i, j);
          correct += classes.loc().get(Object.class, max).equals(trueLabel) ? 1 : 0;
          // found = true;
        }
      }
      if (!found) {
        correct += predictor.predict(record).equals(trueLabel) ? 1 : 0;
        decisionTime.set(i, record.size());
      }
    }

    DoubleArray meanCorrectness = correctAt.asDouble().div(xTest.rows());
    System.out.println(meanCorrectness.get(Bj.range(5, meanCorrectness.size()))
        .collect(Collectors.statisticalSummary()).getMean());
    builder.append(variable).append("_").append(fileName).append(" = ").append("np.")
        .append(meanCorrectness);
  }


  private final Random random = new Random(123);

  private Vector shift(Vector record) {
    Vector.Builder builder = record.newBuilder();
    int start = random.nextInt(record.size() - 1);
    for (int i = start; i < record.size(); i++) {
      builder.add(record, i);
    }
    for (int i = 0; i < start; i++) {
      builder.add(record, i);
    }

    return builder.build();
  }

  @Test
  public void testShift() throws Exception {
    System.out.println(shift(Vector.of(1, 2, 3, 4, 5, 6)));
  }

  private DoubleArray computeOobAccuracy(RandomShapeletForest.Predictor predictor, DataFrame x,
      Vector y) {

    BooleanArray oob = predictor.getOobIndicator();
    for (int i = 0; i < x.rows(); i++) {
      Vector record = x.loc().getRecord(i);
      List<Predictor> oobMembers = getOobMembers(oob.getRow(i), predictor.getPredictors());
      DoubleArray estimate = predictOob(oobMembers, record);

    }
    return null;
  }

  private List<Predictor> getOobMembers(BooleanArray oob, List<Predictor> predictors) {
    List<Predictor> oobPredictors = new ArrayList<>();
    for (int i = 0; i < predictors.size(); i++) {
      if (oob.get(i)) {
        oobPredictors.add(predictors.get(i));
      }
    }

    return oobPredictors;
  }

  private DoubleArray predictOob(List<Predictor> members, Vector record) {

    return null;
  }

  @Test
  public void testSequences() throws Exception {
    String ade = "L270";
    EntryReader in =
        new SequenceDatasetReader(
            new FileInputStream("/Users/isak-kar/Desktop/out/" + ade + ".seq"));

    DataFrame frame = new DataSeriesCollection.Builder(VectorType.STRING).readAll(in).build();
    System.out.println(frame.rows() + ", " + frame.columns());
    // Utils.setRandomSeed(32);
    frame = DataFrames.permuteRecords(frame);
    Vector y = frame.get(0);
    DataFrame x = frame.drop(0);
    Map<Object, Integer> freq = Vectors.count(y);
    int sum = freq.values().stream().reduce(0, Integer::sum);
    int min = freq.values().stream().min(Integer::min).get();
    System.out.println(freq + " => " + ((double) min / sum));

    Classifier forest = KNearestNeighbors.withNeighbors(1).withDistance(new EditDistance()).build();
    Validator cv = Validators.crossValidation(5);
    cv.getEvaluators().add(
        Evaluator.foldOutput(fold -> System.out.printf("Completed fold %d\n", fold)));
    Result result = cv.test(forest, x, y);
    System.out.println(result.getAverageConfusionMatrix().getPrecision("ade"));
    System.out.println(result.getAverageConfusionMatrix().getRecall("ade"));
    System.out.println(result.getAverageConfusionMatrix().getFMeasure("ade", 2));
    System.out.println(result);
  }

  // @Test
  // public void testClassify() throws Exception {
  // String name = "Gun_Point";
  //
  // // path to the data sets
  // String trainFile = String.format("/Users/isak-kar/Downloads/dataset/%s/%s_TRAIN", name, name);
  // String testFile = String.format("/Users/isak-kar/Downloads/dataset/%s/%s_TEST", name, name);
  // DataFrame trainingSet = new DataSeriesCollection.Builder(VectorType.DOUBLE)
  // .readAll(train)
  // .build();
  // DataFrame validationSet = new DataSeriesCollection.Builder(VectorType.DOUBLE)
  // .readAll(test)
  // .build();
  // System.out.println(trainingSet);
  //
  // // remove the class-label column
  // DataFrame xTrain = trainingSet.drop(0);
  // DataFrame xTest = validationSet.drop(0);
  //
  // // get the class label column
  // Vector yTrain = trainingSet.get(0);
  // Vector yTest = validationSet.get(0);
  //
  // long start = System.nanoTime();
  //
  // // Initialize the Shapelet Forest Algorithm with a few default parameters
  // RandomShapeletForest forest = RandomShapeletForest.withSize(500) // No trees
  // .withInspectedShapelets(100) // No inspected shapelets
  // .withLowerLength(0.025) // The lower length (a fraction of time-series length)
  // .withUpperLength(1) // The upper length (a fraction of time-series length)
  // .withAssessment(ShapeletTree.Assessment.IG) // The shapelet scoring function
  // .build();
  //
  // // Evaluate the predictive performance using the holdout dataset
  // Result result = HoldoutValidator.withHoldout(xTest, yTest).test(forest, xTrain, yTrain);
  // System.out.printf("Experiment took: %f milliseconds\n", (System.nanoTime() - start) / 1e6);
  // System.out.println(result);
  //
  // }
}
