/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
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
package org.briljantframework.data;

/**
 * A boolean numeric value supporting NA.
 * 
 * @author Isak Karlsson
 */
public class Logical extends Number {

  public static final Logical TRUE = new Logical((byte) 1);
  public static final Logical FALSE = new Logical((byte) 0);
  public static final Logical NA = new Logical((byte) -1);

  private final byte state;

  private Logical(byte state) {
    this.state = state;
  }

  public static Logical valueOf(boolean value) {
    return value ? TRUE : FALSE;
  }

  public static Logical valueOf(int value) {
    switch (value) {
      case 1:
        return TRUE;
      case 0:
        return FALSE;
      default:
        return NA;
    }
  }

  @Override
  public String toString() {
    return String.valueOf(intValue());
  }

  @Override
  public int intValue() {
    return state == -1 ? Na.INT : state;
  }

  @Override
  public long longValue() {
    return state == -1 ? Na.LONG : state;
  }

  @Override
  public float floatValue() {
    return state == -1 ? Na.FLOAT : state;
  }

  @Override
  public double doubleValue() {
    return state == -1 ? Na.DOUBLE : state;
  }
}
