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

package org.briljantframework.evaluation;

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.vector.Vector;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * The split partitioner simply partitions the input {@code DataFrame} (with {@code m rows}) and
 * {@code Vector} (of length {@code m}) into two parts (according to {@code testFraction} (in the
 * range {@code [0, 1]}).
 *
 * <p>The training partition is of size {@code m*(1-testFraction)} and the validation partition of
 * size {@code m*testFraction}
 *
 * @author Isak Karlsson
 */
public class SplitPartitioner implements Partitioner {

  private final double testFraction;

  public SplitPartitioner(double testFraction) {
    this.testFraction = testFraction;
  }

  @Override
  public Iterable<Partition> partition(DataFrame x, Vector y) {
    return () -> {
      int trainingSize = x.rows() - (int) Math.round(x.rows() * testFraction);

      DataFrame.Builder xTrainingBuilder = x.newBuilder();
      Vector.Builder yTrainingBuilder = y.newBuilder();
      for (int i = 0; i < trainingSize; i++) {
        for (int j = 0; j < x.columns(); j++) {
          xTrainingBuilder.set(i, j, x, i, j);
        }
        yTrainingBuilder.add(y, i);
      }

      DataFrame.Builder xValidationBuilder = x.newBuilder();
      Vector.Builder yValidationBuilder = y.newBuilder();
      int index = 0;
      for (int i = trainingSize; i < x.rows(); i++) {
        for (int j = 0; j < x.columns(); j++) {
          xValidationBuilder.set(index, j, x, i, j);
        }
        yValidationBuilder.add(y, i);
        index += 1;
      }

      return new Iterator<Partition>() {
        private boolean has = true;

        @Override
        public boolean hasNext() {
          return has;
        }

        @Override
        public Partition next() {
          if (!hasNext()) {
            throw new NoSuchElementException();
          }
          has = false;
          return new Partition(xTrainingBuilder.build(), xValidationBuilder.build(),
                               yTrainingBuilder.build(), yValidationBuilder.build());
        }
      };
    };
  }
}
