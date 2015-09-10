/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.data.dataframe;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.briljantframework.Utils;
import org.briljantframework.data.Collectors;
import org.briljantframework.data.dataframe.join.InnerJoin;
import org.briljantframework.data.dataframe.join.JoinOperation;
import org.briljantframework.data.dataframe.join.LeftOuterJoin;
import org.briljantframework.data.dataframe.join.OuterJoin;
import org.briljantframework.data.dataframe.transform.RemoveIncompleteCases;
import org.briljantframework.data.dataframe.transform.RemoveIncompleteColumns;
import org.briljantframework.data.dataframe.transform.Transformation;
import org.briljantframework.data.index.DataFrameLocationSetter;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.Na;
import org.briljantframework.data.Scale;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.VectorType;
import org.briljantframework.data.vector.Vectors;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;

/**
 * Utility methods for handling {@code DataFrame}s <p> Created by Isak Karlsson on 27/11/14.
 */
public final class DataFrames {

  public static final String LEFT_OUTER = "left_outer";
  public static final String OUTER = "outer";
  public static final String INNER = "inner";
  public static final String NO_INTERSECTING_COLUMN_NAMES = "No intersecting column names";
  private static final Transformation removeIncompleteColumns = new RemoveIncompleteColumns();
  private static final Transformation removeIncompleteCases = new RemoveIncompleteCases();
  private static final Map<String, JoinOperation> joinOperations;

  static {
    joinOperations = new HashMap<>();
    joinOperations.put(INNER, InnerJoin.getInstance());
    joinOperations.put(LEFT_OUTER, LeftOuterJoin.getInstance());
    joinOperations.put(OUTER, OuterJoin.getInstance());
  }


  private DataFrames() {
  }


  /**
   * Presents a summary of the given data frame. For each column of {@code df}
   * the returned summary contains one row. Each row is described by four
   * values, the {@code min}, {@code max}, {@code mean} and {@code mode}. The first
   * three are presented for numerical columns and the fourth for categorical.
   *
   * <pre>{@code
   * > DataFrame df = MixedDataFrame.of(
   *    "a", Vector.of(1, 2, 3, 4, 5, 6),
   *    "b", Vector.of("a", "b", "b", "b", "e", "f"),
   *    "c", Vector.of(1.1, 1.2, 1.3, 1.4, 1.5, 1.6)
   *  );
   *
   * > DataFrames.summary(df)
   *    mean   var    std    min    max    mode
   * a  3.500  3.500  1.871  1.000  6.000  6
   * b  NA     NA     NA     NA     NA     f
   * c  1.350  0.035  0.187  1.100  1.600  1.1
   *
   * [3 rows x 6 columns]
   * }</pre>
   *
   * @param df the data frame
   * @return a data frame summarizing {@code df}
   */
  public static DataFrame summary(DataFrame df) {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    builder.set("mean", VectorType.DOUBLE)
        .set("var", VectorType.DOUBLE)
        .set("std", VectorType.DOUBLE)
        .set("min", VectorType.DOUBLE)
        .set("max", VectorType.DOUBLE)
        .set("mode", VectorType.OBJECT);

    for (Object columnKey : df.getColumnIndex().keySet()) {
      Vector column = df.get(columnKey);
      if (column.getType().getScale() == Scale.NUMERICAL) {
        StatisticalSummary summary = column.collect(Number.class, Collectors.statisticalSummary());
        builder.set(columnKey, "mean", summary.getMean())
            .set(columnKey, "var", summary.getVariance())
            .set(columnKey, "std", summary.getStandardDeviation())
            .set(columnKey, "min", summary.getMin())
            .set(columnKey, "max", summary.getMax());
      }
      builder.set(columnKey, "mode", column.collect(Collectors.mode()));
    }
    return builder.build();
  }

  /**
   * Returns a row-permuted copy of {@code df}. This implementations uses the Fisher–Yates shuffle
   * (named after Ronald Fisher and Frank Yates), also known as the Knuth shuffle (after Donald
   * Knuth), which is an algorithm for generating a random permutation of a finite set.
   *
   * <p> The permutation is only visible when accessing values using {@link
   * org.briljantframework.data.index.DataFrameLocationGetter location-based indexing}.
   *
   * @param df     the input {@code DataFrame}
   * @param random the random number generator used
   * @return a permuted copy of input
   */
  public static DataFrame permuteRecords(DataFrame df, Random random) {
    DataFrame.Builder builder = staticRecordCopy(df);
    DataFrameLocationSetter loc = builder.loc();
    for (int i = builder.rows(); i > 1; i--) {
      loc.swapRecords(i - 1, random.nextInt(i));
    }
    DataFrame bdf = builder.build();
    bdf.setColumnIndex(df.getColumnIndex());
    return bdf;
  }

  public static DataFrame.Builder staticRecordCopy(DataFrame df) {
    DataFrame.Builder builder = df.newBuilder();
    for (Object recordKey : df.getRecordIndex().keySet()) {
      // TODO: this will fuck-up when using views
      builder.setRecord(recordKey, Vectors.transferableBuilder(df.getRecord(recordKey)));
    }
    return builder;
  }

  /**
   * Same as {@link #permuteRecords(DataFrame, java.util.Random)} with a static random number
   * generator.
   *
   * @param in the input data frame
   * @return a permuted copy of {@code in}
   */
  public static DataFrame permuteRecords(DataFrame in) {
    return permuteRecords(in, Utils.getRandom());
  }

  /**
   * Returns a column-permuted copy of {@code in}. See {@link #permuteRecords(DataFrame)} for
   * details.
   *
   * @param in input data frame
   * @return a column permuted copy
   * @see #permuteRecords(DataFrame)
   */
  public static DataFrame permuteColumns(DataFrame in) {
    DataFrame.Builder builder = in.newCopyBuilder();
    Random random = Utils.getRandom();
    for (int i = builder.columns(); i > 1; i--) {
      builder.loc().swap(i - 1, random.nextInt(i));
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
    return removeIncompleteColumns.transform(x);
  }

  /**
   * Drop cases (rows) with NA
   *
   * @param x the data frame
   * @return a new data frame with no missing values
   */
  public static DataFrame dropIncompleteCases(DataFrame x) {
    return removeIncompleteCases.transform(x);
  }

  /**
   * Generates a string representation of a maximum of {@code 10} rows.
   *
   * @param dataFrame the data frame
   * @return a tabular string representation
   */
  public static String toString(DataFrame dataFrame) {
    return toString(dataFrame, 100);
  }

  /**
   * Generates a string representation from {@code dataFrame}. <p> For example: <p>
   *
   * <pre>
   *     a    b    c
   * 0   2    3    3
   * 1   1    NA   3
   * </pre>
   *
   * @param df  the data frame
   * @param max the maximum number of rows to show
   * @return a tabular string representation
   */
  public static String toString(DataFrame df, int max) {
    Index recordIndex = df.getRecordIndex();
    Index columnIndex = df.getColumnIndex();

    int longestRecordValue = longestRecordValue(max, recordIndex);
    int[] longestColumnValue = longestColumnValues(df, max, columnIndex);

    StringBuilder builder = new StringBuilder();
    padWithSpace(builder, longestRecordValue);
    int column = 0;
    for (Object columnKey : columnIndex.keySet()) {
      String safeColumnKey = Na.toString(columnKey);
      int columnKeyLength = safeColumnKey.length();
      if (columnKeyLength + 2 > longestColumnValue[column]) {
        longestColumnValue[column] = columnKeyLength + 2;
      }

      builder.append(safeColumnKey);
      padWithSpace(builder, longestColumnValue[column++] - columnKeyLength);
    }
    builder.append("\n");

    int records = 0;
    for (Object recordKey : recordIndex.keySet()) {
      if (records++ > max) {
        break;
      }
      String safeRecordKey = Na.toString(recordKey);
      builder.append(safeRecordKey);
      padWithSpace(builder, longestRecordValue - safeRecordKey.length());
      column = 0;
      for (Object columnKey : columnIndex.keySet()) {
        String str = df.toString(recordKey, columnKey);
        builder.append(str);
        padWithSpace(builder, longestColumnValue[column++] - str.length());
      }
      builder.append("\n");
    }
    return builder.append("\n[")
        .append(df.rows())
        .append(" rows x ")
        .append(df.columns())
        .append(" columns]")
        .toString();
  }

  protected static void padWithSpace(StringBuilder builder, int pad) {
    for (int i = 0; i < pad; i++) {
      builder.append(" ");
    }
  }

  protected static int[] longestColumnValues(DataFrame df, int max, Index columnIndex) {
    return columnIndex.keySet().stream()
        .map(df::get)
        .mapToInt(v -> {
          int longest = 0;
          for (int i = 0, size = v.size(); i < size && i < max; i++) {
            int length = v.loc().toString(i).length();
            if (length > longest) {
              longest = length;
            }
          }
          return longest + 2;
        })
        .toArray();
  }

  protected static int longestRecordValue(int max, Index recordIndex) {
    return recordIndex.keySet().stream()
               .limit(max)
               .map(Na::toString)
               .mapToInt(String::length)
               .max()
               .orElse(0) + 2;
  }

  public static final class Builder {

    private Builder() {

    }
  }
}
