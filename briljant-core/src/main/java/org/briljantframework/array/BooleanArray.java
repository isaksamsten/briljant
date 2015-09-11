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

package org.briljantframework.array;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * @author Isak Karlsson
 */
public interface BooleanArray extends BaseArray<BooleanArray> {

  BooleanArray assign(Supplier<Boolean> supplier);

  BooleanArray assign(boolean value);

  void set(int index, boolean value);

  void set(int i, int j, boolean value);

  void set(int[] index, boolean value);

  boolean get(int index);

  boolean get(int i, int j);

  boolean get(int... index);

  BooleanArray xor(BooleanArray other);

  BooleanArray or(BooleanArray other);

  BooleanArray orNot(BooleanArray other);

  BooleanArray and(BooleanArray other);

  BooleanArray andNot(BooleanArray other);

  BooleanArray not();

  BooleanArray add(BooleanArray o);

  BooleanArray sub(BooleanArray o);

  BooleanArray mul(BooleanArray o);

  BooleanArray div(BooleanArray o);

  BooleanArray mmul(BooleanArray o);

  Stream<Boolean> stream();

  List<Boolean> asList();
}
