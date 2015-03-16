package org.briljantframework.dataseries;

import org.briljantframework.dataframe.Record;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

/**
 * A data series is a vector of ordered events.
 *
 * @author Isak Karlsson
 */
public class DataSeries implements Record {

  private final Vector vector;

  public DataSeries(Vector vector) {
    this.vector = vector;
  }

  @Override
  public String getColumnName(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public VectorType getType() {
    return vector.getType();
  }

  @Override
  public Value get(int index) {
    return vector.get(index);
  }

  @Override
  public <T> T get(Class<T> cls, int index) {
    return vector.get(cls, index);
  }

  @Override
  public String toString(int index) {
    return vector.toString(index);
  }

  @Override
  public boolean isNA(int index) {
    return vector.isNA(index);
  }

  @Override
  public double getAsDouble(int index) {
    return vector.getAsDouble(index);
  }

  @Override
  public int getAsInt(int index) {
    return vector.getAsInt(index);
  }

  @Override
  public Bit getAsBit(int index) {
    return vector.getAsBit(index);
  }

  @Override
  public String getAsString(int index) {
    return vector.getAsString(index);
  }

  @Override
  public int size() {
    return vector.size();
  }

  @Override
  public VectorType getType(int index) {
    return vector.getType();
  }

  @Override
  public Builder newCopyBuilder() {
    return vector.newCopyBuilder();
  }

  @Override
  public Builder newBuilder() {
    return vector.newBuilder();
  }

  @Override
  public Builder newBuilder(int size) {
    return vector.newBuilder(size);
  }

  @Override
  public Matrix asMatrix() {
    return vector.asMatrix();
  }

  @Override
  public int compare(int a, int b) {
    return vector.compare(a, b);
  }

  @Override
  public int compare(int a, Vector other, int b) {
    return vector.compare(a, other, b);
  }

  @Override
  public String toString() {
    return vector.toString();
  }
}
