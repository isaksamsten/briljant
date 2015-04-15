package org.briljantframework.matrix;

import org.briljantframework.Briljant;
import org.briljantframework.complex.ComplexFormat;

import java.io.IOException;
import java.text.NumberFormat;

/**
 * @author Isak Karlsson
 */
public final class MatrixPrinter {

  private static int minTruncateSize = 100;
  private static int visibleRows = 3;
  private static int visibleColumns = 3;
  private static int max = 50;

  public static void setMinimumTruncateSize(int size) {
    minTruncateSize = size;
  }

  public static void setVisibleRows(int rows) {
    visibleRows = rows;
  }

  public static void setVisibleColumns(int cols) {
    visibleColumns = cols;
  }

  public static void print(ComplexMatrix matrix) {
    try {
      print(System.out, matrix);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void print(Appendable out, ComplexMatrix matrix) throws IOException {
    print(out, new ComplexToStringMatrix(matrix));
  }

  public static void print(DoubleMatrix matrix) {
    try {
      print(System.out, matrix);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void print(Appendable out, DoubleMatrix matrix) throws IOException {
    print(out, new DoubleToStringMatrix(matrix));
  }

  public static void print(BitMatrix matrix) {
    try {
      print(System.out, matrix);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void print(Appendable out, BitMatrix matrix) throws IOException {
    print(out, new LongToStringMatrix(matrix.asLongMatrix()));
  }

  public static void print(LongMatrix matrix) {
    try {
      print(System.out, matrix);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void print(Appendable out, LongMatrix matrix) throws IOException {
    print(out, new LongToStringMatrix(matrix));
  }

  public static void print(IntMatrix matrix) {
    try {
      print(System.out, matrix);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public static void print(Appendable out, IntMatrix matrix) throws IOException {
    print(out, new LongToStringMatrix(matrix.asLongMatrix()));
  }

  /**
   * Format the {@link org.briljantframework.matrix.MatrixPrinter.ToStringMatrix}
   *
   * @param out    write resulting string representation to
   * @param matrix the ToStringMatrix
   * @throws IOException if an IO error occurs
   */
  public static void print(Appendable out, ToStringMatrix matrix) throws IOException {
    IntMatrix widths = Briljant.intVector(matrix.columns());
    for (int j = 0; j < matrix.columns(); j++) {
      int m = 0;
      for (int i = 0; i < matrix.rows(); i++) {
        int length = matrix.get(i, j).length();
        if (length > m) {
          m = length;
        }
      }
      widths.set(j, m);
    }

    if (matrix.size() <= minTruncateSize || matrix.rows() < visibleRows * 2) {
      out.append("[");
      printRow(out, matrix.getRowView(0), widths, false);
      for (int i = 1; i < matrix.rows(); i++) {
        out.append("\n");
        out.append(" ");
        printRow(out, matrix.getRowView(i), widths, false);
      }
      out.append("]");
    } else {
      out.append("[");
      printRow(out, matrix.getRowView(0), widths, true);
      for (int i = 1; i < visibleRows; i++) {
        out.append("\n");
        out.append(" ");
        printRow(out, matrix.getRowView(i), widths, true);
      }
      out.append("\n");
      out.append("   ...\n");
      out.append(" ");
      printRow(out, matrix.getRowView(matrix.rows() - visibleRows), widths, true);
      for (int i = matrix.rows() - visibleRows + 1; i < matrix.rows(); i++) {
        out.append("\n");
        out.append(" ");
        printRow(out, matrix.getRowView(i), widths, true);
      }
      out.append("]");
    }

  }

  private static void printValue(Appendable out, String value, int pad) throws IOException {
    for (int j = 0; j < pad; j++) {
      out.append(" ");
    }
    out.append(value);
  }

  private static void printRow(Appendable out, ToStringMatrix values, IntMatrix widths,
                               boolean truncate) throws IOException {
    String f = values.get(0);
    if (!truncate || values.size() < visibleColumns * 2) {
      out.append("[");
      printValue(out, f, widths.get(0) - f.length());
      for (int i = 1; i < values.size(); i++) {
        f = values.get(i);
        out.append(", ");
        printValue(out, f, widths.get(i) - f.length());
      }
      out.append("]");
    } else {
      out.append("[");
      printValue(out, f, widths.get(0) - f.length());
      for (int i = 1; i < visibleColumns; i++) {
        f = values.get(i);
        out.append(", ");
        printValue(out, f, widths.get(i) - f.length());
      }
      out.append(", ..., ");
      f = values.get(values.size() - visibleColumns);
      printValue(out, f, widths.get(values.size() - visibleColumns) - f.length());
      for (int i = values.size() - visibleColumns + 1; i < values.size(); i++) {
        f = values.get(i);
        out.append(", ");
        printValue(out, f, widths.get(i) - f.length());
      }
      out.append("]");
    }
  }

  public static interface ToStringMatrix {


    /**
     * Convert value at {@code i} to string
     *
     * @param i the index
     * @return the string representation
     */
    String get(int i);

    /**
     * Convert value at {@code i, j} to string
     *
     * @param i the index
     * @return the string representation
     */
    String get(int i, int j);

    /**
     * Get row at {@code i} as {@code ToStringMatrix}
     *
     * @param i the index
     * @return the string representation
     */
    ToStringMatrix getRowView(int i);

    /**
     * Get column at {@code i} as {@code ToStringMatrix}
     *
     * @param i the index
     * @return the string representation
     */
    ToStringMatrix getColumnView(int i);

    /**
     * Return the rows
     *
     * @return the rows
     */
    int rows();

    /**
     * Return the columns
     *
     * @return the columns
     */
    int columns();

    /**
     * Returns the size
     *
     * @return the size
     */
    int size();

  }

  private static class DoubleToStringMatrix implements ToStringMatrix {

    private final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    private final DoubleMatrix matrix;

    public DoubleToStringMatrix(DoubleMatrix matrix) {
      this.matrix = matrix;
    }

    @Override
    public String get(int i) {
      return NUMBER_FORMAT.format(matrix.get(i));
    }

    @Override
    public String get(int i, int j) {
      return NUMBER_FORMAT.format(matrix.get(i, j));
    }

    @Override
    public ToStringMatrix getRowView(int i) {
      return new DoubleToStringMatrix(matrix.getRowView(i));
    }

    @Override
    public ToStringMatrix getColumnView(int i) {
      return new DoubleToStringMatrix(matrix.getRowView(i));
    }

    @Override
    public int rows() {
      return matrix.rows();
    }

    @Override
    public int columns() {
      return matrix.columns();
    }

    @Override
    public int size() {
      return matrix.size();
    }
  }

  private static class LongToStringMatrix implements ToStringMatrix {

    private final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance();

    private final LongMatrix matrix;

    public LongToStringMatrix(LongMatrix matrix) {
      this.matrix = matrix;
    }

    @Override
    public String get(int i) {
      return NUMBER_FORMAT.format(matrix.get(i));
    }

    @Override
    public String get(int i, int j) {
      return NUMBER_FORMAT.format(matrix.get(i, j));
    }

    @Override
    public ToStringMatrix getRowView(int i) {
      return new LongToStringMatrix(matrix.getRowView(i));
    }

    @Override
    public ToStringMatrix getColumnView(int i) {
      return new LongToStringMatrix(matrix.getColumnView(i));
    }

    @Override
    public int rows() {
      return matrix.rows();
    }

    @Override
    public int columns() {
      return matrix.columns();
    }

    @Override
    public int size() {
      return matrix.size();
    }
  }

  private static class ComplexToStringMatrix implements ToStringMatrix {

    private final ComplexFormat COMPLEX_FORMAT = new ComplexFormat();

    private final ComplexMatrix matrix;

    public ComplexToStringMatrix(ComplexMatrix matrix) {
      this.matrix = matrix;
    }

    @Override
    public String get(int i) {
      return COMPLEX_FORMAT.format(matrix.get(i));
    }

    @Override
    public String get(int i, int j) {
      return COMPLEX_FORMAT.format(matrix.get(i, j));
    }

    @Override
    public ToStringMatrix getRowView(int i) {
      return new ComplexToStringMatrix(matrix.getRowView(i));
    }

    @Override
    public ToStringMatrix getColumnView(int i) {
      return new ComplexToStringMatrix(matrix.getColumnView(i));
    }

    @Override
    public int rows() {
      return matrix.rows();
    }

    @Override
    public int columns() {
      return matrix.columns();
    }

    @Override
    public int size() {
      return matrix.size();
    }
  }
}
