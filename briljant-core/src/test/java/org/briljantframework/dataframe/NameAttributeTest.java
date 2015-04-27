package org.briljantframework.dataframe;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class NameAttributeTest {

  private NameAttribute attr;

  @Before
  public void setUp() throws Exception {
    attr = new NameAttribute("a", "b", "c", "d");
  }

  @Test
  public void testGet() throws Exception {
    assertEquals("a", attr.get(0));
    assertEquals("d", attr.get(3));
  }

  @Test
  public void testPut() throws Exception {
    attr.put(0, "q");
    attr.put(5, "q");
    assertEquals("q", attr.get(0));
    assertEquals("q", attr.get(5));
  }

  @Test
  public void testRemove() throws Exception {
    attr.remove(0);
    assertEquals("b", attr.get(0));
    attr.remove(1);
    assertEquals("d", attr.get(1));
  }

  @Test
  public void testSwap() throws Exception {

  }
}
