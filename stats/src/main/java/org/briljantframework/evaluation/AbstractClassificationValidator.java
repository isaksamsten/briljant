package org.briljantframework.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.briljantframework.evaluation.measure.Measure;
import org.briljantframework.evaluation.result.Evaluator;

/**
 * Created by isak on 03/10/14.
 * <p>
 */
public abstract class AbstractClassificationValidator implements ClassificationValidator {

  private final List<Evaluator> evaluators;
  private final Partitioner partitioner;

  protected AbstractClassificationValidator(List<Evaluator> evaluators, Partitioner partitioner) {
    this.evaluators = new ArrayList<>(evaluators);
    this.partitioner = partitioner;
  }

  public List<Evaluator> getEvaluators() {
    return evaluators;
  }

  /**
   * Gets the partition strategy
   * 
   * @return the partition strategy
   */
  protected Partitioner getPartitioner() {
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
}
