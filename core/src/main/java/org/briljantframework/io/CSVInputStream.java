/*
 * ADEB - machine learning pipelines made easy
 * Copyright (C) 2014  Isak Karlsson
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.briljantframework.io;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Type;

import java.io.*;

/**
 * Created by Isak Karlsson on 14/08/14.
 */
public class CSVInputStream extends DataFrameInputStream {

    protected static final String MISSMATCH = "Types and values does not match (%d, %d)";

    private final BufferedReader reader;

    private int currentType = -1, currentName = -1, currentValue = -1;
    private String[] types = null, names = null, values = null;

    /**
     * Instantiates a new CSV input stream.
     *
     * @param inputStream the input stream
     */
    public CSVInputStream(InputStream inputStream) {
        super(inputStream);
        reader = new BufferedReader(new InputStreamReader(in));
    }

    public CSVInputStream(File file) throws FileNotFoundException {
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
    public boolean readValue(DataFrame.Builder builder, int index) throws IOException {
        if (names == null || types == null) {
            throw new IOException(VALUES_BEFORE_NAMES_AND_TYPES);
        }
        if (!initializeValues()) {
            return false;
        }
        if (values != null && currentValue < values.length) {
            String value = values[currentValue];
            builder.parseAndAdd(index, value);
            currentValue++;
            if (currentValue == types.length) {
                values = null;
            }
            return true;
        } else {
            return false;
        }
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
                throw new IOException(String.format(MISSMATCH, types.length, names.length));
            }
            currentName = 0;
        }
    }

    private boolean initializeValues() throws IOException {
        if (values == null) {
            String valueLine = reader.readLine();
            if (valueLine == null) {
                return false;
            }
            values = valueLine.split(",");
            if (values.length != types.length) {
                throw new IOException(String.format(MISSMATCH, types.length, values.length));
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
