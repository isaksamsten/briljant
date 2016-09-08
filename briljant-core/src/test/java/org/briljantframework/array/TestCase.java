/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
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
package org.briljantframework.array;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by isak on 9/7/16.
 */
final class TestCase<E extends BaseArray<E>> {
  private Map<Object, E> actual;
  private Map<Object, Object> payload;
  private Map<Object, E> expected;

  protected TestCase(Map<Object, E> actual, Map<Object, E> expected) {
    this.actual = actual;
    this.expected = expected;
    this.payload = new HashMap<>();
  }

  protected TestCase() {
    this(new HashMap<>(), new HashMap<>());
  }

  public void setActual(Object key, E actual) {
    this.actual.put(key, actual);
  }

  public void setExpected(Object key, E expected) {
    this.expected.put(key, expected);
  }

  public void setActual(E actual) {
    this.actual.put(null, actual);
  }

  public void setExpected(E expected) {
    this.expected.put(null, expected);
  }

  public E getActual(Object key) {
    return actual.get(key);
  }

  public E getActual() {
    return actual.get(null);
  }

  public E getExpected(Object key) {
    return expected.get(key);
  }

  public E getExpected() {
    return expected.get(null);
  }

  public <T> T getPayload(String key, Class<T> cls) {
    return cls.cast(payload.get(key));
  }

  public void setPayload(Object key, Object value) {
    payload.put(key, value);
  }
}
