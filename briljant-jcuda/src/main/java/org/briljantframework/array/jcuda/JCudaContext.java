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

import static jcuda.jcublas.JCublas2.cublasSetVector;
import static jcuda.runtime.JCuda.cudaMalloc;

import java.util.ArrayList;
import java.util.List;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas2;
import jcuda.jcublas.cublasHandle;
import jcuda.runtime.JCuda;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class JCudaContext implements AutoCloseable {

  private final cublasHandle handle;
  private final List<Pointer> pointers = new ArrayList<>();

  public JCudaContext() {
    handle = new cublasHandle();
    JCublas2.cublasCreate(handle);
  }

  public cublasHandle getHandle() {
    return handle;
  }

  public Pointer allocate(double[] data, int inc) {
    Pointer devicePointer = new Pointer();
    int x = cudaMalloc(devicePointer, data.length * Sizeof.DOUBLE);
    cublasSetVector(data.length, Sizeof.DOUBLE, Pointer.to(data), 1, devicePointer, 1);
    pointers.add(devicePointer);
    return devicePointer;
  }

  public void deallocate(Pointer pointer, double[] data, int inc) {
    System.out.println(pointer);
    JCublas2.cublasGetVector(data.length, Sizeof.DOUBLE, pointer, 1, Pointer.to(data), inc);
  }

  @Override
  public void close() throws Exception {
    JCublas2.cublasDestroy(handle);
    for (Pointer pointer : pointers) {
      JCuda.cudaFree(pointer);
    }
  }
}
