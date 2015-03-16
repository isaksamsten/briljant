package org.briljantframework.io;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.RowProcessor;
import com.univocity.parsers.csv.CsvFormat;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import org.briljantframework.vector.ComplexVector;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.StringVector;
import org.briljantframework.vector.VectorType;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Reads values from a typed CSV-file similar to those used in Rule Discovery System (RDS).
 *
 * The file-format is simple, a comma separated file with the first two rows being the column names
 * and the types respectively.
 *
 * <p>The types are simple strings and are mapped to {@code briljant} data types as follows:
 *
 * <ul> <li>{@code numeric} and {@code regressor}: {@link org.briljantframework.vector.DoubleVector}</li>
 * <li>{@code categoric} and {@code class}: {@link org.briljantframework.vector.StringVector}</li>
 * <li>{@code date}: {@link org.briljantframework.vector.GenericVector}</li> <li>{@code complex}:
 * {@link org.briljantframework.vector.ComplexVector}</li> </ul>
 *
 * By convention, missing values are represented as {@code ?}.
 *
 * @author Isak Karlsson
 */
public class DelimitedInputStream extends DataInputStream {

  private static final char DEFAULT_SEPARATOR = ',';
  private static final String DEFAULT_MISSING_VALUE = "?";
  private static final Map<String, VectorType> TYPE_MAP;

  static {
    Map<String, VectorType> map = new HashMap<>();
    map.put("complex", ComplexVector.TYPE);
    map.put("numeric", DoubleVector.TYPE);
    map.put("date", VectorType.getInstance(Date.class));
    map.put("regressor", DoubleVector.TYPE);
    map.put("class", StringVector.TYPE);
    map.put("categoric", StringVector.TYPE);

    TYPE_MAP = Collections.unmodifiableMap(map);
  }

  private final CsvParser parser;
  private final RdsRowProcessor processor;
  private String[] currentRow;

  /**
   * @param in the underlying input stream
   */
  public DelimitedInputStream(InputStream in, String missingValue, char separator) {
    super(in);
    CsvParserSettings settings = new CsvParserSettings();
    settings.setIgnoreLeadingWhitespaces(true);
    settings.setIgnoreTrailingWhitespaces(true);

    CsvFormat format = new CsvFormat();
    format.setDelimiter(DEFAULT_SEPARATOR);

    processor = new RdsRowProcessor();
    settings.setRowProcessor(processor);
    parser = new CsvParser(settings);
    parser.beginParsing(new InputStreamReader(in));
    parser.parseNext();
    parser.parseNext();

    currentRow = null;
  }

  /**
   * @param inputStream
   */
  public DelimitedInputStream(InputStream inputStream) {
    this(inputStream, DEFAULT_MISSING_VALUE, DEFAULT_SEPARATOR);
  }

  /**
   * Constructs a new buffered csv input stream from {@code file}
   *
   * @param file the file
   */
  public DelimitedInputStream(File file) throws FileNotFoundException {
    this(new BufferedInputStream(new FileInputStream(file)), DEFAULT_MISSING_VALUE,
         DEFAULT_SEPARATOR);
  }


  /**
   * @param fileName the file name
   */
  public DelimitedInputStream(String fileName) throws FileNotFoundException {
    this(new File(fileName));
  }

  @Override
  public void close() throws IOException {
    super.close();
    parser.stopParsing();
  }

  @Override
  protected VectorType readColumnType() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  protected String readColumnName() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public Collection<VectorType> readColumnTypes() throws IOException {
    return processor.columnTypes;
  }

  @Override
  public Collection<String> readColumnNames() throws IOException {
    return processor.columnNames;
  }

  @Override
  public DataEntry next() throws IOException {
    StringDataEntry entry = new StringDataEntry(currentRow, DEFAULT_MISSING_VALUE);
    currentRow = null;
    return entry;
  }

  @Override
  public boolean hasNext() throws IOException {
    if (currentRow == null) {
      currentRow = parser.parseNext();
    }

    return currentRow != null;
  }

  private static class RdsRowProcessor implements RowProcessor {

    private Collection<String> columnNames = null;

    private Collection<VectorType> columnTypes = null;

    @Override
    public void processStarted(ParsingContext context) {
    }

    @Override
    public void rowProcessed(String[] row, ParsingContext context) {
      long line = context.currentLine();
      if (line == 1) {
        columnTypes = new ArrayList<>();
        for (String col : row) {
          VectorType type = TYPE_MAP.get(col);
          if (type == null) {
            throw new ParseException(line, context.currentColumn());
          }
          columnTypes.add(type);
        }
      } else if (line == 2) {
        if (row.length != columnTypes.size()) {
          throw new ParseException(line, context.currentColumn());
        }
        columnNames = new ArrayList<>();
        Collections.addAll(columnNames, row);
      }
    }

    @Override
    public void processEnded(ParsingContext context) {
    }
  }
}
