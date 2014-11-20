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

import org.briljantframework.data.column.Column;
import org.briljantframework.data.values.Missing;
import org.briljantframework.data.values.Value;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.Set;

/**
 * The type Header.
 */
public abstract class Type implements Serializable {

    private final String name;

    /**
     * Instantiates a new Header.
     *
     * @param name the name
     */
    public Type(String name) {
        this.name = name;
    }

    public abstract Column.CopyTo<? extends Column> getColumnFactory();

    /**
     * Type type.
     *
     * @return the type
     */
    public abstract DataType getDataType();

    /**
     * Name string.
     *
     * @return the string
     */
    public String getName() {
        return name;
    }

    /**
     * Has type.
     *
     * @param dataType the type
     * @return the boolean
     */
    public boolean hasType(DataType dataType) {
        return getDataType() == dataType;
    }

    /**
     * Has any type.
     *
     * @param dataType  the type
     * @param dataTypes the types
     * @return the boolean
     */
    public boolean hasAnyType(DataType dataType, DataType... dataTypes) {
        return EnumSet.of(dataType, dataTypes).contains(getDataType());
    }

    /**
     * Contains boolean.
     *
     * @param values the values
     * @return the boolean
     */
    public boolean contains(Collection<? extends Value> values) {
        return new HashSet<>(values).containsAll(getDomain());
    }

    /**
     * Values set.
     *
     * @return the set
     */
    public abstract Set<Value> getDomain();

    /**
     * Creates a new Value from <code>o</code>
     *
     * @param o - an object suitable for the types
     * @return a value suitable for the types
     */
    public final Value createValueFrom(Object o) {
        if (o == null || (o instanceof Double && Double.isNaN((Double) o))) {
            return Missing.valueOf();
        } else {
            return makeValueOf(o);
        }
    }

    /**
     * Convert value from.
     *
     * @param other the other
     * @return the value
     */
    public final Value convertValueFrom(Value other) {
        if (other == null || other instanceof Missing) {
            return Missing.valueOf();
        } else {
            return makeConversion(other);
        }
    }

    /**
     * Make value.
     *
     * @param o the o
     * @return the value
     */
    protected abstract Value makeValueOf(Object o);

    /**
     * Make conversion.
     *
     * @param value the value
     * @return the value
     */
    protected abstract Value makeConversion(Value value);

    @Override
    public String toString() {
        return String.format("(%s, %s)", name, getDataType());
    }

    /**
     * Is numeric.
     *
     * @return the boolean
     */
    public boolean isNumeric() {
        return getDataType() == DataType.NUMERIC || getDataType() == DataType.FACTOR;
    }
}
