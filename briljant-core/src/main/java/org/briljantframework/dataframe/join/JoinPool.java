package org.briljantframework.dataframe.join;

/**
 * Created by Isak Karlsson on 09/01/15.
 */
public class JoinPool {
  int size;
  int[] references;

  JoinPool(int size, int[] references) {
    this.size = size;
    this.references = references;
  }

  public int getReference(int i) {
    return references[i];
  }

  public int length() {
    return references.length;
  }

  public int getMaxGroups() {
    return size;
  }
}
