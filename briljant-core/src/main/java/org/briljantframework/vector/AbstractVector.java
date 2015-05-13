package org.briljantframework.vector;

import org.briljantframework.dataframe.Aggregator;

import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.UnaryOperator;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractVector implements Vector {

  @Override
  public <T, O> Vector transform(Class<T> in, Class<O> out, Function<T, O> operator) {
    Vector.Builder builder = Vec.typeOf(out).newBuilder();
    for (int i = 0; i < size(); i++) {
      builder.set(i, operator.apply(get(in, i)));
    }
    return builder.build();
  }

  @Override
  public <T> Vector transform(Class<T> cls, UnaryOperator<T> operator) {
    return transform(cls, cls, operator);
  }

  @Override
  public <T> Vector filter(Class<T> cls, Predicate<T> predicate) {
    Vector.Builder builder = Vec.typeOf(cls).newBuilder();
    for (int i = 0; i < size(); i++) {
      T value = get(cls, i);
      if (predicate.test(value)) {
        builder.add(value);
      }
    }
    return builder.build();
  }


  @Override
  public <T, R, C> R aggregate(Class<? extends T> in,
                               Aggregator<? super T, ? extends R, C> aggregator) {
    C accumulator = aggregator.supplier().get();
    for (int i = 0; i < size(); i++) {
      aggregator.accumulator().accept(accumulator, get(in, i));
    }
    return aggregator.finisher().apply(accumulator);
  }

  @Override
  public <T> Vector combine(Class<? extends T> cls, Vector other, BinaryOperator<T> combiner) {
    Vector.Builder builder = Vec.typeOf(cls).newBuilder();
    int size = Math.max(this.size(), other.size());
    int thisSize = size();
    int otherSize = other.size();
    for (int i = 0; i < size; i++) {
      if (i < thisSize && i < otherSize) {
        builder.add(combiner.apply(get(cls, i), other.get(cls, i)));
      } else if (i < thisSize) {
        builder.add(get(cls, i));
      } else {
        builder.add(other.get(cls, i));
      }
    }
    return builder.build();
  }

  @Override
  public VectorType getType(int index) {
    return getType();
  }

  @Override
  public Builder newCopyBuilder() {
    Builder builder = newBuilder(size());
    for (int i = 0; i < size(); i++) {
      builder.set(i, this, i);
    }
    return builder;
  }

  @Override
  public Builder newBuilder() {
    return getType().newBuilder();
  }

  @Override
  public Builder newBuilder(int size) {
    return getType().newBuilder(size);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder("[");
    builder.append(toString(0));
    for (int i = 1; i < size(); i++) {
      builder.append(", ").append(toString(i));
    }
    builder.append(" type: ").append(getType().toString());
    builder.append("]");
    return builder.toString();
  }
}
