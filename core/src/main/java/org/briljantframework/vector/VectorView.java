package org.briljantframework.vector;

import com.google.common.base.Preconditions;

import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.TypeConversionException;
import org.briljantframework.matrix.Matrix;

/**
 * Created by isak on 1/21/15.
 */
public abstract class VectorView extends AbstractVector {

  public static final String OVERRIDE_TO_SUPPORT = "Override to support";
  protected final Vector parent;
  protected final int offset, length;

  protected VectorView(Vector parent) {
    this(parent, 0, 1);
  }

  public VectorView(Vector parent, int offset, int length) {
    this.parent = Preconditions.checkNotNull(parent);
    this.offset = Preconditions.checkElementIndex(offset, parent.size());
    this.length = Preconditions.checkPositionIndex(offset + length, parent.size());
  }

  @Override
  public Value get(int index) {
    return parent.get(offset + index);
  }

  @Override
  public <T> T get(Class<T> cls, int index) {
    return parent.get(cls, offset + index);
  }

  @Override
  public String toString(int index) {
    return parent.toString(offset + index);
  }

  @Override
  public boolean isTrue(int index) {
    return parent.isTrue(offset + index);
  }

  @Override
  public boolean isNA(int index) {
    return parent.isNA(offset + index);
  }

  @Override
  public boolean hasNA() {
    return parent.hasNA();
  }

  @Override
  public double getAsDouble(int index) {
    return parent.getAsDouble(offset + index);
  }

  @Override
  public int getAsInt(int index) {
    return parent.getAsInt(offset + index);
  }

  @Override
  public Bit getAsBit(int index) {
    return parent.getAsBit(offset + index);
  }

  @Override
  public Complex getAsComplex(int index) {
    return parent.getAsComplex(offset + index);
  }

  @Override
  public String getAsString(int index) {
    return parent.getAsString(offset + index);
  }

  @Override
  public int size() {
    return length;
  }

  @Override
  public VectorType getType() {
    return parent.getType();
  }

  @Override
  public int[] toIntArray() {
    return parent.toIntArray();
  }

  @Override
  public double[] toDoubleArray() {
    return parent.toDoubleArray();
  }

  @Override
  public Matrix asMatrix() throws TypeConversionException {
    return parent.asMatrix();
  }

  @Override
  public int compare(int a, int b) {
    return parent.compare(a, b);
  }

  @Override
  public int compare(int a, Vector other, int b) {
    return parent.compare(a, other, b);
  }

  @Override
  public int compare(int a, Value other) {
    return parent.compare(a, other);
  }

  @Override
  public VectorType getType(int index) {
    return parent.getType(offset + index);
  }

  @Override
  public Builder newCopyBuilder() {
    throw new UnsupportedOperationException(OVERRIDE_TO_SUPPORT);
  }

  @Override
  public Builder newBuilder() {
    throw new UnsupportedOperationException(OVERRIDE_TO_SUPPORT);
  }

  @Override
  public Builder newBuilder(int size) {
    throw new UnsupportedOperationException(OVERRIDE_TO_SUPPORT);
  }
}
