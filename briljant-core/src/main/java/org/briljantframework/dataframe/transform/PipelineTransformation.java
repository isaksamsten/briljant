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

package org.briljantframework.dataframe.transform;

import java.util.Arrays;
import java.util.List;

import org.briljantframework.dataframe.DataFrame;

/**
 * Created by Isak Karlsson on 17/12/14.
 */
public class PipelineTransformation implements Transformation {

  private final List<Transformation> transformations;

  public PipelineTransformation(List<Transformation> transformations) {
    this.transformations = transformations;
  }

  public static PipelineTransformation of(Transformation... transformations) {
    return new PipelineTransformation(Arrays.asList(transformations));
  }

  @Override
  public DataFrame transform(DataFrame x) {
    for (Transformation transformation : transformations) {
      x = transformation.transform(x);
    }
    return x;
  }
}
