package org.briljantframework.evaluation;

import static org.briljantframework.matrix.Matrices.argmax;

import java.util.Collections;
import java.util.List;

import org.briljantframework.classification.Classifier;
import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.result.*;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public class HoldoutValidator extends AbstractClassificationValidator {
  private final DataFrame holdoutX;
  private final Vector holdoutY;

  public HoldoutValidator(List<Evaluator> consumers, DataFrame holdoutX, Vector holdoutY) {
    super(consumers, null);
    this.holdoutX = holdoutX;
    this.holdoutY = holdoutY;
  }

  public static HoldoutValidator withHoldout(DataFrame x, Vector y) {
    return new HoldoutValidator(Measures.getDefaultClassificationMeasures(), x, y);
  }

  @Override
  public Result test(Classifier classifier, DataFrame x, Vector y) {
    Predictor model = classifier.fit(x, y);
    return evaluate(model, x, y);
  }

  public Result evaluate(Predictor predictor, DataFrame x, Vector y) {
    Vector classes = predictor.getClasses();
    EvaluationContext ctx = new EvaluationContext();

    Vector holdOutPredictions;
    if (predictor.getCharacteristics().contains(Predictor.Characteristics.ESTIMATOR)) {
      DoubleMatrix estimate = predictor.estimate(holdoutX);
      ctx.setEstimation(estimate);

      Vector.Builder builder = y.newBuilder();
      for (int i = 0; i < estimate.rows(); i++) {
        builder.set(i, classes, argmax(estimate.getRowView(i)));
      }
      holdOutPredictions = builder.build();
    } else {
      holdOutPredictions = predictor.predict(holdoutX);
    }


    ConfusionMatrix confusionMatrix =
        ConfusionMatrix.compute(holdOutPredictions, holdoutY, classes);
    ctx.setPredictor(predictor);
    ctx.setPredictions(holdOutPredictions);
    ctx.setPartition(new Partition(x, holdoutX, y, holdoutY));


    getMeasureProvider().forEach(mc -> mc.accept(ctx));
    predictor.evaluation(ctx);
    return Result.create(collect(ctx.builders()), Collections.singletonList(confusionMatrix));
  }
}
