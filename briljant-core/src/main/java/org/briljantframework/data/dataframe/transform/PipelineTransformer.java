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

import org.briljantframework.data.dataframe.DataFrame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Isak Karlsson
 */
public class PipelineTransformer implements Transformer {

  private final List<Transformer> transformers;

  private PipelineTransformer(List<Transformer> transformers) {
    this.transformers = transformers;
  }

  public static Transformer of(Transformer... transformers) {
    if (transformers.length < 1) {
      throw new IllegalArgumentException(
          "Cannot construct a PipelineTransformer without transformers");
    }

    return new PipelineTransformer(Arrays.asList(transformers));
  }

  @Override
  public Transformation fit(DataFrame dataFrame) {
    List<Transformation> transformations = new ArrayList<>();
    for (Transformer transformer : transformers) {
      Transformation transformation = transformer.fit(dataFrame);
      dataFrame = transformation.transform(dataFrame);
      transformations.add(transformation);
    }
    return new PipelineTransformation(transformations);
  }

}
