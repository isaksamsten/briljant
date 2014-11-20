package org.briljantframework.learning.evaluation;

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.column.Column;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.Model;
import org.briljantframework.learning.Predictions;
import org.briljantframework.learning.SupervisedDataset;
import org.briljantframework.learning.evaluation.result.ConfusionMatrix;
import org.briljantframework.learning.evaluation.result.Metric;
import org.briljantframework.learning.evaluation.result.Metrics;
import org.briljantframework.learning.evaluation.result.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by isak on 05/10/14.
 *
 * @param <D> the type parameter
 * @param <T> the type parameter
 */
public class HoldOutValidation<D extends DataFrame<?>, T extends Column> extends AbstractEvaluator<D, T> {


    private final SupervisedDataset<? extends D, ? extends T> holdoutSupervisedDataset;

    /**
     * Instantiates a new Abstract evaluator.
     *
     * @param holdoutSupervisedDataset
     */
    public HoldOutValidation(SupervisedDataset<? extends D, ? extends T> holdoutSupervisedDataset) {
        super(Metrics.CLASSIFICATION);
        this.holdoutSupervisedDataset = holdoutSupervisedDataset;
    }

    /**
     * Create hold out validation.
     *
     * @param <D> the type parameter
     * @param <T> the type parameter
     * @return the hold out validation
     */
    public static <D extends DataFrame<?>, T extends Column> HoldOutValidation<D, T> withHoldout(
            SupervisedDataset<? extends D, ? extends T> supervisedDataset) {
        return new HoldOutValidation<>(supervisedDataset);
    }

    @Override
    public Result evaluate(Classifier<?, ? super D, ? super T> classifier, SupervisedDataset<? extends D, ? extends T> supervisedDataset) {
        Model<?, ? super D> model = classifier.fit(supervisedDataset.getDataFrame(), supervisedDataset.getTarget());
        return evaluate(model, supervisedDataset);
    }

    /**
     * Evaluate result.
     *
     * @param model the model
     * @return the result
     */
    public Result evaluate(Model<?, ? super D> model, SupervisedDataset<? extends D, ? extends T> supervisedDataset) {
        Predictions holdOutPredictions = model.predict(holdoutSupervisedDataset.getDataFrame());
        Predictions inSamplePredictions = model.predict(supervisedDataset.getDataFrame());

        ConfusionMatrix confusionMatrix = ConfusionMatrix.create(holdOutPredictions, holdoutSupervisedDataset.getTarget());
        List<Metric> metrics = getMetricFactories().stream()
                .map(Metric.Factory::newProducer)
                .map(producer -> {
                    producer.add(Metric.Sample.IN, inSamplePredictions, supervisedDataset.getTarget());
                    producer.add(Metric.Sample.OUT, holdOutPredictions, holdoutSupervisedDataset.getTarget());
                    return producer.produce();
                })
                .collect(Collectors.toCollection(ArrayList::new));
        return Result.create(metrics, Arrays.asList(confusionMatrix));
    }
}
