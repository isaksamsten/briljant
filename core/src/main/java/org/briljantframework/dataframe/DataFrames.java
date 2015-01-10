package org.briljantframework.dataframe;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;

import org.briljantframework.Utils;
import org.briljantframework.dataframe.join.*;
import org.briljantframework.dataframe.transform.RemoveIncompleteCases;
import org.briljantframework.dataframe.transform.RemoveIncompleteColumns;
import org.briljantframework.dataframe.transform.Transformation;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.vector.Type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;

/**
 * Utility methods for handling {@code DataFrame}s
 * <p>
 * Created by Isak Karlsson on 27/11/14.
 */
public final class DataFrames {

  private static final Transformation removeIncompleteColumns = new RemoveIncompleteColumns();
  private static final Transformation removeIncompleteCases = new RemoveIncompleteCases();

  private static final Map<String, JoinOperation> joinOperations = ImmutableMap.of("inner",
      new InnerJoin());

  private DataFrames() {}

  /**
   * Load data frame using {@code in} and construct a new
   * {@link org.briljantframework.dataframe.DataFrame} using the function {@code f} which should
   * return a {@link org.briljantframework.dataframe.DataFrame.Builder} using the column names and
   * the column types. The values from {@code in} are read to the {@code DataFrame.Builder} and
   * returned as the DataFrame created by
   * {@link org.briljantframework.dataframe.DataFrame.Builder#build()}.
   * <p>
   * <code><pre>
   *    DataFrame dataframe =
   *        DataFrames.load(MixedDataFrame.Builder::new, new CsvInputStream("iris.txt"));
   * </pre></code>
   *
   * @param f the producing {@code BiFunction}
   * @param in the input stream
   * @return a new dataframe
   * @throws IOException
   */
  public static DataFrame load(
      BiFunction<Collection<String>, Collection<? extends Type>, DataFrame.Builder> f,
      DataInputStream in) throws IOException {
    try {
      Collection<Type> types = in.readColumnTypes();
      Collection<String> names = in.readColumnNames();
      return f.apply(names, types).read(in).build();
    } finally {
      if (in != null) {
        in.close();
      }
    }
  }

  /**
   * Returns a row-permuted copy of {@code in}. This implementations uses the Fisher–Yates shuffle
   * (named after Ronald Fisher and Frank Yates), also known as the Knuth shuffle (after Donald
   * Knuth), which is an algorithm for generating a random permutation of a finite set — in plain
   * terms, for randomly shuffling the finite set.
   * <p>
   * Requires that {@link DataFrame#newCopyBuilder()} returns a copy and that
   * {@link DataFrame.Builder#swapRows(int, int)} swaps rows at indexes.
   *
   * @param in the input {@code DataFrame}
   * @return a permuted copy of {@code in}
   */
  public static DataFrame permuteRows(DataFrame in) {
    DataFrame.Builder builder = in.newCopyBuilder();
    Random random = Utils.getRandom();
    for (int i = builder.rows(); i > 1; i--) {
      builder.swapRows(i - 1, random.nextInt(i));
    }
    return builder.build();
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

  public static DataFrame innerJoin(DataFrame a, DataFrame b, Collection<Integer> on) {
    if (!(on instanceof Set)) {
      on = new HashSet<>(on);
    }
    JoinKeys joinKeys = JoinUtils.getJoinKeys(a, b, on);
    Joiner joiner = joinOperations.get("inner").createJoiner(joinKeys);

    DataFrame.Builder builder = a.newBuilder();
    for (int i = 0; i < joiner.size(); i++) {
      int aRow = joiner.getLeftIndex(i);
      int bRow = joiner.getRightIndex(i);
      int column = 0;
      for (int j = 0; j < a.columns(); j++) {
        builder.set(i, column, a, aRow, column);
        column += 1;
      }
      for (int j = 0; j < b.columns(); j++) {
        if (!on.contains(j)) {
          if (i == 0) {
            builder.addColumn(b.getColumnType(j).newBuilder());
            builder.setColumnName(column, b.getColumnName(j));
          }
          builder.set(i, column, b, bRow, j);
          column += 1;
        }
      }
    }

    return builder.build();
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
   * Generates a string representation from {@code dataFrame}.
   * <p>
   * For example:
   * <p>
   * 
   * <pre>
   *        a    b    c
   *  [0,]  2    3    3
   *  [1,]  1    NA   3
   * </pre>
   *
   * @param dataFrame the dataframe
   * @param max the maximum number of rows to show
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
      String rowName = dataFrame.getRowName(i);
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
}
