package org.briljantframework.matrix.base;

import org.briljantframework.matrix.Dim;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.MatrixPrinter;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.api.MatrixRoutines;
import org.junit.Test;

public class BaseMatrixRoutinesTest {

  MatrixFactory bj = new BaseMatrixFactory();
  MatrixRoutines bjr = bj.getMatrixRoutines();

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
  public void testNrm2() throws Exception {

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
    MatrixPrinter.setMinimumTruncateSize(1000);
  }

  @Test
  public void testRepmat() throws Exception {
    DoubleMatrix x = bj.matrix(new double[][]{
        new double[]{1, 0, 0},
        new double[]{0, 1, 0},
        new double[]{0, 0, 1}
    });
    System.out.println(x);
    DoubleMatrix repmat = bjr.repmat(x, 1, 1000);
    System.out.println(repmat);
    System.out.println(bjr.mean(repmat, Dim.C));
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
  public void testTranspose() throws Exception {
    DoubleMatrix a = bj.matrix(new double[]{
        1, 2, 3,
        1, 2, 3
    }).reshape(3, 2).transpose();

    System.out.println(a);
    bj.getMatrixRoutines().transpose(a);
    System.out.println(a.reshape(3,2));

  }
}