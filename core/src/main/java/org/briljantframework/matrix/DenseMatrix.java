/*
 * ADEB - machine learning pipelines made easy
 * Copyright (C) 2014  Isak Karlsson
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.briljantframework.matrix;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableTable;
import org.briljantframework.Utils;
import org.briljantframework.matrix.math.Javablas;
import org.briljantframework.matrix.slice.Index;
import org.briljantframework.matrix.slice.Range;
import org.briljantframework.matrix.slice.Slice;
import org.briljantframework.matrix.slice.Slicer;

import java.util.Arrays;
import java.util.function.DoubleUnaryOperator;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Created by Isak Karlsson on 13/06/14.
 */
public class DenseMatrix extends AbstractDenseMatrix implements Matrix {

    /**
     * Instantiates a new Dense matrix.
     *
     * @param columns the columns
     * @param values  the values
     */
    public DenseMatrix(int columns, double[] values) {
        this(values.length / columns, columns, values);
    }

    /**
     * Instantiates a new Dense matrix.
     *
     * @param rows    the rows
     * @param columns the columns
     * @param values  the values
     */
    public DenseMatrix(int rows, int columns, double[] values) {
        super(rows, columns, values);
    }

    /**
     * Instantiates a new Dense matrix.
     *
     * @param shape the shape
     */
    public DenseMatrix(Shape shape) {
        this(shape.rows, shape.columns);
    }

    /**
     * Instantiates a new Dense matrix.
     *
     * @param rows in matrix
     * @param cols columns in matrix
     */
    public DenseMatrix(int rows, int cols) {
        this(rows, cols, new double[Math.multiplyExact(rows, cols)]);
    }

    /**
     * Instantiates a new Dense matrix.
     *
     * @param shape  the shape
     * @param values the values
     */
    public DenseMatrix(Shape shape, double[] values) {
        this(shape.rows, shape.columns, values);
    }

    /**
     * Instantiates a new Dense matrix.
     *
     * @param tensor the tensor like
     */
    public DenseMatrix(MatrixLike tensor) {
        this(tensor.getShape(), tensor);
    }

    /**
     * Copy the tensor (as a Matrix)
     *
     * @param shape  the shape
     * @param tensor to copy
     */
    public DenseMatrix(Shape shape, MatrixLike tensor) {
        this(shape.rows, shape.columns);
        if (!hasCompatibleShape(tensor.getShape())) {
            throw new MismatchException("DenseMatrix", "cant fit tensor");
        }
        System.arraycopy(tensor.asDoubleArray(), 0, values, 0, this.cols * this.rows);
    }

    /**
     * Instantiates a new Dense matrix.
     *
     * @param values the values
     */
    public DenseMatrix(double[][] values) {
        this(values.length, values[0].length);
        for (int i = 0; i < values.length; i++) {
            for (int j = 0; j < values[i].length; j++) {
                put(i, j, values[i][j]);
            }
        }
    }

    /**
     * Filled with.
     *
     * @param rows  number of rows
     * @param cols  number of columns
     * @param value fill matrix with
     * @return a new matrix filled with <code>value</code>
     */
    public static DenseMatrix filledWith(int rows, int cols, double value) {
        DenseMatrix m = new DenseMatrix(rows, cols);
        Arrays.fill(m.values, value);
        return m;
    }

    /**
     * With size.
     *
     * @param rows the rows
     * @param cols the cols
     * @return the builder
     */
    public static Builder withSize(int rows, int cols) {
        return new Builder(rows, cols);
    }

    /**
     * With rows.
     *
     * @param rows the rows
     * @return the builder
     */
    public static Builder withRows(int rows) {
        return new Builder(rows, 1);
    }

    /**
     * With columns.
     *
     * @param columns the columns
     * @return the builder
     */
    public static Builder withColumns(int columns) {
        return new Builder(1, columns);
    }

    /**
     * From row order.
     *
     * @param rows the rows
     * @param cols the cols
     * @param args the args
     * @return dense matrix
     */
    public static DenseMatrix fromRowOrder(int rows, int cols, double... args) {
        return of(rows, cols, args);
    }

    /**
     * Mostly for convenience when writing matrices in code.
     * <p>
     * <pre>
     *     DenseMatrix.of(2, 3,
     *          1, 2, 3,
     *          4, 5, 6
     *     );
     * </pre>
     * <p>
     * Compared to:
     * <p>
     * <pre>
     *     DenseMatrix.fromColumnOrder(2, 3,
     *          1, 4
     *          2, 5
     *          3, 6
     *    )
     * </pre>
     *
     * @param rows number of rows
     * @param cols number of headers
     * @param data in row-major format
     * @return a matrix
     */
    public static DenseMatrix of(int rows, int cols, double... data) {
        Preconditions.checkNotNull(data, "data");
        if (rows * cols != data.length) {
            throw new IllegalArgumentException("rows * headers != data.length");
        }

        // Convert row-major order to column major order
        double[] colOrder = new double[data.length];
        for (int j = 0; j < cols; j++) {
            for (int i = 0; i < rows; i++) {
                colOrder[j * rows + i] = data[i * cols + j];
            }
        }
        return new DenseMatrix(rows, cols, colOrder);
    }

    /**
     * Return a new DenseMatrix
     *
     * @param rows with rows
     * @param cols with columns
     * @param args with values
     * @return the new DenseMatrix
     */
    public static DenseMatrix fromColumnOrder(int rows, int cols, double... args) {
        return new DenseMatrix(rows, cols, args);
    }

    /**
     * Row vector.
     *
     * @param args the args
     * @return the dense matrix
     */
    public static DenseMatrix rowVector(double... args) {
        return new DenseMatrix(args.length, 1, args);
    }

    /**
     * Column vector.
     *
     * @param args the args
     * @return the dense matrix
     */
    public static DenseMatrix columnVector(double... args) {
        return new DenseMatrix(1, args.length, args);
    }

    @Override
    public void put(int i, int j, double value) {
        values[Matrix.columnMajorIndex(i, j, rows(), columns())] = value;
    }

    @Override
    public double get(int i, int j) {
        return values[Matrix.columnMajorIndex(i, j, rows(), columns())];
    }

    @Override
    public void put(int index, double value) {
        checkArgument(index >= 0 && index < values.length);
        values[index] = value;
    }

    @Override
    public int size() {
        return rows() * columns();
    }

    @Override
    public double get(int index) {
        checkArgument(index >= 0 && index < values.length);
        return values[index];
    }

    /**
     * @return a copy of this matrix
     */
    public DenseMatrix copy() {
        DenseMatrix m = new DenseMatrix(this.rows(), this.columns());
        System.arraycopy(values, 0, m.values, 0, values.length);
        return m;
    }

    /**
     * Fill void.
     *
     * @param value the value
     */
    public void fill(double value) {
        Arrays.fill(values, value);
    }

    /**
     * Map matrix.
     *
     * @param operator the operator
     * @return the matrix
     */
    public Matrix map(DoubleUnaryOperator operator) {
        DenseMatrix n = new DenseMatrix(this.rows(), this.columns());
        double[] values = n.asDoubleArray(), array = asDoubleArray();
        for (int i = 0; i < array.length; i++) {
            values[i] = operator.applyAsDouble(array[i]);
        }

        return n;
    }

    /**
     * Rows matrix.
     *
     * @param indices the indices
     * @return the matrix
     */
    public Matrix rows(int[] indices) {
        return getRows(Index.of(indices));
    }

    @Override
    public DenseMatrix getRow(int index) {
        double[] values = new double[columns()];
        for (int i = 0; i < values.length; i++) {
            values[i] = get(index, i);
        }

        return new DenseMatrix(1, columns(), values);
    }

    @Override
    public Matrix getRows(int start, int end) {
        return getRows(Range.exclusive(start, end));
    }

    @Override
    public Matrix getRows(Slicer slicer) {
        if (slicer.length() > this.rows()) {
            throw new MismatchException("slicer", "longer than number of rows");
        }
        Slice slice = slicer.getSlice();
        DenseMatrix m = new DenseMatrix(slicer.length(), this.columns());
        int newI = 0, rows = this.rows;
        while (slice.hasNext(rows)) {
            int i = slice.next();
            for (int j = 0; j < this.columns(); j++) {
                m.put(newI, j, get(i, j));
            }
            newI += 1;
        }
        return m;
    }

    public DenseMatrix dropRow(int row) {
        Preconditions.checkArgument(row >= 0 && row < rows());
        DenseMatrix matrix = new DenseMatrix(rows() - 1, columns());
        for (int j = 0; j < columns(); j++) {
            for (int i = 0; i < rows(); i++) {
                if (i != row) {
                    matrix.put(i - 1, j, get(i, j));
                }
            }
        }

        return matrix;
    }

    public DenseMatrix getColumn(int index) {
        if (index > columns()) {
            throw new IllegalArgumentException("index > headers()");
        }
        double[] col = new double[this.rows()];
        for (int i = 0; i < this.rows(); i++) {
            col[i] = get(i, index);
        }

        return new DenseMatrix(rows(), 1, col);
    }

    @Override
    public Matrix getColumns(int start, int end) {
        throw new UnsupportedOperationException("not implemented");
    }

    @Override
    public DenseMatrix getColumns(Slicer slicer) {
        if (slicer.length() > this.columns()) {
            throw new MismatchException("column", "slice longer than number of columns");
        }
        DenseMatrix m = new DenseMatrix(this.rows(), slicer.length());
        Slice slice = slicer.getSlice();

        slice.rewind();
        for (int i = 0; i < this.rows(); i++) {
            int newJ = 0;
            while (slice.hasNext(this.columns())) {
                int j = slice.next();
                m.put(i, newJ++, get(i, j));
            }
            slice.rewind();
        }
        return m;
    }

    /**
     * Drop column.
     *
     * @param col the col
     * @return matrix matrix
     */
    public Matrix dropColumn(int col) {
        checkArgument(col > 0 && col < columns());

        DenseMatrix m = new DenseMatrix(rows(), columns() - 1);
        for (int i = 0; i < rows(); i++) {
            for (int j = 0; j < m.columns(); j++) {
                if (j != col) {
                    m.put(i, j - 1, this.get(i, j));
                }
            }
        }
        return m;
    }

    @Override
    public DenseMatrix slice(Slicer rows, Slicer cols) {
        if (rows.length() <= 0 || rows.length() > this.rows()) {
            throw new IllegalArgumentException("cannot slice more rows than there are rows");
        }

        if (cols.length() <= 0 || cols.length() > this.columns()) {
            throw new IllegalArgumentException("cannot slice more columns than there are colums");
        }

        DenseMatrix result = new DenseMatrix(rows.length(), cols.length());
        Slice colSlice = cols.getSlice();
        Slice rowSlice = rows.getSlice();

        int newI = 0;
        while (rowSlice.hasNext(this.rows())) {
            int i = rowSlice.next();
            int newJ = 0;
            while (colSlice.hasNext(this.columns())) {
                int j = colSlice.next();
                result.put(newI, newJ++, get(i, j));
            }
            newI++;
            colSlice.rewind();
        }
        return result;
    }

    /**
     * @param diagonal the diagonal
     * @return
     */
    @Override
    public DenseMatrix mmuld(Diagonal diagonal) {
        //        if (diagonal.rows() != this.columns()) {
        //            throw new NonConformantException(this, diagonal);
        //        }
        //        DenseMatrix y = new DenseMatrix(this.rows(), diagonal.columns());
        //
        //        int rows = y.rows();
        //        for (int column = 0; column < this.columns(); column++) {
        //            for (int row = 0; row < rows; row++) {
        //                double xv = this.get(row, column);
        //                double dv = diagonal.get(column);
        //                y.put(row, column, xv * dv);
        //            }
        //        }
        //        return y;
        return Matrices.mdmul(DenseMatrix::new, this, diagonal);
    }

    /**
     * @return the transpose of this matrix
     */
    public DenseMatrix transpose() {
        int rows = this.rows(), cols = this.columns();
        DenseMatrix matrix = new DenseMatrix(cols, rows);
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                matrix.put(j, i, get(i, j));
            }
        }
        return matrix;
    }

    /**
     * Multiply this matrix with other
     *
     * @param other matrix
     * @return a new matrix
     * @throws org.briljantframework.matrix.NonConformantException
     */
    @Override
    public DenseMatrix mmul(MatrixLike other) throws NonConformantException {
        return Matrices.mmul(DenseMatrix::new, this, other);
    }

    @Override
    public Matrix mul(MatrixLike other) {
        Preconditions.checkArgument(hasCompatibleShape(other.getShape()));
        return muli(1, other, 1);
    }

    public DenseMatrix muli(double scalar) {
        Javablas.mul(values, scalar, values);
        return this;
    }

    @Override
    public DenseMatrix muli(MatrixLike other) {
        return muli(1.0, other, 1.0);
    }

    @Override
    public DenseMatrix add(MatrixLike other) {
        return add(1, other, 1);
    }

    @Override
    public DenseMatrix add(double scalar) {
        DenseMatrix tmp = new DenseMatrix(this.rows(), this.columns());
        Javablas.add(values, scalar, tmp.asDoubleArray());
        return tmp;
    }

    @Override
    public DenseMatrix addi(MatrixLike other) {
        addi(1, other, 1);
        return this;
    }

    @Override
    public DenseMatrix addi(double scalar) {
        Javablas.add(values, scalar, values);
        return this;
    }

    @Override
    public DenseMatrix sub(MatrixLike other) {
        return sub(1, other, 1);
    }

    @Override
    public DenseMatrix sub(double scalar) {
        DenseMatrix tmp = new DenseMatrix(this.rows(), this.columns());
        Javablas.sub(values, scalar, tmp.asDoubleArray());
        return tmp;
    }

    @Override
    public DenseMatrix subi(MatrixLike other) {
        addi(1, other, -1);
        return this;
    }

    public DenseMatrix subi(double scalar) {
        addi(-scalar);
        return this;
    }

    @Override
    public Matrix rsub(MatrixLike other) {
        DenseMatrix n = new DenseMatrix(getShape());
        Javablas.sub(other.asDoubleArray(), 1, asDoubleArray(), 1, n.asDoubleArray());
        return n;
    }

    @Override
    public Matrix rsub(double scalar) {
        DenseMatrix n = new DenseMatrix(rows(), columns());
        Javablas.sub(scalar, asDoubleArray(), n.asDoubleArray());
        return n;
    }

    @Override
    public Matrix rsubi(MatrixLike other) {
        Javablas.sub(other.asDoubleArray(), 1, asDoubleArray(), 1, asDoubleArray());
        return this;
    }

    @Override
    public Matrix rsubi(double scalar) {
        Javablas.sub(scalar, asDoubleArray(), asDoubleArray());
        return this;
    }

    @Override
    public DenseMatrix div(MatrixLike other) {
        double[] result = new double[rows * cols];
        Javablas.div(values, 1.0, other.asDoubleArray(), 1.0, result);
        return new DenseMatrix(this.rows(), this.columns(), result);
    }

    /**
     * Multiply this matrix with a scalar
     *
     * @param scalar to multiply
     * @return a new matrix with the values multiplied
     */
    @Override
    public DenseMatrix mul(double scalar) {
        return Matrices.mul(DenseMatrix::new, this, scalar);
    }

    @Override
    public DenseMatrix divi(MatrixLike other) {
        Javablas.div(values, 1, other.asDoubleArray(), 1, values);
        return this;
    }

    @Override
    public DenseMatrix divi(double other) {
        muli(1 / other);
        return this;
    }

    @Override
    public DenseMatrix rdiv(MatrixLike other) {
        double[] result = new double[rows * cols];
        Javablas.div(other.asDoubleArray(), 1.0, asDoubleArray(), 1.0, result);
        return new DenseMatrix(this.rows(), this.columns(), result);
    }

    @Override
    public DenseMatrix rdivi(MatrixLike other) {
        Javablas.div(other.asDoubleArray(), 1.0, asDoubleArray(), 1.0, asDoubleArray());
        return this;
    }

    @Override
    public DenseMatrix rdiv(double other) {
        return Matrices.div(DenseMatrix::new, other, this);
    }

    @Override
    public DenseMatrix rdivi(double other) {
        throw new UnsupportedOperationException();
    }

    @Override
    public DenseMatrix negate() {
        DenseMatrix n = new DenseMatrix(getShape());
        for (int j = 0; j < columns(); j++) {
            for (int i = 0; i < rows(); i++) {
                n.put(i, j, -get(i, j));
            }
        }
        return n;
    }

    /**
     * Subtract dense matrix.
     *
     * @param alpha the alpha
     * @param other the other
     * @param beta  the beta
     * @return dense matrix
     */
    public DenseMatrix sub(double alpha, MatrixLike other, double beta) {
        if (!hasEqualShape(other)) {
            throw new NonConformantException(this, other);
        }
        DenseMatrix tmp = new DenseMatrix(this.rows(), this.columns());
        Javablas.sub(values, alpha, other.asDoubleArray(), beta, tmp.asDoubleArray());
        return tmp;
    }

    /**
     * Add inplace.
     *
     * @param alpha the alpha
     * @param other the other
     * @param beta  the beta
     * @return the dense matrix
     */
    public DenseMatrix addi(double alpha, MatrixLike other, double beta) {
        if (!hasEqualShape(other)) {
            throw new NonConformantException(this, other);
        }
        Javablas.add(values, alpha, other.asDoubleArray(), beta, values);
        return this;
    }

    /**
     * Add dense matrix.
     *
     * @param alpha the alpha
     * @param other the other
     * @param beta  the beta
     * @return the dense matrix
     */
    public DenseMatrix add(double alpha, MatrixLike other, double beta) {
        if (!hasEqualShape(other)) {
            throw new NonConformantException(this, other);
        }
        DenseMatrix tmp = new DenseMatrix(this.rows(), this.columns());
        Javablas.add(values, alpha, other.asDoubleArray(), beta, tmp.asDoubleArray());
        return tmp;
    }

    /**
     * Elementwise multiply.
     *
     * @param alpha the alpha
     * @param other the other
     * @param beta  the beta
     * @return dense matrix
     */
    public DenseMatrix muli(double alpha, MatrixLike other, double beta) {
        double[] result = new double[rows * cols];
        Javablas.mul(values, alpha, other.asDoubleArray(), beta, result);
        return new DenseMatrix(this.rows(), this.columns(), result);
    }

    /**
     * Multiply this matrix with <code>other</code> scaling <code>this</code> with <code>alpha</code> and other with
     * <code>beta</code>
     *
     * @param alpha scaling factor for this
     * @param other matrix
     * @param beta  scaling factor for other
     * @return a new Matrix
     */
    public DenseMatrix mmul(double alpha, MatrixLike other, double beta) {
        if (this.columns() != other.rows()) {
            throw new NonConformantException(this, other);
        }

        return Matrices.mmul(DenseMatrix::new, this, alpha, other, beta);
    }

    /**
     * Subtract inplace.
     *
     * @param alpha the alpha
     * @param other the other
     * @param beta  the beta
     * @return the matrix
     */
    public Matrix subi(double alpha, Matrix other, double beta) {
        addi(alpha, other, -1 * beta);
        return this;
    }

    /**
     * Reshape inplace.
     *
     * @param rows the rows
     * @param cols the cols
     */
    public void reshapei(int rows, int cols) {
        if (rows * cols != rows() * columns()) {
            throw new MismatchException("reshapeInplace", String.format("can't reshape %s tensor into %s tensor",
                    getShape(), Shape.of(rows, cols)));
        }
        this.rows = rows;
        this.cols = cols;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(values);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AbstractDenseMatrix) {
            AbstractDenseMatrix o = (AbstractDenseMatrix) obj;
            if (o.rows() == rows() && o.columns() == columns()) {
                int rows = this.rows(), cols = this.columns();
                double[] values = this.values, ovalues = o.values;
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        if (values[Matrix.columnMajorIndex(i, j, rows(), columns())] !=
                                ovalues[Matrix.columnMajorIndex(i, j, rows(), columns())]) {
                            return false;
                        }
                    }
                }
                return true;
            }
            return false;
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
                    builder.put(i, j, String.format("%1.4f", get(i, j)));
                } else {
                    builder.put(i, j, String.format(" %1.4f", get(i, j)));
                }
            }
        }
        StringBuilder out = new StringBuilder("DenseMatrix\n");
        Utils.prettyPrintTable(out, builder.build(), 0, 2, false, false);
        out.append("Shape: ").append(getShape());
        return out.toString();
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        private final int rows, cols;
        private double[][] values;
        private int currentRow = 0;

        /**
         * Instantiates a new Builder.
         *
         * @param rows the rows
         * @param cols the cols
         */
        public Builder(int rows, int cols) {
            this.rows = rows;
            this.cols = cols;
        }

        /**
         * Row builder.
         *
         * @param args the args
         * @return the builder
         */
        public Builder row(double... args) {
            if (args.length != cols) {
                throw new IllegalArgumentException(String.format("Expecting %d rows but got %d", rows, args.length));
            }
            initialize();
            if (currentRow < rows) {
                values[currentRow++] = args;
            } else {
                throw new IllegalArgumentException(String.format("To many rows"));
            }
            return this;
        }

        private void initialize() {
            if (values == null) {
                values = new double[rows][cols];
            }
        }

        /**
         * Create dense matrix.
         *
         * @return the dense matrix
         */
        public DenseMatrix create() {
            return new DenseMatrix(values);
        }

        /**
         * With values.
         *
         * @param args the args
         * @return the dense matrix
         */
        public DenseMatrix withValues(double... args) {
            return DenseMatrix.of(rows, cols, args);
        }
    }
}