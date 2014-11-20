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
 * TODO(isak): this should perhaps carry more information than an Enum can hold?
 * <p>
 * Created by Isak Karlsson on 11/06/14.
 */
public enum DataType {

    /**
     * The NUMERIC. generic floating point number (32 .. 64 bit)
     */
    NUMERIC(1),

    /**
     * The FACTOR.
     */
    FACTOR(2),

    /**
     * The CATEGORIC.
     */
    CATEGORIC(102),

    /**
     * The MISSING.
     */
    MISSING(5);

    /**
     * From tag.
     *
     * @param b the b
     * @return the type
     */
    public static DataType fromTag(byte b) {
        switch (b) {
            case 1:
                return NUMERIC;
            case 2:
                return FACTOR;
            case 3:
                return CATEGORIC;
            case 5:
                return MISSING;
        }
        return null;
    }

    private byte value;
    private boolean target;
    private String toString;

    /**
     * Instantiates a new Type.
     *
     * @param value  the value
     * @param target the target
     */
    DataType(int value, boolean target) {
        this.toString = super.toString().toLowerCase();
        this.value = (byte) value;
        this.target = target;
    }

    /**
     * Instantiates a new Type.
     *
     * @param value the value
     */
    DataType(int value) {
        this(value, false);
    }

    /**
     * Gets target.
     *
     * @return the target
     */
    public boolean isTarget() {
        return target;
    }

    /**
     * Gets value.
     *
     * @return the value
     */
    public byte getValue() {
        return value;
    }

    @Override
    public String toString() {
        return toString;
    }
}
