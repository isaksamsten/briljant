package org.briljantframework.matrix.base;

import com.google.common.base.Preconditions;

import org.briljantframework.Check;
import org.briljantframework.IndexComparator;
import org.briljantframework.QuickSort;
import org.briljantframework.Utils;
import org.briljantframework.complex.Complex;
import org.briljantframework.complex.ComplexBuilder;
import org.briljantframework.exceptions.NonConformantException;
import org.briljantframework.matrix.ComplexMatrix;
import org.briljantframework.matrix.Dim;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.matrix.Transpose;
import org.briljantframework.matrix.api.MatrixFactory;
import org.briljantframework.matrix.api.MatrixRoutines;
import org.briljantframework.stat.RunningStatistics;
import org.jetbrains.annotations.NotNull;

import java.util.AbstractList;
import java.util.Collection;
import java.util.List;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkElementIndex;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.briljantframework.matrix.Indexer.columnMajor;
import static org.briljantframework.matrix.Indexer.rowMajor;

/**
 * @author Isak Karlsson
 */
public class BaseMatrixRoutines implements MatrixRoutines {

  private final MatrixFactory matrixFactory;

  protected BaseMatrixRoutines(MatrixFactory matrixFactory) {
    this.matrixFactory = Preconditions.checkNotNull(matrixFactory);
  }

  @Override
  public MatrixFactory getMatrixFactory() {
    return matrixFactory;
  }

  @Override
  public double mean(DoubleMatrix x) {
    return x.reduce(0, Double::sum) / x.size();
  }

  @Override
  public DoubleMatrix mean(DoubleMatrix x, Dim dim) {
    return x.reduceAlongVector(dim, this::mean);
  }

  @Override
  public double var(DoubleMatrix x) {
    RunningStatistics s = new RunningStatistics();
    x.forEach(s::add);
    return s.getVariance();
  }

  @Override
  public DoubleMatrix var(DoubleMatrix x, Dim dim) {
    return x.reduceAlongVector(dim, this::var);
  }

  @Override
  public double std(DoubleMatrix x) {
    return Math.sqrt(var(x));
  }

  @Override
  public DoubleMatrix std(DoubleMatrix x, Dim dim) {
    return x.reduceAlongVector(dim, this::std);
  }

  @Override
  public double min(DoubleMatrix x) {
    return x.reduce(Double.NEGATIVE_INFINITY, Math::min);
  }

  @Override
  public DoubleMatrix min(DoubleMatrix x, Dim dim) {
    return x.reduceAlongVector(dim, this::min);
  }

  @Override
  public double max(DoubleMatrix x) {
    return x.reduce(Double.POSITIVE_INFINITY, Math::max);
  }

  @Override
  public DoubleMatrix max(DoubleMatrix x, Dim dim) {
    return x.reduceAlongVector(dim, this::max);
  }

  @Override
  public double sum(DoubleMatrix x) {
    return x.reduce(0, Double::sum);
  }

  @Override
  public DoubleMatrix sum(DoubleMatrix x, Dim dim) {
    return x.reduceAlongVector(dim, this::sum);
  }

  @Override
  public double prod(DoubleMatrix x) {
    double prod = x.get(0);
    for (int i = 1; i < x.size(); i++) {
      prod *= x.get(i);
    }
    return prod;
  }

  @Override
  public DoubleMatrix prod(DoubleMatrix x, Dim dim) {
    return x.reduceAlongVector(dim, this::prod);
  }

  @Override
  public DoubleMatrix cumsum(DoubleMatrix x) {
    DoubleMatrix n = x.newEmptyMatrix(x.rows(), x.columns());
    double sum = 0;
    for (int i = 0; i < n.size(); i++) {
      sum += x.get(i);
      n.set(i, sum);
    }
    return n;
  }

  @Override
  public DoubleMatrix cumsum(DoubleMatrix x, Dim dim) {
    DoubleMatrix n = x.newEmptyMatrix(x.rows(), x.columns());
    for (int i = 0; i < x.size(dim); i++) {
      n.setVectorAlong(dim, i, cumsum(n.getVectorAlong(dim, i)));
    }

    return n;
  }

  @Override
  public int iamax(DoubleMatrix x) {
    int i = 0;
    double m = Math.abs(x.get(0));
    for (int j = 1; j < x.size(); j++) {
      double d = Math.abs(x.get(j));
      if (d > m) {
        i = j;
        m = d;
      }
    }
    return i;
  }

  @Override
  public double dot(DoubleMatrix a, DoubleMatrix b) {
    Check.size(a, b);
    double s = 0;
    for (int i = 0; i < a.size(); i++) {
      s += a.get(i) * b.get(i);
    }
    return s;
  }

  @Override
  public Complex dotu(ComplexMatrix a, ComplexMatrix b) {
    return null;
  }

  @Override
  public Complex dotc(ComplexMatrix a, ComplexMatrix b) {
    return null;
  }

  @Override
  public double nrm2(DoubleMatrix a) {
    double sum = 0;
    for (int i = 0; i < a.size(); i++) {
      double v = a.get(i);
      sum += v * v;
    }

    return Math.sqrt(sum);
  }

  @Override
  public Complex norm2(ComplexMatrix a) {
    ComplexBuilder c = new ComplexBuilder(a.get(0).pow(2));
    for (int i = 1; i < a.size(); i++) {
      c.plus(a.get(i).pow(2));
    }
    return c.toComplex().sqrt();
  }

  @Override
  public double asum(DoubleMatrix a) {
    double sum = 0;
    for (int i = 0; i < a.size(); i++) {
      sum += Math.abs(a.get(i));
    }
    return sum;
  }

  @Override
  public double asum(ComplexMatrix a) {
    double s = 0;
    for (int i = 0; i < a.size(); i++) {
      s += a.get(i).abs();
    }
    return s;
  }

  @Override
  public int iamax(ComplexMatrix x) {
    return 0;
  }

  @Override
  public void axpy(double alpha, DoubleMatrix x, DoubleMatrix y) {
    Check.equalShape(x, y);
    if (alpha != 0) {
      int size = x.size();
      for (int i = 0; i < size; i++) {
        y.set(i, alpha * x.get(i) + y.get(i));
      }
    }
  }

  @Override
  public void gemv(Transpose transA, double alpha, DoubleMatrix a, DoubleMatrix x, double beta,
                   DoubleMatrix y) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void ger(double alpha, DoubleMatrix x, DoubleMatrix y, DoubleMatrix a) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void gemm(Transpose transA, Transpose transB,
                   double alpha, DoubleMatrix a, DoubleMatrix b,
                   double beta, DoubleMatrix c) {

    int thisRows = a.rows();
    int thisCols = a.columns();
    if (transA.transpose()) {
      thisRows = a.columns();
      thisCols = a.rows();
    }
    int otherRows = b.rows();
    int otherColumns = b.columns();
    if (transB.transpose()) {
      otherRows = b.columns();
      otherColumns = b.rows();
    }

    if (thisCols != otherRows) {
      throw new NonConformantException(thisRows, thisCols, otherRows, otherColumns);
    }

    for (int row = 0; row < thisRows; row++) {
      for (int col = 0; col < otherColumns; col++) {
        double sum = 0.0;
        for (int k = 0; k < thisCols; k++) {
          int thisIndex = transA.transpose() ?
                          rowMajor(row, k, thisRows, thisCols) :
                          columnMajor(row, k, thisRows, thisCols);
          int otherIndex = transB.transpose() ?
                           rowMajor(k, col, otherRows, otherColumns) :
                           columnMajor(k, col, otherRows, otherColumns);
          sum += a.get(thisIndex) * b.get(otherIndex);
        }
        c.set(row, col, alpha * sum + beta * c.get(row, col));
      }
    }
  }

  @Override
  public <T extends Matrix<T>> T repeat(T x, int num) {
    return null;
  }

  @Override
  public <T extends Matrix<T>> T take(T x, int num) {
    if (num < 0 || num > x.size()) {
      throw new IllegalArgumentException();
    }
    T c = x.newEmptyVector(num);
    for (int i = 0; i < num; i++) {
      c.set(i, x, i);
    }
    return c;
  }

  @Override
  public <T extends Matrix<T>> List<T> vsplit(T x, int parts) {
    checkNotNull(x);
    checkArgument(x.rows() % parts == 0, "Parts does not evenly divide rows.");
    int partRows = x.rows() / parts;
    return new AbstractList<T>() {
      @NotNull
      @Override
      public T get(int index) {
        checkElementIndex(index, size());
        T part = x.newEmptyMatrix(partRows, x.columns());
        for (int j = 0; j < part.columns(); j++) {
          for (int i = 0; i < part.rows(); i++) {
            part.set(i, j, x, i + partRows * index, j);
          }
        }
        return part;
      }

      @Override
      public int size() {
        return parts;
      }
    };
  }

  @Override
  public <T extends Matrix<T>> T vstack(Collection<T> matrices) {
    checkArgument(matrices.size() > 0);
    int rows = 0;
    int columns = 0;
    T first = null;
    for (T matrix : matrices) {
      if (first == null) {
        first = matrix;
        columns = first.columns();
      }
      checkArgument(columns == matrix.columns(),
                    "Can't vstack %s with %s.", matrix.getShape(), first.getShape());
      rows += matrix.rows();
    }

    T newMatrix = first.newEmptyMatrix(rows, columns);
    int pad = 0;
    for (T matrix : matrices) {
      for (int j = 0; j < matrix.columns(); j++) {
        for (int i = 0; i < matrix.rows(); i++) {
          newMatrix.set(i + pad, j, matrix, i, j);
        }
      }
      pad += matrix.rows();
    }
    return newMatrix;
  }

  @Override
  public <T extends Matrix<T>> List<T> hsplit(T matrix, int parts) {
    checkNotNull(matrix);
    checkArgument(matrix.rows() % parts == 0, "Parts does not evenly dived columns.");
    int partColumns = matrix.columns() / parts;
    return new AbstractList<T>() {

      @NotNull
      @Override
      public T get(int index) {
        checkElementIndex(index, size());
        T part = matrix.newEmptyMatrix(matrix.rows(), partColumns);
        for (int j = 0; j < part.columns(); j++) {
          for (int i = 0; i < part.rows(); i++) {
            part.set(i, j, matrix, i, j + partColumns * index);
          }
        }
        return part;
      }

      @Override
      public int size() {
        return parts;
      }
    };
  }

  @Override
  public <T extends Matrix<T>> T hstack(Collection<T> matrices) {
    checkArgument(matrices.size() > 0);
    int columns = 0;
    int rows = 0;
    T first = null;
    for (T matrix : matrices) {
      if (first == null) {
        first = matrix;
        rows = first.rows();
      }
      Preconditions.checkArgument(rows == matrix.rows(),
                                  "Can't hstack %s with %s.", matrix.getShape(), first.getShape());
      columns += matrix.columns();
    }
    T newMatrix = first.newEmptyMatrix(rows, columns);
    int pad = 0;
    for (T matrix : matrices) {
      for (int j = 0; j < matrix.columns(); j++) {
        for (int i = 0; i < matrix.rows(); i++) {
          newMatrix.set(i, j + pad, matrix, i, j);
        }
      }
      pad += matrix.columns();
    }
    return newMatrix;
  }

  @Override
  public <T extends Matrix<T>> T shuffle(T x) {
    T out = x.copy();
    Utils.permute(out.size(), out);
    return out;
  }

  @Override
  public <T extends Matrix<T>> T sort(T x, IndexComparator<T> cmp) {
    T out = x.copy();
    QuickSort.quickSort(0, out.size(), (a, b) -> cmp.compare(out, a, b), out);
    return out;
  }

  @Override
  public <T extends Matrix<T>> T sort(T x, IndexComparator<T> cmp, Dim dim) {
    T out = x.copy();
    int m = x.size(dim);
    for (int i = 0; i < m; i++) {
      T v = out.getVectorAlong(dim, i);
      QuickSort.quickSort(0, v.size(), (a, b) -> cmp.compare(v, a, b), v);
    }

    return out;
  }

  @Override
  public <T extends Matrix<T>> void copy(T from, T to) {
    Check.equalShape(from, to);
    for (int i = 0; i < from.size(); i++) {
      to.set(i, from, i);
    }
  }

  @Override
  public <T extends Matrix<T>> void swap(T a, T b) {
    Check.equalShape(a, b);
    T tmp = a.newEmptyVector(1);
    for (int i = 0; i < a.size(); i++) {
      tmp.set(0, a, i);
      a.set(i, b, i);
      b.set(i, tmp, 0);
    }
  }
}
