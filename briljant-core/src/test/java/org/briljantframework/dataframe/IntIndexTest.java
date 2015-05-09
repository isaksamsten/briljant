package org.briljantframework.dataframe;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.junit.Assert.*;

public class IntIndexTest {

  @Test
  public void testGet() throws Exception {

  }

  @Test
  public void testContains() throws Exception {

  }

  @Test
  public void testIndices() throws Exception {

  }

  @Test
  public void testKeySet() throws Exception {

  }

  @Test
  public void testIndices1() throws Exception {

  }

  @Test
  public void testNewBuilder() throws Exception {
    Index.Builder b = new IntIndex(10).newBuilder();
    b.set(0, 0);
    b.add(1);
    b.add(2);
    b.add(3);
    b.add(4);
    b.set(5, 5);
    b.set(3, 3);
    Index index = b.build();

    System.out.println(index);




    assertThat(index, CoreMatchers.instanceOf(IntIndex.class));


  }

  @Test
  public void testSize() throws Exception {

  }

  @Test
  public void testIterator() throws Exception {

  }
}