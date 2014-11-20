package org.briljantframework.matrix.math;

import org.briljantframework.matrix.*;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

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

    @Test
    public void testRandn() throws Exception {
        SparseMatrix matrix = Matrices.rand(SparseMatrix::new, 2, 2);
        assertEquals(Shape.of(2, 2), matrix.getShape());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRandVectorIllegalSize() throws Exception {
//        randn(DenseVector::new, 2, 2);
    }

    @Test
    public void testReshape() throws Exception {
        assertArrayEquals(
                DenseMatrix.of(2, 2, 1, 3, 2, 4).toArray(),
                Matrices.reshape(DenseMatrix::new, DenseMatrix.of(1, 4, 1, 2, 3, 4), 2, 2).toArray(), 0.00001
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
        assertArrayEquals(result.toArray(), Matrices.mmul(DenseMatrix::new, a, b).toArray(), 0.0001);
    }

    @Test
    public void testAxb() throws Exception {
        Matrix a = Matrices.parseMatrix(DenseMatrix::new, "1,2,3,4;1,2,3,4;1,2,3,4;1,2,3,4;1,2,3,4");
        System.out.println(a);

    }
}