package org.briljantframework.learning.evaluation;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.learning.Classifier;
import org.briljantframework.learning.evaluation.result.Result;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 02/10/14.
 */
public class Evaluators {

    /**
     * Cross validation.
     *
     * @param classifier the classifier
     * @param container  the storage
     * @param target     the target
     * @param folds      the folds
     * @return the double
     */
    public static Result crossValidation(Classifier classifier, DataFrame container, Vector target, int folds) {
        return CrossValidation.withFolds(folds).evaluate(classifier, container, target);
    }

}
