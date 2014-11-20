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

package org.briljantframework.learning;

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.Row;
import org.briljantframework.data.column.Column;

/**
 * The interface Classifier.
 * <p>
 */
public interface Classifier<R extends Row, D extends DataFrame<? extends R>, T extends Column> {

    Model<R, D> fit(D dataset, T target);

    /**
     * The interface Builder.
     */
    public static interface Builder<C extends Classifier<? extends Row, ? extends DataFrame, ? extends Column>> {

        /**
         * Create classifier.
         *
         * @return the classifier
         */
        C create();
    }
}
