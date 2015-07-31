package org.briljantframework.distance;

import com.google.common.collect.Lists;

import org.briljantframework.similiarity.SmithWatermanSimilarity;
import org.briljantframework.vector.Vector;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class SmithWatermanSimilarityTest {

  @Test
  public void testCompute() throws Exception {
    String[] aa = Lists.charactersOf("xxxxABCx").stream()
        .map(String::valueOf)
        .toArray(String[]::new);

    String[] bb = Lists.charactersOf("yABCyyyy").stream()
        .map(String::valueOf)
        .toArray(String[]::new);

    Vector a = Vector.of(aa);
    Vector b = Vector.of(bb);
    SmithWatermanSimilarity distance = new SmithWatermanSimilarity(1, 0, 0);
    double compute = distance.compute(a, b);
    assertEquals(3, compute, 0);
  }
}
