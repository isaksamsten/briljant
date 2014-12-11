package org.briljantframework.dataframe;

import java.util.Iterator;

import org.briljantframework.Utils;
import org.briljantframework.vector.*;

import com.google.common.collect.ImmutableTable;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Created by Isak Karlsson on 26/11/14.
 */
class DataFrameRowView implements DataFrameRow {

  private final DataFrame parent;
  private final int row;

  DataFrameRowView(DataFrame parent, int row) {
    this.parent = parent;
    this.row = row;
  }

  @Override
  public String getColumnName(int index) {
    return parent.getColumnName(index);
  }

  @Override
  public Type getType() {
    return VariableVector.TYPE;
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
    return new ValueVector.Builder(size()).addAll((Vector) this);
  }

  @Override
  public Builder newBuilder() {
    return new ValueVector.Builder();
  }

  @Override
  public Builder newBuilder(int size) {
    return new ValueVector.Builder(size);
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
    ImmutableTable.Builder<Integer, Integer, String> b = new ImmutableTable.Builder<>();
    b.put(0, 0, "");
    b.put(1, 0, "[" + row + ",]");
    for (int i = 0; i < size(); i++) {
      b.put(0, i + 1, getColumnName(i));
      b.put(1, i + 1, getAsString(i));
    }


    return Utils.prettyPrintTable(b.build(), 1, 2, false, false);
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
