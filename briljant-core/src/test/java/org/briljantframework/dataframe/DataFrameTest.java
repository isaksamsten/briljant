/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
