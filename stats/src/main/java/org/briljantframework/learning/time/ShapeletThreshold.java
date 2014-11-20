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

package org.briljantframework.learning.time;

/**
 * Created by Isak Karlsson on 17/09/14.
 */
public class ShapeletThreshold {

    private final Shapelet shapelet;
    private final double distance;

    private ShapeletThreshold(Shapelet shapelet, double distance) {
        this.shapelet = shapelet;
        this.distance = distance;
    }

    /**
     * Create shapelet threshold.
     *
     * @param shapelet the shapelet
     * @param distance the distance
     * @return the shapelet threshold
     */
    public static ShapeletThreshold create(Shapelet shapelet, double distance) {
        return new ShapeletThreshold(shapelet, distance);
    }


    /**
     * Gets shapelet.
     *
     * @return the shapelet
     */
    public Shapelet getShapelet() {
        return shapelet;
    }

    /**
     * Gets distance.
     *
     * @return the distance
     */
    public double getDistance() {
        return distance;
    }

}
