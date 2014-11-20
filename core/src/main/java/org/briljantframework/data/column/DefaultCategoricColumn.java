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

package org.briljantframework.data.column;

import org.briljantframework.data.types.CategoricType;
import org.briljantframework.data.types.Type;
import org.briljantframework.data.values.Value;

import java.util.List;

/**
 * Created by Isak Karlsson on 13/08/14.
 */
public class DefaultCategoricColumn extends AbstractColumn implements CategoricColumn {

    /**
     * Instantiates a new Basic target.
     *
     * @param type   the types
     * @param values the values
     */
    public DefaultCategoricColumn(Type type, List<Value> values) {
        super(type, values);
    }

    /**
     * Gets builder.
     *
     * @param type the headers
     * @return the builder
     */
    public static Builder<CategoricColumn> newBuilder(Type type) {
        return new CategoricColumnBuilder(type);
    }

    /**
     * Gets factory.
     *
     * @return the factory
     */
    public static CopyTo<CategoricColumn> copyTo() {
        return target -> new CategoricColumnBuilder(new CategoricType(target.getName()));
    }

    @Override
    public Object get(int id) {
        return getValue(id).value();
    }

    /**
     * Created by isak on 17/08/14.
     */
    private static class CategoricColumnBuilder extends AbstractColumn.AbstractColumnBuilder<CategoricColumn> {

        /**
         * Instantiates a new Builder.
         *
         * @param type the types
         */
        public CategoricColumnBuilder(Type type) {
            super(type);
        }

        @Override
        public DefaultCategoricColumn create() {
            return new DefaultCategoricColumn(type, values);
        }
    }
}
