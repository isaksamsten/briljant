package org.briljantframework;

import com.google.common.collect.Lists;

import org.briljantframework.sort.QuickSort;
import org.briljantframework.sort.Swappable;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class QuickSortTest {


  @Test
  public void testTest() throws Exception {
    ArrayList<String> list =
        Lists.newArrayList("a", "b", "c", "q", "f", "d", "r", "a");
    Swappable s = new ListSwappable(list);
    QuickSort.quickSort(0, list.size(), (a, b) -> list.get(a).compareTo(list.get(b)), s);

    assertEquals("a", list.get(0));
    assertEquals("a", list.get(1));
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
