package org.briljantframework.array;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * Created by isak on 01/06/16.
 */
public final class Broadcast<E extends BaseArray<? extends E>> {

  private final E first;

  Broadcast(E first) {
    this.first = first;
  }

  public E to(int[] shape) {
    return Arrays.broadcastTo(first, shape);
  }

  public E to(BaseArray<?> other) {
    return Arrays.broadcastTo(first, other.getShape());
  }

  /**
   * Broadcast the argument to the shape of broadcast.
   *
   * @param second the array to broadcast to the shape of this broadcast
   * @param consumer the consumer, first argument is the original array and the second the given
   *        argument broadcast to the shape of the first
   * @param <S> the type of array to broadcast
   */
  public <S extends BaseArray<? extends S>> void with(S second,
      BiConsumer<? super E, ? super S> consumer) {
    consumer.accept(first, ShapeUtils.broadcastToShapeOf(second, first));
  }

  /**
   * Broadcast the argument and the current broadcaster to the same shape and apply the function.
   *
   * @param second the array to broadcast
   * @param function the function
   * @param <S> the type of array
   * @param <R> the type of return value
   * @return the result of applying the function
   */
  public <S extends BaseArray<? extends S>, R> R combine(S second,
      BiFunction<? super E, ? super S, R> function) {
    int[] newShape = ShapeUtils.findCombinedBroadcastShape(java.util.Arrays.asList(first, second));
    return function.apply(Arrays.broadcastTo(first, newShape),
        Arrays.broadcastTo(second, newShape));
  }
}
