package org.briljantframework.matrix.transformation;

import org.briljantframework.matrix.DenseMatrix;
import org.briljantframework.transform.InverseTransformation;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class InverseTransformerTest {

    DenseMatrix matrix;

    @Before
    public void setUp() throws Exception {
        matrix = DenseMatrix.of(4, 4,
                0, 2, 0, 1,
                2, 2, 3, 2,
                4, -3, 0, 1.,
                6, 1, -6, -5
        );
    }

    @Test
    public void testTransform() throws Exception {
        InverseTransformation transformer = new InverseTransformation();
        DenseMatrix inverse = transformer.transform(matrix);
        assertEquals(-0.02564102564102574, inverse.get(0, 0), 0.01);
    }
}