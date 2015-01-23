package org.briljantframework.vector;

/**
 * Created by isak on 1/21/15.
 */
public abstract class AbstractVector implements Vector {

  @Override
  public Builder newBuilder() {
    return getType().newBuilder();
  }

  @Override
  public Builder newBuilder(int size) {
    return getType().newBuilder(size);
  }

  @Override
  public Builder newCopyBuilder() {
    Builder builder = newBuilder(size());
    for (int i = 0; i < size(); i++) {
      builder.add(this, i);
    }
    return builder;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("[");
    builder.append(toString(0));
    for (int i = 1; i < size(); i++) {
      builder.append(", ").append(toString(i));
    }
    builder.append("]");
    return builder.toString();
  }
}
