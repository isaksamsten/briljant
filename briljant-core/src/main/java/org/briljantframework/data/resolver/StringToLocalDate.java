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
package org.briljantframework.data.resolver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Converts a string to a {@link LocalDate}.
 * 
 * @author Isak Karlsson
 */
public class StringToLocalDate implements Converter<String, LocalDate> {

  public static final StringToLocalDate ISO_DATE = new StringToLocalDate();
  private final DateTimeFormatter format;

  public StringToLocalDate(String pattern) {
    this(DateTimeFormatter.ofPattern(pattern));
  }

  public StringToLocalDate(DateTimeFormatter format) {
    this.format = format;
  }

  private StringToLocalDate() {
    this(DateTimeFormatter.ISO_DATE);
  }

  @Override
  public LocalDate convert(String t) {
    try {
      return LocalDate.parse(t, format);
    } catch (Exception e) {
      return null; // NA
    }
  }
}
