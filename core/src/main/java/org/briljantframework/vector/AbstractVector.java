package org.briljantframework.vector;

import java.util.Iterator;

import com.google.common.collect.UnmodifiableIterator;

/**
 * @author Isak Karlsson
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
  public Iterator<Value> iterator() {
    return new UnmodifiableIterator<Value>() {
      private int current = 0;

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
