package org.briljantframework.classification.tune;

import java.util.function.BiConsumer;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
class EnumerationUpdater<T, V> implements ParameterUpdater<T> {

  private final String name;
  private final V[] enumeration;
  private final BiConsumer<T, V> updater;
  private int current;

  public EnumerationUpdater(String name, BiConsumer<T, V> updater, V[] enumeration) {
    this.name = name;
    this.enumeration = enumeration;
    this.updater = updater;
    current = 0;
  }

  @Override
  public String getParameter() {
    return name;
  }

  @Override
  public void restore() {
    current = 0;
  }

  @Override
  public boolean hasUpdate() {
    return current < enumeration.length;
  }

  @Override
  public Object update(T toUpdate) {
    V value = enumeration[current++];
    updater.accept(toUpdate, value);
    return value;
  }
}
