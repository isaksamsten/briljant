package org.briljantframework.data.values;

import org.briljantframework.data.types.DataType;

/**
 * Created by Isak Karlsson on 11/11/14.
 */
public class Factor implements Value {

    private final int value;

    private Factor(int value) {
        this.value = value;
    }

    /**
     * Value of.
     *
     * @param value the value
     * @return the factor
     */
    public static Factor valueOf(int value) {
        return new Factor(value);
    }

    @Override
    public DataType getDataType() {
        return DataType.FACTOR;
    }

    @Override
    public Object value() {
        return value;
    }

    @Override
    public double asDouble() {
        return value;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Factor) {
            return ((Factor) obj).value == value;
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(Value o) {
        if (o instanceof Factor) {
            return Integer.compare(value, ((Factor) o).value);
        } else {
            return -1;
        }
    }

    @Override
    public String repr() {
        return Integer.toString(value);
    }

    @Override
    public String toString() {
        return repr();
    }
}
