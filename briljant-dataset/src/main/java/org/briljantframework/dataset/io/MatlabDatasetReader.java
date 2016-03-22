/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.dataset.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.NoSuchElementException;

import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.reader.EntryReaderException;
import org.briljantframework.data.reader.StringDataEntry;
import org.briljantframework.data.vector.Type;

/**
 * Load a time series as formatted in the <a
 * href="http://www.cs.ucr.edu/~eamonn/time_series_data/">UCR Time Series Classification/Clustering
 * Page</a>.
 * <p>
 *
 * <pre>
 *     [double|target]\s+[double|value]\s+,...\s+[double|value]
 *     ...
 *     ...
 * </pre>
 * <p>
 * <p>
 * Created by Isak Karlsson on 01/09/14.
 */
@Deprecated
public class MatlabDatasetReader extends DatasetReader {

  private final BufferedReader reader;
  private String separator = "\\s+";
  private String missingValue = "?";
  private int columns = -1;
  private int currentType = 0;
  private int currentName = 0;
  private String[] values = null;

  /**
   * Instantiates a new Data series input stream.
   *
   * @param in the in
   */
  public MatlabDatasetReader(InputStream in) {
    super(in);
    this.reader = new BufferedReader(new InputStreamReader(in));
  }

  @Override
  public Type readColumnType() throws IOException {
    initializeValues();
    if (currentType < columns) {
      currentType++;
      return Type.DOUBLE;
    } else {
      return null;
    }
  }

  @Override
  public Object readColumnName() throws IOException {
    initializeValues();
    return currentName < columns ? currentName++ : null;
  }

  @Override
  public List<Class<?>> getTypes() {
    throw new UnsupportedOperationException();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public DataEntry next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    DataEntry entry = new StringDataEntry(values, missingValue);
    values = null;
    return entry;
  }

  @Override
  public boolean hasNext() {
    try {
      return initializeValues();
    } catch (IOException e) {
      throw new EntryReaderException(e);
    }
  }

  @Override
  public void close() throws IOException {
    reader.close();
    super.close();
  }

  /**
   * @return true if successfully initialized and there are more values to consume
   * @throws IOException on failure
   */
  private boolean initializeValues() throws IOException {
    if (values == null) {
      String valueLine = reader.readLine();
      if (valueLine == null) {
        return false;
      }
      values = valueLine.trim().split(separator);
      if (columns == -1) {
        columns = values.length;
      }
    }
    return true;
  }
}
