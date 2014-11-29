package org.briljantframework.dataframe;

import java.io.IOException;
import java.util.Collection;
import java.util.function.BiFunction;

import org.briljantframework.Utils;
import org.briljantframework.io.DataFrameInputStream;
import org.briljantframework.vector.Type;

import com.google.common.collect.ImmutableTable;

/**
 * Utility methods for handling {@code DataFrame}s
 * <p>
 * Created by Isak Karlsson on 27/11/14.
 */
public final class DataFrames {
  private DataFrames() {}

  /**
   * Load data frame using {@code in} and construct a new
   * {@link org.briljantframework.dataframe.DataFrame} using the function {@code f} which should
   * return a {@link org.briljantframework.dataframe.DataFrame.Builder} using the column names and
   * the column types. The values from {@code in} are read to the {@code DataFrame.Builder} and
   * returned as the DataFrame created by
   * {@link org.briljantframework.dataframe.DataFrame.Builder#create()}.
   * 
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
      DataFrameInputStream in) throws IOException {
    try {
      Collection<Type> types = in.readColumnTypes();
      Collection<String> names = in.readColumnNames();
      return f.apply(names, types).read(in).create();
    } finally {
      if (in != null) {
        in.close();
      }
    }
  }

  /**
   * Generates a string representation of a maximum of {@code 10} rows.
   * 
   * @param dataFrame the data frame
   * @return a tabular string representation
   */
  public static String toTabularString(DataFrame dataFrame) {
    return toTabularString(dataFrame, 10);
  }

  /**
   * Generates a string representation from {@code dataFrame}.
   *
   * For example:
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
    for (int i = 0; i < dataFrame.columns(); i++) {
      b.put(0, i + 1, dataFrame.getColumnName(i));
    }

    for (int i = 0; i < dataFrame.rows() && i < max; i++) {
      b.put(i + 1, 0, String.format("[%d,]   ", i));
      for (int j = 0; j < dataFrame.columns(); j++) {
        b.put(i + 1, j + 1, dataFrame.getColumn(j).toString(i));
      }
    }

    StringBuilder builder =
        new StringBuilder(dataFrame.getClass().getSimpleName()).append(" (")
            .append(dataFrame.rows()).append("x").append(dataFrame.columns()).append(")\n");
    Utils.prettyPrintTable(builder, b.build(), 1, 2, false, false);
    return builder.toString();
  }
}
