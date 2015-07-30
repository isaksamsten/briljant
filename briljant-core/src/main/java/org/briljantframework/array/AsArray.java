package org.briljantframework.array;

import org.briljantframework.array.api.ArrayFactory;

/**
 * @author Isak Karlsson
 */
abstract class AsArray<T> extends AbstractArray<T> {

  AsArray(ArrayFactory bj, int offset, int[] shape,
          int[] stride, int majorStride) {
    super(bj, offset, shape, stride, majorStride);
  }

  AsArray(AbstractBaseArray<?> array) {
    super(array.getArrayFactory(), array.getOffset(), array.getShape(), array.getStride(),
          array.getMajorStrideIndex());
  }

  @Override
  public Array<T> asView(int offset, int[] shape, int[] stride, int majorStride) {
    return new AsArray<T>(getArrayFactory(), getOffset(), getShape(), getStride(),
                          getMajorStrideIndex()) {
      @Override
      protected int elementSize() {
        return AsArray.this.elementSize();
      }

      @Override
      protected T getElement(int i) {
        return AsArray.this.getElement(i);
      }

      @Override
      protected void setElement(int i, T value) {
        AsArray.this.setElement(i, value);
      }
    };
  }

  @Override
  public Array<T> newEmptyArray(int... shape) {
    throw new UnsupportedOperationException();
  }
}
