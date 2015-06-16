package org.briljantframework.matrix;

import com.google.common.base.Preconditions;

import org.briljantframework.Check;
import org.briljantframework.matrix.api.MatrixFactory;

import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.UnaryOperator;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractMatrix<E extends Matrix<E>> implements Matrix<E> {

  protected static final String CHANGED_TOTAL_SIZE =
      "Total size of new matrix must be unchanged. (%d, %d)";

  protected static final String ARG_DIFF_SIZE = "Arguments imply different size.";

  protected final MatrixFactory bj;

  protected final int size;
  protected final int offset;

  private final int[] stride;
  private final int[] shape;


  protected AbstractMatrix(MatrixFactory bj, int size) {
    this(bj, size, 1);
  }

  protected AbstractMatrix(MatrixFactory bj, int rows, int cols) {
    this.bj = Preconditions.checkNotNull(bj);
    this.size = Math.multiplyExact(rows, cols);
    this.shape = new int[]{rows, cols};
    this.stride = Indexer.computeStride(1, shape);
    offset = 0;
  }

  protected AbstractMatrix(MatrixFactory bj, int[] shape) {
    this.bj = Preconditions.checkNotNull(bj);
    this.shape = shape;
    this.stride = Indexer.computeStride(1, shape);
    this.size = Indexer.computeSize(shape);
    offset = 0;
  }

  protected AbstractMatrix(MatrixFactory bj, int offset, int[] shape, int[] stride) {
    this.bj = bj;
    this.shape = shape;
    this.stride = stride;
    this.size = Indexer.computeSize(shape);
    this.offset = offset;
  }

  protected MatrixFactory getMatrixFactory() {
    return bj;
  }

  protected int getOffset() {
    return offset;
  }

  @Override
  public E slice(int[] slice) {
    Check.argument(slice.length < dims(), "To many dimensions in slice.");
    int[] shape = new int[dims() - slice.length];
    int[] stride = new int[dims() - slice.length];
    for (int i = 0; i < shape.length; i++) {
      shape[i] = size(i + slice.length);
      stride[i] = stride(i + slice.length);
    }

    int offset = getOffset();
    for (int i = 0; i < slice.length; i++) {
      offset += slice[i] * stride(i);
    }
    return makeView(offset, shape, stride);
  }

  @Override
  public E slice(int index) {
//    Check.argument(index < dims());
    return makeView(
        getOffset() + index * stride(0),
        Arrays.copyOfRange(shape(), 1, dims()),
        Arrays.copyOfRange(getStride(), 1, dims())
    );
  }

  @Override
  public E getVectorAlong(int dimension, int index) {
    Check.argument(dimension < dims(), "Invalid dimension");
    int vectorsAlong = size() / size(dimension);
    Check.argument(index < vectorsAlong, "Invalid index");

    int stride = stride(0);
    if (dimension == 0) {
      stride = stride(dims() - 1);
    }

    int offset = getOffset() + index * stride;
    if (offset >= size()) {
      int size = size(0);
      int d = 0;
      int s = index;
      while (s >= size) {
        d++;
        s -= size;
      }

      int i = s * stride(dims() - 1);
      int j = d * stride(dims() - 2);
      offset = getOffset() + (i + j);

    }
    return makeView(
        offset,
        new int[]{size(dimension)},
        new int[]{stride(dimension)}
    );
  }

  protected E makeView(int offset, int[] shape, int[] stride) {
    throw new UnsupportedOperationException();
  }

  @Override
  public E map(Dim dim, UnaryOperator<E> mapper) {
    E matrix = newEmptyArray(rows(), columns());
    if (dim == Dim.R) {
      for (int i = 0; i < rows(); i++) {
        matrix.setRow(i, mapper.apply(getRow(i)));
      }
    } else {
      for (int i = 0; i < columns(); i++) {
        matrix.setColumn(i, mapper.apply(getColumn(i)));
      }
    }

    return matrix;
  }

  @Override
  public void forEach(Dim dim, Consumer<E> consumer) {
    if (dim == Dim.R) {
      for (int i = 0; i < rows(); i++) {
        consumer.accept(getRow(i));
      }
    } else {
      for (int i = 0; i < columns(); i++) {
        consumer.accept(getColumn(i));
      }
    }
  }

  @Override
  public E reshape(int... shape) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void setVectorAlong(Dim dim, int i, E vector) {
    if (dim == Dim.R) {
      setRow(i, vector);
    } else {
      setColumn(i, vector);
    }
  }

  @Override
  public E getVectorAlong(Dim dim, int index) {
    return dim == Dim.R ? getRow(index) : getColumn(index);
  }


  @Override
  public final int rows() {
    return shape[0];
  }

  @Override
  public final int columns() {
    return shape.length > 1 ? shape[1] : 1;
  }

  @Override
  public int size(Dim dim) {
    return dim == Dim.R ? rows() : columns();
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isVector() {
    return dims() == 1;
  }

  @Override
  public boolean isMatrix() {
    return dims() == 2;
  }

  @Override
  public int size(int dim) {
    return shape[dim];
  }

  @Override
  public int dims() {
    return shape.length;
  }

  @Override
  public int[] shape() {
    return shape;
  }

  @Override
  public int[] getStride() {
    return stride;
  }

  protected int getIndex(int index) {
    return getOffset() + index;
  }

  @Override
  public E newEmptyArray(int... shape) {
    throw new UnsupportedOperationException();
  }

  @Override
  public BitMatrix lt(E other) {
    return null;
  }

  @Override
  public BitMatrix gt(E other) {
    return null;
  }

  @Override
  public BitMatrix eq(E other) {
    return null;
  }

  @Override
  public BitMatrix lte(E other) {
    return null;
  }

  @Override
  public BitMatrix gte(E other) {
    return null;
  }
}
