package org.briljantframework.matrix;

import org.junit.Test;

import static org.briljantframework.matrix.Matrices.parseMatrix;
import static org.junit.Assert.assertArrayEquals;

public class DenseMatrixTest {

    @Test
    public void testDropRow() throws Exception {
        Matrix a = parseMatrix(DenseMatrix::new, "1,1,1;2,2,2");
        System.out.println(a.dropRow(0));

    }

    @Test
    public void testMmul() throws Exception {
        Matrix a = parseMatrix("1,2,3;1,2,3");
        Matrix b = parseMatrix("1,2;1,2;1,2");

        Matrix AxB = parseMatrix("6,12;6,12");
        Matrix BxA = parseMatrix("3,6,9;3,6,9;3,6,9");
        assertArrayEquals(AxB.toArray(), a.mmul(b).toArray(), 0.00001);
        assertArrayEquals(BxA.toArray(), b.mmul(a).toArray(), 0.00001);
    }

    @Test
    public void testMul() throws Exception {
        Matrix a = parseMatrix("1,2,3;1,2,3");
        Matrix b = parseMatrix("1,2,3;1,2,3");
        assertArrayEquals(parseMatrix("1,4,9;1,4,9").toArray(), a.mul(b).toArray(), 0.0001);
        assertArrayEquals(parseMatrix("1,4,9;1,4,9").toArray(), a.muli(b).toArray(), 0.0001);

        assertArrayEquals(parseMatrix("2,4,6;2,4,6").toArray(), b.mul(2).toArray(), 0.0001);
        assertArrayEquals(parseMatrix("2,4,6;2,4,6").toArray(), b.muli(2).toArray(), 0.0001);
    }

    @Test
    public void testSub() throws Exception {
        Matrix a = parseMatrix("1,2,3;1,2,3");
        Matrix b = parseMatrix("10,10,10;2,2,2");

        assertArrayEquals(parseMatrix("-9,-8,-7;-1,0,1").toArray(), a.sub(b).toArray(), 0.0001);
        assertArrayEquals(a.sub(b).toArray(), b.rsub(a).toArray(), 0.00001);

        assertArrayEquals(parseMatrix("-9,-8,-7;-9,-8,-7").toArray(), a.sub(10).toArray(), 0.0001);
        assertArrayEquals(parseMatrix("9,8,7;9,8,7").toArray(), a.rsub(10).toArray(), 0.00001);

        assertArrayEquals(parseMatrix("-9,-8,-7;-9,-8,-7").toArray(), a.subi(10).toArray(), 0.001);

        a = parseMatrix("1,2,3;1,2,3");
        assertArrayEquals(parseMatrix("9,8,7;9,8,7").toArray(), a.rsubi(10).toArray(), 0.00001);
    }

    @Test
    public void testAdd() throws Exception {
        Matrix a = parseMatrix("1,2,3;1,2,3");
        Matrix b = parseMatrix("10,10,10;2,2,2");

        assertArrayEquals(parseMatrix("11,12,13;3,4,5").toArray(), a.add(b).toArray(), 0.0001);
        assertArrayEquals(parseMatrix("2,3,4;2,3,4").toArray(), a.add(1).toArray(), 0.0001);
        assertArrayEquals(parseMatrix("11,12,13;3,4,5").toArray(), a.addi(b).toArray(), 0.0001);

        a = parseMatrix("1,2,3;1,2,3");
        assertArrayEquals(parseMatrix("2,3,4;2,3,4").toArray(), a.addi(1).toArray(), 0.0001);

    }

    @Test
    public void testDiv() throws Exception {
        Matrix a = parseMatrix("1,2,3;2,3,4");
        Matrix b = parseMatrix("10,2,3;43,2,1");

        assertArrayEquals(parseMatrix("0.1,1,1;0.0465,1.5,4").toArray(), a.div(b).toArray(), 0.0001);
        assertArrayEquals(parseMatrix("10,1,1;21.5,0.6667,0.25").toArray(), a.rdiv(b).toArray(), 0.0001);

        assertArrayEquals(parseMatrix("0.5,1,1.5;1,1.5,2").toArray(), a.div(2).toArray(), 0.0001);
        assertArrayEquals(parseMatrix("2,1,0.6667;1,0.6667,0.5").toArray(), a.rdiv(2).toArray(), 0.0001);
    }

    @Test
    public void testTranspose() throws Exception {
        Matrix a = parseMatrix("1,2,3;1,2,3");
        Matrix result = parseMatrix("1,1;2,2;3,3");
        assertArrayEquals(result.toArray(), a.transpose().toArray(), 0.0000001);
    }
}