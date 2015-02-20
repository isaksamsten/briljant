/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import com.carrotsearch.hppc.IntDoubleMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.google.common.base.Strings;
import com.google.common.collect.Table;

/**
 * Created by Isak Karlsson on 14/08/14.
 */
public final class Utils {

  private volatile static Random random = new Random();

  private Utils() {}

  public static Random getRandom() {
    return random;
  }

  public static synchronized void setRandomSeed(int seed) {
    random.setSeed(seed);
  }

  /**
   * The Fisher–Yates shuffle (named after Ronald Fisher and Frank Yates), also known as the Knuth
   * shuffle (after Donald Knuth), is an algorithm for generating a random permutation of a finite
   * set — in plain terms, for randomly shuffling the set.
   * <p>
   * Code from method java.util.Collections.shuffle();
   *
   * @param array the array
   */
  public static void permute(int[] array) {
    if (random == null)
      random = new Random();
    int count = array.length;
    for (int i = count; i > 1; i--) {
      swap(array, i - 1, random.nextInt(i));
    }
  }

  public static void permute(int count, Swappable swappable) {
    if (random == null)
      random = new Random();
    for (int i = count; i > 1; i--) {
      swappable.swap(i - 1, random.nextInt(i));
    }
  }

  /**
   * Swap values {@code i} and {@code j} in {@code array}
   *
   * @param array the array
   * @param i the i
   * @param j the j
   */
  public static void swap(int[] array, int i, int j) {
    int temp = array[i];
    array[i] = array[j];
    array[j] = temp;
  }

  /**
   * Swap values {@code i} and {@code j} in {@code array}
   *
   * @param array the array
   * @param i the i
   * @param j the j
   */
  public static void swap(double[] array, int i, int j) {
    double temp = array[i];
    array[i] = array[j];
    array[j] = temp;
  }

  public static <T> void swap(IntObjectMap<T> map, int i, int j) {
    boolean containsI = map.containsKey(i);
    boolean containsJ = map.containsKey(j);
    if (containsI && containsJ) {
      T v = map.get(i);
      map.put(i, map.get(j));
      map.put(j, v);
    } else if (containsI) {
      map.put(j, map.get(i));
      map.remove(i);
    } else if (containsJ) {
      map.put(i, map.get(j));
      map.remove(j);
    }
  }

  public static void swap(IntDoubleMap map, int i, int j) {
    boolean containsI = map.containsKey(i);
    boolean containsJ = map.containsKey(j);
    if (containsI && containsJ) {
      double v = map.get(i);
      map.put(i, map.get(j));
      map.put(j, v);
    } else if (containsI) {
      map.put(j, map.get(i));
      map.remove(i);
    } else if (containsJ) {
      map.put(i, map.get(j));
      map.remove(j);
    }
  }

  /**
   * Pretty print table.
   *
   * @param builder the builder
   * @param table the table
   * @param padding the padding
   * @param space the space
   * @param printRow the print row
   * @param printColumn
   */
  public static <R, C, V> void prettyPrintTable(StringBuilder builder, Table<R, C, V> table,
      int padding, int space, boolean printRow, boolean printColumn) {
    Set<C> columns = table.columnKeySet();
    Set<R> rows = table.rowKeySet();

    Map<C, Integer> valueLength = new HashMap<>();
    int rowLength = rows.stream().mapToInt(x -> x.toString().length()).max().orElse(1);
    for (R row : rows) {
      for (C column : columns) {
        Object obj = table.get(row, column);
        String value = table.get(row, column).toString();
        if (value.length() > valueLength.getOrDefault(column, 0)) {
          valueLength.put(column, value.length());
        }
      }
    }

    if (printRow) {
      builder.append(Strings.repeat(" ", rowLength + space));
    }

    if (printColumn) {
      builder.append(Strings.repeat(" ", padding));
      for (C c : columns) {
        String column = c.toString();
        builder.append(column);
        int spacing =
            valueLength.get(c) < column.length() ? space : valueLength.get(c) - column.length()
                + space;
        builder.append(Strings.repeat(" ", spacing));
      }
      builder.append("\n");
    }

    for (R rowKey : rows) {
      String rowString = rowKey.toString();
      builder.append(Strings.repeat(" ", padding));
      int spacing = rowLength < rowString.length() ? space : rowLength - rowString.length() + space;
      if (printRow) {
        builder.append(rowString);
        builder.append(Strings.repeat(" ", spacing));
      }
      for (C columnKey : columns) {
        String column = columnKey.toString();
        String value = table.get(rowKey, columnKey).toString();

        spacing =
            valueLength.get(columnKey) < column.length() ? space : valueLength.get(columnKey)
                - column.length() + space;
        builder.append(value);
        builder.append(Strings.repeat(" ", column.length() - value.length() + spacing));
      }
      builder.append("\n");
    }
  }

  /**
   * Pretty print table.
   *
   * @param table the table
   * @param padding the padding
   * @param space the space
   * @param printRow the print row
   * @param printColumn
   * @return the string
   */
  public static <R, C, V> String prettyPrintTable(Table<R, C, V> table, int padding, int space,
      boolean printRow, boolean printColumn) {
    StringBuilder builder = new StringBuilder();
    prettyPrintTable(builder, table, padding, space, printRow, printColumn);
    return builder.toString();
  }

  /**
   * Returns a pseudo-random number between min and max, inclusive. The difference between min and
   * max can be at most <code>Integer.MAX_VALUE - 1</code>.
   *
   * @param min Minimum value
   * @param max Maximum value. Must be greater than min.
   * @return Integer between min and max, inclusive.
   * @see java.util.Random#nextInt(int)
   */
  public static int randInt(int min, int max) {
    return getRandom().nextInt((max - min) + 1) + min;
  }

  public static double randDouble(double min, double max) {
    return min + (max - min) * getRandom().nextDouble();
  }

  // /**
  // * Min double.
  // *
  // * @param a the a
  // * @param b the b
  // * @return the double
  // */
  // public static double min(double a, double b) {
  // return Math.min(a, b);
  // }
  //
  // /**
  // * Min double.
  // *
  // * @param a the a
  // * @param b the b
  // * @param c the c
  // * @return the double
  // */
  // public static double min(double a, double b, double c) {
  // return Math.min(a, Math.min(b, c));
  // }
  //
  // /**
  // * Min double.
  // *
  // * @param args the args
  // * @return the double
  // */
  // public static double min(double... args) {
  // Preconditions.checkArgument(args.length > 0);
  // return DoubleStream.of(args).min().getAsDouble();
  // }
}
