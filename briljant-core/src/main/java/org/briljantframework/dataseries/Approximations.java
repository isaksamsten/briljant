package org.briljantframework.dataseries;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.transform.PipelineTransformation;

/**
 * @author Isak Karlsson
 */
public final class Approximations {

  private static final List<String> alphabet = Collections.unmodifiableList(Arrays.asList("a", "b",
      "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s", "t",
      "u", "v", "x", "y", "z"));

  private Approximations() {}

  public static List<String> getAlphabet(int size) {
    checkArgument(size < alphabet.size(), "Alphabet size to large.");
    return alphabet.subList(0, size);
  }

  /**
   * Performs Piecewise Aggregate Approximation, reducing each row of {@code in} to length
   * {@code size}
   * 
   * @param in the input data frame
   * @param size the resulting time series size
   * @return a new data frame with {@link org.briljantframework.dataframe.DataFrame#columns()}
   *         equals to {@code size}
   */
  public static DataFrame paa(DataFrame in, int size) {
    return paa(size).transform(in);
  }

  /**
   * Returns a {@link org.briljantframework.dataframe.transform.Transformation} that reduces the
   * length of each row-vector to {@code size}.
   * 
   * @param size the resulting time series size
   * @return a transformation
   */
  public static AggregateApproximation paa(int size) {
    return new AggregateApproximation(size);
  }

  /**
   * Returns a {@link org.briljantframework.dataframe.transform.Transformation} that reduces the
   * size and transforms each row in the input data frame to
   * 
   * @param alphabet
   * @return
   */
  public static AggregateApproximation sax(List<String> alphabet) {
    return new AggregateApproximation(new SymbolicAggregator(alphabet));
  }

  public static DataFrame sax(DataFrame in, int size) {
    return sax(in, size, getAlphabet(size));
  }

  public static DataFrame sax(DataFrame in, int size, String... alphabet) {
    return sax(in, size, Arrays.asList(alphabet));
  }

  public static DataFrame sax(DataFrame in, int size, List<String> alphabet) {
    checkArgument(alphabet.size() > 1, "Alphabet size must be larger than 1.");
    return PipelineTransformation.of(paa(size), sax(alphabet)).transform(in);
  }

}
