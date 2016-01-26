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
