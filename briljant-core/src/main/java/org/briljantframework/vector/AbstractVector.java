package org.briljantframework.vector;

import com.google.common.base.Strings;

import org.briljantframework.Check;
import org.briljantframework.dataframe.Index;
import org.briljantframework.dataframe.IntIndex;
import org.briljantframework.function.Aggregator;

import java.util.Objects;
import java.util.function.BiFunction;

/**
 * @author Isak Karlsson
 */
public abstract class AbstractVector implements Vector {

  private Index index = null;

  protected AbstractVector(Index index) {
    this.index = index;
  }

  protected AbstractVector() {
    this.index = null;
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
  public <T, R> Vector combine(Class<? extends T> in, Class<? extends R> out, Vector other,
                               BiFunction<? super T, ? super T, ? extends R> combiner) {
    Vector.Builder builder = Vec.typeOf(out).newBuilder();
    return combineVectors(in, other, combiner, builder);
  }

  @Override
  public <T> Vector combine(Class<T> cls, Vector other, BiFunction<T, T, ? extends T> combiner) {
    return combineVectors(cls, other, combiner, newBuilder());
  }

  protected <T> Vector combineVectors(Class<? extends T> cls, Vector other,
                                      BiFunction<? super T, ? super T, ?> combiner,
                                      Builder builder) {
    int thisSize = this.size();
    int otherSize = other.size();
    int size = Math.max(thisSize, otherSize);
    for (int i = 0; i < size; i++) {
      if (i < thisSize && i < otherSize) {
        builder.add(combiner.apply(get(cls, i), other.get(cls, i)));
      } else {
        if (i < thisSize) {
          builder.add(get(cls, i));
        } else {
          builder.add(other.get(cls, i));
        }
      }
    }
    return builder.build();
  }

  @Override
  public Vector head(int n) {
    Vector.Builder b = newBuilder();
    for (int i = 0; i < n && i < size(); i++) {
      b.add(this, i);
    }
    return b.build();
  }

  @Override
  public Vector tail(int n) {
    Vector.Builder b = newBuilder();
    for (int i = size() - n; i < size(); i++) {
      b.add(this, i);
    }
    return b.build();
  }

  @Override
  public final Index getIndex() {
    if (index == null) {
      index = new IntIndex(size());
    }
    return index;
  }

  @Override
  public final void setIndex(Index index) {
    Objects.requireNonNull(index);
    Check.size(size(), index.size());
    this.index = index;
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
    builder.append("] type: ").append(getType().toString());
    return builder.toString();
  }
//  @Override
//  public String toString() {
//    StringBuilder builder = new StringBuilder();
//    int longest = getIndex().keySet().stream()
//        .map(Object::toString)
//        .mapToInt(String::length)
//        .max()
//        .orElse(0);
//
//    Index index = getIndex();
//    for (int i = 0; i < size(); i++) {
//      String key = index.get(i).toString();
//      builder.append(key)
//          .append(Strings.repeat(" ", longest - key.length() + 2))
//          .append(toString(i)).append("\n");
//    }
//    return builder
//        .append("Name: ").append(name())
//        .append(" type: ").append(getType())
//        .toString();
//  }
}
