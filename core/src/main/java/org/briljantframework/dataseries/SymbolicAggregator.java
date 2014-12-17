package org.briljantframework.dataseries;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.briljantframework.distribution.NormalDistribution;
import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.Type;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.Vectors;

/**
 * <p>
 * Symbolic aggregation (when normalized) is a representation method for data series that allow for
 * lower bounding the distance calculation. It works by transforming the normalized data series into
 * a series of discrete symbols, called words, by mapping each data point using the probability
 * associated with a particular value.
 * </p>
 * 
 * <p>
 * For example, given the alphabet {@code [a,b,c,d]} the following thresholds can be computed from
 * the normal distribution and equally sized regions {@code [-0.674,0.000,0.674]}. Then, if a value
 * is {@code <= -0.674} assign {@code a}, if the value is {@code >= 0.674} assign {@code d}, if
 * value is {@code -0.674 < value < 0} assign b and if {@code 0 < value < 0.674} assign c. That is,
 * {@code [-1, 1, 0.3, -0.2]} becomes {@code [a,d,c,b]}.
 * </p>
 * 
 * <p>
 * The {@link org.briljantframework.dataseries.SymbolicAggregator} is often coupled with the
 * {@link org.briljantframework.dataseries.MeanAggregator}. For example,
 * 
 * <pre>
 * Transformation sax =
 *     PipelineTransformation.of(new AggregateApproximation(new MeanAggreagator(5)),
 *         new AggreagetApproximation(new SymbolicAggregator(&quot;a&quot;, &quot;b&quot;, &quot;c&quot;, &quot;d&quot;)));
 * sax.transform(x);
 * 
 * // Alternatively
 * Approximations.sax(x, 5, &quot;a&quot;, &quot;b&quot;, &quot;c&quot;, &quot;d&quot;);
 * </pre>
 * 
 * </p>
 *
 * @author Isak Karlsson
 */
public class SymbolicAggregator implements Aggregator {
  private final Vector alphabet;
  private final Vector thresholds;

  /**
   * Constructs a symbolic aggregator using {@code alphabet}
   * 
   * @param alphabet the alphabet
   */
  public SymbolicAggregator(Vector alphabet) {
    this.alphabet = alphabet;
    thresholds = calculateThresholds(alphabet);
  }

  /**
   * Constructs a symbolic aggregator using {@code alphabet}
   * 
   * @param alphabet the alphabet
   */
  public SymbolicAggregator(List<String> alphabet) {
    this(new StringVector(alphabet));
  }

  /**
   * Constructs a symbolic aggregator using {@code alphabet}
   *
   * @param alphabet the alphabet
   */
  public SymbolicAggregator(String... alphabet) {
    this(new StringVector(Arrays.asList(alphabet)));
  }

  /**
   * Gets the lookup table, used for similarity calculations, as produced by {@code alphabet}
   * 
   * @param alphabet the alphabet
   * @return the lookup table
   */
  public static Map<String, Map<String, Double>> newLookupTable(List<String> alphabet) {
    StringVector vector = new StringVector(alphabet);
    return createLookupTable(vector, calculateThresholds(vector));
  }

  /*
   * Compute the thresholds for the alphabet using the normal distribution. Given an alphabet A,
   * computes the thresholds as [ppf(1/|A|), ppf(2/|A|), ..., ppf((|A|-1)/|A|)].
   */
  private static Vector calculateThresholds(Vector alphabet) {
    double prob = 1.0 / alphabet.size();
    int length = alphabet.size() - 1;
    return NormalDistribution.ppf(Vectors.linspace(prob, 1.0 - prob, length));
  }

  /*
   * Creates a lookup table for the similarity between two items in alphabet
   */
  private static Map<String, Map<String, Double>> createLookupTable(Vector alphabet,
      Vector thresholds) {
    Map<String, Map<String, Double>> tab = new HashMap<>();
    for (int r = 0; r < alphabet.size(); r++) {
      Map<String, Double> sub = new HashMap<>();
      tab.put(alphabet.getAsString(r), sub);
      for (int c = 0; c < alphabet.size(); c++) {
        if (Math.abs(r - c) <= 1) {
          sub.put(alphabet.getAsString(c), 0.0);
        } else {
          sub.put(alphabet.getAsString(c),
              thresholds.getAsDouble(Math.max(r, c) - 1) - thresholds.getAsDouble(Math.min(r, c)));
        }
      }
    }
    return tab;
  }

  /**
   * Gets the lookup table, used for computing the similarity, produced by the alphabet used for
   * this aggregator
   * 
   * @return a lookup table
   */
  public Map<String, Map<String, Double>> getLookupTable() {
    return createLookupTable(alphabet, thresholds);
  }

  @Override
  public Vector.Builder partialAggregate(Vector in) {
    StringVector.Builder sax = new StringVector.Builder(0, in.size());
    for (int j = 0; j < in.size(); j++) {
      double value = in.getAsDouble(j);
      if (value <= thresholds.getAsDouble(0)) {
        sax.set(j, alphabet.getAsString(0));
      } else if (value >= thresholds.getAsDouble(thresholds.size() - 1)) {
        sax.set(j, alphabet.getAsString(alphabet.size() - 1));
      } else {
        int index = 0;
        for (int k = 0; k < thresholds.size(); k++) {
          if (thresholds.getAsDouble(k) <= value) {
            index = k;
          } else {
            break;
          }
        }
        sax.set(j, alphabet.getAsString(index + 1));
      }
    }
    return sax;
  }

  @Override
  public Type getAggregatedType() {
    return StringVector.TYPE;
  }
}
