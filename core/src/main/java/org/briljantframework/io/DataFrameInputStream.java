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

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.types.DefaultTypeFactory;
import org.briljantframework.data.types.TypeFactory;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Isak Karlsson on 14/08/14.
 * <p>
 * TODO - StorageInputStream should perhaps take type parameters and store factories
 */
public abstract class DataFrameInputStream extends FilterInputStream {


    /**
     * The Factory.
     */
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
     * Read storage.
     *
     * @param <D>     the type parameter
     * @param copyTo the factory
     * @return the storage
     * @throws IOException the iO exception
     */
    public abstract <D extends DataFrame<?>> D read(DataFrame.CopyTo<D> copyTo) throws IOException;

}
