package org.briljantframework.evaluation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.result.*;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vectors;

/**
 * Created by isak on 05/10/14.
 */
public class HoldOutValidation extends AbstractClassificationEvaluator {


  private final DataFrame holdoutX;
  private final Vector holdoutY;

  /**
   * Instantiates a new Abstract evaluator.
   *
   * @param holdoutX
   */
  public HoldOutValidation(MeasureProvider measureProvider, DataFrame holdoutX, Vector holdoutY) {
    super(measureProvider, null);
    this.holdoutX = holdoutX;
    this.holdoutY = holdoutY;
  }

  /**
   * Create hold out validation.
   *
   * @return the hold out validation
   */
  public static HoldOutValidation withHoldout(DataFrame x, Vector y) {
    return new HoldOutValidation(Measures.getDefaultClassificationMeasures(), x, y);
  }

  @Override
  public Result evaluate(Classifier classifier, DataFrame x, Vector y) {
    Predictor model = classifier.fit(x, y);
    return evaluate(model, x, y);
  }

  /**
   * Evaluate result.
   *
   * @param predictor the model
   * @return the result
   */
  public Result evaluate(Predictor predictor, DataFrame x, Vector y) {
    Vector domain = Vectors.unique(holdoutY, y);
    Vector holdOutPredictions = predictor.predict(holdoutX);
    Vector inSamplePredictions = predictor.predict(x);
    DoubleMatrix holdOutProba = predictor.predictProba(holdoutX);
    DoubleMatrix inSampleProba = predictor.predictProba(x);

    ConfusionMatrix confusionMatrix = ConfusionMatrix.compute(holdOutPredictions, holdoutY, domain);
    List<Measure> measures =
        getMeasureProvider()
            .getMeasures(domain)
            .stream()
            .map(
                producer -> {
                  producer.compute(Sample.IN, predictor, x, inSamplePredictions, inSampleProba, y);
                  producer.compute(Sample.OUT, predictor, holdoutX, holdOutPredictions,
                      holdOutProba, holdoutY);
                  return producer.build();
                }).collect(Collectors.toCollection(ArrayList::new));
    return Result.create(measures, Collections.singletonList(confusionMatrix));
  }
}
