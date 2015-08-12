/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.array.base;

import org.briljantframework.Bj;
import org.briljantframework.array.ArrayPrinter;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.Op;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.array.api.ArrayRoutines;
import org.briljantframework.array.netlib.NetlibArrayBackend;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class BaseArrayRoutinesTest {

  ArrayBackend b = new NetlibArrayBackend();
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
    double[][] doubles = {
        {1, 2, 3},
        {1, 2, 3}
    };
  }

  @Test
  public void testVar1() throws Exception {
    DoubleArray a = Bj.range(100 * 8)
        .reshape(100, 8).asDouble().copy()
        .get(bj.range(1, 20, 2), bj.range(1, 5));
    System.out.println(a);
    long start = System.nanoTime();
    DoubleArray ca = a.transpose().mmul(a);
    DoubleArray c = Bj.doubleArray(4, 4);
    Bj.gemm(Op.KEEP, Op.KEEP, 1, a.transpose(), a, 1, c);
    System.out.println((System.nanoTime() - start) / 1e6);
    System.out.println(c);
    System.out.println(ca);
    System.out.println(c.equals(ca));
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
//    bjr.norm2(x)
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
    assertEquals(c, bj.array(new double[]{
        1, 2, 3, 4, 2, 4, 6, 8, 3, 6, 9, 12
    }).reshape(4, 3));
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
    DoubleArray repmat = bjr.repmat(x, 1, 1000);
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