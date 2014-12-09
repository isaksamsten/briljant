package org.briljantframework.vector;

/**
 * Created by Isak Karlsson on 09/12/14.
 */
public final class As {

  private As() {}

  public static StringVector stringVector(Vector vector) {
    if (vector instanceof StringVector) {
      return (StringVector) vector;
    }
    return new StringVector.Builder().addAll(vector).build();
  }

  public static DoubleVector doubleVector(Vector vector) {
    if (vector instanceof DoubleVector) {
      return (DoubleVector) vector;
    }
    return new DoubleVector.Builder().addAll(vector).build();
  }

  public static IntVector intVector(Vector vector) {
    if (vector instanceof IntVector) {
      return (IntVector) vector;
    }
    return new IntVector.Builder().addAll(vector).build();
  }

  public static BinaryVector binaryVector(Vector vector) {
    if (vector instanceof BinaryVector) {
      return (BinaryVector) vector;
    }
    return new BinaryVector.Builder().addAll(vector).build();
  }

  public static ComplexVector complexVector(Vector vector) {
    if (vector instanceof ComplexVector) {
      return (ComplexVector) vector;
    }
    return new ComplexVector.Builder().addAll(vector).build();
  }
}
