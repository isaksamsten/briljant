package org.briljantframework.vector;

import com.google.common.base.Preconditions;

import org.briljantframework.exceptions.TypeConversionException;
import org.briljantframework.matrix.Matrix;

/**
 * Created by isak on 13/03/15.
 */
public class GenericValue extends AbstractVector implements Value {

  private final Object obj;

  public GenericValue(Object obj) {
    this.obj = Preconditions.checkNotNull(obj);
  }

  @Override
  public int compareTo(Value o) {
    if (obj instanceof Comparable) {
      Comparable a = (Comparable) obj;
      Comparable b = o.get(Comparable.class);
      @SuppressWarnings("unchecked")
      int cmp = a.compareTo(b);
      return cmp;
    }
    throw new UnsupportedOperationException();
  }

  @Override
  public Value get(int index) {
    return this;
  }

  @Override
  public <T> T get(Class<T> cls, int index) {
    if (index != 0) {
      throw new IndexOutOfBoundsException();
    }
    return cls.cast(obj);
  }

  @Override
  public String toString(int index) {
    return getAsString(index);
  }

  @Override
  public boolean isNA(int index) {
    if (index != 0) {
      throw new IndexOutOfBoundsException();
    }
    return obj == null;
  }

  @Override
  public double getAsDouble(int index) {
    if (index != 0) {
      throw new IndexOutOfBoundsException();
    }

    if (obj instanceof Number) {
      return ((Number) obj).doubleValue();
    } else {
      return IntVector.NA;
    }
  }

  @Override
  public int getAsInt(int index) {
    if (index != 0) {
      throw new IndexOutOfBoundsException();
    }

    if (obj instanceof Number) {
      return ((Number) obj).intValue();
    } else {
      return IntVector.NA;
    }
  }

  @Override
  public Bit getAsBit(int index) {
    if (index != 0) {
      throw new IndexOutOfBoundsException();
    }
    if (obj instanceof Integer) {
      return Bit.valueOf((Integer) obj);
    } else if (obj instanceof Boolean) {
      return Bit.valueOf((Boolean) obj);
    } else if (obj instanceof Bit) {
      return (Bit) obj;
    } else {
      return Bit.NA;
    }
  }

  @Override
  public String getAsString(int index) {
    if (index != 0) {
      throw new IndexOutOfBoundsException();
    }
    return obj == null ? StringVector.NA : obj.toString();
  }

  @Override
  public int size() {
    return 1;
  }

  @Override
  public VectorType getType() {
    return VectorType.getInstance(obj.getClass());
  }

  @Override
  public Matrix asMatrix() throws TypeConversionException {
    throw new TypeConversionException(
        String.format("Can't convert value(%s) to matrix", obj.getClass()));
  }

  @Override
  public int compare(int a, int b) {
    if (a != 0 && b != 0) {
      throw new IndexOutOfBoundsException();
    }
    return 0;
  }

  @Override
  public int compare(int a, Vector other, int b) {
    if (a != 0) {
      throw new IndexOutOfBoundsException();
    }
    if (obj instanceof Comparable) {
      Comparable bv = other.get(Comparable.class, b);
      @SuppressWarnings("unchecked")
      int cmp = ((Comparable) obj).compareTo(b);
      return cmp;
    }
    throw new UnsupportedOperationException();
  }

  @Override
  public int hashCode() {
    return obj != null ? obj.hashCode() : -1;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof GenericValue) {
      if (this.obj == null && ((GenericValue) obj).obj == null) {
        return true;
      } else if (this.obj != null && ((GenericValue) obj).obj != null) {
        return this.obj.equals(((GenericValue) obj).obj);
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
}
