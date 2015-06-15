package org.briljantframework.dataframe;

/**
 * @author Isak Karlsson
 */
class IxImpl implements Ix {

  private final Index rec, col;
  private final DataFrame df;

  IxImpl(DataFrame df) {
    this.rec = df.getRecordIndex();
    this.col = df.getColumnIndex();
    this.df = df;
  }

  @Override
  public <T> T get(Class<T> cls, Object r, int c) {
    return df.get(cls, rec.index(r), c);
  }

  @Override
  public <T> T get(Class<T> cls, int r, Object c) {
    return df.get(cls, r, col.index(c));
  }

  @Override
  public <T> T get(Class<T> cls, Object r, Object c) {
    return df.get(cls, r, c);
  }

  @Override
  public <T> T get(Class<T> cls, int r, int c) {
    return df.get(cls, r, c);
  }

  @Override
  public int getAsInt(Object r, int c) {
    return df.getAsInt(rec.index(r), c);
  }

  @Override
  public int getAsInt(int r, Object c) {
    return df.getAsInt(r, col.index(c));
  }

  @Override
  public int getAsInt(Object r, Object c) {
    return df.getAsInt(r, c);
  }

  @Override
  public int getAsInt(int r, int c) {
    return df.getAsInt(r, c);
  }

  @Override
  public double getAsDouble(Object r, int c) {
    return df.getAsDouble(rec.index(r), c);
  }

  @Override
  public double getAsDouble(int r, Object c) {
    return df.getAsDouble(r, col.index(c));
  }

  @Override
  public double getAsDouble(Object r, Object c) {
    return df.getAsDouble(r, c);
  }

  @Override
  public double getAsDouble(int r, int c) {
    return df.getAsDouble(r, c);
  }
}
