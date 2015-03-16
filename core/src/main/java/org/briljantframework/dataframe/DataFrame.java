package org.briljantframework.dataframe;

import org.briljantframework.Swappable;
import org.briljantframework.complex.Complex;
import org.briljantframework.io.DataInputStream;
import org.briljantframework.matrix.DoubleMatrix;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <p> A DataFrame is a heterogeneous or homogeneous storage of data. </p>
 *
 * <p> While {@code DataFrame} is immutable, {@link #setColumnName(int, String)}, {@link
 * #setRecordName(int, String)} are allowed to mutate the receiver. The rationale is simple, chang
 * the names won't affect the validity of {@code DataFrame} usages. </p>
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
  default DataFrame setColumnNames(String... names) {
    return setColumnNames(Arrays.asList(names));
  }

  /**
   * Sets the name of column c<sub>0</sub>...c<sub>names.length</sub>
   *
   * @param names the names
   * @return receiver modified
   */
  DataFrame setColumnNames(List<String> names);

  /**
   * Get value at {@code row} and {@code column} as a value
   *
   * @param row    the row
   * @param column the column
   * @return the value
   */
  Value get(int row, int column);

  /**
   * Get value at {@code row} and {@code column} as an instance of {@code T}. If conversion fails,
   * return {@code NA} as defined by {@link org.briljantframework.vector.Vectors#naValue(Class)}.
   *
   * @param cls    the class
   * @param row    the row
   * @param column the column
   * @param <T>    the type of the returned value
   * @return an instance of {@code T}
   */
  <T> T get(Class<T> cls, int row, int column);

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
  Bit getAsBit(int row, int column);

  /**
   * Get value at {@code row} and {@code column} as complex.
   *
   * @param row    the row
   * @param column the column
   * @return the value
   */
  Complex getAsComplex(int row, int column);

  /**
   * Returns string representation of value at {@code row, column}
   *
   * @param row    the row
   * @param column the column
   * @return the representation
   */
  String toString(int row, int column);

  /**
   * Returns true if value at {@code row, column} is {@code NA}.
   *
   * @param row    the row
   * @param column the column
   * @return true or false
   */
  boolean isNA(int row, int column);

  DataFrame addColumn(Vector column);

  DataFrame addColumn(int index, Vector column);

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
   * @throws java.lang.IndexOutOfBoundsException if {@code index < 0 || index > columns}
   */
  Vector getColumn(int index);

  /**
   * Uses the column name to lookup a specified column.
   *
   * @param name the column name
   * @return the column
   * @throws java.lang.IllegalArgumentException if key is not found
   */
  Vector getColumn(String name);

  /**
   * Remove column with {@code index}
   *
   * @param index the index
   * @return a new dataframe
   */
  DataFrame removeColumn(int index);

  /**
   * Remove columns with {@code indexes}
   *
   * @param indexes collection of indexes
   * @return a new dataframe
   */
  DataFrame removeColumns(Iterable<Integer> indexes);

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
   * @param index      the index
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
   * @param index   the index
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
   * Sets the name of column c<sub>0</sub>...c<sub>names.size()</sub>
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
   * Returns a collection of records.
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
  DataFrame getRecords(Iterable<Integer> indexes);

  /**
   * Drop rows in {@code indexes} and return a new DataFrame
   *
   * @param indexes the indexes to drop
   * @return a new data frame
   */
  DataFrame removeRecords(Iterable<Integer> indexes);

  DataFrame addRecord(int index, Vector record);

  DataFrame addRecord(Vector record);

  /**
   * {@code stack} {@code DataFrames} on top of each other.  All DataFrames in {@code dataFrames}
   * must have the same number of columns.
   *
   * @param dataFrames the data frames to stack.
   * @return a new data data frame
   */
  DataFrame stack(Iterable<DataFrame> dataFrames);

  /**
   * {@code concat}enate {@code DataFrames} side-by-side.
   *
   * @param dataFrames the data frames to stack.
   * @return a new data frame
   */
  DataFrame concat(Iterable<DataFrame> dataFrames);

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
   * Creates a new builder for creating new data frames which produces the concrete implementation
   * of {@code this}
   *
   * @return a new builder
   */
  Builder newBuilder();

  /**
   * Creates a new builder, initialized with a copy of this data frame, i.e. {@code
   * c.newCopyBuilder().create()} creates a new copy.
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

  default Stream<Record> parallelStream() {
    return StreamSupport.stream(spliterator(), true);
  }

  Collection<VectorType> getColumnTypes();

  /**
   * Since DataFrames are immutable, this builder allows for the creation of new data frames
   */
  interface Builder extends Swappable {

    /**
     * Set value at {@code row} in {@code column} to NA. If {@code column >= columns()} adds empty
     * {@link org.briljantframework.vector.VariableVector} of all {@code NA} from {@code columns()
     * ... column}.
     *
     * @param row    the row
     * @param column the column
     * @return a modified builder
     * @see org.briljantframework.vector.Vector.Builder#setNA(int)
     */
    Builder setNA(int row, int column);

    /**
     * Set value at {@code row, toCol} using the value at {@code fromRow, fromCol} in {@code from}.
     * If {@code toCol >= columns()}, adds empty {@link org.briljantframework.vector.VariableVector}
     * columns from {@code columns() ... column - 1}, inferring the type at {@code toCol} using
     * {@code from.getColumnType(fromCol)}
     *
     * @param toRow   the row
     * @param toCol   the column
     * @param from    the vector
     * @param fromRow the row
     * @param fromCol the column
     * @return a modified builder
     * @see org.briljantframework.vector.Vector.Builder#set(int, org.briljantframework.vector.Vector,
     * int)
     */
    Builder set(int toRow, int toCol, DataFrame from, int fromRow, int fromCol);

    /**
     * Add the value {@code fromRow} from {@code from} to {@code toCol} and {@code toRow}. If {@code
     * toCol >= columns()}, adds empty {@link org.briljantframework.vector.VariableVector} columns
     * from {@code columns() ... column - 1}, inferring the type at {@code toCol} using {@code
     * from.getType(index)}
     *
     * @param row    the row in this
     * @param column the column in this
     * @param from   the vector
     * @param index  the row in {@code from}
     * @return a modified builder
     */
    Builder set(int row, int column, Vector from, int index);

    /**
     * Set value at {@code row, column} to {@code value}. If {@code toCol >= columns()}, adds empty
     * {@link org.briljantframework.vector.VariableVector} columns from {@code columns() ... column
     * - 1}, inferring the type at {@code toCol} using {@code Vectors.getInstance(object)}
     *
     * @param row    the row
     * @param column the column
     * @param value  the value
     * @return a modified builder
     * @see org.briljantframework.vector.Vector.Builder#set(int, Object)
     */
    Builder set(int row, int column, Object value);

    default Builder set(int row, int column, Value value) {
      if (value == null) {
        return setNA(row, column);
      }
      return set(row, column, value, 0);
    }

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
    Builder addColumnBuilder(Vector.Builder builder);

    /**
     * Adds a new vector builder as an additional column using {@link org.briljantframework.vector.VectorType#newBuilder()}
     *
     * @param type the type
     * @return receiver modified
     */
    Builder addColumnBuilder(VectorType type);

    /**
     * Add a new vector. If the {@code vector.size() < rows()}, the resulting vector is padded with
     * NA.
     *
     * @param vector the vector
     * @return a modified builder
     */
    Builder addColumn(Vector vector);

    /**
     * Sets the column at {@code index} to {@code builder}. If {@code index >= columns()} adds empty
     * {@link org.briljantframework.vector.VariableVector} columns from {@code columns() ... column
     * - 1}. If {@code index < columns()} each column is shifted to the right.
     *
     * @param index   the index {@code index < columns()}
     * @param builder the builder
     * @return receiver modified
     */
    Builder insertColumn(int index, Vector.Builder builder);

    /**
     * Sets the column at {@code index} to {@code vector}. If {@code index >= columns()} adds empty
     * {@link org.briljantframework.vector.VariableVector} columns from {@code columns() ... column
     * - 1}. If {@code index < columns()} each column is shifted to the right.
     *
     * @param index  the index {@code index < columns()}
     * @param vector the vector
     * @return receiver modified
     */
    Builder insertColumn(int index, Vector vector);

    /**
     * Adds a new record. If {@code builder.size() < columns()}, left-over columns are padded with
     * NA.
     *
     * @param builder the builder
     * @return receiver modified
     */
    Builder addRecord(Vector.Builder builder);

    /**
     * Adds a new record. If {@code vector.size() < columns()}, left-over columns are padded with
     * NA.
     *
     * @param vector the vector
     * @return receiver modified
     */
    Builder addRecord(Vector vector);

    /**
     * Sets the {@code builder} at {@code index}.
     *
     * @param index   the index
     * @param builder the builder
     * @return receiver modified
     */
    Builder insertRecord(int index, Vector.Builder builder);

    /**
     * Sets the {@code vector} at {@code index}.
     *
     * @param index  the index
     * @param vector the vector
     * @return receiver modified
     */
    Builder insertRecord(int index, Vector vector);

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
     * Swap value at index {@code a} with value at index {@code b} in column with index {@code
     * column}
     *
     * @param column the column
     * @param a      the first index
     * @param b      the second index
     * @return a modified builder
     */
    Builder swapInColumn(int column, int a, int b);

    /**
     * Swap row at index {@code a} with {@code b}. <p> Generally, this is the same as <p> <p>
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
     * Concatenates the row at {@code toRow} with {@code vector} starting at {@code startCol}. If
     * {@code startCol < columns()}, values will be overwritten.
     *
     * @param toRow    the row to concat {@code vector} with
     * @param startCol the starting index in {@code toRow}
     * @param vector   the vector to concat
     * @return receiver modified
     */
    default Builder concat(int toRow, int startCol, Vector vector) {
      if (startCol > columns() || startCol < 0 || toRow > rows() || toRow < 0) {
        throw new IndexOutOfBoundsException();
      }

      for (int i = 0; i < vector.size(); i++) {
        if (startCol == columns()) {
          addColumnBuilder(vector.getType(i).newBuilder());
        }

        set(toRow, startCol++, vector, i);
      }
      return this;
    }

    /**
     * Same as {@code concat(toRow, columns(), vector)}
     *
     * @param toRow  the row concat {@code vector} with
     * @param vector the vector to concat
     * @return receiver modified
     */
    default Builder concat(int toRow, Vector vector) {
      return concat(toRow, columns(), vector);
    }

    /**
     * Concatenates {@code this} builder with {@code frame}. If {@code startCol < columns()} values
     * will be overwritten. <p> For example, a builder representing:
     * <pre>
     *   a   b
     *   2   2
     *   3   5
     *   3   5
     *
     *   concatenated with
     *
     *   c   d
     *   a   b
     *   c   d
     *
     *   results in
     *
     *   a   b   c   d
     *   2   2   a   b
     *   3   5   c   d
     *   3   5   NA  NA
     *
     *   (assuming that startCol = columns())
     * </pre>
     *
     * @param startCol the starting column
     * @param frame    the data frame to concatenate
     * @return receiver modified
     */
    default Builder concat(int startCol, DataFrame frame) {
      for (int i = 0; i < frame.columns(); i++) {
        if (frame.getColumnNames().containsKey(i)) {
          getColumnNames().put(startCol + i, frame.getColumnName(i));
        }
      }
      for (int i = 0; i < rows(); i++) {
        concat(i, startCol, frame.getRecord(i));
      }
      return this;
    }

    /**
     * @param frame the data frame
     * @return receiver modified
     */
    default Builder concat(DataFrame frame) {
      return concat(columns(), frame);
    }

    default Builder stack(int toCol, Vector vector) {
      return stack(rows(), toCol, vector);
    }

    /**
     * Add all values in {@code vector} to column {@code toCol}, starting at {@code startRow}. If
     * {@code startRow < rows()}, values are overwritten.
     *
     * @param startRow the start row
     * @param toCol    the index
     * @param vector   the vector
     * @return a modified builder
     */
    default Builder stack(int startRow, int toCol, Vector vector) {
      for (int i = 0; i < vector.size(); i++) {
        set(startRow++, toCol, vector, i);
      }
      return this;
    }

    default Builder stack(DataFrame frame) {
      return stack(rows(), frame);
    }

    /**
     * Add all values from frame (from column 0 until column()) starting at {@code startRow}. If
     * {@code startRow < rows()}, values are overwritten.
     *
     * @param frame the frame
     * @return a modified builder
     */
    default Builder stack(int startRow, DataFrame frame) {
      for (int i = 0; i < frame.columns(); i++) {
        stack(startRow, i, frame.getColumn(i));
      }
      return this;
    }

    /**
     * Read values from the {@code inputStream} and add the values to the correct column.
     *
     * @param inputStream the input stream
     * @return a modified builder
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
