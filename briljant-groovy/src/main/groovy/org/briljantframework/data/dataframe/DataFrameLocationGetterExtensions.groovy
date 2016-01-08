/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
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
package org.briljantframework.data.dataframe

import groovy.transform.CompileStatic
import org.briljantframework.data.index.DataFrameLocationGetter
import org.briljantframework.data.vector.Vector

/**
 * Extensions to support common Groovy idioms when working with {@link DataFrameLocationGetter data frame location getters}.
 *
 * @author Isak Karlsson
 */
@CompileStatic
class DataFrameLocationGetterExtensions {

  /**
   * Get the vector at the specified location
   *
   * @param self the data frame
   * @param column the column location
   * @return a vector
   */
  static Vector getAt(DataFrameLocationGetter self, int column) {
    return self.get(column)
  }

  /**
   * Get the value at the specified location
   *
   * @param self the data frame
   * @param row the row
   * @param column the column
   * @return a value
   */
  static Object getAt(DataFrameLocationGetter self, int row, int column) {
    return self.get(Object, row, column)
  }
}
