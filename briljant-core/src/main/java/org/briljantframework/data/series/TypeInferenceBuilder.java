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
package org.briljantframework.data.series;

import org.briljantframework.data.Is;
import org.briljantframework.data.reader.DataEntry;

/**
 * Builder that infers the type of series to build based on the first added value. Creates a new
 * {@code Series.Builder} which is able to infer the correct {@code Series} to return based on the
 * first value added value.
 *
 * <p/>
 * For example, {@code new TypeInferenceBuilder().plus(1.0).build()} returns a {@code double}
 * series. The builder is unable to infer the correct type if the first call is
 * {@link #setNA(Object)}, {@link #read(DataEntry)} or {@link Series.Builder#readAll(DataEntry)} an
 * {@link Object} series is returned.
 */
public final class TypeInferenceBuilder implements Series.Builder {

  /**
   * Recommended initial capacity.
   */
  public static final int INITIAL_CAPACITY = 5;
  private int noNaValues = 0;
  private Series.Builder builder = null;
  private LocationSetter locationSetter = new InferringLocationSetter();

  @Override
  public Series.Builder addNA() {
    noNaValues++;
    return this;
  }

  @Override
  public Series.Builder setNA(Object key) {
    getObjectBuilder().setNA(key);
    return this;
  }

  private Series.Builder getObjectBuilder() {
    if (builder == null) {
      initBuilderFromType(Types.OBJECT);
    }
    return builder;
  }

  private void initBuilderFromType(Type type) {
    if (builder == null) {
      builder = type.newBuilder();
      for (int i = 0; i < noNaValues; i++) {
        builder.addNA();
      }
    }
  }

  @Override
  public Series.Builder addFrom(Series from, Object key) {
    initBuilderFromType(from.getType());
    builder.addFrom(from, key);
    return this;
  }

  @Override
  public Series.Builder addFromLocation(Series from, int pos) {
    initBuilderFromType(from.getType());
    if (builder != null) {
      builder.addFromLocation(from, pos);
    }
    return this;
  }

  @Override
  public Series.Builder setFromLocation(Object atKey, Series from, int fromIndex) {
    initBuilderFromType(from.getType());
    if (builder != null) {
      builder.setFromLocation(atKey, from, fromIndex);
    }
    return this;
  }

  @Override
  public Series.Builder setFrom(Object atKey, Series from, Object fromKey) {
    initBuilderFromType(from.getType());
    if (builder != null) {
      builder.setFrom(atKey, from, fromKey);
    }
    return this;
  }

  @Override
  public Series.Builder set(Object key, Object value) {
    initBuilderFromObject(value);
    if (builder != null) {
      builder.set(key, value);
    }
    return this;
  }

  @Override
  public Series.Builder setInt(Object key, int value) {
    initBuilderFromType(Types.INT);
    builder.setInt(key, value);
    return this;
  }

  @Override
  public Series.Builder setDouble(Object key, double value) {
    initBuilderFromType(Types.DOUBLE);
    builder.setDouble(key, value);
    return this;
  }

  /**
   * Initializes the builder if value is non-<tt>NA</tt>.
   *
   * Always perform null check on the builder after the call to this method.
   * 
   * @param value the element
   */
  private void initBuilderFromObject(Object value) {
    if (builder == null) {
      if (Is.NA(value)) {
        noNaValues++;
      } else {
        builder = Types.inferFrom(value).newBuilder();
        for (int i = 0; i < noNaValues; i++) {
          builder.addNA();
        }
      }
    }
  }

  @Override
  public Series.Builder add(Object value) {
    loc().set(size(), value);
    return this;
  }

  @Override
  public Series.Builder addDouble(double value) {
    loc().setDouble(size(), value);
    return this;
  }

  @Override
  public Series.Builder addInt(int value) {
    loc().setInt(size(), value);
    return this;
  }

  @Override
  public Series.Builder setAll(Series from) {
    if (from.size() > 0) {
      initBuilderFromType(from.getType());
      if (builder != null) {
        builder.setAll(from);
      }
    }
    return this;
  }

  @Override
  public Series.Builder readAll(DataEntry entry) {
    getObjectBuilder().readAll(entry);
    return this;
  }

  @Override
  public LocationSetter loc() {
    return locationSetter;
  }

  @Override
  public Series.Builder read(DataEntry entry) {
    getObjectBuilder().read(entry);
    return this;
  }

  @Override
  public int size() {
    if (builder != null) {
      return builder.size();
    } else {
      return noNaValues;
    }
  }

  @Override
  public Series build() {
    if (builder != null) {
      return builder.build();
    } else {
      if (noNaValues == 0) {
        return Series.of();
      } else {
        return Series.repeat(null, noNaValues);
      }
    }
  }

  private class InferringLocationSetter implements LocationSetter {

    @Override
    public void setNA(int i) {
      if (builder == null) {
        if (i > noNaValues) {
          noNaValues = i;
        }
      } else {
        builder.loc().setNA(i);
      }
    }

    @Override
    public void set(int i, Object value) {
      initBuilderFromObject(value);
      if (builder != null) {
        builder.loc().set(i, value);
      }
    }

    @Override
    public void setDouble(int i, double value) {
      if (builder == null) {
        builder = Types.from(Double.class).newBuilder();
      }
      builder.loc().setDouble(i, value);
    }

    @Override
    public void setInt(int i, int value) {
      if (builder == null) {
        builder = Types.from(Integer.class).newBuilder();
      }
      builder.loc().setInt(i, value);
    }

    @Override
    public void setFrom(int t, Series from, int f) {
      initBuilderFromType(from.getType());
      if (builder != null) {
        builder.loc().setFrom(t, from, f);
      }
    }

    @Override
    public void setFromKey(int atIndex, Series from, Object fromKey) {
      initBuilderFromType(from.getType());
      if (builder != null) {
        builder.loc().setFromKey(atIndex, from, fromKey);
      }
    }

    @Override
    public void read(int index, DataEntry entry) {
      getObjectBuilder().loc().read(index, entry);
    }

    @Override
    public void remove(int i) {
      getObjectBuilder().loc().remove(i);
    }

    @Override
    public void swap(int a, int b) {
      getObjectBuilder().loc().swap(a, b);
    }
  }
}
