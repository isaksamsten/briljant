package org.briljantframework.distance;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.briljantframework.dataseries.SymbolicAggregator;
import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.Vector;

import com.google.common.base.Preconditions;

/**
 *
 * @author Isak Karlsson
 */
public class SaxDistance implements Distance {

  private final Map<String, Map<String, Double>> lookup;
  private final double n;

  public SaxDistance(double n, Map<String, Map<String, Double>> lookup) {
    this.lookup = lookup;
    this.n = n;
  }

  public SaxDistance(double n, String... alphabet) {
    this(n, Arrays.asList(alphabet));
  }

  public SaxDistance(double n, List<String> alphabet) {
    this(n, SymbolicAggregator.newLookupTable(alphabet));
  }

  @Override
  public double compute(double a, double b) {
    throw new UnsupportedOperationException();
  }

  @Override
  public double compute(Vector a, Vector b) {
    org.briljantframework.Check.requireType(StringVector.TYPE, a);

    Preconditions.checkArgument(a.size() == b.size());

    double w = a.size();
    double sum = 0;

    for (int i = 0; i < w; i++) {
      String av = a.getAsString(i);
      String bv = b.getAsString(i);
      double value = lookup.get(av).get(bv);
      sum += value * value;
    }
    return Math.sqrt(n / w) * Math.sqrt(sum);
  }

  @Override
  public double max() {
    return Double.POSITIVE_INFINITY;
  }

  @Override
  public double min() {
    return Double.NEGATIVE_INFINITY;
  }
}
