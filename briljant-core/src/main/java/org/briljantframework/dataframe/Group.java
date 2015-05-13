package org.briljantframework.dataframe;

/**
 * @author Isak Karlsson
 */
public class Group {

  private final Object group;
  private final DataFrame data;

  public Group(Object group, DataFrame data) {
    this.group = group;
    this.data = data;
  }

  public Object group() {
    return group;
  }

  public DataFrame data() {
    return data;
  }
}
