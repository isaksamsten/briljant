package org.briljantframework.vector;

import java.io.Serializable;

/**
 * Created by Isak Karlsson on 20/11/14.
 */
public interface Vector extends Serializable {

    double getAsDouble(int index);

    int getAsInteger(int index);

    Binary getAsBinary(int index);

    String getAsString(int index);

    default boolean isTrue(int index) {
        return getAsBinary(index) == Binary.TRUE;
    }

    boolean isNA(int index);

    default boolean hasNA() {
        for (int i = 0; i < size(); i++) {
            if (isNA(i)) {
                return true;
            }
        }
        return false;
    }

    int compare(int a, int b);

    int size();

    Type getType();

    Builder newCopyBuilder();

    Builder newBuilder();

    Builder newBuilder(int size);

    public static interface Builder {


        int INITIAL_CAPACITY = 50;

        Builder addNA(int index);

        Builder addNA();

        Builder add(Vector from, int fromIndex);

        Builder add(int atIndex, Vector from, int fromIndex);

        Builder add(int index, Object value);

        Builder add(Object value);

        int size();

        Vector create();
    }


    public static interface Type {

        Builder newBuilder();

        Builder newBuilder(int size);

        Class<?> getDataClass();

        int compare(int a, Vector va, int b, Vector ba);

        default boolean equals(int a, Vector va, int b, Vector ba) {
            return compare(a, va, b, ba) == 0;
        }
    }
}
