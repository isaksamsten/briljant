package org.briljantframework.vector;

import java.util.Iterator;

import com.google.common.collect.Iterators;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public class BitValue extends AbstractBinaryVector implements Value {
  private final int binary;

  public BitValue(int binary) {
    this.binary = binary;
  }

  public BitValue(Bit bit) {
    this(bit.asInt());
  }

  @Override
  public Iterator<Bit> iterator() {
    return Iterators.singletonIterator(Bit.valueOf(binary));
  }

  @Override
  public int compareTo(Value o) {
    return getAsInt() - o.getAsInt();
  }

  @Override
  public int getAsInt(int index) {
    return binary;
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

  @Override
  public int hashCode() {
    return binary;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;

    BitValue binaries = (BitValue) o;

    if (binary != binaries.binary)
      return false;

    return true;
  }
}
