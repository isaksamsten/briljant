package org.briljantframework.dataframe;

import java.util.function.Supplier;

/**
 * Created by Isak Karlsson on 07/01/15.
 */
public interface AttributeCollection<T> {

  T get(int index);

  T getOrDefault(int index, Supplier<T> dafault);

  void put(int index, T value);

  void remove(int index);

  void swap(int a, int b);
}
