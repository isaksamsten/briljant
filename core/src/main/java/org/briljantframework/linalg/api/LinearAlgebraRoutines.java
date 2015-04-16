package org.briljantframework.linalg.api;

import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.IntMatrix;
import org.briljantframework.matrix.api.MatrixFactory;

/**
 * Created by isak on 2/10/15.
 */
public interface LinearAlgebraRoutines {

  /**
   * Computes the eigenvalues and, optionally, the left and/or right eigenvectors for symmetric
   * matrices
   *
   * @param jobz 'N':  Compute eigenvalues only; 'V':  Compute eigenvalues and eigenvectors.
   * @param uplo 'U':  Upper triangle of A is stored; 'L':  Lower triangle of A is stored.
   * @param a    On entry, the symmetric matrix A.  If UPLO = 'U', the
   *             leading N-by-N upper triangular part of A contains the
   *             upper triangular part of the matrix A.  If UPLO = 'L',
   *             the leading N-by-N lower triangular part of A contains
   *             the lower triangular part of the matrix A.
   *             On exit, if JOBZ = 'V', then if INFO = 0, A contains the
   *             orthonormal eigenvectors of the matrix A.
   *             If JOBZ = 'N', then on exit the lower triangle (if UPLO='L')
   *             or the upper triangle (if UPLO='U') of A, including the
   *             diagonal, is destroyed.
   * @param w    the eigenvalues in ascending order
   */
  void syev(char jobz, char uplo, DoubleMatrix a, DoubleMatrix w);

  /**
   * computes selected eigenvalues and, optionally, eigenvectors
   * of a real symmetric matrix A.  Eigenvalues and eigenvectors can be
   * selected by specifying either a range of values or a range of
   * indices for the desired eigenvalues.
   *
   * @param jobz   'N':  Compute eigenvalues only; 'V':  Compute eigenvalues and eigenvectors.
   * @param range  'A': all eigenvalues will be found. 'V': all eigenvalues in the half-open
   *               interval (VL,VU] will be found. 'I': the IL-th through IU-th eigenvalues will be
   *               found.
   * @param uplo   U':  Upper triangle of A is stored; 'L':  Lower triangle of A is stored.
   * @param a      On entry, the symmetric matrix A.  If UPLO = 'U', the
   *               leading N-by-N upper triangular part of A contains the
   *               upper triangular part of the matrix A.  If UPLO = 'L',
   *               the leading N-by-N lower triangular part of A contains
   *               the lower triangular part of the matrix A.
   *               On exit, the lower triangle (if UPLO='L') or the upper
   *               triangle (if UPLO='U') of A, including the diagonal, is
   *               destroyed.
   * @param vl     the lower and upper bounds of the interval to
   *               be searched for eigenvalues. VL < VU.
   * @param vu     the lower and upper bounds of the interval to
   *               be searched for eigenvalues. VL < VU.
   * @param il     the indices (in ascending order) of the
   *               smallest and largest eigenvalues to be returned.
   *               1 <= IL <= IU <= N, if N > 0; IL = 1 and IU = 0 if N = 0.
   *               Not referenced if RANGE = 'A' or 'V'.
   * @param iu     {@see il}
   * @param abstol The absolute error tolerance for the eigenvalues.
   *               An approximate eigenvalue is accepted as converged
   *               when it is determined to lie in an interval [a,b]
   *               of width less than or equal to
   * @param w      The first M elements contain the selected eigenvalues in
   *               ascending order
   * @param z      If JOBZ = 'V', then if INFO = 0, the first M columns of Z
   *               contain the orthonormal eigenvectors of the matrix A
   *               corresponding to the selected eigenvalues, with the i-th
   *               column of Z holding the eigenvector associated with W(i).
   *               If JOBZ = 'N', then Z is not referenced.
   *               Note: the user must ensure that at least max(1,M) columns are
   *               supplied in the array Z; if RANGE = 'V', the exact value of M
   *               is not known in advance and an upper bound must be used.
   *               Supplying N columns is always safe.
   * @param isuppz The support of the eigenvectors in Z, i.e., the indices
   *               indicating the nonzero elements in Z. The i-th eigenvector
   *               is nonzero only in elements ISUPPZ( 2*i-1 ) through
   *               ISUPPZ( 2*i )
   */
  int syevr(char jobz, char range, char uplo,
            DoubleMatrix a,
            double vl,
            double vu,
            int il,
            int iu,
            double abstol,
            DoubleMatrix w,
            DoubleMatrix z,
            IntMatrix isuppz);

  /**
   * GETRF computes an LU factorization of a general M-by-N matrix A
   * using partial pivoting with row interchanges.
   *
   * @param a    A double matrix, dimension (LDA,N)
   *             On entry, the M-by-N matrix to be factored.
   *             On exit, the factors L and U from the factorization
   *             A = P*L*U; the unit diagonal elements of L are not stored.
   * @param ipiv An int matrix, dimension (min(M,N))
   *             The pivot indices; for 1 <= i <= min(M,N), row i of the
   *             matrix was interchanged with row IPIV(i).
   * @return 0 if factorization completed correctly and > 0 if some values are zero.
   */
  int getrf(DoubleMatrix a, IntMatrix ipiv);

  /**
   * GELSY computes the minimum-norm solution to a  real  linear
   * least squares problem: minimize || A * X - B || using a complete orthogonal factorization of
   * A. A is an  M- by-N matrix which may be rank-deficient.
   *
   * @param a     On entry, the M-by-N matrix A.   On  exit,  A  has
   *              been   overwritten  by  details  of  its  complete
   *              orthogonal factorization.
   * @param b     On entry, the M-by-NRHS right hand side matrix  B.
   *              On exit, the N-by-NRHS solution matrix X.
   * @param jpvt  On entry, if JPVT(i) .ne. 0, the i-th column of  A
   *              is permuted to the front of AP, otherwise column i
   *              is a free column.  On exit, if JPVT(i) =  k,  then
   *              the i-th column of AP was the k-th column of A.
   * @param rcond RCOND is used to determine the effective  rank  of
   *              A,  which  is  defined as the order of the largest
   *              leading triangular submatrix R11 in the QR factor-
   *              ization with pivoting of A, whose estimated condi-
   *              tion number < 1/RCOND.
   * @return The effective rank of A, i.e., the  order  of  the
   * submatrix  R11.   This is the same as the order of
   * the submatrix T11 in the complete orthogonal  fac-
   * torization of A.
   */
  int gelsy(DoubleMatrix a, DoubleMatrix b, IntMatrix jpvt, double rcond);

  /**
   * DGESVD computes the singular value decomposition (SVD) of a
   * real M-by-N matrix A, optionally computing the left and/or
   * right singular vectors. The SVD is written
   *
   * A = U * SIGMA * transpose(V)
   *
   * where SIGMA is an M-by-N matrix which is zero except for its
   * min(m,n) diagonal elements, U is an M-by-M orthogonal
   * matrix, and V is an N-by-N orthogonal matrix.  The diagonal
   * elements of SIGMA are the singular values of A; they are
   * real and non-negative, and are returned in descending order.
   * The first min(m,n) columns of U and V are the left and right
   * singular vectors of A.
   *
   * Note that the routine returns V**T, not V.
   *
   * @param jobu  Specifies options for computing all or part of the
   *              matrix U:
   *              = 'A':  all M columns of U are returned in array U:
   *              = 'S':  the first min(m,n) columns of U (the left
   *              singular vectors) are returned in the array U; =
   *              'O':  the first min(m,n) columns of U (the left
   *              singular vectors) are overwritten on the array A; =
   *              'N':  no columns of U (no left singular vectors) are
   *              computed.
   * @param jobvt Specifies options for computing all or part of the
   *              matrix V**T:
   *              = 'A':  all N rows of V**T are returned in the array
   *              VT;
   *              = 'S':  the first min(m,n) rows of V**T (the right
   *
   *              singular vectors) are returned in the array VT; =
   *              'O':  the first min(m,n) rows of V**T (the right
   *              singular vectors) are overwritten on the array A; =
   *              'N':  no rows of V**T (no right singular vectors)
   *              are computed.
   * @param a     (input/output) On entry, the M-by-N matrix A.  On exit, if JOBU =
   *              'O',  A is overwritten with the first min(m,n)
   *              columns of U (the left singular vectors, stored
   *              columnwise); if JOBVT = 'O', A is overwritten with
   *              the first min(m,n) rows of V**T (the right singular
   *              vectors, stored rowwise); if JOBU .ne. 'O' and JOBVT
   *              .ne. 'O', the contents of A are destroyed.
   * @param s     (output) matrix, dimension (min(M,N))
   *              The singular values of A, sorted so that S(i) >=
   *              S(i+1).
   * @param u     (output) matrix, dimension (LDU,UCOL)
   *              (LDU,M) if JOBU = 'A' or (LDU,min(M,N)) if JOBU =
   *              'S'.  If JOBU = 'A', U contains the M-by-M orthogo-
   *              nal matrix U; if JOBU = 'S', U contains the first
   *              min(m,n) columns of U (the left singular vectors,
   *              stored columnwise); if JOBU = 'N' or 'O', U is not
   *              referenced.
   */
  public void gesvd(char jobu,
                    char jobvt,
                    DoubleMatrix a,
                    DoubleMatrix s,
                    DoubleMatrix u,
                    DoubleMatrix vt);

  MatrixFactory getMatrixFactory();
}
