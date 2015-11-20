package org.briljantframework.array;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class BooleanArrayTest {

  @Test
  public void testAny_withDim() throws Exception {
    BooleanArray array = BooleanArray.falses(3, 3, 3);
    array.set(new int[] {0, 0, 0}, true);
    array.set(new int[] {0, 1, 0}, true);
    array.set(new int[] {0, 0, 1}, true);

    BooleanArray expected_0 = BooleanArray.falses(3, 3);
    expected_0.set(0, 0, true);
    expected_0.set(0, 1, true);
    expected_0.set(1, 0, true);

    BooleanArray expected_1 = BooleanArray.falses(3, 3);
    expected_1.set(0, 0, true);
    expected_1.set(0, 1, true);

    BooleanArray expected_2 = BooleanArray.falses(3, 3);
    expected_2.set(0, 0, true);
    expected_2.set(0, 1, true);

    assertEquals(expected_0, array.any(0));
    assertEquals(expected_1, array.any(1));
    assertEquals(expected_2, array.any(2));
  }

  @Test
  public void testAny() throws Exception {
    assertEquals(true, BooleanArray.of(true, false, true, false).any());
    assertEquals(false, BooleanArray.of(false, false, false).any());
  }

  @Test
  public void testAll_withDim() throws Exception {
    BooleanArray array = BooleanArray.falses(3, 3, 3);
    array.set(new int[] {0, 0, 0}, true);
    array.set(new int[] {0, 1, 0}, true);
    array.set(new int[] {0, 2, 0}, true);
    array.set(new int[] {0, 0, 1}, true);

    BooleanArray expected_0 = BooleanArray.falses(3, 3);
    BooleanArray expected_1 = BooleanArray.falses(3, 3);
    expected_1.set(0, 0, true);
    BooleanArray expected_2 = BooleanArray.falses(3, 3);

    assertEquals(expected_0, array.all(0));
    assertEquals(expected_1, array.all(1));
    assertEquals(expected_2, array.all(2));
  }

  @Test
  public void testAll() throws Exception {
    assertEquals(false, BooleanArray.of(true, false, true, false).all());
    assertEquals(true, BooleanArray.of(true, true, true).all());
  }

}
