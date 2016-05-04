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
package org.briljantframework.data.parser;

import java.sql.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

import org.briljantframework.Check;
import org.briljantframework.data.dataframe.DataFrame;
import org.briljantframework.data.index.HashIndex;
import org.briljantframework.data.reader.EntryReaderException;
import org.briljantframework.data.reader.SqlEntryReader;
import org.briljantframework.data.series.Type;

/**
 * Parse a specified database using a given query.
 * 
 * @author Isak Karlsson
 */
public class SqlParser implements Parser {

  private final Settings settings = new Settings();
  private final Properties properties = new Properties();
  private String url;
  private String query;
  private List<Class<?>> types = null;
  private List<Object> header = null;
  private Map<String, Object> headerReMap = new HashMap<>();

  public SqlParser() {}

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
  public DataFrame parse(Supplier<? extends DataFrame.Builder> supplier) {
    Check.state(url != null, "No database provided");
    Check.state(query != null, "No query provided");

    try {
      Connection connection = DriverManager.getConnection(url, properties);
      PreparedStatement stmt = connection.prepareStatement(query);
      ResultSet resultSet = stmt.executeQuery();
      HashIndex.Builder index = new HashIndex.Builder();

      if (header != null) {
        header.forEach(index::add);
      } else {
        ResultSetMetaData metaData = resultSet.getMetaData();
        for (int i = 0; i < metaData.getColumnCount(); i++) {
          // index starts with 1
          String columnLabel = metaData.getColumnLabel(i + 1);
          Object remappedColumnLabel = headerReMap.get(columnLabel);
          if (remappedColumnLabel != null) {
            index.add(remappedColumnLabel);
          } else {
            index.add(columnLabel);
          }
        }
      }

      SqlEntryReader entryReader = new SqlEntryReader(resultSet);
      DataFrame.Builder builder = supplier.get();
      List<Class<?>> columnTypes;
      if (types != null) {
        columnTypes = types;
      } else {
        columnTypes = entryReader.getTypes();
      }
      columnTypes.stream().map(Type::of).forEach(builder::newColumn);
      builder.readAll(entryReader);
      DataFrame df = builder.build();
      df.setColumnIndex(index.build());
      return df;
    } catch (SQLException e) {
      throw new EntryReaderException(e);
    }
  }

  public class Settings {

    private Settings() {}

    /**
     * Set the path to the data base.
     * 
     * @param path the path
     */
    public void setPath(String path) {
      SqlParser.this.url = path;
    }

    public String getPath() {
      return SqlParser.this.url;
    }

    /**
     * Set the database query.
     * 
     * @param query the query
     * @return receiver modified
     */
    public void setQuery(String query) {
      SqlParser.this.query = query;
    }

    public String getQuery() {
      return SqlParser.this.query;
    }

    /**
     * Instead of using the column header given by the database query, use the specified header.
     * 
     * @param header the header
     * @return receiver modified
     */
    public void setHeader(List<Object> header) {
      SqlParser.this.header = header;
    }

    /**
     * A map for remapping column headers from the database to alternative headers.
     * 
     * @param map a map of database headers to alternative headers
     * @return receiver modified
     */
    public void setHeader(Map<String, Object> map) {
      headerReMap = map;
    }

    /**
     * Instead of using the column types specified by the database query, use the specified column
     * types.
     *
     * @param types the column types
     * @return receiver modified
     */
    public void setTypes(List<Class<?>> types) {
      SqlParser.this.types = types;
    }

    /**
     * Create a single remapping from a database column to a dataframe header.
     * 
     * @param column the database column
     * @param header the new header name
     * @return receiver modified
     */
    public void remap(String column, Object header) {
      headerReMap.put(column, header);
    }

    /**
     * Set a database property (the specific properties depend on the database driver found in the
     * database path). For example, MySQL requires
     * {@code a.setProperty("user", "username"); a.setProperty("password", "password");}
     * 
     * @param key the property key
     * @param value the property value
     * @return receiver modified
     */
    public void setProperty(Object key, Object value) {
      properties.put(key, value);
    }
  }
}
