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

import java.util.*;

/**
 * Create a list of arrays that will be manipulated and given back to the test method.
 *
 * @param <E>
 */
public abstract class ArrayTest<E extends BaseArray<E>> implements Iterable<TestCase<E>> {

  private final List<TestCase<E>> testCase = new ArrayList<>();

  public void addTestCase(TestCase<E> test) {
    testCase.add(test);
  }

  public void addTestCase(Map<Object, E> actual, Map<Object, E> expected) {
    testCase.add(new TestCase<E>(actual, expected));
  }

  public void addTestCase(E value, E expected) {
    addTestCase(null, value, expected);
  }

  public void addTestCase(String key, E value, E e) {
    Map<Object, E> actual = new HashMap<>();
    actual.put(key, value);
    Map<Object, E> expected = new HashMap<>();
    expected.put(null, e);
    testCase.add(new TestCase<>(actual, expected));
  }

  public void addTestCase(String key, E value, String key2, E value2, E e) {
    Map<Object, E> actual = new HashMap<>();
    actual.put(key, value);
    actual.put(key2, value2);
    Map<Object, E> expected = new HashMap<>();
    expected.put(null, e);
    testCase.add(new TestCase<E>(actual, expected));
  }

  public List<TestCase<E>> getTestCases() {
    return testCase;
  }

  abstract void assertEqual(E actual, E expected);

  @Override
  public Iterator<TestCase<E>> iterator() {
    return testCase.iterator();
  }

  public boolean isEmpty() {
    return testCase.isEmpty();
  }
}
