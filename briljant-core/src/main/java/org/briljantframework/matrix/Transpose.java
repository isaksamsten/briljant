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

package org.briljantframework.matrix;

/**
 * Created by Isak Karlsson on 25/06/14.
 */
public enum Transpose {
  /**
   * Simple transpose
   */
  YES("t", true),

  /**
   * Don't do anything
   */
  NO("n", false),

  /**
   * Conjugate transpose (only for complex matrices)
   */
  CONJ("c", true);

  private final String string;
  private final boolean transpose;

  private Transpose(String string, boolean transpose) {
    this.string = string;
    this.transpose = transpose;
  }

  public boolean transpose() {
    return transpose;
  }

  /**
   * Cblas transpose.
   *
   * @return the int
   */
  public String asString() {
    return string;
  }
}