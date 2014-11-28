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

import java.io.*;
import java.util.NoSuchElementException;

import org.briljantframework.vector.*;

/**
 * Reads values from a typed CSV-file similar to those used in Rule Discovery System (
 *
 *
 * Created by Isak Karlsson on 14/08/14.
 */
public class CsvInputStream extends DataFrameInputStream {

  protected static final String MISMATCH = "Types and values does not match (%d, %d)";

  private final BufferedReader reader;

  private int currentType = -1, currentName = -1, currentValue = -1;
  private String[] types = null, names = null, values = null;

  /**
   * Instantiates a new CSV input stream.
   *
   * @param inputStream the input stream
   */
  public CsvInputStream(InputStream inputStream) {
    super(inputStream);
    reader = new BufferedReader(new InputStreamReader(in));
  }

  public CsvInputStream(File file) throws FileNotFoundException {
    this(new FileInputStream(file));
  }

  @Override
  public Type readColumnType() throws IOException {
    initializeTypes();
    if (currentType < types.length) {
      String type = types[currentType++];
      return typeFactory.getTypeForName(type);
    } else {
      return null;
    }
  }

  @Override
  public String readColumnName() throws IOException {
    if (types == null) {
      throw new IOException(NAMES_BEFORE_TYPE);
    }
    initializeNames();
    if (currentName < names.length) {
      return names[currentName++].trim();
    } else {
      return null;
    }
  }

  @Override
  public String nextString() throws IOException {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    if (currentValue < values.length) {
      String value = values[currentValue];
      currentValue += 1;
      if (currentValue == types.length) {
        values = null;
      }
      return value.trim();
    }
    return null;
  }

  @Override
  public int nextInt() throws IOException {
    String repr = nextString();
    if (repr.equals("?") || repr.equals("NA")) {
      return IntVector.NA;
    }
    return Integer.parseInt(repr);
  }

  @Override
  public double nextDouble() throws IOException {
    String repr = nextString();
    if (repr.equals("?") || repr.equals("NA")) {
      return DoubleVector.NA;
    }
    return Double.parseDouble(repr);
  }

  @Override
  public Binary nextBinary() throws IOException {
    return Binary.valueOf(nextInt());
  }

  @Override
  public Complex nextComplex() {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasNext() throws IOException {
    return initializeValues();
  }

  private void initializeTypes() throws IOException {
    if (types == null) {
      String typeLine = reader.readLine();
      if (typeLine == null) {
        throw new IOException(UNEXPECTED_EOF);
      }
      types = typeLine.split(",");
      currentType = 0;
    }
  }

  private void initializeNames() throws IOException {
    if (names == null) {
      String namesLine = reader.readLine();
      if (namesLine == null) {
        throw new IOException(UNEXPECTED_EOF);
      }
      names = namesLine.split(",");
      if (names.length != types.length) {
        throw new IOException(String.format(MISMATCH, types.length, names.length));
      }
      currentName = 0;
    }
  }

  /**
   * @return true if successfully initialized and there are more values to consume
   * @throws IOException
   */
  private boolean initializeValues() throws IOException {
    if (names == null || types == null) {
      throw new IOException(VALUES_BEFORE_NAMES_AND_TYPES);
    }
    if (values == null) {
      String valueLine = reader.readLine();
      if (valueLine == null) {
        return false;
      }
      values = valueLine.split(",");
      if (values.length != types.length) {
        throw new IOException(String.format(MISMATCH, types.length, values.length));
      }
      currentValue = 0;
    }
    return true;
  }

  @Override
  public void close() throws IOException {
    super.close();
    reader.close();
  }

}
