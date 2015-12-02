/**
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
package org.briljantframework.data.parser;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.briljantframework.Check;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.index.ObjectIndex;
import org.briljantframework.data.reader.CsvEntryReader;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.vector.VectorType;

import com.univocity.parsers.csv.CsvParserSettings;

/**
 * @author Isak Karlsson
 */
public class CsvParser implements Parser {

  private final Settings settings = new Settings();
  private final CsvParserSettings csvSettings;

  private int skipRows = -1;
  private String missingValue = "?";
  private List<Object> header = null;
  private List<VectorType> types = null;
  private Reader reader;

  public CsvParser() {
    this(null);
  }

  public CsvParser(Reader reader) {
    this.csvSettings = new CsvParserSettings();
    this.reader = reader;
  }

  public void set(Consumer<Settings> consumer) {
    consumer.accept(getSettings());
  }

  public Settings getSettings() {
    return settings;
  }

  @Override
  public DataFrame parse(Supplier<? extends DataFrame.Builder> supplier) {
    Check.state(reader != null, "No source file provided.");
    CsvEntryReader entryReader = new CsvEntryReader(csvSettings, reader, missingValue);
    for (int i = 0; i < skipRows && entryReader.hasNext(); i++) {
      entryReader.next();
    }

    DataFrame.Builder builder = supplier.get();
    Index.Builder columnIndex = new ObjectIndex.Builder();

    // The first row after skipRows are used as header if there are no
    // headers already set,
    if (entryReader.hasNext() && header == null) {
      DataEntry entry = entryReader.next();
      while (entry.hasNext()) {
        columnIndex.add(entry.nextString());
      }
    } else if (header != null) {
      header.forEach(columnIndex::add);
    }

    // If no types are set, use the entry reader to infer the types
    if (types == null) {
      for (Class<?> type : entryReader.getTypes()) {
        builder.add(VectorType.of(type));
      }
    } else {
      types.forEach(builder::add);
    }
    return builder.readAll(entryReader).setColumnIndex(columnIndex.build()).build();
  }

  /**
   * Modify the settings of the CsvParser
   */
  public class Settings {

    private Settings() {}

    public Settings setFile(File file) throws FileNotFoundException {
      reader = new FileReader(file);
      return this;
    }

    public Settings setFileName(String fileName) throws FileNotFoundException {
      reader = new FileReader(new File(fileName));
      return this;
    }

    public Settings setUrl(URL url) throws IOException {
      return setInputStream(url.openStream());
    }

    public Settings setInputStream(InputStream inputStream) {
      reader = new InputStreamReader(inputStream);
      return this;
    }

    /**
     * Set the value which should be interpreted as {@code NA} (default: '?')
     *
     * @param naValue the new na value
     * @return this
     */
    public Settings setNaValue(String naValue) {
      missingValue = naValue;
      return this;
    }

    /**
     * Set the header of the returned data frame. If the csv-file has a header, use
     * {@link #setSkipRows(int)} (default: null). If {@code null} is supplied, the header is the
     * first non-skipped row of the file.
     *
     * @param header the new header (if {@code header.size() < numberOfColumns}, the resulting data
     *        frame will have some headers numbered from {@code header.size()} until
     *        {@code numberOfColumns})
     * @return this
     */
    public Settings setHeader(List<Object> header) {
      CsvParser.this.header = new ArrayList<>(header);
      return this;
    }

    /**
     * Set the number of initial rows to skip (default: 0)
     *
     * @param skipRows the number of rows to skip
     * @return this
     */
    public Settings setSkipRows(int skipRows) {
      CsvParser.this.skipRows = skipRows;
      return this;
    }

    /**
     * Set the column types (default: null). If {@code null}, the types are inferred.
     *
     * @param types the column types or {@code null}
     * @return this
     */
    public Settings setTypes(List<Class> types) {
      CsvParser.this.types = types.stream().map(VectorType::of).collect(Collectors.toList());
      return this;
    }

    /**
     * @see #setTypes(java.util.List)
     */
    public Settings setVectorTypes(List<VectorType> types) {
      CsvParser.this.types = new ArrayList<>(types);
      return this;
    }

    /**
     * Set the value delimiter (default: ',')
     *
     * @param delimiter the value delimiter
     * @return this
     */
    public Settings setDelimiter(char delimiter) {
      csvSettings.getFormat().setDelimiter(delimiter);
      return this;
    }

    /**
     * Set the comment character (default: '#') (commented rows will be skipped)
     *
     * @param comment the comment character
     * @return this
     */
    public Settings setComment(char comment) {
      csvSettings.getFormat().setComment(comment);
      return this;
    }

    /**
     * Set the value quote character (default '"')
     *
     * @param quote the quote character
     * @return this
     */
    public Settings setQuote(char quote) {
      csvSettings.getFormat().setQuote(quote);
      return this;
    }

    /**
     * Skip empty lines (i.e. ignore them)
     *
     * @param skipEmptyLines true to skip
     * @return this
     */
    public Settings setSkipEmptyLines(boolean skipEmptyLines) {
      csvSettings.setSkipEmptyLines(skipEmptyLines);
      return this;
    }

    /**
     * Accept un-escaped quotes inside values (if true accept them; if false raises
     * {@link com.univocity.parsers.common.TextParsingException} if encountered during parsing)
     *
     * @param parseUnescapedQuotes boolean
     * @return this
     */
    public Settings setParseUnescapedQuotes(boolean parseUnescapedQuotes) {
      csvSettings.setParseUnescapedQuotes(parseUnescapedQuotes);
      return this;
    }

  }
}
