package org.briljantframework.array;

import org.apache.commons.math3.linear.RealMatrix;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class MatricesTest {

  @Test
  public void testAsFieldVector() throws Exception {

  }

  @Test
  public void testAsFieldMatrix() throws Exception {

  }

  @Test
  public void testAsFieldMatrix1() throws Exception {

  }

  @Test
  public void testAsRealMatrix() throws Exception {

  }

  @Test
  public void testAsRealVector() throws Exception {

  }

  @Test
  public void testRepmat() throws Exception {
    IntArray x = Range.of(3 * 3).reshape(3, 3);
    IntArray expected =
        Arrays.newIntVector(0, 1, 2, 0, 1, 2, 0, 1, 2, 3, 4, 5, 3, 4, 5, 3, 4, 5, 6, 7, 8, 6, 7, 8,
            6, 7, 8, 0, 1, 2, 0, 1, 2, 0, 1, 2, 3, 4, 5, 3, 4, 5, 3, 4, 5, 6, 7, 8, 6, 7, 8, 6, 7,
            8, 0, 1, 2, 0, 1, 2, 0, 1, 2, 3, 4, 5, 3, 4, 5, 3, 4, 5, 6, 7, 8, 6, 7, 8, 6, 7, 8)
            .reshape(9, 9);
    Assert.assertEquals(expected, Matrices.repmat(x, 3));
  }

  @Test
  public void testRepmat1() throws Exception {
    DoubleArray array = DoubleArray.linspace(-1, 1, 1000000).reshape(1000, 1000);
    RealMatrix x = Matrices.asRealMatrix(array);

    long start = System.nanoTime();

    // SingularValueDecomposition decomposition = new SingularValueDecomposition(x);
    // decomposition.getSingularValues();
    System.out.println((System.nanoTime() - start) / 1e6);

    start = System.nanoTime();
    org.briljantframework.linalg.decomposition.SingularValueDecomposition d =
        Arrays.linalg.svd(array);
    System.out.println((System.nanoTime() - start) / 1e6);

    // System.out.println(d.getSingularValues());


    // System.out.println(x);
  }
}
