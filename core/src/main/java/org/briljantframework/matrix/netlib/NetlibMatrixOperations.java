package org.briljantframework.matrix.netlib;

import com.github.fommil.netlib.BLAS;

import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Storage;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.base.BaseMatrixRoutines;

/**
 * Created by isak on 13/04/15.
 */
class NetlibMatrixOperations extends BaseMatrixRoutines {

  private final static BLAS blas = BLAS.getInstance();

  NetlibMatrixOperations(MatrixFactory matrixFactory) {
    super(matrixFactory);
  }

  @Override
  public int iamax(DoubleMatrix x) {
    Storage s = x.getStorage();
    if (s.getNativeType().equals(Double.TYPE) && s instanceof NetlibDoubleStorage) {
      double[] values = ((NetlibDoubleStorage) s).doubleArray();
      return blas.idamax(s.size(), values, 1);
    } else {
      return super.iamax(x);
    }
  }


}
