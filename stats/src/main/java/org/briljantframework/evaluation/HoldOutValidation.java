package org.briljantframework.evaluation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.ClassifierModel;
import org.briljantframework.classification.Label;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.result.*;
import org.briljantframework.vector.Vector;

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
    ClassifierModel model = classifier.fit(x, y);
    return evaluate(model, x, y);
  }

  /**
   * Evaluate result.
   *
   * @param model the model
   * @return the result
   */
  public Result evaluate(ClassifierModel model, DataFrame x, Vector y) {
    List<Label> holdOutPredictions = model.predict(holdoutX);
    List<Label> inSamplePredictions = model.predict(x);

    ConfusionMatrix confusionMatrix = ConfusionMatrix.compute(holdOutPredictions, holdoutY);
    List<Measure> measures = getMeasureProvider().getMeasures().stream().map(producer -> {
      producer.compute(Measure.Sample.IN, inSamplePredictions, y);
      producer.compute(Measure.Sample.OUT, holdOutPredictions, holdoutY);
      return producer.build();
    }).collect(Collectors.toCollection(ArrayList::new));
    return Result.create(measures, Collections.singletonList(confusionMatrix));
  }
}
