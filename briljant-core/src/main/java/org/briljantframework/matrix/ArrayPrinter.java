package org.briljantframework.matrix;

import org.briljantframework.complex.ComplexFormat;

import java.io.IOException;
import java.text.NumberFormat;
import java.util.StringJoiner;

/**
 * @author Isak Karlsson
 */
public final class ArrayPrinter {

  private static int minTruncateSize = 100;
  private static int printSlices = 2;
  private static int visiblePerSlice = 2;

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
      print(System.out, new DoubleToStringArray(x), start, end);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void print(Appendable out, DoubleArray matrix) throws IOException {
    print(out, new DoubleToStringArray(matrix), "[", "]");
  }

  public static void print(BitArray matrix) {
    try {
      print(System.out, matrix);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void print(Appendable out, BitArray matrix) throws IOException {
    print(out, new LongToStringArray(matrix.asLongMatrix()), "[", "]");
  }

  public static void print(LongArray matrix) {
    try {
      print(System.out, matrix);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void print(Appendable out, LongArray matrix) throws IOException {
    print(out, new LongToStringArray(matrix), "[", "]");
  }

  public static void print(IntArray matrix) {
    try {
      print(System.out, matrix);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void print(Appendable out, IntArray matrix) throws IOException {
    print(out, new LongToStringArray(matrix.asLongMatrix()), "[", "]");
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
    print(
        out,
        arr,
        startChar,
        endChar,
        minTruncateSize < arr.size(),
        arr.dims() + 1 + "array(".length()
    );
    out.append(")");
  }

  /**
   * Format the {@link org.briljantframework.matrix.ArrayPrinter.ToStringArray}
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
                           int dims)
      throws IOException {
    if (arr.dims() == 1) {
      StringJoiner joiner = new StringJoiner(", ", startChar, endChar);
      int max = arr.size();
      if (truncate) {
        max = visiblePerSlice < 0 ? arr.size() : visiblePerSlice;
      }
      for (int i = 0; i < arr.size(); i++) {
        joiner.add(arr.get(i));
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
      print(sb, arr.select(0), startChar, endChar, truncate, dims);
      for (int i = 1; i < len; i++) {
        if (arr.dims() > 2) {
          sb.append(",\n\n");
        } else {
          sb.append(",\n");
        }
        for (int j = 0; j < dims - arr.dims(); j++) {
          sb.append(" ");
        }
        print(sb, arr.select(i), startChar, endChar, truncate, dims);
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

  public static interface ToStringArray {

    String get(int i);

    int dims();

    ToStringArray select(int dim);

    int size(int dim);

    int size();
  }

  private static class DoubleToStringArray implements ToStringArray {

    private final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    private final DoubleArray array;

    public DoubleToStringArray(DoubleArray array) {
      this.array = array;
    }


    @Override
    public String get(int i) {
      return NUMBER_FORMAT.format(array.get(i));
    }

    @Override
    public int dims() {
      return array.dims();
    }

    @Override
    public ToStringArray select(int dim) {
      return new DoubleToStringArray(array.select(dim));
    }

    @Override
    public int size(int dim) {
      return array.size(dim);
    }

    @Override
    public int size() {
      return array.size();
    }
  }

  private static class LongToStringArray implements ToStringArray {

    private final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    private final LongArray array;

    public LongToStringArray(LongArray array) {
      this.array = array;
    }

    @Override
    public String get(int i) {
      return NUMBER_FORMAT.format(array.get(i));
    }

    @Override
    public int dims() {
      return array.dims();
    }

    @Override
    public ToStringArray select(int dim) {
      return new LongToStringArray(array.select(dim));
    }

    @Override
    public int size(int dim) {
      return array.size(dim);
    }

    @Override
    public int size() {
      return array.size();
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
  }
}
