package org.briljantframework.evaluation.result;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.briljantframework.vector.Value;

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
    public List<Measure.Builder> getMeasures(Set<Value> domain) {
      return Arrays.asList(new ErrorRate.Builder(domain), new Accuracy.Builder(domain),
          new AreaUnderCurve.Builder(domain), new Brier.Builder(domain));
    }
  }
}
