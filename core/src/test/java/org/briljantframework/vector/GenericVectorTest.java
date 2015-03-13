package org.briljantframework.vector;

import org.briljantframework.exceptions.TypeConversionException;
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
    dateVector =
        new GenericVector(Date.class,
                          Arrays.asList(firstDate, new Date(System.currentTimeMillis() + 199999),
                                        new Date(), null));
    cmpVector = new GenericVector(Comparable.class, Arrays.asList(1, 2, 3, 3.2));
    charVector = new GenericVector(Character.class, Arrays.asList('a', 'b', 'c'));
  }

  @Test
  public void testGet() throws Exception {
    Value dateValue = dateVector.get(0);
    assertEquals(firstDate, dateValue.getAs(Date.class));
  }

  @Test
  public void testGetAs() throws Exception {
    Date date = dateVector.getAs(Date.class, 0);
    Comparable cmp = cmpVector.getAs(Comparable.class, 1);
    Character c = charVector.getAs(Character.class, 2);

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
    assertEquals(DoubleVector.NA, dateVector.getAsDouble(0), 0);

  }

  @Test
  public void testGetAsInt() throws Exception {
    assertEquals(IntVector.NA, dateVector.getAsInt(0));

  }

  @Test
  public void testGetAsBit() throws Exception {
    assertEquals(BitVector.NA, dateVector.getAsBit(0));

  }

  @Test
  public void testGetAsString() throws Exception {
    assertEquals(firstDate.toString(), dateVector.getAsString(0));
  }

  @Test
  public void testSize() throws Exception {
    assertEquals(4, dateVector.size());
  }

  @Test
  public void testGetType() throws Exception {

  }

  @Test(expected = TypeConversionException.class)
  public void testAsMatrix() throws Exception {
    dateVector.asMatrix();
  }

  @Test
  public void testCompare() throws Exception {
    assertEquals(-1, dateVector.compare(0, 1));
    assertEquals(-1, cmpVector.compare(0, 1));
  }

  @Test(expected = ClassCastException.class)
  public void testCompare1() throws Exception {
    dateVector.compare(0, cmpVector, 1);
  }
}