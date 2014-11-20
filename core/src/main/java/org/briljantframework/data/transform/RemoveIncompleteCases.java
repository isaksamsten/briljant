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

package org.briljantframework.data.transform;

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.values.Value;

/**
 * Created by Isak Karlsson on 15/08/14.
 */
public class RemoveIncompleteCases<D extends DataFrame<?>> implements Transformer<D> {

    @Override
    public Transformation<D> fit(D container) {
        return new DoRemoveIncompleteCases<>();
    }

    private static final class DoRemoveIncompleteCases<E extends DataFrame<?>> implements Transformation<E> {

        @Override
        public E transform(E dataset, DataFrame.CopyTo<E> copyTo) {
            DataFrame.Builder<E> builder = copyTo.newBuilder(dataset.getTypes());
            dataset.stream().filter(x -> !x.stream().anyMatch(Value::na)).forEach(builder::addRow);
            return builder.create();
        }
    }
}
