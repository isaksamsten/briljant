package org.briljantframework.matrix;

import org.briljantframework.data.column.ColumnView;
import org.briljantframework.matrix.dataset.MatrixDataFrame;

/**
 * Created by Isak Karlsson on 16/11/14.
 */
public class ColumnVector extends ColumnView implements MatrixLike {

    private final int size;

    /**
     * Instantiates a new Vector view.
     *
     * @param parent the parent
     * @param column the row
     */
    public ColumnVector(MatrixDataFrame parent, int column) {
        super(parent, column);
        this.size = parent.columns();
    }

    private MatrixDataFrame getFrame() {
        return (MatrixDataFrame) dataFrame;
    }

    @Override
    public void put(int i, int j, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public double get(int i, int j) {
        return getFrame().get(i, index);
    }

    @Override
    public void put(int index, double value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Matrix copy() {
        double[] array = new double[size];
        for (int i = 0; i < size(); i++) {
            array[i] = get(i);
        }
        return new DenseMatrix(columns(), rows(), array);
    }

    @Override
    public int rows() {
        return size();
    }

    @Override
    public int columns() {
        return 1;
    }

    @Override
    public double get(int index) {
        return getFrame().get(index, this.index);
    }

    @Override
    public double[] asDoubleArray() {
        return copy().asDoubleArray();
    }

    public Matrix asColumnVector() {
        return copy();
    }
}
