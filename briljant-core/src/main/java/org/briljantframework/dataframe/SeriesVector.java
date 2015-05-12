package org.briljantframework.dataframe;

import com.google.common.base.Strings;

import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.TypeConversionException;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

/**
 * @author Isak Karlsson
 */
public class SeriesVector implements Series {

  private final Object name;
  private final Index index;
  private final Vector vector;

  public SeriesVector(Object name, Index index, Vector vector) {
    this.name = name;
    this.index = index;
    this.vector = vector;
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
          .append(toString(i));
    }
    return builder
        .append("Name: ").append(name())
        .append(" type: ").append(getType())
        .toString();
  }

  @Override
  public <T> T get(Class<T> cls, int index) {
    return vector.get(cls, index);
  }

  @Override
  public String toString(int index) {
    return vector.toString(index);
  }

  @Override
  public boolean isTrue(int index) {
    return vector.isTrue(index);
  }

  @Override
  public boolean isNA(int index) {
    return vector.isNA(index);
  }

  @Override
  public boolean hasNA() {
    return vector.hasNA();
  }

  @Override
  public double getAsDouble(int index) {
    return vector.getAsDouble(index);
  }

  @Override
  public int getAsInt(int index) {
    return vector.getAsInt(index);
  }

  @Override
  public Bit getAsBit(int index) {
    return vector.getAsBit(index);
  }

  @Override
  public Complex getAsComplex(int index) {
    return vector.getAsComplex(index);
  }

  @Override
  public String getAsString(int index) {
    return vector.getAsString(index);
  }

  @Override
  public Vector slice(Iterable<Integer> indexes) {
    return vector.slice(indexes);
  }

  @Override
  public int size() {
    return vector.size();
  }

  @Override
  public VectorType getType() {
    return vector.getType();
  }

  @Override
  public VectorType getType(int index) {
    return vector.getType(index);
  }

  @Override
  public Builder newCopyBuilder() {
    return vector.newCopyBuilder();
  }

  @Override
  public Builder newBuilder() {
    return vector.newBuilder();
  }

  @Override
  public Builder newBuilder(int size) {
    return vector.newBuilder(size);
  }


  @Override
  public Matrix toMatrix() throws TypeConversionException {
    return vector.toMatrix();
  }

  @Override
  public int compare(int a, int b) {
    return vector.compare(a, b);
  }

  @Override
  public int compare(int a, Vector other, int b) {
    return vector.compare(a, other, b);
  }

}
