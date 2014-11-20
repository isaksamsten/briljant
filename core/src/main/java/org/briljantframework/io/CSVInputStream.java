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

import com.google.common.base.Preconditions;
import org.briljantframework.data.DataFrame;
import org.briljantframework.data.types.DataType;
import org.briljantframework.data.types.Type;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Isak Karlsson on 14/08/14.
 */
public class CSVInputStream extends DataFrameInputStream {
    private final String missingValue;
    private final BufferedReader reader;

    /**
     * Instantiates a new CSV input stream.
     *
     * @param inputStream the input stream
     */
    public CSVInputStream(InputStream inputStream) {
        this(inputStream, "?");
    }

    /**
     * Instantiates a new CSV input stream.
     *
     * @param inputStream  the input stream
     * @param missingValue the missing value
     */
    public CSVInputStream(InputStream inputStream, String missingValue) {
        super(inputStream);
        reader = new BufferedReader(new InputStreamReader(in));
        this.missingValue = Preconditions.checkNotNull(missingValue);
    }

    /**
     * Load storage.
     *
     * @param <D>  the type parameter
     * @param file the file
     * @param df   the df
     * @return the storage
     * @throws IOException the iO exception
     */
    public static <D extends DataFrame<?>> D load(String file, DataFrame.CopyTo<D> df) throws IOException {
        return new CSVInputStream(new FileInputStream(file)).read(df);
    }

    @Override
    public <D extends DataFrame<?>> D read(DataFrame.CopyTo<D> copyTo) throws IOException {
        String line = reader.readLine();
        if (line == null) {
            throw new IOException("invalid csv types (reached EOF while parsing)");
        }
        String[] types = line.split(",");

        line = reader.readLine();
        if (line == null) {
            throw new IOException("invalid csv types (reached EOF while parsing)");

        }
        String[] names = line.split(",");
        if (types.length != names.length) {
            throw new IOException(String.format("the number of types and names does not match (%d != %d)",
                    types.length, names.length));
        }

        List<Type> headers = parseHeader(types, names);
        DataFrame.Builder<D> datasetBuilder = copyTo.newBuilder(headers);

        parseValues(reader, headers, datasetBuilder);

        return datasetBuilder.create();
    }

    private List<Type> parseHeader(String[] types, String[] names) throws IOException {
        List<Type> headers = new ArrayList<>(types.length);
        for (int i = 0; i < types.length; i++) {
            headers.add(typeFactory.create(names[i].trim(), getType(types[i].trim())));
        }
        return headers;
    }

    private void parseValues(BufferedReader reader, List<Type> types, DataFrame.Builder<?> datasetBuilder) throws
            IOException {
        int rows = 0, cols = types.size();

        for (String line = reader.readLine(); line != null; line = reader.readLine()) {
            String[] value = line.split(",");
            if (cols != value.length) {
                throw new IOException(String.format("invalid row (at line %d). To few values?", rows));
            }

            for (int i = 0; i < value.length; i++) {
                Type col = types.get(i);
                String val = value[i].trim();
                if (val.equalsIgnoreCase(this.missingValue)) {
                    datasetBuilder.add((String) null);
                } else {
                    switch (col.getDataType()) {
                        case CATEGORIC:
                            datasetBuilder.add(val);
                            break;
                        case NUMERIC:
                            datasetBuilder.add(Double.parseDouble(val));
                            break;
                        case FACTOR:
                            datasetBuilder.add(Integer.parseInt(val));
                            break;
                        default:
                            throw new IOException("not implemented yet " + col.getDataType());
                    }
                }
            }
        }
    }

    private DataType getType(String type) throws IOException {
        switch (type.toLowerCase()) {
            case "regressor":
            case "numeric":
                return DataType.NUMERIC;
            case "integer":
                return DataType.FACTOR;
            case "class":
            case "categoric":
                return DataType.CATEGORIC;
            default:
                throw new IOException(String.format("unexpected types type %s", type));
        }
    }

    @Override
    public void close() throws IOException {
        super.close();
        reader.close();
    }

}
