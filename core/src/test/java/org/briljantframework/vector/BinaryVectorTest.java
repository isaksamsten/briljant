package org.briljantframework.vector;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class BinaryVectorTest {

    private BinaryVector vector;
    private int[] trueArray = new int[]{0, 1, IntVector.NA, 0};

    @Before
    public void setUp() throws Exception {
        vector = new BinaryVector.Builder().add(0).add(1).add(Binary.NA).add(Binary.FALSE).create();
    }

    @Test
    public void testIterator() throws Exception {
        int i = 0;
        for (Binary bin : vector) {
            assertEquals(trueArray[i++], bin.asInt());
        }
    }

    @Test
    public void testGetAsDouble() throws Exception {
        assertEquals(0, vector.getAsDouble(0), 0);
        assertEquals(1, vector.getAsDouble(1), 0);
        assertEquals(DoubleVector.NA, vector.getAsDouble(2), 0);
    }

    @Test
    public void testGetAsInt() throws Exception {
        assertEquals(0, vector.getAsInt(0));
        assertEquals(1, vector.getAsInt(1));
        assertEquals(IntVector.NA, vector.getAsInt(2));
    }

    @Test
    public void testGetAsBinary() throws Exception {
        assertEquals(Binary.FALSE, vector.getAsBinary(0));
        assertEquals(Binary.TRUE, vector.getAsBinary(1));
        assertEquals(BinaryVector.NA, vector.getAsBinary(2));
    }

    @Test
    public void testGetAsString() throws Exception {
        assertEquals("FALSE", vector.getAsString(0));
        assertEquals("TRUE", vector.getAsString(1));
        assertEquals(StringVector.NA, vector.getAsString(2));
    }

    @Test
    public void testIsNA() throws Exception {
        assertEquals(true, vector.isNA(2));
        assertEquals(false, vector.isNA(0));
    }

    @Test
    public void testCompare() throws Exception {
        assertEquals(true, vector.compare(0, 1) < 0);
        assertEquals(true, vector.compare(1, 0) > 0);
        assertEquals(true, vector.compare(0, 3) == 0);
    }

    @Test
    public void testSize() throws Exception {
        assertEquals(4, vector.size());
        assertEquals(5, vector.newCopyBuilder().add(1).create().size());
    }

    @Test
    public void testGetType() throws Exception {
        assertEquals(BinaryVector.TYPE, vector.getType());
        assertEquals(Binary.class, vector.getType().getDataClass());
    }

    @Test
    public void testNewCopyBuilder() throws Exception {

    }

    @Test
    public void testNewBuilder() throws Exception {

    }

    @Test
    public void testNewBuilder1() throws Exception {

    }

    @Test
    public void testToIntArray() throws Exception {
        assertArrayEquals(trueArray, vector.toIntArray());
    }

    @Test
    public void testAsIntArray() throws Exception {
        assertArrayEquals(trueArray, vector.asIntArray());
    }
}