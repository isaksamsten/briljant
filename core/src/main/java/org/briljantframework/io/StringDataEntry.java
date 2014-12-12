package org.briljantframework.io;

import java.io.IOException;

import org.briljantframework.vector.*;

import com.google.common.primitives.Doubles;

/**
 * A string data entry holds string values and tries to convert them to appropriate types. Such
 * failures won't propagate, instead the respective NA value will be returned.
 */
public class StringDataEntry implements DataEntry {

  private final String[] values;
  private int current = 0;

  public StringDataEntry(String... values) {
    this.values = values;
  }

  @Override
  public String nextString() throws IOException {
    String value = values[current++];
    return value.equals("?") ? StringVector.NA : value;
  }

  @Override
  public int nextInt() throws IOException {
    String repr = nextString();
    return repr == StringVector.NA ? IntVector.NA : Integer.parseInt(repr);
  }

  @Override
  public double nextDouble() throws IOException {
    String repr = nextString();
    if (repr == StringVector.NA) {
      return DoubleVector.NA;
    } else {
      Double d = Doubles.tryParse(repr);
      return d == null ? DoubleVector.NA : d;
    }
  }

  @Override
  public Binary nextBinary() throws IOException {
    return Binary.valueOf(nextInt());
  }

  @Override
  public Complex nextComplex() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasNext() throws IOException {
    return current < size();
  }

  @Override
  public int size() throws IOException {
    return values.length;
  }
}
