package org.briljantframework.dataframe;

import java.io.IOException;
import java.util.Collection;
import java.util.function.BiFunction;

import org.briljantframework.Utils;
import org.briljantframework.io.DataFrameInputStream;
import org.briljantframework.vector.Type;

import com.google.common.collect.ImmutableTable;

/**
 * Utility methods for handling
 * <p>
 * Created by Isak Karlsson on 27/11/14.
 */
public final class DataFrames {
  private DataFrames() {

  }

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
   * To string.
   *
   * @param dataFrame the dataset
   * @return the string
   */
  public static String toString(DataFrame dataFrame) {
    return toString(dataFrame, 10);
  }

  /**
   * To string.
   *
   * @param dataFrame the dataset
   * @param max the max
   * @return the string
   */
  public static String toString(DataFrame dataFrame, int max) {
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
