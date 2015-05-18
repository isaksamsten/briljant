package org.briljantframework.dataframe;

import org.briljantframework.complex.Complex;
import org.briljantframework.function.Aggregator;
import org.briljantframework.matrix.Matrix;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;

import java.util.function.BiFunction;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author Isak Karlsson
 */
public abstract class VectorDelegate implements Vector {

  private final Vector vector;

  public VectorDelegate(Vector vector) {
    this.vector = vector;
  }

  @Override
  public <T> Vector satisfies(Class<T> cls, Vector other,
                              BiPredicate<T, T> predicate) {
    return vector.satisfies(cls, other, predicate);
  }

  @Override
  public <T> Vector satisfies(Class<? extends T> cls, Predicate<? super T> predicate) {
    return vector.satisfies(cls, predicate);
  }

  @Override
  public <T, O> Vector transform(Class<T> in, Class<O> out, Function<T, O> operator) {
    return vector.transform(in, out, operator);
  }

  @Override
  public <T> Vector transform(Class<T> cls, Function<T, ?> operator) {
    return vector.transform(cls, operator);
  }

  @Override
  public <T> Vector filter(Class<T> cls, Predicate<T> predicate) {
    return vector.filter(cls, predicate);
  }

  @Override
  public <T, R, C> R aggregate(Class<? extends T> in,
                               Aggregator<? super T, ? extends R, C> aggregator) {
    return vector.aggregate(in, aggregator);
  }

  @Override
  public <T, R> Vector combine(Class<? extends T> in, Class<? extends R> out, Vector other,
                               BiFunction<? super T, ? super T, ? extends R> combiner) {
    return vector.combine(in, out, other, combiner);
  }

  @Override
  public <T> Vector combine(Class<? extends T> cls, Vector other,
                            BiFunction<? super T, ? super T, ?> combiner) {
    return vector.combine(cls, other, combiner);
  }

  public VectorType getType() {
    return vector.getType();
  }

  public <T> T get(Class<T> cls, int index) {
    return vector.get(cls, index);
  }

  public String toString(int index) {
    return vector.toString(index);
  }

  public boolean isNA(int index) {
    return vector.isNA(index);
  }

  public double getAsDouble(int index) {
    return vector.getAsDouble(index);
  }

  public int getAsInt(int index) {
    return vector.getAsInt(index);
  }

  public Bit getAsBit(int index) {
    return vector.getAsBit(index);
  }

  public Complex getAsComplex(int index) {
    return vector.getAsComplex(index);
  }

  public int size() {
    return vector.size();
  }

  public VectorType getType(int index) {
    return vector.getType();
  }

  public Vector.Builder newCopyBuilder() {
    return vector.newCopyBuilder();
  }

  public Vector.Builder newBuilder() {
    return vector.newBuilder();
  }

  public Vector.Builder newBuilder(int size) {
    return vector.newBuilder(size);
  }

  public Matrix toMatrix() {
    return vector.toMatrix();
  }

  public int compare(int a, int b) {
    return vector.compare(a, b);
  }

  public int compare(int a, Vector other, int b) {
    return vector.compare(a, other, b);
  }
}
