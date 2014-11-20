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

import org.briljantframework.data.values.Value;

import java.util.Iterator;
import java.util.stream.Collectors;

/**
 * Created by Isak Karlsson on 12/08/14.
 */
public class RowView implements Row, Cloneable {

    /**
     * The Dataset.
     */
    protected final DataFrame dataFrame;

    /**
     * The Index.
     */
    protected final int index;

    /**
     * Instantiates a new Entry cursor.
     *
     * @param dataFrame the dataset
     * @param index   the index
     */
    protected RowView(DataFrame dataFrame, int index) {
        this.dataFrame = dataFrame;
        this.index = index;
    }

    @Override
    public Iterator<Value> iterator() {
        return new Iterator<Value>() {
            private int col = 0;

            @Override
            public boolean hasNext() {
                return col < size();
            }

            @Override
            public Value next() {
                return RowView.this.getValue(col++);
            }
        };
    }

    @Override
    public Value getValue(int col) {
        return dataFrame.getValue(index, col);
    }

    @Override
    public int size() {
        return dataFrame.columns();
    }

    @Override
    public MutableRow asMutable() {
        return new MutableRowView(dataFrame, index);
    }

    @Override
    public String toString() {
        String values = String.join(",", stream().map(Value::toString).collect(Collectors.toList()));
        return String.format("RowView(row: %d, size=%d, value=(%s))", index, size(), values);
    }
}
