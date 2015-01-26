package org.briljantframework.matrix;

import static org.briljantframework.matrix.Doubles.parseMatrix;
import static org.junit.Assert.assertArrayEquals;

import org.briljantframework.Range;
import org.junit.Test;

import java.util.Iterator;
import java.util.Random;
import java.util.stream.IntStream;

public class DoubleMatrixTest {

  @Test
  public void testDropRow() throws Exception {
    DoubleMatrix a = parseMatrix("1,1,1;2,2,2");

  }

  @Test
  public void testMmul() throws Exception {
    DoubleMatrix a = parseMatrix("1,2,3;1,2,3");
    DoubleMatrix b = parseMatrix("1,2;1,2;1,2");

    DoubleMatrix AxB = parseMatrix("6,12;6,12");
    DoubleMatrix BxA = parseMatrix("3,6,9;3,6,9;3,6,9");
//    assertArrayEquals(AxB.asDoubleArray(), a.mmul(b).asDoubleArray(), 0.00001);
//    assertArrayEquals(BxA.asDoubleArray(), b.mmul(a).asDoubleArray(), 0.00001);

    DoubleMatrix m = Doubles.rand(4, 4);

    System.out.println(m);
    Iterator<Integer> i = Range.range(50).iterator();
    m.assign(i::next);
    System.out.println(m);
    Axis ax = Axis.COLUMN;
    DoubleMatrix d = m.slice(Range.range(3), Range.range(3));
    for (int j = 0; j < d.size(); j++) {
      System.out.println(d.get(j));
    }

    System.out.println(m.slice(Range.range(3), Range.range(3)));
    System.out.println(a.slice(Range.range(0, 1)));

    System.out.println(m.slice(Range.range(10)));

  }

  @Test
  public void testMul() throws Exception {
    DoubleMatrix a = parseMatrix("1,2,3;1,2,3");
    DoubleMatrix b = parseMatrix("1,2,3;1,2,3");

    System.out.println(a.mmul(Transpose.CONJ, a, Transpose.NO));

    System.out.println(a.transpose().mmul(a));
    //
    // System.out.println(a.add(b));
    //
    // Matrix a = fill(3, 3, 2);
    // Matrix b = randn(3, 3);
    //
    // System.out.println(b.add(a));
    // System.out.println(b.sub(a));
    // System.out.println(b.mul(a));
    // System.out.println(b.mmul(a));
    // System.out.println(b.add(1, a, -1));
    // System.out.println(b.add(a.mul(-1)));
    //
    // System.out.println(b.add(1, a, -1).equalsTo(b.add(a.mul(-1))));
    //
    // System.out.println(Arrays.toString(b.add(1, a, -1).asDoubleArray()));
    //
    // System.out.println(Arrays.toString(b.add(a.mul(-1)).asDoubleArray()));
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
    DoubleMatrix a = parseMatrix("1,2,3;1,2,3");
    DoubleMatrix b = parseMatrix("10,10,10;2,2,2");

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

    // assertArrayEquals(parseMatrix("9,8,7;9,8,7").asDoubleArray(), a.rsubi(10).asDoubleArray(),
    // 0.00001);
  }

  @Test
  public void testAdd() throws Exception {
    DoubleMatrix a = parseMatrix("1,2,3;1,2,3");
    DoubleMatrix b = parseMatrix("10,10,10;2,2,2");

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
    DoubleMatrix a = parseMatrix("1,2,3;2,3,4");
    DoubleMatrix b = parseMatrix("10,2,3;43,2,1");

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
    DoubleMatrix a = parseMatrix("1,2,3;1,2,3");
    DoubleMatrix result = parseMatrix("1,1;2,2;3,3");
    // assertArrayEquals(result.asDoubleArray(), a.transpose().asDoubleArray(), 0.0000001);
  }
}
