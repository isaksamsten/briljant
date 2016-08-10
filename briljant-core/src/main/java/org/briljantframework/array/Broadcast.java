/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.array;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * @author Isak Karlsson
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
