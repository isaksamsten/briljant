package org.briljantframework.vector;

import com.google.common.base.Preconditions;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public class StringValue extends AbstractStringVector implements Value {

  private final String value;

  public StringValue(String value) {
    this.value = Preconditions.checkNotNull(value);
  }

  public static Value valueOf(String value1) {
    if (value1 == null) {
      return Undefined.INSTANCE;
    }
    return new StringValue(value1);
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
  public Builder newBuilder() {
    return new StringVector.Builder();
  }

  @Override
  public Builder newBuilder(int size) {
    return new StringVector.Builder(size);
  }

  @Override
  public Builder newCopyBuilder() {
    return new StringVector.Builder().add(this);
  }

  @Override
  public String toString() {
    return toString(0);
  }
}
