package org.briljantframework.vector;

import org.briljantframework.matrix.BitMatrix;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;

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
  public static DoubleVector toVector(DoubleMatrix matrix) {
    DoubleVector.Builder builder = new DoubleVector.Builder(0, matrix.size());
    for (int i = 0; i < matrix.size(); i++) {
      builder.set(i, matrix.get(i));
    }
    return builder.build();
  }

  public static IntVector toVector(IntMatrix matrix) {
    IntVector.Builder builder = new IntVector.Builder(0, matrix.size());
    for (int i = 0; i < matrix.size(); i++) {
      builder.set(i, matrix.get(i));
    }
    return builder.build();
  }

  public static ComplexVector toVector(ComplexMatrix matrix) {
    ComplexVector.Builder builder = new ComplexVector.Builder(0, matrix.size());
    for (int i = 0; i < matrix.size(); i++) {
      builder.set(i, matrix.get(i));
    }
    return builder.build();
  }

  public static BitVector toVector(BitMatrix matrix) {
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
