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
    vec8 = Vector.of("a", "sb", "cds", "qdsa", "fdasdsadsa", "dd", "r", "a");
  }

  @Test
  public void testInferType() throws Exception {
    assertEquals(Vec.DOUBLE, Vec.typeOf(Double.class));
    assertEquals(Vec.INT, Vec.typeOf(Integer.class));
    assertEquals(Vec.BIT, Vec.typeOf(Boolean.class));
    assertEquals(Vec.BIT, Vec.typeOf(Bit.class));
    assertEquals(Vec.COMPLEX, Vec.typeOf(Complex.class));
    assertEquals(Vec.STRING, Vec.typeOf(String.class));
    assertEquals(Vec.DOUBLE, Vec.typeOf(Double.TYPE));
    assertEquals(Vec.INT, Vec.typeOf(Integer.TYPE));
    assertEquals(Vec.VARIABLE, Vec.inferTypeOf(null));
  }

  @Test
  public void testMode() throws Exception {
    Vector v = Vector.of("a", "b", "c", "d", "e", "f", "a");
    assertEquals("a", Vec.mode(v));
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
