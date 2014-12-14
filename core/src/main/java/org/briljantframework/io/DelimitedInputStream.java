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
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.Type;

/**
 * Reads values from a typed CSV-file similar to those used in Rule Discovery System (RDS).
 * 
 * The file-format is simple, a comma separated file with the first two rows being the column names
 * and the types respectively.
 * 
 * The types are simple strings and are mapped to {@code briljant} data types as follows:
 * <ul>
 * <li>{@code numeric} and {@code regressor} {@link org.briljantframework.vector.DoubleVector}</li>
 * <li>{@code categoric} and {@code class} {@link org.briljantframework.vector.StringVector}</li>
 * </ul>
 * 
 * By convention, missing values are represented as {@code ?}.
 * 
 * Created by Isak Karlsson on 14/08/14.
 */
public class DelimitedInputStream extends DataInputStream {

  public static final String INVALID_NAME = "Can't understand the type %s";
  protected static final Map<String, Type> TYPE_MAP;
  static {
    Map<String, Type> map = new HashMap<>();
    map.put("numeric", DoubleVector.TYPE);
    map.put("regressor", DoubleVector.TYPE);
    map.put("class", StringVector.TYPE);
    map.put("categoric", StringVector.TYPE);

    TYPE_MAP = Collections.unmodifiableMap(map);
  }
  private static final String DEFAULT_SEPARATOR = ",";
  private static final String DEFAULT_MISSING_VALUE = "?";
  private final BufferedReader reader;
  private final String missingValue;
  private final String separator;

  private int currentType = -1;
  private int currentName = -1;
  private String[] types = null, names = null, values = null;

  /**
   * Instantiates a new CSV input stream using {@code inputStream}.
   *
   * @param inputStream the input stream
   */
  public DelimitedInputStream(InputStream inputStream, String missingValue, String separator) {
    super(inputStream);
    this.missingValue = missingValue;
    this.separator = separator;
    reader = new BufferedReader(new InputStreamReader(in));
  }

  public DelimitedInputStream(InputStream inputStream) {
    this(inputStream, DEFAULT_MISSING_VALUE, DEFAULT_SEPARATOR);
  }


  /**
   * Constructs a new buffered csv input stream from {@code file}
   * 
   * @param file the file
   * @throws FileNotFoundException
   */
  public DelimitedInputStream(File file) throws FileNotFoundException {
    this(new BufferedInputStream(new FileInputStream(file)), DEFAULT_MISSING_VALUE,
        DEFAULT_SEPARATOR);
  }

  /**
   * @param fileName the file name
   * @throws FileNotFoundException
   */
  public DelimitedInputStream(String fileName) throws FileNotFoundException {
    this(new File(fileName));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Type readColumnType() throws IOException {
    initializeTypes();
    if (currentType < types.length) {
      String repr = types[currentType++].trim().toLowerCase();
      Type type = TYPE_MAP.get(repr);
      if (type == null) {
        throw new IllegalArgumentException(String.format(INVALID_NAME, repr));
      }
      return type;
    } else {
      return null;
    }
  }

  /**
   * {@inheritDoc}
   */
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

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean hasNext() throws IOException {
    return initializeValues();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void close() throws IOException {
    super.close();
    reader.close();
  }

  private void initializeTypes() throws IOException {
    if (types == null) {
      String typeLine = reader.readLine();
      if (typeLine == null) {
        throw new IOException(UNEXPECTED_EOF);
      }
      types = typeLine.split(separator);
      currentType = 0;
    }
  }

  private void initializeNames() throws IOException {
    if (names == null) {
      String namesLine = reader.readLine();
      if (namesLine == null) {
        throw new IOException(UNEXPECTED_EOF);
      }
      names = namesLine.split(separator);
      if (names.length != types.length) {
        throw new IOException(String.format(MISMATCH, types.length, names.length, 0));
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
      values = valueLine.split(separator);
      if (values.length != types.length) {
        throw new IOException(String.format(MISMATCH, types.length, values.length, 0));
      }
      // currentValue = 0;
    }
    return true;
  }

}
