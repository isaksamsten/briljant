package org.briljantframework.dataframe;

import org.briljantframework.complex.Complex;

/**
 * @author Isak Karlsson
 */
public class Indexer {

  private final Index rec, col;
  private final DataFrame df;

  public Indexer(DataFrame df) {
    this.rec = df.getRecordIndex();
    this.col = df.getColumnIndex();
    this.df = df;
  }

  public String getAsString(Object r, int c) {
    return df.getAsString(rec.get(r), c);
  }

  public String getAsString(int r, Object c) {
    return df.getAsString(r, col.get(c));
  }

  public String getAsString(Object r, Object c) {
    return df.getAsString(r, c);
  }

  public String getAsString(int r, int c) {
    return df.getAsString(r, c);
  }

  public int getAsInt(Object r, int c) {
    return df.getAsInt(rec.get(r), c);
  }

  public int getAsInt(int r, Object c) {
    return df.getAsInt(r, col.get(c));
  }

  public int getAsInt(Object r, Object c) {
    return df.getAsInt(r, c);
  }

  public int getAsInt(int r, int c) {
    return df.getAsInt(r, c);
  }

  public double getAsDouble(Object r, int c) {
    return df.getAsDouble(rec.get(r), c);
  }

  public double getAsDouble(int r, Object c) {
    return df.getAsDouble(r, col.get(c));
  }

  public double getAsDouble(Object r, Object c) {
    return df.getAsDouble(r, c);
  }

  public double getAsDouble(int r, int c) {
    return df.getAsDouble(r, c);
  }

  public Complex getAsComplex(Object r, int c) {
    return df.getAsComplex(rec.get(r), c);
  }

  public Complex getAsComplex(int r, Object c) {
    return df.getAsComplex(r, col.get(c));
  }

  public Complex getAsComplex(Object r, Object c) {
    return df.getAsComplex(r, c);
  }

  public Complex getAsComplex(int r, int c) {
    return df.getAsComplex(r, c);
  }
}
