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

package org.briljantframework.dataframe

import org.briljantframework.vector.Na
import org.briljantframework.vector.Vec
import org.briljantframework.vector.VectorType
import spock.lang.Specification
import spock.lang.Unroll

/**
 * Created by isak on 30/07/15.
 */
class DataFrameSpec extends Specification {

  def "a data frame has rows and columns"() {
    given:
    def c = getBuilder(String, int, Integer)
        .set(0, 0, "hello world")
        .set(2, 1, 3)
        .set(1, 2, 10)
        .build()

    def result = createDataFrame(
        [["hello world", null, null],
         [null, null, 10],
         [null, 3, null]],
        String, int, Integer
    )

    expect:
    c.rows() == 3
    c.columns() == 3
    c == result
  }

  def "getAsDouble returns a double value"() {
    given:
    def c = createDataFrame(
        [["hello", 1, 32.2],
         ["aa", 2, Na.from(double)]],
        String, int, double)

    expect:
    c.getAsDouble(0, 0) == Na.from(double)
    c.getAsDouble(1, 1) == 2.0
    c.getAsDouble(0, 2) == 32.2
    c.getAsDouble(1, 2) == Na.from(double)
  }

  def "getAsInt returns an int value"() {
    given:
    def c = createDataFrame(
        [["hello", 1, 32.2],
         ["aa", 2, Na.from(double)]],
        String, int, double)

    expect:
    c.getAsInt(0, 0) == Na.from(int)
    c.getAsInt(1, 1) == 2
    c.getAsInt(0, 2) == 32
    c.getAsInt(1, 2) == Na.from(int)
  }

  @Unroll
  def "head returns the #n first rows"() {
    given:
    def df = createDataFrame([[1, 2], [3, 4], [5, 6], [7, 8], [9, 10]], int, int)

    expect:
    df.head(n) == result

    where:
    n << [0, 1, 2, 3, 5]
    result << [
        createDataFrame([[]], int, int),
        createDataFrame([[1, 2]], int, int),
        createDataFrame([[1, 2], [3, 4]], int, int),
        createDataFrame([[1, 2], [3, 4], [5, 6]], int, int),
        createDataFrame([[1, 2], [3, 4], [5, 6], [7, 8], [9, 10]], int, int)
    ]
  }

  def createDataFrame(List<List> values, Class<?>... types) {
    DataFrame.Builder builder = getBuilder(types)
    for (int i = 0; i < values.size(); i++) {
      List row = values[i]
      for (int j = 0; j < row.size(); j++) {
        builder.set(i, j, row[j])
      }
    }
    return builder.build()
  }

  def getBuilder(Class<?>... types) {
    return new MixedDataFrame.Builder(types.collect {Vec.typeOf(it)} as VectorType[])
  }
}