/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
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
package org.briljantframework.array;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import org.briljantframework.array.api.ArrayFactory;
import org.junit.Test;

import java.util.*;

/**
 * @author Isak Karlsson
 */
public abstract class DoubleArrayTest extends AbstractBaseArrayTest<DoubleArray> {

  protected abstract ArrayFactory getArrayFactory();

  @Override
  protected ArrayTest<DoubleArray> createSetSingleIndexTest() {
    return new ArrayTest<DoubleArray>() {
      @Override
      public List<DoubleArray> create() {
        // @formatter:off
        return java.util.Arrays.asList(
            getArrayFactory().newDoubleVector(1,2,3,4),
            getArrayFactory().newDoubleVector(5,6,7,8),

            getArrayFactory().newDoubleVector(1,3,2,4).reshape(2,2),
            getArrayFactory().newDoubleVector(5,7,6,8).reshape(2,2),

            getArrayFactory().newDoubleVector(1,3,2,4).reshape(2,1,2),
            getArrayFactory().newDoubleVector(5,7,6,8).reshape(2,1,2)
        );
        // @formatter:on
      }

      @Override
      public void test(List<DoubleArray> actual) {
        assertEquals(5, actual.get(0).get(1),0);
        assertEquals(5, actual.get(1).get(0, 1),0);
        assertEquals(5, actual.get(2).get(0, 0, 1),0);
      }
    };

  }

  @Override
  protected ArrayTest<DoubleArray> createSetMatrixIndexTest() {
    return new ArrayTest<DoubleArray>() {
      @Override
      public List<DoubleArray> create() {
        //@formatter:off
        return java.util.Arrays.asList(
            getArrayFactory().newDoubleVector(1,2,3,4),

            getArrayFactory().newDoubleVector(1,2,3,4,5,6,7,8).reshape(2,4),
            getArrayFactory().newDoubleArray(2,4),

            getArrayFactory().newDoubleArray(2,1,2)
        );
        //@formatter:on
      }

      @Override
      public void test(List<DoubleArray> actual) {
        for (int i = 0; i < actual.get(0).size(); i++) {
          assertEquals(0, actual.get(0).get(i),0);
        }
      }
    };
  }
  
  @Override
  protected ArrayTest<DoubleArray> createAssignTest() {
    return new ArrayTest<DoubleArray>() {
      @Override
      public List<DoubleArray> create() {
        //@formatter:off
        return java.util.Arrays.asList(
            getArrayFactory().newDoubleArray(3,3,3),
            getArrayFactory().newDoubleVector(10),
            
            getArrayFactory().newDoubleArray(1,3,4),
            getArrayFactory().newDoubleVector(1,2,3,4)
        );
        //@formatter:on
      }

      @Override
      public void test(List<DoubleArray> actual) {
        DoubleArray first = actual.get(0);
        assertArrayEquals(new int[] {3, 3, 3}, first.getShape());
        for (int i = 0; i < first.size(); i++) {
          assertEquals(10, first.get(10),0);
        }

        DoubleArray second = actual.get(1);
        assertArrayEquals(new int[] {1, 3, 4}, second.getShape());
        for (int i = 0; i < second.vectors(2); i++) {
          DoubleArray vector = second.getVector(2, i);
          assertEquals(vector, getArrayFactory().newDoubleVector(1, 2, 3, 4));
        }
      }
    };
  }

}
