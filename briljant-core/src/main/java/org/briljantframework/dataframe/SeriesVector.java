package org.briljantframework.dataframe;

import com.google.common.base.Strings;

import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public class SeriesVector extends VectorDelegate implements Series {

  private final Object name;
  private final Index index;

  public SeriesVector(Object name, Index index, Vector vector) {
    super(vector);
    this.name = name;
    this.index = index;
  }

  @Override
  public Object name() {
    return name;
  }

  @Override
  public Index index() {
    return index;
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    int longest = index().keySet().stream()
        .map(Object::toString)
        .mapToInt(String::length)
        .max()
        .orElse(0);

    Index index = index();
    for (int i = 0; i < size(); i++) {
      String key = index.get(i).toString();
      builder.append(key)
          .append(Strings.repeat(" ", longest - key.length() + 2))
          .append(toString(i)).append("\n");
    }
    return builder
        .append("Name: ").append(name())
        .append(" type: ").append(getType())
        .toString();
  }
}
