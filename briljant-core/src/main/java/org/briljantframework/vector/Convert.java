package org.briljantframework.vector;

import org.briljantframework.complex.Complex;
import org.briljantframework.matrix.BitMatrix;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;

import static com.google.common.primitives.Ints.checkedCast;

/**
 * @author Isak Karlsson
 */
public final class Convert {

  private Convert() {
  }

//  public static Value toValue(String s) {
//    return StringValue.valueOf(s);
//  }
//
//  public static Value toValue(int i) {
//    return IntValue.valueOf(i);
//  }
//
//  public static Value toValue(double d) {
//    return DoubleValue.valueOf(d);
//  }
//
//  public static Value toValue(Bit bit) {
//    return BitValue.valueOf(bit);
//  }
//
//  public static Value toValue(boolean bit) {
//    return toValue(Bit.valueOf(bit));
//  }
//
//  public static Value toValue(Complex complex) {
//    return ComplexValue.valueOf(complex);
//  }
//
//  public static Value toValue(Object object) {
//    if (object == null) {
//      return Undefined.INSTANCE;
//    } else if (object instanceof String) {
//      return toValue((String) object);
//    } else if (object instanceof Double) {
//      return toValue((double) object);
//    } else if (object instanceof Integer) {
//      return toValue((int) object);
//    } else if (object instanceof Complex) {
//      return toValue((Complex) object);
//    } else if (object instanceof Bit) {
//      return toValue((Bit) object);
//    } else if (object instanceof Boolean) {
//      return toValue((boolean) object);
//    } else {
//      return new GenericValue(object);
//    }
//  }

  /**
   * Adapts the {@code DoubleMatrix} as a {@code DoubleVector}. Modifications to {@code matrix} is
   * propagated to the vector.
   *
   * @param matrix the matrix
   * @return a vector using the matrix as its underlying representation
   */
  public static Vector toAdapter(DoubleMatrix matrix) {
    return new AbstractDoubleVector() {

      @Override
      public double getAsDouble(int index) {
        return matrix.get(index);
      }

      @Override
      public int size() {
        return checkedCast(matrix.size());
      }
    };
  }

  /**
   * Adapts the {@code IntMatrix} as a {@code IntVector}. Modifications to {@code matrix} is
   * propagated to the vector.
   *
   * @param matrix the matrix
   * @return a vector using the matrix as its underlying representation
   */
  public static Vector toAdapter(IntMatrix matrix) {
    return new AbstractIntVector() {
      @Override
      public int getAsInt(int index) {
        return matrix.get(index);
      }

      @Override
      public int size() {
        return checkedCast(matrix.size());
      }
    };
  }

  /**
   * Adapts the {@code BitMatrix} as a {@code BitVector}. Modifications to {@code matrix} is
   * propagated to the vector.
   *
   * @param matrix the matrix
   * @return a vector using the matrix as its underlying representation
   */
  public static Vector toAdapter(BitMatrix matrix) {
    return new AbstractBitVector() {
      @Override
      public int getAsInt(int index) {
        return matrix.get(index) ? 1 : 0;
      }

      @Override
      public int size() {
        return checkedCast(matrix.size());
      }
    };
  }

  /**
   * Adapts the {@code ComplexMatrix} as a {@code ComplexVector}. Modifications to {@code matrix}
   * is
   * propagated to the vector.
   *
   * @param matrix the matrix
   * @return a vector using the matrix as its underlying representation
   */
  public static Vector toAdapter(ComplexMatrix matrix) {
    return new AbstractComplexVector() {
      @Override
      public double getAsDouble(int index) {
        return matrix.get(index).real();
      }

      @Override
      public Complex getAsComplex(int index) {
        return matrix.get(index);
      }

      @Override
      public int size() {
        return checkedCast(matrix.size());
      }
    };
  }


  /**
   * @param matrix the matrix
   * @return a vector representation of {@code matrix}
   */
  public static DoubleVector toVector(DoubleMatrix matrix) {
    DoubleVector.Builder builder = new DoubleVector.Builder(0, checkedCast(matrix.size()));
    for (int i = 0; i < matrix.size(); i++) {
      builder.set(i, matrix.get(i));
    }
    return builder.build();
  }

  public static IntVector toVector(IntMatrix matrix) {
    IntVector.Builder builder = new IntVector.Builder(0, checkedCast(matrix.size()));
    for (int i = 0; i < matrix.size(); i++) {
      builder.set(i, matrix.get(i));
    }
    return builder.build();
  }

  public static ComplexVector toVector(ComplexMatrix matrix) {
    ComplexVector.Builder builder = new ComplexVector.Builder(0, checkedCast(matrix.size()));
    for (int i = 0; i < matrix.size(); i++) {
      builder.set(i, matrix.get(i));
    }
    return builder.build();
  }

  public static BitVector toVector(BitMatrix matrix) {
    BitVector.Builder builder = new BitVector.Builder(0, checkedCast(matrix.size()));
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
