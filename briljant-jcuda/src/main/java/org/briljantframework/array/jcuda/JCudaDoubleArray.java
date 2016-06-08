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

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.jcublas.JCublas2;
import jcuda.runtime.JCuda;

import org.briljantframework.array.AbstractDoubleArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.api.ArrayBackend;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class JCudaDoubleArray extends AbstractDoubleArray {

  private double[] hostMemory = null;
  private Pointer deviceMemory = null;
  private boolean dirty = false;

  protected JCudaDoubleArray(ArrayBackend bj, int[] shape) {
    super(bj, shape);
  }

  protected JCudaDoubleArray(ArrayBackend bj, int offset, int[] shape, int[] stride) {
    super(bj, offset, shape, stride);
  }

  public JCudaDoubleArray(ArrayBackend arrayBackend, int offset, int[] shape, int[] stride, double[] hostMemory, Pointer deviceMemory) {
    super(arrayBackend, offset, shape, stride);
    this.hostMemory = hostMemory;
    this.deviceMemory = deviceMemory;
  }

  @Override
  protected double getElement(int i) {
    allocateHostMemory();
    return hostMemory[i];
  }

  @Override
  protected void setElement(int i, double value) {
    // allocateHostMemory();
    if (hostMemory == null) {
      hostMemory = new double[size()];
    }
    dirty = true;
    hostMemory[i] = value;
  }

  private void allocateHostMemory() {
    if (hostMemory == null) {
      hostMemory = new double[size()];
      if (deviceMemory != null) {
        // TODO: 20/01/16 register this for de-allocation
        JCublas2.cublasGetVector(size(), Sizeof.DOUBLE, deviceMemory, stride(0),
            Pointer.to(hostMemory), 1);
        dirty = false;
      } else {
        dirty = true;
      }
    }

    if (hostMemory != null && dirty) {
      if (deviceMemory == null) {
        deviceMemory = Pointer.to(hostMemory);
        JCuda.cudaMalloc(deviceMemory, size() * Sizeof.DOUBLE);
      }
      JCublas2.cublasSetVector(size(), Sizeof.DOUBLE, Pointer.to(hostMemory), stride(0),
          deviceMemory, 1);
      dirty = false;
    }
  }

  @Override
  protected int elementSize() {
    allocateHostMemory();
    return hostMemory.length;
  }

  private double[] data() {
    allocateHostMemory();
    return hostMemory;
  }

  public Pointer getDeviceMemory() {
    allocateHostMemory();
    return deviceMemory;
  }

  @Override
  public DoubleArray asView(int offset, int[] shape, int[] stride) {
    return new JCudaDoubleArray(getArrayBackend(), offset, shape, stride, hostMemory,
        deviceMemory);
  }

  @Override
  public DoubleArray newEmptyArray(int... shape) {
    return new JCudaDoubleArray(getArrayBackend(), getShape());
  }
}
