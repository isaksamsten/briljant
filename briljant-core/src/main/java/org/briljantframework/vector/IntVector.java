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

package org.briljantframework.vector;

import com.carrotsearch.hppc.IntArrayList;

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.Utils;
import org.briljantframework.array.IntArray;
import org.briljantframework.exceptions.IllegalTypeException;
import org.briljantframework.index.Index;
import org.briljantframework.index.IntIndex;
import org.briljantframework.io.DataEntry;
import org.briljantframework.io.resolver.Resolver;
import org.briljantframework.io.resolver.Resolvers;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.IntStream;

/**
 * Created by Isak Karlsson on 20/11/14.
 */
public class IntVector extends AbstractVector {

  public static final VectorType TYPE = new VectorType() {
    @Override
    public Builder newBuilder() {
      return new Builder();
    }

    @Override
    public Builder newBuilder(int size) {
      return new Builder(size, size);
    }

    @Override
    public Class<?> getDataClass() {
      return Integer.class;
    }

    @Override
    public boolean isNA(Object value) {
      return value == null || (value instanceof Integer && (int) value == Na.INT);
    }

    @Override
    public int compare(int a, Vector va, int b, Vector ba) {
      int x = va.loc().getAsInt(a);
      int y = ba.loc().getAsInt(b);
      boolean aIsNa = Is.NA(x);
      boolean bIsNa = Is.NA(y);
      if (aIsNa && !bIsNa) {
        return -1;
      } else if (!aIsNa && bIsNa) {
        return 1;
      } else {
        return Integer.compare(x, y);
      }
//      return !va.isNA(a) && !ba.isNA(b) ? va.getAsInt(a) - ba.getAsInt(b) : 0;
    }

    @Override
    public Scale getScale() {
      return Scale.NUMERICAL;
    }

    @Override
    public String toString() {
      return "int";
    }
  };
  private final int[] values;
  private final int size;

  public IntVector(int... values) {
    this(values, values.length);
  }

  public IntVector(int[] values, int size) {
    this(values, size, true);
  }

  IntVector(int[] values, int size, boolean safe) {
    if (safe) {
      this.values = Arrays.copyOf(values, size);
    } else {
      this.values = values;
    }
    this.size = size;
  }

  private IntVector(int[] buffer, int size, Index index) {
    super(index);
    this.values = buffer;
    this.size = size;
  }

  public static Builder newBuilderWithInitialValues(int... values) {
    Builder builder = new Builder(0, values.length);
    for (int value : values) {
      builder.add(value);
    }
    return builder;
  }

  public static IntVector unsafe(int[] newLeftPool) {
    return new IntVector(newLeftPool, newLeftPool.length, false);
  }

  public static IntVector range(int end) {
    int[] v = new int[end];
    for (int i = 0; i < v.length; i++) {
      v[i] = i;
    }
    return new IntVector(v, v.length, false);
  }

  @Override
  public int getAsIntAt(int i) {
    return values[i];
  }

  @Override
  public <T> T getAt(Class<T> cls, int index) {
    Check.argument(!cls.isPrimitive(), "can't get primitive values");
    int v = getAsIntAt(index);
    return Convert.to(cls, v);
//    if (Is.NA(v)) {
//      return Na.from(cls);
//    }
//    if (cls.isAssignableFrom(Integer.class)) {
//      return cls.cast(v);
//    } else {
//      if (cls.isAssignableFrom(Double.class)) {
//        return cls.cast(getAsDouble(index));
//      } else if (cls.isAssignableFrom(Complex.class)) {
//        return cls.cast(getAsComplex(index));
//      } else if (cls.isAssignableFrom(Logical.class)) {
//        return cls.cast(getAsBit(index));
//      } else if (cls.isAssignableFrom(String.class)) {
//        return cls.cast(Integer.toString(v));
//      } else {
//        return Na.from(cls);
//      }
//    }
  }

  @Override
  public String toStringAt(int index) {
    int value = getAsIntAt(index);
    return value == Na.INT ? "NA" : String.valueOf(value);
  }

  @Override
  public boolean isNaAt(int index) {
    return getAsIntAt(index) == Na.INT;
  }

  @Override
  public double getAsDoubleAt(int i) {
    int value = getAsIntAt(i);
    return value == Na.INT ? Na.DOUBLE : value;
  }

  @Override
  public IntArray toIntArray() throws IllegalTypeException {
    return Bj.array(Arrays.copyOf(values, size()));
  }

  public Logical getAsBit(int index) {
    return Logical.valueOf(getAsIntAt(index));
  }

  @Override
  public VectorType getType() {
    return TYPE;
  }

  @Override
  public int compareAt(int a, Vector other, int b) {
    return getType().compare(a, this, b, other);
  }

  @Override
  public int hashCode() {
    int code = 1;
    for (int i = 0; i < size(); i++) {
      code += 31 * getAsIntAt(i);
    }
    return code;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o instanceof Vector) {
      Vector ov = (Vector) o;
      if (size() == ov.size()) {
        for (int i = 0; i < size(); i++) {
          if (getAsIntAt(i) != ov.loc().getAsInt(i)) {
            return false;
          }
        }
        return true;
      } else {
        return false;
      }
    } else {
      return false;
    }
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public Builder newBuilder() {
    return new Builder();
  }

  @Override
  public Builder newBuilder(int size) {
    return new Builder(size, size);
  }

  @Override
  public Builder newCopyBuilder() {
    return new Builder(Arrays.copyOf(values, size()), getIndex().newCopyBuilder());
  }

  @Override
  public IntStream intStream() {
    return Arrays.stream(values, 0, size());
  }

  public static final class Builder extends AbstractBuilder {

    private IntArrayList buffer;

    public Builder() {
      this(0, INITIAL_CAPACITY);
    }

    public Builder(int size) {
      this(0, size);
    }

    public Builder(int size, int capacity) {
      super(new IntIndex.Builder(size));
      buffer = new IntArrayList(Math.max(size, capacity));
      for (int i = 0; i < size; i++) {
        buffer.add(Na.INT);
      }
    }

    private Builder(int[] values) {
      super(new IntIndex.Builder(values.length));
      buffer = IntArrayList.from(values);
    }

    private Builder(int[] buffer, Index.Builder index) {
      super(index);
      this.buffer = IntArrayList.from(buffer);
    }

    @Override
    public void setNaAt(int index) {
      ensureCapacity(index);
      buffer.buffer[index] = Na.INT;
    }

    @Override
    protected void setAt(int atIndex, Vector from, Object fromKey) {
      setAt(atIndex, from.getAsInt(fromKey));
    }

    @Override
    protected void setAt(int index, Object value) {
      ensureCapacity(index);
      int dval = Na.INT;
      if (value instanceof Number) {
        dval = ((Number) value).intValue();
      } else if (value != null) {
        Resolver<Integer> resolver = Resolvers.find(Integer.class);
        if (resolver != null) {
          Integer resolve = resolver.resolve(value);
          if (resolve != null) {
            dval = resolve;
          }
        }
      }
      buffer.buffer[index] = dval;
    }

    @Override
    protected void setAt(int atIndex, Vector from, int fromIndex) {
      ensureCapacity(atIndex);
      buffer.buffer[atIndex] = from.loc().getAsInt(fromIndex);
    }

    protected void setAt(int index, int value) {
      ensureCapacity(index);
      buffer.buffer[index] = value;
    }

    @Override
    protected void removeAt(int index) {
      buffer.remove(index);
    }

    @Override
    public void swapAt(int a, int b) {
      Check.argument(a >= 0 && a < size() && b >= 0 && b < size());
      Utils.swap(buffer.buffer, a, b);
    }

    @Override
    public Vector.Builder read(int index, DataEntry entry) throws IOException {
      setAt(index, entry.nextInt());
      return this;
    }

    @Override
    public int size() {
      return buffer.size();
    }

    @Override
    public Vector getTemporaryVector() {
      return new IntVector(buffer.buffer, buffer.size(), false) {
        @Override
        public Builder newCopyBuilder() {
          return Builder.this;
        }
      };
    }

    @Override
    public IntVector build() {
      IntVector vector = new IntVector(buffer.buffer, buffer.size(), getIndex());
      buffer = null;
      return vector;
    }

    private void ensureCapacity(int index) {
      buffer.ensureCapacity(index + 1);
      // TODO: extend
      int i = buffer.size();
      while (i <= index) {
//        if (i < index) {
//          this.indexer.add(i);
//        }
        buffer.buffer[i++] = Na.INT;
        buffer.elementsCount++;
      }
    }
  }
}
