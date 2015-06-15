package org.briljantframework.dataframe;

/**
 * @author Isak Karlsson
 */
public interface Ix {

  <T> T get(Class<T> cls, Object r, int c);

  <T> T get(Class<T> cls, int r, Object c);

  <T> T get(Class<T> cls, Object r, Object c);

  <T> T get(Class<T> cls, int r, int c);

  int getAsInt(Object r, int c);

  int getAsInt(int r, Object c);

  int getAsInt(Object r, Object c);

  int getAsInt(int r, int c);

  double getAsDouble(Object r, int c);

  double getAsDouble(int r, Object c);

  double getAsDouble(Object r, Object c);

  double getAsDouble(int r, int c);
}
