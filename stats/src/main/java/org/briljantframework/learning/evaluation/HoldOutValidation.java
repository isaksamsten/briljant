package org.briljantframework.learning.evaluation;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.Model;
import org.briljantframework.learning.Predictions;
import org.briljantframework.learning.evaluation.result.ConfusionMatrix;
import org.briljantframework.learning.evaluation.result.Metric;
import org.briljantframework.learning.evaluation.result.Metrics;
import org.briljantframework.learning.evaluation.result.Result;
import org.briljantframework.vector.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by isak on 05/10/14.
 */
public class HoldOutValidation extends AbstractEvaluator {


    private final DataFrame holdoutX;
    private final Vector holdoutY;

    /**
     * Instantiates a new Abstract evaluator.
     *
     * @param holdoutX
     */
    public HoldOutValidation(DataFrame holdoutX, Vector holdoutY) {
        super(Metrics.CLASSIFICATION);
        this.holdoutX = holdoutX;
        this.holdoutY = holdoutY;
    }

    /**
     * Create hold out validation.
     *
     * @return the hold out validation
     */
    public static HoldOutValidation withHoldout(DataFrame x, Vector y) {
        return new HoldOutValidation(x, y);
    }

    @Override
    public Result evaluate(Classifier classifier, DataFrame x, Vector y) {
        Model model = classifier.fit(x, y);
        return evaluate(model, x, y);
    }

    /**
     * Evaluate result.
     *
     * @param model the model
     * @return the result
     */
    public Result evaluate(Model model, DataFrame x, Vector y) {
        Predictions holdOutPredictions = model.predict(holdoutX);
        Predictions inSamplePredictions = model.predict(x);

        ConfusionMatrix confusionMatrix = ConfusionMatrix.create(holdOutPredictions, y);
        List<Metric> metrics = getMetricFactories().stream()
                .map(Metric.Factory::newProducer)
                .map(producer -> {
                    producer.add(Metric.Sample.IN, inSamplePredictions, y);
                    producer.add(Metric.Sample.OUT, holdOutPredictions, holdoutY);
                    return producer.produce();
                })
                .collect(Collectors.toCollection(ArrayList::new));
        return Result.create(metrics, Arrays.asList(confusionMatrix));
    }
}
