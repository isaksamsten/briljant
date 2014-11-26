package org.briljantframework.vector;

import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * A sequence is a vector which contains elements of different type.
 * <p>
 * Created by Isak Karlsson on 26/11/14.
 */
public interface Sequence extends Vector, Iterable<Vector> {

    public static final Binary NA = Binary.NA;

    public static final Type TYPE = new Type() {
        @Override
        public Builder newBuilder() {
            return new VariableVector.Builder();
        }

        @Override
        public Builder newBuilder(int size) {
            return new VariableVector.Builder(size);
        }


        @Override
        public Class<?> getDataClass() {
            return Object.class;
        }

        @Override
        public boolean isNA(Object value) {
            return value == null || value == Binary.NA;
        }

        @Override
        public int compare(int a, Vector va, int b, Vector ba) {
            throw new UnsupportedOperationException("Can't compare values for sequence types");
        }

        @Override
        public String toString() {
            return "sequence";
        }
    };

    /**
     * Get value at {@code index} as an object
     *
     * @param index the index
     * @return value as object or {@link org.briljantframework.vector.Sequence#NA}
     */
    Vector getAsVector(int index);

    /**
     * {@inheritDoc}
     */
    @Override
    default Type getType() {
        return TYPE;
    }

    /**
     * Get type of value at {@code index}
     *
     * @param index the index
     * @return the type of value
     */
    Type getType(int index);

    /**
     * Returns a sequential {@code Stream} with this collection as its source.
     * <p>
     * <p>This method should be overridden when the {@link #spliterator()}
     * method cannot return a spliterator that is {@code IMMUTABLE},
     * {@code CONCURRENT}, or <em>late-binding</em>. (See {@link #spliterator()}
     * for details.)
     * <p>
     * (comment from {@link java.util.Collection#stream()})
     *
     * @return a stream
     */
    default Stream<Vector> stream() {
        return StreamSupport.stream(spliterator(), false);
    }
}
