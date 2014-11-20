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
 * The type Categoric.
 */
public class Categoric implements Value {

    private Object value = null;

    /**
     * Instantiates a new Categoric.
     *
     * @param value the value
     */
    protected Categoric(Object value) {
        this.value = value;
    }

    /**
     * Value of.
     *
     * @param name the name
     * @return the categoric
     */
    public static Categoric valueOf(Object name) {
        return new Categoric(name);
    }

    /**
     * @param o other value
     * @return x.compareTo(y) return 0 if x.equals(y), and 1 if !x.equals(y)
     */
    @Override
    public int compareTo(Value o) {
        return equals(o) ? 0 : 1;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Categoric) {
            return value.equals(((Categoric) obj).value);
        } else {
            return false;
        }
    }

    @Override
    public String toString() {
        return String.format("%s", value.toString());
    }

    @Override
    public DataType getDataType() {
        return DataType.CATEGORIC;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public double asDouble() {
        return Double.NaN;
    }

    @Override
    public String repr() {
        return value instanceof String ? (String) value : value.toString();
    }
}
