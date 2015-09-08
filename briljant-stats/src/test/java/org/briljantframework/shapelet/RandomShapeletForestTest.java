/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.shapelet;

import org.briljantframework.Utils;
import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.KNearestNeighbors;
import org.briljantframework.classification.RandomShapeletForest;
import org.briljantframework.classification.ShapeletTree;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.dataframe.DataFrames;
import org.briljantframework.data.dataseries.DataSeriesCollection;
import org.briljantframework.distance.EditDistance;
import org.briljantframework.evaluation.HoldoutValidator;
import org.briljantframework.evaluation.Validator;
import org.briljantframework.evaluation.Validators;
import org.briljantframework.evaluation.result.Evaluator;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.io.EntryReader;
import org.briljantframework.io.MatlabTextInputStream;
import org.briljantframework.io.SequenceInputStream;
import org.briljantframework.data.vector.Vectors;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.VectorType;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.Map;

public class RandomShapeletForestTest {

//  @Test
//  public void testLOOCV() throws Exception {
//    String name = "BirdChicken";
//    String trainFile = String.format("/Users/isak-kar/Downloads/dataset3/%s/%s.arff", name, name);
//    try (DataInputStream train = new ArffInputStream(new FileInputStream(trainFile))) {
//      DataFrame trainingSet = MixedDataFrame.read(train);
//      Transformation znorm = new DataSeriesNormalization();
////      DataFrame xTrain =
////          znorm.transform(new DataSeriesCollection.Builder(DoubleVector.TYPE)
////                              .stack(0, trainingSet.drop(trainingSet.columns() - 1))
////                              .build());
//      Vector yTrain = Convert.toStringVector(trainingSet.get(trainingSet.columns() - 1));
//
//      RandomShapeletForest f =
//          RandomShapeletForest
//              .withSize(100)
//              .withInspectedShapelets(50)
//              .withLowerLength(0.025)
//              .withUpperLength(0.3)
//              .withAssessment(ShapeletTree.Assessment.FSTAT)
//              .build();
//      List<Evaluator> evaluatorList = Evaluator.getDefaultClassificationEvaluators();
//      evaluatorList.add(new Evaluator() {
//        private int fold = 0;
//
//        @Override
//        public void accept(EvaluationContext ctx) {
//          System.out.printf("Fold %d\n", fold++);
//        }
//      });
//      Result re = Validators.leaveOneOutValidation().test(f, xTrain, yTrain);
//      System.out.println(re);
//    }
//    throw new UnsupportedOperationException();

//  }

//  @Test
//  public void testClassifiy2() throws Exception {
//    String name = "DP_Middle";
//    String trainFile =
//        String.format("/Users/isak-kar/Downloads/dataset3/%s/%s_TRAIN.arff", name, name);
//    String testFile =
//        String.format("/Users/isak-kar/Downloads/dataset3/%s/%s_TEST.arff", name, name);
//    try (DataInputStream train = new ArffInputStream(new FileInputStream(trainFile));
//         DataInputStream test = new ArffInputStream(new FileInputStream(testFile))) {
//      train.readColumnIndex(); // TODO!
//      DataFrame trainingSet =
//          new MixedDataFrame.Builder(train.readColumnTypes()).read(train)
//              .build();
//
//      test.readColumnIndex(); // TODO:
//      DataFrame validationSet =
//          new MixedDataFrame.Builder(test.readColumnTypes()).read(test)
//              .build();
//
//      Transformation znorm = new DataSeriesNormalization();
//      DataFrame xTrain =
//          znorm.transform(new DataSeriesCollection.Builder(DoubleVector.TYPE).stack(
//              0, trainingSet.drop(trainingSet.columns() - 1)).build());
//      Vector yTrain = Convert.toStringVector(trainingSet.get(trainingSet.columns() - 1));
//
//      DataFrame xTest =
//          znorm.transform(new DataSeriesCollection.Builder(DoubleVector.TYPE).stack(
//              0, validationSet.drop(validationSet.columns() - 1)).build());
//      Vector yTest = Convert.toStringVector(validationSet.get(validationSet.columns() - 1));
//
//      long start = System.nanoTime();
//      DoubleArray upper = Bj.array(new double[]{0.05, 0.1, 0.3, 0.5, 0.7, 1});
//      IntArray sizes = Bj.array(new int[]{100});
//      // IntMatrix sizes = IntMatrix.of(500);
//      System.out.println("Size,Correlation,Strength,Quality,Expected Error,"
//                         + "Accuracy,OOB Accuracy,Variance,Bias,Brier,Depth");
//      for (int i = 0; i < sizes.size(); i++) {
//        RandomShapeletForest forest =
//            RandomShapeletForest.withSize(1000).withInspectedShapelets(sizes.get(i))
//                .withLowerLength(0.025).withUpperLength(0.5)
//                // .withSampleMode(ShapeletTree.SampleMode.RANDOMIZE)
//                .withAssessment(ShapeletTree.Assessment.FSTAT).build();
//        Result result = HoldoutValidator.withHoldout(xTest, yTest).test(forest, xTrain, yTrain);
//        // System.out.println(result);
//        System.out.println(sizes.get(i) + ", "
//                           + result.getAverage(Ensemble.Correlation.class) + ", "
//                           + result.getAverage(Ensemble.Strength.class) + ", "
//                           + result.getAverage(Ensemble.Quality.class) + ", "
//                           + result.getAverage(Ensemble.ErrorBound.class) + ", "
//                           + result.getAverage(Accuracy.class) + ", "
//                           + result.getAverage(Ensemble.OobAccuracy.class) + ", "
//                           + result.getAverage(Ensemble.Variance.class) + ", "
//                           + result.getAverage(Ensemble.MeanSquareError.class) + ", "
//                           + result.getAverage(Brier.class) + ", "
//                           + result.getAverage(RandomShapeletForest.Depth.class));
//      }
//      System.out.println((System.nanoTime() - start) / 1e6);
//
//    }
//  }

  @Test
  public void testSequences() throws Exception {
    String ade = "L270";
    EntryReader in =
        new SequenceInputStream(new FileInputStream("/Users/isak-kar/Desktop/out/" + ade + ".seq"));

    DataFrame frame = new DataSeriesCollection.Builder(VectorType.STRING).read(in).build();
    System.out.println(frame.rows() + ", " + frame.columns());
    Utils.setRandomSeed(32);
    frame = DataFrames.permuteRecords(frame);
    Vector y = frame.get(0);
    DataFrame x = frame.drop(0);
    Map<Object, Integer> freq = Vectors.count(y);
    int sum = freq.values().stream().reduce(0, Integer::sum);
    int min = freq.values().stream().min(Integer::min).get();
    System.out.println(freq + " => " + ((double) min / sum));

    Classifier forest =
        KNearestNeighbors.withNeighbors(1)
            .withDistance(new EditDistance()).build();
    Validator cv = Validators.crossValidation(5);
    cv.getEvaluators().add(
        Evaluator.foldOutput(fold -> System.out.printf("Completed fold %d\n", fold)));
    Result result = cv.test(forest, x, y);
    System.out.println(result.getAverageConfusionMatrix().getPrecision("ade"));
    System.out.println(result.getAverageConfusionMatrix().getRecall("ade"));
    System.out.println(result.getAverageConfusionMatrix().getFMeasure("ade", 2));
    System.out.println(result);
  }

  @Test
  public void testClassify() throws Exception {
    String name = "Gun_Point";

    // path to the data sets
    String trainFile = String.format("/Users/isak-kar/Downloads/dataset/%s/%s_TRAIN", name, name);
    String testFile = String.format("/Users/isak-kar/Downloads/dataset/%s/%s_TEST", name, name);
    try (DataInputStream train = new MatlabTextInputStream(new FileInputStream(trainFile));
         DataInputStream test = new MatlabTextInputStream(new FileInputStream(testFile))) {
      DataFrame trainingSet = new DataSeriesCollection.Builder(VectorType.DOUBLE)
          .read(train)
          .build();
      DataFrame validationSet = new DataSeriesCollection.Builder(VectorType.DOUBLE)
          .read(test)
          .build();
      System.out.println(trainingSet);

      // remove the class-label column
      DataFrame xTrain = trainingSet.drop(0);
      DataFrame xTest = validationSet.drop(0);

      // get the class label column
      Vector yTrain = trainingSet.get(0);
      Vector yTest = validationSet.get(0);

      long start = System.nanoTime();

      // Initialize the Shapelet Forest Algorithm with a few default parameters
      RandomShapeletForest forest = RandomShapeletForest.withSize(500) // No trees
          .withInspectedShapelets(100) // No inspected shapelets
          .withLowerLength(0.025) // The lower length (a fraction of time-series length)
          .withUpperLength(1) // The upper length (a fraction of time-series length)
          .withAssessment(ShapeletTree.Assessment.IG) // The shapelet scoring function
          .build();

      // Evaluate the predictive performance using the holdout dataset
      Result result = HoldoutValidator.withHoldout(xTest, yTest).test(forest, xTrain, yTrain);
      System.out.printf("Experiment took: %f milliseconds\n", (System.nanoTime() - start) / 1e6);
      System.out.println(result);

    }
  }
}
