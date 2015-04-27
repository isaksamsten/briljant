/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.classification.tree;

import org.briljantframework.shapelet.Shapelet;

/**
 * @author Isak Karlsson
 */
public class ShapeletThreshold {

  private final Shapelet shapelet;
  private final double distance;

  public ShapeletThreshold(Shapelet shapelet, double distance) {
    this.shapelet = shapelet;
    this.distance = distance;
  }

  public Shapelet getShapelet() {
    return shapelet;
  }

  public double getDistance() {
    return distance;
  }

}