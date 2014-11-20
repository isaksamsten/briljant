package org.briljantframework.learning.evaluation.result;

import org.briljantframework.data.column.CategoricColumn;
import org.briljantframework.data.column.Column;
import org.briljantframework.data.column.DefaultCategoricColumn;
import org.briljantframework.data.types.CategoricType;
import org.briljantframework.data.values.Categoric;
import org.briljantframework.learning.Prediction;
import org.briljantframework.learning.Predictions;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class ProducerTest {

    @Test
    public void testGetPerformanceMetric() throws Exception {
        Random random = new Random(123);

        List<Prediction> preds = new ArrayList<>();
        Column.Builder<CategoricColumn> targetBuilder = DefaultCategoricColumn.copyTo().newBuilder(new
                CategoricType("hello"));
        for (int i = 0; i < 10; i++) {
            preds.add(Prediction.unary(Categoric.valueOf(random.nextInt(3))));
            targetBuilder.add(Categoric.valueOf(random.nextInt(3)));
        }

        Metric.Producer errorProducer = ErrorRate.getFactory().newProducer();

        Predictions predictions = Predictions.create(preds);
        Column column = targetBuilder.create();
        errorProducer.add(Metric.Sample.OUT, predictions, column);


        for (int i = 0; i < 10; i++) {
            preds.add(Prediction.unary(Categoric.valueOf(random.nextInt(3))));
            targetBuilder.add(Categoric.valueOf(random.nextInt(3)));
        }
        column = targetBuilder.create();
        predictions = Predictions.create(preds);

        errorProducer.add(Metric.Sample.OUT, predictions, column);
        Metric metric = errorProducer.produce();

        System.out.println(metric.get(0) + " " + metric.get(1));

        System.out.println(metric.getStandardDeviation());
        System.out.println(metric.getAverage());


        ConfusionMatrix confusionMatrix = ConfusionMatrix.create(predictions, column);
        System.out.println(confusionMatrix);


        Predictions predictions1 = Predictions.create(Arrays.asList(
                Prediction.unary(Categoric.valueOf("hello")),
                Prediction.unary(Categoric.valueOf("4")),
                Prediction.unary(Categoric.valueOf("hello")),
                Prediction.unary(Categoric.valueOf("dsadsadsadsa")),
                Prediction.unary(Categoric.valueOf("dsa")),
                Prediction.unary(Categoric.valueOf("d"))
        ));
        Column.Builder<CategoricColumn> targetBuilder1 = DefaultCategoricColumn.copyTo().newBuilder(new
                CategoricType("hello"));
        targetBuilder1.add(Categoric.valueOf("hello"));
        targetBuilder1.add(Categoric.valueOf("fsvcxvcxvcxv"));
        targetBuilder1.add(Categoric.valueOf("3"));
        targetBuilder1.add(Categoric.valueOf("hello"));
        targetBuilder1.add(Categoric.valueOf("helloooo"));
        targetBuilder1.add(Categoric.valueOf("1"));

        System.out.println(ConfusionMatrix.create(predictions1, targetBuilder1.create()));
    }
}