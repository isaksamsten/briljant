package org.briljantframework.evaluation;

import org.briljantframework.classification.Predictor;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.evaluation.measure.Measure;
import org.briljantframework.evaluation.result.EvaluationContext;
import org.briljantframework.evaluation.result.Evaluator;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.briljantframework.matrix.Matrices.argmax;

/**
 * Created by isak on 03/10/14.
 * <p>
 */
public abstract class AbstractValidator implements Validator {

  private final List<Evaluator> evaluators;
  private final Partitioner partitioner;

  protected AbstractValidator(List<Evaluator> evaluators, Partitioner partitioner) {
    this.evaluators = new ArrayList<>(evaluators);
    this.partitioner = partitioner;
  }

  @Override
  public List<Evaluator> getEvaluators() {
    return evaluators;
  }

  /**
   * Gets the partition strategy
   * 
   * @return the partition strategy
   */
  @Override
  public Partitioner getPartitioner() {
    return partitioner;
  }

  /**
   * Collect metric producers.
   *
   * @param builders the producers
   * @return the array list
   */
  protected List<Measure> collect(Collection<Measure.Builder<?>> builders) {
    List<Measure> measures = new ArrayList<>();
    for (Measure.Builder<?> builder : builders) {
      measures.add(builder.build());
    }
    return measures;
  }

  /**
   * Computes the class labels. Chooses the 'best' strategy to avoid computing the probability
   * estimation matrix twice.
   *
   * @param predictor the predictor
   * @param type      the the resulting vector type
   * @param ctx       the evaluation context
   * @return a vector of class-labels produced for {@code predictor} using the hold-out dataset
   */
  protected Vector computeClassLabels(DataFrame holdoutX, Predictor predictor, VectorType type,
                                      EvaluationContext ctx) {
    Vector classes = predictor.getClasses();
    Vector.Builder builder = type.newBuilder();
    if (predictor.getCharacteristics().contains(Predictor.Characteristics.ESTIMATOR)) {
      DoubleMatrix estimate = predictor.estimate(holdoutX);
      ctx.setEstimation(estimate);
      for (int i = 0; i < estimate.rows(); i++) {
        builder.set(i, classes, argmax(estimate.getRow(i)));
      }
      return builder.build();
    } else {
      return predictor.predict(holdoutX);
    }
  }
}
