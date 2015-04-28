package org.briljantframework.vector;

import org.briljantframework.complex.Complex;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Isak Karlsson
 */
public final class Na {

  private static final Map<Class<?>, Object> CLASS_TO_NA;

  static {
    Map<Class<?>, Object> clsToNa = new HashMap<>();
    clsToNa.put(Integer.class, IntVector.NA);
    clsToNa.put(Integer.TYPE, IntVector.NA);
    clsToNa.put(Long.class, Long.MAX_VALUE);
    clsToNa.put(Long.TYPE, Long.MAX_VALUE);
    clsToNa.put(Double.class, DoubleVector.NA);
    clsToNa.put(Double.TYPE, DoubleVector.NA);
    clsToNa.put(String.class, StringVector.NA);
    clsToNa.put(Bit.class, BitVector.NA);
    clsToNa.put(Complex.class, ComplexVector.NA);
    clsToNa.put(Object.class, null);
    CLASS_TO_NA = Collections.unmodifiableMap(clsToNa);
  }


  private Na() {
  }

  /**
   * Returns the {@code NA} value for the class {@code T}. For reference types {@code NA} is
   * represented as {@code null}, but for primitive types a special convention is used.
   *
   * <ul>
   * <li>{@code double}: {@link org.briljantframework.vector.DoubleVector#NA}</li>
   * <li>{@code int}: {@link org.briljantframework.vector.IntVector#NA}</li>
   * <li>{@code long}: {@link Long#MAX_VALUE}</li>
   * </ul>
   *
   * @param cls the class
   * @param <T> the type of {@code cls}
   * @return a {@code NA} value of type {@code T}
   */
  public static <T> T valueOf(Class<T> cls) {
    @SuppressWarnings("unchecked")
    T t = (T) CLASS_TO_NA.get(checkNotNull(cls));
    return t;
  }
}
