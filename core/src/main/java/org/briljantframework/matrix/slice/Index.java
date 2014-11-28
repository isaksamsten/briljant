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

package org.briljantframework.matrix.slice;

/**
 * Created by isak on 21/06/14.
 */
public class Index implements Slicer {

  private final int[] indicies;

  /**
   * Instantiates a new Index.
   *
   * @param indicies the indicies
   */
  Index(int[] indicies) {
    this.indicies = indicies;
  }

  /**
   * Of index.
   *
   * @param args the args
   * @return the index
   */
  public static Index of(int... args) {
    if (args.length <= 0) {
      throw new IllegalArgumentException("to few indices");
    }
    return new Index(args);
  }

  @Override
  public Slice getSlice() {
    return new Slice() {

      private int current = 0;

      @Override
      public void rewind() {
        this.current = 0;
      }

      @Override
      public boolean hasNext(int max) {
        return current < indicies.length && indicies[current] < max;
      }

      @Override
      public int current() {
        return indicies[current];
      }

      @Override
      public int next() {
        return indicies[current++];
      }
    };
  }

  @Override
  public int length() {
    return indicies.length;
  }

  @Override
  public String toString() {
    StringBuilder b = new StringBuilder("Index(");
    int j = 0;
    for (int i : indicies) {
      if (j++ > 10) {
        b.append("...,");
        break;
      }
      b.append(indicies[i]);
      b.append(',');
    }
    b.replace(b.length() - 1, b.length(), "");
    b.append(")");
    return b.toString();
  }
}
