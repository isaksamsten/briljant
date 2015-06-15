package org.briljantframework.vector;

/**
 * @author Isak Karlsson
 */
public final class Na {

  public static final Integer BOXED_INT_NA = IntVector.NA;
  public static final Double BOXED_DOUBLE_NA = DoubleVector.NA;
  public static final Long BOXED_LONG_NA = Long.MAX_VALUE;

  private Na() {
  }

  /**
   * Returns the {@code NA} value for the class {@code T}. For reference types {@code NA} is
   * represented as {@code null}, but for primitive types a special convention is used.
   *
   * <ul>
   * <li>{@code double}: {@link org.briljantframework.vector.DoubleVector#NA}</li>
   * <li>{@code int}: {@link IntVector#NA}</li>
   * <li>{@code long}: {@link Long#MAX_VALUE}</li> // TODO: the rest
   * </ul>
   *
   * @param cls the class
   * @param <T> the type of {@code cls}
   * @return a {@code NA} value of type {@code T}
   */
  @SuppressWarnings("unchecked")
  public static <T> T of(Class<T> cls) {
    if (cls == null) {
      return null;
    } else if (Integer.class.equals(cls) || Integer.TYPE.equals(cls)) {
      return (T) BOXED_INT_NA;
    } else if (Double.class.equals(cls) || Double.TYPE.equals(cls)) {
      return (T) BOXED_DOUBLE_NA;
    } else if (Long.class.equals(cls) || Long.TYPE.equals(cls)) {
      return (T) BOXED_LONG_NA;
    } else if (Bit.class.equals(cls)) {
      return (T) Bit.NA;
    } else {
      return null;
    }
  }
}
