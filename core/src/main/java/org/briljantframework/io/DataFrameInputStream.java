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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * The {@code DataFrameInputStream} is supposed to read a {@code DataFrame} from
 * an input source.
 * <p>
 * There are three steps associated with this
 * <ol>
 * <li>Read the types of the Columns via {@link #readColumnType()}</li>
 * <li>Read the names of the Columns via {@link #readColumnName()}</li>
 * <li>Read the values and fill the {@code DataFrame} via {@link #readValue(org.briljantframework.dataframe.DataFrame.Builder, int)}</li>
 * </ol>
 * <p>
 * The simplest is to use the convince methods {@link #readColumnTypes()},
 * {@link #readColumnNames()} and {@link #read(org.briljantframework.dataframe.DataFrame.Builder)}.
 * <p>
 * For example:
 * <code>
 * <pre>
 *      DataFrameInputStream dfis = ...;
 *      Collection<Type> types = dfis.readTypes();
 *      Collection<String> names = dfis.readNames();
 *      DataFrame.Builder builder = new MixedDataFrame(names, types);
 *      DataFrame dataFrame = dfis.read(builder).create(); *
 * </pre>
 * </code>
 * <p>
 * <p>
 * <p>
 * Created by Isak Karlsson on 14/08/14.
 */
public abstract class DataFrameInputStream extends FilterInputStream {

    protected static final String NAMES_BEFORE_TYPE = "Can't read name before types";
    protected static final String UNEXPECTED_EOF = "Unexpected EOF.";
    protected static final String VALUES_BEFORE_NAMES_AND_TYPES = "Reading values before names and types";


    protected final TypeFactory typeFactory;

    /**
     * Instantiates a new Storage input stream.
     *
     * @param in          the in
     * @param typeFactory the factory
     */
    protected DataFrameInputStream(InputStream in, TypeFactory typeFactory) {
        super(in);
        this.typeFactory = typeFactory;
    }

    /**
     * Instantiates a new Storage input stream.
     *
     * @param in the in
     */
    protected DataFrameInputStream(InputStream in) {
        this(in, new DefaultTypeFactory());
    }

    /**
     * Reads the column types of this data frame input stream.
     *
     * @return
     */
    public abstract Type readColumnType() throws IOException;

    public Collection<Type> readColumnTypes() throws IOException {
        List<Type> types = new ArrayList<>();
        for (Type type = readColumnType(); type != null; type = readColumnType()) {
            types.add(type);
        }
        return Collections.unmodifiableCollection(types);
    }

    public abstract String readColumnName() throws IOException;

    public Collection<String> readColumnNames() throws IOException {
        List<String> names = new ArrayList<>();
        for (String type = readColumnName(); type != null; type = readColumnName()) {
            names.add(type);
        }
        return Collections.unmodifiableCollection(names);
    }

    public abstract boolean readValue(DataFrame.Builder builder, int index) throws IOException;

    public DataFrame.Builder read(DataFrame.Builder builder) throws IOException {
        int index = 0;
        while (readValue(builder, index++)) {
            if (index == builder.columns()) {
                index = 0;
            }
        }
        return builder;
    }

}
