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

package org.briljantframework.data.dataframe

import org.briljantframework.data.parser.CsvParser
import org.briljantframework.data.parser.SqlParser

/**
 * @author Isak Karlsson
 */
class DataFrameStaticExtensions {

  /**
   * Read a {@link File}, {@link String file path}, {@link InputStream input stream} or {@link Reader} and constructs a data frame.
   *
   * <pre>
   * DataFrame.readCSV("test.csv") {*   delimiter = ','
   *   header = ["First", "Second", "Third"]
   * </pre>
   *
   * @param self a dataframe
   * @param fileOrPath a file, reader or path
   * @param closure a closure for providing additional settings
   * @return a data frame
   * @see CsvParser
   * @see CsvParser.Settings
   */
  static DataFrame readCSV(DataFrame self, @DelegatesTo(CsvParser.Settings) Closure closure = {}) {
    def parser = new CsvParser()
    parser.settings.with closure
    return parser.parse()
  }

  static DataFrame readSQL(DataFrame self, @DelegatesTo(SqlParser.Settings) Closure closure = {}) {
    def parser = new SqlParser()
    parser.settings.with closure
    return parser.parse()
  }

}
