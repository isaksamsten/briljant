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

import org.briljantframework.data.values.Value;

import java.io.Serializable;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A <code>Row</code> is generally a view
 * <p>
 * Created by Isak Karlsson on 11/06/14.
 */
public interface Row extends Iterable<Value>, Serializable {

    /**
     * Get value at position col
     *
     * @param col types
     * @return a value
     */
    Value getValue(int col);

    /**
     * Width of the entry
     * <pre>
     *     for(int i = 0; i < e.size(); i++)
     *         System.out.println(e.value(i))
     * </pre>
     * <p>
     * <pre>
     *     for(Value v : e) System.out.println(v)
     * </pre>
     *
     * @return the width
     */
    int size();

    MutableRow asMutable();


    /**
     * Value stream.
     *
     * @return the stream
     */
    default Stream<Value> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
