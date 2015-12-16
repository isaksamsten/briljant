package org.briljantframework.array;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class ShapeUtils {

  /**
   * Checks if the given shapes are broadcast compatible.
   * 
   * <p/>
   * An array can be broadcast to a specified shape if the dimensions are compatible. The shapes are
   * by compared element-wise starting with the trailing dimension. Two dimensions are compatible
   * if:
   * <ul>
   * <li>they are equal; or</li>
   * <li>one of them is equal to {@code 1}</li>
   * </ul>
   * 
   * @param a the first shape
   * @param b the second shape
   * @return true if the shapes are broadcast compatible
   */
  public static boolean isBroadcastCompatible(int[] a, int[] b) {
    int ac = b.length - 1;
    int bc = a.length - 1;
    for (int i = 0; i < b.length; i++) {
      if (i >= a.length) {
        break;
      }
      if (b[ac - i] != a[bc - i] && a[bc - i] != 1) {
        return false;
      }
    }
    return true;
  }

  /**
   * Compute the size of an array with the given shape.
   * 
   * @param shape the shape
   * @return the size
   * @throws ArithmeticException if the size is larger than an int
   */
  public static int size(int[] shape) throws ArithmeticException {
    int size = shape[0];
    for (int i = 1; i < shape.length; i++) {
      size = Math.multiplyExact(size, shape[i]);
    }
    return size;
  }
}
