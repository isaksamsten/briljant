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


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.IdentityHashMap;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.*;

/**
 * Produces, insanely naively CSV-produ
 * 
 * Created by Isak Karlsson on 14/08/14
 */
public class CsvOutputStream extends DataOutputStream {

  private static final String NA_REPR = "?";
  private static final String DEFAULT_SEPARATOR = ",";
  private static IdentityHashMap<Type, String> TYPE_TO_NAME = new IdentityHashMap<>();
  static {
    TYPE_TO_NAME.put(DoubleVector.TYPE, "numeric");
    TYPE_TO_NAME.put(IntVector.TYPE, "numeric");
    TYPE_TO_NAME.put(ComplexVector.TYPE, "numeric");
    TYPE_TO_NAME.put(BinaryVector.TYPE, "categoric");
    TYPE_TO_NAME.put(StringVector.TYPE, "categoric");
  }
  private final String separator;

  /**
   * @param out the out
   */
  public CsvOutputStream(OutputStream out, String separator) {
    super(out);
    this.separator = separator;
  }

  public CsvOutputStream(OutputStream out) {
    this(out, DEFAULT_SEPARATOR);
  }

  @Override
  public void write(DataFrame dataFrame) throws IOException {
    BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
    String[] colNames = new String[dataFrame.columns()];
    String[] colTypes = new String[dataFrame.columns()];
    for (int i = 0; i < dataFrame.columns(); i++) {
      colNames[i] = dataFrame.getColumnName(i);
      colTypes[i] = generateTypeRepresentation(dataFrame.getColumnType(i));
    }

    writer.write(String.join(separator, colTypes));
    writer.newLine();
    writer.write(String.join(separator, colNames));
    writer.newLine();

    String[] row = new String[dataFrame.columns()];
    for (int i = 0; i < dataFrame.rows(); i++) {
      for (int j = 0; j < dataFrame.columns(); j++) {
        row[j] = dataFrame.isNA(i, j) ? NA_REPR : dataFrame.toString(i, j);
      }
      writer.write(String.join(separator, row));
      writer.newLine();
    }
    writer.flush();
  }

  private String generateTypeRepresentation(Type columnType) {
    String name = TYPE_TO_NAME.get(columnType);
    if (name != null) {
      return name;
    } else {
      throw new IllegalArgumentException(columnType.toString());
    }
  }
}
