/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
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

package org.briljantframework.array;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

import org.briljantframework.Check;
import org.briljantframework.array.api.ArrayFactory;

/**
 * This class provides a skeletal implementation
 *
 * @author Isak Karlsson
 */
public abstract class AbstractBaseArray<E extends BaseArray<E>> implements BaseArray<E> {

  protected static final String CHANGED_TOTAL_SIZE =
      "Total size of new array must be unchanged. (%s, %s)";

  public static final String INVALID_DIMENSION = "Dimension out of bounds (%s < %s)";
  public static final String INVALID_VECTOR = "Vector index out of bounds (%s < %s)";
  protected static final String ILLEGAL_INDEX = "Illegal index";
  protected static final String ILLEGAL_DIMENSION_INDEX =
      "Index %s is out of bounds for dimension %s with size %s";

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


  protected AbstractBaseArray(ArrayFactory bj, int[] shape) {
    this.bj = Objects.requireNonNull(bj);
    this.shape = shape;
    this.stride = Indexer.computeStride(1, shape);
    this.size = Indexer.size(shape);
    offset = 0;
    this.majorStride = 0;
  }

  protected AbstractBaseArray(ArrayFactory bj, int offset, int[] shape, int[] stride,
      int majorStride) {
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
    return asView(getOffset() + index * stride(0), Arrays.copyOfRange(getShape(), 1, dims),
        Arrays.copyOfRange(getStride(), 1, dims));
  }

  @Override
  public E select(int dimension, int index) {
    Check.argument(dimension < dims() && dimension >= 0, "Can't select dimension.");
    Check.argument(index < size(dimension), "Index outside of shape.");
    return asView(getOffset() + index * stride(dimension), Indexer.remove(getShape(), dimension),
        Indexer.remove(getStride(), dimension));
  }

  @Override
  public E select(List<List<Integer>> indexes) {
    Check.argument(indexes.size() > 0 && indexes.size() <= dims());
    E self = asView(getOffset(), this.shape, this.stride);

    int[] shape = getShape();
    int commonShape = indexes.get(0).size();
    for (int i = 0; i < indexes.size(); i++) {
      List<Integer> index = indexes.get(i);
      Check.argument(commonShape == index.size(), "Indexing arrays could not be used together");
      shape[i] = index.size();
    }
    int[] newShape = new int[shape.length - indexes.size() + 1];
    System.arraycopy(shape, dims() - newShape.length, newShape, 0, newShape.length);
    E array = newEmptyArray(newShape);
    List<Integer> subIndex = indexes.get(0);
    if (indexes.size() == 1) {
      if (array.isVector()) {
        int size = self.size();
        for (int i = 0; i < subIndex.size(); i++) {
          Integer fromIndex = subIndex.get(i);
          Check.boxedIndex(fromIndex, size);
          array.set(i, self, fromIndex);
        }
      } else {
        for (int j = 0; j < subIndex.size(); j++) {
          Integer fromIndex = subIndex.get(j);
          Check.boxedIndex(fromIndex, self.size(0));
          array.select(j).assign(self.select(fromIndex));
        }
      }
    } else {
      for (int j = 0; j < subIndex.size(); j++) {
        Integer fromIndex = subIndex.get(j);
        Check.boxedIndex(fromIndex, self.size(0));
        select(indexes, self.select(fromIndex), array, j, 1);
      }
    }
    return array;
  }

  private void select(List<List<Integer>> indexes, E from, E to, int j, int dim) {
    Integer fromIndex = indexes.get(dim).get(j);
    if (indexes.size() - 1 == dim) {
      if (to.isVector()) {
        Check.state(from.isVector());
        Check.boxedIndex(fromIndex, from.size());
        to.set(j, from, fromIndex);
      } else {
        Check.boxedIndex(fromIndex, from.size(dim));
        to.select(j).assign(from.select(fromIndex));
      }
    } else {
      Check.elementIndex(dim, from.dims());
      Check.boxedIndex(fromIndex, from.size(dim));
      select(indexes, from.select(fromIndex), to, j, dim + 1);
    }
  }

  @Override
  public E select(int[][] indexes) {
    List<List<Integer>> boxed = new ArrayList<>();
    for (int[] index : indexes) {
      boxed.add(Indexer.asList(index));
    }
    return select(boxed);
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
      offset += (indexMajorStride / stride) * stride * (shape - 1);
    }

    return asView(offset + indexMajorStride, new int[] {size(dimension)},
        new int[] {stride(dimension)});
  }

  protected ArrayFactory getArrayFactory() {
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
    return asView(getOffset(), new int[] {Math.min(rows(), columns())}, new int[] {rows() + 1});
  }

  @Override
  public E getView(int rowOffset, int colOffset, int rows, int columns) {
    Check.state(isMatrix(), "Can only get view from 2d-arrays");
    Check.argument(rowOffset + rows <= rows() && colOffset + columns <= columns(),
        "Selected view is to large");
    return asView(getOffset() + rowOffset * stride(0) + colOffset * stride(1), new int[] {rows,
        columns}, getStride(), rows == 1 ? 1 : 0 // change the major stride
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
      int start = r.start();
      int end = r.end() == -1 ? size(i) : r.size();
      int step = r.step() == -1 ? 1 : r.step();

      Check.argument(step > 0, "Illegal step size in dimension %s", step);
      Check.argument(start >= 0 && start <= end, ILLEGAL_DIMENSION_INDEX, start, i, size(i));
      Check.argument(end >= start && end <= size(i), ILLEGAL_DIMENSION_INDEX, end, i, size(i));
      offset += start * stride[i];
      shape[i] = end;
      stride[i] = stride[i] * step;
    }

    return asView(offset, shape, stride);
  }

  @Override
  public E asView(int[] shape, int[] stride) {
    return asView(getOffset(), shape, stride);
  }

  @Override
  public E asView(int offset, int[] shape, int[] stride) {
    return asView(offset, shape, stride, 0);
  }

  /**
   * Return the number of elements in the data source.
   *
   * @return the number of elements in the data source
   */
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
      if (isContiguous()) {
        int[] newShape = {size()};
        return asView(getOffset(), newShape, Indexer.computeStride(1, newShape));
      } else {
        return copy().reshape(shape);
      }
    }

    Check.size(Indexer.size(this.shape), Indexer.size(shape), CHANGED_TOTAL_SIZE,
        Arrays.toString(this.shape), Arrays.toString(shape));
    if (isContiguous()) {
      return asView(getOffset(), shape.clone(), Indexer.computeStride(1, shape));
    } else {
      return copy().reshape(shape);
    }
  }

  @Override
  public E ravel() {
    return reshape(-1);
  }

  @Override
  public boolean isContiguous() {
    return majorStride == 0;
  }

  @Override
  public final E transpose() {
    if (dims() == 1) {
      return asView(getOffset(), getShape(), getStride());
    } else {
      return asView(getOffset(), Indexer.reverse(shape), Indexer.reverse(stride),
          majorStride == 0 ? dims() - 1 : 0 // change the major stride
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
    return dims() == 1 || (dims() == 2 && (rows() == 1 || columns() == 1));
  }

  @Override
  public final boolean isMatrix() {
    return dims() == 2;
  }

  @Override
  public boolean isView() {
    return !(majorStride == 0 && offset == 0 && Arrays.equals(stride,
        Indexer.computeStride(1, shape)));
  }

  @Override
  public final int size(int dim) {
    Check.argument(dim >= 0 && dim < dims(), "dimension out of bounds");
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

  protected int getMajorStrideIndex() {
    return majorStride;
  }
}
