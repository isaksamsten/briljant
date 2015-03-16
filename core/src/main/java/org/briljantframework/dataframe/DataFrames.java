package org.briljantframework.dataframe;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;

import org.briljantframework.Utils;
import org.briljantframework.dataframe.join.InnerJoin;
import org.briljantframework.dataframe.join.JoinKeys;
import org.briljantframework.dataframe.join.JoinOperation;
import org.briljantframework.dataframe.join.Joiner;
import org.briljantframework.dataframe.join.LeftOuterJoin;
import org.briljantframework.dataframe.transform.RemoveIncompleteCases;
import org.briljantframework.dataframe.transform.RemoveIncompleteColumns;
import org.briljantframework.dataframe.transform.Transformation;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.vector.Scale;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;
import org.briljantframework.vector.Vectors;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.BiFunction;

import static org.briljantframework.dataframe.join.JoinUtils.createJoinKeys;

/**
 * Utility methods for handling {@code DataFrame}s <p> Created by Isak Karlsson on 27/11/14.
 */
public final class DataFrames {

  public static final String LEFT_OUTER = "left_outer";
  public static final String INNER = "inner";
  private static final Transformation removeIncompleteColumns = new RemoveIncompleteColumns();
  private static final Transformation removeIncompleteCases = new RemoveIncompleteCases();
  private static final Map<String, JoinOperation> joinOperations = ImmutableMap.of(INNER,
                                                                                   InnerJoin
                                                                                       .getInstance(),
                                                                                   LEFT_OUTER,
                                                                                   LeftOuterJoin
                                                                                       .getInstance());

  private DataFrames() {
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
  public static DataFrame load(
      BiFunction<Collection<String>, Collection<? extends VectorType>, DataFrame.Builder> f,
      DataInputStream in) throws IOException {
    try {
      Collection<VectorType> types = in.readColumnTypes();
      Collection<String> names = in.readColumnNames();
      return f.apply(names, types).read(in).build();
    } finally {
      if (in != null) {
        in.close();
      }
    }
  }

  public static DataFrame summary(DataFrame df) {
    DataFrame.Builder builder =
        new MixedDataFrame.Builder(Arrays.asList("Mean", "Min", "Max", "Mode"), Arrays.asList(
            Vectors.DOUBLE, Vectors.DOUBLE, Vectors.DOUBLE, Vectors.STRING));

    for (int j = 0; j < df.columns(); j++) {
      Vector column = df.getColumn(j);
      builder.getRecordNames().put(j, df.getColumnName(j));
      if (column.getType().getScale() == Scale.NUMERICAL) {
        double mean = Vectors.mean(column);
        double min = Vectors.min(column);
        double max = Vectors.max(column);
        builder.set(j, 0, mean).set(j, 1, min).set(j, 2, max);
      } else {
        Value mode = Vectors.mode(column);
        builder.set(j, 3, mode);
      }
    }
    return builder.build();
  }

  /**
   * Returns a row-permuted copy of {@code in}. This implementations uses the Fisher–Yates shuffle
   * (named after Ronald Fisher and Frank Yates), also known as the Knuth shuffle (after Donald
   * Knuth), which is an algorithm for generating a random permutation of a finite set — in plain
   * terms, for randomly shuffling the finite set. <p> Requires that {@link
   * DataFrame#newCopyBuilder()} returns a copy and that {@link DataFrame.Builder#swapRecords(int,
   * int)} swaps rows at indexes.
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

  public static Map<Value, DataFrame> groupBy(DataFrame dataframe, String column) {
    Map<Value, DataFrame.Builder> builders = new HashMap<>();
    Vector keyColumn = dataframe.getColumn(column);

    for (int i = 0; i < dataframe.rows(); i++) {
      Value key = keyColumn.get(i);
      DataFrame.Builder builder = builders.get(key);
      if (builder == null) {
        builder = dataframe.newBuilder();
        builders.put(key, builder);
      }
      builder.addRecord(dataframe.getRecord(i));
    }

    Map<Value, DataFrame> frame = new HashMap<>();
    for (Map.Entry<Value, DataFrame.Builder> entry : builders.entrySet()) {
      frame.put(entry.getKey(), entry.getValue().build());
    }
    return frame;
  }

  public static DataFrame innerJoin(DataFrame a, DataFrame b, Collection<String> on) {
    if (!(on instanceof Set)) {
      on = new HashSet<>(on);
    }
    JoinKeys joinKeys = createJoinKeys(a, b, on);
    Joiner joiner = joinOperations.get(INNER).createJoiner(joinKeys);
    return join(a, b, joiner, on).build();
  }

  public static DataFrame leftOuterJoin(DataFrame a, DataFrame b, Collection<String> on) {
    if (!(on instanceof Set)) {
      on = new HashSet<>(on);
    }
    JoinKeys joinKeys = createJoinKeys(a, b, on);
    Joiner joiner = joinOperations.get(LEFT_OUTER).createJoiner(joinKeys);
    return join(a, b, joiner, on).build();
  }

  private static DataFrame.Builder join(DataFrame a, DataFrame b, Joiner joiner,
                                        Collection<String> on) {
    DataFrame.Builder builder = a.newBuilder();
    for (int i = 0; i < joiner.size(); i++) {
      int aRow = joiner.getLeftIndex(i);
      int bRow = joiner.getRightIndex(i);
      int column = 0;
      for (int j = 0; j < a.columns(); j++) {
        if (aRow < 0) {
          builder.setNA(i, column);
        } else {
          builder.set(i, column, a, aRow, column);
        }
        column += 1;
      }
      for (int j = 0; j < b.columns(); j++) {
        String columnName = b.getColumnName(j);
        if (!on.contains(columnName)) {
          if (i == 0) {
            builder.addColumnBuilder(b.getColumnType(j));
            builder.getColumnNames().put(column, columnName);
          }
          if (bRow < 0) {
            builder.setNA(i, column);
          } else {
            builder.set(i, column, b, bRow, j);
          }
          column += 1;
        }
      }
    }
    return builder;
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
    // b.put(0, 1, " ");
    for (int i = 0; i < dataFrame.columns(); i++) {
      String columnName = dataFrame.getColumnName(i);
      b.put(0, i + 1, columnName == null ? "Undefined" : columnName);
    }

    for (int i = 0; i < dataFrame.rows() && i < max; i++) {
      // b.put(i + 1, 0, String.format("[%d,] ", i));
      String rowName = dataFrame.getRecordName(i);
      b.put(i + 1, 0, rowName == null ? " " : rowName + " ");
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
