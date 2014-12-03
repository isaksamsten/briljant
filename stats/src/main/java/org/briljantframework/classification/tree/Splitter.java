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

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

/**
 * Created by Isak Karlsson on 09/09/14.
 *
 * @param <T> the type parameter
 */
public interface Splitter<T> {

  /**
   * Find find a "good" separating split in dataset using Examples
   *
   * @param examples the examples
   * @param dataset the container
   * @return the examples . split
   */
  Tree.Split<T> find(Examples examples, DataFrame dataset, Vector target);


  /**
   * The interface Builder.
   *
   * @param <T> the type parameter
   */
  public static interface Builder<T extends Splitter<?>> {
    /**
     * Create splitter.
     *
     * @return the splitter
     */
    T create();
  }
}
