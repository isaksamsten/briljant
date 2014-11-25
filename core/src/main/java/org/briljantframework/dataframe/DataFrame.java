package org.briljantframework.dataframe;

import com.google.common.collect.ImmutableTable;
import org.briljantframework.Utils;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.Binary;
import org.briljantframework.vector.Complex;
import org.briljantframework.vector.Vector;

import java.util.Collection;

/**
 * Created by Isak Karlsson on 21/11/14.
 */
public interface DataFrame {
    /**
     * To string.
     *
     * @param dataFrame the dataset
     * @return the string
     */
    public static String toString(DataFrame dataFrame) {
        return toString(dataFrame, 10);
    }

    /**
     * To string.
     *
     * @param dataFrame the dataset
     * @param max       the max
     * @return the string
     */
    public static String toString(DataFrame dataFrame, int max) {
        ImmutableTable.Builder<Object, Object, Object> b = ImmutableTable.builder();
        b.put(0, 0, " ");
        for (int i = 0; i < dataFrame.columns(); i++) {
            b.put(0, i + 1, dataFrame.getColumnName(i));
        }

        b.put(1, 0, " ");
        for (int i = 0; i < dataFrame.columns(); i++) {
            b.put(1, i + 1, dataFrame.getColumn(i).getType());
        }

        for (int i = 0; i < dataFrame.rows() && i < max; i++) {
            b.put(i + 2, 0, String.format("[%d,]   ", i));
            for (int j = 0; j < dataFrame.columns(); j++) {
                b.put(i + 2, j + 1, dataFrame.getColumn(j).toString(i));
            }
        }

        StringBuilder builder = new StringBuilder(dataFrame.getClass().getSimpleName()).append(" (").append(dataFrame.rows()).append("x").append(dataFrame.columns()).append(")\n");
        Utils.prettyPrintTable(builder, b.build(), 1, 2, false, false);
        return builder.toString();
    }

    /**
     * Get value at {@code row} and {@code column} as string.
     *
     * @param row    the row
     * @param column the column
     * @return the value
     */
    String getAsString(int row, int column);

    /**
     * Get value at {@code row} and {@code column} as double.
     *
     * @param row    the row
     * @param column the column
     * @return the value
     */
    double getAsDouble(int row, int column);

    /**
     * Get value at {@code row} and {@code column} as int.
     *
     * @param row    the row
     * @param column the column
     * @return the value
     */
    int getAsInt(int row, int column);

    /**
     * Get value at {@code row} and {@code column} as binary.
     *
     * @param row    the row
     * @param column the column
     * @return the value
     */
    Binary getAsBinary(int row, int column);

    /**
     * Get value at {@code row} and {@code column} as complex.
     *
     * @param row    the row
     * @param column the column
     * @return the value
     */
    Complex getAsComplex(int row, int column);

    /**
     * Returns true if value at {@code row, column} is NA.
     *
     * @param row    the row
     * @param column the column
     * @return true or false
     */
    boolean isNA(int row, int column);

    /**
     * Get vector at {@code column}
     *
     * @param index the index
     * @return the vector
     */
    Vector getColumn(int index);

    /**
     * Get the name for the column vector at {@code index}.
     *
     * @param index the index
     * @return the name
     */
    String getColumnName(int index);

    /**
     * Returns the number of rows in this data frame
     *
     * @return the number of rows
     */
    int rows();

    /**
     * Returns the number of columns in this data frame
     *
     * @return the number of columns
     */
    int columns();


    DataFrame newDataFrame(Collection<? extends Vector> vectors);

    /**
     * Creates a new builder with the same column types as this data frame
     *
     * @return a new builder
     */
    Builder newBuilder();

    /**
     * Creates a new builder with the same column types as this data frame
     * with {@code rows} rows, all initialized to NA
     *
     * @param rows initial size
     * @return a new builder
     */
    Builder newBuilder(int rows);

    /**
     * Creates a new builder, initialized with a copy of this data frame, i.e.
     * {@code c.newCopyBuilder().create()} creates a new copy.
     *
     * @return a new builder
     */
    Builder newCopyBuilder();

    /**
     * Returns this as a matrix of double values.
     *
     * @return this data frame as a matrix
     */
    Matrix asMatrix();

    /**
     * Since DataFrames are immutable, this builder allows for the creation of new data frames
     */
    interface Builder {

        /**
         * Set value at {@code row} in {@code column} to NA.
         *
         * @param row    the row
         * @param column the column
         * @return a modified builder
         * @see org.briljantframework.vector.Vector.Builder#setNA(int)
         */
        Builder setNA(int row, int column);

        /**
         * Add a new NA value to {@code column}.
         *
         * @param column the column
         * @return a modified builder
         * @see org.briljantframework.vector.Vector.Builder#addNA()
         */
        Builder addNA(int column);

        /**
         * Set value at {@code row, toCol} using the value at {@code fromRow, fromCol} in {@code from}.
         *
         * @param toRow   the row
         * @param toCol   the column
         * @param from    the vector
         * @param fromRow the row
         * @param fromCol the column
         * @return a modified builder
         * @see org.briljantframework.vector.Vector.Builder#set(int, org.briljantframework.vector.Vector, int)
         */
        Builder set(int toRow, int toCol, DataFrame from, int fromRow, int fromCol);

        /**
         * Add value to {@code toCol} using value {@code fromRow, fromCol} in {@code vector}.
         *
         * @param toCol   the column
         * @param from    the vector
         * @param fromRow the row
         * @param fromCol the column
         * @return a modified builder
         * @see org.briljantframework.vector.Vector.Builder#add(org.briljantframework.vector.Vector, int)
         */
        Builder add(int toCol, DataFrame from, int fromRow, int fromCol);

        /**
         * Add the value {@code fromRow} from {@code from} to {@code toCol}.
         *
         * @param toCol   the column
         * @param from    the vector
         * @param fromRow the column
         * @return a modified builder
         */
        Builder add(int toCol, Vector from, int fromRow);

        /**
         * Set value at {@code row, column} to {@code value}.
         *
         * @param row    the row
         * @param column the column
         * @param value  the value
         * @return a modified builder
         * @see org.briljantframework.vector.Vector.Builder#set(int, Object)
         */
        Builder set(int row, int column, Object value);

        /**
         * Add value to {@code column}
         *
         * @param col   the column
         * @param value the value
         * @return a modified builder
         */
        Builder add(int col, Object value);

        /**
         * Add a new vector builder. If {@code builder.size() < rows()} the builder is padded with NA.
         *
         * @param builder builder to add
         * @return a modified builder
         */
        Builder addColumn(Vector.Builder builder);

        /**
         * Removes vector builder at {@code column}.
         *
         * @param column the index
         * @return a modified builder
         */
        Builder removeColumn(int column);

        /**
         * Swaps column vector {@code a} and {@code b}.
         *
         * @param a an index
         * @param b an index
         * @return a modified builder
         */
        Builder swapColumns(int a, int b);

        /**
         * Add all values in {@code vector} to column {@code toCol}.
         *
         * @param toCol  the index
         * @param vector the vector
         * @return a modified builder
         */
        default Builder addAll(int toCol, Vector vector) {
            for (int i = 0; i < vector.size(); i++) {
                add(toCol, vector, i);
            }
            return this;
        }

        /**
         * Add all values from frame (from column 0 until column())
         *
         * @param frame the frame
         * @return a modified builder
         */
        default Builder addAll(DataFrame frame) {
            for (int i = 0; i < columns(); i++) {
                addAll(i, frame.getColumn(i));
            }
            return this;
        }

        /**
         * Returns the number of columns in the resulting data frame
         *
         * @return the number of columns
         */
        int columns();

        /**
         * Returns the number of rows in the resulting data frame
         *
         * @return the number of rows
         */
        int rows();

        /**
         * Create a new DataFrame.
         *
         * @return a new data frame
         */
        DataFrame create();
    }

}
