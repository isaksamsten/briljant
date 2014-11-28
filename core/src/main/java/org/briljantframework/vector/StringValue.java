package org.briljantframework.vector;

import java.util.Iterator;

import com.google.common.collect.Iterators;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public class StringValue extends AbstractStringVector implements Value {

  private final String value;

  public StringValue(String value) {
    this.value = value;
  }

  @Override
  public Iterator<String> iterator() {
    return Iterators.singletonIterator(value);
  }

  @Override
  public int compareTo(Value o) {
    return isNA() || o.isNA() ? 0 : getAsString().compareTo(o.getAsString());
  }

  @Override
  public String getAsString(int index) {
    return value;
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public Builder newCopyBuilder() {
    return null;
  }

  @Override
  public Builder newBuilder() {
    return null;
  }

  @Override
  public Builder newBuilder(int size) {
    return null;
  }
}
