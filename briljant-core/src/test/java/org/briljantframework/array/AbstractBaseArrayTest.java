package org.briljantframework.array;

import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.array.netlib.NetlibArrayBackend;
import org.briljantframework.complex.Complex;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AbstractBaseArrayTest {

  private final ArrayFactory bj = new NetlibArrayBackend().getArrayFactory();

  @Test
  public void testAsReturnThisOnCorrectType() throws Exception {
    IntArray a = bj.range(0, 10).reshape(2, 5);
    Array<String> b = bj.array("lorem ipsum foo bar baz toto".split(" ")).reshape(2, 3);
    System.out.println(a);
    System.out.println(b);

    System.out.println(b.asInt(String::length).mapToObj(String::valueOf));
  }

  @Test
  public void testAsDoubleMatrix() throws Exception {
    IntArray a = bj.intArray(3, 3).assign(10);
    LongArray b = bj.longArray(3, 3).assign(10);
    ComplexArray c = bj.complexArray(3, 3).assign(10);
    BitArray d = bj.booleanArray(3, 3).assign(true);

    assertEquals(10.0, a.asDouble().get(0), 0.0001);
    assertEquals(10.0, b.asDouble().get(0), 0.0001);
    assertEquals(10.0, c.asDouble().get(0), 0.0001);
    assertEquals(1.0, d.asDouble().get(0), 0.0001);
  }

  @Test
  public void testAsIntMatrix() throws Exception {
    DoubleArray a = bj.doubleArray(3, 3).assign(10);
    LongArray b = bj.longArray(3, 3).assign(10);
    ComplexArray c = bj.complexArray(3, 3).assign(10);
    BitArray d = bj.booleanArray(3, 3).assign(true);

    assertEquals(10, a.asInt().get(0));
    assertEquals(10, b.asInt().get(0));
    assertEquals(10, c.asInt().get(0));
    assertEquals(1, d.asInt().get(0));
  }

  @Test
  public void testAsLongMatrix() throws Exception {
    DoubleArray a = bj.doubleArray(3, 3).assign(10);
    IntArray b = bj.intArray(3, 3).assign(10);
    ComplexArray c = bj.complexArray(3, 3).assign(10);
    BitArray d = bj.booleanArray(3, 3).assign(true);

    assertEquals(10, a.asLong().get(0));
    assertEquals(10, b.asLong().get(0));
    assertEquals(10, c.asLong().get(0));
    assertEquals(1, d.asLong().get(0));

  }

  @Test
  public void testAsBitMatrix() throws Exception {
    DoubleArray a = bj.doubleArray(3, 3).assign(10);
    IntArray b = bj.intArray(3, 3).assign(10);
    ComplexArray c = bj.complexArray(3, 3).assign(10);
    LongArray d = bj.longArray(3, 3).assign(1);

    assertEquals(false, a.asBit().get(0));
    assertEquals(false, b.asBit().get(0));
    assertEquals(false, c.asBit().get(0));
    assertEquals(true, d.asBit().get(0));
  }

  @Test
  public void testAsComplexMatrix() throws Exception {
    DoubleArray a = bj.doubleArray(3, 3).assign(10);
    LongArray b = bj.longArray(3, 3).assign(10);
    IntArray c = bj.intArray(3, 3).assign(10);
    BitArray d = bj.booleanArray(3, 3).assign(true);

    assertEquals(Complex.valueOf(10), a.asComplex().get(0));
    assertEquals(Complex.valueOf(10), b.asComplex().get(0));
    assertEquals(Complex.valueOf(10), c.asComplex().get(0));
    assertEquals(Complex.valueOf(1), d.asComplex().get(0));
  }
}
