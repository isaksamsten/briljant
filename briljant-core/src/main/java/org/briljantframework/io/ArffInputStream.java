package org.briljantframework.io;

import com.google.common.collect.ImmutableMap;

import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.Vec;
import org.briljantframework.vector.VectorType;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Isak Karlsson
 */
public class ArffInputStream extends DataInputStream {

  private static final String INVALID_TYPE = "Can't understand type %s";
  private static final Map<String, VectorType> TYPE_MAP;

  private static Pattern RELATION = Pattern.compile("@relation\\s+.*$", Pattern.CASE_INSENSITIVE);
  private static Pattern ATTRIBUTE = Pattern.compile("@attribute\\s+([a-zA-Z0-9]+)\\s+(.+)$",
                                                     Pattern.CASE_INSENSITIVE);
  private static Pattern DATA = Pattern.compile("@data\\s*$", Pattern.CASE_INSENSITIVE);
  private static Pattern NOMINAL = Pattern.compile("\\{([a-zA-Z0-9]+,?\\s?)+\\}",
                                                   Pattern.CASE_INSENSITIVE);

  static {
    TYPE_MAP = ImmutableMap.of("real", DoubleVector.TYPE, "numeric", DoubleVector.TYPE);
  }

  private BufferedReader reader;
  private List<Object> columnNames = null;
  private List<VectorType> columnTypes = null;
  private String currentLine = null;

  /**
   * @param in the underlying input stream
   */
  public ArffInputStream(InputStream in) {
    super(in);
    reader = new BufferedReader(new InputStreamReader(in));
  }

  @Override
  public void close() throws IOException {
    super.close();
    reader.close();
  }

  private void initialize() throws IOException {
    if (columnNames != null) {
      return;
    }
    columnNames = new ArrayList<>();
    columnTypes = new ArrayList<>();

    while ((currentLine = reader.readLine()) != null
           && (RELATION.matcher(currentLine).matches() || currentLine.trim().equals(""))) {

    }
    Matcher attr;
    while (currentLine != null && (attr = ATTRIBUTE.matcher(currentLine)).matches()) {
      String name = attr.group(1);
      String typeRepr = attr.group(2).trim().toLowerCase();
      VectorType type = TYPE_MAP.get(typeRepr);
      columnNames.add(name);
      if (type != null) {
        columnTypes.add(type);
      } else if ((NOMINAL.matcher(typeRepr)).matches()) {
        columnTypes.add(Vec.typeOf(String.class));
      } else {
        throw new IllegalArgumentException(String.format(INVALID_TYPE, typeRepr));
      }
      currentLine = reader.readLine();
    }

    while (currentLine != null
           && (currentLine.trim().isEmpty() || DATA.matcher(currentLine).matches())) {
      currentLine = reader.readLine();
    }
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
    initialize();
    return columnTypes;
  }

  @Override
  public Collection<Object> readColumnIndex() throws IOException {
    initialize();
    return columnNames;
  }

  @Override
  public DataEntry next() throws IOException {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    StringDataEntry entry = new StringDataEntry(currentLine.split(","));
    currentLine = null;
    return entry;
  }

  @Override
  public boolean hasNext() throws IOException {
    initialize();
    if (currentLine == null || DATA.matcher(currentLine).matches()) {
      currentLine = reader.readLine();
    }
    while (currentLine != null && currentLine.trim().equals("")) {
      currentLine = reader.readLine();
    }
    if (currentLine == null) {
      return false;
    }
    return true;
  }
}
