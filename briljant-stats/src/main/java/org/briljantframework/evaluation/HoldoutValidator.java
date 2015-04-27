package org.briljantframework.evaluation;

import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.result.ConfusionMatrix;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.briljantframework.evaluation.result.Evaluator;
import org.briljantframework.evaluation.result.Result;
import org.briljantframework.vector.Vector;

import java.util.Collections;
import java.util.List;

/**
 * @author Isak Karlsson
 */
public class HoldoutValidator extends AbstractValidator {

  private final DataFrame holdoutX;
  private final Vector holdoutY;

  public HoldoutValidator(List<Evaluator> consumers, DataFrame holdoutX, Vector holdoutY) {
    super(consumers, null);
    this.holdoutX = holdoutX;
    this.holdoutY = holdoutY;
  }

  public static HoldoutValidator withHoldout(DataFrame x, Vector y) {
    return new HoldoutValidator(Evaluator.getDefaultClassificationEvaluators(), x, y);
  }

  @Override
  public Result test(Classifier classifier, DataFrame x, Vector y) {
    Predictor model = classifier.fit(x, y);
    return evaluate(model, x, y);
  }

  public Result evaluate(Predictor predictor, DataFrame x, Vector y) {
    Vector classes = predictor.getClasses();
    EvaluationContext ctx = new EvaluationContext();

    Vector holdOutPredictions = computeClassLabels(holdoutX, predictor, y.getType(), ctx);
    ConfusionMatrix confusionMatrix =
        ConfusionMatrix.compute(holdOutPredictions, holdoutY, classes);
    ctx.setPredictor(predictor);
    ctx.setPredictions(holdOutPredictions);
    ctx.setPartition(new Partition(x, holdoutX, y, holdoutY));

    getEvaluators().forEach(mc -> mc.accept(ctx));
    predictor.evaluation(ctx);
    return Result.create(collect(ctx.builders()), Collections.singletonList(confusionMatrix));
  }

}
