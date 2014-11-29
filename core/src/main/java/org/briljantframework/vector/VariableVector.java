package org.briljantframework.vector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.briljantframework.io.DataFrameInputStream;

import com.google.common.collect.UnmodifiableIterator;

/**
 * Created by Isak Karlsson on 26/11/14.
 */
public class VariableVector implements CompoundVector {

  private final List<? extends Value> values;

  /**
   * Constructs a {@code VariableVector}
   *
   * @param values the values
   */
  public VariableVector(List<? extends Value> values) {
    this.values = values;
  }

  @Override
  public double getAsDouble(int index) {
    return values.get(index).getAsDouble(0);
  }

  @Override
  public int getAsInt(int index) {
    return values.get(index).getAsInt(0);
  }

  @Override
  public Binary getAsBinary(int index) {
    return values.get(index).getAsBinary(0);
  }

  @Override
  public String getAsString(int index) {
    return values.get(index).getAsString(0);
  }

  @Override
  public Value getAsValue(int index) {
    return values.get(index);
  }

  @Override
  public Complex getAsComplex(int index) {
    return values.get(index).getAsComplex(0);
  }

  @Override
  public String toString(int index) {
    return values.get(index).toString(0);
  }

  @Override
  public boolean isNA(int index) {
    return values.get(index).isNA(0);
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

  @Override
  public int compare(int a, int b) {
    return getAsValue(a).compareTo(getAsValue(b));
  }

  @Override
  public int compare(int a, int b, Vector other) {
    return getAsValue(a).compareTo(other.getAsValue(b));
  }

  @Override
  public Type getType(int index) {
    return values.get(index).getType();
  }

  @Override
  public Iterator<Value> iterator() {
    return new UnmodifiableIterator<Value>() {
      public int current = 0;

      @Override
      public boolean hasNext() {
        return current < size();
      }

      @Override
      public Value next() {
        return getAsValue(current++);
      }
    };
  }

  public static class Builder implements Vector.Builder {
    private List<Value> buffer;


    private Builder(List<Value> buffer) {
      this.buffer = buffer;
    }

    public Builder() {
      this(0, INITIAL_CAPACITY);
    }

    public Builder(int size) {
      this(size, size);
    }

    public Builder(int size, int capacity) {
      buffer = new ArrayList<>(Math.max(size, capacity));
      for (int i = 0; i < size; i++) {
        buffer.add(Undefined.INSTANCE);
      }
    }

    @Override
    public Builder setNA(int index) {
      ensureCapacity(index);
      buffer.set(index, Undefined.INSTANCE);
      return this;
    }

    @Override
    public Builder addNA() {
      return setNA(size());
    }

    @Override
    public Builder add(Vector from, int fromIndex) {
      set(size(), from, fromIndex);
      return this;
    }

    @Override
    public Builder set(int atIndex, Vector from, int fromIndex) {
      ensureCapacity(atIndex);
      buffer.set(atIndex, from.getAsValue(fromIndex));
      return this;
    }

    @Override
    public Builder set(int index, Object obj) {
      Value value;
      if (obj instanceof Value) {
        value = (Value) obj;
      } else if (obj instanceof Integer || obj instanceof Byte || obj instanceof Short) {
        value = new IntValue(((Number) obj).intValue());
      } else if (obj instanceof Float || obj instanceof Double) {
        value = new DoubleValue(((Number) obj).doubleValue());
      } else if (obj instanceof Complex) {
        value = new ComplexValue((Complex) obj);
      } else if (obj instanceof Binary) {
        value = new BinaryValue((Binary) obj);
      } else if (obj != null) {
        value = new StringValue(obj.toString());
      } else {
        value = Undefined.INSTANCE;
      }
      ensureCapacity(index);
      buffer.set(index, value);
      return this;
    }

    @Override
    public Builder add(Object value) {
      set(size(), value);
      return this;
    }

    @Override
    public Builder addAll(Vector from) {
      for (int i = 0; i < from.size(); i++) {
        buffer.add(from.getAsValue(i));
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
    public VariableVector create() {
      return new VariableVector(buffer);
    }

    private void ensureCapacity(int index) {
      while (buffer.size() <= index) {
        buffer.add(Undefined.INSTANCE);
      }
    }
  }
}
