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

package org.briljantframework.data;

import org.briljantframework.data.types.Type;
import org.briljantframework.data.types.Types;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Created by Isak Karlsson on 14/08/14.
 */
public interface Traversable<T extends Row> extends Iterable<T> {

    /**
     * Get a specified types
     *
     * @param col the types index
     * @return return the types
     */
    Type getType(int col);

    /**
     * Gets headers.
     *
     * @return a view of the headers of this data source
     */
    Types getTypes();

    /**
     * Columns int.
     *
     * @return number of headers
     */
    int columns();

    /**
     * Paralell stream.
     *
     * @return the stream
     */
    default Stream<T> parallelStream() {
        return StreamSupport.stream(spliterator(), true);
    }

    /**
     * Entry stream.
     *
     * @return the stream
     */
    default Stream<T> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
