package org.briljantframework.dataframe;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @param <T> the type of the input to the accumulator
 * @param <C> the mutable accumulator
 * @param <R> the return type of the reduction
 * @author Isak Karlsson
 */
public interface Aggregator<T, R, C> {

  Supplier<C> supplier();

  BiConsumer<C, T> accumulator();

  Function<C, R> finisher();

}
