package org.briljantframework.learning.evaluation;

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.column.Column;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.SupervisedDataset;
import org.briljantframework.learning.evaluation.result.Result;

/**
 * Created by isak on 02/10/14.
 */
public class Evaluators {

    /**
     * Cross validation.
     *
     * @param <D>            the type parameter
     * @param <T>            the type parameter
     * @param classifier     the classifier
     * @param container      the storage
     * @param target         the target
     * @param folds          the folds
     * @param datasetCopyTo the factory
     * @param targetCopyTo  the target factory
     * @return the double
     */
    public static <D extends DataFrame<?>, T extends Column> Result crossValidation(Classifier<?, ? super D, ? super T> classifier, D container, T target, int folds, DataFrame.CopyTo<? extends D> datasetCopyTo, Column.CopyTo<? extends T> targetCopyTo) {
        return crossValidation(classifier, new SupervisedDataset<>(container, target, datasetCopyTo, targetCopyTo), folds);
    }

    /**
     * Cross validation.
     *
     * @param <D>        the type parameter
     * @param <T>        the type parameter
     * @param classifier the classifier
     * @param folds      the folds
     * @return the result
     */
    public static <D extends DataFrame<?>, T extends Column> Result crossValidation(Classifier<?, ? super D, ? super T> classifier, SupervisedDataset<? extends D, ? extends T> supervisedDataset, int folds) {
        return CrossValidation.<D, T>withFolds(folds).evaluate(classifier, supervisedDataset);
    }

    /**
     * Split validation.
     *
     * @param <D>        the type parameter
     * @param <T>        the type parameter
     * @param classifier the classifier
     * @param testRatio  the test ration
     * @return the result
     */
    public static <D extends DataFrame<?>, T extends Column> Result splitValidation(Classifier<?, ? super D, ? super T> classifier, SupervisedDataset<? extends D, ? extends T> supervisedDataset, double testRatio) {
        return SplitValidation.<D, T>withTestRatio(testRatio).evaluate(classifier, supervisedDataset);
    }

    /**
     * Hold out validation.
     *
     * @param <D>        the type parameter
     * @param <T>        the type parameter
     * @param classifier the classifier
     * @param train      the train
     * @param test       the test
     * @return the result
     */
    public static <D extends DataFrame<?>, T extends Column> Result holdOutValidation(Classifier<?, ? super D, ? super T> classifier, SupervisedDataset<? extends D, ? extends T> train, SupervisedDataset<? extends D, ? extends T> test) {
        return HoldOutValidation.withHoldout(test).evaluate(classifier, train);
    }

    /**
     * Hold out validation.
     *
     * @param <D>          the type parameter
     * @param <T>          the type parameter
     * @param classifier   the classifier
     * @param trainDataset the train dataset
     * @param trainTarget  the train target
     * @param testDataset  the test dataset
     * @param testTarget   the test target
     * @return the result
     */
    public static <D extends DataFrame<?>, T extends Column> Result holdOutValidation(Classifier<?, ? super D, ? super T> classifier, D trainDataset, T trainTarget, D testDataset, T testTarget) {
        return holdOutValidation(classifier, new SupervisedDataset<>(trainDataset, trainTarget, null, null), new SupervisedDataset<>(testDataset, testTarget, null, null));
    }


}
