package org.briljantframework;

import org.junit.Test;

import static org.junit.Assert.*;

public class RangeTest {

    @Test
    public void testRange() throws Exception {
        Range r = Range.range(10);
        for (int i = 0; i < r.size(); i++) {
            assertEquals(i, r.get(i));
        }
    }
}