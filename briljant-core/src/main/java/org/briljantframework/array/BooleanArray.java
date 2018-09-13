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

import java.util.Collection;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

/**
 * A n-dimensional array of complex values.
 * 
 * @author Isak Karlsson
 */
public interface BooleanArray extends BaseArray<BooleanArray>, Collection<Boolean> {

  static BooleanArray trues(int... shape) {
    BooleanArray array = falses(shape);
    array.assign(true);
    return array;
  }

  /**
   * Return a boolean array of the specifier shape filled with false.
   * 
   * @param shape the shape
   * @return a boolean array
   */
  static BooleanArray falses(int... shape) {
    return Arrays.booleanArray(shape);
  }

  DoubleArray doubleArray();

  IntArray intArray();

  LongArray longArray();

  BooleanArray booleanArray();

  ComplexArray asComplexArray();

  /**
   * Assign the scalar value
   *
   * @param value the value
   */
  void assign(boolean value);

  /**
   * @see Arrays#booleanVector(boolean[])
   */
  static BooleanArray of(boolean... data) {
    return Arrays.booleanVector(data);
  }

  static BooleanArray of(int... data) {
    BooleanArray array = falses(data.length);
    for (int i = 0; i < data.length; i++) {
      array.set(i, data[i] == 1);
    }
    return array;
  }

  static BooleanArray copyOf(Collection<Boolean> collection) {
    BooleanArray array = falses(collection.size());
    int i = 0;
    for (Boolean bool : collection) {
      array.set(i++, bool);
    }
    return array;
  }

  void set(int index, boolean value);

  /**
   * Assign the value of successive cals to the supplier
   *
   * @param supplier the supplier
   */
  void assign(Supplier<Boolean> supplier);

  void set(int i, int j, boolean value);

  void set(int[] index, boolean value);

  boolean get(int index);

  boolean get(int i, int j);

  boolean get(int... index);

  BooleanArray map(Function<Boolean, Boolean> mapper);

  void apply(UnaryOperator<Boolean> operator);

  BooleanArray not();

  /**
   * Perform a reduction using the initial value and the given accumulator
   * 
   * @param identity the initial value
   * @param accumulator the accumulator
   * @return an accumulated value
   * @see Stream#reduce(Object, BinaryOperator)
   */
  boolean reduce(boolean identity, BinaryOperator<Boolean> accumulator);

  BooleanArray reduceAlong(int dim, Function<? super BooleanArray, Boolean> function);

  /**
   * Return a boolean array of the test if a given element along a specified dimension evaluates to
   * true
   * 
   * @param dim the dimension
   * @return a new boolean array
   */
  BooleanArray any(int dim);

  /**
   * Test whether an element in the array evaluates to to true
   *
   * @return a boolean
   */
  boolean any();

  /**
   * Test whether all elements along a specified dimension evaluates to true
   *
   * @param dim the dimension
   * @return a new array
   */
  BooleanArray all(int dim);

  /**
   * Test whether all elements evaluates to true
   * 
   * @return a boolean
   */
  boolean all();

  Array<Boolean> boxed();

  Stream<Boolean> stream();

}
