package org.briljantframework.learning;

import org.briljantframework.data.DenseDataFrame;
import org.briljantframework.data.column.CategoricColumn;
import org.briljantframework.data.column.DefaultCategoricColumn;
import org.briljantframework.io.CSVInputStream;
import org.briljantframework.io.DataFrameInputStream;
import org.briljantframework.learning.evaluation.Evaluators;
import org.briljantframework.learning.evaluation.result.AreaUnderCurve;
import org.briljantframework.learning.evaluation.result.Result;
import org.briljantframework.learning.evaluation.tune.Configuration;
import org.briljantframework.learning.evaluation.tune.Tuners;
import org.briljantframework.learning.evaluation.tune.Updaters;
import org.briljantframework.learning.tree.ClassificationForest;
import org.junit.Assume;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;

/**
 * The type Remote classifier test.
 */
public class RemoteClassifierTest {

    @Test
    public void testDataset() throws Exception {
        DataFrameInputStream in = new CSVInputStream(new FileInputStream("erlang/adeb-rr/deps/rr/data/iris.txt"));
        DenseDataFrame dataset = in.read(DenseDataFrame.copyTo());

    }

    /**
     * Test random forest.
     *
     * @throws Exception the exception
     */
    @Test
    public void testRandomForest() throws Exception {
        Assume.assumeTrue(new File("erlang/adeb-rr/ebin/").exists());

        DataFrameInputStream in = new CSVInputStream(new FileInputStream("erlang/adeb-rr/deps/rr/data/anneal-orig.txt"));
        DenseDataFrame dataset = in.read(DenseDataFrame.copyTo());

        SupervisedDataset<DenseDataFrame, CategoricColumn> supervisedDataset =
                SupervisedDataset.createClassificationInput(dataset, DenseDataFrame.copyTo(), DefaultCategoricColumn.copyTo());

        ClassificationForest forest = ClassificationForest.withSize(100).withMaximumFeatures(4).create();
        long start = System.currentTimeMillis();
        Result result = Evaluators.crossValidation(forest, supervisedDataset, 10);
        System.out.println(System.currentTimeMillis() - start);
        System.out.println(result);


        ClassificationForest.Builder forestBuilder = ClassificationForest.withSize(10);
        System.out.println(
                Tuners.crossValidation(forestBuilder, supervisedDataset, Configuration.metricComparator(AreaUnderCurve.class), 10,
                        Updaters.range("features", ClassificationForest.Builder::withMaximumFeatures, 2, 10, 2),
                        Updaters.range("trees", ClassificationForest.Builder::withSize, 10, 100, 10)
                )
        );


        //        Chart chart = QuickChart.getChart("Plot", "x", "y", "line", x.array(), y.array());
        //        Accuracy auc = result.get(Accuracy.class);

        //        System.out.println(result.get(Error.class));
        //        System.out.println(result.getAverageConfusionMatrix());
        //        System.out.println(result.getAverageConfusionMatrix().getAveragePrecision());
        //        System.out.println(result.getAverageConfusionMatrix().getAverageRecall());
        //        System.out.println(result.getAverageConfusionMatrix().getPrecision(Categoric.valueOf("1")));
        //        System.out.println(result.getAverageConfusionMatrix().getRecall(Categoric.valueOf("1")));
        //        System.out.println(result.getAverageConfusionMatrix().getAverageFMeasure(1));
        //

        //        AreaUnderCurve auc = result.get(AreaUnderCurve.class);
        //        for (Value value : auc.getLabels())
        //            System.out.println(value + " " + auc.getAverage(value) + "(" + auc.getAverage(Metric.Sample.IN) + ")");
        //
        //        System.out.println(result.getAverage(AreaUnderCurve.class));


        //        System.out.println(result);

        //        result.getMetrics().forEach(System.out::println);

        //        System.out.println(storage);
        //
        //        Model<Dataset> model = randomForest.fit(storage);
        //        List<Prediction> predictions = model.predict(storage.getDataset());
        //        for (int i = 0; i < predictions.size(); i++) {
        //            System.out.println(predictions.get(i).getTarget().equals(storage.getTarget().getValue(i)));
        //
        //        }

        //        System.out.println(CrossValidation.withFolds(10).evaluate(randomForest, storage));


    }
}