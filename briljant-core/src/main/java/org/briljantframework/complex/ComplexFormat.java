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

package org.briljantframework.complex;

import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParsePosition;

/**
 * Created by isak on 14/03/15.
 */
public class ComplexFormat {

  private static final double[] SPECIAL = new double[]{Double.NEGATIVE_INFINITY,
                                                       Double.POSITIVE_INFINITY,
                                                       Double.NaN};

  private final NumberFormat realFormat, imagFormat;

  public ComplexFormat(NumberFormat realFormat, NumberFormat imagFormat) {
    this.realFormat = realFormat;
    this.imagFormat = imagFormat;
  }

  public ComplexFormat() {
    this(NumberFormat.getInstance(), NumberFormat.getInstance());
  }

  public String format(Complex complex) {
    FieldPosition pos = new FieldPosition(0);
    pos.setBeginIndex(0);
    pos.setEndIndex(0);

    StringBuffer builder = new StringBuffer();
    double re = complex.real();
    double im = complex.imag();
    realFormat.format(re, builder, pos);
    if (im > 0) {
      builder.append(" - ");
      imagFormat.format(im, builder, pos);
      builder.append("i");
    } else {
      builder.append(" + ");
      imagFormat.format(im, builder, pos);
      builder.append("i");
    }

    return builder.toString();
  }

  public Complex parse(String source, ParsePosition position) {
    int initialIndex = position.getIndex();
    ignoreWhitespace(source, position);

    Number real = parseNumber(source, realFormat, position);
    if (real == null) {
      position.setIndex(initialIndex);
      return null;
    }
    int startIndex = position.getIndex();
    char signChar = parseNextChar(source, position);
    int sign = 0;
    switch (signChar) {
      case 0:
        return new Complex(real.doubleValue());
      case '+':
        sign = 1;
        break;
      case '-':
        sign = -1;
        break;
      default:
        position.setIndex(initialIndex);
        position.setErrorIndex(startIndex);
        return null;
    }
    ignoreWhitespace(source, position);

    Number imag = parseNumber(source, imagFormat, position);
    if (imag == null) {
      position.setIndex(initialIndex);
      return null;
    } else {
      char i = parseNextChar(source, position);
      if (i != 'i') {
        return null;
      } else {
        return new Complex(real.doubleValue(), imag.doubleValue() * sign);
      }
    }
  }

  private char parseNextChar(String source, ParsePosition position) {
    ignoreWhitespace(source, position);
    int idx = position.getIndex();
    if (idx < source.length()) {
      char c = source.charAt(idx);
      position.setIndex(idx + 1);
      return c;
    } else {
      return 0;
    }
  }

  private Number parseNumber(String source, NumberFormat format, ParsePosition position) {
    int start = position.getIndex();
    Number number = format.parse(source, position);
    int end = position.getIndex();
    if (start == end) {
      for (double special : SPECIAL) {
        number = parseNumber(source, special, position);
        if (number != null) {
          break;
        }
      }
    }

    return number;
  }

  private Number parseNumber(String source, double special, ParsePosition position) {
    String value = "(" + special + ")";
    int start = position.getIndex();
    int end = start + value.length();
    if (end < source.length() && source.substring(start, end).equals(value)) {
      position.setIndex(end);
      return special;
    } else {
      return null;
    }
  }

  private void ignoreWhitespace(String source, ParsePosition position) {
    int idx = position.getIndex();
    int n = source.length();
    if (idx < n) {
      char c;
      do {
        c = source.charAt(idx++);
      } while (Character.isWhitespace(c) && idx < n);
      position.setIndex(idx - 1);
    }
  }
}
