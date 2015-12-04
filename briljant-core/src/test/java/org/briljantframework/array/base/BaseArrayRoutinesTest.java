/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.array.base;

import static org.junit.Assert.assertEquals;

import org.briljantframework.array.ArrayPrinter;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.array.api.ArrayRoutines;
import org.briljantframework.array.netlib.NetlibArrayBackend;
import org.junit.Test;

public class BaseArrayRoutinesTest {

  static {
    ArrayPrinter.setMinimumTruncateSize(1000);
  }

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
    double[][] doubles = { {1, 2, 3}, {1, 2, 3}};
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
    DoubleArray x = bj.newVector(new double[] {1, 2, 3});
    // bjr.norm2(x)
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
    DoubleArray a = bj.newVector(new double[] {1, 2, 3, 4});
    DoubleArray b = bj.newVector(new double[] {1, 2, 3});
    DoubleArray c = bj.newDoubleArray(4, 3);
    bjr.ger(1, a, b, c);
    assertEquals(c, bj.newVector(new double[] {1, 2, 3, 4, 2, 4, 6, 8, 3, 6, 9, 12}).reshape(4, 3));
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
