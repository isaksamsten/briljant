package org.briljantframework;

import java.util.List;

/**
 * @author Isak Karlsson
 */
public interface Listable<T> {

  /**
   * Return a list implementation
   * 
   * @return a list
   */
  List<T> toList();
}
