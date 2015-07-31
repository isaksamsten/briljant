package org.briljantframework.dataframe;

import org.briljantframework.vector.IntVector;
import org.junit.Test;

import static org.briljantframework.function.Aggregates.count;
import static org.junit.Assert.assertEquals;

public class DataFrameTest {

  @Test
  public void testFizzBuzz() throws Exception {
    IntVector.Builder b = new IntVector.Builder();
    for (int i = 1; i <= 100; i++) {
      b.set(i - 1, i);
    }
    DataFrame df = MixedDataFrame.of("number", b.build());
    DataFrame fizzBuzz =
        df.transform(
            v -> v.transform(Integer.class, String.class,
                             i -> i % 15 == 0 ? "FizzBuzz" :
                                  i % 3 == 0 ? "Fizz" :
                                  i % 5 == 0 ? "Buzz" :
                                  String.valueOf(i)))
            .groupBy("number")
            .aggregate(Object.class, count())
            .sort(SortOrder.DESC, "number")
            .head(3);

    assertEquals(3, fizzBuzz.rows());
    assertEquals(1, fizzBuzz.columns());
    assertEquals(27, fizzBuzz.getAsInt("Fizz", "number"));
    assertEquals(14, fizzBuzz.getAsInt("Buzz", "number"));
    assertEquals(6, fizzBuzz.getAsInt("FizzBuzz", "number"));
  }
}
