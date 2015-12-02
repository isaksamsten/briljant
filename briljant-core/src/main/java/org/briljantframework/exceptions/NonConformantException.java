/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
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
    this("op1", new int[] {am, an}, "op2", new int[] {bm, bn});
  }

  public NonConformantException(String op1, int[] shapeOp1, String op2, int[] shapeOp2) {
    super(String.format("nonconformant arguments (%s is %s, %s is %s)", op1, formatShape(shapeOp1),
        op2, formatShape(shapeOp2)));
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
