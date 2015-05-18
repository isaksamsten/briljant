package org.briljantframework.dataframe;

import org.briljantframework.complex.Complex;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public interface Series extends Vector {

  Object name();

  Index index();

  default <T> T get(Class<T> cls, Object key) {
    return get(cls, index().index(key));
  }

  default int getAsInt(Object key) {
    return getAsInt(index().index(key));
  }

  default double getAsDouble(Object key) {
    return getAsDouble(index().index(key));
  }

  default Complex getAsComplex(Object key) {
    return getAsComplex(index().index(key));
  }

  default Bit getAsBit(Object key) {
    return getAsBit(index().index(key));
  }

  default String toString(Object key) {
    return toString(index().index(key));
  }
}
