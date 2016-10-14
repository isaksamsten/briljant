/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.data.parser;

import static org.junit.Assert.assertEquals;

import org.briljantframework.data.Collectors;
import org.briljantframework.data.SortOrder;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.series.Series;
import org.junit.Test;

public class SqlParserTest {

  @Test
  public void testParse() throws Exception {
    String url = "jdbc:sqlite::resource:org/briljantframework/data/chinook.db";
    String query =
        "SELECT ab.Title, a.Name FROM Album AS ab, Artist AS a WHERE ab.ArtistId = a.ArtistId";
    SqlParser parser = new SqlParser(url, query);
    SqlParser.Settings settings = parser.getSettings();
    settings.remap("Title", "MyTitle");
    settings.remap("Name", "Artist");

    DataFrame df = parser.parse();
    System.out.println(df);
    System.out.println(df.groupBy("Artist").collect(Object.class, Collectors.count()));
    System.out.println(df.get("Artist").valueCounts().sort(SortOrder.DESC));
    // for (int i = 0; i < sort.rows(); i++) {
    // System.out.println(sort.loc().getRecord(i));
    // }

    assertEquals(2, df.columns());

  }
}
