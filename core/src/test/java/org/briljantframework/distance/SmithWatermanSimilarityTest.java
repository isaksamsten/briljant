package org.briljantframework.distance;

import junit.framework.TestCase;

import org.briljantframework.similiarity.SmithWatermanSimilarity;
import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.Vector;
import org.junit.Assert;

import com.google.common.collect.Lists;

public class SmithWatermanSimilarityTest extends TestCase {

  public void testCompute() throws Exception {
    String[] aa =
        Lists.charactersOf("xxxxABCx").stream().map(String::valueOf).toArray(String[]::new);
    String[] bb =
        Lists.charactersOf("yABCyyyy").stream().map(String::valueOf).toArray(String[]::new);
    Vector a = StringVector.of("A", "G", "C", "A", "C", "A", "C", "A");
    Vector b = StringVector.of("A", "C", "A", "C", "A", "C", "T", "A");
    // Vector a = StringVector.of(aa);
    // Vector b = StringVector.of(bb);
    SmithWatermanSimilarity distance = new SmithWatermanSimilarity(2, -1, -1);
    double compute = distance.compute(a, b);
    Assert.assertEquals(3, compute, 0);
  }
}
