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

package org.briljantframework.dataframe;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import org.apache.commons.math3.stat.descriptive.StatisticalSummary;
import org.briljantframework.Utils;
import org.briljantframework.dataframe.join.InnerJoin;
import org.briljantframework.dataframe.join.JoinOperation;
import org.briljantframework.dataframe.join.LeftOuterJoin;
import org.briljantframework.dataframe.join.OuterJoin;
import org.briljantframework.dataframe.transform.RemoveIncompleteCases;
import org.briljantframework.dataframe.transform.RemoveIncompleteColumns;
import org.briljantframework.dataframe.transform.Transformation;
import org.briljantframework.index.Index;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.io.EntryReader;
import org.briljantframework.io.StringDataEntry;
import org.briljantframework.vector.Scale;
import org.briljantframework.vector.Vec;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
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

  public static DataFrame loadCsv(String file) throws IOException {
    CsvParserSettings settings = new CsvParserSettings();
    settings.getFormat().setDelimiter(',');
//    settings.setLineSeparatorDetectionEnabled(true);
    CsvParser parser = new CsvParser(settings);
    parser.beginParsing(new BufferedReader(new FileReader(new File(file))));

    Index.Builder columnIndex = new HashIndex.Builder();
    for (String s : parser.parseNext()) {
      columnIndex.add(s);
    }
    DataFrame.Builder df = new MixedDataFrame.Builder();
    DataEntry entry = new StringDataEntry(parser.parseNext());
    for (int col = 0; col < entry.size() && entry.hasNext(); col++) {
      String value = entry.nextString();
      Object val;
      if ((val = Integer.parseInt(value)) != null) {
        df.add(VectorType.INT);
      } else if ((val = Double.parseDouble(value)) != null) {
        df.add(VectorType.DOUBLE);
      } else if ("true".equalsIgnoreCase(value)) {
        val = true;
        df.add(VectorType.LOGICAL);
      } else if ("false".equalsIgnoreCase(value)) {
        val = false;
        df.add(VectorType.LOGICAL);
      } else {
        val = value;
        df.add(VectorType.from(LocalDate.class));
      }
      df.loc().set(0, col, val);
    }

    try {
      df.read(new EntryReader() {
        private String[] current = null;

        @Override
        public DataEntry next() throws IOException {
          DataEntry de = new StringDataEntry(current);
          current = null;
          return de;
        }

        @Override
        public boolean hasNext() throws IOException {
          if (current == null) {
            current = parser.parseNext();
          }
          return current != null;
        }
      });
    } catch (IOException e) {
      e.printStackTrace();
    }
    parser.stopParsing();
    DataFrame bdf = df.build();
    bdf.setColumnIndex(columnIndex.build());
    return bdf;
  }

  public static DataFrame concat(Collection<? extends DataFrame> dataFrames) {
    if (dataFrames.size() == 1) {
      return dataFrames.iterator().next();
    }
    DataFrame.Builder builder = null;
    Index.Builder columnIndex = new HashIndex.Builder();

    int toRow = 0;
    int currentColumn = 0;
    for (DataFrame df : dataFrames) {
      if (builder == null) {
        builder = df.newBuilder();
      }

      int rows = df.rows();
      for (Index.Entry col : df.getColumnIndex().entrySet()) {
        int toColumn = columnIndex.index(col.key());
        int fromCol = col.index();
        if (toColumn < 0) {
          columnIndex.add(col.key());
          toColumn = currentColumn;
          currentColumn += 1;
        }
        for (int i = 0; i < rows; i++) {
          builder.loc().set(toRow + i, toColumn, df, i, fromCol);
        }
      }
      toRow += rows;
    }
    assert builder != null;
    DataFrame df = builder.build();
    df.setColumnIndex(columnIndex.build());
    return df;
  }

  /**
   * Load data frame using {@code in} and construct a new {@link org.briljantframework.dataframe.DataFrame}
   * using the function {@code f} which should return a {@link org.briljantframework.dataframe.DataFrame.Builder}
   * using the column names and the column types. The values from {@code in} are read to the {@code
   * DataFrame.Builder} and returned as the DataFrame created by {@link
   * org.briljantframework.dataframe.DataFrame.Builder#build()}. <p>
   * <code><pre>
   *    DataFrame dataframe =
   *        DataFrames.load(MixedDataFrame.Builder::new, new CsvInputStream("iris.txt"));
   * </pre></code>
   *
   * @param f  the producing {@code BiFunction}
   * @param in the input stream
   * @return a new dataframe
   */
  public static DataFrame load(Function<Collection<? extends VectorType>, DataFrame.Builder> f,
                               DataInputStream in) throws IOException {
    try {
      Collection<VectorType> types = in.readColumnTypes();
      Collection<Object> names = in.readColumnIndex();
      DataFrame df = f.apply(types).read(in).build();
      df.setColumnIndex(HashIndex.from(names));
      return df;
    } finally {
      if (in != null) {
        in.close();
      }
    }
  }

  /**
   * Presents a summary of the given data frame. For each column of {@code df}
   * the returned summary contains one row. Each row is described by four
   * values, the {@code min}, {@code max}, {@code mean} and {@code mode}. The first
   * three are presented for numerical columns and the fourth for categorical.
   *
   * @param df the data frame
   * @return a data frame summarizing {@code df}
   */
  public static DataFrame summary(DataFrame df) {
    DataFrame.Builder builder = new MixedDataFrame.Builder(
        Arrays.asList(
            VectorType.DOUBLE, VectorType.DOUBLE, VectorType.DOUBLE, VectorType.STRING
        )
    );
    for (int j = 0; j < df.columns(); j++) {
      Vector column = df.loc().get(j);
      if (column.getType().getScale() == Scale.NUMERICAL) {
        StatisticalSummary desc = Vec.statistics(column);
        double mean = desc.getMean();
        double min = desc.getMin();
        double max = desc.getMax();
        builder.loc().set(j, 0, mean);
        builder.loc().set(j, 1, min);
        builder.loc().set(j, 2, max);
      } else {
        throw new UnsupportedOperationException();
//        Object mode = Vec.mode(column);
//        builder.set(j, 3, mode);
      }
    }
    DataFrame bdf = builder.build();
    bdf.setColumnIndex(HashIndex.from(
        Arrays.asList("Mean", "Min", "Max", "Mode")
    ));
    return bdf;
  }

  /**
   * Returns a row-permuted copy of {@code df}. This implementations uses the Fisher–Yates shuffle
   * (named after Ronald Fisher and Frank Yates), also known as the Knuth shuffle (after Donald
   * Knuth), which is an algorithm for generating a random permutation of a finite set — df plain
   * terms, for randomly shuffling the finite set.
   *
   * @param df     the input {@code DataFrame}
   * @param random the random number generator used
   * @return a permuted copy of {@code df}
   */
  public static DataFrame permuteRows(DataFrame df, Random random) {
    DataFrame.Builder builder = df.newCopyBuilder();
    for (int i = builder.rows(); i > 1; i--) {
      builder.loc().swapRecords(i - 1, random.nextInt(i));
    }
    DataFrame bdf = builder.build();
    bdf.setColumnIndex(df.getColumnIndex());
    return bdf;
  }

  /**
   * Same as {@link #permuteRows(DataFrame, java.util.Random)} with a static random number
   * generator.
   *
   * @param in the input data frame
   * @return a permuted copy of {@code in}
   */
  public static DataFrame permuteRows(DataFrame in) {
    return permuteRows(in, Utils.getRandom());
  }

  /**
   * Returns a column-permuted copy of {@code in}. See {@link #permuteRows(DataFrame)} for details.
   *
   * @param in input data frame
   * @return a column permuted copy
   * @see #permuteRows(DataFrame)
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
  public static String toTabularString(DataFrame dataFrame) {
    return toTabularString(dataFrame, 100);
  }

  /**
   * Generates a string representation from {@code dataFrame}. <p> For example: <p>
   *
   * <pre>
   *        a    b    c
   *  [0,]  2    3    3
   *  [1,]  1    NA   3
   * </pre>
   *
   * @param dataFrame the dataframe
   * @param max       the maximum number of rows to show
   * @return a tabular string representation
   */
  public static String toTabularString(DataFrame dataFrame, int max) {
    Index recordIndex = dataFrame.getRecordIndex();
    Index columnIndex = dataFrame.getColumnIndex();

//    ImmutableTable.Builder<Object, Object, Object> b = ImmutableTable.builder();
//    b.put(0, 0, " Index");
//
//    for (int j = 0; j < dataFrame.columns(); j++) {
//      b.put(0, j + 1, columnIndex.get(j));
//    }
//
//    for (int i = 0; i < dataFrame.rows() && i < max; i++) {
//      b.put(i + 1, 0, String.format("[%s,] ", recordIndex.get(i)));
//      for (int j = 0; j < dataFrame.columns(); j++) {
//        b.put(i + 1, j + 1, dataFrame.toString(i, j));
//      }
//    }
//
    StringBuilder builder =
        new StringBuilder(dataFrame.getClass().getSimpleName()).append(" (")
            .append(dataFrame.rows()).append("x").append(dataFrame.columns()).append(")\n");
//    Utils.prettyPrintTable(builder, b.build(), 1, 2, false, false);
    return builder.toString();
  }

  public static final class Builder {

    private Builder() {

    }
  }
}
