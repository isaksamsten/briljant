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

import org.briljantframework.data.Traversable;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Isak Karlsson on 14/08/14.
 */
public abstract class DatasetOutputStream extends FilterOutputStream {

    /**
     * Instantiates a new Storage output stream.
     *
     * @param out the out
     */
    public DatasetOutputStream(OutputStream out) {
        super(out);
    }

    /**
     * Write void.
     *
     * @param instances the instances
     * @throws IOException the iO exception
     */
    public abstract void write(Traversable<?> instances) throws IOException;

}
