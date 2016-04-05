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

import java.util.StringJoiner;

import org.apache.commons.math3.complex.ComplexFormat;

/**
 * Utility class for (pretty) printing arrays.
 * 
 * @author Isak Karlsson
 */
public final class ArrayPrinter {

  private static int minTruncateSize = 100;
  private static int printSlices = 2;
  private static int visiblePerSlice = 2;
  private static String intFormat = "%d";
  private static String floatFormat = "%.3f";

  public static void setMinimumTruncateSize(int size) {
    minTruncateSize = size;
  }

  public static void setPrintSlices(int rows) {
    printSlices = rows;
  }

  public static void setVisiblePerSlice(int cols) {
    visiblePerSlice = cols;
  }

  public static String toString(ComplexArray array) {
    StringBuilder builder = new StringBuilder();
    append(builder, array);
    return builder.toString();
  }

  public static String toString(LongArray array) {
    StringBuilder builder = new StringBuilder();
    append(builder, array);
    return builder.toString();
  }

  public static String toString(IntArray array) {
    StringBuilder builder = new StringBuilder();
    append(builder, array);
    return builder.toString();
  }

  public static String toString(DoubleArray array) {
    StringBuilder builder = new StringBuilder();
    append(builder, array);
    return builder.toString();
  }

  public static String toString(BooleanArray array) {
    StringBuilder builder = new StringBuilder();
    append(builder, array);
    return builder.toString();
  }

  public static String toString(Array<?> array) {
    StringBuilder builder = new StringBuilder();
    append(builder, array);
    return builder.toString();
  }

  public static void append(StringBuilder out, ComplexArray matrix) {
    append(out, new ComplexToStringArray(matrix), "[", "]");
  }

  public static void append(StringBuilder out, DoubleArray matrix) {
    append(out, new DoubleToStringArray(matrix, floatFormat), "[", "]");
  }

  public static void append(StringBuilder out, BooleanArray matrix) {
    append(out, new LongToStringArray(matrix.asLong(), intFormat), "[", "]");
  }

  public static void append(StringBuilder out, LongArray matrix) {
    append(out, new LongToStringArray(matrix, intFormat), "[", "]");
  }

  public static void append(StringBuilder out, IntArray matrix) {
    append(out, new LongToStringArray(matrix.asLong(), intFormat), "[", "]");
  }

  public static void append(StringBuilder out, Array<?> array) {
    append(out, new ReferenceArrayToStringArray(array), "[", "]");
  }

  /**
   * Format the {@link ArrayPrinter.ToStringArray}
   *
   * @param out write resulting string representation to
   * @param arr the ToStringMatrix
   */
  public static void append(StringBuilder out, ToStringArray arr, String startChar,
      String endChar) {
    out.append("array(");
    if (arr.size() == 0) {
      out.append(startChar).append(endChar);
    } else if (arr.size() == 1) {
      out.append(startChar).append(arr.get(0)).append(endChar);
    } else {
      boolean truncate = minTruncateSize < arr.size() && arr.dims() != 1;
      IntArray maxWidth = computeMaxWidth(arr, truncate);
      append(out, arr, startChar, endChar, truncate, arr.dims() + 1 + "array(".length(), maxWidth);
    }
    out.append(")");
  }

  private static IntArray computeMaxWidth(ToStringArray arr, boolean truncate) {
    int lastDim = arr.dims() - 1;
    int maxPerSlice = arr.size(lastDim);
    if (truncate) {
      maxPerSlice = visiblePerSlice < 0 ? maxPerSlice : visiblePerSlice;
    }
    IntArray maxWidth = IntArray.zeros(maxPerSlice);
    return computeMaxWidthRecursive(arr, truncate, maxWidth, maxPerSlice);
  }

  private static IntArray computeMaxWidthRecursive(ToStringArray arr, boolean truncate,
      IntArray maxWidth, int maxPerSlice) {
    if (arr.dims() == 1) {
      for (int i = 0; i < arr.size(); i++) {
        int index = i % maxPerSlice;
        int currentMax = maxWidth.get(index);
        int current = arr.get(i).length();
        if (current > currentMax) {
          maxWidth.set(index, current);
        }
        if (i >= maxPerSlice) {
          int left = arr.size() - i - 1;
          if (left > maxPerSlice) {
            i += left - maxPerSlice - 1;
          }
        }
      }
    } else {
      int len = arr.size(0);
      for (int i = 0; i < len; i++) {
        computeMaxWidthRecursive(arr.select(i), truncate, maxWidth, maxPerSlice);
        int max = len;
        if (truncate) {
          max = printSlices < 0 ? len : printSlices;
        }
        if (i >= max) {
          int left = len - i - 1;
          if (left > max) {
            i += left - max - 1;
          }
        }
      }
    }

    return maxWidth;
  }

  /**
   * Format the {@link org.briljantframework.array.ArrayPrinter.ToStringArray}
   *
   * @param sb write resulting string representation to
   * @param arr the matrix
   */
  public static void append(StringBuilder sb, ToStringArray arr, String startChar, String endChar,
      boolean truncate, int dims, IntArray maxWidth) {
    if (arr.size() == 0) {
      sb.append(startChar).append(arr.get(0)).append(endChar);
      return;
    }
    if (arr.dims() == 1) {
      StringJoiner joiner = new StringJoiner(", ", startChar, endChar);
      int max = arr.size();
      if (truncate) {
        max = visiblePerSlice < 0 ? arr.size() : visiblePerSlice;
      }
      for (int i = 0; i < arr.size(); i++) {
        String value = pad(arr.get(i), maxWidth.get(i % max));
        joiner.add(value);
        if (i >= max) {
          int left = arr.size() - i - 1;
          if (left > max) {
            i += left - max - 1;
            joiner.add("...");
          }
        }
      }
      sb.append(joiner.toString());
    } else {
      int len = arr.size(0);
      sb.append("[");
      append(sb, arr.select(0), startChar, endChar, truncate, dims, maxWidth);
      for (int i = 1; i < len; i++) {
        if (arr.dims() > 2) {
          sb.append(",\n\n");
        } else {
          sb.append(",\n");
        }
        for (int j = 0; j < dims - arr.dims(); j++) {
          sb.append(" ");
        }
        append(sb, arr.select(i), startChar, endChar, truncate, dims, maxWidth);
        int max = len;
        if (truncate) {
          max = printSlices < 0 ? len : printSlices;
        }
        if (i >= max) {
          int left = len - i - 1;
          if (left > max) {
            i += left - max - 1;
            sb.append(",\n");
            for (int j = 0; j < dims - arr.dims(); j++) {
              sb.append(" ");
            }
            sb.append("...");
          }
        }
      }
      sb.append("]");
    }
  }

  private static String pad(String value, int pad) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < pad - value.length(); i++) {
      builder.append(" ");
    }
    return builder.append(value).toString();
  }

  public static void setFloatFormat(String floatFormat) {
    ArrayPrinter.floatFormat = floatFormat;
  }

  public static void setIntFormat(String intFormat) {
    ArrayPrinter.intFormat = intFormat;
  }

  /**
   * Interface for converting arrays to string representations.
   * 
   * @author Isak Karlsson
   */
  public interface ToStringArray {

    String get(int i);

    int dims();

    int vectors(int i);

    ToStringArray getVector(int dim, int index);

    ToStringArray select(int dim);

    int size(int dim);

    int size();

    String type();
  }

  private static class DoubleToStringArray implements ToStringArray {

    private final String formatString;
    private final DoubleArray array;

    DoubleToStringArray(DoubleArray array, String formatString) {
      this.array = array;
      this.formatString = formatString;
    }


    @Override
    public String get(int i) {
      return String.format(formatString, array.get(i));
    }

    @Override
    public int dims() {
      return array.dims();
    }

    @Override
    public int vectors(int i) {
      return array.vectors(i);
    }

    @Override
    public ToStringArray getVector(int dim, int index) {
      return new DoubleToStringArray(array.getVector(dim, index), formatString);
    }

    @Override
    public ToStringArray select(int dim) {
      return new DoubleToStringArray(array.select(dim), formatString);
    }

    @Override
    public int size(int dim) {
      return array.size(dim);
    }

    @Override
    public int size() {
      return array.size();
    }

    @Override
    public String type() {
      return "double";
    }
  }

  private static class LongToStringArray implements ToStringArray {

    private final String formatString;
    private final LongArray array;

    LongToStringArray(LongArray array, String formatString) {
      this.array = array;
      this.formatString = formatString;
    }

    @Override
    public String get(int i) {
      return String.format(formatString, array.get(i));
    }

    @Override
    public int dims() {
      return array.dims();
    }

    @Override
    public int vectors(int i) {
      return array.vectors(i);
    }

    @Override
    public ToStringArray getVector(int dim, int index) {
      return new LongToStringArray(array.getVector(dim, index), formatString);
    }

    @Override
    public ToStringArray select(int dim) {
      return new LongToStringArray(array.select(dim), formatString);
    }

    @Override
    public int size(int dim) {
      return array.size(dim);
    }

    @Override
    public int size() {
      return array.size();
    }

    @Override
    public String type() {
      return "int";
    }
  }

  private static class ComplexToStringArray implements ToStringArray {

    private final ComplexFormat COMPLEX_FORMAT = new ComplexFormat();

    private final ComplexArray array;

    ComplexToStringArray(ComplexArray array) {
      this.array = array;
    }

    @Override
    public String get(int i) {
      return COMPLEX_FORMAT.format(array.get(i));
    }

    @Override
    public int dims() {
      return array.dims();
    }

    @Override
    public int vectors(int i) {
      return array.vectors(i);
    }

    @Override
    public ToStringArray getVector(int dim, int index) {
      return new ComplexToStringArray(array.getVector(dim, index));
    }

    @Override
    public ToStringArray select(int dim) {
      return new ComplexToStringArray(array.select(dim));
    }

    @Override
    public int size(int dim) {
      return array.size(dim);
    }

    @Override
    public int size() {
      return array.size();
    }

    @Override
    public String type() {
      return "complex";
    }
  }

  private static class ReferenceArrayToStringArray implements ToStringArray {

    private final Array<?> array;

    ReferenceArrayToStringArray(Array<?> array) {
      this.array = array;
    }

    @Override
    public String get(int i) {
      Object value = array.get(i);
      return value == null ? "null" : value.toString();
    }

    @Override
    public int dims() {
      return array.dims();
    }

    @Override
    public int vectors(int i) {
      return array.vectors(i);
    }

    @Override
    public ToStringArray getVector(int dim, int index) {
      return new ReferenceArrayToStringArray(array.getVector(dim, index));
    }

    @Override
    public ToStringArray select(int dim) {
      return new ReferenceArrayToStringArray(array.select(dim));
    }

    @Override
    public int size(int dim) {
      return array.size(dim);
    }

    @Override
    public int size() {
      return array.size();
    }

    @Override
    public String type() {
      return "object";
    }
  }
}
