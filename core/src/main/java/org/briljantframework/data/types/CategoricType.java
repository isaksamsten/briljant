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
import org.briljantframework.data.column.DefaultCategoricColumn;
import org.briljantframework.data.values.Categoric;
import org.briljantframework.data.values.Value;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by Isak Karlsson on 11/06/14.
 */
public class CategoricType extends Type {

    private final Map<Object, Categoric> values;

    /**
     * Instantiates a new Categoric types.
     *
     * @param name the name
     */
    public CategoricType(String name) {
        this(name, new HashMap<>());
    }

    @Override
    public Column.CopyTo<? extends Column> getColumnFactory() {
        return DefaultCategoricColumn.copyTo();
    }

    /**
     * Instantiates a new Categoric type.
     *
     * @param name   the name
     * @param values the values
     */
    protected CategoricType(String name, Map<Object, Categoric> values) {
        super(name);
        this.values = values;
    }

    @Override
    public DataType getDataType() {
        return DataType.CATEGORIC;
    }

    @Override
    public Set<Value> getDomain() {
        return new HashSet<>(values.values());
    }

    @Override
    protected Value makeConversion(Value value) {
        if (value instanceof Categoric) {
            Categoric newValue = values.get(value.value());
            if (newValue == null) {
                this.values.put(value.value(), (Categoric) value);
            }
            return value;
        } else {
            return makeValueOf(value.value());
        }
    }

    /**
     * @param o - if o instanceof Integer use as id otherwise use as Categoric string
     * @return a Categoric value
     * @throws java.lang.IllegalArgumentException if o is Integer and not found
     */
    @Override
    public Value makeValueOf(Object o) {
        Categoric value = values.get(o);
        if (value == null) {
            value = Categoric.valueOf(o.toString());
            values.put(o, value);
        }
        return value;
    }
}