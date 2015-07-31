package org.briljantframework.function;

import org.briljantframework.vector.Vector;
import org.junit.Test;

import static org.briljantframework.function.Aggregates.normalizedValueCounts;
import static org.briljantframework.function.Aggregates.repeat;
import static org.briljantframework.function.Aggregates.valueCounts;
import static org.junit.Assert.assertEquals;

public class AggregatesTest {

  @Test
  public void testRepeat() throws Exception {
    Vector vec = Vector.of(1.0, 2.0, 3.0, 4.0, 5.0);
    Vector vecX2 = vec.aggregate(repeat(2));
    assertEquals(vec.size() * 2, vecX2.size());
  }

  @Test
  public void testValueCounts() throws Exception {
    Vector vec = Vector.of('a', 'b', 'c', 'd', 'e', 'e');
    Vector counts = vec.aggregate(Character.class, valueCounts());
    assertEquals(2, counts.get(Integer.class, (Object) 'e').intValue());
  }
}