package org.briljantframework.matrix.math;

import org.briljantframework.matrix.Scalars;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ScalarsTest {

    @Test
    public void testMin() throws Exception {
        assertEquals(2.0, Scalars.min(2.0, 3.0), 0.0001);
        assertEquals(2.0, Scalars.min(3.0, 2.0), 0.0001);
    }

    @Test
    public void testMin1() throws Exception {
        assertEquals(2.0, Scalars.min(2.0, 3.0, 4.0), 0.0001);
        assertEquals(2.0, Scalars.min(4.0, 3.0, 2.0), 0.0001);
        assertEquals(2.0, Scalars.min(3.0, 2.0, 4.0), 0.0001);
    }

    @Test
    public void testMin2() throws Exception {
        assertEquals(2.0, Scalars.min(2.0, 3.0, 4.0, 10, 20, 2.000001, 2.01), 0.0001);
    }
}