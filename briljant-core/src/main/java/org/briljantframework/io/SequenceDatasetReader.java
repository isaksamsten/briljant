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

package org.briljantframework.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.reader.StringDataEntry;
import org.briljantframework.data.vector.VectorType;

/**
 * @author Isak Karlsson
 */
@Deprecated
public class SequenceDatasetReader extends DatasetReader {

  private final BufferedReader reader;
  private String separator = ",";
  private String missingValue = "?";
  private int columns = -1;
  private String[] values = null;

  /**
   * @param in the underlying input stream
   */
  public SequenceDatasetReader(InputStream in) {
    super(in);
    this.reader = new BufferedReader(new InputStreamReader(in));

  }

  @Override
  public VectorType readColumnType() throws IOException {
    throw new UnsupportedOperationException("Variable data entry sizes");
  }

  @Override
  public String readColumnName() throws IOException {
    throw new UnsupportedOperationException("Variable data entry sizes");
  }

  @Override
  public DataEntry next() throws IOException {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    DataEntry entry = new StringDataEntry(values, missingValue);
    values = null;
    return entry;
  }

  @Override
  public void close() throws IOException {
    super.close();
    reader.close();
  }

  @Override
  public boolean hasNext() throws IOException {
    return initializeValues();
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
