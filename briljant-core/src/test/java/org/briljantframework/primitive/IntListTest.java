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
package org.briljantframework.primitive;

import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.ObjectIndex;
import org.junit.Test;

/**
 * Created by isak on 14/09/15.
 */
public class IntListTest {

  @Test
  public void testSort() throws Exception {
    IntList list = new IntList();
    list.add(1);
    list.add(99);
    list.add(3);

    list.primitiveSort(Integer::compare);
    System.out.println(list);

    Index.Builder index = new ObjectIndex.Builder();
    index.add(10);
    index.add(32);
    index.add(2);

    int[] values = {1, 99, 3};

    index.sortIterationOrder((a, b) -> Integer.compare(values[a], values[b]));
    System.out.println(index.build().keySet());
  }
}
