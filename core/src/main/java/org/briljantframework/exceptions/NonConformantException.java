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

package org.briljantframework.exceptions;

import org.briljantframework.matrix.Matrix;

/**
 * Created by isak on 21/06/14.
 */
public class NonConformantException extends RuntimeException {

  /**
   * Instantiates a new Non conformant exception.
   *
   * @param param1 the param 1
   * @param op1 the op 1
   * @param param2 the param 2
   * @param op2 the op 2
   */
  public NonConformantException(String param1, Matrix op1, String param2, Matrix op2) {
    this(param1, op1.rows(), op1.columns(), param2, op2.rows(), op2.columns());
  }

  /**
   * Instantiates a new Non conformant exception.
   *
   * @param op1 the op 1
   * @param op2 the op 2
   */
  public NonConformantException(Matrix op1, Matrix op2) {
    this("op1", op1, "op2", op2);
  }

  /**
   * Instantiates a new Non conformant exception.
   * 
   * @param am the am
   * @param an the an
   * @param bm the bm
   * @param bn the bn
   */
  public NonConformantException(long am, long an, long bm, long bn) {
    this("op1", am, an, "op2", bm, bn);
  }

  /**
   * Instantiates a new Non conformant exception.
   * 
   * @param op1 the op 1
   * @param am the am
   * @param an the an
   * @param op2 the op 2
   * @param bm the bm
   * @param bn the bn
   */
  public NonConformantException(String op1, long am, long an, String op2, long bm, long bn) {
    super(String.format("nonconformant arguments (%s is %dx%d, %s is %dx%d)", op1, am, an, op2, bm,
        bn));
  }

}
