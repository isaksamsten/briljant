package org.briljantframework.evaluation.result;

import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

import org.briljantframework.classification.Predictor;
import org.briljantframework.evaluation.Partition;
import org.briljantframework.vector.Vector;

/**
 * Created by isak on 2/13/15.
 */
public interface ResultContext {
  Vector getDomain();

  Partition getPartition();

  Predictor getPredictor();

  Measure.Builder get(Class<? extends Measure> measure);

  Measure.Builder getOrDefault(Class<? extends Measure> measure,
      Supplier<? extends Measure.Builder> supplier);

  boolean containsKey(Class<? extends Measure> measure);

  void put(Class<? extends Measure> measure, Measure.Builder builder);

  Set<Map.Entry<Class<Measure>, Measure.Builder>> entrySet();
}
