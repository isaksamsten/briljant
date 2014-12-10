package org.briljantframework.vector;

import com.google.common.base.Preconditions;

/**
 * 
 * 
 * Created by Isak Karlsson on 10/12/14.
 */
public abstract class AbstractVectorView implements Vector {

  private final Vector vector;
  private final Type type;

  public AbstractVectorView(Vector vector, Type type) {
    this.vector = Preconditions.checkNotNull(vector, "Vector can't be null");
    this.type = Preconditions.checkNotNull(type, "Type can't be null");
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
  public Type getType() {
    return type;
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
  public int compare(int a, int b) {
    return vector.compare(a, b);
  }

  @Override
  public int compare(int a, int b, Vector other) {
    return vector.compare(a, b, other);
  }
}
