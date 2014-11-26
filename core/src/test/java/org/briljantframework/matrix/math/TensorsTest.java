package org.briljantframework.matrix.math;

import org.briljantframework.matrix.DenseMatrix;
import org.briljantframework.matrix.Matrices;
import org.briljantframework.matrix.Matrix;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;

public class TensorsTest {

    @Test
    public void testIndexSort() throws Exception {
        Matrix test = Matrices.randn(DenseMatrix::new, 1, 10);


        System.out.println(test);
        int[] order = Matrices.sortIndex(test, (a, b) ->
                        Double.compare(Math.abs(test.get(b)), Math.abs(test.get(a)))
        );
        for (int i : order) {
            System.out.println(test.get(i));
        }

    }

    @Test
    public void testPow() throws Exception {
//        DenseVector vector = DenseVector.of(2, 3, 3, 3);
//        DenseVector pow = pow(DenseVector::new, vector, 2);
//        assertArrayEquals(pow.array(), new double[]{4, 9, 9, 9}, 0.0001);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testRandVectorIllegalSize() throws Exception {
//        randn(DenseVector::new, 2, 2);
    }

    @Test
    public void testReshape() throws Exception {
        assertArrayEquals(
                DenseMatrix.of(2, 2, 1, 3, 2, 4).asDoubleArray(),
                Matrices.reshape(DenseMatrix::new, DenseMatrix.of(1, 4, 1, 2, 3, 4), 2, 2).asDoubleArray(), 0.00001
        );
//        assertEquals(Shape.of(1, 4), reshape(DenseVector::new, DenseVector.of(1, 2, 3, 4), 1, 4).getShape());
    }

    @Test
    public void testAxB() throws Exception {
        Matrix a = DenseMatrix.of(2, 3,
                1, 2, 3,
                1, 2, 3
        );
        Matrix b = DenseMatrix.of(3, 2,
                2, 2,
                1, 1,
                3, 3
        );
        Matrix result = DenseMatrix.of(2, 2, 13, 13, 13, 13);
        assertArrayEquals(result.asDoubleArray(), Matrices.mmul(DenseMatrix::new, a, b).asDoubleArray(), 0.0001);
    }

    @Test
    public void testAxb() throws Exception {
        Matrix a = Matrices.parseMatrix(DenseMatrix::new, "1,2,3,4;1,2,3,4;1,2,3,4;1,2,3,4;1,2,3,4");
        System.out.println(a);

    }
}