/*
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

package org.briljantframework.vector;

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class GenericVectorTest {

  private Vector dateVector;
  private Vector cmpVector;
  private Vector charVector;
  private Date firstDate;

  @Before
  public void setUp() throws Exception {
    firstDate = new Date();
    dateVector = new GenericVector(Date.class, Arrays.asList(
        firstDate, new Date(System.currentTimeMillis() + 199999), new Date(), null
    ));
    cmpVector = new GenericVector(Comparable.class, Arrays.asList(1, 2, 3, 3.2));
    charVector = new GenericVector(Character.class, Arrays.asList('a', 'b', 'c'));
  }

  @Test
  public void testGetScale() throws Exception {
    assertEquals(Scale.NOMINAL, cmpVector.getScale());
  }

  @Test
  public void testGet() throws Exception {
    Date dateValue = dateVector.get(Date.class, 0);
    assertEquals(firstDate, dateValue);
  }

  @Test
  public void testGetAs() throws Exception {
    Date date = dateVector.get(Date.class, 0);
    Comparable cmp = cmpVector.get(Comparable.class, 1);
    Character c = charVector.get(Character.class, 2);

    assertEquals(date, firstDate);
    assertEquals(cmp, 2);
    assertEquals('c', (char) c);
  }

  @Test
  public void testToString() throws Exception {
    String dateValue = dateVector.toString(0);
    assertEquals(firstDate.toString(), dateValue);
  }

  @Test
  public void testIsNA() throws Exception {
    assertTrue(dateVector.isNA(3));
    assertFalse(dateVector.isNA(2));
  }

  @Test
  public void testGetAsDouble() throws Exception {
    assertEquals(Na.DOUBLE, dateVector.getAsDouble(0), 0);

  }

  @Test
  public void testGetAsInt() throws Exception {
    assertEquals(Na.INT, dateVector.getAsInt(0));

  }

  @Test
  public void testGetAsBit() throws Exception {
    assertEquals(Logical.NA, dateVector.get(Logical.class, 0));
  }

  @Test
  public void testSize() throws Exception {
    assertEquals(4, dateVector.size());
  }

  @Test
  public void testGetType() throws Exception {

  }

  @Test
  public void testCompare() throws Exception {
    assertEquals(-1, dateVector.compare(0, 1));
    assertEquals(-1, cmpVector.compare(0, 1));
  }

}