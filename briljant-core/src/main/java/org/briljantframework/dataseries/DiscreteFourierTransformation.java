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

package org.briljantframework.dataseries;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.Check;
import org.briljantframework.array.ComplexArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.dataframe.transform.InvertibleTransformation;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import static org.briljantframework.math.transform.DiscreteFourierTransform.fft;
import static org.briljantframework.math.transform.DiscreteFourierTransform.ifft;

/**
 * @author Isak Karlsson
 */
public class DiscreteFourierTransformation implements InvertibleTransformation {

  /**
   * Asserts that each row has {@link org.briljantframework.vector.DoubleVector#TYPE}.
   *
   * @param x data frame to transform
   * @return a new data frame; each row has type
   */
  @Override
  public DataFrame transform(DataFrame x) {
    DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(Complex.class);
    for (Vector row : x) {
      Check.type(row, DoubleVector.TYPE);
      DoubleArray timeDomain = row.toDoubleArray();
      ComplexArray frequencyDomain = fft(timeDomain);
      Vector.Builder rowBuilder = VectorType.from(Complex.class).newBuilder(timeDomain.size());
      for (int i = 0; i < frequencyDomain.size(); i++) {
        rowBuilder.loc().set(i, frequencyDomain.get(i));
      }
      builder.addRecord(rowBuilder);
    }
    return builder.build();
  }

  @Override
  public DataFrame inverseTransform(DataFrame x) {
    DataSeriesCollection.Builder builder = new DataSeriesCollection.Builder(DoubleVector.TYPE);
    for (Vector row : x) {
      Check.type(row, VectorType.from(Complex.class));
      ComplexArray timeDomain = row.toComplexArray();
      DoubleArray frequencyDomain = ifft(timeDomain).asDouble();
      DoubleVector.Builder rowBuilder = new DoubleVector.Builder(0, frequencyDomain.size());
      for (int i = 0; i < frequencyDomain.size(); i++) {
        rowBuilder.loc().set(i, frequencyDomain.get(i));
      }
      builder.addRecord(rowBuilder);
    }
    return builder.build();
  }
}
