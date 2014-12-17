package org.briljantframework.matrix;

import com.carrotsearch.hppc.IntDoubleMap;
import com.carrotsearch.hppc.IntDoubleOpenHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.carrotsearch.hppc.IntObjectOpenHashMap;
import com.carrotsearch.hppc.cursors.IntObjectCursor;
import com.google.common.base.Preconditions;

/**
 * <p>
 * A sparse matrix implemented using rather efficient hash maps. Although the performance is rather
 * fine in many cases, the {@link org.briljantframework.matrix.ArrayMatrix} is several order of
 * magnitudes faster, especially for complex operations such as matrix-matrix multiplication (
 * {@link #mmul(Matrix)}) and should hence be preferred for all cases except when the number of
 * non-zero elements is <b>very</b> small and the size of the matrix is <b>very</b> large.
 * </p>
 * 
 * <p>
 * For this reason, most (all) operations defined in {@link org.briljantframework.matrix.Matrices}
 * return an {@link org.briljantframework.matrix.ArrayMatrix} if not the type of the receiver.
 * </p>
 *
 * <p>
 * To put the performance differences into perspective, multiplying a {@code 100 x 5000} matrix with
 * a {@code 5000 x 100} matrix takes {@code 5} ms using {@code ArrayMatrix} and {@code 10202} ms,
 * i.e. the {@code HashMatrix} is almost 2000 times slower.
 * </p>
 * 
 * @author Isak Karlsson
 */
public class HashMatrix extends AbstractMatrix {
  private final IntObjectMap<IntDoubleMap> values;
  private final double defaultValue;

  public HashMatrix(int rows, int columns) {
    this(rows, columns, new IntObjectOpenHashMap<>());
  }

  protected HashMatrix(int rows, int columns, IntObjectMap<IntDoubleMap> values) {
    super(rows, columns);
    this.values = values;
    this.defaultValue = 0;
  }

  @Override
  public void put(int i, int j, double value) {
    Preconditions.checkArgument(i < rows() && i >= 0 && j < columns() && j >= 0);
    IntDoubleMap col = values.get(j);
    if (col == null) {
      col = new IntDoubleOpenHashMap();
      values.put(j, col);
    }
    col.put(i, value);
  }

  @Override
  public double get(int i, int j) {
    IntDoubleMap col = values.get(j);
    if (col == null) {
      return defaultValue;
    }

    return col.getOrDefault(i, defaultValue);
  }

  @Override
  public void put(int index, double value) {
    int col = index / rows();
    int row = index % rows();
    put(row, col, value);
  }

  @Override
  public double getAsDouble(int index) {
    int col = index / rows();
    int row = index % rows();
    return get(row, col);
  }

  @Override
  public int size() {
    return rows() * columns();
  }

  @Override
  public boolean isArrayBased() {
    return false;
  }

  @Override
  public Matrix newEmptyMatrix(int rows, int columns) {
    return new HashMatrix(rows, columns);
  }

  @Override
  public Matrix copy() {
    IntObjectMap<IntDoubleMap> copy = new IntObjectOpenHashMap<>();
    for (IntObjectCursor<IntDoubleMap> value : values) {
      copy.put(value.key, new IntDoubleOpenHashMap(value.value));
    }
    return new HashMatrix(rows(), columns(), copy);
  }
}
