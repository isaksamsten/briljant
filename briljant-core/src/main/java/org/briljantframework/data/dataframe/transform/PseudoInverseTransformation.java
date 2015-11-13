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

package org.briljantframework.data.dataframe.transform;

import org.briljantframework.array.Arrays;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.dataframe.DataFrame;


/**
 * Transforms a frame to it's inverse
 * <p>
 * Created by Isak Karlsson on 11/08/14.
 */
public class PseudoInverseTransformation implements Transformation {

  @Override
  public Transformer fit(DataFrame container) {
    return new PinvTransformer();
  }

  private static class PinvTransformer implements Transformer {

    @Override
    public DataFrame transform(DataFrame x) {
      DoubleArray matrix = Arrays.linalg.pinv(x.toDoubleArray());
      DataFrame.Builder builder = x.newBuilder();
      for (int j = 0; j < x.columns(); j++) {
        for (int i = 0; i < x.rows(); i++) {
          builder.loc().set(i, j, matrix.get(i, j));
        }
      }
      return builder.build();
    }
  }
}
