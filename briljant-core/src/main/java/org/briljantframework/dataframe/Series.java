package org.briljantframework.dataframe;

import org.briljantframework.complex.Complex;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.Vector;

/**
 * @author Isak Karlsson
 */
public interface Series extends Vector {

  Object name();

  Index getIndex();

  default <T> T get(Class<T> cls, Object key) {
    return get(cls, getIndex().index(key));
  }

  default int getAsInt(Object key) {
    return getAsInt(getIndex().index(key));
  }

  default double getAsDouble(Object key) {
    return getAsDouble(getIndex().index(key));
  }

  default Complex getAsComplex(Object key) {
    return getAsComplex(getIndex().index(key));
  }

  default Bit getAsBit(Object key) {
    return getAsBit(getIndex().index(key));
  }

  default String toString(Object key) {
    return toString(getIndex().index(key));
  }
}
