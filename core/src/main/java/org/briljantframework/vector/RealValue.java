package org.briljantframework.vector;

import java.util.Iterator;

import com.google.common.collect.Iterators;

/**
 * Created by Isak Karlsson on 27/11/14.
 */
public class RealValue extends AbstractRealVector implements Value {

  private final double value;

  public RealValue(double value) {
    this.value = value;
  }

  @Override
  public int compareTo(Value o) {
    return isNA() && o.isNA() ? 0 : Double.compare(getAsDouble(), o.getAsDouble());
  }

  @Override
  public double getAsReal(int index) {
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

  @Override
  public Iterator<Double> iterator() {
    return Iterators.singletonIterator(getAsDouble());
  }

}
