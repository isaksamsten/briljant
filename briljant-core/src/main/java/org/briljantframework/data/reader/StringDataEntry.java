/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.data.reader;

import org.apache.commons.lang3.math.NumberUtils;
import org.briljantframework.data.Is;
import org.briljantframework.data.Na;
import org.briljantframework.data.resolver.Resolve;

/**
 * Converts a string array to a data entry.
 * 
 * @author Isak Karlsson
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
      return Na.of(cls);
    }
    return Resolve.to(cls, value);
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
    if (repr == null || !NumberUtils.isNumber(repr)) {
      return Na.INT;
    }
    return NumberUtils.createNumber(repr).intValue();
  }

  @Override
  public double nextDouble() {
    String repr = nextString();
    if (repr == null || !NumberUtils.isNumber(repr)) {
      return Na.DOUBLE;
    }
    return NumberUtils.createNumber(repr).doubleValue();
  }

  @Override
  public boolean hasNext() {
    return current < size();
  }

  @Override
  public void skip(int no) {
    if (current + no < size()) {
      current += no;
    }
  }

  @Override
  public int size() {
    return values.length;
  }
}
