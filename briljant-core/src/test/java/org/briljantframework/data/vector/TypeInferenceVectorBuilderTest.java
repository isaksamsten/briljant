package org.briljantframework.data.vector;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collections;

import org.briljantframework.data.Na;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class TypeInferenceVectorBuilderTest {

  Vector.Builder builder;

  @Before
  public void setUp() throws Exception {
    builder = new TypeInferenceVectorBuilder();
  }

  @Test
  public void testSetNA_NoneIntegerKey() throws Exception {
    builder.setNA("key");
    builder.add(1);
    Vector vector = builder.build();
    assertEquals(VectorType.OBJECT, vector.getType());
    assertEquals(2, vector.size());
  }

  @Test
  public void testAddNA() throws Exception {
    builder.addNA().add(1);
    Vector vector = builder.build();
    assertEquals(VectorType.INT, vector.getType());
    assertEquals(2, vector.size());
    assertEquals(Arrays.asList(Na.INT, 1), vector.toList(Integer.class));
  }

  @Test
  public void testAdd_int() throws Exception {
    builder.add(1);
    Vector vector = builder.build();
    assertEquals(VectorType.INT, vector.getType());
    assertEquals(1, vector.size());
    assertEquals(Collections.singletonList(1), vector.toList(Integer.class));
  }

  @Test
  public void testAdd_double() throws Exception {
    builder.add(1.0);
    Vector vector = builder.build();
    assertEquals(VectorType.DOUBLE, vector.getType());
    assertEquals(1, vector.size());
    assertEquals(Collections.singletonList(1.0), vector.toList(Double.class));
  }

  @Test
  public void testAdd_ReferenceType() throws Exception {
    builder.add("hello");
    Vector vector = builder.build();
    assertEquals(VectorType.of(String.class), vector.getType());
    assertEquals(1, vector.size());
    assertEquals(Collections.singletonList("hello"), vector.toList(String.class));
  }

  @Test
  public void testAdd1() throws Exception {

  }

  @Test
  public void testSet() throws Exception {

  }

  @Test
  public void testSet1() throws Exception {

  }

  @Test
  public void testSet2() throws Exception {

  }

  @Test
  public void testAdd2() throws Exception {

  }

  @Test
  public void testAddFromVector() throws Exception {

  }

  @Test
  public void testAdd4() throws Exception {

  }

  @Test
  public void testAddAll() throws Exception {

  }

  @Test
  public void testRemove() throws Exception {

  }

  @Test
  public void testRead() throws Exception {

  }

  @Test
  public void testReadAll() throws Exception {

  }

  @Test
  public void testSize_addNA() throws Exception {

  }

  @Test
  public void testSize_setNA() throws Exception {

  }

  @Test
  public void testSize_setLocNA() throws Exception {

  }
}
