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

package org.briljantframework.data.reader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class SqlEntryReaderTest {

  private Connection connection;

  @Before
  public void setUp() throws Exception {
    connection = DriverManager.getConnection(
        "jdbc:sqlite::resource:org/briljantframework/data/reader/chinook.db"
    );

  }

  @After
  public void tearDown() throws Exception {
    connection.close();
  }

  @Test
  public void testReadAll() throws Exception {
    PreparedStatement stmt = connection.prepareStatement("SELECT * FROM Album");
    ResultSet resultSet = stmt.executeQuery();
    EntryReader reader = new SqlEntryReader(resultSet);
    DataEntry entry = reader.next();

    assertEquals(1, entry.nextInt());
    assertEquals("For Those About To Rock We Salute You", entry.nextString());
    assertEquals(1, entry.nextInt());
    List<Class<?>> classes = reader.getTypes();
    assertArrayEquals(
        new Class<?>[]{Integer.class, String.class, Integer.class},
        classes.toArray(new Class<?>[classes.size()])
    );

  }
}