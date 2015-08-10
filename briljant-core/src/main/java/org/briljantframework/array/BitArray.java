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
public interface BitArray extends BaseArray<BitArray> {

  BitArray assign(Supplier<Boolean> supplier);

  BitArray assign(boolean value);

  void set(int index, boolean value);

  void set(int i, int j, boolean value);

  void set(int[] index, boolean value);

  boolean get(int index);

  boolean get(int i, int j);

  boolean get(int... index);

  BitArray xor(BitArray other);

  BitArray or(BitArray other);

  BitArray orNot(BitArray other);

  BitArray and(BitArray other);

  BitArray andNot(BitArray other);

  BitArray not();

  BitArray add(BitArray o);

  BitArray sub(BitArray o);

  BitArray mul(BitArray o);

  BitArray div(BitArray o);

  BitArray mmul(BitArray o);

  Stream<Boolean> stream();

  List<Boolean> asList();
}
