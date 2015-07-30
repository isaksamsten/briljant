package org.briljantframework.example.matrix;

import org.briljantframework.linalg.api.LinearAlgebraRoutines;
import org.briljantframework.array.DoubleMatrix;
import org.briljantframework.array.T;
import org.briljantframework.array.api.MatrixBackend;
import org.briljantframework.array.api.MatrixFactory;
import org.briljantframework.array.api.MatrixRoutines;
import org.briljantframework.array.netlib.NetlibMatrixBackend;

/**
 * Created by isak on 14/05/15.
 */
public class Ex7 {

  public static void main(String[] args) {
    MatrixBackend mb = new NetlibMatrixBackend();
    MatrixFactory bj = mb.getMatrixFactory();
    MatrixRoutines bjr = mb.getMatrixRoutines();
    LinearAlgebraRoutines linalg = mb.getLinearAlgebraRoutines();

    DoubleMatrix x = bj.matrix(new double[]{
        1, 5, 9,
        2, 6, 10,
        3, 7, 11,
        4, 8, 12
    }).reshape(4, 3);

    DoubleMatrix c = bj.doubleMatrix(3, 3);
    bjr.gemm(T.YES, T.NO, 1, x, x, 1, c);
    double sum = bjr.sum(c);
    System.out.println(sum);
  }
}
