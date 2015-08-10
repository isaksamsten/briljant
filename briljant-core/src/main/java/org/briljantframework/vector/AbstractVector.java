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

import org.briljantframework.Bj;
import org.briljantframework.Check;
import org.briljantframework.array.Array;
import org.briljantframework.dataframe.Index;
import org.briljantframework.dataframe.IntIndex;
import org.briljantframework.exceptions.IllegalTypeException;
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
  public <U> Array<U> asArray(Class<U> cls) throws IllegalTypeException {
    Array<U> n = Bj.referenceArray(size());
    for (int i = 0; i < size(); i++) {
      n.set(i, get(cls, i));
    }
    return n;
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
}
