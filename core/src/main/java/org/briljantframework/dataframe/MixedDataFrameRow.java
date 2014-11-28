package org.briljantframework.dataframe;

import java.util.Iterator;
import java.util.stream.Collectors;

import org.briljantframework.vector.*;

import com.google.common.collect.UnmodifiableIterator;

/**
 * Created by Isak Karlsson on 26/11/14.
 */
class MixedDataFrameRow implements CompoundVector {

  private final DataFrame parent;
  private final int row;

  MixedDataFrameRow(DataFrame parent, int row) {
    this.parent = parent;
    this.row = row;
  }

  @Override
  public Type getType() {
    return CompoundVector.TYPE;
  }

  @Override
  public Type getType(int index) {
    return parent.getColumnType(index);
  }

  @Override
  public double getAsDouble(int index) {
    return parent.getAsDouble(row, index);
  }

  @Override
  public int getAsInt(int index) {
    return parent.getAsInt(row, index);
  }

  @Override
  public Binary getAsBinary(int index) {
    return parent.getAsBinary(row, index);
  }

  @Override
  public String getAsString(int index) {
    return parent.getAsString(row, index);
  }

  @Override
  public Value getAsValue(int index) {
    return parent.getColumn(index).getAsValue(row);
  }

  @Override
  public Complex getAsComplex(int index) {
    return parent.getAsComplex(row, index);
  }

  @Override
  public String toString(int index) {
    return parent.getColumn(index).toString(row);
  }

  @Override
  public boolean isNA(int index) {
    return parent.isNA(row, index);
  }

  @Override
  public int size() {
    return parent.columns();
  }

  @Override
  public Builder newCopyBuilder() {
    return new VariableVector.Builder(size()).addAll((Vector) this);
  }

  @Override
  public Builder newBuilder() {
    return new VariableVector.Builder();
  }

  @Override
  public Builder newBuilder(int size) {
    return new VariableVector.Builder(size);
  }

  @Override
  public int compare(int a, int b) {
    return getAsValue(a).compareTo(getAsValue(b));
  }

  @Override
  public int compare(int a, int b, Vector other) {
    return getAsValue(a).compareTo(other.getAsValue(b));
  }

  @Override
  public String toString() {
    return stream().map(Value::getAsString).collect(Collectors.joining(","));
  }

  @Override
  public Iterator<Value> iterator() {
    return new UnmodifiableIterator<Value>() {
      public int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public Value next() {
        return getAsValue(current++);
      }
    };
  }
}
