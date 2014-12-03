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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

import org.briljantframework.vector.RealVector;
import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.Type;

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
public class MatlabTextInputStream extends DataFrameInputStream {

  private static String separator = "\\s+";
  private static String missingValue = "?";

  private final BufferedReader reader;
  private int columns = -1, currentValue = -1, currentType = 0, currentName = 0;
  private String[] values = null;

  /**
   * Instantiates a new Data series input stream.
   *
   * @param in the in
   */
  public MatlabTextInputStream(InputStream in) {
    super(in);
    this.reader = new BufferedReader(new InputStreamReader(in));
  }

  @Override
  public Type readColumnType() throws IOException {
    initializeValues();
    if (currentType < columns) {
      currentType++;
      return RealVector.TYPE;
    } else {
      return null;
    }
  }

  @Override
  public String readColumnName() throws IOException {
    initializeValues();
    return currentName < columns ? String.valueOf(currentName++) : null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String nextString() throws IOException {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    if (currentValue < values.length) {
      String value = values[currentValue].trim();
      currentValue += 1;
      if (currentValue == columns) {
        values = null;
      }
      if (value.equals(missingValue)) {
        return StringVector.NA;
      }
      return value;
    }
    throw new NoSuchElementException();
  }

  @Override
  public boolean hasNext() throws IOException {
    return initializeValues();
  }

  @Override
  public void close() throws IOException {
    reader.close();
    super.close();
  }

  /**
   * @return true if successfully initialized and there are more values to consume
   * @throws IOException
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
      if (values.length != columns) {
        throw new IOException(String.format(MISMATCH, columns, values.length));
      }
      currentValue = 0;
    }
    return true;
  }
}
