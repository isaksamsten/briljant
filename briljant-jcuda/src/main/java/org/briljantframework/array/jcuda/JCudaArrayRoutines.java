/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.array.jcuda;

import static jcuda.jcublas.cublasOperation.CUBLAS_OP_N;

import jcuda.Pointer;
import jcuda.jcublas.JCublas2;

import org.briljantframework.array.ArrayOperation;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.array.api.ArrayBackend;
import org.briljantframework.array.base.BaseArrayRoutines;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public class JCudaArrayRoutines extends BaseArrayRoutines {

  protected JCudaArrayRoutines(ArrayBackend backend) {
    super(backend);
  }

  @Override
  public double norm2(DoubleArray a) {
    if (a instanceof JCudaDoubleArray) {
      try (JCudaContext context = new JCudaContext()) {
        // Pointer x = context.allocate(a.data(), 1);
        double[] data = new double[1];
        JCublas2.cublasDnrm2(context.getHandle(), a.size(),
                             ((JCudaDoubleArray) a).getDeviceMemory(), 1, Pointer.to(data));
        return data[0];
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

    return super.norm2(a);

  }

  @Override
  public void gemm(ArrayOperation transA, ArrayOperation transB, double alpha, DoubleArray a,
                   DoubleArray b, double beta, DoubleArray c) {
    if (a instanceof JCudaDoubleArray && b instanceof JCudaDoubleArray
        && c instanceof JCudaDoubleArray) {
      try (JCudaContext context = new JCudaContext()) {
        int m = a.size(transA == ArrayOperation.KEEP ? 0 : 1);
        int n = b.size(transB == ArrayOperation.KEEP ? 1 : 0);
        int k = a.size(transA == ArrayOperation.KEEP ? 1 : 0);
        if (m != c.size(0) || n != c.size(1)) {
          throw new IllegalArgumentException(String.format(
              "a has size (%d,%d), b has size (%d,%d), c has size (%d, %d)", m, k, k, n, c.size(0),
              c.size(1)));
        }

        Pointer deviceAlpha = Pointer.to(new double[]{alpha});
        Pointer deviceBeta = Pointer.to(new double[]{beta});
        JCublas2.cublasDgemm(context.getHandle(), CUBLAS_OP_N, CUBLAS_OP_N, m, n, k, deviceAlpha,
                             ((JCudaDoubleArray) a).getDeviceMemory(), Math.max(1, a.stride(1)),
                             ((JCudaDoubleArray) b).getDeviceMemory(), Math.max(1, b.stride(1)),
                             deviceBeta, ((JCudaDoubleArray) c).getDeviceMemory(),
                             Math.max(1, c.stride(1)));
      } catch (Exception e) {
        throw new RuntimeException(e);
      }
    }

//    super.gemm(transA, transB, alpha, a, b, beta, c);
  }
}
