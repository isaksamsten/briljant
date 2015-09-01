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

import org.briljantframework.index.VectorLocationSetter;
import org.briljantframework.io.DataEntry;

import java.io.IOException;

/**
 * Builder that infers the type of vector to build based on the first added value.
 *
 * Creates a new {@code Vector.Builder} which is able to infer the correct {@code Vector} to
 * return based on the first value added value.
 *
 * <p> For example, {@code new TypeInferenceVectorBuilder().add(1.0).build()} returns a {@code double}
 * vector. If unable to infer the type, e.g., when the first added value is {@code NA}, an {@code
 * object} vector is returned.
 */
public class TypeInferenceVectorBuilder implements Vector.Builder {

  private Vector.Builder builder;
  private VectorLocationSetter locationSetter = new InferringVectorLocationSetter();

  @Override
  public Vector.Builder setNA(Object key) {
    return null;
  }

  protected Vector.Builder getObjectBuilder() {
    if (builder == null) {
      this.builder = new GenericVector.Builder(Object.class);
    }
    return builder;
  }

  @Override
  public Vector.Builder addNA() {
    getObjectBuilder().addNA();
    return this;
  }

  @Override
  public Vector.Builder add(Vector from, int fromIndex) {
    return add(from.loc().get(Object.class, fromIndex));
  }

  @Override
  public Vector.Builder add(Vector from, Object key) {
    Object value = from.get(Object.class, key);
    if (builder == null) {
      builder = VectorType.from(value).newBuilder();
    }
    builder.loc().set(size(), value);
    return this;
  }

  @Override
  public Vector.Builder set(Object atKey, Vector from, int fromIndex) {
    if (builder == null) {
      Object value = from.loc().get(Object.class, fromIndex);
      builder = VectorType.from(value).newBuilder();
    }
    builder.set(atKey, from, fromIndex);
    return this;
  }

  @Override
  public Vector.Builder set(Object atKey, Vector from, Object fromKey) {
    if (builder == null) {
      Object value = from.get(Object.class, fromKey);
      builder = VectorType.from(value).newBuilder();
    }
    builder.set(atKey, from, fromKey);
    return this;
  }

  @Override
  public Vector.Builder set(Object key, Object value) {
    if (builder == null) {
      builder = VectorType.from(value).newBuilder();
    }
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
      Object value = from.loc().get(Object.class, 0);
      if (builder == null) {
        builder = VectorType.from(value).newBuilder();
      }
      builder.addAll(from);
    }
    return this;
  }

  @Override
  public Vector.Builder remove(Object key) {
    getObjectBuilder().remove(key);
    return this;
  }

  @Override
  public Vector.Builder read(DataEntry entry) throws IOException {
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
    return builder != null ? builder.size() : 0;
  }

  @Override
  public Vector getTemporaryVector() {
    return builder != null ? builder.getTemporaryVector() : Vector.empty();
  }

  @Override
  public Vector build() {
    return builder != null ? builder.build() : Vector.empty();
  }

  private class InferringVectorLocationSetter implements VectorLocationSetter {

    @Override
    public void setNA(int i) {
      getObjectBuilder().loc().setNA(i);
    }

    @Override
    public void set(int i, Object value) {
      if (builder == null) {
        builder = VectorType.from(value).newBuilder();
      }
      builder.loc().set(i, value);
    }

    @Override
    public void set(int t, Vector from, int f) {
      if (builder == null) {
        Object value = from.loc().get(Object.class, f);
        builder = VectorType.from(value).newBuilder();
      }
      builder.loc().set(t, from, f);
    }

    @Override
    public void set(int atIndex, Vector from, Object fromKey) {
      if (builder == null) {
        Object value = from.get(Object.class, fromKey);
        builder = VectorType.from(value).newBuilder();
      }
      builder.loc().set(atIndex, from, fromKey);
    }

    @Override
    public void read(int index, DataEntry entry) throws IOException {
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
        builder = VectorType.from(Double.class).newBuilder();
      }
      builder.loc().set(i, value);
    }

    @Override
    public void set(int i, int value) {
      if (builder == null) {
        builder = VectorType.from(Integer.class).newBuilder();
      }
      builder.loc().set(i, value);
    }
  }
}
