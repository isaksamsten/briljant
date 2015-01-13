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

package org.briljantframework.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.VectorType;

/**
 * The {@code DataFrameInputStream} is supposed to read a {@code DataFrame} from an input source.
 * <p>
 * There are three steps associated with this
 * <ol>
 * <li>Read the types of the Columns via {@link #readColumnType()}</li>
 * <li>Read the names of the Columns via {@link #readColumnName()}</li>
 * <li>Read values</li>
 * </ol>
 * <p>
 * The simplest is to use the convince methods {@link #readColumnTypes()} and
 * {@link #readColumnNames()} constructing a {@link DataFrame.Builder} and use its
 * {@link org.briljantframework.dataframe.DataFrame.Builder#read(DataInputStream)} method.
 * <p>
 * For example: <code>
 * <pre>
 *      DataFrameInputStream dfis = ...;
 *      Collection<Type> types = dfis.readTypes();
 *      Collection<String> names = dfis.readNames();
 *      DataFrame.Builder builder = new MixedDataFrame(names, types);
 *      DataFrame dataFrame = builder.read(dfis).create();
 * </pre>
 * </code>
 * 
 * Entries returned by {@link #next()} are returned in row-major order and typed according to the
 * {@link org.briljantframework.vector.VectorType}s returned by {@link #readColumnTypes()}.
 * 
 * For example, given the dataset, where the first and second row are names and types respectively:
 * 
 * <pre>
 *     a       b       c
 *   double  string   int
 *    3.2     hello    1
 *    2.0     sx       3
 *    2       dds     100
 * </pre>
 * 
 * {@link #readColumnNames()} should return {@code ["a", "b", "c"]} and {@link #readColumnTypes()}
 * should return {@code [DoubleVector.TYPE, StringVector.TYPE, IntVector.TYPE]}.
 * 
 * Then, subsequent calls to {@link #next()} should return a
 * {@link org.briljantframework.io.DataEntry} with {@code [3.2, "hello", 1]}, {@code [2.0 "sx", 3]}
 * and {@code [2, "dds", 100]} in sequence.
 * 
 * Hence, summing the columns of
 * 
 * <pre>
 *     a       b       c
 *   double  double   int
 *    3.2     3        1
 *    2.0     4        3
 *    2       7       100
 * </pre>
 * 
 * Is as simple as
 * 
 * <pre>
 * try (DataFrameInputStream dfis = new CsvInputStream(&quot;file.txt&quot;)) {
 *   Map&lt;Integer, Double&gt; sum = new HashMap&lt;&gt;();
 *   dfis.readColumnNames();
 *   dfis.readColumnTypes();
 *   while (dfis.hasNext()) {
 *     DataEntry entry = dfis.next();
 *     for (int i = 0; i &lt; entry.size() &amp;&amp; entry.hasNext(); i++) {
 *       sum.put(i, entry.nextDouble());
 *     }
 *   }
 * }
 * </pre>
 * 
 * <p>
 * 
 * @author Isak Karlsson
 */
public abstract class DataInputStream extends FilterInputStream {

  protected static final String NAMES_BEFORE_TYPE = "Can't read name before types";
  protected static final String UNEXPECTED_EOF = "Unexpected EOF.";
  protected static final String VALUES_BEFORE_NAMES_AND_TYPES =
      "Reading values before names and types";
  protected static final String MISMATCH = "Types and values does not match (%d, %d) at line %d";


  /**
   * @param in the underlying input stream
   */
  protected DataInputStream(InputStream in) {
    super(in);
  }

  /**
   * Reads the column types of this data frame input stream. Returns {@code null} when there are no
   * more types to read.
   *
   * @return a type or {@code null}
   * @throws IOException
   */
  public abstract VectorType readColumnType() throws IOException;

  /**
   * Reads the column names from the input stream. Returns {@code null} when there are no more
   * column names.
   *
   * @return a column name or {@code null}
   * @throws IOException
   */
  public abstract String readColumnName() throws IOException;

  /**
   * For convenience. This method reads all column types from the input stream.
   * <p>
   * Same as:
   * 
   * <pre>
   * Type t = null;
   * while ((t = f.readColumnType()) != null) {
   *   coll.add(t);
   * }
   * </pre>
   *
   * @return a collection of types
   * @throws IOException
   */
  public Collection<VectorType> readColumnTypes() throws IOException {
    List<VectorType> types = new ArrayList<>();
    for (VectorType type = readColumnType(); type != null; type = readColumnType()) {
      types.add(type);
    }
    return Collections.unmodifiableCollection(types);
  }

  /**
   * For convenience. This method read all the column names from the input stream.
   * <p>
   * Same as:
   * 
   * <pre>
   * String n = null;
   * while ((n = f.readColumnName()) != null) {
   *   coll.add(t);
   * }
   * </pre>
   *
   * @return a collection of column names
   * @throws IOException
   */
  public Collection<String> readColumnNames() throws IOException {
    List<String> names = new ArrayList<>();
    for (String type = readColumnName(); type != null; type = readColumnName()) {
      names.add(type);
    }
    return Collections.unmodifiableCollection(names);
  }

  /**
   * Reads the next entry from this stream
   * 
   * @return the next entry
   * @throws IOException
   */
  public abstract DataEntry next() throws IOException;

  /**
   * Returns {@code true} if there are more values in the stream
   *
   * @return if has next
   * @throws IOException
   */
  public abstract boolean hasNext() throws IOException;

  // /**
  // * Reads the next string in this stream
  // *
  // * @return the next string
  // * @throws IOException
  // */
  // public abstract String nextString() throws IOException;
  //
  // /**
  // * Reads the next int in this stream
  // *
  // * @return the next int
  // * @throws IOException
  // * @throws java.lang.NumberFormatException
  // */
  // public int nextInt() throws IOException {
  // String repr = nextString();
  // return repr == StringVector.NA ? IntVector.NA : Integer.parseInt(repr);
  // }
  //
  // /**
  // * Reads the next {@code double} in this stream
  // *
  // * @return the next {@code double}
  // * @throws IOException
  // * @throws java.lang.NumberFormatException
  // */
  // public double nextDouble() throws IOException {
  // String repr = nextString();
  // if (repr == StringVector.NA) {
  // return DoubleVector.NA;
  // } else {
  // Double d = Doubles.tryParse(repr);
  // return d == null ? DoubleVector.NA : d;
  // }
  // }
  //
  // /**
  // * Reads the next {@code Binary} in this stream.
  // *
  // * @return the next binary
  // * @throws IOException
  // * @throws java.lang.NumberFormatException
  // */
  // public Binary nextBinary() throws IOException {
  // return Binary.valueOf(nextInt());
  // }
  //
  // /**
  // * Reads the next {@code Complex} in this stream.
  // *
  // * @return the next complex
  // * @throws IOException
  // * @throws NumberFormatException
  // */
  // public Complex nextComplex() throws IOException {
  // throw new UnsupportedOperationException();
  // }
  //
  // public abstract boolean hasNext() throws IOException;
  //
  // public int currentRowSize() throws IOException {
  // throw new UnsupportedOperationException();
  // }
}
