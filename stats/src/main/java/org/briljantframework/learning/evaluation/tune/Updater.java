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

package org.briljantframework.learning.evaluation.tune;

/**
 * Created by Isak Karlsson on 24/09/14.
 *
 * @param <T> the type parameter
 */
public interface Updater<T> {

    /**
     * Gets name.
     *
     * @return the name
     */
    String getParameter();

    /**
     * Reset void.
     */
    public void restore();

    /**
     * Can update.
     *
     * @return the boolean
     */
    public boolean hasUpdate();

    /**
     * Update void.
     *
     * @param toUpdate the to update
     * @return the object
     */
    public Object update(T toUpdate);

    /**
     * The interface Update.
     *
     * @param <T> the type parameter
     * @param <V> the type parameter
     */
    @FunctionalInterface
    public interface Update<T, V> {
        /**
         * Update void.
         *
         * @param toUpdate the to update
         * @param value    the value
         */
        void update(T toUpdate, V value);
    }
}
