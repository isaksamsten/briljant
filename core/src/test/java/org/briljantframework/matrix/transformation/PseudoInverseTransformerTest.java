package org.briljantframework.matrix.transformation;

import org.briljantframework.matrix.*;
import org.briljantframework.matrix.decomposition.SingularValueDecomposition;
import org.briljantframework.matrix.math.LinearAlgebra;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class PseudoInverseTransformerTest {

    private DenseMatrix matrix;

    @Before
    public void setUp() throws Exception {
        matrix = DenseMatrix.of(4, 4,
                0, 2, 0, 1,
                2, 2, 3, 2,
                4, -3, 0, 1.,
                6, 1, -6, -5
                //                0, 1, 1, 1
        );

    }

    @Test
    public void testTransform() throws Exception {
        Matrix inverse = LinearAlgebra.pinv(matrix);

        double[] expected = {
                -0.0256410256410256, 0.1794871794871795, -0.5299145299145301, 0.6410256410256413,
                0.12820512820512825, 0.10256410256410253, 0.3162393162393162, -0.20512820512820507,
                0.08974358974358979, -0.12820512820512814, -0.1452991452991454, 0.25641025641025655,
                0.06410256410256417, 0.051282051282051266, -0.0085470085470086, -0.10256410256410259
        };

        assertArrayEquals(expected, inverse.toArray(), 0.001);

        Matrix a = Matrices.parseMatrix(DenseMatrix::new, "1,2,3; 1,2,3; 1,2,3; 1,2,3");
        Diagonal x = Diagonal.of(3, 3, 1, 1, 1);

        //        System.out.println(matrix);
        //
        //        System.out.println(Matrices.multiplyByDiagonal(a, x, DenseMatrix::new));

        Matrix A = DenseMatrix.withSize(4, 2).withValues(1, 2, 3, 4, 5, 6, 7, 8);
        System.out.println(A);

        SingularValueDecomposition svd = LinearAlgebra.svd(A);
        System.out.println(svd.u);
        System.out.println(svd.s);
        System.out.println(svd.v);


        svd.s.apply(y -> 1.0 / y);
        //        System.out.println(svd.s.transpose());


        //        System.out.println("PINV");
        //        System.out.println(svd.v.multiply(svd.s.transpose()).multiply(svd.u.transpose()));

        System.out.println(svd.v.mmul(svd.s.transpose()));

        System.out.println("Multiply by diagonal: ");
        System.out.println(svd.v.mmuld(svd.s.transpose()));

        System.out.println("Working by diagnoal");
        System.out.println(svd.v.mmul(svd.s.transpose()));

        DenseMatrix mmul = Matrices.mmul(DenseMatrix::new, svd.v.mmuld(svd.s.transpose()), Transpose.NO, svd.u, Transpose.YES);
        System.out.println(mmul);


        DenseMatrix large = Matrices.randn(DenseMatrix::new, 1000, 100);

        System.out.println(LinearAlgebra.pinv(A));
    }
}