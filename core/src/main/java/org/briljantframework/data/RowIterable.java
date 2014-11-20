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

package org.briljantframework.data;

import java.util.Iterator;

/**
 * Created by Isak Karlsson on 19/08/14.
 */
public class RowIterable<R extends Row> implements Iterable<R> {
    private final DataFrame<? extends R> dataFrame;

    /**
     * Instantiates a new Entry iterable.
     *
     * @param dataFrame the dataset
     */
    public RowIterable(DataFrame<? extends R> dataFrame) {
        this.dataFrame = dataFrame;
    }

    @Override
    public Iterator<R> iterator() {
        return new Iterator<R>() {

            private int row = 0;

            @Override
            public boolean hasNext() {
                return row < dataFrame.rows();
            }

            @Override
            public R next() {
                return dataFrame.getRow(row++);
            }
        };
    }
}
