package org.briljantframework.vector;

import org.briljantframework.complex.Complex;
import org.briljantframework.exceptions.TypeConversionException;
import org.briljantframework.matrix.AnyMatrix;

/**
 * Created by isak on 1/21/15.
 */
public abstract class VectorView implements Vector {

    public static final String OVERRIDE_TO_SUPPORT = "Override to support";
    protected final Vector parent;

    protected VectorView(Vector parent) {
        this.parent = parent;
    }

    @Override
    public Value getAsValue(int index) {
        return parent.getAsValue(index);
    }

    @Override
    public String toString(int index) {
        return parent.toString(index);
    }

    @Override
    public boolean isTrue(int index) {
        return parent.isTrue(index);
    }

    @Override
    public boolean isNA(int index) {
        return parent.isNA(index);
    }

    @Override
    public boolean hasNA() {
        return parent.hasNA();
    }

    @Override
    public double getAsDouble(int index) {
        return parent.getAsDouble(index);
    }

    @Override
    public int getAsInt(int index) {
        return parent.getAsInt(index);
    }

    @Override
    public Bit getAsBit(int index) {
        return parent.getAsBit(index);
    }

    @Override
    public Complex getAsComplex(int index) {
        return parent.getAsComplex(index);
    }

    @Override
    public String getAsString(int index) {
        return parent.getAsString(index);
    }

    @Override
    public int size() {
        return parent.size();
    }

    @Override
    public VectorType getType() {
        return parent.getType();
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

    @Override
    public int[] toIntArray() {
        return parent.toIntArray();
    }

    @Override
    public int[] asIntArray() {
        return parent.asIntArray();
    }

    @Override
    public double[] toDoubleArray() {
        return parent.toDoubleArray();
    }

    @Override
    public double[] asDoubleArray() {
        return parent.asDoubleArray();
    }

    @Override
    public AnyMatrix asMatrix() throws TypeConversionException {
        return parent.asMatrix();
    }

    @Override
    public int compare(int a, int b) {
        return parent.compare(a, b);
    }

    @Override
    public int compare(int a, int b, Vector other) {
        return parent.compare(a, b, other);
    }

    @Override
    public int compare(int a, Value other) {
        return parent.compare(a, other);
    }
}
