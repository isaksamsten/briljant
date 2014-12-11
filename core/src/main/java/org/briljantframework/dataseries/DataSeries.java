package org.briljantframework.dataseries;

import org.briljantframework.dataframe.DataFrameRow;
import org.briljantframework.vector.AbstractDoubleVector;
import org.briljantframework.vector.Type;
import org.briljantframework.vector.Vector;

import com.google.common.base.MoreObjects;

/**
 * Created by Isak Karlsson on 11/12/14.
 */
public class DataSeries extends AbstractDoubleVector implements DataFrameRow {
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
  public double getAsDouble(int index) {
    return vector.getAsDouble(index);
  }

  @Override
  public int size() {
    return vector.size();
  }

  @Override
  public Builder newCopyBuilder() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Builder newBuilder() {
    throw new UnsupportedOperationException();
  }

  @Override
  public Builder newBuilder(int size) {
    throw new UnsupportedOperationException();
  }

  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this).add("size", size()).toString();
  }
}
