package org.briljantframework.classification;

import org.briljantframework.Bj;
import org.briljantframework.classification.tree.ClassSet;
import org.briljantframework.classification.tree.Example;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.DataFrames;
import org.briljantframework.dataseries.DataSeriesCollection;
import org.briljantframework.evaluation.HoldoutValidator;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.io.MatlabTextInputStream;
import org.briljantframework.matrix.BitMatrix;
import org.briljantframework.vector.Convert;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vec;
import org.junit.Test;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Callable;

public class RandomPointTreeTest {

  @Test
  public void testFit() throws Exception {

    Classifier forest = new Ensemble(500) {
      @Override
      public Predictor fit(DataFrame x, Vector y) {
        Vector classes = Vec.unique(y);
        ClassSet classSet = new ClassSet(y, classes);
        List<FitTask> fitTasks = new ArrayList<>();
        BitMatrix oobIndicator = Bj.booleanMatrix(x.rows(), size());
        for (int i = 0; i < size(); i++) {
          fitTasks.add(new FitTask(classSet, x, y, classes, oobIndicator.getColumnView(i)));
        }
        try {
          return new DefaultEnsemblePredictor(classes, execute(fitTasks), oobIndicator);
        } catch (Exception e) {
          e.printStackTrace();
          return null;
        }
      }

      class FitTask implements Callable<Predictor> {

        private final ClassSet classSet;
        private final DataFrame x;
        private final Vector y;
        private final Vector classes;
        private final BitMatrix oobIndicator;

        private FitTask(ClassSet classSet, DataFrame x, Vector y, Vector classes,
                        BitMatrix oobIndicator) {
          this.classSet = classSet;
          this.x = x;
          this.y = y;
          this.classes = classes;
          this.oobIndicator = oobIndicator;
        }

        @Override
        public Predictor call() throws Exception {
          Random random = new Random(Thread.currentThread().getId() * System.currentTimeMillis());
          ClassSet bootstrap = sample(classSet, random);
          return new PatternTree(classes, bootstrap).fit(x, y);
        }

        public ClassSet sample(ClassSet classSet, Random random) {
          ClassSet inBag = new ClassSet(classSet.getDomain());
          int[] bootstrap = bootstrap(classSet, random);
          for (ClassSet.Sample sample : classSet.samples()) {
            ClassSet.Sample inSample = ClassSet.Sample.create(sample.getTarget());
            for (Example example : sample) {
              int id = example.getIndex();
              if (bootstrap[id] > 0) {
                inSample.add(example.updateWeight(bootstrap[id]));
              } else {
                oobIndicator.set(id, true);
              }
            }
            if (!inSample.isEmpty()) {
              inBag.add(inSample);
            }
          }
          return inBag;
        }

        private int[] bootstrap(ClassSet sample, Random random) {
          int[] bootstrap = new int[sample.size()];
          for (int i = 0; i < bootstrap.length; i++) {
            int idx = random.nextInt(bootstrap.length);
            bootstrap[idx]++;
          }

          return bootstrap;
        }
      }
    };
//    Result r = Validators.crossValidation(5).test(t, x, y);
//    System.out.println(r);

    String name = "OSULeaf";
    String trainFile = String.format("/Users/isak-kar/Downloads/dataset/%s/%s_TRAIN", name, name);
    String testFile = String.format("/Users/isak-kar/Downloads/dataset/%s/%s_TEST", name, name);
    try (DataInputStream train = new MatlabTextInputStream(new FileInputStream(trainFile));
         DataInputStream test = new MatlabTextInputStream(new FileInputStream(testFile))) {
      DataFrame trainingSet =
          DataFrames.permuteRows(new DataSeriesCollection.Builder(DoubleVector.TYPE).read(train)
                                     .build());
      DataFrame validationSet =
          new DataSeriesCollection.Builder(DoubleVector.TYPE).read(test).build();

      DataFrame xTrain = trainingSet.drop(0);
      Vector yTrain = Convert.toStringVector(trainingSet.get(0));

      DataFrame xTest = validationSet.drop(0);
      Vector yTest = Convert.toStringVector(validationSet.get(0));

      System.out.printf("Running with %s (rows: %d, columns: %d)\n", name, xTrain.rows(),
                        xTrain.columns());

      Result result = HoldoutValidator.withHoldout(xTest, yTest).test(forest, xTrain, yTrain);
      System.out.println(result);
    }

  }
}