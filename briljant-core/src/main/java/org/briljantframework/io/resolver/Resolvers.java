package org.briljantframework.io.resolver;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

import org.briljantframework.complex.Complex;
import org.briljantframework.vector.Bit;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Isak Karlsson
 */
public final class Resolvers {

  private static final Map<Class<?>, Resolver<?>> RESOLVERS = Collections.synchronizedMap(
      new HashMap<>()
  );

  static {
    Resolver<LocalDate> localDateResolver = new Resolver<>(LocalDate.class);
    localDateResolver.put(String.class, new StringDateConverter());
    Converter<Long, LocalDate> converter = (l) ->
        Instant.ofEpochMilli(l).atZone(ZoneId.systemDefault()).toLocalDate();
    localDateResolver.put(Long.class, converter);
    localDateResolver.put(Long.TYPE, converter);

    Resolver<Integer> integerResolver = new Resolver<>(Integer.class);
    integerResolver.put(String.class, Ints::tryParse);
    integerResolver.put(Number.class, Number::intValue);

    Resolver<Double> doubleResolver = new Resolver<>(Double.class);
    doubleResolver.put(String.class, Doubles::tryParse);
    doubleResolver.put(Number.class, Number::doubleValue);

    Resolver<String> stringResolver = new Resolver<>(String.class);
    stringResolver.put(Object.class, Object::toString);

    Resolver<Complex> complexResolver = new Resolver<>(Complex.class);
    complexResolver.put(String.class, Complex::tryParse);

    Resolver<Bit> bitResolver = new Resolver<>(Bit.class);
    bitResolver.put(String.class, (v) -> Bit.valueOf(v.trim().equalsIgnoreCase("true")));

    install(Bit.class, bitResolver);
    install(LocalDate.class, localDateResolver);
    install(String.class, stringResolver);
    install(Double.class, doubleResolver);
    install(Integer.class, integerResolver);
    install(Complex.class, complexResolver);
  }

  private Resolvers() {
  }

  public static <T> void install(Class<T> cls, Resolver<T> resolver) {
    RESOLVERS.put(cls, resolver);
  }

  public static <T> Resolver<T> find(Class<T> cls) {
    @SuppressWarnings("unchecked")
    Resolver<T> resolver = (Resolver<T>) RESOLVERS.get(cls);
    return resolver;
  }

}
