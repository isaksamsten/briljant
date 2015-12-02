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
package org.briljantframework.data.dataframe;

import static org.junit.Assert.assertEquals;

import org.briljantframework.data.dataframe.join.JoinType;
import org.briljantframework.data.vector.Vector;
import org.junit.Test;

public class JoinTest {

  @Test
  public void testSimpleInnerJoin() throws Exception {
    DataFrame left = DataFrame.of("key", Vector.of("foo", "foo", "ko"), "lval", Vector.of(1, 2, 4));
    DataFrame right = DataFrame.of("key", Vector.of("foo", "bar"), "rval", Vector.of(3, 5));

    DataFrame actual = left.join(JoinType.INNER, right);
    DataFrame expected =
        DataFrame.of("key", Vector.of("foo", "foo"), "lval", Vector.of(1, 2), "rval",
            Vector.of(3, 3));
//    assertEquals(expected, actual);
  }

  @Test
  public void testComplexInnerJoin() throws Exception {
    DataFrame left =
        DataFrame.of("key1", Vector.of("foo", "foo", "bar"), "key2",
            Vector.of("one", "two", "one"), "lval", Vector.of(1, 2, 3));
    DataFrame right =
        DataFrame.of("key1", Vector.of("foo", "foo", "bar", "bar"), "key2",
            Vector.of("one", "one", "one", "two"), "rval", Vector.of(4, 5, 6, 7));

    DataFrame actual = left.join(JoinType.INNER, right);
    DataFrame expected =
        DataFrame.of("key1", Vector.of("foo", "foo", "bar"), "key2",
            Vector.of("one", "one", "one"), "lval", Vector.of(1, 1, 3), "rval", Vector.of(4, 5, 6));
    //assertEquals(expected, actual);
  }
}
