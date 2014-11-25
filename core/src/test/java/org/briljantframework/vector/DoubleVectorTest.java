package org.briljantframework.vector;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class DoubleVectorTest {

    public static final double[] DOUBLE_ARRAY = new double[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
    private DoubleVector vector;
    private DoubleVector hasNA = new DoubleVector.Builder().addNA().addNA().add(1).add(2).create();


    @Before
    public void setUp() throws Exception {
        DoubleVector.Builder builder = new DoubleVector.Builder();
        for (int i = 0; i < 10; i++) {
            builder.add(i);
        }
        vector = builder.create();
    }

    @Test
    public void testAddAtIndex() throws Exception {
        DoubleVector.Builder builder = new DoubleVector.Builder();
        builder.set(3, 10);
        builder.set(10, 10);
        System.out.println(builder.size());

        System.out.println(builder.create());
    }

    @Test
    public void testAsIntArray() throws Exception {
        assertArrayEquals(DOUBLE_ARRAY, vector.asDoubleArray(), 0);
    }

    @Test
    public void testToIntArray() throws Exception {
        assertArrayEquals(DOUBLE_ARRAY, vector.toDoubleArray(), 0);
    }

    @Test
    public void testIterator() throws Exception {
        for (Double value : vector) {
            assertEquals(value, DOUBLE_ARRAY[value.intValue()], 0.0);
        }
    }

    @Test
    public void testGetAsInteger() throws Exception {
        assertEquals(2, vector.getAsInt(2));
        assertEquals(9, vector.getAsInt(9));
    }

    @Test
    public void testHasNA() throws Exception {
        assertEquals(false, vector.hasNA());
        assertEquals(true, hasNA.hasNA());
    }

    @Test
    public void testCompare() throws Exception {
        assertEquals(true, vector.compare(0, 2) < 0);
        assertEquals(true, vector.compare(2, 1) > 0);
        assertEquals(true, vector.compare(2, 2) == 0);
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(10, vector.size());
        assertEquals(4, hasNA.size());
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals(DoubleVector.TYPE, vector.getType());
    }

    @Test
    public void testNewCopyBuilder() throws Exception {
        DoubleVector copy = vector.newCopyBuilder().add(10).create();
        assertEquals(11, copy.size());
        assertEquals(copy.getAsInt(2), vector.getAsInt(2));
    }

    @Test
    public void testGetAsDouble() throws Exception {
        assertEquals(2.0, vector.getAsDouble(2), 0);
    }

    @Test
    public void testGetAsBinary() throws Exception {
        assertEquals(Binary.TRUE, vector.getAsBinary(1));
        assertEquals(Binary.FALSE, vector.getAsBinary(0));
        assertEquals(Binary.NA, hasNA.getAsBinary(0));
    }

    @Test
    public void testGetAsString() throws Exception {
        assertEquals("9.0", vector.getAsString(9));
    }

    @Test
    public void testIsTrue() throws Exception {
        assertEquals(true, vector.isTrue(1));
        assertEquals(false, vector.isTrue(0));
    }

    @Test
    public void testIsNA() throws Exception {
        assertEquals(true, hasNA.isNA(0));
        assertEquals(false, hasNA.isNA(2));
    }

    @Test
    public void testNewBuilder() throws Exception {
        DoubleVector.Builder builder = vector.newBuilder();

        builder.add(hasNA, 0);
        builder.add(vector, 0);
        builder.add(vector, 9);

        assertArrayEquals(new double[]{DoubleVector.NA, 0, 9}, builder.create().asDoubleArray(), 0);

    }

    @Test
    public void testNewBuilder1() throws Exception {

    }

}