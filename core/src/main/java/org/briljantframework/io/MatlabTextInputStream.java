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

import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.VectorType;

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
public class MatlabTextInputStream extends DataInputStream {

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
  public MatlabTextInputStream(InputStream in) {
    super(in);
    this.reader = new BufferedReader(new InputStreamReader(in));
  }

  @Override
  public VectorType readColumnType() throws IOException {
    initializeValues();
    if (currentType < columns) {
      currentType++;
      return DoubleVector.TYPE;
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
  public DataEntry next() throws IOException {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    DataEntry entry = new StringDataEntry(values, missingValue);
    values = null;
    return entry;
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
