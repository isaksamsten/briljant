package org.briljantframework.array.netlib;

import com.github.fommil.netlib.BLAS;

import org.briljantframework.Check;
import org.briljantframework.array.AbstractDoubleArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.Op;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.exceptions.NonConformantException;

import java.util.Objects;

/**
 * @author Isak Karlsson
 */
class NetlibDoubleArray extends AbstractDoubleArray {

  private static BLAS blas = BLAS.getInstance();
  private final double[] data;

  NetlibDoubleArray(ArrayFactory bj, int size) {
    super(bj, new int[]{size});
    data = new double[size];
  }

  NetlibDoubleArray(ArrayFactory bj, double[] data) {
    super(bj, new int[]{Objects.requireNonNull(data).length});
    this.data = data;
  }

  public NetlibDoubleArray(ArrayFactory bj, int[] shape) {
    super(bj, shape);
    this.data = new double[size()];
  }

  public NetlibDoubleArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride,
                           double[] data) {
    super(bj, offset, shape, stride, majorStride);
    this.data = data;
  }

  public NetlibDoubleArray(ArrayFactory bj, double[] data, int rows, int columns) {
    super(bj, new int[]{rows, columns});
    this.data = data;
  }

  @Override
  protected double getElement(int i) {
    return data[i];
  }

  @Override
  protected void setElement(int i, double value) {
    data[i] = value;
  }

  @Override
  public DoubleArray newEmptyArray(int... shape) {
    return new NetlibDoubleArray(getArrayFactory(), shape);
  }

  @Override
  public DoubleArray asView(int offset, int[] shape, int[] stride, int majorStride) {
    return new NetlibDoubleArray(
        getArrayFactory(),
        offset,
        shape,
        stride,
        majorStride,
        data
    );
  }

  @Override
  protected int elementSize() {
    return data.length;
  }

  @Override
  public DoubleArray mmul(double alpha, Op transA, DoubleArray b, Op transB) {
    Check.argument(b.isMatrix());
    Check.state(isMatrix());
    if (stride(0) == 1 && b.stride(0) == 1 && stride(1) >= size(1) && b.stride(1) >= b.size(1)) {
      int m = size(transA == Op.KEEP ? 0 : 1);
      int bm = b.size(transB == Op.KEEP ? 0 : 1);
      int n = b.size(transB == Op.KEEP ? 1 : 0);
      int k = size(transA == Op.KEEP ? 1 : 0);
      if (b.size(transB == Op.KEEP ? 0 : 1) != size(transA == Op.KEEP ? 1 : 0)) {
        throw new NonConformantException(this, b);
      }
      if (m == 0 || k == 0 || n == 0 || bm == 0) {
        return new NetlibDoubleArray(getArrayFactory(), new double[0], 0, 0);
      }

//      if (m != c.size(0) || n != c.size(1)) {
//        throw new NonConformantException(String.format(
//            "a has size (%d,%d), b has size (%d,%d), c has size (%d, %d)",
//            m, k, k, n, c.size(0), c.size(1)));
//      }

      double[] ca = new double[m * n];
      blas.dgemm(
          transA.asString(),
          transB.asString(),
          m,
          n,
          k,
          alpha,
          data(),
          getOffset(),
          Math.max(1, stride(1)),
          b.data(),
          b.getOffset(),
          Math.max(1, b.stride(1)),
          1.0,
          ca,
          0,
          Math.max(1, n)
      );
      return new NetlibDoubleArray(getArrayFactory(), ca, m, n);
    }

//    if (!isView() && !b.isView()) {
//      String transA = "n";
//      int thisRows = rows();
//      int thisColumns = columns();
//      if (transA.isTrue()) {
//        thisRows = columns();
//        thisColumns = rows();
//        transA = "t";
//      }
//
//      String transB = "n";
//      int otherRows = b.rows();
//      int otherColumns = b.columns();
//      if (transB.isTrue()) {
//        otherRows = b.columns();
//        otherColumns = b.rows();
//        transB = "t";
//      }
//
//      if (thisColumns != otherRows) {
//        throw new NonConformantException(this, b);
//      }
//      double[] tmp = new double[Math.multiplyExact(thisRows, otherColumns)];
//      blas.dgemm(transA, transB, thisRows, otherColumns, otherRows, alpha, data,
//                 this.rows(), b.data(), b.rows(), 0, tmp,
//                 thisRows);
//
//      return new NetlibDoubleArray(getArrayFactory(), tmp, thisRows, otherColumns);
    else {
      return super.mmul(alpha, transA, b, transB);
    }

  }

  @Override
  public double[] data() {
    return data;
  }
}
