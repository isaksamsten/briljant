package org.briljantframework.io;

import com.google.common.primitives.Doubles;
import com.google.common.primitives.Ints;

import org.briljantframework.complex.Complex;
import org.briljantframework.io.reslover.Resolver;
import org.briljantframework.io.reslover.Resolvers;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.Na;
import org.briljantframework.vector.StringVector;

/**
 * A string data entry holds string values and tries to convert them to appropriate types. Such
 * failures won't propagate, instead the respective NA value will be returned.
 */
public class StringDataEntry implements DataEntry {

  public static final String MISSING_VALUE = "?";
  private final String[] values;
  private final String missingValue;
  private int current = 0;

  public StringDataEntry(String... values) {
    this(values, MISSING_VALUE);
  }

  public StringDataEntry(String[] values, String missingValue) {
    this.values = values;
    this.missingValue = missingValue;
  }

  @Override
  public <T> T next(Class<T> cls) {
    String value = nextString();
    if (value == StringVector.NA) {
      return Na.of(cls);
    } else {
      Resolver<T> resolver = Resolvers.find(cls);
      if (resolver == null) {
        return Na.of(cls);
      } else {
        return resolver.resolve(value);
      }
    }
  }

  @Override
  public String nextString() {
    String value = values[current++];
    if (value == null) {
      return StringVector.NA;
    } else {
      value = value.trim();
      return value.equals(missingValue) ? StringVector.NA : value;
    }
  }

  @Override
  public int nextInt() {
    String repr = nextString();
    if (repr == StringVector.NA) {
      return IntVector.NA;
    }
    Integer integer = Ints.tryParse(repr);
    return integer == null ? IntVector.NA : integer;
  }

  @Override
  public double nextDouble() {
    String repr = nextString();
    if (repr == StringVector.NA) {
      return DoubleVector.NA;
    } else {
      Double d = Doubles.tryParse(repr);
      return d == null ? DoubleVector.NA : d;
    }
  }

  @Override
  public Bit nextBinary() {
    return Bit.valueOf(nextInt());
  }

  @Override
  public Complex nextComplex() {
    return next(Complex.class);
  }

  @Override
  public boolean hasNext() {
    return current < size();
  }

  @Override
  public int size() {
    return values.length;
  }
}
