/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.io;

import org.briljantframework.io.resolver.Resolver;
import org.briljantframework.io.resolver.Resolvers;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Na;

/**
 * A string data entry holds string values and tries to convert them to appropriate types. Such
 * failures won't propagate, instead the respective NA value will be returned.
 */
public final class StringDataEntry implements DataEntry {

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
    if (Is.NA(value)) {
      return Na.from(cls);
    }
    Resolver<T> resolver = Resolvers.find(cls);
    if (resolver == null) {
      return Na.from(cls);
    } else {
      return resolver.resolve(value);
    }
  }

  @Override
  public String nextString() {
    String value = values[current++];
    if (value == null) {
      return null;
    } else {
      value = value.trim();
      return value.equals(missingValue) ? null : value;
    }
  }

  @Override
  public int nextInt() {
    String repr = nextString();
    if (repr == null) {
      return Na.INT;
    }
    try {
      return Integer.parseInt(repr);
    } catch (NumberFormatException e) {
      return Na.INT;
    }
  }

  @Override
  public double nextDouble() {
    String repr = nextString();
    if (repr == null) {
      return Na.DOUBLE;
    }
    try {
      return Double.parseDouble(repr);
    } catch (NumberFormatException e) {
      return Na.DOUBLE;
    }
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
