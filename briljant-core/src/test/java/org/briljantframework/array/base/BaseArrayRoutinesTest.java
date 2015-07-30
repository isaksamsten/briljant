package org.briljantframework.array.base;

import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.ArrayPrinter;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.array.api.ArrayRoutines;
import org.junit.Test;

public class BaseArrayRoutinesTest {

  ArrayBackend b = new BaseArrayBackend();
  ArrayFactory bj = b.getArrayFactory();
  ArrayRoutines bjr = b.getArrayRoutines();

  @Test
  public void testMean() throws Exception {

  }

  @Test
  public void testMean1() throws Exception {

  }

  @Test
  public void testVar() throws Exception {

  }

  @Test
  public void testVar1() throws Exception {

  }

  @Test
  public void testStd() throws Exception {

  }

  @Test
  public void testStd1() throws Exception {

  }

  @Test
  public void testMin() throws Exception {

  }

  @Test
  public void testMin1() throws Exception {

  }

  @Test
  public void testMax() throws Exception {

  }

  @Test
  public void testMax1() throws Exception {

  }

  @Test
  public void testSum() throws Exception {

  }

  @Test
  public void testSum1() throws Exception {

  }

  @Test
  public void testProd() throws Exception {

  }

  @Test
  public void testProd1() throws Exception {

  }

  @Test
  public void testCumsum() throws Exception {

  }

  @Test
  public void testCumsum1() throws Exception {

  }

  @Test
  public void testIamax() throws Exception {

  }

  @Test
  public void testDot() throws Exception {

  }

  @Test
  public void testDotu() throws Exception {

  }

  @Test
  public void testDotc() throws Exception {

  }

  @Test
  public void testNorm21() throws Exception {
    DoubleArray x = bj.array(new double[]{1, 2, 3});
    System.out.println(bjr.norm2(x));
  }

  @Test
  public void testNorm2() throws Exception {

  }

  @Test
  public void testAsum() throws Exception {

  }

  @Test
  public void testAsum1() throws Exception {

  }

  @Test
  public void testIamax1() throws Exception {

  }

  @Test
  public void testAxpy() throws Exception {

  }

  @Test
  public void testGemv() throws Exception {

  }

  @Test
  public void testGer() throws Exception {
    DoubleArray a = bj.array(new double[]{1, 2, 3, 4});
    DoubleArray b = bj.array(new double[]{1, 2, 3});
    DoubleArray c = bj.doubleArray(4, 3);
    bjr.ger(1, a, b, c);
    System.out.println(c);
    System.out.println(a.mmul(b.transpose()));
  }

  @Test
  public void testGemm() throws Exception {

  }

  @Test
  public void testRepeat() throws Exception {

  }

  @Test
  public void testTake() throws Exception {

  }

  @Test
  public void testVsplit() throws Exception {

  }

  @Test
  public void testVstack() throws Exception {

  }

  @Test
  public void testHsplit() throws Exception {

  }

  @Test
  public void testHstack() throws Exception {

  }

  @Test
  public void testShuffle() throws Exception {

  }

  @Test
  public void testSort() throws Exception {

  }

  @Test
  public void testSort1() throws Exception {

  }

  static {
    ArrayPrinter.setMinimumTruncateSize(1000);
  }

  @Test
  public void testRepmat() throws Exception {
    DoubleArray x = bj.array(new double[][]{
        new double[]{1, 0, 0},
        new double[]{0, 1, 0},
        new double[]{0, 0, 1}
    });
    System.out.println(x);
    DoubleArray repmat = bjr.repmat(x, 1, 1000);
    System.out.println(repmat);
  }

  @Test
  public void testRepmat1() throws Exception {

  }

  @Test
  public void testCopy() throws Exception {

  }

  @Test
  public void testSwap() throws Exception {

  }

  @Test
  public void testScal() throws Exception {

  }
}