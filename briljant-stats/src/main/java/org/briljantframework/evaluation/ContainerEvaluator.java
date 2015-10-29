package org.briljantframework.evaluation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.supervised.Predictor;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class ContainerEvaluator<P extends Predictor> implements Evaluator<P> {

  private final List<PredictionMeasure<? super P>> measures = new ArrayList<>();

  public boolean add(PredictionMeasure<? super P> predictionMeasure) {
    return measures.add(predictionMeasure);
  }

  public int size() {
    return measures.size();
  }

  public boolean isEmpty() {
    return measures.isEmpty();
  }

  public boolean contains(Object o) {
    return measures.contains(o);
  }

  public boolean remove(Object o) {
    return measures.remove(o);
  }

  public boolean addAll(Collection<? extends PredictionMeasure<? super P>> c) {
    return measures.addAll(c);
  }

  public void clear() {
    measures.clear();
  }

  @Override
  public void accept(EvaluationContext<? extends P> ctx) {
    MeasureCollection<? extends P> measureCollection = ctx.getMeasureCollection();
    P predictor = ctx.getPredictor();
    DataFrame x = ctx.getPartition().getValidationData();
    Vector t = ctx.getPartition().getValidationTarget();
    for (PredictionMeasure<? super P> measure : measures) {
      measureCollection.add(measure, measure.compute(predictor, x, t));
    }
  }
}
