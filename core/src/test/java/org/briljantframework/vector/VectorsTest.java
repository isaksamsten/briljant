package org.briljantframework.vector;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.briljantframework.Utils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.primitives.Doubles;

public class VectorsTest {

  private Vector vec6;

  @Before
  public void setUp() throws Exception {
    vec6 = DoubleVector.wrap(1, 2, 3, 4, 5, 6);
  }

  @Test
  public void testSort() throws Exception {
    DoubleVector.Builder b = new DoubleVector.Builder();
    for (int i = 0; i < 100000; i++) {
      b.add(Utils.getRandom().nextDouble());
    }
    DoubleVector v = b.build();

    // long start = System.currentTimeMillis();

    // Sort.quickSort(0, b.size(), (a, bi) -> Double.compare(v.getAsDouble(a), v.getAsDouble(bi)),
    // b);
    // Vectors.sort(v, Vector::compare);
    // System.out.println(System.currentTimeMillis() - start);

    double[] arr = new double[100000];
    for (int i = 0; i < arr.length; i++) {
      arr[i] = Utils.getRandom().nextDouble();
    }

    long start = System.currentTimeMillis();
    // arr.clone();
    Collections.sort(Doubles.asList(arr));
    System.out.println(System.currentTimeMillis() - start);

  }

  @Test
  public void testName() throws Exception {
    Vector paa =
        Vectors.split(vec6, 3).stream().map(Vectors::mean).collect(DoubleVector.collector())
            .build();

    System.out.println(paa);

  }

  @Test
  public void testSplitExact() throws Exception {
    Collection<Vector> chunks = Vectors.split(vec6, 3);
    List<Vector> listChunks = new ArrayList<>(chunks);

    assertEquals(3, chunks.size());
    assertEquals(DoubleVector.wrap(1, 2), listChunks.get(0));
    assertEquals(DoubleVector.wrap(3, 4), listChunks.get(1));
    assertEquals(DoubleVector.wrap(5, 6), listChunks.get(2));
  }

  @Test
  public void testSplitSingleton() throws Exception {
    Collection<Vector> chunks = Vectors.split(vec6, 6);
    List<Vector> listChunks = new ArrayList<>(chunks);

    assertEquals(1, chunks.size());
    assertEquals(DoubleVector.wrap(1, 2, 3, 4, 5, 6), listChunks.get(0));
  }

  @Test
  public void testSplitUneven() throws Exception {
    Collection<Vector> chunks = Vectors.split(vec6, 4);
    List<Vector> listChunks = new ArrayList<>(chunks);

    assertEquals(4, chunks.size());
    assertEquals(DoubleVector.wrap(1, 2), listChunks.get(0));
    assertEquals(DoubleVector.wrap(3, 4), listChunks.get(1));
    assertEquals(DoubleVector.wrap(5), listChunks.get(2));
    assertEquals(DoubleVector.wrap(6), listChunks.get(3));

  }
}
