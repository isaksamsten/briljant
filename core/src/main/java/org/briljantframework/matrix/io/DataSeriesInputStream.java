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

package org.briljantframework.matrix.io;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.io.DataFrameInputStream;
import org.briljantframework.vector.Type;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Load a time series as formatted in the <a href="http://www.cs.ucr.edu/~eamonn/time_series_data/">UCR Time Series
 * Classification/Clustering Page</a>.
 * <p>
 * <pre>
 *     [double|target]\s+[double|value]\s+,...\s+[double|value]
 *     ...
 *     ...
 * </pre>
 * <p>
 * <p>
 * Created by Isak Karlsson on 01/09/14.
 */
public class DataSeriesInputStream extends DataFrameInputStream {

    private final BufferedReader reader;

    /**
     * Instantiates a new Data series input stream.
     *
     * @param in the in
     */
    public DataSeriesInputStream(InputStream in) {
        super(in);
        this.reader = new BufferedReader(new InputStreamReader(in));
    }

    @Override
    public Type readColumnType() throws IOException {
        return null;
    }

    @Override
    public String readColumnName() throws IOException {
        return null;
    }

    @Override
    public boolean readValue(DataFrame.Builder builder, int index) throws IOException {
        return false;
    }

    @Override
    public void close() throws IOException {
        reader.close();
        super.close();
    }
    //
    //    /**
    //     * Read container.
    //     *
    //     * @param <D>    the type parameter
    //     * @param copyTo the factory
    //     * @return the container
    //     * @throws java.io.IOException             the iO exception
    //     * @throws java.lang.NumberFormatException if a value is badly formatted
    //     */
    //    @Override
    //    public <D extends DataFrame<?>> D read(DataFrame.CopyTo<D> copyTo) throws IOException {
    //        String line = reader.readLine();
    //        if (line == null) {
    //            throw new IOException("Unexpected EOF");
    //        }
    //
    //        String[] values = line.trim().split("\\s+");
    //        List<Type> types = new ArrayList<>();
    //        for (int i = 0; i < values.length; i++) {
    //            types.add(typeFactory.newNumeric(String.valueOf(i)));
    //        }
    //
    //        DataFrame.Builder<D> datasetBuilder = copyTo.newBuilder(types);
    //        for (; line != null; line = reader.readLine()) {
    //            values = line.trim().split("\\s+");
    //            for (String value : values) {
    //                datasetBuilder.add(Double.parseDouble(value.trim()));
    //            }
    //        }
    //
    //        return datasetBuilder.create();
    //    }
}
