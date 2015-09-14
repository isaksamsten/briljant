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

package org.briljantframework.data.dataframe.transform;

import org.briljantframework.data.Na;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.dataframe.MixedDataFrame;
import org.briljantframework.data.dataframe.ObjectIndex;
import org.briljantframework.data.vector.Vector;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MeanImputerTest {

  @Test
  public void testFit() throws Exception {
    DataFrame frame = MixedDataFrame.create(
        Vector.of(1.0, 2, 3, Na.DOUBLE),
        Vector.of(3.0, 3, 3, Na.DOUBLE),
        Vector.of(Na.DOUBLE, 2, 2, Na.DOUBLE)
    );
    ObjectIndex columnIndex = ObjectIndex.of("first", "second", "third");
    ObjectIndex index = ObjectIndex.of("a", "b", "c", "d");
    frame.setColumnIndex(columnIndex);
    frame.setIndex(index);

    MeanImputer imputer = new MeanImputer();
    Transformation t = imputer.fit(frame);
    DataFrame imputed = t.transform(frame);

    assertEquals(columnIndex, imputed.getColumnIndex());
    assertEquals(index, imputed.getIndex());
    assertEquals(2, imputed.loc().getAsDouble(3, 0), 0.0);
  }
}