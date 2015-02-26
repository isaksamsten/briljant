package org.briljantframework.distance;

import junit.framework.TestCase;

import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.Vector;
import org.junit.Assert;

import com.google.common.collect.Lists;

public class SmithWatermanDistanceTest extends TestCase {

  public void testCompute() throws Exception {
//    String[] aa =
//        Lists.charactersOf("xxxxABChdsjkahdksjahdkjsahdjksahkdjhsakjdhsakjdhsakjhdjskax").stream().map(String::valueOf).toArray(String[]::new);
//    String[] bb =
//        Lists.charactersOf("yABCyyyyzzzz").stream().map(String::valueOf).toArray(String[]::new);
    Vector b = StringVector.of("A", "G", "C", "A", "C", "A", "C", "A");
    Vector a = StringVector.of("A", "C", "A", "C", "A", "C", "T", "A");
//    Vector a = StringVector.of(aa);
//    Vector b = StringVector.of(bb);
    SmithWatermanDistance distance = new SmithWatermanDistance(-2, 1, 1);
      double compute = distance.compute(a, b);
    Assert.assertEquals(-12, compute, 0);
  }
}
