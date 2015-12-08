/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.linalg.api;

import org.briljantframework.array.ArrayOperation;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.IntArray;
import org.briljantframework.linalg.decomposition.EigenDecomposition;
import org.briljantframework.linalg.decomposition.LuDecomposition;
import org.briljantframework.linalg.decomposition.SingularValueDecomposition;

/**
 * Linear algebra routines
 * 
 * @author Isak Karlsson
 */
public interface LinearAlgebraRoutines {

  EigenDecomposition eig(DoubleArray x);

  LuDecomposition lu(DoubleArray x);

  SingularValueDecomposition svd(DoubleArray x);

  DoubleArray inv(DoubleArray x);

  DoubleArray pinv(DoubleArray x);

  double det(DoubleArray x);

  double rank(DoubleArray x);

  /**
   * DGEEV computes for an N-by-N real nonsymmetric matrix A, the eigenvalues and, optionally, the
   * left and/or right eigenvectors.
   *
   * The right eigenvector v(j) of A satisfies A * v(j) = lambda(j) * v(j) where lambda(j) is its
   * eigenvalue. The left eigenvector u(j) of A satisfies u(j)**H * A = lambda(j) * u(j)**H where
   * u(j)**H denotes the conjugate-transpose of u(j).
   *
   * The computed eigenvectors are normalized to have Euclidean norm equal to 1 and largest
   * component real.
   *
   * @param jobvl = 'N': left eigenvectors of A are not computed; = 'V': left eigenvectors of A are
   *        computed.
   * @param jobvr = 'N': right eigenvectors of A are not computed; = 'V': right eigenvectors of A
   *        are computed.
   * @param a (input/output) On entry, the N-by-N matrix A. On exit, A has been overwritten.
   * @param wr (output) dimension n
   * @param wi (output) wr and wi contain the real and imaginary parts, respectively, of the
   *        computed eigenvalues. Complex conjugate pairs of eigenvalues appear consecutively with
   *        the eigenvalue having the positive imaginary part first.
   * @param vl If JOBVL = 'V', the left eigenvectors u(j) are stored one after another in the
   *        columns of VL, in the same order as their eigenvalues. If JOBVL = 'N', VL is not
   *        referenced. If the j-th eigenvalue is real, then u(j) = VL(:,j), the j-th column of VL.
   *        If the j-th and (j+1)-st eigenvalues form a complex conjugate pair, then u(j) = VL(:,j)
   *        + i*VL(:,j+1) and u(j+1) = VL(:,j) - i*VL(:,j+1).
   * @param vr If JOBVR = 'V', the right eigenvectors v(j) are stored one after another in the
   *        columns of VR, in the same order as their eigenvalues. If JOBVR = 'N', VR is not
   *        referenced. If the j-th eigenvalue is real, then v(j) = VR(:,j), the j-th column of VR.
   *        If the j-th and (j+1)-st eigenvalues form a complex
   */
  void geev(char jobvl, char jobvr, DoubleArray a, DoubleArray wr, DoubleArray wi, DoubleArray vl,
      DoubleArray vr);

  /**
   * DORMQR overwrites the general real M-by-N matrix C with
   * 
   * <pre>
   * SIDE = 'L'     SIDE = 'R'
   * transA = NO:      Q * C          C * Q
   * transA = YES:      Q**T * C       C * Q**T
   * </pre>
   * 
   * where Q is a real orthogonal matrix defined as the product of k elementary reflectors
   *
   * Q = H(1) H(2) . . . H(k)
   *
   * as returned by DGEQRF. Q is of order M if SIDE = 'L' and of order N if SIDE = 'R'.
   *
   * @param side 'L': apply Q or Q**T from the Left; = 'R': apply Q or Q**T from the Right.
   * @param transA transpose Q
   * @param a (input) the i-th column must contain the vector which defines the elementary reflector
   *        H(i), for i = 1,2,...,k, as returned by
   *        {@link #geqrf(org.briljantframework.array.DoubleArray, org.briljantframework.array.DoubleArray)}
   *        in the first k columns of its array argument A.
   * @param tau (input) TAU(i) must contain the scalar factor of the elementary reflector H(i), as
   *        returned by DGEQRF.
   * @param c (input/output) On entry, the M-by-N matrix C. On exit, C is overwritten by Q*C or
   *        Q**T*C or C*Q**T or C*Q.
   */
  void ormqr(char side, ArrayOperation transA, DoubleArray a, DoubleArray tau, DoubleArray c);

  /**
   * Computes a QR factorization of a real M-by-N matrix A: A = Q * R.
   *
   * @param a (input/output) On entry, the M-by-N matrix A. On exit, the elements on and above the
   *        diagonal of the array contain the min(M,N)-by-N upper trapezoidal matrix R (R is upper
   *        triangular if m >= n); the elements below the diagonal, with the array TAU, represent
   *        the orthogonal matrix Q as a product of min(m,n) elementary reflectors.
   * @param tau (output) The scalar factors of the elementary reflectors
   */
  void geqrf(DoubleArray a, DoubleArray tau);

  /**
   * Computes the eigenvalues and, optionally, the left and/or right eigenvectors for symmetric
   * matrices
   *
   * @param jobz 'N': Compute eigenvalues only; 'V': Compute eigenvalues and eigenvectors.
   * @param uplo 'U': Upper triangle of A is stored; 'L': Lower triangle of A is stored.
   * @param a (input/output) On entry, the symmetric matrix A. If UPLO = 'U', the leading N-by-N
   *        upper triangular part of A contains the upper triangular part of the matrix A. If UPLO =
   *        'L', the leading N-by-N lower triangular part of A contains the lower triangular part of
   *        the matrix A. On exit, if JOBZ = 'V', then if INFO = 0, A contains the orthonormal
   *        eigenvectors of the matrix A. If JOBZ = 'N', then on exit the lower triangle (if
   *        UPLO='L') or the upper triangle (if UPLO='U') of A, including the diagonal, is
   *        destroyed.
   * @param w (output) the eigenvalues in ascending order
   */
  void syev(char jobz, char uplo, DoubleArray a, DoubleArray w);

  /**
   * Computes selected eigenvalues and, optionally, eigenvectors of a real symmetric matrix A.
   * Eigenvalues and eigenvectors can be selected by specifying either a range of values or a range
   * of indices for the desired eigenvalues.
   *
   * @param jobz 'N': Compute eigenvalues only; 'V': Compute eigenvalues and eigenvectors.
   * @param range 'A': all eigenvalues will be found. 'V': all eigenvalues in the half-open interval
   *        (VL,VU] will be found. 'I': the IL-th through IU-th eigenvalues will be found.
   * @param uplo U': Upper triangle of A is stored; 'L': Lower triangle of A is stored.
   * @param a (input/output) On entry, the symmetric matrix A. If UPLO = 'U', the leading N-by-N
   *        upper triangular part of A contains the upper triangular part of the matrix A. If UPLO =
   *        'L', the leading N-by-N lower triangular part of A contains the lower triangular part of
   *        the matrix A. On exit, the lower triangle (if UPLO='L') or the upper triangle (if
   *        UPLO='U') of A, including the diagonal, is destroyed.
   * @param vl the lower and upper bounds of the interval to be searched for eigenvalues. VL < VU.
   * @param vu the lower and upper bounds of the interval to be searched for eigenvalues. VL < VU.
   * @param il the indices (in ascending order) of the smallest and largest eigenvalues to be
   *        returned. 1 <= IL <= IU <= N, if N > 0; IL = 1 and IU = 0 if N = 0. Not referenced if
   *        RANGE = 'A' or 'V'.
   * @param iu {@see il}
   * @param abstol The absolute error tolerance for the eigenvalues. An approximate eigenvalue is
   *        accepted as converged when it is determined to lie in an interval [a,b] of width less
   *        than or equal to
   * @param w (output) The first M elements contain the selected eigenvalues in ascending order
   * @param z (output) If JOBZ = 'V', then if INFO = 0, the first M columns of Z contain the
   *        orthonormal eigenvectors of the matrix A corresponding to the selected eigenvalues, with
   *        the i-th column of Z holding the eigenvector associated with W(i). If JOBZ = 'N', then Z
   *        is not referenced. Note: the user must ensure that at least max(1,M) columns are
   *        supplied in the array Z; if RANGE = 'V', the exact value of M is not known in advance
   *        and an upper bound must be used. Supplying N columns is always safe.
   * @param isuppz (output) The support of the eigenvectors in Z, i.e., the indices indicating the
   *        nonzero elements in Z. The i-th eigenvector is nonzero only in elements ISUPPZ( 2*i-1 )
   *        through ISUPPZ( 2*i )
   */
  int syevr(char jobz, char range, char uplo, DoubleArray a, double vl, double vu, int il, int iu,
      double abstol, DoubleArray w, DoubleArray z, IntArray isuppz);

  /**
   * Computes an LU factorization of a general M-by-N matrix A using partial pivoting with row
   * interchanges.
   *
   * @param a (input/output) A double matrix, dimension (LDA,N) On entry, the M-by-N matrix to be
   *        factored. On exit, the factors L and U from the factorization A = P*L*U; the unit
   *        diagonal elements of L are not stored.
   * @param ipiv (output) An int matrix, dimension (min(M,N)) The pivot indices; for 1 <= i <=
   *        min(M,N), row i of the matrix was interchanged with row IPIV(i).
   * @return 0 if factorization completed correctly and > 0 if some values are zero.
   */
  int getrf(DoubleArray a, IntArray ipiv);

  int getri(DoubleArray a, IntArray ipiv);

  /**
   * Computes the minimum-norm solution to a real linear least squares problem: minimize || A * X -
   * B || using a complete orthogonal factorization of A. A is an M- by-N matrix which may be
   * rank-deficient.
   *
   * @param a (input/output) On entry, the M-by-N matrix A. On exit, A has been overwritten by
   *        details of its complete orthogonal factorization.
   * @param b (input/output) On entry, the M-by-NRHS right hand side matrix B. On exit, the
   *        N-by-NRHS solution matrix X.
   * @param jpvt (input/output) On entry, if JPVT(i) .ne. 0, the i-th column of A is permuted to the
   *        front of AP, otherwise column i is a free column. On exit, if JPVT(i) = k, then the i-th
   *        column of AP was the k-th column of A.
   * @param rcond RCOND is used to determine the effective rank of A, which is defined as the order
   *        of the largest leading triangular submatrix R11 in the QR factor- ization with pivoting
   *        of A, whose estimated condi- tion number < 1/RCOND.
   * @return The effective rank of A, i.e., the order of the submatrix R11. This is the same as the
   *         order of the submatrix T11 in the complete orthogonal fac- torization of A.
   */
  int gelsy(DoubleArray a, DoubleArray b, IntArray jpvt, double rcond);

  /**
   * DGESV computes the solution to a real system of linear equations A * X = B, where A is an
   * N-by-N matrix and X and B are N-by-NRHS matrices.
   *
   * The LU decomposition with partial pivoting and row interchanges is used to factor A as A = P *
   * L * U, where P is a permutation matrix, L is unit lower triangular, and U is upper triangular.
   * The factored form of A is then used to solve the system of equations A * X = B.
   *
   * @param a On entry, the N-by-N coefficient matrix A. On exit, the factors L and U from the
   *        factorization A = P*L*U; the unit diagonal elements of L are not stored.
   * @param ipiv The pivot indices that define the permutation matrix P; row i of the matrix was
   *        interchanged with row IPIV(i).
   * @param b On entry, the N-by-NRHS matrix of right hand side matrix B.
   * @return i, U(i,i) is exactly zero. The factorization has been completed, but the factor U is
   *         exactly singular, so the solution could not be computed.
   */
  int gesv(DoubleArray a, IntArray ipiv, DoubleArray b);

  /**
   * Computes the singular value decomposition (SVD) of a real M-by-N matrix A, optionally computing
   * the left and/or right singular vectors. The SVD is written {@code A = U * SIGMA *
   * transpose(V)},
   *
   *
   * <p>
   * where SIGMA is an M-by-N matrix which is zero except for its min(m,n) diagonal elements, U is
   * an M-by-M orthogonal matrix, and V is an N-by-N orthogonal matrix. The diagonal elements of
   * SIGMA are the singular values of A; they are real and non-negative, and are returned in
   * descending order. The first min(m,n) columns of U and V are the left and right singular vectors
   * of A.
   *
   * Note that the routine returns V**T, not V.
   *
   * @param jobu Specifies options for computing all or part of the matrix U: = 'A': all M columns
   *        of U are returned in array U: = 'S': the first min(m,n) columns of U (the left singular
   *        vectors) are returned in the array U; = 'O': the first min(m,n) columns of U (the left
   *        singular vectors) are overwritten on the array A; = 'N': no columns of U (no left
   *        singular vectors) are computed.
   * @param jobvt Specifies options for computing all or part of the matrix V**T: = 'A': all N rows
   *        of V**T are returned in the array VT; = 'S': the first min(m,n) rows of V**T (the right
   *
   *        singular vectors) are returned in the array VT; = 'O': the first min(m,n) rows of V**T
   *        (the right singular vectors) are overwritten on the array A; = 'N': no rows of V**T (no
   *        right singular vectors) are computed.
   * @param a (input/output) On entry, the M-by-N matrix A. On exit, if JOBU = 'O', A is overwritten
   *        with the first min(m,n) columns of U (the left singular vectors, stored columnwise); if
   *        JOBVT = 'O', A is overwritten with the first min(m,n) rows of V**T (the right singular
   *        vectors, stored rowwise); if JOBU .ne. 'O' and JOBVT .ne. 'O', the contents of A are
   *        destroyed.
   * @param s (output) matrix, dimension (min(M,N)) The singular values of A, sorted so that S(i) >=
   *        S(i+1).
   * @param u (output) matrix, dimension (LDU,UCOL) (LDU,M) if JOBU = 'A' or (LDU,min(M,N)) if JOBU
   *        = 'S'. If JOBU = 'A', U contains the M-by-M orthogo- nal matrix U; if JOBU = 'S', U
   *        contains the first min(m,n) columns of U (the left singular vectors, stored columnwise);
   *        if JOBU = 'N' or 'O', U is not referenced.
   */
  void gesvd(char jobu, char jobvt, DoubleArray a, DoubleArray s, DoubleArray u, DoubleArray vt);

  /**
   * @param jobz Specifies options for computing all or part of the matrix U: = 'A': all M columns
   *        of U and all N rows of V**T are returned in the arrays U and VT; = 'S': the first
   *        min(M,N) columns of U and the first min(M,N) rows of V**T are returned in the arrays U
   *        and VT; = 'O': If M >= N, the first N columns of U are overwritten on the array A and
   *        all rows of V**T are returned in the array VT; otherwise, all columns of U are returned
   *        in the array U and the first M rows of V**T are overwritten in the array A; = 'N': no
   *        columns of U or rows of V**T are computed.
   * @param a (input/output) On entry, the M-by-N matrix A. On exit, if JOBZ = 'O', A is overwritten
   *        with the first N columns of U (the left singular vectors, stored columnwise) if M >= N;
   *        A is overwritten with the first M rows of V**T (the right singular vectors, stored
   *        rowwise) otherwise. if JOBZ .ne. 'O', the contents of A are destroyed.
   * @param s The singular values of A, sorted so that S(i) >= S(i+1).
   * @param u UCOL = M if JOBZ = 'A' or JOBZ = 'O' and M < N; UCOL = min(M,N) if JOBZ = 'S'. If JOBZ
   *        = 'A' or JOBZ = 'O' and M < N, U contains the M-by-M orthogonal matrix U; if JOBZ = 'S',
   *        U contains the first min(M,N) columns of U (the left singular vectors, stored
   *        columnwise); if JOBZ = 'O' and M >= N, or JOBZ = 'N', U is not referenced.
   * @param vt If JOBZ = 'A' or JOBZ = 'O' and M >= N, VT contains the N-by-N orthogonal matrix
   *        V**T; if JOBZ = 'S', VT contains the first min(M,N) rows of V**T (the right singular
   *        vectors, stored rowwise); if JOBZ = 'O' and M < N, or JOBZ = 'N', VT is not referenced
   */
  void gesdd(char jobz, DoubleArray a, DoubleArray s, DoubleArray u, DoubleArray vt);
}
