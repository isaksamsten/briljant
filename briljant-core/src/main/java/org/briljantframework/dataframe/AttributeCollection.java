package org.briljantframework.dataframe;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Created by Isak Karlsson on 07/01/15.
 */
public interface AttributeCollection<T> extends Collection<T> {

  T get(int index);

  T getOrDefault(int index, Supplier<T> dafault);

  Set<Map.Entry<Integer, T>> entrySet();

  boolean containsKey(int index);

  void put(int index, T value);

  void remove(int index);

  void swap(int a, int b);
}
