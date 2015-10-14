package org.briljantframework.evaluation;

import java.util.List;
import java.util.function.Supplier;

import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.evaluation.partition.Partition;
import org.briljantframework.supervised.Predictor;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public interface EvaluationContext {

  Partition getPartition();

  Vector getPredictions();

  DoubleArray getEstimation();

  Predictor getPredictor();

  <T extends Measure, C extends Measure.Builder<T>> C getOrDefault(Class<T> measure,
      Supplier<C> supplier);

  List<Measure> getMeasures();
}
