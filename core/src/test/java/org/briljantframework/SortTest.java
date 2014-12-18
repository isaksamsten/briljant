package org.briljantframework;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class SortTest {


  @Test
  public void testTest() throws Exception {
    ArrayList<Integer> list =
        Lists.newArrayList(9, 2, 3, 5, 1, 53, 5, 64, 65, 23, 2, 321, 13, 2, 321);
    Swappable s = new ListSwappable(list);
    Sort.quickSort(0, 15, (a, b) -> list.get(a) - list.get(b), s);
    System.out.println(list);

    assertEquals(1, 1, 1);
  }

  private static class ListSwappable implements Swappable {
    private final List<?> list;

    private ListSwappable(List<?> list) {
      this.list = list;
    }

    @Override
    public void swap(int a, int b) {
      Collections.swap(list, a, b);
    }
  }
}
