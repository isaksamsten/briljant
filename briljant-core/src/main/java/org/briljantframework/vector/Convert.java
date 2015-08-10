/*
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

package org.briljantframework.vector;

import org.briljantframework.array.BitArray;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;

/**
 * @author Isak Karlsson
 */
public final class Convert {

  private Convert() {
  }

  /**
   * @param matrix the matrix
   * @return a vector representation of {@code matrix}
   */
  public static DoubleVector toVector(DoubleArray matrix) {
    DoubleVector.Builder builder = new DoubleVector.Builder(0, matrix.size());
    for (int i = 0; i < matrix.size(); i++) {
      builder.set(i, matrix.get(i));
    }
    return builder.build();
  }

  public static IntVector toVector(IntArray matrix) {
    IntVector.Builder builder = new IntVector.Builder(0, matrix.size());
    for (int i = 0; i < matrix.size(); i++) {
      builder.set(i, matrix.get(i));
    }
    return builder.build();
  }

  public static ComplexVector toVector(ComplexArray matrix) {
    ComplexVector.Builder builder = new ComplexVector.Builder(0, matrix.size());
    for (int i = 0; i < matrix.size(); i++) {
      builder.set(i, matrix.get(i));
    }
    return builder.build();
  }

  public static BitVector toVector(BitArray matrix) {
    BitVector.Builder builder = new BitVector.Builder(0, matrix.size());
    for (int i = 0; i < matrix.size(); i++) {
      builder.set(i, matrix.get(i));
    }
    return builder.build();
  }

  /**
   * Convert {@code vector} to a StringVector.
   *
   * @param vector the vector
   * @return a new StringVector
   */
  public static Vector toStringVector(Vector vector) {
    if (vector instanceof GenericVector && vector.getType().getDataClass().equals(String.class)) {
      return vector;
    }
    return new GenericVector.Builder(String.class).addAll(vector).build();
  }

  public static DoubleVector toDoubleVector(Vector vector) {
    if (vector instanceof DoubleVector) {
      return (DoubleVector) vector;
    }
    return new DoubleVector.Builder().addAll(vector).build();
  }

  public static IntVector toIntVector(Vector vector) {
    if (vector instanceof IntVector) {
      return (IntVector) vector;
    }
    return new IntVector.Builder().addAll(vector).build();
  }

  public static BitVector toBitVector(Vector vector) {
    if (vector instanceof BitVector) {
      return (BitVector) vector;
    }
    return new BitVector.Builder().addAll(vector).build();
  }

  public static ComplexVector toComplexVector(Vector vector) {
    if (vector instanceof ComplexVector) {
      return (ComplexVector) vector;
    }
    return new ComplexVector.Builder().addAll(vector).build();
  }

}
