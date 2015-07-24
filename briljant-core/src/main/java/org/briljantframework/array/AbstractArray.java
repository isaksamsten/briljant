package org.briljantframework.array;

import com.google.common.base.Preconditions;

import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayFactory;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractArray<E extends Array<E>> implements Array<E> {

  protected static final String CHANGED_TOTAL_SIZE =
      "Total size of new array must be unchanged. (%s, %s)";

  public static final String INVALID_DIMENSION = "Dimension out of bounds (%s < %s)";
  public static final String INVALID_VECTOR = "Vector index out of bounds (%s < %s)";
  protected static final String ILLEGAL_INDEX = "Illegal index";
  protected static final String
      ILLEGAL_DIMENSION_INDEX = "Index %s is out of bounds for dimension %s with size %s";

  protected final ArrayFactory bj;

  protected final int majorStride;

  /**
   * The size of the array. Equals to shape[0] * shape[1] * ... * shape[shape.length - 1]
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
  public void assign(E o) {
    Check.size(this, o);
    for (int i = 0; i < o.size(); i++) {
      set(i, o, i);
    }
  }

  @Override
  public E select(int index) {
    Check.argument(dims() > 1, "Can't select in 1-d array");
    Check.argument(index >= 0 && index < size(0), ILLEGAL_DIMENSION_INDEX, index, 0, size(0));
    int dims = dims();
    return makeView(
        getOffset() + index * stride(0),
        Arrays.copyOfRange(getShape(), 1, dims),
        Arrays.copyOfRange(getStride(), 1, dims)
    );
  }

  @Override
  public E select(int dimension, int index) {
    Check.argument(dimension < dims() && dimension >= 0, "Can't select dimension.");
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
    int indexMajorStride = index * stride(majorStride);
    if (indexMajorStride >= stride) {
//      if (!isTransposed()) {
      offset += (indexMajorStride / stride) * stride * (shape - 1);
//      } else {
//        offset += (indexMajorStride % stride) * stride * (shape - 1);
//      }
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
    return getView(i, 0, 1, columns());
  }

  @Override
  public E getColumn(int i) {
    Check.state(isMatrix(), "Can only get columns from 2d-arrays");
    return getView(0, i, rows(), 1);
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
    Check.state(isMatrix(), "Can only get view from 2d-arrays");
    Check.argument(rowOffset + rows <= rows() && colOffset + columns <= columns(),
                   "Selected view is to large");
    return makeView(
        getOffset() + rowOffset * stride(0) + colOffset * stride(1),
        new int[]{rows, columns},
        getStride(),
        rows == 1 ? 1 : 0 // change the major stride
    );
  }

  @Override
  public E get(Range... ranges) {
    return get(Arrays.asList(ranges));
  }

  @Override
  public E get(List<Range> ranges) {
    Check.argument(ranges.size() > 0, "Too few ranges to slice");
    Check.argument(ranges.size() <= dims(), "Too many ranges to slice");
    int[] stride = getStride();
    int[] shape = getShape();
    int offset = getOffset();
    for (int i = 0; i < ranges.size(); i++) {
      Range r = ranges.get(i);
      offset += r.start() * stride(i);
      shape[i] = r.size();
      stride[i] = stride[i] * r.step();
    }

    return makeView(
        offset,
        shape,
        stride
    );
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
  public void setRow(int i, E vec) {
    getRow(i).assign(vec);
  }

  @Override
  public void setColumn(int i, E vec) {
    getColumn(i).assign(vec);
  }

  @Override
  public final E reshape(int... shape) {
    if (shape.length == 1 && shape[0] == -1) {
      int[] newShape = {size()};
      if (isContiguous()) {
        return makeView(getOffset(), newShape, Indexer.computeStride(1, newShape));
      } else {
        return copy().reshape(shape);
      }
    }

    Check.size(Indexer.size(this.shape), Indexer.size(shape),
               CHANGED_TOTAL_SIZE, Arrays.toString(this.shape), Arrays.toString(shape));
    if (isContiguous()) {
      return makeView(getOffset(), shape.clone(), Indexer.computeStride(1, shape));
    } else {
      return copy().reshape(shape);
    }
  }

  protected boolean isContiguous() {
    return majorStride == 0;
  }

  @Override
  public final E transpose() {
    if (isVector()) {
      return makeView(getOffset(), getShape(), getStride());
    } else {
      return makeView(
          getOffset(),
          Indexer.reverse(shape),
          Indexer.reverse(stride),
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
  public int getMajorStride() {
    return stride(majorStride);
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
   * {@code stride[stride.length - 1]} and otherwise {@code stride[0]}
   */
  protected int getMajorStrideIndex() {
    return majorStride;
  }

  protected boolean isTransposed() {
    return majorStride != 0;
  }
}
