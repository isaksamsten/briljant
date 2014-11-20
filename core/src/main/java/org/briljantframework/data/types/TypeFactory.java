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

package org.briljantframework.data.types;

/**
 * Created by Isak Karlsson on 11/06/14.
 */
public interface TypeFactory {

    /**
     * Create categoric.
     *
     * @param name the name
     * @return the types
     */
    Type newCategoric(String name);

    /**
     * Create numeric.
     *
     * @param name the name
     * @return the types
     */
    Type newNumeric(String name);

    /**
     * Create types.
     *
     * @param name     the name
     * @param dataType the type
     * @return the types
     */
    default Type create(String name, DataType dataType) {
        switch (dataType) {
            case NUMERIC:
                return newNumeric(name);
            case FACTOR:
                return newFactor(name);
            case CATEGORIC:
                return newCategoric(name);
            default:
                throw new IllegalArgumentException(dataType + " not supported!");
        }
    }

    Type newFactor(String name);


}
