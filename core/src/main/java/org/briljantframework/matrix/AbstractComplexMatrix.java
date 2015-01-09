package org.briljantframework.matrix;

import static org.briljantframework.matrix.Indexer.columnMajor;
import static org.briljantframework.matrix.Indexer.rowMajor;

import java.util.Iterator;
import java.util.function.*;

import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.complex.ComplexBuilder;
import org.briljantframework.exceptions.NonConformantException;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableTable;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Created by Isak Karlsson on 02/01/15.
 */
public abstract class AbstractComplexMatrix extends AbstractAnyMatrix implements ComplexMatrix {

  protected AbstractComplexMatrix(int rows, int cols) {
    super(rows, cols);
  }

  @Override
  public Complex getAsComplex(int i, int j) {
    return get(i, j);
  }

  @Override
  public Complex getAsComplex(int index) {
    return get(index);
  }

  @Override
  public double getAsDouble(int i, int j) {
    return get(i, j).doubleValue();
  }

  @Override
  public double getAsDouble(int index) {
    return get(index).doubleValue();
  }

  @Override
  public void put(int i, int j, double value) {
    put(i, j, Complex.valueOf(value));
  }

  @Override
  public void put(int index, double value) {
    put(index, Complex.valueOf(value));
  }

  @Override
  public int getAsInt(int i, int j) {
    return get(i, j).intValue();
  }

  @Override
  public int getAsInt(int index) {
    return get(index).intValue();
  }

  @Override
  public void put(int i, int j, int value) {
    put(i, j, Complex.valueOf(value));
  }

  @Override
  public void put(int index, int value) {
    put(index, Complex.valueOf(value));
  }

  @Override
  public ComplexMatrix assign(Supplier<Complex> supplier) {
    for (int i = 0; i < size(); i++) {
      put(i, supplier.get());
    }
    return this;
  }

  @Override
  public ComplexMatrix assign(Complex value) {
    for (int i = 0; i < size(); i++) {
      put(i, value);
    }
    return this;
  }

  @Override
  public ComplexMatrix assign(Complex[] values) {
    Preconditions.checkArgument(size() == values.length);
    for (int i = 0; i < size(); i++) {
      put(i, values[i]);
    }
    return this;
  }

  @Override
  public ComplexMatrix assign(ComplexMatrix matrix) {
    return assign(matrix, UnaryOperator.identity());
  }

  @Override
  public ComplexMatrix assign(ComplexMatrix matrix, UnaryOperator<Complex> operator) {
    assertEqualSize(matrix);
    for (int i = 0; i < size(); i++) {
      put(i, operator.apply(matrix.get(i)));
    }
    return this;
  }

  @Override
  public ComplexMatrix assign(DoubleMatrix matrix) {
    Preconditions.checkArgument(matrix.size() == size());
    for (int i = 0; i < size(); i++) {
      put(i, Complex.valueOf(matrix.get(i)));
    }
    return this;
  }

  @Override
  public ComplexMatrix assign(DoubleMatrix matrix, DoubleFunction<? extends Complex> operator) {
    Preconditions.checkArgument(matrix.size() == size());
    for (int i = 0; i < size(); i++) {
      put(i, operator.apply(matrix.get(i)));
    }
    return this;
  }

  @Override
  public ComplexMatrix assignStream(Iterable<? extends Complex> complexes) {
    int index = 0;
    Iterator<? extends Complex> iter = complexes.iterator();
    while (iter.hasNext() && index < size()) {
      put(index++, iter.next());
    }
    return this;
  }

  @Override
  public <T> ComplexMatrix assignStream(Iterable<T> iterable,
      Function<? super T, ? extends Complex> function) {
    int index = 0;
    Iterator<T> iter = iterable.iterator();
    while (iter.hasNext() && index < size()) {
      put(index++, function.apply(iter.next()));
    }
    return this;
  }

  @Override
  public ComplexMatrix map(UnaryOperator<Complex> operator) {
    ComplexMatrix m = newEmptyMatrix(rows(), columns());
    for (int i = 0; i < size(); i++) {
      m.put(i, operator.apply(get(i)));
    }
    return m;
  }

  @Override
  public ComplexMatrix mapi(UnaryOperator<Complex> operator) {
    for (int i = 0; i < size(); i++) {
      put(i, operator.apply(get(i)));
    }
    return this;
  }

  @Override
  public Complex reduce(Complex identity, BinaryOperator<Complex> reduce, UnaryOperator<Complex> map) {
    for (int i = 0; i < size(); i++) {
      identity = reduce.apply(identity, map.apply(get(i)));
    }
    return identity;
  }

  @Override
  public ComplexMatrix reduceColumns(Function<? super ComplexMatrix, ? extends Complex> reduce) {
    ComplexMatrix mat = newEmptyMatrix(1, columns());
    for (int i = 0; i < columns(); i++) {
      mat.put(i, reduce.apply(getColumnView(i)));
    }
    return mat;
  }

  @Override
  public ComplexMatrix reduceRows(Function<? super ComplexMatrix, ? extends Complex> reduce) {
    ComplexMatrix mat = newEmptyMatrix(rows(), 1);
    for (int i = 0; i < rows(); i++) {
      mat.put(i, reduce.apply(getRowView(i)));
    }
    return mat;
  }

  @Override
  public ComplexMatrix getRowView(int i) {
    return new ComplexMatrixView(this, i, 0, 1, columns());
  }

  @Override
  public ComplexMatrix getColumnView(int index) {
    return new ComplexMatrixView(this, 0, index, rows(), 1);
  }

  @Override
  public Diagonal getDiagonalView() {
    throw new UnsupportedOperationException();
  }

  @Override
  public ComplexMatrix getView(int rowOffset, int colOffset, int rows, int columns) {
    return new ComplexMatrixView(this, rowOffset, colOffset, rows, columns);
  }

  @Override
  public ComplexMatrix transpose() {
    ComplexMatrix matrix = newEmptyMatrix(columns(), rows());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(j, i, get(i, j));
      }
    }
    return matrix;
  }

  @Override
  public ComplexMatrix conjugateTranspose() {
    ComplexMatrix matrix = newEmptyMatrix(columns(), rows());
    for (int j = 0; j < columns(); j++) {
      for (int i = 0; i < rows(); i++) {
        matrix.put(j, i, get(i, j).conjugate());
      }
    }
    return matrix;
  }

  @Override
  public ComplexMatrix mmul(ComplexMatrix other) {
    return mmul(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexMatrix mmul(Complex alpha, ComplexMatrix other, Complex beta) {
    return mmul(alpha, Transpose.NO, other, beta, Transpose.NO);
  }

  @Override
  public ComplexMatrix mmul(Transpose a, ComplexMatrix other, Transpose b) {
    return mmul(Complex.ONE, a, other, Complex.ONE, b);
  }

  @Override
  public ComplexMatrix mmul(Complex alpha, Transpose a, ComplexMatrix other, Complex beta,
      Transpose b) {
    int thisRows = rows();
    int thisCols = columns();
    if (a.transpose()) {
      thisRows = columns();
      thisCols = rows();
    }

    int otherRows = other.rows();
    int otherColumns = other.columns();
    if (b.transpose()) {
      otherRows = other.columns();
      otherColumns = other.rows();
    }

    if (thisCols != otherRows) {
      throw new NonConformantException(thisRows, thisCols, otherRows, otherColumns);
    }

    ComplexMatrix result = newEmptyMatrix(thisRows, otherColumns);
    for (int row = 0; row < thisRows; row++) {
      for (int col = 0; col < otherColumns; col++) {
        ComplexBuilder sumAcc = new ComplexBuilder(0);
        for (int k = 0; k < thisCols; k++) {
          int thisIndex;
          int otherIndex;
          if (a.transpose()) {
            thisIndex = rowMajor(row, k, thisRows, thisCols);
          } else {
            thisIndex = columnMajor(row, k, thisRows, thisCols);
          }
          if (b.transpose()) {
            otherIndex = rowMajor(k, col, otherRows, otherColumns);
          } else {
            otherIndex = columnMajor(k, col, otherRows, otherColumns);
          }

          Complex thisValue = get(thisIndex);
          Complex otherValue = other.get(otherIndex);
          thisValue = a == Transpose.CONJ ? thisValue.conjugate() : thisValue;
          otherValue = b == Transpose.CONJ ? otherValue.conjugate() : otherValue;

          if (alpha.equals(Complex.ONE) && beta.equals(Complex.ONE)) {
            sumAcc.plus(thisValue.multiply(otherValue));
          } else {
            sumAcc.plus(alpha.multiply(thisValue).multiply(beta).multiply(otherValue));
          }
        }
        result.put(row, col, sumAcc.toComplex());
      }
    }
    return result;
  }

  @Override
  public ComplexMatrix mul(ComplexMatrix other) {
    return mul(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexMatrix mul(Complex alpha, ComplexMatrix other, Complex beta) {
    return copy().muli(alpha, other, beta);
  }

  @Override
  public ComplexMatrix mul(Complex scalar) {
    return copy().muli(scalar);
  }

  @Override
  public ComplexMatrix muli(ComplexMatrix other) {
    return muli(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexMatrix muli(Complex scalar) {
    for (int i = 0; i < size(); i++) {
      put(i, get(i).multiply(scalar));
    }
    return this;
  }

  @Override
  public ComplexMatrix muli(Complex alpha, ComplexMatrix other, Complex beta) {
    assertEqualSize(other);
    for (int i = 0; i < size(); i++) {
      put(i, alpha.multiply(get(i)).multiply(beta).multiply(other.get(i)));
    }
    return this;
  }

  @Override
  public ComplexMatrix add(ComplexMatrix other) {
    return add(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexMatrix add(Complex scalar) {
    return copy().addi(scalar);
  }

  @Override
  public ComplexMatrix add(Complex alpha, ComplexMatrix other, Complex beta) {
    return copy().addi(alpha, other, beta);
  }

  @Override
  public ComplexMatrix addi(ComplexMatrix other) {
    return addi(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexMatrix addi(Complex scalar) {
    for (int i = 0; i < size(); i++) {
      put(i, get(i).plus(scalar));
    }
    return this;
  }

  @Override
  public ComplexMatrix addi(Complex alpha, ComplexMatrix other, Complex beta) {
    assertEqualSize(other);
    for (int i = 0; i < size(); i++) {
      put(i, get(i).multiply(alpha).plus(other.get(i).multiply(beta)));
    }
    return this;
  }

  @Override
  public ComplexMatrix sub(ComplexMatrix other) {
    return sub(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexMatrix sub(Complex scalar) {
    return copy().subi(scalar);
  }

  @Override
  public ComplexMatrix sub(Complex alpha, ComplexMatrix other, Complex beta) {
    return copy().subi(alpha, other, beta);
  }

  @Override
  public ComplexMatrix subi(ComplexMatrix other) {
    return subi(Complex.ONE, other, Complex.ONE);
  }

  @Override
  public ComplexMatrix subi(Complex scalar) {
    for (int i = 0; i < size(); i++) {
      put(i, get(i).minus(scalar));
    }
    return this;
  }

  @Override
  public ComplexMatrix subi(Complex alpha, ComplexMatrix other, Complex beta) {
    assertEqualSize(other);
    for (int i = 0; i < size(); i++) {
      put(i, alpha.multiply(get(i)).minus(beta.multiply(other.get(i))));
    }
    return this;
  }

  @Override
  public ComplexMatrix rsub(Complex scalar) {
    return copy().rsubi(scalar);
  }

  @Override
  public ComplexMatrix rsubi(Complex scalar) {
    for (int i = 0; i < size(); i++) {
      put(i, scalar.minus(get(i)));
    }
    return this;
  }

  @Override
  public ComplexMatrix div(ComplexMatrix other) {
    return copy().divi(other);
  }

  @Override
  public ComplexMatrix div(Complex other) {
    return copy().divi(other);
  }

  @Override
  public ComplexMatrix divi(ComplexMatrix other) {
    for (int i = 0; i < size(); i++) {
      put(i, get(i).div(other.get(i)));
    }
    return this;
  }

  @Override
  public ComplexMatrix divi(Complex other) {
    for (int i = 0; i < size(); i++) {
      put(i, get(i).div(other));
    }
    return this;
  }

  @Override
  public ComplexMatrix rdiv(Complex other) {
    return copy().rdivi(other);
  }

  @Override
  public ComplexMatrix rdivi(Complex other) {
    for (int i = 0; i < size(); i++) {
      put(i, other.div(get(i)));
    }
    return this;
  }

  @Override
  public ComplexMatrix negate() {
    return map(Complex::negate);
  }

  @Override
  public double[] asDoubleArray() {
    double[] array = new double[size() * 2];
    for (int i = 0; i < size(); i++) {
      Complex complex = get(i);
      array[i] = complex.real();
      array[i + 1] = complex.imag();
    }
    return array;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    ImmutableTable.Builder<Object, Object, Object> builder = new ImmutableTable.Builder<>();
    for (int i = 0; i < rows(); i++) {
      for (int j = 0; j < columns(); j++) {
        builder.put(i, j, get(i, j));
      }
    }
    Utils.prettyPrintTable(str, builder.build(), 0, 2, false, false);
    str.append("Shape: ").append(getShape());
    return str.toString();
  }

  @Override
  public Iterator<Complex> iterator() {
    return new UnmodifiableIterator<Complex>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public Complex next() {
        return get(current++);
      }
    };
  }
}
