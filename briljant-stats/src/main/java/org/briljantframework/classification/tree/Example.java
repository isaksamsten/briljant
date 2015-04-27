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

/**
 * An example represents the index to a specified instances with an associated weight.
 */
public final class Example {

  private final int index;
  private final double weight;

  public Example(int index, double weight) {
    this.index = index;
    this.weight = weight;
  }

  public int getIndex() {
    return index;
  }

  public double getWeight() {
    return weight;
  }

  public Example updateWeight(float weight) {
    return new Example(index, weight);
  }

  @Override
  public String toString() {
    return String.format("Example(index: %d, weight: %.2f)", index, weight);
  }
}
