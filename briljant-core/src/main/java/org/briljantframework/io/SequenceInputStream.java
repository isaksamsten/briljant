package org.briljantframework.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.NoSuchElementException;

import org.briljantframework.vector.VectorType;

/**
 * @author Isak Karlsson
 */
public class SequenceInputStream extends DataInputStream {

  private final BufferedReader reader;
  private String separator = ",";
  private String missingValue = "?";
  private int columns = -1;
  private int currentType = 0;
  private int currentName = 0;
  private String[] values = null;

  /**
   * @param in the underlying input stream
   */
  public SequenceInputStream(InputStream in) {
    super(in);
    this.reader = new BufferedReader(new InputStreamReader(in));

  }

  @Override
  public VectorType readColumnType() throws IOException {
    throw new UnsupportedOperationException("Variable data entry sizes");
  }

  @Override
  public String readColumnName() throws IOException {
    throw new UnsupportedOperationException("Variable data entry sizes");
  }

  @Override
  public DataEntry next() throws IOException {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    DataEntry entry = new StringDataEntry(values, missingValue);
    values = null;
    return entry;
  }

  @Override
  public void close() throws IOException {
    super.close();
    reader.close();
  }

  @Override
  public boolean hasNext() throws IOException {
    return initializeValues();
  }

  /**
   * @return true if successfully initialized and there are more values to consume
   * @throws IOException on failure
   */
  private boolean initializeValues() throws IOException {
    if (values == null) {
      String valueLine = reader.readLine();
      if (valueLine == null) {
        return false;
      }
      values = valueLine.trim().split(separator);
      if (columns == -1) {
        columns = values.length;
      }
    }
    return true;
  }
}
