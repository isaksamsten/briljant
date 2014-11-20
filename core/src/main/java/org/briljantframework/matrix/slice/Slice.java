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

package org.briljantframework.matrix.slice;

/**
 * A slice is basically an IntIterator
 * <p>
 * Created by isak on 24/06/14.
 */
public interface Slice {

    /**
     * Rewind this slice to its original position
     */
    void rewind();

    /**
     * Has next.
     *
     * @param max the max
     * @return true if there are more indices in this slice
     */
    boolean hasNext(int max);

    /**
     * Current int.
     *
     * @return the current index without moving to the next
     */
    int current();

    /**
     * Next int.
     *
     * @return the current index while incrementing the slice
     */
    int next();
}
