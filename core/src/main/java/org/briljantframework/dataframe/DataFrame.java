package org.briljantframework.dataframe;

import java.io.IOException;
import java.util.Collection;

import org.briljantframework.io.DataInputStream;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.*;

/**
 * <p>
 * A DataFrame is a heterogeneous or homogeneous storage of data.
 * </p>
 * 
 * @author Isak Karlsson
 */
public interface DataFrame extends Iterable<DataFrameRow> {

  /**
   * Get value at {@code row} and {@code column} as string.
   *
   * @param row the row
   * @param column the column
   * @return the value
   */
  String getAsString(int row, int column);

  /**
   * Get value at {@code row} and {@code column} as double.
   *
   * @param row the row
   * @param column the column
   * @return the value
   */
  double getAsDouble(int row, int column);

  /**
   * Get value at {@code row} and {@code column} as int.
   *
   * @param row the row
   * @param column the column
   * @return the value
   */
  int getAsInt(int row, int column);

  /**
   * Get value at {@code row} and {@code column} as binary.
   *
   * @param row the row
   * @param column the column
   * @return the value
   */
  Binary getAsBinary(int row, int column);

  /**
   * Get value at {@code row} and {@code column} as complex.
   *
   * @param row the row
   * @param column the column
   * @return the value
   */
  Complex getAsComplex(int row, int column);

  /**
   * Get value at {@code row} and {@code column} as a value
   *
   * @param row the row
   * @param column the column
   * @return the value
   */
  Value getAsValue(int row, int column);

  /**
   * Returns string representation of value at {@code row, column}
   * 
   * @param row the row
   * @param column the column
   * @return the representation
   */
  String toString(int row, int column);

  /**
   * Returns true if value at {@code row, column} is NA.
   *
   * @param row the row
   * @param column the column
   * @return true or false
   */
  boolean isNA(int row, int column);

  /**
   * Get vector at {@code index}
   *
   * @param index the index
   * @return the vector
   */
  Vector getColumn(int index);

  /**
   * Drop column {@code index}
   * 
   * @param index the index
   * @return a new dataframe
   */
  DataFrame dropColumn(int index);

  /**
   * Drop columns with {@code indexes}
   *
   * @param indexes collection of indexes
   * @return a new dataframe
   */
  DataFrame dropColumns(Collection<Integer> indexes);

  /**
   * Take columns with {@code indexes}
   * 
   * @param indexes collection of indexes
   * @return a new dataframe
   */
  DataFrame takeColumns(Collection<Integer> indexes);

  /**
   * Get the type of vector at {@code index}
   *
   * @param index the index
   * @return the type
   */
  Type getColumnType(int index);

  /**
   * Get the name for the column vector at {@code index}.
   *
   * @param index the index
   * @return the name
   */
  String getColumnName(int index);

  /**
   * Set the name for the column at {@code index}
   * 
   * @param index the index
   * @param columnName the name
   * @return modified receiver to allow for chaining
   */
  DataFrame setColumnName(int index, String columnName);

  /**
   * Get the row at {@code index}. Since a {@code DataFrame} can have columns of multiple types, the
   * returned type is a Sequence i.e. a heterogeneous vector of values.
   *
   * @param index the index
   * @return the row sequence
   */
  DataFrameRow getRow(int index);

  /**
   * Take the rows in {@code indexes}
   * 
   * @param indexes the indexes to take
   * @return a new data frame
   */
  DataFrame takeRows(Collection<Integer> indexes);

  /**
   * Drop rows in {@code indexes} and return a new DataFrame
   * 
   * @param indexes the indexes to drop
   * @return a new data frame
   */
  DataFrame dropRows(Collection<Integer> indexes);

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

  /**
   * Creates a new builder with the same column types as this data frame
   *
   * @return a new builder
   */
  Builder newBuilder();

  /**
   * Creates a new builder with the same column types as this data frame with {@code rows} rows, all
   * initialized to NA
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
   * Returns this as a real valued matrix.
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
     * @param row the row
     * @param column the column
     * @return a modified builder
     * @see org.briljantframework.vector.Vector.Builder#setNA(int)
     */
    Builder setNA(int row, int column);

    /**
     * Set value at {@code row, toCol} using the value at {@code fromRow, fromCol} in {@code from}.
     *
     * @param toRow the row
     * @param toCol the column
     * @param from the vector
     * @param fromRow the row
     * @param fromCol the column
     * @return a modified builder
     * @see org.briljantframework.vector.Vector.Builder#set(int,
     *      org.briljantframework.vector.Vector, int)
     */
    Builder set(int toRow, int toCol, DataFrame from, int fromRow, int fromCol);

    /**
     * Add the value {@code fromRow} from {@code from} to {@code toCol} and {@code toRow}.
     *
     * @param row the row in this
     * @param column the column in this
     * @param from the vector
     * @param index the row in {@code from}
     * @return a modified builder
     */
    Builder set(int row, int column, Vector from, int index);

    /**
     * Set value at {@code row, column} to {@code value}.
     *
     * @param row the row
     * @param column the column
     * @param value the value
     * @return a modified builder
     * @see org.briljantframework.vector.Vector.Builder#set(int, Object)
     */
    Builder set(int row, int column, Object value);

    /**
     * Add a new vector builder as an additional column. If {@code builder.size() < rows()} the
     * added builder is padded with NA.
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
     * Swap value at index {@code a} with value at index {@code b} in column with index
     * {@code column}
     * 
     * @param column the column
     * @param a the first index
     * @param b the second index
     * @return a modified builder
     */
    Builder swapInColumn(int column, int a, int b);

    /**
     * Swap row at index {@code a} with {@code b}.
     * <p>
     * Generally, this is the same as
     * <p>
     * 
     * <pre>
     * for (int i = 0; i &lt; builder.columns(); i++)
     *   builder.swapInColumn(i, a, b);
     * </pre>
     *
     * @param a the first row
     * @param b the second row
     * @return a modified builder
     */
    default Builder swapRows(int a, int b) {
      for (int i = 0; i < columns(); i++) {
        swapInColumn(i, a, b);
      }
      return this;
    }

    /**
     * Add all values in {@code vector} to column {@code toCol}, starting at {@code startRow}. If
     * {@code startRow < rows()}, values are overwritten.
     *
     * @param startRow the start row
     * @param toCol the index
     * @param vector the vector
     * @return a modified builder
     */
    default Builder addAll(int startRow, int toCol, Vector vector) {
      for (int i = 0; i < vector.size(); i++) {
        set(startRow++, toCol, vector, i);
      }
      return this;
    }

    /**
     * Add all values from frame (from column 0 until column()) starting at {@code startRow}. If
     * {@code startRow < rows()}, values are overwritten.
     *
     * @param startRow
     * @param frame the frame
     * @return a modified builder
     */
    default Builder addAll(int startRow, DataFrame frame) {
      for (int i = 0; i < columns(); i++) {
        addAll(startRow, i, frame.getColumn(i));
      }
      return this;
    }

    /**
     * Read values from the {@code inputStream} and add the values to the correct column.
     * 
     * @param inputStream the input stream
     * @return a modified builder
     * @throws IOException
     */
    public Builder read(DataInputStream inputStream) throws IOException;

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
    DataFrame build();
  }

}
