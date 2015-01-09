package org.briljantframework.vector;

import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.briljantframework.Utils;
import org.briljantframework.io.DataEntry;

import com.carrotsearch.hppc.IntArrayList;
import com.google.common.base.Preconditions;
import com.google.common.collect.UnmodifiableIterator;

/**
 * Created by Isak Karlsson on 20/11/14.
 */
public class IntVector extends AbstractIntVector {

  private final int[] values;

  public IntVector(int... values) {
    this(values, values.length);
  }

  public IntVector(int[] values, int size) {
    this(values, size, true);
  }

  IntVector(int[] values, int size, boolean unsafe) {
    if (unsafe) {
      this.values = Arrays.copyOf(values, size);
    } else {
      this.values = values;
    }
  }

  public static Vector.Builder newBuilderWithInitialValues(int... values) {
    Builder builder = new Builder(0, values.length);
    for (int value : values) {
      builder.add(value);
    }
    return builder;
  }

  public static IntVector unsafe(int[] newLeftPool) {
    return new IntVector(newLeftPool, newLeftPool.length, false);
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
    return new Builder(size, size);
  }

  public int[] toIntArray() {
    return values.clone();
  }

  public int[] asIntArray() {
    return values;
  }

  @Override
  public Iterator<Integer> iterator() {
    return new UnmodifiableIterator<Integer>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public Integer next() {
        return getAsInt(current++);
      }
    };
  }

  @Override
  public String toString() {
    return IntStream.of(values).mapToObj(x -> x == NA ? "NA" : String.valueOf(x))
        .collect(Collectors.joining(","));
  }

  public static final class Builder implements Vector.Builder {

    private IntArrayList buffer;

    public Builder() {
      this(0, INITIAL_CAPACITY);
    }

    public Builder(int size) {
      this(0, size);
    }

    public Builder(int size, int capacity) {
      buffer = new IntArrayList(Math.max(size, capacity));
      for (int i = 0; i < size; i++) {
        buffer.add(NA);
      }
    }

    Builder(int[] values) {
      buffer = IntArrayList.from(values);
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
      if (value instanceof Number) {
        ensureCapacity(index);
        int intValue = ((Number) value).intValue();
        buffer.buffer[index] = intValue;
      } else {
        setNA(index);
      }

      return this;
    }

    @Override
    public Builder add(Object value) {
      return set(size(), value);
    }

    @Override
    public Builder addAll(Vector from) {
      for (int i = 0; i < from.size(); i++) {
        add(from.getAsInt(i));
      }

      return this;
    }

    @Override
    public Vector.Builder remove(int index) {
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
      set(index, entry.nextInt());
      return this;
    }

    @Override
    public int size() {
      return buffer.size();
    }

    @Override
    public VectorLike getVectorView() {
      return null;
    }

    @Override
    public IntVector build() {
      IntVector vector = new IntVector(buffer.buffer, buffer.size());
      buffer = null;
      return vector;
    }

    public Builder add(int value) {
      return set(size(), value);
    }

    public Builder set(int index, int value) {
      ensureCapacity(index);
      buffer.buffer[index] = value;
      return this;
    }

    private void ensureCapacity(int index) {
      buffer.ensureCapacity(index + 1);
      int i = buffer.size();
      while (i <= index) {
        buffer.buffer[i++] = NA;
        buffer.elementsCount++;
      }
    }
  }


}
