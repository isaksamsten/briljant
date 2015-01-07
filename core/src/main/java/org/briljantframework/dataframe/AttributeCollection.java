package org.briljantframework.dataframe;

/**
 * Created by Isak Karlsson on 07/01/15.
 */
public interface AttributeCollection<T> {

  T get(int index);

  void put(int index, T value);

  void remove(int index);

  void swap(int a, int b);
}
