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

package org.briljantframework.data.values;

import org.briljantframework.data.types.DataType;

import java.io.Serializable;

/**
 * The interface Value.
 */
public interface Value extends Comparable<Value>, Serializable {

    /**
     * Gets data type.
     *
     * @return the data type
     */
    DataType getDataType();

    /**
     * Value object.
     *
     * @return the object
     */
    Object value();

    /**
     * As double.
     *
     * @return the double
     */
    double asDouble();

    /**
     * Repr string.
     *
     * @return the string
     */
    default String repr() {
        return value().toString();
    }

    /**
     * Na boolean.
     *
     * @return the boolean
     */
    default boolean na() {
        return false;
    }
}
