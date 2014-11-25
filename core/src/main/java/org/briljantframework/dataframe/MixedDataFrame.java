package org.briljantframework.dataframe;

import com.google.common.base.Preconditions;
import org.briljantframework.matrix.DenseMatrix;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.Binary;
import org.briljantframework.vector.Complex;
import org.briljantframework.vector.Vector;

import java.util.*;
import java.util.stream.Stream;

/**
 * A mixed (i.e. heterogeneous) data frame contains vectors of possibly different types.
 * <p>
 * Created by Isak Karlsson on 21/11/14.
 */
public class MixedDataFrame implements DataFrame {

    private final List<String> names;
    private final List<Vector> columns;
    private final int rows;

    /**
     * Constructs a new mixed data frame from balanced vectors
     *
     * @param columns the vectors of values
     */
    public MixedDataFrame(Vector... columns) {
        this(Arrays.asList(columns));
    }

    protected MixedDataFrame(Collection<? extends Vector> vectors) {
        Preconditions.checkArgument(vectors.size() > 0);
        Iterator<? extends Vector> it = vectors.iterator();
        names = new ArrayList<>(vectors.size());
        columns = new ArrayList<>(vectors.size());
        names.add("0");

        Vector vector = it.next();
        columns.add(vector);
        rows = vector.size();

        int index = 1;
        while (it.hasNext()) {
            names.add(String.valueOf(index++));
            vector = it.next();
            columns.add(vector);
            Preconditions.checkArgument(vector.size() == rows, "Column vector lengths does not match.");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getAsString(int row, int column) {
        return columns.get(column).getAsString(row);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public double getAsDouble(int row, int column) {
        return columns.get(column).getAsDouble(row);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getAsInt(int row, int column) {
        return columns.get(column).getAsInt(row);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Binary getAsBinary(int row, int column) {
        return columns.get(column).getAsBinary(row);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Complex getAsComplex(int row, int column) {
        return columns.get(column).getAsComplex(row);
    }

    @Override
    public boolean isNA(int row, int column) {
        return columns.get(column).isNA(row);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Vector getColumn(int index) {
        return columns.get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getColumnName(int index) {
        return names.get(index);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int rows() {
        return rows;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int columns() {
        return columns.size();
    }

    @Override
    public DataFrame newDataFrame(Collection<? extends Vector> vectors) {
        return new MixedDataFrame(vectors);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Builder newBuilder() {
        return new Builder(this, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Builder newBuilder(int rows) {
        return new Builder(this, rows, columns());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Builder newCopyBuilder() {
        return new Builder(this, true);
    }

    @Override
    public Matrix asMatrix() {
        Matrix matrix = new DenseMatrix(rows(), columns());
        for (int j = 0; j < columns(); j++) {
            for (int i = 0; i < rows(); i++) {
                matrix.put(i, j, getAsDouble(i, j));
            }
        }

        return matrix;
    }

    @Override
    public String toString() {
        return DataFrame.toString(this, 100);
    }

    /**
     * Type for constructing a new MixedDataFrame by mutation.
     */
    public static class Builder implements DataFrame.Builder {

        private List<Vector.Builder> buffers = null;
        private List<String> colNames = null;

        public Builder(Vector.Type... types) {
            buffers = new ArrayList<>(types.length);
            colNames = new ArrayList<>(types.length);
            for (int i = 0; i < types.length; i++) {
                buffers.add(types[i].newBuilder());
                colNames.add(String.valueOf(i));
            }
        }

        public Builder(Vector.Builder... builders) {
            int rows = Stream.of(builders).mapToInt(Vector.Builder::size).max().getAsInt();
            this.buffers = new ArrayList<>();
            for (Vector.Builder builder : builders) {
                if (builder.size() < rows) {
                    builder.setNA(rows - 1);
                }

                buffers.add(builder);
            }
        }

        protected Builder(MixedDataFrame frame, boolean copy) {
            buffers = new ArrayList<>(frame.columns());
            colNames = new ArrayList<>(frame.columns());

            ArrayList<Vector> columns = new ArrayList<>(frame.columns);
            for (int i = 0; i < columns.size(); i++) {
                Vector vector = columns.get(i);
                if (copy) {
                    buffers.add(vector.newCopyBuilder());
                } else {
                    buffers.add(vector.newBuilder());
                }
                colNames.add(frame.getColumnName(i));
            }
        }

        private Builder(MixedDataFrame frame, int rows, int columns) {
            buffers = new ArrayList<>(columns);
            for (int i = 0; i < columns; i++) {
                buffers.add(frame.getColumn(i).newBuilder(rows));
            }
        }

        @Override
        public Builder setNA(int row, int column) {
            buffers.get(column).setNA(row);
            return this;
        }

        @Override
        public Builder addNA(int column) {
            buffers.get(column).addNA();
            return this;
        }

        @Override
        public Builder set(int toRow, int toCol, DataFrame from, int fromRow, int fromCol) {
            buffers.get(toCol).set(toRow, from.getColumn(fromCol), fromRow);
            return this;
        }

        @Override
        public Builder add(int toCol, DataFrame from, int fromRow, int fromCol) {
            buffers.get(toCol).add(from.getColumn(fromCol), fromRow);
            return this;
        }

        @Override
        public Builder add(int toCol, Vector from, int fromRow) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Builder set(int row, int column, Object value) {
            buffers.get(column).set(row, value);
            return this;
        }

        @Override
        public DataFrame.Builder add(int col, Object value) {
            buffers.get(col).add(value);
            return this;
        }

        @Override
        public Builder addColumn(Vector.Builder builder) {
            if (colNames != null) {
                colNames.add(String.valueOf(colNames.size()));
            }
            buffers.add(builder);
            return this;
        }

        @Override
        public Builder removeColumn(int column) {
            if (colNames != null) {
                colNames.remove(column);
            }
            buffers.remove(column);
            return this;
        }

        @Override
        public Builder swapColumns(int a, int b) {
            if (colNames != null) {
                Collections.swap(colNames, a, b);
            }
            Collections.swap(buffers, a, b);
            return this;
        }

        @Override
        public int columns() {
            return buffers.size();
        }

        @Override
        public int rows() {
            return buffers.stream().mapToInt(Vector.Builder::size).reduce(0, Integer::max);
        }

        private Vector createVector(Vector.Builder builder, int maximumRows) {
            if (builder.size() < maximumRows) {
                builder.setNA(maximumRows - 1);
            }
            return builder.create();
        }

        @Override
        public DataFrame create() {
            List<Vector> vectors = new ArrayList<>(buffers.size());
            int rows = rows();
            buffers.forEach(x -> vectors.add(createVector(x, rows)));
            return new MixedDataFrame(vectors);
        }
    }
}
