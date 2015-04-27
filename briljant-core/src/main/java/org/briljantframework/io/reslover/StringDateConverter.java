package org.briljantframework.io.reslover;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * @author Isak Karlsson
 */
public class StringDateConverter implements Converter<Date, String> {

  private final DateFormat format;

  public StringDateConverter(DateFormat format) {
    this.format = format;
  }

  public StringDateConverter() {
    this(DateFormat.getDateInstance(DateFormat.DEFAULT, Locale.getDefault()));
  }

  @Override
  public Date convert(String t) {
    try {
      return format.parse(t);
    } catch (Exception e) {
      return null;
    }
  }
}
