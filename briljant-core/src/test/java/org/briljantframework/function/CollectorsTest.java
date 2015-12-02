/**
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
package org.briljantframework.function;

import static org.briljantframework.data.Collectors.repeat;
import static org.briljantframework.data.Collectors.valueCounts;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;

import org.briljantframework.data.Collectors;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.vector.Vector;
import org.junit.Test;

public class CollectorsTest {

  @Test
  public void testRepeat() throws Exception {
    Vector vec = Vector.of(1.0, 2.0, 3.0, 4.0, 5.0);
    Vector vecX2 = vec.collect(repeat(2));
    assertEquals(vec.size() * 2, vecX2.size());
  }

  @Test
  public void testValueCounts() throws Exception {
    Vector vec = Vector.of('a', 'b', 'c', 'd', 'e', 'e');
    Vector counts = vec.collect(Character.class, valueCounts());
    assertEquals(2, counts.get(Integer.class, 'e').intValue());
  }


  @Test
  public void testFactorize() throws Exception {
    Vector v = Vector.of("a", "b", "c", "c", "d", "d", "a");
    Vector actual = v.collect(Collectors.factorize());
    assertEquals(Vector.of(0, 1, 2, 2, 3, 3, 0), actual);
  }

  @Test
  public void testToDataFrame() throws Exception {
    DataFrame df =
        Arrays.asList(Vector.of(1, 2, 3), Vector.of(1, 2, 3), Vector.of(1, 2, 3)).stream()
            .collect(Collectors.toDataFrame());
    System.out.println(df);

    assertEquals(2, df.getAsInt(1, 1));
  }
}
