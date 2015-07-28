package org.briljantframework.array;

import org.briljantframework.Bj;
import org.briljantframework.complex.ComplexFormat;

import java.io.IOException;
import java.util.StringJoiner;

/**
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

  public static void print(ComplexArray matrix) {
    try {
      print(System.out, matrix);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void print(Appendable out, ComplexArray matrix) throws IOException {
    print(out, new ComplexToStringArray(matrix), "[", "]");
  }

  public static void print(DoubleArray matrix) {
    try {
      print(System.out, matrix);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void print(DoubleArray x, String start, String end) {
    try {
      print(System.out, new DoubleToStringArray(x, floatFormat), start, end);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void print(Appendable out, DoubleArray matrix) throws IOException {
    print(out, new DoubleToStringArray(matrix, floatFormat), "[", "]");
  }

  public static void print(BitArray matrix) {
    try {
      print(System.out, matrix);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void print(Appendable out, BitArray matrix) throws IOException {
    print(out, new LongToStringArray(matrix.asLong(), intFormat), "[", "]");
  }

  public static void print(LongArray matrix) {
    try {
      print(System.out, matrix);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void print(Appendable out, LongArray matrix) throws IOException {
    print(out, new LongToStringArray(matrix, intFormat), "[", "]");
  }

  public static void print(IntArray matrix) {
    try {
      print(System.out, matrix);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void print(Appendable out, IntArray matrix) throws IOException {
    print(out, new LongToStringArray(matrix.asLong(), intFormat), "[", "]");
  }

  public static <T> void print(Appendable out, Array<T> array) throws IOException {
    print(out, new ArrayToStringArray<>(array), "[", "]");
  }

  /**
   * Format the {@link ArrayPrinter.ToStringArray}
   *
   * @param out write resulting string representation to
   * @param arr the ToStringMatrix
   * @throws java.io.IOException if an IO error occurs
   */
  public static void print(Appendable out, ToStringArray arr, String startChar, String endChar)
      throws IOException {
    out.append("array(");
    if (arr.size() == 0) {
      out.append(startChar).append(endChar);
    } else if (arr.size() == 1) {
      out.append(startChar).append(arr.get(0)).append(endChar);
    } else {
      boolean truncate = minTruncateSize < arr.size() && arr.dims() != 1;
      IntArray maxWidth = computeMaxWidth(arr, truncate);
      print(
          out,
          arr,
          startChar,
          endChar,
          truncate,
          arr.dims() + 1 + "array(".length(),
          maxWidth
      );
    }
    out.append(" type: ").append(arr.type()).append(")");
  }

  private static IntArray computeMaxWidth(ToStringArray arr, boolean truncate) {
    int lastDim = arr.dims() - 1;
    int maxPerSlice = arr.size(lastDim);
    if (truncate) {
      maxPerSlice = visiblePerSlice < 0 ? maxPerSlice : visiblePerSlice;
    }
    IntArray maxWidth = Bj.intArray(maxPerSlice);
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
   * @param sb  write resulting string representation to
   * @param arr the ToStringMatrix
   * @throws java.io.IOException if an IO error occurs
   */
  public static void print(Appendable sb,
                           ToStringArray arr,
                           String startChar,
                           String endChar,
                           boolean truncate,
                           int dims,
                           IntArray maxWidth)
      throws IOException {
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
      print(sb, arr.select(0), startChar, endChar, truncate, dims, maxWidth);
      for (int i = 1; i < len; i++) {
        if (arr.dims() > 2) {
          sb.append(",\n\n");
        } else {
          sb.append(",\n");
        }
        for (int j = 0; j < dims - arr.dims(); j++) {
          sb.append(" ");
        }
        print(sb, arr.select(i), startChar, endChar, truncate, dims, maxWidth);
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

  public static interface ToStringArray {

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

    public DoubleToStringArray(DoubleArray array, String formatString) {
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

    public LongToStringArray(LongArray array, String formatString) {
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

    public ComplexToStringArray(ComplexArray array) {
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

  private static class ArrayToStringArray<T> implements ToStringArray {

    private final Array<T> array;

    public ArrayToStringArray(Array<T> array) {
      this.array = array;
    }

    @Override
    public String get(int i) {
      return array.get(i).toString();
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
      return new ArrayToStringArray<>(array.getVector(dim, index));
    }

    @Override
    public ToStringArray select(int dim) {
      return new ArrayToStringArray<>(array.select(dim));
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
      return array.get(0).getClass().getSimpleName();
    }
  }
}
