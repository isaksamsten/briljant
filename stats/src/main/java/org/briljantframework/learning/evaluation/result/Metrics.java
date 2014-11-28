package org.briljantframework.learning.evaluation.result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Created by isak on 03/10/14.
 */
public class Metrics {

  /**
   * The constant ERROR.
   */
  public static final Class<ErrorRate> ERROR = ErrorRate.class;

  /**
   * The constant CLASSIFICATION.
   */
  public static final List<Metric.Factory> CLASSIFICATION = Collections.unmodifiableList(Arrays
      .asList(ErrorRate.getFactory(), Accuracy.getFactory(), AreaUnderCurve.getFactory()));


  private Metrics() {

  }

  /**
   * Collect metric producers.
   *
   * @param producers the producers
   * @return the array list
   */
  public static ArrayList<Metric> collect(List<Metric.Producer> producers) {
    return collect(producers, ArrayList::new);
  }

  public static <T extends List<Metric>> T collect(List<Metric.Producer> producers,
      Supplier<T> supplier) {
    return producers.stream().map(Metric.Producer::produce)
        .collect(Collectors.toCollection(supplier));
  }
}
