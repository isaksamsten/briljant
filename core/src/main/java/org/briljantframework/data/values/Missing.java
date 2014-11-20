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

/**
 * The type Missing.
 */
public class Missing implements Value {

    public static final Missing INSTANCE = new Missing();

    /**
     * Value of.
     *
     * @return the value
     */
    public static Missing valueOf() {
        return INSTANCE;
    }

    private Missing() {

    }

    @Override
    public boolean na() {
        return true;
    }

    @Override
    public int compareTo(Value other) {
        return other == this ? 0 : 1;
    }

    @Override
    public DataType getDataType() {
        return DataType.MISSING;
    }

    @Override
    public Object value() {
        return Double.NaN;
    }

    @Override
    public double asDouble() {
        return Double.NaN;
    }

    @Override
    public String toString() {
        return "?";
    }

}
