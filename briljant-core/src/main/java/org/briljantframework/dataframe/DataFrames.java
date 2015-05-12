package org.briljantframework.dataframe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import org.briljantframework.Utils;
import org.briljantframework.dataframe.join.InnerJoin;
import org.briljantframework.dataframe.join.JoinOperation;
import org.briljantframework.dataframe.join.LeftOuterJoin;
import org.briljantframework.dataframe.join.OuterJoin;
import org.briljantframework.dataframe.transform.RemoveIncompleteCases;
import org.briljantframework.dataframe.transform.RemoveIncompleteColumns;
import org.briljantframework.dataframe.transform.Transformation;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.io.EntryReader;
import org.briljantframework.io.StringDataEntry;
import org.briljantframework.stat.DescriptiveStatistics;
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
  private static final Map<String, JoinOperation> joinOperations =
      ImmutableMap.of(INNER, InnerJoin.getInstance(),
                      LEFT_OUTER, LeftOuterJoin.getInstance(),
                      OUTER, OuterJoin.getInstance());

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
      if ((val = Ints.tryParse(value)) != null) {
        df.addColumnBuilder(Vec.INT);
      } else if ((val = Doubles.tryParse(value)) != null) {
        df.addColumnBuilder(Vec.DOUBLE);
      } else if ("true".equalsIgnoreCase(value)) {
        val = true;
        df.addColumnBuilder(Vec.BIT);
      } else if ("false".equalsIgnoreCase(value)) {
        val = false;
        df.addColumnBuilder(Vec.BIT);
      } else {
        val = value;
        df.addColumnBuilder(Vec.typeOf(LocalDate.class));
      }
      df.set(0, col, val);
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
    return df.build().setColumnIndex(columnIndex.build());
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
          builder.set(toRow + i, toColumn, df, i, fromCol);
        }
      }
      toRow += rows;
    }
    assert builder != null;
    return builder.build().setColumnIndex(columnIndex.build());
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
      return f.apply(types).read(in).build().setColumnIndex(HashIndex.from(names));
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
            Vec.DOUBLE, Vec.DOUBLE, Vec.DOUBLE, Vec.STRING
        )
    );
    for (int j = 0; j < df.columns(); j++) {
      Vector column = df.get(j);
      if (column.getType().getScale() == Scale.NUMERICAL) {
        DescriptiveStatistics desc = Vec.statistics(column);
        double mean = desc.getMean();
        double min = desc.getMin();
        double max = desc.getMax();
        builder.set(j, 0, mean).set(j, 1, min).set(j, 2, max);
      } else {
        Object mode = Vec.mode(column);
        builder.set(j, 3, mode);
      }
    }
    return builder.build().setColumnIndex(HashIndex.from(
        Arrays.asList("Mean", "Min", "Max", "Mode")
    ));
  }

  /**
   * Returns a row-permuted copy of {@code in}. This implementations uses the Fisher–Yates shuffle
   * (named after Ronald Fisher and Frank Yates), also known as the Knuth shuffle (after Donald
   * Knuth), which is an algorithm for generating a random permutation of a finite set — in plain
   * terms, for randomly shuffling the finite set.
   *
   * @param in     the input {@code DataFrame}
   * @param random the random number generator used
   * @return a permuted copy of {@code in}
   */
  public static DataFrame permuteRows(DataFrame in, Random random) {
    DataFrame.Builder builder = in.newCopyBuilder();
    for (int i = builder.rows(); i > 1; i--) {
      builder.swapRecords(i - 1, random.nextInt(i));
    }
    return builder.build();
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
      builder.swapColumns(i - 1, random.nextInt(i));
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

  //TODO(isak) - this is quick and dirty. Implement a real group by data frame
  public static Map<Object, DataFrame> groupBy(DataFrame dataframe, String column) {
    Map<Object, DataFrame.Builder> builders = new HashMap<>();
    Vector keyColumn = dataframe.get(column);

    for (int i = 0; i < dataframe.rows(); i++) {
      Object key = keyColumn.get(Object.class, i);
      DataFrame.Builder builder = builders.get(key);
      if (builder == null) {
        builder = dataframe.newBuilder();
        builders.put(key, builder);
      }
      builder.addRecord(dataframe.getRecord(i));
    }

    Map<Object, DataFrame> frame = new HashMap<>();
    for (Map.Entry<Object, DataFrame.Builder> entry : builders.entrySet()) {
      frame.put(entry.getKey(), entry.getValue().build());
    }
    return frame;
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
    ImmutableTable.Builder<Object, Object, Object> b = ImmutableTable.builder();
    b.put(0, 0, " ");

    Index columnIndex = dataFrame.getColumnIndex();
    for (int j = 0; j < dataFrame.columns(); j++) {
      b.put(0, j + 1, columnIndex.get(j));
    }

    Index recordIndex = dataFrame.getRecordIndex();
    for (int i = 0; i < dataFrame.rows() && i < max; i++) {
      b.put(i + 1, 0, String.format("[%s,] ", recordIndex.get(i)));
      for (int j = 0; j < dataFrame.columns(); j++) {
        b.put(i + 1, j + 1, dataFrame.toString(i, j));
      }
    }

    StringBuilder builder =
        new StringBuilder(dataFrame.getClass().getSimpleName()).append(" (")
            .append(dataFrame.rows()).append("x").append(dataFrame.columns()).append(")\n");
    Utils.prettyPrintTable(builder, b.build(), 1, 2, false, false);
    return builder.toString();
  }

  public static final class Builder {

    private Builder() {

    }
  }
}
