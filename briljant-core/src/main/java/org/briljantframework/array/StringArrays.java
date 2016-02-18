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

import org.apache.commons.lang3.StringUtils;

/**
 * Vectorized string operations.
 */
public class StringArrays {

  public static BooleanArray isEmpty(Array<? extends CharSequence> array) {
    return array.where(StringUtils::isEmpty);
  }

  public static BooleanArray isNotEmpty(Array<? extends CharSequence> array) {
    return array.where(StringUtils::isNotEmpty);
  }

  public static BooleanArray isAnyEmpty(Array<? extends CharSequence> array) {
    return array.where(StringUtils::isAnyEmpty);
  }

  public static BooleanArray isNoneEmpty(Array<? extends CharSequence> array) {
    return array.where(StringUtils::isNoneEmpty);
  }

  public static BooleanArray isBlank(Array<? extends CharSequence> array) {
    return array.where(StringUtils::isBlank);
  }

  public static BooleanArray isNotBlank(Array<? extends CharSequence> array) {
    return array.where(StringUtils::isEmpty);
  }

  public static BooleanArray isAnyBlank(Array<? extends CharSequence> array) {
    return array.where(StringUtils::isAnyBlank);
  }

  public static BooleanArray isNoneBlank(Array<? extends CharSequence> array) {
    return array.where(StringUtils::isNoneBlank);
  }

  public static BooleanArray contains(Array<? extends CharSequence> array, char c) {
    return array.where(ch -> StringUtils.contains(ch, c));
  }

  public static BooleanArray contains(Array<? extends CharSequence> array, CharSequence c) {
    return array.where(ch -> StringUtils.contains(ch, c));
  }

  public static BooleanArray containsAny(Array<? extends CharSequence> array, String any) {
    return array.where(ch -> StringUtils.containsAny(ch, any));
  }

  public static <T extends CharSequence> BooleanArray equalsIgnoreCase(Array<T> x, Array<T> y) {
    return x.where(y, StringUtils::equalsIgnoreCase);
  }

  public static IntArray indexOf(Array<? extends CharSequence> array, char c) {
    return array.mapToInt(seq -> StringUtils.indexOf(seq, c));
  }

  public static IntArray indexOf(Array<? extends CharSequence> array, CharSequence c) {
    return array.mapToInt(seq -> StringUtils.indexOf(seq, c));
  }

  public static IntArray lastIndexOf(Array<? extends CharSequence> array, char c) {
    return array.mapToInt(seq -> StringUtils.lastIndexOf(seq, c));
  }

  public static IntArray lastIndexOf(Array<? extends CharSequence> array, CharSequence c) {
    return array.mapToInt(seq -> StringUtils.lastIndexOf(seq, c));
  }

  public static IntArray indexOfAny(Array<? extends CharSequence> array, String any) {
    return array.mapToInt(seq -> StringUtils.indexOfAny(seq, any));
  }

  public static Array<String> trim(Array<String> array) {
    return array.map(StringUtils::trim);
  }

  public static Array<String> trimToEmpty(Array<String> array) {
    return array.map(StringUtils::trimToEmpty);
  }

  public static Array<String> strip(Array<String> array) {
    return array.map(StringUtils::strip);
  }

  public static Array<String> stripToEmpty(Array<String> array) {
    return array.map(StringUtils::stripToEmpty);
  }


}
