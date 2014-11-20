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
 * The type Numeric.
 */
public class Numeric implements Value {

    /**
     * The Value.
     */
    private final double value;

    /**
     * Instantiates a new Numeric.
     *
     * @param value the value
     */
    protected Numeric(double value) {
        this.value = value;
    }

    /**
     * Value of.
     *
     * @param value the value
     * @return the numeric
     */
    public static Numeric valueOf(double value) {
        if (Double.isNaN(value)) {
            throw new IllegalArgumentException("Cannot create a Numeric with Double.NaN (Create an org.adbe.Missing " +
                    "instead)");
        }
        return new Numeric(value);
    }

    /**
     * @param o other value
     * @return -1 if x < y, 0 if x.equals(y) and 1 if x > y
     */
    @Override
    public int compareTo(Value o) {
        if (o instanceof Numeric) {
            return Double.compare(value, ((Numeric) o).value);
        } else {
            return 1;
        }
    }

    @Override
    public int hashCode() {
        return Double.hashCode(value);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Numeric)) {
            return false;
        }
        Numeric n = (Numeric) obj;
        return value == n.value;
    }

    @Override
    public String toString() {
        return String.format("%.3f", value);
    }

    @Override
    public DataType getDataType() {
        return DataType.NUMERIC;
    }

    @Override
    public Object value() {
        return value;
    }

    public double asDouble() {
        return value;
    }

}
