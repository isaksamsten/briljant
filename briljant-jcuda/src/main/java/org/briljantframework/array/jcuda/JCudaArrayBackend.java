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
package org.briljantframework.array.jcuda;

import org.briljantframework.array.ArrayOperation;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.array.api.ArrayFactory;
import org.briljantframework.array.api.ArrayRoutines;
import org.briljantframework.array.netlib.NetlibArrayBackend;
import org.briljantframework.array.api.LinearAlgebraRoutines;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class JCudaArrayBackend implements ArrayBackend {

  private final Object lock = new Object();
  private ArrayFactory factory = null;
  private ArrayRoutines routines = null;
  private static ArrayBackend INSTANCE = new JCudaArrayBackend();

  private JCudaArrayBackend() {
    if (INSTANCE != null) {
      throw new UnsupportedOperationException("Already initialized");
    }
    factory = new JCudaArrayFactory(this);
    routines = new JCudaArrayRoutines(this);
  }



  public static void main(String[] args) {
    int size = 1000;
    time(NetlibArrayBackend.getInstance(), size);
    time(JCudaArrayBackend.getInstance(), size);
  }

  private static void time(ArrayBackend backend, int size) {
    ArrayRoutines routines = backend.getArrayRoutines();
    ArrayFactory factory = backend.getArrayFactory();

    DoubleArray a = factory.newDoubleArray(size, size);
    DoubleArray b = factory.newDoubleArray(size, size);
    DoubleArray c = factory.newDoubleArray(size, size);
    for (int i = 0; i < a.size(); i++) {
      a.set(i, i);
      b.set(i, i);
    }
    double value = 0;
    long start = System.currentTimeMillis();
    for (int i = 0; i < 100; i++) {
      routines.gemm(ArrayOperation.KEEP, ArrayOperation.KEEP, 1, a, b, 1, c);
    }
    System.out.println(c);

    System.out.println((System.currentTimeMillis() - start) + "  " + value);
  }

  public static ArrayBackend getInstance() {
    return INSTANCE;
  }

  @Override
  public ArrayFactory getArrayFactory() {
    return factory;
  }

  @Override
  public ArrayRoutines getArrayRoutines() {
    return routines;
  }

  @Override
  public LinearAlgebraRoutines getLinearAlgebraRoutines() {
    return null;
  }
}
