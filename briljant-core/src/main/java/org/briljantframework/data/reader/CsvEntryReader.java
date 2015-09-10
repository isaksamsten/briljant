/*
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

package org.briljantframework.data.reader;

import com.univocity.parsers.csv.CsvParser;
import com.univocity.parsers.csv.CsvParserSettings;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;

/**
 * Reads a CSV-file and produces one {@linkplain DataEntry data entry} for each line in the file.
 *
 * @author Isak Karlsson
 * @see org.briljantframework.data.parser.CsvParser Read a data frame from a Csv-file
 */
public class CsvEntryReader implements EntryReader {

  private final CsvParser csvParser;
  private final String missingValue;
  private String[] current = null;

  public CsvEntryReader(CsvParserSettings settings, Reader reader, String missingValue) {
    csvParser = new CsvParser(settings);
    csvParser.beginParsing(new BufferedReader(reader));
    this.missingValue = missingValue;
  }

  @Override
  public DataEntry next() throws IOException {
    if (current == null) {
      current = csvParser.parseNext();
    }
    if (current == null) {
      throw new NoSuchElementException();
    }
    DataEntry entry = new StringDataEntry(current, missingValue);
    current = null;
    return entry;
  }

  @Override
  public boolean hasNext() throws IOException {
    if (current == null) {
      current = csvParser.parseNext();
    }
    return current != null;
  }
}
