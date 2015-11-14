package org.briljantframework;

import java.util.List;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.array.Arrays;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;
import org.junit.Test;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class ArraysTest {

  @Test
  public void testOrder() throws Exception {
    DoubleArray array = DoubleArray.of(2, 3, 1, 9, 1);
    System.out.println(Arrays.order(array));

  }

  @Test
  public void testOrderDimension() throws Exception {
    DoubleArray array = DoubleArray.of(1, 9, 1, 9, 2, 4).reshape(3, 2);
    System.out.println(array);
    System.out.println(Arrays.order(0, array));
  }

  @Test
  public void testConcatenate() throws Exception {
    DoubleArray x = Arrays.range(2 * 2 * 3).asDouble().reshape(2, 2, 3);
    System.out.println(Arrays.concatenate(java.util.Arrays.asList(x, x, x), 2));

    System.out.println(Arrays.vstack(Arrays.ones(3), Arrays.ones(3)));
  }

  @Test
  public void testSplit() throws Exception {
    DoubleArray x = Arrays.range(2 * 2 * 3).asDouble().reshape(2, 2, 3);
    List<DoubleArray> split = Arrays.split(x, 3, 2);
    for (DoubleArray array : split) {
      System.out.println(array);
    }

  }

  @Test
  public void testReferenceArray() throws Exception {

  }

  @Test
  public void testOf() throws Exception {

  }

  @Test
  public void testOf1() throws Exception {

  }

  @Test
  public void testDoubleArray() throws Exception {

  }

  @Test
  public void testOnes() throws Exception {

  }

  @Test
  public void testZero() throws Exception {

  }

  @Test
  public void testEye() throws Exception {

  }

  @Test
  public void testOf2() throws Exception {

  }

  @Test
  public void testOf3() throws Exception {

  }

  @Test
  public void testLinspace() throws Exception {

  }

  @Test
  public void testRand() throws Exception {

  }

  @Test
  public void testRandn() throws Exception {

  }

  @Test
  public void testRand1() throws Exception {

  }

  @Test
  public void testComplexArray() throws Exception {

  }

  @Test
  public void testComplexArray1() throws Exception {

  }

  @Test
  public void testOf4() throws Exception {

  }

  @Test
  public void testOf5() throws Exception {

  }

  @Test
  public void testIntArray() throws Exception {

  }

  @Test
  public void testOf6() throws Exception {

  }

  @Test
  public void testOf7() throws Exception {

  }

  @Test
  public void testRange() throws Exception {

  }

  @Test
  public void testRange1() throws Exception {

  }

  @Test
  public void testRange2() throws Exception {

  }

  @Test
  public void testRange3() throws Exception {

  }

  @Test
  public void testRandi() throws Exception {

  }

  @Test
  public void testLongArray() throws Exception {

  }

  @Test
  public void testOf8() throws Exception {

  }

  @Test
  public void testOf9() throws Exception {

  }

  @Test
  public void testBooleanArray() throws Exception {

  }

  @Test
  public void testOf10() throws Exception {

  }

  @Test
  public void testOf11() throws Exception {

  }

  @Test
  public void testDiag() throws Exception {

  }

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
  public void testSum() throws Exception {

  }

  @Test
  public void testSum1() throws Exception {

  }

  @Test
  public void testSum2() throws Exception {

  }

  @Test
  public void testSum3() throws Exception {

  }

  @Test
  public void testSum4() throws Exception {

  }

  @Test
  public void testSum5() throws Exception {

  }

  @Test
  public void testProd() throws Exception {

  }

  @Test
  public void testProd1() throws Exception {

  }

  @Test
  public void testMin() throws Exception {

  }

  @Test
  public void testMin1() throws Exception {

  }

  @Test
  public void testMin2() throws Exception {

  }

  @Test
  public void testMin3() throws Exception {

  }

  @Test
  public void testMin4() throws Exception {

  }

  @Test
  public void testMin5() throws Exception {

  }

  @Test
  public void testMin6() throws Exception {

  }

  @Test
  public void testMin7() throws Exception {

  }

  @Test
  public void testMin8() throws Exception {

  }

  @Test
  public void testMin9() throws Exception {

  }

  @Test
  public void testMax() throws Exception {

  }

  @Test
  public void testMax1() throws Exception {

  }

  @Test
  public void testMax2() throws Exception {

  }

  @Test
  public void testMax3() throws Exception {

  }

  @Test
  public void testMax4() throws Exception {

  }

  @Test
  public void testMax5() throws Exception {

  }

  @Test
  public void testMax6() throws Exception {

  }

  @Test
  public void testMax7() throws Exception {

  }

  @Test
  public void testMax8() throws Exception {

  }

  @Test
  public void testMax9() throws Exception {

  }

  @Test
  public void testNorm2() throws Exception {

  }

  @Test
  public void testNorm21() throws Exception {

  }

  @Test
  public void testAsum() throws Exception {

  }

  @Test
  public void testAsum1() throws Exception {

  }

  @Test
  public void testIamax() throws Exception {

  }

  @Test
  public void testIamax1() throws Exception {

  }

  @Test
  public void testCumsum() throws Exception {

  }

  @Test
  public void testCumsum1() throws Exception {

  }

  @Test
  public void testTrace() throws Exception {

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
  public void testCopy() throws Exception {

  }

  @Test
  public void testSwap() throws Exception {

  }

  @Test
  public void testTake() throws Exception {

  }

  @Test
  public void testRepmat() throws Exception {

  }

  @Test
  public void testRepmat1() throws Exception {

  }

  @Test
  public void testRepeat() throws Exception {

  }

  @Test
  public void testSort() throws Exception {

  }

  @Test
  public void testSort1() throws Exception {

  }

  @Test
  public void testSort2() throws Exception {

  }

  @Test
  public void testSort3() throws Exception {

  }

  @Test
  public void testSort4() throws Exception {

  }

  @Test
  public void testSort5() throws Exception {

  }

  @Test
  public void testSort6() throws Exception {

  }

  @Test
  public void testSort7() throws Exception {

  }

  @Test
  public void testShuffle() throws Exception {

  }

  @Test
  public void testDot() throws Exception {

  }

  @Test
  public void testInner() throws Exception {

  }

  @Test
  public void testOuter() throws Exception {

  }

  @Test
  public void testDotc() throws Exception {

  }

  @Test
  public void testDotu() throws Exception {

  }

  @Test
  public void testScal() throws Exception {

  }

  @Test
  public void testAxpy() throws Exception {

  }

  @Test
  public void testGer() throws Exception {

  }

  @Test
  public void testGemv() throws Exception {

  }

  @Test
  public void testGemv1() throws Exception {

  }

  @Test
  public void testGemv2() throws Exception {

  }

  @Test
  public void testGemm() throws Exception {

  }

  @Test
  public void testGemm1() throws Exception {

  }

  @Test
  public void testGemm2() throws Exception {

  }

  @Test
  public void testArgmax() throws Exception {

  }

  @Test
  public void testArgmin() throws Exception {

  }

  @Test
  public void testTake1() throws Exception {

  }

  @Test
  public void testMask() throws Exception {

  }

  @Test
  public void testPutMask() throws Exception {

  }

  @Test
  public void testSelect() throws Exception {

  }

  @Test
  public void testCos() throws Exception {

  }

  @Test
  public void testSqrt() throws Exception {

  }

  @Test
  public void testPow() throws Exception {

  }

  @Test
  public void testLog2() throws Exception {

  }

  @Test
  public void testAcos() throws Exception {

  }

  @Test
  public void testCosh() throws Exception {

  }

  @Test
  public void testSignum() throws Exception {

  }

  @Test
  public void testCos1() throws Exception {

  }

  @Test
  public void testAsin() throws Exception {

  }

  @Test
  public void testAbs() throws Exception {

  }

  @Test
  public void testCbrt() throws Exception {

  }

  @Test
  public void testAbs1() throws Exception {

  }

  @Test
  public void testCeil() throws Exception {

  }

  @Test
  public void testSinh() throws Exception {

  }

  @Test
  public void testLog() throws Exception {

  }

  @Test
  public void testTanh() throws Exception {

  }

  @Test
  public void testSin() throws Exception {

  }

  @Test
  public void testScalb() throws Exception {

  }

  @Test
  public void testExp() throws Exception {

  }

  @Test
  public void testLog10() throws Exception {

  }

  @Test
  public void testFloor() throws Exception {

  }

  @Test
  public void testTan() throws Exception {

  }

  @Test
  public void testAbs2() throws Exception {

  }

  @Test
  public void testRound() throws Exception {

  }

  @Test
  public void testAtan() throws Exception {

  }

  @Test
  public void testSinh1() throws Exception {

  }

  @Test
  public void testExp1() throws Exception {

  }

  @Test
  public void testAcos1() throws Exception {

  }

  @Test
  public void testSin1() throws Exception {

  }

  @Test
  public void testAbs3() throws Exception {

  }

  @Test
  public void testSqrt1() throws Exception {

  }

  @Test
  public void testLog1() throws Exception {

  }

  @Test
  public void testFloor1() throws Exception {

  }

  @Test
  public void testTan1() throws Exception {

  }

  @Test
  public void testTanh1() throws Exception {

  }

  @Test
  public void testAsin1() throws Exception {

  }

  @Test
  public void testCosh1() throws Exception {

  }

  @Test
  public void testAtan1() throws Exception {

  }

  @Test
  public void testCeil1() throws Exception {

  }

  @Test
  public void testArg() throws Exception {

  }

  @Test
  public void testWhere() throws Exception {
    DoubleArray condition = Arrays.newDoubleVector(1, 0, 1, 2, 1);
    ComplexArray x = Arrays.newDoubleVector(1, 1, 1, 1, 1).asComplex();
    ComplexArray y = Arrays.rand(5).mapToComplex(v -> Complex.valueOf(v).sqrt());
    System.out.println(Arrays.where(condition.gte(2), x.asComplex(), y.asComplex()));

  }

  @Test
  public void testSelect1() throws Exception {

  }
}
