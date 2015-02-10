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
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    StringValue strings = (StringValue) o;

    if (!value.equals(strings.value))
      return false;

    return true;
  }

  @Override
  public int hashCode() {
    return value.hashCode();
  }

  @Override
  public String toString() {
    return value;
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
