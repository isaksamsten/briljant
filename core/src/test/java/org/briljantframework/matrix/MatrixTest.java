package org.briljantframework.matrix;

import static org.briljantframework.matrix.Matrices.parseMatrix;

import org.briljantframework.DoubleArray;
import org.junit.Test;

public class MatrixTest {

  @Test
  public void testDropRow() throws Exception {
    Matrix a = parseMatrix("1,1,1;2,2,2");

  }

  @Test
  public void testMmul() throws Exception {
    Matrix a = parseMatrix("1,2,3;1,2,3");
    Matrix b = parseMatrix("1,2;1,2;1,2");

    Matrix AxB = parseMatrix("6,12;6,12");
    Matrix BxA = parseMatrix("3,6,9;3,6,9;3,6,9");
    // assertArrayEquals(AxB.asDoubleArray(), a.mmul(b).asDoubleArray(), 0.00001);
    // assertArrayEquals(BxA.asDoubleArray(), b.mmul(a).asDoubleArray(), 0.00001);
  }

  @Test
  public void testMul() throws Exception {
    Matrix a = parseMatrix("1,2,3;1,2,3");
    Matrix b = parseMatrix("1,2,3;1,2,3");
    // assertArrayEquals(parseMatrix("1,4,9;1,4,9").asDoubleArray(), a.mul(b).asDoubleArray(),
    // 0.0001);
    // assertArrayEquals(parseMatrix("1,4,9;1,4,9").asDoubleArray(), a.muli(b).asDoubleArray(),
    // 0.0001);
    //
    // assertArrayEquals(parseMatrix("2,4,6;2,4,6").asDoubleArray(), b.mul(2).asDoubleArray(),
    // 0.0001);
    // assertArrayEquals(parseMatrix("2,4,6;2,4,6").asDoubleArray(), b.muli(2).asDoubleArray(),
    // 0.0001);
  }

  @Test
  public void testSub() throws Exception {
    Matrix a = parseMatrix("1,2,3;1,2,3");
    Matrix b = parseMatrix("10,10,10;2,2,2");

    // assertArrayEquals(parseMatrix("-9,-8,-7;-1,0,1").asDoubleArray(), a.sub(b).asDoubleArray(),
    // 0.0001);
    // assertArrayEquals(parseMatrix("-9,-8,-7;-9,-8,-7").asDoubleArray(),
    // a.sub(10).asDoubleArray(),
    // 0.0001);
    // assertArrayEquals(parseMatrix("9,8,7;9,8,7").asDoubleArray(), a.rsub(10).asDoubleArray(),
    // 0.00001);
    //
    // assertArrayEquals(parseMatrix("-9,-8,-7;-9,-8,-7").asDoubleArray(),
    // a.subi(10).asDoubleArray(),
    // 0.001);

    a = parseMatrix("1,2,3;1,2,3");
    DoubleArray c = DoubleArray.wrap(2, 4, 3);

    System.out.println(a.div(c, Axis.ROW).mul(2).sub(c, Axis.ROW));

    // assertArrayEquals(parseMatrix("9,8,7;9,8,7").asDoubleArray(), a.rsubi(10).asDoubleArray(),
    // 0.00001);
  }

  @Test
  public void testAdd() throws Exception {
    Matrix a = parseMatrix("1,2,3;1,2,3");
    Matrix b = parseMatrix("10,10,10;2,2,2");

    // assertArrayEquals(parseMatrix("11,12,13;3,4,5").asDoubleArray(), a.add(b).asDoubleArray(),
    // 0.0001);
    // assertArrayEquals(parseMatrix("2,3,4;2,3,4").asDoubleArray(), a.add(1).asDoubleArray(),
    // 0.0001);
    // assertArrayEquals(parseMatrix("11,12,13;3,4,5").asDoubleArray(), a.addi(b).asDoubleArray(),
    // 0.0001);
    //
    // a = parseMatrix("1,2,3;1,2,3");
    // assertArrayEquals(parseMatrix("2,3,4;2,3,4").asDoubleArray(), a.addi(1).asDoubleArray(),
    // 0.0001);

  }

  @Test
  public void testDiv() throws Exception {
    Matrix a = parseMatrix("1,2,3;2,3,4");
    Matrix b = parseMatrix("10,2,3;43,2,1");

    // assertArrayEquals(parseMatrix("0.1,1,1;0.0465,1.5,4").asDoubleArray(),
    // a.div(b).asDoubleArray(), 0.0001);
    //
    // assertArrayEquals(parseMatrix("0.5,1,1.5;1,1.5,2").asDoubleArray(), a.div(2).asDoubleArray(),
    // 0.0001);
    // assertArrayEquals(parseMatrix("2,1,0.6667;1,0.6667,0.5").asDoubleArray(), a.rdiv(2)
    // .asDoubleArray(), 0.0001);
  }

  @Test
  public void testTranspose() throws Exception {
    Matrix a = parseMatrix("1,2,3;1,2,3");
    Matrix result = parseMatrix("1,1;2,2;3,3");
    // assertArrayEquals(result.asDoubleArray(), a.transpose().asDoubleArray(), 0.0000001);
  }
}
