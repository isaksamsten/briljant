package org.briljantframework.function;

import org.briljantframework.vector.Vector;
import org.junit.Test;

import java.util.Map;

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
    Map<Character, Integer> counts = vec.aggregate(Character.class, valueCounts());
    assertEquals(2, counts.get('e').intValue());

    System.out.println(
        vec.aggregate(repeat(4)).aggregate(normalizedValueCounts())
    );

//
//    double[] doubles = {1.00000000001, 2, 199, 4, 5};
//    Vector s = Vector.of(Doubles.asList(doubles));
//    System.out.println(s.add(Complex.valueOf(2)).mul(3));
//    Vector combine = s.combine(Number.class, Vector.singleton(Complex.valueOf(2), s.size()), Combine.add(2));
//    System.out.println(combine);
//    System.out.println(combine.sort(SortOrder.ASC));
//    System.out.println(combine.sort(String.class, Comparator.comparingInt(String::length)));
//    System.out.println(combine.get(String.class, 0));
  }
}