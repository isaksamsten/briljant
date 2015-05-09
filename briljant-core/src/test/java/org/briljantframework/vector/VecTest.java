package org.briljantframework.vector;

import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class VecTest {

  private Vector vec6;
  private Vector vec8;

  @Before
  public void setUp() throws Exception {
    vec6 = DoubleVector.wrap(1, 2, 3, 4, 5, 6);
    vec8 = new StringVector("a", "sb", "cds", "qdsa", "fdasdsadsa", "dd", "r", "a");
  }

  @Test
  public void testInferType() throws Exception {
    assertEquals(Vec.DOUBLE, VectorType.getInstance(Double.class));
    assertEquals(Vec.INT, VectorType.getInstance(Integer.class));
    assertEquals(Vec.BIT, VectorType.getInstance(Boolean.class));
    assertEquals(Vec.BIT, VectorType.getInstance(Bit.class));
    assertEquals(Vec.COMPLEX, VectorType.getInstance(Complex.class));
    assertEquals(Vec.STRING, VectorType.getInstance(String.class));
    assertEquals(Vec.DOUBLE, VectorType.getInstance(Double.TYPE));
    assertEquals(Vec.INT, VectorType.getInstance(Integer.TYPE));
    assertEquals(Vec.VARIABLE, VectorType.infer(null));
  }

  @Test
  public void testMode() throws Exception {
    Vector v = new StringVector("a", "b", "c", "d", "e", "f", "a");
    assertEquals("a", Vec.mode(v));
  }

  @Test
  public void testCount() throws Exception {
    Map<Double, Integer> counts = Vec.count(Double.class, vec6);
    System.out.println(counts);

    System.out.println(Vec.count(vec6));

  }

  @Test
  public void testSort() throws Exception {
    DoubleVector.Builder b = new DoubleVector.Builder();
    for (int i = 0; i < 7; i++) {
      b.add(Utils.getRandom().nextDouble());
    }
    DoubleVector v = b.build();
    System.out.println(v);
    Vector v2 = Vec.sortAsc(v);
    System.out.println(v2);

    System.out.println(Arrays.toString(v.toArray(new Double[v.size()])));

    // System.out.println(v2.getAsDouble(v2.size() - 1));

    Vector space = Vec.linspace(-10, 10, 10000000);
    System.out.println(Vec.mean(space));
  }

  @Test
  public void testSort2() throws Exception {
    Vector sorted = Vec.sortDesc(vec8);
    System.out.println(sorted);

  }

  @Test
  public void testName() throws Exception {
    Vector paa =
        Vec.split(vec6, 3).stream().map(Vec::mean).collect(DoubleVector.collector())
            .build();

    System.out.println(paa);

  }

  @Test
  public void testSplitExact() throws Exception {
    Collection<Vector> chunks = Vec.split(vec6, 3);
    List<Vector> listChunks = new ArrayList<>(chunks);

    assertEquals(3, chunks.size());
    assertEquals(DoubleVector.wrap(1, 2), listChunks.get(0));
    assertEquals(DoubleVector.wrap(3, 4), listChunks.get(1));
    assertEquals(DoubleVector.wrap(5, 6), listChunks.get(2));
  }

  @Test
  public void testSplitSingleton() throws Exception {
    Collection<Vector> chunks = Vec.split(vec6, 6);
    List<Vector> listChunks = new ArrayList<>(chunks);

    assertEquals(1, chunks.size());
    assertEquals(DoubleVector.wrap(1, 2, 3, 4, 5, 6), listChunks.get(0));
  }

  @Test
  public void testSplitUneven() throws Exception {
    Collection<Vector> chunks = Vec.split(vec6, 4);
    List<Vector> listChunks = new ArrayList<>(chunks);

    assertEquals(4, chunks.size());
    assertEquals(DoubleVector.wrap(1, 2), listChunks.get(0));
    assertEquals(DoubleVector.wrap(3, 4), listChunks.get(1));
    assertEquals(DoubleVector.wrap(5), listChunks.get(2));
    assertEquals(DoubleVector.wrap(6), listChunks.get(3));

  }
}
