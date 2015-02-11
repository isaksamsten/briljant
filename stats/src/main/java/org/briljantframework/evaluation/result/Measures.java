package org.briljantframework.evaluation.result;

import java.util.Arrays;
import java.util.List;

/**
 * Created by isak on 03/10/14.
 */
public final class Measures {

  private static final DefaultClassificationMeasureProvider defaultClassificationProvider =
      new DefaultClassificationMeasureProvider();

  private Measures() {}

  public static MeasureProvider getDefaultClassificationMeasures() {
    return defaultClassificationProvider;
  }

  private static class DefaultClassificationMeasureProvider implements MeasureProvider {
    @Override
    public List<Measure.Builder> getMeasures() {
      return Arrays.asList(new ErrorRate.Builder(), new Accuracy.Builder(),
          new AreaUnderCurve.Builder(), new Brier.Builder());
    }
  }
}
