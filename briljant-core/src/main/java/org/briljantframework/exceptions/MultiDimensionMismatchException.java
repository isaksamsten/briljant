/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.exceptions;

import org.briljantframework.array.BaseArray;

/**
 * Exception indicating that two arrays have illegal dimensions.
 * 
 * @author Isak Karlsson
 */
public final class MultiDimensionMismatchException extends IllegalArgumentException {

  private final int[] wrong, expected;

  public MultiDimensionMismatchException(BaseArray<?> op1, BaseArray<?> op2) {
    this(op1.getShape(), op2.getShape());
  }

  public MultiDimensionMismatchException(int[] wrong, int[] expected) {
    super(String.format("Dimension mismatch (%s != %s)", formatShape(wrong), formatShape(expected)));

    this.wrong = wrong.clone();
    this.expected = expected.clone();
  }

  private static String formatShape(int[] shape) {
    StringBuilder b = new StringBuilder();
    b.append(shape[0]);
    for (int i = 1; i < shape.length; i++) {
      b.append("x").append(shape[i]);
    }
    return b.toString();
  }

  public MultiDimensionMismatchException(int am, int an, int bm, int bn) {
    this(new int[] {am, an}, new int[] {bm, bn});
  }

  public int[] getWrongDimensions() {
    return wrong.clone();
  }

  public int[] getExpectedDimension() {
    return expected.clone();
  }
}
