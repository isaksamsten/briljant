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

package org.briljantframework.shapelet;

import java.util.ArrayList;
import java.util.List;

import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorView;

/**
 * A Shapelet is a (short) view of a larger data series (i.e. a vector). The underlying vector
 * should not be mutated (as this will change the view).
 * <p>
 * 
 * <pre>
 *     Shapelet shapelet = Shapelet.create(10, 10, frame.getEntry(10))
 * </pre>
 * <p>
 * creates a short view of the 10-th entry
 * <p>
 * <p>
 * 
 * @author Isak Karlsson
 */
// TODO: override getAs... to support the changed indexing
public class Shapelet extends VectorView {
  private final int start, length;

  /**
   * Instantiates a new Shapelet.
   *
   * @param start the start
   * @param length the length
   * @param vector the vector
   */
  public Shapelet(int start, int length, Vector vector) {
    super(vector);
    this.start = start;
    this.length = length; // inclusive
  }

  /**
   * From vector.
   *
   * @param start the start
   * @param length the length
   * @param vector the vector
   * @return the shapelet
   */
  public static Shapelet create(int start, int length, Vector vector) {
    return new Shapelet(start, length, vector);
  }

  /**
   * Gets start.
   *
   * @return the start
   */
  public int start() {
    return start;
  }

  @Override
  public double getAsDouble(int index) {
    return parent.getAsDouble(start + index);
  }

  @Override
  public int size() {
    return length;
  }

  @Override
  public String toString() {
    List<Double> r = new ArrayList<>();
    for (int i = 0; i < size(); i++) {
      r.add(getAsDouble(i));
    }
    return String.format("Shapelet(%s, shape=(%d, 1))", r, size());
  }
}
