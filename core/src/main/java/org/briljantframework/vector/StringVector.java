package org.briljantframework.vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.briljantframework.io.DataFrameInputStream;

import com.google.common.collect.UnmodifiableIterator;

/**
 * A StringVector contains string values or NA.
 * <p>
 * TODO(isak): It might be wasteful to store equal objects multiple times. Consider having a
 * subclass CompressedObjectVector or similar. TODO(isak): The CompressedStringVector requires
 * StringVector to be abstract Created by Isak Karlsson on 20/11/14.
 */
public class StringVector extends AbstractStringVector {

  private final List<String> values;

  protected StringVector(List<String> values, boolean copy) {
    if (copy) {
      this.values = new ArrayList<>(values);
    } else {
      this.values = values;
    }
  }

  public StringVector(List<String> values) {
    this(values, true);
  }

  public StringVector(String... values) {
    this(Arrays.asList(values), false);
  }

  public static Builder newBuilderWithInitialValues(Object... values) {
    Builder builder = new Builder(0, values.length);
    builder.addAll(Arrays.asList(values));
    return builder;
  }

  @Override
  public Iterator<String> iterator() {
    return new UnmodifiableIterator<String>() {
      private int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public String next() {
        return getAsString(current++);
      }
    };
  }

  @Override
  public String getAsString(int index) {
    return values.get(index);
  }

  @Override
  public int size() {
    return values.size();
  }

  @Override
  public Builder newCopyBuilder() {
    return new Builder(new ArrayList<>(values));
  }

  @Override
  public Builder newBuilder() {
    return new Builder();
  }

  @Override
  public Builder newBuilder(int size) {
    return new Builder(size);
  }

  public static class Builder implements Vector.Builder {

    private ArrayList<String> buffer;

    public Builder() {
      this(0, INITIAL_CAPACITY);
    }

    public Builder(int size) {
      this(size, size);
    }

    public Builder(int size, int capacity) {
      buffer = new ArrayList<>(Math.max(size, capacity));
      for (int i = 0; i < size; i++) {
        buffer.add(StringVector.NA);
      }
    }

    public Builder(ArrayList<String> buffer) {
      this.buffer = buffer;
    }

    @Override
    public Builder setNA(int index) {
      ensureCapacity(index);
      buffer.set(index, StringVector.NA);
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
      buffer.set(atIndex, from.getAsString(fromIndex));
      return this;
    }

    @Override
    public Builder set(int index, Object value) {
      ensureCapacity(index);

      // NOTE: value == StringVector.NA is a null-check
      if (value == StringVector.NA || value.equals("?") || value.equals("NA")) {
        buffer.set(index, StringVector.NA);
      } else {
        buffer.set(index, value.toString());
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
        add(from.getAsString(i));
      }
      return this;
    }

    @Override
    public void read(DataFrameInputStream inputStream) throws IOException {
      add(inputStream.nextString());
    }

    @Override
    public int size() {
      return buffer.size();
    }

    @Override
    public StringVector create() {
      return new StringVector(buffer, false);
    }

    private void ensureCapacity(int index) {
      while (buffer.size() <= index) {
        buffer.add(StringVector.NA);
      }
    }
  }
}
