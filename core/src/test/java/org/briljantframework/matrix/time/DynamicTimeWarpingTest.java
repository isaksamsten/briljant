package org.briljantframework.matrix.time;

import org.briljantframework.matrix.DenseMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.distance.Distance;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DynamicTimeWarpingTest {

    @Test
    public void testDistance() throws Exception {
        Matrix a = DenseMatrix.of(1, 16, 0, 0, 0, 0, 1, 1, 2, 2, 3, 2, 1, 1, 0, 0, 0, 0);
        Matrix b = DenseMatrix.of(1, 16, 0, 1, 1, 1, 2, 2, 3, 3, 3, 3, 2, 2, 1, 1, 0, 0);

        Distance dwt = DynamicTimeWarping.unconstraint(Distance.MANHATTAN);
        assertEquals(3.0, dwt.distance(a, b), 0);

        dwt = DynamicTimeWarping.withDistance(Distance.MANHATTAN).withConstraint(10).create();
        assertEquals(3.0, dwt.distance(a, b), 0);

        assertEquals(12.0, Distance.MANHATTAN.distance(a, b), 0);
        assertEquals(true, dwt.distance(a, b) < Distance.MANHATTAN.distance(a, b));
    }
}