package org.briljantframework;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.google.common.collect.Lists;

public class QuickSortTest {


  @Test
  public void testTest() throws Exception {
    ArrayList<String> list =
        Lists.newArrayList("a", "b", "c", "q", "f", "d", "r", "a");
    Swappable s = new ListSwappable(list);
    System.out.println();
    QuickSort.quickSort(0, list.size(), (a, b) -> list.get(a).compareTo(list.get(b)), s);
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
