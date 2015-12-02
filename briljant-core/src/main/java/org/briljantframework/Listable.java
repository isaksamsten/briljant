package org.briljantframework;

import java.util.List;

/**
 * An instance of Listable can be converted to a list.
 * 
 * @author Isak Karlsson
 */
public interface Listable<T> {

  /**
   * Return a list implementation.
   * 
   * @return a list
   */
  List<T> toList();
}
