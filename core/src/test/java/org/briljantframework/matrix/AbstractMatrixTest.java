package org.briljantframework.matrix;

import org.briljantframework.complex.Complex;
import org.junit.Test;

import static org.briljantframework.matrix.Matrices.newComplexMatrix;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;

public class AbstractMatrixTest {

  @Test
  public void testAsReturnThisOnCorrectType() throws Exception {
    IntMatrix a = Matrices.range(0, 10);
    assertSame(a.getStorage(), a.asDoubleMatrix().asComplexMatrix().asLongMatrix().asBitMatrix()
        .asIntMatrix().getStorage());

    DoubleMatrix b = Matrices.linspace(0, 2, 10);
    assertSame(b.getStorage(), b.asIntMatrix().asComplexMatrix().asLongMatrix().asBitMatrix()
        .asDoubleMatrix().getStorage());

    ComplexMatrix c = newComplexMatrix(3, 3);
    assertSame(c.getStorage(), c.asIntMatrix().asDoubleMatrix().asLongMatrix().asBitMatrix()
        .asComplexMatrix().getStorage());

    LongMatrix d = LongMatrix.newMatrix(3, 3);
    assertSame(d.getStorage(), d.asComplexMatrix().asIntMatrix().asBitMatrix().asDoubleMatrix()
        .asLongMatrix().getStorage());

    BitMatrix e = BitMatrix.newBitMatrix(3, 3);
    assertSame(e.getStorage(), e.asIntMatrix().asLongMatrix().asDoubleMatrix().asComplexMatrix()
        .asBitMatrix().getStorage());
  }

  @Test
  public void testAsDoubleMatrix() throws Exception {
    IntMatrix a = IntMatrix.newMatrix(3, 3).assign(10);
    LongMatrix b = LongMatrix.newMatrix(3, 3).assign(10);
    ComplexMatrix c = newComplexMatrix(3, 3).assign(10);
    BitMatrix d = BitMatrix.newBitMatrix(3, 3).assign(true);

    assertEquals(10.0, a.asDoubleMatrix().get(0), 0.0001);
    assertEquals(10.0, b.asDoubleMatrix().get(0), 0.0001);
    assertEquals(10.0, c.asDoubleMatrix().get(0), 0.0001);
    assertEquals(1.0, d.asDoubleMatrix().get(0), 0.0001);
  }

  @Test
  public void testAsIntMatrix() throws Exception {
    DoubleMatrix a = DoubleMatrix.newMatrix(3, 3).assign(10);
    LongMatrix b = LongMatrix.newMatrix(3, 3).assign(10);
    ComplexMatrix c = newComplexMatrix(3, 3).assign(10);
    BitMatrix d = BitMatrix.newBitMatrix(3, 3).assign(true);

    assertEquals(10, a.asIntMatrix().get(0));
    assertEquals(10, b.asIntMatrix().get(0));
    assertEquals(10, c.asIntMatrix().get(0));
    assertEquals(1, d.asIntMatrix().get(0));
  }

  @Test
  public void testAsLongMatrix() throws Exception {
    DoubleMatrix a = DoubleMatrix.newMatrix(3, 3).assign(10);
    IntMatrix b = IntMatrix.newMatrix(3, 3).assign(10);
    ComplexMatrix c = newComplexMatrix(3, 3).assign(10);
    BitMatrix d = BitMatrix.newBitMatrix(3, 3).assign(true);

    assertEquals(10, a.asLongMatrix().get(0));
    assertEquals(10, b.asLongMatrix().get(0));
    assertEquals(10, c.asLongMatrix().get(0));
    assertEquals(1, d.asLongMatrix().get(0));

  }

  @Test
  public void testAsBitMatrix() throws Exception {
    DoubleMatrix a = DoubleMatrix.newMatrix(3, 3).assign(10);
    IntMatrix b = IntMatrix.newMatrix(3, 3).assign(10);
    ComplexMatrix c = newComplexMatrix(3, 3).assign(10);
    LongMatrix d = LongMatrix.newMatrix(3, 3).assign(1);

    assertEquals(false, a.asBitMatrix().get(0));
    assertEquals(false, b.asBitMatrix().get(0));
    assertEquals(false, c.asBitMatrix().get(0));
    assertEquals(true, d.asBitMatrix().get(0));
  }

  @Test
  public void testAsComplexMatrix() throws Exception {
    DoubleMatrix a = DoubleMatrix.newMatrix(3, 3).assign(10);
    LongMatrix b = LongMatrix.newMatrix(3, 3).assign(10);
    IntMatrix c = IntMatrix.newMatrix(3, 3).assign(10);
    BitMatrix d = BitMatrix.newBitMatrix(3, 3).assign(true);

    assertEquals(Complex.valueOf(10), a.asComplexMatrix().get(0));
    assertEquals(Complex.valueOf(10), b.asComplexMatrix().get(0));
    assertEquals(Complex.valueOf(10), c.asComplexMatrix().get(0));
    assertEquals(Complex.valueOf(1), d.asComplexMatrix().get(0));
  }
}
