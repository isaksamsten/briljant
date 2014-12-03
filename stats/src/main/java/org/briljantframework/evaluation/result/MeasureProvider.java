package org.briljantframework.evaluation.result;

import java.util.List;

/**
 * A measure provider provides measure builders to be used while evaluating algorithms.
 *
 *
 * For example,
 * 
 * <pre>
 * MeasureProvider measureProvider = () -&gt; Arrays.asList(new Accuracy.Builder());
 * ClassificationEvaluator eval =
 *     new DefaultClassificationEvaluator(measureProvider, new RandomFoldPartitioner(10));
 * 
 * eval.evaluate(classifier, x, y);
 * </pre>
 * 
 * Evaluates {@code classifier} using 10-fold cross-validation
 * 
 * Created by isak on 03/12/14.
 */
public interface MeasureProvider {

  List<Measure.Builder> getMeasures();
}
