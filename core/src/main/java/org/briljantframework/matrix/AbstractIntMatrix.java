package org.briljantframework.matrix;

import static com.google.common.base.Preconditions.checkArgument;
import static org.briljantframework.matrix.Indexer.columnMajor;
import static org.briljantframework.matrix.Indexer.rowMajor;

import java.util.Iterator;
import java.util.Objects;
import java.util.function.*;

import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.vector.VectorLike;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableTable;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public abstract class AbstractIntMatrix extends AbstractAnyMatrix implements IntMatrix {

  protected AbstractIntMatrix(int rows, int cols) {
    super(rows, cols);
  }

  @Override
  public Type getType() {
    return Type.INT;
  }

  @Override
  public Complex getAsComplex(int i, int j) {
    return new Complex(getAsDouble(i, j));
  }

  @Override
  public Complex getAsComplex(int index) {
    return new Complex(getAsDouble(index));
  }

  @Override
  public void set(int i, int j, Complex value) {
    set(i, j, value.doubleValue());
  }

  @Override
  public void set(int index, Complex value) {
    set(index, value.doubleValue());
  }

  @Override
  public double getAsDouble(int i, int j) {
    return get(i, j);
  }

  @Override
  public double getAsDouble(int index) {
    return get(index);
  }

  @Override
  public void set(int i, int j, double value) {
    set(i, j, (int) value);
  }

  @Override
  public void set(int index, double value) {
    set(index, (int) value);
  }

  @Override
  public int getAsInt(int i, int j) {
    return get(i, j);
  }

  @Override
  public int getAsInt(int index) {
    return get(index);
  }

  @Override
  public void set(int atIndex, AnyMatrix from, int fromIndex) {
    set(atIndex, from.getAsInt(fromIndex));
  }

  @Override
  public void set(int atRow, int atColumn, AnyMatrix from, int fromRow, int fromColumn) {
    set(atRow, atColumn, from.getAsInt(fromRow, fromColumn));
  }

  @Override
  public IntMatrix assign(IntSupplier supplier) {
    for (int i = 0; i < size(); i++) {
      set(i, supplier.getAsInt());
    }
    return this;
  }

  @Override
  public IntMatrix assign(int value) {
    for (int i = 0; i < size(); i++) {
      set(i, value);
    }
    return this;
  }

  @Override
  public IntMatrix assign(VectorLike vector, Axis axis) {
    return assign(vector, (a, b) -> b, axis);
  }

  @Override
  public IntMatrix assign(VectorLike other, IntBinaryOperator operator, Axis axis) {
    /*
     * Due to cache-locality, put(i, ) is for most (at least array based) matrices a _big_ win.
     * Therefore, the straightforward implementation using two for-loops is not used below. This is
     * a big win since this.size() >= other.size().
     */
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        set(i, operator.applyAsInt(get(i), other.getAsInt(i % rows())));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        set(i, operator.applyAsInt(get(i), other.getAsInt(i / rows())));
      }
    }
    return this;
  }

  @Override
  public IntMatrix assign(IntMatrix matrix) {
    return assign(matrix, IntUnaryOperator.identity());
  }

  @Override
  public IntMatrix assign(IntMatrix matrix, IntUnaryOperator operator) {
    assertEqualSize(matrix);
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsInt(matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntMatrix assign(ComplexMatrix matrix, ToIntFunction<? super Complex> function) {
    Preconditions.checkArgument(matrix.size() == size());
    for (int i = 0; i < size(); i++) {
      set(i, function.applyAsInt(matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntMatrix assign(DoubleMatrix matrix, DoubleToIntFunction function) {
    for (int i = 0; i < matrix.size(); i++) {
      set(i, function.applyAsInt(matrix.get(i)));
    }
    return this;
  }

  @Override
  public IntMatrix assignStream(Iterable<? extends Number> numbers) {
    int index = 0;
    for (Number number : numbers) {
      set(index++, number.intValue());
    }
    return this;
  }

  @Override
  public <T> IntMatrix assignStream(Iterable<T> iterable, ToIntFunction<? super T> function) {
    int index = 0;
    for (T t : iterable) {
      set(index++, function.applyAsInt(t));
    }
    return this;
  }

  @Override
  public IntMatrix assign(int[] values) {
    checkArgument(size() == values.length);
    for (int i = 0; i < size(); i++) {
      set(i, values[i]);
    }
    return this;
  }

  @Override
  public IntMatrix map(IntUnaryOperator operator) {
    IntMatrix mat = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      mat.set(i, operator.applyAsInt(get(i)));
    }
    return mat;
  }

  @Override
  public IntMatrix mapi(IntUnaryOperator operator) {
    for (int i = 0; i < size(); i++) {
      set(i, operator.applyAsInt(get(i)));
    }
    return this;
  }

  @Override
  public int reduce(int identity, IntBinaryOperator reduce, IntUnaryOperator map) {
    for (int i = 0; i < size(); i++) {
      identity = reduce.applyAsInt(identity, map.applyAsInt(get(i)));
    }
    return identity;
  }



  @Override
  public IntMatrix reduceColumns(ToIntFunction<? super IntMatrix> reduce) {
    IntMatrix mat = newEmptyMatrix(1, columns());
    for (int i = 0; i < columns(); i++) {
      mat.set(i, reduce.applyAsInt(getColumnView(i)));
    }
    return mat;
  }

  @Override
  public IntMatrix reduceRows(ToIntFunction<? super IntMatrix> reduce) {
    IntMatrix mat = newEmptyMatrix(rows(), 1);
    for (int i = 0; i < rows(); i++) {
      mat.set(i, reduce.applyAsInt(getRowView(i)));
    }
    return mat;
  }

  @Override
  public IntMatrix getRowView(int i) {
    return new IntMatrixView(this, i, 0, 1, columns());
  }

  public IntMatrix getColumnView(int index) {
    return new IntMatrixView(this, 0, index, rows(), 1);
  }

  @Override
  public Diagonal getDiagonalView() {
    throw new UnsupportedOperationException();
  }

  @Override
  public IntMatrix getView(int rowOffset, int colOffset, int rows, int columns) {
    return new IntMatrixView(this, rowOffset, colOffset, rows, columns);
  }

  public IntMatrix transpose() {
    IntMatrix matrix = newEmptyMatrix(this.columns(), this.rows());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(j, i, get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public IntMatrix mmul(IntMatrix other) {
    return mmul(1, other, 1);
  }

  @Override
  public IntMatrix mmul(int alpha, IntMatrix other, int beta) {
    return mmul(alpha, Transpose.NO, other, beta, Transpose.NO);
  }

  @Override
  public IntMatrix mmul(Transpose a, IntMatrix other, Transpose b) {
    return mmul(1, a, other, 1, b);
  }

  @Override
  public IntMatrix mmul(int alpha, Transpose a, IntMatrix other, int beta, Transpose b) {
    int thisRows = rows();
    int thisCols = columns();
    if (a == Transpose.YES) {
      thisRows = columns();
      thisCols = rows();
    }
    int otherRows = other.rows();
    int otherColumns = other.columns();
    if (b == Transpose.YES) {
      otherRows = other.columns();
      otherColumns = other.rows();
    }

    if (thisCols != otherRows) {
      throw new NonConformantException(thisRows, thisCols, otherRows, otherColumns);
    }

    IntMatrix result = newEmptyMatrix(thisRows, otherColumns);
    for (int row = 0; row < thisRows; row++) {
      for (int col = 0; col < otherColumns; col++) {
        int sum = 0;
        for (int k = 0; k < thisCols; k++) {
          int thisIndex =
              a == Transpose.YES ? rowMajor(row, k, thisRows, thisCols) : columnMajor(row, k,
                  thisRows, thisCols);
          int otherIndex =
              b == Transpose.YES ? rowMajor(k, col, otherRows, otherColumns) : columnMajor(k, col,
                  otherRows, otherColumns);
          sum += alpha * get(thisIndex) * beta * other.get(otherIndex);
        }
        result.set(row, col, sum);
      }
    }
    return result;
  }

  @Override
  public IntMatrix mul(IntMatrix other) {
    return mul(1, other, 1);
  }

  @Override
  public IntMatrix mul(int alpha, IntMatrix other, int beta) {
    return copy().muli(alpha, other, beta);
  }

  @Override
  public IntMatrix mul(VectorLike other, Axis axis) {
    return mul(1, other, 1, axis);
  }

  @Override
  public IntMatrix mul(int alpha, VectorLike other, int beta, Axis axis) {
    return copy().muli(alpha, other, beta, axis);
  }

  @Override
  public IntMatrix mul(int scalar) {
    return copy().muli(scalar);
  }

  @Override
  public IntMatrix muli(IntMatrix other) {
    return muli(1, other, 1);
  }

  @Override
  public IntMatrix muli(int scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        set(i, j, get(i, j) * scalar);
      }
    }
    return this;
  }

  @Override
  public IntMatrix muli(int alpha, IntMatrix other, int beta) {
    assertEqualSize(other);
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        set(i, j, alpha * get(i, j) * other.get(i, j) * beta);
      }
    }
    return this;
  }

  @Override
  public IntMatrix muli(VectorLike other, Axis axis) {
    return muli(1, other, 1, axis);
  }

  @Override
  public IntMatrix muli(int alpha, VectorLike other, int beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (alpha * get(i)) * (other.getAsInt(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (alpha * get(i)) * (other.getAsInt(i / rows()) * beta));
      }
    }
    return this;
  }

  @Override
  public IntMatrix add(IntMatrix other) {
    return add(1, other, 1);
  }

  @Override
  public IntMatrix add(int scalar) {
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, get(i, j) + scalar);
      }
    }
    return matrix;
  }

  @Override
  public IntMatrix add(VectorLike other, Axis axis) {
    return add(1, other, 1, axis);
  }

  @Override
  public IntMatrix add(int alpha, VectorLike other, int beta, Axis axis) {
    return copy().addi(alpha, other, beta, axis);
  }

  @Override
  public IntMatrix add(int alpha, IntMatrix other, int beta) {
    assertEqualSize(other);
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) + other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  @Override
  public IntMatrix addi(IntMatrix other) {
    addi(1, other, 1);
    return this;
  }

  @Override
  public IntMatrix addi(int scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        this.set(i, j, get(i, j) + scalar);
      }
    }
    return this;
  }

  @Override
  public IntMatrix addi(VectorLike other, Axis axis) {
    return addi(1, other, 1, axis);
  }

  @Override
  public IntMatrix addi(int alpha, VectorLike other, int beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (alpha * get(i)) + (other.getAsInt(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (alpha * get(i)) + (other.getAsInt(i / rows()) * beta));
      }
    }
    return this;
  }

  @Override
  public IntMatrix addi(int alpha, IntMatrix other, int beta) {
    assertEqualSize(other);
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        set(i, j, alpha * get(i, j) + other.get(i, j) * beta);
      }
    }
    return this;
  }

  @Override
  public IntMatrix sub(IntMatrix other) {
    return sub(1, other, 1);
  }

  @Override
  public IntMatrix sub(int scalar) {
    return add(-scalar);
  }

  @Override
  public IntMatrix sub(VectorLike other, Axis axis) {
    return sub(1, other, 1, axis);
  }

  @Override
  public IntMatrix sub(int alpha, VectorLike other, int beta, Axis axis) {
    return copy().subi(alpha, other, beta, axis);
  }

  @Override
  public IntMatrix sub(int alpha, IntMatrix other, int beta) {
    assertEqualSize(other);
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, alpha * get(i, j) - other.get(i, j) * beta);
      }
    }
    return matrix;
  }

  @Override
  public IntMatrix subi(IntMatrix other) {
    addi(1, other, -1);
    return this;
  }

  @Override
  public IntMatrix subi(int scalar) {
    addi(-scalar);
    return this;
  }

  @Override
  public IntMatrix subi(VectorLike other, Axis axis) {
    return subi(1, other, 1, axis);
  }

  @Override
  public IntMatrix subi(int alpha, VectorLike other, int beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (alpha * get(i)) - (other.getAsInt(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (alpha * get(i)) - (other.getAsInt(i / rows()) * beta));
      }
    }
    return this;
  }

  @Override
  public IntMatrix subi(int alpha, IntMatrix other, int beta) {
    addi(alpha, other, -1 * beta);
    return this;
  }

  @Override
  public IntMatrix rsub(int scalar) {
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, scalar - get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public IntMatrix rsub(VectorLike other, Axis axis) {
    return rsub(1, other, 1, axis);
  }

  @Override
  public IntMatrix rsub(int alpha, VectorLike other, int beta, Axis axis) {
    return copy().rsubi(alpha, other, beta, axis);
  }

  @Override
  public IntMatrix rsubi(int scalar) {
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        set(i, j, scalar - get(i, j));
      }
    }
    return this;
  }

  @Override
  public IntMatrix rsubi(VectorLike other, Axis axis) {
    return rsubi(1, other, 1, axis);
  }

  @Override
  public IntMatrix rsubi(int alpha, VectorLike other, int beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (other.getAsInt(i % rows()) * beta) - (alpha * get(i)));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (other.getAsInt(i / rows()) * beta) - (alpha * get(i)));
      }
    }
    return this;
  }

  @Override
  public IntMatrix div(IntMatrix other) {
    assertEqualSize(other);
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.set(i, j, get(i, j) / other.get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public IntMatrix div(int other) {
    return mul(1 / other);
  }

  @Override
  public IntMatrix div(VectorLike other, Axis axis) {
    return div(1, other, 1, axis);
  }

  @Override
  public IntMatrix div(int alpha, VectorLike other, int beta, Axis axis) {
    return copy().divi(alpha, other, beta, axis);
  }

  @Override
  public IntMatrix divi(IntMatrix other) {
    assertEqualSize(other);
    for (int i = 0; i < size(); i++) {
      set(i, get(i) / other.get(i));
    }
    return this;
  }

  @Override
  public IntMatrix divi(int other) {
    return muli(1 / other);
  }

  @Override
  public IntMatrix divi(VectorLike other, Axis axis) {
    return divi(1, other, 1, axis);
  }

  @Override
  public IntMatrix divi(int alpha, VectorLike other, int beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (alpha * get(i)) / (other.getAsInt(i % rows()) * beta));
      }
    } else {
      checkArgument(other.size() == columns(), ARG_DIFF_SIZE);
      for (int i = 0; i < size(); i++) {
        this.set(i, (alpha * get(i)) / (other.getAsInt(i / rows()) * beta));
      }
    }
    return this;
  }

  @Override
  public IntMatrix rdiv(int other) {
    IntMatrix matrix = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      matrix.set(i, other / get(i));
    }
    return matrix;
  }

  @Override
  public IntMatrix rdiv(VectorLike other, Axis axis) {
    return rdiv(1, other, 1, axis);
  }

  @Override
  public IntMatrix rdiv(int alpha, VectorLike other, int beta, Axis axis) {
    return copy().rdivi(alpha, other, beta, axis);
  }

  @Override
  public IntMatrix rdivi(int other) {
    for (int i = 0; i < size(); i++) {
      set(i, other / get(i));
    }
    return this;
  }

  @Override
  public IntMatrix rdivi(VectorLike other, Axis axis) {
    return rdivi(1, other, 1, axis);
  }

  @Override
  public IntMatrix rdivi(int alpha, VectorLike other, int beta, Axis axis) {
    if (axis == Axis.COLUMN) {
      checkArgument(other.size() == rows());
      for (int i = 0; i < size(); i++) {
        this.set(i, (other.getAsInt(i % rows()) * beta) / (alpha * get(i)));
      }
    } else {
      checkArgument(other.size() == columns());
      for (int i = 0; i < size(); i++) {
        this.set(i, (other.getAsInt(i / rows()) * beta) / (alpha * get(i)));
      }
    }
    return this;
  }

  @Override
  public IntMatrix negate() {
    IntMatrix n = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      n.set(i, -get(i));
    }
    return n;
  }

  @Override
  public int[] asIntArray() {
    int[] array = new int[size()];
    for (int i = 0; i < size(); i++) {
      array[i] = get(i);
    }
    return array;
  }

  @Override
  public int hashCode() {
    int result = 1;
    for (int i = 0; i < size(); i++) {
      long bits = get(i);
      result = 31 * result + (int) (bits ^ (bits >>> 32));
    }

    return Objects.hash(rows(), columns(), result);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj instanceof IntMatrix) {
      IntMatrix mat = (IntMatrix) obj;
      if (!mat.hasEqualShape(this)) {
        return false;
      }
      for (int i = 0; i < size(); i++) {
        if (get(i) != mat.get(i)) {
          return false;
        }
      }
      return true;
    } else {
      return false;
    }
  }

  @Override
  public String toString() {
    ImmutableTable.Builder<Integer, Integer, String> builder = ImmutableTable.builder();
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        if (get(i, j) < 0) {
          builder.put(i, j, String.format("%d", get(i, j)));
        } else {
          builder.put(i, j, String.format(" %d", get(i, j)));
        }
      }
    }
    StringBuilder out = new StringBuilder();
    Utils.prettyPrintTable(out, builder.build(), 0, 2, false, false);
    out.append("shape: ").append(getShape());
    out.append(" type: int");
    return out.toString();
  }

  @Override
  public Iterator<Integer> iterator() {
    return new Iterator<Integer>() {
      private int index = 0;

      @Override
      public boolean hasNext() {
        return index < size();
      }

      @Override
      public Integer next() {
        return get(index++);
      }
    };
  }


}
