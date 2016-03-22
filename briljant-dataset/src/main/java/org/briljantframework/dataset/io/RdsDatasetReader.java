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
package org.briljantframework.dataset.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.reader.StringDataEntry;
import org.briljantframework.data.vector.Type;

import com.univocity.parsers.common.ParsingContext;
import com.univocity.parsers.common.processor.RowProcessor;
import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

/**
 * Reads values from a typed CSV-file similar to those used in Rule Discovery System (RDS).
 *
 * The file-format is simple, a comma separated file with the first two rows being the column names
 * and the types respectively.
 *
 * <p>The types are simple strings and are mapped to {@code briljant} data types as follows:
 *
 * <ul>
 * <li>{@code numeric} and {@code regressor}: {@link org.briljantframework.data.vector.DoubleVector}</li>
 * <li>{@code categoric} and {@code class}: {@link org.briljantframework.data.vector.GenericVector}</li>
 * <li>{@code date}: {@link org.briljantframework.data.vector.GenericVector}</li>
 * </ul>
 *
 * By convention, missing values are represented as {@code ?}.
 *
 * @author Isak Karlsson
 */
@Deprecated
public class RdsDatasetReader extends DatasetReader {

  private static final char DEFAULT_SEPARATOR = ',';
  private static final String DEFAULT_MISSING_VALUE = "?";
  private static final Map<String, Type> TYPE_MAP;

  static {
    Map<String, Type> map = new HashMap<>();
    map.put("numeric", Type.DOUBLE);
    map.put("date", Type.of(LocalDate.class));
    map.put("regressor", Type.DOUBLE);
    map.put("class", Type.of(String.class));
    map.put("categoric", Type.of(String.class));

    TYPE_MAP = Collections.unmodifiableMap(map);
  }

  private final CsvParser parser;
  private final RdsRowProcessor processor = new RdsRowProcessor();
  private final String missingValue;
  private String[] currentRow;

  /**
   * @param in the underlying input stream
   */
  public RdsDatasetReader(InputStream in, String missingValue, char separator) {
    super(in);
    CsvParserSettings settings = new CsvParserSettings();
    settings.setIgnoreLeadingWhitespaces(true);
    settings.setIgnoreTrailingWhitespaces(true);
    settings.getFormat().setDelimiter(separator);
    settings.setRowProcessor(processor);
    parser = new CsvParser(settings);
    parser.beginParsing(new InputStreamReader(in));
    parser.parseNext();
    parser.parseNext();
    this.missingValue = missingValue;

    currentRow = null;
  }

  /**
   * @param inputStream
   */
  public RdsDatasetReader(InputStream inputStream) {
    this(inputStream, DEFAULT_MISSING_VALUE, DEFAULT_SEPARATOR);
  }

  /**
   * Constructs a new buffered csv input stream from {@code file}
   *
   * @param file the file
   */
  public RdsDatasetReader(File file) throws FileNotFoundException {
    this(new BufferedInputStream(new FileInputStream(file)), DEFAULT_MISSING_VALUE,
         DEFAULT_SEPARATOR);
  }


  /**
   * @param fileName the file name
   */
  public RdsDatasetReader(String fileName) throws FileNotFoundException {
    this(new File(fileName));
  }

  @Override
  public void close() throws IOException {
    super.close();
    parser.stopParsing();
  }

  @Override
  protected Type readColumnType() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  protected Object readColumnName() throws IOException {
    throw new UnsupportedOperationException();
  }

  @Override
  public List<Type> readColumnTypes() throws IOException {
    return processor.columnTypes;
  }

  @Override
  public Collection<Object> readColumnIndex() throws IOException {
    return processor.columnNames;
  }

  @Override
  public List<Class<?>> getTypes() {
    throw new UnsupportedOperationException();
  }

  @Override
  public DataEntry next() {
    StringDataEntry entry = new StringDataEntry(currentRow, missingValue);
    currentRow = null;
    return entry;
  }

  @Override
  public boolean hasNext() {
    if (currentRow == null) {
      currentRow = parser.parseNext();
    }

    return currentRow != null;
  }

  private static class RdsRowProcessor implements RowProcessor {

    private List<Object> columnNames = null;

    private List<Type> columnTypes = null;

    @Override
    public void processStarted(ParsingContext context) {
    }

    @Override
    public void rowProcessed(String[] row, ParsingContext context) {
      long line = context.currentLine();
      if (line == 1) {
        columnTypes = new ArrayList<>();
        for (String col : row) {
          Type type = TYPE_MAP.get(col);
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
