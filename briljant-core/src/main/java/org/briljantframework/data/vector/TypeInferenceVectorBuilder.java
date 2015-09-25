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

package org.briljantframework.data.vector;

import org.briljantframework.data.Is;
import org.briljantframework.data.index.VectorLocationSetter;
import org.briljantframework.data.reader.DataEntry;

import java.io.IOException;
import java.util.NoSuchElementException;

/**
 * Builder that infers the type of vector to build based on the first added value.
 *
 * Creates a new {@code Vector.Builder} which is able to infer the correct {@code Vector} to
 * return based on the first value added value.
 *
 * <p> For example, {@code new TypeInferenceVectorBuilder().add(1.0).build()} returns a {@code
 * double}
 * vector. The builder is unable to infer the correct type if the first call is {@link
 * #setNA(Object)}, {@link #read(DataEntry)} or {@link #readAll(DataEntry)} an {@link Object}
 * vector is returned.
 */
public final class TypeInferenceVectorBuilder implements Vector.Builder {

  private int noNaValues = 0;
  private Vector.Builder builder = null;
  private VectorLocationSetter locationSetter = new InferringVectorLocationSetter();

  @Override
  public Vector.Builder setNA(Object key) {
    getObjectBuilder().setNA(key);
    return this;
  }

  protected Vector.Builder getObjectBuilder() {
    if (builder == null) {
      initializeBuilder(VectorType.OBJECT);
    }
    return builder;
  }

  private void initializeBuilder(Object value) {
    if (builder == null) {
      if (Is.NA(value)) {
        noNaValues++;
      } else {
        builder = VectorType.of(value).newBuilder();
        for (int i = 0; i < noNaValues; i++) {
          builder.addNA();
        }
      }
    }
  }

  private void initializeBuilder(VectorType vectorType) {
    if (builder == null) {
      builder = vectorType.newBuilder();
      for (int i = 0; i < noNaValues; i++) {
        builder.addNA();
      }
    }
  }

  @Override
  public Vector.Builder addNA() {
    noNaValues++;
    return this;
  }

  @Override
  public Vector.Builder add(Vector from, int fromIndex) {
    initializeBuilder(from.getType());
    builder.add(from, fromIndex);
    return this;
  }

  @Override
  public Vector.Builder add(Vector from, Object key) {
    initializeBuilder(from.getType());
    builder.add(from, key);
    return this;
  }

  @Override
  public Vector.Builder set(Object atKey, Vector from, int fromIndex) {
    initializeBuilder(from.getType());
    builder.set(atKey, from, fromIndex);
    return this;
  }

  @Override
  public Vector.Builder set(Object atKey, Vector from, Object fromKey) {
    initializeBuilder(from.getType());
    builder.set(atKey, from, fromKey);
    return this;
  }

  @Override
  public Vector.Builder set(Object key, Object value) {
    initializeBuilder(value);
    builder.set(key, value);
    return this;
  }

  @Override
  public Vector.Builder add(Object value) {
    loc().set(size(), value);
    return this;
  }

  @Override
  public Vector.Builder add(double value) {
    loc().set(size(), value);
    return this;
  }

  @Override
  public Vector.Builder add(int value) {
    loc().set(size(), value);
    return this;
  }

  @Override
  public Vector.Builder addAll(Vector from) {
    if (from.size() > 0) {
      initializeBuilder(from.getType());
      builder.addAll(from);
    }
    return this;
  }

  @Override
  public Vector.Builder remove(Object key) {
    if (builder == null) {
      throw new NoSuchElementException(key + "");
    }
    builder.remove(key);
    return this;
  }

  @Override
  public Vector.Builder read(DataEntry entry) {
    getObjectBuilder().read(entry);
    return this;
  }

  @Override
  public Vector.Builder readAll(DataEntry entry) throws IOException {
    getObjectBuilder().readAll(entry);
    return this;
  }

  @Override
  public VectorLocationSetter loc() {
    return locationSetter;
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
  public Vector getTemporaryVector() {
    if (builder != null) {
      return builder.getTemporaryVector();
    } else {
      if (noNaValues == 0) {
        return Vector.of();
      } else {
        return Vector.singleton(null, noNaValues);
      }
    }
  }

  @Override
  public Vector build() {
    if (builder != null) {
      return builder.build();
    } else {
      if (noNaValues == 0) {
        return Vector.of();
      } else {
        return Vector.singleton(null, noNaValues);
      }
    }
  }

  private class InferringVectorLocationSetter implements VectorLocationSetter {

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
      initializeBuilder(value);
      builder.loc().set(i, value);
    }

    @Override
    public void set(int t, Vector from, int f) {
      initializeBuilder(from.getType());
      builder.loc().set(t, from, f);
    }

    @Override
    public void set(int atIndex, Vector from, Object fromKey) {
      initializeBuilder(from.getType());
      builder.loc().set(atIndex, from, fromKey);
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

    @Override
    public void set(int i, double value) {
      if (builder == null) {
        builder = VectorType.of(Double.class).newBuilder();
      }
      builder.loc().set(i, value);
    }

    @Override
    public void set(int i, int value) {
      if (builder == null) {
        builder = VectorType.of(Integer.class).newBuilder();
      }
      builder.loc().set(i, value);
    }
  }
}
