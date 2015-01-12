package org.briljantframework.matrix;

import org.briljantframework.Check;

/**
 * Created by Isak Karlsson on 11/01/15.
 */
public final class Anys {
  private Anys() {}

  /**
   * <p>
   * Take values in {@code a}, using the indexes in {@code indexes}.
   * 
   * For example,
   * </p>
   * 
   * <pre>
   *  > import org.briljantframework.matrix.*;
   *    IntMatrix a = Ints.newMatrix(1, 2, 3, 4);
   *    IntMatrix indexes = Ints.newMatrix(0, 0, 1, 2, 3);
   *    IntMatrix taken = Anys.take(a, indexes).asIntMatrix();
   *    1  
   *    1  
   *    2  
   *    3  
   *    4  
   *    shape: 5x1 type: int
   * </pre>
   * 
   * Produces:
   * 
   * <pre>
   *  1  
   *  1  
   *  2  
   *  3  
   *  4  
   *  shape: 5x1 type: int
   * </pre>
   * 
   * @param a the source matrix
   * @param indexes the indexes of the values to extract
   * @return a new matrix; the returned matrix has the same type as {@code a} (as returned by
   *         {@link org.briljantframework.matrix.AnyMatrix#newEmptyMatrix(int, int)}).
   */
  public static AnyMatrix take(AnyMatrix a, IntMatrix indexes) {
    AnyMatrix taken = a.newEmptyMatrix(indexes.size(), 1);
    for (int i = 0; i < indexes.size(); i++) {
      taken.set(i, a, indexes.get(i));
    }
    return taken;
  }

  /**
   * Changes the values of a copy of {@code a} according to the values of the {@code mask} and the
   * values in {@code values}. The value at {@code i} in a copy of {@code a} is set to value at
   * {@code i} from {@code values} if the boolean at {@code i} in {@code mask} is {@code true}.
   * 
   * <pre>
   *  > import org.briljantframework.matrix.*;
   *    IntMatrix a = Ints.range(0, 10).reshape(5, 2)
   *    BitMatrix mask = a.greaterThan(5)
   *    IntMatrix values = a.mul(2)
   *    IntMatrix result = Anys.mask(a, mask, values).asIntMatrix()
   *    
   *    0   5   
   *    1   12  
   *    2   14  
   *    3   16  
   *    4   18  
   *    shape: 5x2 type: int
   * </pre>
   * 
   * @param a a source array
   * @param mask the mask; same shape as {@code a}
   * @param values the values; same shape as {@code a}
   * @return a new matrix; the returned matrix has the same type as {@code a}.
   */
  public static AnyMatrix mask(AnyMatrix a, BitMatrix mask, AnyMatrix values) {
    Check.equalShape(a, mask);
    Check.equalShape(a, values);

    AnyMatrix masked = a.copy();
    putMask(masked, mask, values);
    return masked;
  }

  /**
   * Changes the values of {@code a} according to the values of the {@code mask} and the values in
   * {@code values}.
   * 
   * <pre>
   *  > import org.briljantframework.matrix.*;
   *    IntMatrix a = Ints.range(0, 10).reshape(5, 2)
   *    BitMatrix mask = a.greaterThan(5)
   *    IntMatrix values = a.mul(2)
   *    Anys.putMask(a, mask, values)
   *    System.out.println(a)
   * 
   *    0   5   
   *    1   12  
   *    2   14  
   *    3   16
   *    4   18
   *    shape: 5x2 type: int
   * </pre>
   * 
   * @param a the target matrix
   * @param mask the mask; same shape as {@code a}
   * @param values the mask; same shape as {@code a}
   * @see #mask(AnyMatrix, BitMatrix, AnyMatrix)
   */
  public static void putMask(AnyMatrix a, BitMatrix mask, AnyMatrix values) {
    Check.equalShape(a, mask);
    Check.equalShape(a, values);
    for (int i = 0; i < a.size(); i++) {
      if (mask.get(i)) {
        a.set(i, values, i);
      }
    }
  }

  /**
   * Selects the values in {@code a} according to the values in {@code where}, replacing those not
   * selected with {@code replace}.
   * 
   * <pre>
   *  > import org.briljantframework.matrix.*;
   *    IntMatrix a = Ints.range(0, 10).reshape(5, 2)
   *    BitMatrix mask = a.greaterThan(5)
   *    IntMatrix b = Anys.select(a, mask, -1)
   * 
   *    -1  -1  
   *    -1   6  
   *    -1   7  
   *    -1   8  
   *    -1   9  
   *    shape: 5x2 type: int
   * </pre>
   * 
   * @param a the source matrix
   * @param where the selection matrix; same shape as {@code a}
   * @param replace the replacement value
   * @return a new matrix; the returned matrix has the same type as {@code a}.
   */
  public static AnyMatrix select(AnyMatrix a, BitMatrix where, Number replace) {
    Check.equalShape(a, where);
    AnyMatrix copy = a.copy();
    for (int i = 0; i < a.size(); i++) {
      if (!where.get(i)) {
        copy.set(i, replace);
      }
    }
    return copy;
  }

  /**
   * Selects the values in {@code a} according to the values in {@code where}.
   * 
   * <pre>
   *  > import org.briljantframework.matrix.*;
   *    IntMatrix a = Ints.range(0, 10).reshape(5, 2)
   *    BitMatrix mask = a.greaterThan(5)
   *    DoubleMatrix b = Anys.select(a.asDoubleMatrix(), mask).asDoubleMatrix()
   * 
   *    6.0000  
   *    7.0000  
   *    8.0000  
   *    9.0000  
   *    shape: 4x1 type: double
   * </pre>
   * 
   * @param a the source matrix
   * @param where the selection matrix; same shape as {@code a}
   * @return a new matrix; the returned matrix has the same type as {@code a}
   */
  public static AnyMatrix select(AnyMatrix a, BitMatrix where) {
    Check.equalShape(a, where);
    AnyMatrix.Builder builder = a.newBuilder();
    for (int i = 0; i < a.size(); i++) {
      if (where.get(i)) {
        builder.add(a, i);
      }
    }
    return builder.build();
  }
}
