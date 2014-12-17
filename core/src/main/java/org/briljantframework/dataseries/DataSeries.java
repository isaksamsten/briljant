package org.briljantframework.dataseries;

import org.briljantframework.dataframe.DataFrameRow;
import org.briljantframework.vector.Binary;
import org.briljantframework.vector.Type;
import org.briljantframework.vector.Value;
import org.briljantframework.vector.Vector;

import com.google.common.base.MoreObjects;

/**
 * A data series is a vector of ordered events.
 * 
 * @author Isak Karlsson
 */
public class DataSeries implements DataFrameRow {
  private final Vector vector;

  public DataSeries(Vector vector) {
    this.vector = vector;
  }

  @Override
  public String getColumnName(int index) {
    throw new UnsupportedOperationException();
  }

  @Override
  public Type getType(int index) {
    return vector.getType();
  }

  @Override
  public Value getAsValue(int index) {
    return vector.getAsValue(index);
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
  public Binary getAsBinary(int index) {
    return vector.getAsBinary(index);
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
  public int compare(int a, int b) {
    return vector.compare(a, b);
  }

  @Override
  public int compare(int a, int b, Vector other) {
    return vector.compare(a, b, other);
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("size", size()).toString();
  }
}
