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

import org.briljantframework.array.BaseArray;

/**
 * @author Isak Karlsson
 */
public final class NonConformantException extends RuntimeException {

  public NonConformantException(String param1, BaseArray op1, String param2, BaseArray op2) {
    this(param1, op1.getShape(), param2, op2.getShape());
  }

  public NonConformantException(BaseArray op1, BaseArray op2) {
    this("op1", op1, "op2", op2);
  }

  public NonConformantException(int am, int an, int bm, int bn) {
    this("op1", new int[]{am, an}, "op2", new int[]{bm, bn});
  }

  public NonConformantException(String op1, int[] shapeOp1, String op2, int[] shapeOp2) {
    super(String.format("nonconformant arguments (%s is %s, %s is %s)",
                        op1, formatShape(shapeOp1), op2, formatShape(shapeOp2)));
  }

  public NonConformantException(String message) {
    super(message);
  }

  private static String formatShape(int[] shape) {
    StringBuilder b = new StringBuilder();
    b.append(shape[0]);
    for (int i = 1; i < shape.length; i++) {
      b.append("x").append(shape[i]);
    }
    return b.toString();
  }
}
