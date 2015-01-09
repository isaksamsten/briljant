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
 * fine in many cases, the {@link ArrayDoubleMatrix} is several order of magnitudes faster,
 * especially for complex operations such as matrix-matrix multiplication (
 * {@link #mmul(DoubleMatrix)}) and should hence be preferred for all cases except when the number
 * of non-zero elements is <b>very</b> small and the size of the matrix is <b>very</b> large.
 * </p>
 * 
 * <p>
 * For this reason, most (all) operations defined in {@link Doubles} return an
 * {@link ArrayDoubleMatrix} if not the type of the receiver.
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
public class HashDoubleMatrix extends AbstractDoubleMatrix {
  private final IntObjectMap<IntDoubleMap> values;
  private final double defaultValue;

  public HashDoubleMatrix(int rows, int columns) {
    this(rows, columns, new IntObjectOpenHashMap<>());
  }

  protected HashDoubleMatrix(int rows, int columns, IntObjectMap<IntDoubleMap> values) {
    super(rows, columns);
    this.values = values;
    this.defaultValue = 0;
  }

  @Override
  public DoubleMatrix reshape(int rows, int columns) {
    Preconditions.checkArgument(rows * columns == size(),
        "Total size of new matrix must be unchanged.");
    return new HashDoubleMatrix(rows, columns, values);
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
  public double get(int index) {
    int col = index / rows();
    int row = index % rows();
    return get(row, col);
  }

  @Override
  public boolean isArrayBased() {
    return false;
  }

  @Override
  public DoubleMatrix newEmptyMatrix(int rows, int columns) {
    return new HashDoubleMatrix(rows, columns);
  }

  @Override
  public DoubleMatrix copy() {
    IntObjectMap<IntDoubleMap> copy = new IntObjectOpenHashMap<>();
    for (IntObjectCursor<IntDoubleMap> value : values) {
      copy.put(value.key, new IntDoubleOpenHashMap(value.value));
    }
    return new HashDoubleMatrix(rows(), columns(), copy);
  }

  @Override
  public void set(int i, int j, double value) {
    Preconditions.checkArgument(i < rows() && i >= 0 && j < columns() && j >= 0);
    IntDoubleMap col = values.get(j);
    if (col == null) {
      col = new IntDoubleOpenHashMap();
      values.put(j, col);
    }
    col.put(i, value);
  }

  @Override
  public void set(int index, double value) {
    int col = index / rows();
    int row = index % rows();
    set(row, col, value);
  }

  @Override
  public int size() {
    return rows() * columns();
  }
}
