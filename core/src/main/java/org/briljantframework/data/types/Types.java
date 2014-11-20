package org.briljantframework.data.types;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Created by Isak Karlsson on 23/10/14.
 */
public final class Types implements Iterable<Type> {

    private final List<Type> types;

    /**
     * Instantiates a new Types.
     *
     * @param types the types
     */
    public Types(Iterable<? extends Type> types) {
        this.types = clone(types);
    }

    /**
     * Instantiates a new Types.
     *
     * @param types the types
     */
    protected Types(List<Type> types) {
        this.types = types;
    }

    /**
     * Clone list.
     *
     * @param types the types
     * @return the list
     */
    public static List<Type> clone(Iterable<? extends Type> types) {
        return clone(types, DefaultTypeFactory.INSTANCE);
    }

    /**
     * Range list.
     *
     * @param f      the supplier
     * @param length the length
     * @return the list
     */
    public static Types range(Function<String, Type> f, int length) {
        return new Types(
                IntStream.range(0, length)
                        .mapToObj(index -> f.apply(String.valueOf(index)))
                        .collect(Collectors.toCollection(ArrayList::new))
        );
    }

    /**
     * Clone list.
     *
     * @param types   the headers
     * @param factory the factory
     * @return the list
     */
    public static List<Type> clone(Iterable<? extends Type> types, TypeFactory factory) {
        List<Type> copy = new ArrayList<>();
        for (Type type : types) {
            copy.add(factory.create(type.getName(), type.getDataType()));
        }

        return copy;
    }

    /**
     * Without copying.
     *
     * @param types the types
     * @return the types
     */
    public static Types withoutCopying(List<Type> types) {
        return new Types(types);
    }

    /**
     * Get type.
     *
     * @param index the index
     * @return the type
     */
    public Type get(int index) {
        return types.get(index);
    }

    /**
     * Size int.
     *
     * @return the int
     */
    public int size() {
        return types.size();
    }

    /**
     * Add builder.
     *
     * @param type the type
     * @return the builder
     */
    public Builder add(Type type) {
        return new Builder(types).add(type);
    }

    /**
     * Builder builder.
     *
     * @return the builder
     */
    public Builder builder() {
        return new Builder(types);
    }

    @Override
    public Iterator<Type> iterator() {
        return types.iterator();
    }

    /**
     * The type Builder.
     */
    public static class Builder {
        private final List<Type> types;

        /**
         * Instantiates a new Builder.
         *
         * @param types the types
         */
        public Builder(List<Type> types) {
            this.types = new ArrayList<>(types);
        }

        /**
         * Add builder.
         *
         * @param type the type
         * @return the builder
         */
        public Builder add(Type type) {
            types.add(type);
            return this;
        }

        /**
         * Add builder.
         *
         * @param index the index
         * @param type  the type
         * @return the builder
         */
        public Builder add(int index, Type type) {
            types.add(index, type);
            return this;
        }

        /**
         * Remove builder.
         *
         * @param index the index
         * @return the builder
         */
        public Builder remove(int index) {
            types.remove(index);
            return this;
        }

        /**
         * Create types.
         *
         * @return the types
         */
        public Types create() {
            return new Types((Iterable<Type>) types);
        }
    }
}
