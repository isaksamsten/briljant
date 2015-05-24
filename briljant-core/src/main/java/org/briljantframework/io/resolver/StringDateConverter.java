package org.briljantframework.io.resolver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * @author Isak Karlsson
 */
public class StringDateConverter implements Converter<String, LocalDate> {

  private final DateTimeFormatter format;

  public StringDateConverter(DateTimeFormatter format) {
    this.format = format;
  }

  public StringDateConverter(String pattern) {
    this(DateTimeFormatter.ofPattern(pattern));
  }

  public StringDateConverter() {
    this(DateTimeFormatter.ISO_DATE);
  }

  @Override
  public LocalDate convert(String t) {
    try {
      return LocalDate.parse(t, format);
    } catch (Exception e) {
      return null;
    }
  }
}
