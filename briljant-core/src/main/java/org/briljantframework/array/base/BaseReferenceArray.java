package org.briljantframework.array.base;

import org.briljantframework.array.AbstractArray;
import org.briljantframework.array.Array;
import org.briljantframework.array.Indexer;
import org.briljantframework.array.api.ArrayFactory;

/**
 * @author Isak Karlsson
 */
class BaseReferenceArray<T> extends AbstractArray<T> {

  private final T[] data;

  BaseReferenceArray(ArrayFactory bj, T[] data) {
    this(bj, new int[]{data.length}, data);
  }

  BaseReferenceArray(ArrayFactory bj, int[] shape, T[] data) {
    super(bj, shape);
    this.data = data;
  }

  BaseReferenceArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride,
                     T[] data) {
    super(bj, offset, shape, stride, majorStride);
    this.data = data;
  }

  @SuppressWarnings("unchecked")
  public BaseReferenceArray(BaseArrayFactory bj, int[] shape) {
    super(bj, shape);
    this.data = (T[]) new Object[Indexer.size(shape)];
  }

  @Override
  protected T getElement(int i) {
    return data[i];
  }

  @Override
  protected void setElement(int i, T value) {
    data[i] = value;
  }

  @Override
  protected int elementSize() {
    return data.length;
  }

  @Override
  public Array<T> asView(int offset, int[] shape, int[] stride, int majorStride) {
    return new BaseReferenceArray<T>(getArrayFactory(), offset, shape, stride, majorStride, data);
  }

  @Override
  public Array<T> newEmptyArray(int... shape) {
    @SuppressWarnings("unchecked") T[] data = (T[]) new Object[Indexer.size(shape)];
    return new BaseReferenceArray<>(getArrayFactory(), shape, data);
  }

  @Override
  public T[] data() {
    return data;
  }
}
