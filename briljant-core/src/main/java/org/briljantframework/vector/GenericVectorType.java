package org.briljantframework.vector;

/**
 * Created by isak on 13/03/15.
 */
public class GenericVectorType implements VectorType {

  private Class<?> cls;

  public GenericVectorType(Class<?> cls) {
    this.cls = cls;
  }

  @Override
  public Vector.Builder newBuilder() {
    return new GenericVector.Builder(cls);
  }

  @Override
  public Vector.Builder newBuilder(int size) {
    return new GenericVector.Builder(cls, size);
  }

  @Override
  public Class<?> getDataClass() {
    return cls;
  }

  @Override
  public boolean isNA(Object value) {
    return value == null;
  }

  @Override
  public int compare(int a, Vector va, int b, Vector ba) {
    if (Comparable.class.isAssignableFrom(cls)) {
      Comparable oa = va.get(Comparable.class, a);
      Comparable ob = va.get(Comparable.class, b);

      @SuppressWarnings("unchecked")
      int cmp = oa.compareTo(ob);
      return cmp;
    }
    throw new UnsupportedOperationException("Can't compare");
  }

  @Override
  public Scale getScale() {
    return Number.class.isAssignableFrom(cls) ? Scale.NUMERICAL : Scale.NOMINAL;
  }

  @Override
  public int hashCode() {
    return cls.hashCode();
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof GenericVectorType) {
      return ((GenericVectorType) obj).cls.equals(cls);
    }
    return false;
  }

  @Override
  public String toString() {
    return String.format("generic(%s)", cls.getSimpleName());
  }
}
