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
import org.briljantframework.data.column.DefaultNumericColumn;
import org.briljantframework.data.values.*;

import java.util.Collections;
import java.util.Set;

/**
 * Created by Isak Karlsson on 11/06/14.
 */
public class NumericType extends Type {

    /**
     * Instantiates a new Numeric types.
     *
     * @param name the name
     */
    public NumericType(String name) {
        super(name);
    }

    @Override
    public Column.CopyTo<? extends Column> getColumnFactory() {
        return DefaultNumericColumn.copyTo();
    }

    @Override
    public DataType getDataType() {
        return DataType.NUMERIC;
    }

    /**
     * Instantiates a new Numeric types.
     *
     * @param index the index
     * @param name  the name
     */
    public NumericType(int index, String name) {
        this(name);
    }

    @Override
    public Set<Value> getDomain() {
        return Collections.emptySet();
    }

    @Override
    public Value makeValueOf(Object o) {
        if (o instanceof Number) {
            return Numeric.valueOf(((Number) o).doubleValue());
        } else {
            return Missing.valueOf();
        }
    }

    @Override
    protected Value makeConversion(Value value) {
        if (value instanceof Numeric) {
            return value;
        } else if (value instanceof Categoric) {
            try {
                return Numeric.valueOf(Double.parseDouble(value.value().toString()));
            } catch (NumberFormatException e) {
                return Missing.valueOf();
            }
        } else if (value instanceof Numeric) {
            return value;
        } else if (value instanceof Factor) {
            return Numeric.valueOf(((Factor) value).asDouble());
        } else {
            return Missing.valueOf();
        }
    }

    @Override
    public NumericType clone() {
        return this;
    }
}
