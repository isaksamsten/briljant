package org.briljantframework.matrix;

import com.google.common.base.Preconditions;

import org.briljantframework.Check;
import org.briljantframework.matrix.api.ArrayFactory;

import java.util.Arrays;
import java.util.function.Consumer;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractArray<E extends Array<E>> implements Array<E> {

  protected static final String CHANGED_TOTAL_SIZE =
      "Total size of new array must be unchanged. (%s, %s)";

  protected static final String ARG_DIFF_SIZE = "Arguments imply different size.";

  public static final String INVALID_DIMENSION = "Dimension out of bounds (%s < %s)";
  public static final String INVALID_VECTOR = "Vector index out of bounds (%s < %s)";
  protected static final String ILLEGAL_INDEX = "Illegal index";

  protected final ArrayFactory bj;

  protected final int majorStride;

  /**
   * The size of the array. Equals to shape[0] * shape[1] * ... * shape[shape.length -1]
   */
  protected final int size;

  /**
   * The offset of the array, i.e. the position where indexing should start
   */
  protected final int offset;

  /**
   * The i:th position holds the number of elements between elements in the i:th dimension
   */
  protected final int[] stride;

  /**
   * The size of the i:th dimension
   */
  protected final int[] shape;


  protected AbstractArray(ArrayFactory bj, int[] shape) {
    this.bj = Preconditions.checkNotNull(bj);
    this.shape = shape;
    this.stride = Indexer.computeStride(1, shape);
    this.size = Indexer.size(shape);
    offset = 0;
    this.majorStride = 0;
  }

  protected AbstractArray(ArrayFactory bj, int offset, int[] shape, int[] stride, int majorStride) {
    this.bj = bj;
    this.shape = shape;
    this.stride = stride;
    this.size = Indexer.size(shape);
    this.offset = offset;
    this.majorStride = majorStride;
  }

  @Override
  public E select(int index) {
    Check.argument(dims() > 0, "Can't select in 1-d array");
    int dims = dims();
    return makeView(
        getOffset() + index * stride(getMajorStride()),
        Arrays.copyOfRange(getShape(), 1, dims),
        Arrays.copyOfRange(getStride(), 1, dims)
    );
  }

  @Override
  public E select(int dimension, int index) {
    Check.argument(dimension < dims(), "Can't select dimension.");
    Check.argument(index < size(dimension), "Index outside of shape.");
    return makeView(
        getOffset() + index * stride(dimension),
        Indexer.remove(getShape(), dimension),
        Indexer.remove(getStride(), dimension)
    );
  }

  @Override
  public void setVector(int dimension, int index, E other) {
    getVector(dimension, index).assign(other);
  }

  @Override
  public E getVector(int dimension, int index) {
    int dims = dims();
    int vectors = vectors(dimension);
    Check.argument(dimension < dims, INVALID_DIMENSION, dimension, dims);
    Check.argument(index < vectors, INVALID_VECTOR, index, vectors);

    int offset = getOffset();
    int stride = stride(dimension);
    int shape = size(dimension);
    int indexMajorStride = index * stride(getMajorStride());
    if (indexMajorStride >= stride) {
      if (!isTransposed()) {
        offset += (indexMajorStride / stride) * stride * (shape - 1);
      } else {
        offset += (indexMajorStride % stride) * stride * (shape - 1);
      }
    }

    return makeView(
        offset + indexMajorStride,
        new int[]{size(dimension)},
        new int[]{stride(dimension)}
    );
  }

  protected ArrayFactory getMatrixFactory() {
    return bj;
  }

  @Override
  public int getOffset() {
    return offset;
  }

  @Override
  public E getRow(int i) {
    Check.state(isMatrix(), "Can only get rows from 2d-arrays");
    return getVector(1, i);
  }

  @Override
  public E getColumn(int i) {
    Check.state(isMatrix(), "Can only get columns from 2d-arrays");
    return getVector(0, i);
  }

  @Override
  public E getDiagonal() {
    Check.state(isMatrix(), "Can only get the diagonal of 2d-arrays");
    return makeView(
        getOffset(),
        new int[]{Math.min(rows(), columns())},
        new int[]{rows() + 1}
    );
  }

  @Override
  public E getView(int rowOffset, int colOffset, int rows, int columns) {
    throw new UnsupportedOperationException();
  }

  protected E makeView(int offset, int[] shape, int[] stride) {
    return makeView(offset, shape, stride, 0);
  }

  protected abstract E makeView(int offset, int[] shape,
                                int[] stride,
                                int majorStride);

  protected abstract int elementSize();

  @Override
  public void forEach(int dim, Consumer<E> consumer) {
    int size = vectors(dim);
    for (int i = 0; i < size; i++) {
      consumer.accept(getVector(dim, i));
    }
  }

  @Override
  public final E reshape(int... shape) {
    Check.size(Indexer.size(getShape()), Indexer.size(shape),
               CHANGED_TOTAL_SIZE, getShape(), shape);
    return makeView(getOffset(), shape, Indexer.computeStride(1, shape));
  }

  @Override
  public final E transpose() {
    if (isVector()) {
      return makeView(getOffset(), getShape(), getStride());
    } else {
      return makeView(
          getOffset(),
          Indexer.reverse(getShape()),
          Indexer.reverse(getStride()),
          dims() - 1 // change the major stride
      );
    }
  }

  @Override
  public final int rows() {
    Check.state(isMatrix(), "Can only get number of rows of 2-d array");
    return shape[0];
  }

  @Override
  public final int columns() {
    Check.state(isMatrix(), "Can only get number of columns of 2-d array");
    return shape[1];
  }

  @Override
  public final int size() {
    return size;
  }

  @Override
  public final boolean isVector() {
    return dims() == 1;
  }

  @Override
  public final boolean isMatrix() {
    return dims() == 2;
  }

  @Override
  public boolean isView() {
    return false;
  }

  @Override
  public final int size(int dim) {
    return shape[dim];
  }

  @Override
  public final int vectors(int i) {
    return size() / size(i);
  }

  @Override
  public final int dims() {
    return shape.length;
  }

  @Override
  public final int stride(int i) {
    return stride[i];
  }

  @Override
  public final int[] getShape() {
    return shape.clone();
  }

  @Override
  public final int[] getStride() {
    return stride.clone();
  }

  /**
   * The major stride of the array, for a transposed matrix this equals to
   * {@code stride[stride.length -1]} and otherwise {@code stride[0]}
   */
  public int getMajorStride() {
    return majorStride;
  }

  protected boolean isTransposed() {
    return majorStride != 0;
  }
}
