package org.briljantframework.array;

import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.DoubleToIntFunction;
import java.util.function.IntBinaryOperator;
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntToDoubleFunction;
import java.util.function.IntToLongFunction;
import java.util.function.IntUnaryOperator;
import java.util.function.LongToIntFunction;
import java.util.function.ToIntFunction;
import java.util.stream.IntStream;

import net.mintern.primitive.comparators.IntComparator;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.function.IntBiPredicate;
import org.briljantframework.function.ToIntObjIntBiFunction;

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */
public enum BasicIndex implements Range {
  ALL;

  @Override
  public int step() {
    return 1;
  }

  @Override
  public int start() {
    return 0;
  }

  @Override
  public int end() {
    throw unsupported();
  }

  @Override
  public boolean contains(int value) {
    throw unsupported();
  }

  private UnsupportedOperationException unsupported() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void assign(int value) {
    throw unsupported();
  }

  @Override
  public void assign(int[] data) {
    throw unsupported();
  }

  @Override
  public void assign(IntSupplier supplier) {
    throw unsupported();
  }

  @Override
  public void assign(IntArray matrix, IntUnaryOperator operator) {
    throw unsupported();
  }

  @Override
  public void assign(IntArray matrix, IntBinaryOperator combine) {
    throw unsupported();
  }

  @Override
  public void assign(ComplexArray matrix, ToIntFunction<? super Complex> function) {
    throw unsupported();
  }

  @Override
  public void assign(DoubleArray matrix, DoubleToIntFunction function) {
    throw unsupported();
  }

  @Override
  public void assign(LongArray matrix, LongToIntFunction operator) {
    throw unsupported();
  }

  @Override
  public void assign(BooleanArray matrix, ToIntObjIntBiFunction<Boolean> function) {
    throw unsupported();
  }

  @Override
  public void apply(IntUnaryOperator operator) {
    throw unsupported();
  }

  @Override
  public IntArray map(IntUnaryOperator operator) {
    throw unsupported();
  }

  @Override
  public LongArray mapToLong(IntToLongFunction function) {
    throw unsupported();
  }

  @Override
  public DoubleArray mapToDouble(IntToDoubleFunction function) {
    throw unsupported();
  }

  @Override
  public ComplexArray mapToComplex(IntFunction<Complex> function) {
    throw unsupported();
  }

  @Override
  public <U> Array<U> mapToObj(IntFunction<? extends U> function) {
    throw unsupported();
  }

  @Override
  public IntArray filter(IntPredicate operator) {
    throw unsupported();
  }

  @Override
  public BooleanArray where(IntPredicate predicate) {
    throw unsupported();
  }

  @Override
  public BooleanArray where(IntArray matrix, IntBiPredicate predicate) {
    throw unsupported();
  }

  @Override
  public void forEachPrimitive(IntConsumer consumer) {
    throw unsupported();
  }

  @Override
  public int reduce(int identity, IntBinaryOperator reduce) {
    throw unsupported();
  }

  @Override
  public int reduce(int identity, IntBinaryOperator reduce, IntUnaryOperator map) {
    throw unsupported();
  }

  @Override
  public IntArray reduceVectors(int dim, ToIntFunction<? super IntArray> accumulator) {
    throw unsupported();
  }

  @Override
  public void set(int index, int value) {
    throw unsupported();
  }

  @Override
  public int get(int i, int j) {
    throw unsupported();
  }

  @Override
  public void set(int row, int column, int value) {
    throw unsupported();
  }

  @Override
  public void set(int[] ix, int value) {
    throw unsupported();
  }

  @Override
  public int get(int... ix) {
    throw unsupported();
  }

  @Override
  public int get(int index) {
    throw unsupported();
  }

  @Override
  public void apply(int index, IntUnaryOperator operator) {
    throw unsupported();
  }

  @Override
  public void apply(int i, int j, IntUnaryOperator operator) {
    throw unsupported();
  }

  @Override
  public IntStream stream() {
    throw unsupported();
  }

  @Override
  public List<Integer> toList() {
    throw unsupported();
  }

  @Override
  public Array<Integer> boxed() {
    throw unsupported();
  }

  @Override
  public void sort() {
    throw unsupported();
  }

  @Override
  public void sort(IntComparator cmp) {
    throw unsupported();
  }

  @Override
  public IntArray times(IntArray other) {
    throw unsupported();
  }

  @Override
  public IntArray times(int alpha, IntArray other, int beta) {
    throw unsupported();
  }

  @Override
  public IntArray times(int scalar) {
    throw unsupported();
  }

  @Override
  public IntArray plus(IntArray other) {
    throw unsupported();
  }

  @Override
  public IntArray plus(int scalar) {
    throw unsupported();
  }

  @Override
  public void plusAssign(IntArray other) {
    throw unsupported();
  }

  @Override
  public void plusAssign(int scalar) {
    throw unsupported();
  }

  @Override
  public IntArray plus(int alpha, IntArray other) {
    throw unsupported();
  }

  @Override
  public IntArray minus(IntArray other) {
    throw unsupported();
  }

  @Override
  public IntArray minus(int scalar) {
    throw unsupported();
  }

  @Override
  public IntArray minus(int alpha, IntArray other) {
    throw unsupported();
  }

  @Override
  public void minusAssign(IntArray other) {
    throw unsupported();
  }

  @Override
  public void minusAssign(int scalar) {
    throw unsupported();
  }

  @Override
  public IntArray reverseMinus(int scalar) {
    throw unsupported();
  }

  @Override
  public void reverseMinusAssign(int scalar) {
    throw unsupported();
  }

  @Override
  public IntArray div(IntArray other) {
    throw unsupported();
  }

  @Override
  public IntArray div(int other) {
    throw unsupported();
  }

  @Override
  public void divAssign(IntArray other) {
    throw unsupported();
  }

  @Override
  public void divAssign(int other) {
    throw unsupported();
  }

  @Override
  public IntArray reverseDiv(int other) {
    throw unsupported();
  }

  @Override
  public void reverseDivAssign(int other) {
    throw unsupported();
  }

  @Override
  public IntArray negate() {
    throw unsupported();
  }

  @Override
  public int[] data() {
    throw unsupported();
  }

  @Override
  public void set(int toIndex, IntArray from, int fromIndex) {
    throw unsupported();
  }

  @Override
  public void set(int toRow, int toColumn, IntArray from, int fromRow, int fromColumn) {
    throw unsupported();
  }

  @Override
  public void set(int[] toIndex, IntArray from, int[] fromIndex) {
    throw unsupported();
  }

  @Override
  public void set(int[] toIndex, IntArray from, int fromIndex) {
    throw unsupported();
  }

  @Override
  public void set(int toIndex, IntArray from, int[] fromIndex) {
    throw unsupported();
  }

  @Override
  public IntArray reverse() {
    throw unsupported();
  }

  @Override
  public void assign(IntArray o) {
    throw unsupported();
  }

  @Override
  public void forEach(int dim, Consumer<IntArray> consumer) {
    throw unsupported();
  }

  @Override
  public void setColumn(int i, IntArray vec) {
    throw unsupported();
  }

  @Override
  public IntArray getColumn(int index) {
    throw unsupported();
  }

  @Override
  public void setRow(int i, IntArray vec) {
    throw unsupported();
  }

  @Override
  public IntArray getRow(int i) {
    throw unsupported();
  }

  @Override
  public IntArray reshape(int... shape) {
    throw unsupported();
  }

  @Override
  public IntArray ravel() {
    return this;
  }

  @Override
  public IntArray select(int index) {
    throw unsupported();
  }

  @Override
  public IntArray select(int dimension, int index) {
    throw unsupported();
  }

  @Override
  public IntArray getView(Range... indexers) {
    throw unsupported();
  }

  @Override
  public IntArray getView(List<? extends Range> ranges) {
    throw unsupported();
  }

  @Override
  public IntArray getVector(int dimension, int index) {
    throw unsupported();
  }

  @Override
  public void setVector(int dimension, int index, IntArray other) {
    throw unsupported();
  }

  @Override
  public IntArray getDiagonal() {
    throw unsupported();
  }

  @Override
  public IntArray get(IntArray... arrays) {
    throw unsupported();
  }

  @Override
  public IntArray get(List<? extends IntArray> arrays) {
    throw unsupported();
  }

  @Override
  public void set(List<? extends IntArray> arrays, IntArray slice) {
    throw unsupported();
  }

  @Override
  public IntArray getView(int rowOffset, int colOffset, int rows, int columns) {
    throw unsupported();
  }

  @Override
  public int size() {
    throw unsupported();
  }

  @Override
  public int size(int dim) {
    throw unsupported();
  }

  @Override
  public int vectors(int i) {
    throw unsupported();
  }

  @Override
  public int stride(int i) {
    throw unsupported();
  }

  @Override
  public int getOffset() {
    throw unsupported();
  }

  @Override
  public int[] getShape() {
    throw unsupported();
  }

  @Override
  public int[] getStride() {
    throw unsupported();
  }

  @Override
  public int getMajorStride() {
    throw unsupported();
  }

  @Override
  public int rows() {
    throw unsupported();
  }

  @Override
  public int columns() {
    throw unsupported();
  }

  @Override
  public int dims() {
    return 0;
  }

  @Override
  public boolean isVector() {
    return false;
  }

  @Override
  public boolean isMatrix() {
    return false;
  }

  @Override
  public IntArray asView(int[] shape, int[] stride) {
    throw unsupported();
  }

  @Override
  public IntArray asView(int offset, int[] shape, int[] stride) {
    throw unsupported();
  }

  @Override
  public IntArray asView(int offset, int[] shape, int[] stride, int majorStride) {
    throw unsupported();
  }

  @Override
  public IntArray newEmptyArray(int... shape) {
    throw unsupported();
  }

  @Override
  public boolean isView() {
    throw unsupported();
  }

  @Override
  public DoubleArray asDouble() {
    throw unsupported();
  }

  @Override
  public IntArray asInt() {
    throw unsupported();
  }

  @Override
  public LongArray asLong() {
    throw unsupported();
  }

  @Override
  public BooleanArray asBoolean() {
    throw unsupported();
  }

  @Override
  public ComplexArray asComplex() {
    throw unsupported();
  }

  @Override
  public boolean isContiguous() {
    throw unsupported();
  }

  @Override
  public IntArray transpose() {
    return this;
  }

  @Override
  public IntArray copy() {
    return this;
  }

  @Override
  public BooleanArray lt(IntArray other) {
    throw unsupported();
  }

  @Override
  public BooleanArray gt(IntArray other) {
    throw unsupported();
  }

  @Override
  public BooleanArray eq(IntArray other) {
    throw unsupported();
  }

  @Override
  public BooleanArray lte(IntArray other) {
    throw unsupported();
  }

  @Override
  public BooleanArray gte(IntArray other) {
    throw unsupported();
  }

  @Override
  public Iterator<Integer> iterator() {
    throw unsupported();
  }

  @Override
  public void swap(int a, int b) {
    throw unsupported();
  }
}
