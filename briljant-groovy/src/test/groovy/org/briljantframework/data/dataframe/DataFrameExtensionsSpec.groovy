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

import org.briljantframework.data.vector.Vector
import spock.lang.Specification

/**
 * Created by isak on 04/06/15.
 */
class DataFrameExtensionsSpec extends Specification {

  def "getAt returns the correct type"() {
    when:
//    def df = MixedDataFrame.of(
//        "a", Vector.of([1, 2, 3, 4]),
//        "b", Vector.of(["a","b","q","f"])
//    )
    DataFrame df = MixedDataFrame.create([
        a: Vector.of([1, 1, 1, 2]),
        b: Vector.of([1, 2, 3, 4]),
        c: Vector.of(["1", "3", "10", "g"])
    ])
    then:
    df.get("a")[0] == 1
  }
}