package org.briljantframework.dataframe;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.briljantframework.Swappable;
import org.briljantframework.complex.Complex;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

/**
 * <p>
 * A DataFrame is a heterogeneous or homogeneous storage of data.
 * </p>
 * 
 * <p>
 * While {@code DataFrame} is immutable, {@link #setColumnName(int, String)},
 * {@link #setRecordName(int, String)} are allowed to mutate the receiver. The rationale is simple,
 * chang the names won't affect the validity of {@code DataFrame} usages.
 * </p>
 *
 * @author Isak Karlsson
 */
public interface DataFrame extends Iterable<Record> {

  /**
   * Get the column name attribute
   * 
   * @return the column names
   */
  NameAttribute getColumnNames();

  /**
   * Sets the name of column c<sub>0</sub>...c<sub>names.length</sub>
   *
   * @param names the names
   * @return receiver modified
   */
  DataFrame setColumnNames(List<String> names);

  /**
   * Sets the name of column c<sub>0</sub>...c<sub>names.length</sub>
   *
   * @param names the names
   * @return receiver modified
   */
  default DataFrame setColumnNames(String... names) {
    return setColumnNames(Arrays.asList(names));
  }

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
  Bit getAsBinary(int row, int column);

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
   * Return a collection of columns
   *
   * @return an (immutable) collection of columns
   */
  Collection<Vector> getColumns();

  /**
   * Get vector at {@code index}
   *
   * @param index the index
   * @return the vector
   */
  Vector getColumn(int index);

  /**
   * Uses the column name to lookup a specified column.
   *
   * @param name the column name
   * @return the column
   */
  Vector getColumn(String name);

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
  DataFrame dropColumns(Iterable<Integer> indexes);

  /**
   * Take columns with {@code indexes}
   *
   * @param indexes collection of indexes
   * @return a new dataframe
   */
  DataFrame takeColumns(Iterable<Integer> indexes);

  /**
   * Get the type of vector at {@code index}
   *
   * @param index the index
   * @return the type
   */
  VectorType getColumnType(int index);

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
   * Get the name for the row at {@code index}
   * 
   * @param index the index
   * @return the name
   */
  String getRecordName(int index);

  /**
   * Set the name for the row at {@code index} to {@code rowName}
   * 
   * @param index the index
   * @param rowName the row name
   * @return receiver modified
   */
  DataFrame setRecordName(int index, String rowName);

  /**
   * Sets the name of column c<sub>0</sub>...c<sub>names.length</sub>
   *
   * @param names the names
   * @return receiver modified
   */
  default DataFrame setRecordNames(String... names) {
    return setRecordNames(Arrays.asList(names));
  }

  /**
   * Sets the name of column c<sub>0</sub>...c<sub>names.length</sub>
   *
   * @param names the names
   * @return receiver modified
   */
  DataFrame setRecordNames(List<String> names);

  /**
   * Get the type of the row at {@code index}
   * 
   * @param index the index
   * @return the type
   */
  VectorType getRecordType(int index);

  /**
   * Returns a collection of rows
   * 
   * @return an (immutable) collection of rows
   */
  Collection<Record> getRecords();

  /**
   * Get the row at {@code index}. Since a {@code DataFrame} can have columns of multiple types, the
   * returned type is a Sequence i.e. a heterogeneous vector of values.
   *
   * @param index the index
   * @return the row sequence
   */
  Record getRecord(int index);

  /**
   * Take the rows in {@code indexes}
   * 
   * @param indexes the indexes to take
   * @return a new data frame
   */
  DataFrame takeRecords(Iterable<Integer> indexes);

  /**
   * Drop rows in {@code indexes} and return a new DataFrame
   * 
   * @param indexes the indexes to drop
   * @return a new data frame
   */
  DataFrame dropRecords(Iterable<Integer> indexes);

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
  DoubleMatrix asMatrix();

  default Stream<Record> stream() {
    return StreamSupport.stream(spliterator(), false);
  }

  /**
   * Since DataFrames are immutable, this builder allows for the creation of new data frames
   */
  interface Builder extends Swappable {

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
     * Returns the column names collection.
     * 
     * @return the name attribute
     */
    NameAttribute getColumnNames();

    /**
     * Returns the row names collection.
     * 
     * @return the name attribute
     */
    NameAttribute getRecordNames();

    /**
     * Add a new vector builder as an additional column. If {@code builder.size() < rows()} the
     * added builder is padded with NA.
     *
     * @param builder builder to add
     * @return a modified builder
     */
    Builder addColumn(Vector.Builder builder);

    /**
     * Add a new vector. If the {@code vector.size() < rows()}, the resulting vector is padded with
     * NA.
     * 
     * @param vector the vector
     * @return a modified builder
     */
    Builder addColumn(Vector vector);

    Builder setColumn(int index, Vector.Builder builder);

    Builder setColumn(int index, Vector vector);

    Builder addRecord(Vector.Builder builder);

    Builder addRecord(Vector vector);

    Builder setRecord(int index, Vector.Builder builder);

    Builder setRecord(int index, Vector vector);

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
    Builder swapRecords(int a, int b);

    default void swap(int a, int b) {
      swapRecords(a, b);
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
