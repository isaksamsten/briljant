package org.briljantframework.linalg.api;

import org.briljantframework.linalg.decomposition.SingularValueDecomposition;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.api.MatrixFactory;

/**
 * Created by isak on 2/10/15.
 */
public interface LinearAlgebraRoutines {

  /**
   * DGETRF computes an LU factorization of a general M-by-N matrix A
   * using partial pivoting with row interchanges.
   *
   * @param a    A double matrix, dimension (LDA,N)
   *             On entry, the M-by-N matrix to be factored.
   *             On exit, the factors L and U from the factorization
   *             A = P*L*U; the unit diagonal elements of L are not stored.
   * @param ipiv An int array, dimension (min(M,N))
   *             The pivot indices; for 1 <= i <= min(M,N), row i of the
   *             matrix was interchanged with row IPIV(i).
   * @returns 0 if successful. If > 0 the factorization has been completed, but the factor U is
   * exactly singular, and division by zero will occur if it is used to solve a system of
   * equations.
   */
  int getrf(DoubleMatrix a, int[] ipiv);

  int dgelsy(DoubleMatrix a, DoubleMatrix b, int[] jpvt, double rcond);

  public SingularValueDecomposition gesvd(DoubleMatrix x);

  MatrixFactory getMatrixFactory();
}
