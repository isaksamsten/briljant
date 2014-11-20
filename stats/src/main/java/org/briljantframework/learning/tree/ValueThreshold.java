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

package org.briljantframework.learning.tree;

import org.briljantframework.data.values.Value;

/**
 * Created by Isak Karlsson on 23/09/14.
 */
public class ValueThreshold {

    private final int axis;
    private final Value threshold;

    private ValueThreshold(int axis, Value threshold) {
        this.axis = axis;
        this.threshold = threshold;
    }

    /**
     * Create value threshold.
     *
     * @param axis      the axis
     * @param threshold the threshold
     * @return the value threshold
     */
    public static ValueThreshold create(int axis, Value threshold) {
        return new ValueThreshold(axis, threshold);
    }

    /**
     * Gets axis.
     *
     * @return the axis
     */
    public int getAxis() {
        return axis;
    }

    /**
     * Gets threshold.
     *
     * @return the threshold
     */
    public Value getValue() {
        return threshold;
    }
}
