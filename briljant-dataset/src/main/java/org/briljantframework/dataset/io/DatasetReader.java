/**
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
package org.briljantframework.dataset.io;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.reader.EntryReader;
import org.briljantframework.data.vector.VectorType;

/**
 * The {@code DataFrameInputStream} is supposed to read a {@code DataFrame} from an input source.
 * <p>
 * There are three steps associated with this
 * <ol>
 * <li>Read the types of the Columns via {@link #readColumnTypes()}</li>
 * <li>Read the names of the Columns via {@link #readColumnIndex()}</li>
 * <li>Read values</li>
 * </ol>
 * <p>
 * The simplest is to use the convince methods {@link #readColumnTypes()} and
 * {@link #readColumnIndex()} constructing a {@link DataFrame.Builder} and use its
 * {@link org.briljantframework.data.dataframe.DataFrame.Builder#readAll(org.briljantframework.data.reader.EntryReader)}
 * method.
 * <p>
 * For example: <code>
 * <pre>
 *      DataFrameInputStream dfis = ...;
 *      Collection<Type> types = dfis.readColumnTypes();
 *      Collection<String> names = dfis.readColumnNames();
 *      DataFrame.Builder builder = new MixedDataFrame(names, types);
 *      DataFrame dataFrame = builder.read(dfis).build();
 * </pre>
 * </code>
 *
 * Entries returned by {@link #next()} are returned in row-major order and typed according to the
 * {@link org.briljantframework.data.vector.VectorType}s returned by {@link #readColumnTypes()}.
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
 * {@link #readColumnIndex()} should return {@code ["a", "b", "c"]} and {@link #readColumnTypes()}
 * should return {@code [DoubleVector.TYPE, StringVector.TYPE, IntVector.TYPE]}.
 *
 * Then, subsequent calls to {@link #next()} should return a
 * {@link org.briljantframework.data.reader.DataEntry} with {@code [3.2, "hello", 1]},
 * {@code [2.0 "sx",
 * 3]} and {@code [2, "dds", 100]} in sequence.
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
@Deprecated
public abstract class DatasetReader extends FilterInputStream implements EntryReader {

  protected static final String NAMES_BEFORE_TYPE = "Can't read name before types";
  protected static final String UNEXPECTED_EOF = "Unexpected EOF.";
  protected static final String VALUES_BEFORE_NAMES_AND_TYPES =
      "Reading values before names and types";
  protected static final String MISMATCH = "Types and values does not match (%d, %d) at line %d";


  /**
   * @param in the underlying input stream
   */
  protected DatasetReader(InputStream in) {
    super(in);
  }

  /**
   * Reads the column types of this data frame input stream. Returns {@code null} when there are no
   * more types to read.
   *
   * @return a type or {@code null}
   */
  protected abstract VectorType readColumnType() throws IOException;

  /**
   * Reads the column names from the input stream. Returns {@code null} when there are no more
   * column names.
   *
   * @return a column name or {@code null}
   */
  protected abstract Object readColumnName() throws IOException;

  /**
   * @return a collection of types
   */
  public List<VectorType> readColumnTypes() throws IOException {
    List<VectorType> types = new ArrayList<>();
    for (VectorType type = readColumnType(); type != null; type = readColumnType()) {
      types.add(type);
    }
    return Collections.unmodifiableList(types);
  }

  /**
   * @return a collection of column names
   */
  public Collection<Object> readColumnIndex() throws IOException {
    List<Object> names = new ArrayList<>();
    for (Object type = readColumnName(); type != null; type = readColumnName()) {
      names.add(type);
    }
    return Collections.unmodifiableCollection(names);
  }

}
