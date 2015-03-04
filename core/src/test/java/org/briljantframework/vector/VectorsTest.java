package org.briljantframework.vector;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.briljantframework.Utils;
import org.junit.Before;
import org.junit.Test;

public class VectorsTest {

  private Vector vec6;
  private Vector vec8;

  @Before
  public void setUp() throws Exception {
    vec6 = DoubleVector.wrap(1, 2, 3, 4, 5, 6);
    vec8 = new StringVector("a", "sb", "cds", "qdsa", "fdasdsadsa", "dd", "r", "a");
  }

  @Test
  public void testTest() throws Exception {
    System.out.println(Vectors.sortDesc(vec8));
    System.out.println(Vectors.sortAsc(vec8));
    System.out.println(Vectors.sort(vec8,
        (vec, a, b) -> Integer.compare(vec.getAsString(a).length(), vec.getAsString(b).length())));
  }

    @Test
    public void testMode() throws Exception {
        Vector v = new StringVector("a", "b", "c", "d", "e", "f", "a");
        System.out.println(Vectors.mode(v));

    }

    @Test
  public void testSort() throws Exception {
    DoubleVector.Builder b = new DoubleVector.Builder();
    for (int i = 0; i < 7; i++) {
      b.add(Utils.getRandom().nextDouble());
    }
    DoubleVector v = b.build();
    System.out.println(v);
    Vector v2 = Vectors.sortAsc(v);
    System.out.println(v2);
    // System.out.println(v2.getAsDouble(v2.size() - 1));


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
