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
