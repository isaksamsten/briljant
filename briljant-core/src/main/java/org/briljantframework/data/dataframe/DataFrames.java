/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.data.dataframe;

import static java.util.stream.Collectors.maxBy;
import static java.util.stream.Collectors.minBy;
import static org.briljantframework.data.Collectors.withFinisher;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;

import net.mintern.primitive.comparators.IntComparator;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.briljantframework.Check;
import org.briljantframework.array.Array;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.Collectors;
import org.briljantframework.data.Is;
import org.briljantframework.data.Na;
import org.briljantframework.data.SortOrder;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.NaturalOrdering;
import org.briljantframework.data.series.Series;
import org.briljantframework.data.series.Type;
import org.briljantframework.data.series.Vectors;

/**
 * Utility methods for handling {@code DataFrame}s
 * 
 * @author Isak Karlsson
 */
public final class DataFrames {
  public static final int PER_SLICE = 4;

  private DataFrames() {}

  public static DataFrame table(Series a, Series b) {
    Check.dimension(a.size(), b.size());
    Map<Object, Map<Object, Integer>> counts = new HashMap<>();
    Set<Object> aUnique = new HashSet<>();
    Set<Object> bUnique = new HashSet<>();
    for (int i = 0; i < a.size(); i++) {
      Object va = a.loc().get(i);
      Object vb = b.loc().get(i);
      Map<Object, Integer> countVb = counts.get(va);
      if (countVb == null) {
        countVb = new HashMap<>();
        counts.put(va, countVb);
      }
      countVb.compute(vb, (key, value) -> value == null ? 1 : value + 1);
      aUnique.add(va);
      bUnique.add(vb);
    }

    DataFrame.Builder df = DataFrame.builder();
    for (Object i : aUnique) {
      Map<Object, Integer> row = counts.get(i);
      if (row == null) {
        for (Object j : bUnique) {
          df.set(i, j, 0);
        }
      } else {
        for (Object j : bUnique) {
          df.set(i, j, row.getOrDefault(j, 0));
        }
      }
    }
    return df.build();
  }

  public static DataFrame concatenate(Collection<? extends DataFrame> dataFrames) {
    throw new UnsupportedOperationException();
  }

  public static DataFrame merge(Collection<? extends DataFrame> dataFrames) {
    throw new UnsupportedOperationException();
  }

  /**
   * Returns a data frame which has an index sorted in ascending order.
   *
   * @param df the data frame
   * @return a new data frame
   */
  public static DataFrame sort(DataFrame df) {
    return sort(df, SortOrder.ASC);
  }

  /**
   * Returns a data frame which has an index sorted in the specified order.
   *
   * @param df the data frame
   * @param order the order
   * @return a new data frame
   */
  public static DataFrame sort(DataFrame df, SortOrder order) {
    Index.Builder index = df.getIndex().newCopyBuilder();
    index.sort(order);
    return df.reindex(df.getColumnIndex(), index.build());
  }

  public static DataFrame sort(DataFrame df, Comparator<Object> comparator) {
    Index.Builder index = df.getIndex().newCopyBuilder();
    index.sort(comparator);
    return df.reindex(df.getColumnIndex(), index.build());
  }

  public static DataFrame sortBy(DataFrame df, Object key) {
    return sortBy(df, key, SortOrder.ASC);
  }

  public static DataFrame sortBy(DataFrame df, Object key, SortOrder order) {
    org.briljantframework.data.series.LocationGetter loc = df.get(key).loc();
    boolean asc = order == SortOrder.ASC;
    IntComparator cmp = asc ? loc::compare : (a, b) -> loc.compare(b, a);
    Index.Builder index = df.getIndex().newCopyBuilder();
    index.sortIterationOrder(cmp);
    return df.reindex(df.getColumnIndex(), index.build());
  }

  public static <T> DataFrame sortBy(DataFrame df, Object key, Class<? extends T> cls,
      Comparator<? super T> cmp) {
    org.briljantframework.data.series.LocationGetter loc = df.get(key).loc();
    Index.Builder index = df.getIndex().newCopyBuilder();
    index.sortIterationOrder((a, b) -> cmp.compare(loc.get(cls, a), loc.get(cls, b)));
    return df.reindex(df.getColumnIndex(), index.build());
  }

  public static DataFrame sortColumns(DataFrame df, Comparator<Object> comparator) {
    return df.reindex(sortIndex(df.getColumnIndex(), comparator), df.getIndex());
  }

  private static Index sortIndex(Index index, Comparator<Object> comparator) {
    Index.Builder b = index.newCopyBuilder();
    b.sort(comparator);
    return b.build();
  }

  /**
   * Presents a summary of the given data frame. For each column of {@code df} the returned summary
   * contains one row. Each row is described by four values, the {@code min}, {@code max},
   * {@code mean} and {@code mode}. The first three are presented for numerical columns and the
   * fourth for categorical.
   *
   * <pre>
   * {@code
   * > DataFrame df = MixedDataFrame.of(
   *    "a", Series.of(1, 2, 3, 4, 5, 6),
   *    "b", Series.of("a", "b", "b", "b", "e", "f"),
   *    "c", Series.of(1.1, 1.2, 1.3, 1.4, 1.5, 1.6)
   *  );
   * 
   * > DataFrames.summary(df)
   *    mean   var    std    min    max    mode
   * a  3.500  3.500  1.871  1.000  6.000  6
   * b  NA     NA     NA     NA     NA     f
   * c  1.350  0.035  0.187  1.100  1.600  1.1
   * 
   * [3 rows x 6 columns]
   * }
   * </pre>
   *
   * @param df the data frame
   * @return a data frame summarizing {@code df}
   */
  public static DataFrame summary(DataFrame df) {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    builder.newColumn("mean", Type.DOUBLE).newColumn("var", Type.DOUBLE)
        .newColumn("std", Type.DOUBLE).newColumn("min", Type.DOUBLE).newColumn("max", Type.DOUBLE)
        .newColumn("mode", Type.OBJECT);

    for (Object columnKey : df.getColumnIndex().keySet()) {
      Series column = df.get(columnKey);
      if (Is.numeric(column)) {
        StatisticalSummary summary = column.collect(Number.class, Collectors.statisticalSummary());
        builder.set(columnKey, "mean", summary.getMean())
            .set(columnKey, "var", summary.getVariance())
            .set(columnKey, "std", summary.getStandardDeviation())
            .set(columnKey, "min", summary.getMin()).set(columnKey, "max", summary.getMax());
      }
      builder.set(columnKey, "mode", column.collect(Collectors.mode()));
    }
    return builder.build();
  }

  public static Series sum(DataFrame df) {
    return df.reduce(Vectors::sum);
  }

  public static Series mean(DataFrame df) {
    return df.reduce(Vectors::mean);
  }

  public static Series min(DataFrame df) {
    return df.collect(Object.class,
        withFinisher(minBy(NaturalOrdering.ascending()), (o) -> o.isPresent() ? o.get() : null));
  }

  public static Series max(DataFrame df) {
    return df.collect(Object.class,
        withFinisher(maxBy(NaturalOrdering.ascending()), (o) -> o.isPresent() ? o.get() : null));
  }

  /**
   * Same as {@link #permute(DataFrame, java.util.Random)} with a static random number generator.
   *
   * @param in the input data frame
   * @return a permuted copy of {@code in}
   */
  public static DataFrame permute(DataFrame in) {
    return permute(in, ThreadLocalRandom.current());
  }

  /**
   * Returns a row-permuted copy of {@code df}. This implementations uses the Fisher–Yates shuffle
   * (named after Ronald Fisher and Frank Yates), also known as the Knuth shuffle (after Donald
   * Knuth), which is an algorithm for generating a random permutation of a finite set.
   *
   * <p>
   * The permutation is only visible when accessing values using {@link LocationGetter
   * location-based indexing}.
   *
   * @param df the input {@code DataFrame}
   * @param random the random number generator used
   * @return a permuted copy of input
   */
  public static DataFrame permute(DataFrame df, Random random) {
    DataFrame.Builder builder = df.newCopyBuilder();
    LocationSetter loc = builder.loc();
    for (int i = builder.size(0); i > 1; i--) {
      loc.swapRecords(i - 1, random.nextInt(i));
    }
    return builder.build();
  }

  /**
   * Drop columns with NA
   *
   * @param x the data frame
   * @return a new data frame with no missing values
   */
  public static DataFrame dropMissingColumns(DataFrame x) {
    throw new UnsupportedOperationException("Not implemented yet");
  }

  /**
   * Drop cases (rows) with NA
   *
   * @param x the data frame
   * @return a new data frame with no missing values
   */
  public static DataFrame dropIncompleteCases(DataFrame x) {
    throw new UnsupportedOperationException("not implemented yet");
  }

  /**
   * Return the data frame as an {@link Array} applying the supplied function to each element.
   *
   * @param t the class
   * @param function the function
   * @param <T> the input type
   * @param <R> the output type
   * @return a new array
   */
  public static <T, R> Array<R> toArray(Class<T> t, DataFrame x,
      Function<? super T, ? extends R> function) {
    Array<R> array = Arrays.array(x.size(0), x.size(1));
    for (int j = 0; j < x.size(1); j++) {
      for (int i = 0; i < x.size(0); i++) {
        array.set(i, j, function.apply(x.loc().get(t, i, j)));
      }
    }
    return array;
  }

  /**
   * Return the data frame as an {@linkplain Array array}
   *
   * <pre>
   * DataFrame df = DataFrame.of(&quot;A&quot;, Series.of(&quot;a&quot;, &quot;b&quot;, &quot;c&quot;), &quot;B&quot;, Series.of(1, 2, 3));
   * DataFrames.toArray(String.class, df);
   * DataFrames.toArray(Double.class, df);
   * </pre>
   *
   * produces,
   *
   * <pre>
   * array([[a, 1],
   *        [b, 2],
   *        [c, 3]])
   *
   * array([[NaN, 1.0],
   *        [NaN, 2.0],
   *        [NaN, 3.0]])
   * </pre>
   *
   * Note that the array is populated with {@code NA} if the conversion fails; also note that this
   * will result in surprising results if the type is Integer, where {@code NA} is represented as
   * {@code Integer.MIN_VALUE}; use {@link org.briljantframework.data.Is#NA(Object)} to find
   * {@code NA} values in the resulting array
   *
   * @param t the type of the array
   * @param <T> the type
   * @return an array with the given type
   */
  public static <T> Array<T> toArray(Class<T> t, DataFrame x) {
    return toArray(t, x, Function.identity());
  }

  /**
   * Return this data frame as a double array applying the given function to each element
   *
   * @param operator the operator
   * @return a new double array
   */
  public static DoubleArray toDoubleArray(DataFrame x, DoubleUnaryOperator operator) {
    DoubleArray array = Arrays.doubleArray(x.size(0), x.size(1));
    for (int j = 0; j < x.size(1); j++) {
      for (int i = 0; i < x.size(0); i++) {
        array.set(i, j, operator.applyAsDouble(x.loc().getDouble(i, j)));
      }
    }
    return array;
  }

  public static DoubleArray toDoubleArray(DataFrame x) {
    return toDoubleArray(x, DoubleUnaryOperator.identity());
  }

  /**
   * Generates a string representation of a maximum of {@code 10} rows.
   *
   * @param df the data frame
   * @return a tabular string representation
   */
  public static String toString(DataFrame df) {
    return toString(df, 100);
  }

  /**
   * Generates a string representation from {@code dataFrame}.
   * <p>
   * For example:
   * <p>
   *
   * <pre>
   *     a    b    c
   * 0   2    3    3
   * 1   1    NA   3
   * </pre>
   *
   * @param df the data frame
   * @param max the maximum number of rows to show
   * @return a tabular string representation
   */
  public static String toString(DataFrame df, int max) {
    max = df.size(0) > max ? PER_SLICE : df.size(0);
    Index index = df.getIndex();
    Index columnIndex = df.getColumnIndex();

    int longestRecordValue = longestRecordValue(max, index);
    int[] longestColumnValue = longestColumnValues(df, max, columnIndex, 2);

    StringBuilder builder = new StringBuilder();
    padWithSpace(builder, longestRecordValue);
    int column = 0;
    for (Object columnKey : columnIndex.keySet()) {
      String safeColumnKey = Na.toString(columnKey);
      int columnKeyLength = safeColumnKey.length();
      if (longestColumnValue[column] < columnKeyLength) {
        longestColumnValue[column] = columnKeyLength;
      }
      int i = longestColumnValue[column++] - columnKeyLength;
      padWithSpace(builder, (i + 2));
      builder.append(safeColumnKey);
    }
    builder.append("\n");
    for (int i = 0; i < df.size(0); i++) {
      Object recordKey = index.get(i);
      String safeRecordKey = Na.toString(recordKey);
      builder.append(safeRecordKey);
      padWithSpace(builder, (longestRecordValue - safeRecordKey.length()));

      for (int j = 0; j < df.size(1); j++) {
        Object columnKey = columnIndex.get(j);
        String str = Na.toString(df.get(String.class, recordKey, columnKey));
        padWithSpace(builder, (longestColumnValue[j] - str.length()) + 2);
        builder.append(str);
      }
      builder.append("\n");
      if (i >= max) {
        int left = df.size(0) - i - 1;
        if (left > max) {
          padWithSpace(builder, longestRecordValue);
          for (int j = 0; j < df.size(1); j++) {
            String str = "...";
            padWithSpace(builder, (longestColumnValue[j] - str.length()) + 2);
            builder.append(str);
          }
          builder.append("\n");
          i += left - max - 1;
        }
      }

    }
    return builder.append("\n[").append(df.size(0)).append(" rows x ").append(df.size(1))
        .append(" columns]").toString();
  }

  private static void padWithSpace(StringBuilder builder, int pad) {
    for (int i = 0; i < pad; i++) {
      builder.append(" ");
    }
  }

  private static int[] longestColumnValues(DataFrame df, int max, Index columnIndex, int padding) {
    return columnIndex.keySet().stream().map(df::get).mapToInt(v -> {
      int longest = df.size(0) > max * 2 ? 3 : 0;
      for (int i = 0; i < df.size(0); i++) {
        Object recordKey = df.getIndex().get(i);
        int length = Na.toString(v.get(String.class, recordKey)).length();
        if (length > longest) {
          longest = length;
        }
        if (i >= max) {
          int left = df.size(0) - i - 1;
          if (left > max) {
            i += left - max - 1;
          }
        }
      }
      return longest + padding;
    }).toArray();
  }

  private static int longestRecordValue(int max, Index index) {
    int longest = index.size() > max * 2 ? 3 : 0;
    for (int i = 0; i < index.size(); i++) {
      int length = Na.toString(index.get(i)).length();
      if (length > longest) {
        longest = length;
      }
      if (i >= max) {
        int left = index.size() - i - 1;
        if (left > max) {
          i += left - max - 1;
        }
      }
    }
    return longest;
  }
}
