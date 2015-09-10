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

package org.briljantframework.data.parser;

import org.briljantframework.Check;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.dataframe.ObjectIndex;
import org.briljantframework.data.reader.EntryReaderException;
import org.briljantframework.data.reader.SqlEntryReader;
import org.briljantframework.data.vector.VectorType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Properties;

/**
 * @author Isak Karlsson
 */
public class SqlParser extends Parser {

  private final Settings settings = new Settings();
  private String url;
  private String query;
  private final Properties properties = new Properties();

  public SqlParser() {
  }

  public SqlParser(String url) {
    this(url, null);
  }

  public SqlParser(String url, String query) {
    this.url = url;
    this.query = query;
  }

  public Settings getSettings() {
    return settings;
  }

  @Override
  public DataFrame parse() {
    Check.state(url != null, "No database provided");
    Check.state(query != null, "No query provided");

    try {
      Connection connection = DriverManager.getConnection(url);
      PreparedStatement stmt = connection.prepareStatement(query);
      ResultSet resultSet = stmt.executeQuery();
      ObjectIndex.Builder index = new ObjectIndex.Builder();

      ResultSetMetaData metaData = resultSet.getMetaData();
      for (int i = 0; i < metaData.getColumnCount(); i++) {
        index.add(metaData.getColumnLabel(i + 1)); // index starts with 1
      }

      SqlEntryReader entryReader = new SqlEntryReader(resultSet);
      DataFrame.Builder builder = getBuilderFactory().get();
      entryReader.getTypes().stream().map(VectorType::of).forEach(builder::add);
      builder.readAll(entryReader);
      builder.setColumnIndex(index.build());
      return builder.build();
    } catch (SQLException e) {
      throw new EntryReaderException(e);
    }
  }

  public class Settings {

    private Settings() {
    }

    public Settings setUrl(String url) {
      SqlParser.this.url = url;
      return this;
    }

    public Settings setQuery(String query) {
      SqlParser.this.query = query;
      return this;
    }

    public Settings set(Object key, Object value) {
      properties.put(key, value);
      return this;
    }
  }
}
