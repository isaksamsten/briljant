package org.briljantframework.vector;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

import org.briljantframework.Utils;
import org.briljantframework.io.DataEntry;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.base.Preconditions;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Created by Isak Karlsson on 20/11/14.
 */
public class BinaryVector extends AbstractBinaryVector {

  private int[] values;

  protected BinaryVector(IntArrayList values) {
    this.values = values.toArray();
  }

  public BinaryVector(boolean... values) {
    this.values = new int[values.length];
    for (int i = 0; i < values.length; i++) {
      this.values[i] = values[i] ? 1 : 0;
    }
  }

  public BinaryVector(int... values) {
    this.values = Arrays.copyOf(values, values.length);
  }

  public static Builder newBuilderWithInitialValues(Object... values) {
    Builder builder = new Builder(0, values.length);
    builder.addAll(Arrays.asList(values));
    return builder;
  }

  @Override
  public Iterator<Binary> iterator() {
    return new UnmodifiableIterator<Binary>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public Binary next() {
        return getAsBinary(current++);
      }
    };
  }

  @Override
  public int getAsInt(int index) {
    return values[index];
  }

  @Override
  public int size() {
    return values.length;
  }

  @Override
  public Builder newCopyBuilder() {
    return new Builder(toIntArray());
  }

  @Override
  public Builder newBuilder() {
    return new Builder();
  }

  @Override
  public Builder newBuilder(int size) {
    return new Builder(size);
  }

  public int[] toIntArray() {
    return values.clone();
  }

  public int[] asIntArray() {
    return values;
  }

  public static class Builder implements Vector.Builder {

    private IntArrayList buffer;

    public Builder() {
      this(0);
    }

    public Builder(int size) {
      this(size, Math.max(INITIAL_CAPACITY, size));
    }

    public Builder(int size, int capacity) {
      buffer = new IntArrayList(capacity);
      for (int i = 0; i < size; i++) {
        buffer.add(IntVector.NA);
      }
    }

    public Builder(int[] ints) {
      this.buffer = IntArrayList.from(ints);
    }

    @Override
    public Builder setNA(int index) {
      ensureCapacity(index);
      buffer.buffer[index] = IntVector.NA;
      return this;
    }

    @Override
    public Builder addNA() {
      return setNA(size());
    }

    @Override
    public Builder add(Vector from, int fromIndex) {
      return set(size(), from, fromIndex);
    }

    @Override
    public Builder set(int atIndex, Vector from, int fromIndex) {
      ensureCapacity(atIndex);
      buffer.buffer[atIndex] = from.getAsInt(fromIndex);
      return this;
    }

    @Override
    public Builder set(int index, Object value) {
      ensureCapacity(index);
      int intValue = IntVector.NA;
      if (value instanceof Number) {
        intValue = ((Number) value).intValue();
      } else if (value instanceof Binary) {
        intValue = ((Binary) value).asInt();
      }
      buffer.buffer[index] = intValue;
      return this;
    }

    @Override
    public Builder add(Object value) {
      return set(size(), value);
    }

    @Override
    public Builder addAll(Vector from) {
      for (int i = 0; i < from.size(); i++) {
        add(from.getAsBinary(i));
      }
      return this;
    }

    @Override
    public Builder remove(int index) {
      // ArrayBuffers.remove(buffer.buffer, index);
      buffer.remove(index);
      return this;
    }

    @Override
    public int compare(int a, int b) {
      return Integer.compare(buffer.get(a), buffer.get(b));
    }

    @Override
    public void swap(int a, int b) {
      Preconditions.checkArgument(a >= 0 && a < size() && b >= 0 && b < size());
      Utils.swap(buffer.buffer, a, b);
    }

    @Override
    public Vector.Builder read(DataEntry entry) throws IOException {
      return read(size(), entry);
    }

    @Override
    public Vector.Builder read(int index, DataEntry entry) throws IOException {
      Binary binary = entry.nextBinary();
      if (binary == null) {
        setNA(index);
      } else {
        set(index, binary);
      }
      return this;
    }

    @Override
    public int size() {
      return buffer.size();
    }

    @Override
    public VectorLike temporaryVector() {
      return new VectorLike() {

        @Override
        public double getAsDouble(int index) {
          return getAsInt(index);
        }

        @Override
        public int getAsInt(int index) {
          return buffer.get(index);
        }

        @Override
        public int size() {
          return buffer.size();
        }
      };
    }

    @Override
    public BinaryVector build() {
      BinaryVector vector = new BinaryVector(buffer);
      buffer = null;
      return vector;
    }

    public Builder add(Binary binary) {
      return add(binary.asInt());
    }

    public Builder add(int value) {
      ensureCapacity(size());
      buffer.buffer[size()] = value;
      return this;
    }

    private void ensureCapacity(int index) {
      buffer.ensureCapacity(index + 1);
      int i = buffer.size();
      while (i <= index) {
        buffer.buffer[i++] = IntVector.NA;
        buffer.elementsCount++;
      }
    }
  }
}
