/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
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

package org.briljantframework.classification.tune;

import java.util.function.BiConsumer;

import org.briljantframework.Check;

/**
 * Created by Isak Karlsson on 24/09/14.
 */
public final class Updaters {

  private Updaters() {}

  /**
   * Range updater.
   *
   * @param consumer the consumer
   * @param start the start
   * @param end the end
   * @param step the step
   * @return the updater
   */
  public static <T> ParameterUpdater<T> range(String name, BiConsumer<? super T, Integer> consumer,
      int start, int end, int step) {
    return new ParameterUpdater<T>() {
      private int current = start;

      @Override
      public String getParameter() {
        return name;
      }

      @Override
      public void restore() {
        current = start;
      }

      @Override
      public boolean hasUpdate() {
        return step > 0 ? current <= end : current >= end;
      }

      @Override
      public Object update(T toUpdate) {
        if (!hasUpdate()) {
          throw new IllegalStateException();
        }
        consumer.accept(toUpdate, current);
        current += step;

        return current - step;
      }
    };
  }

  /**
   * Range updater.
   *
   * @param consumer the consumer
   * @param start the start
   * @param end the end
   * @param step the step
   * @return the updater
   */
  public static <T> ParameterUpdater<T> range(String name, BiConsumer<? super T, Double> consumer,
      double start, double end, double step) {
    return new ParameterUpdater<T>() {
      private double current = start;

      @Override
      public String getParameter() {
        return name;
      }

      @Override
      public void restore() {
        current = start;
      }

      @Override
      public boolean hasUpdate() {
        return step > 0 ? current <= end : current >= end;
      }

      @Override
      public Object update(T toUpdate) {
        if (!hasUpdate()) {
          throw new IllegalStateException();
        }
        consumer.accept(toUpdate, current);
        current += step;

        return current - step;
      }
    };
  }

  @SafeVarargs
  public static <T, V> ParameterUpdater<T> enumeration(String name, BiConsumer<T, V> updater,
      V... enumeration) {
    Check.argument(enumeration.length > 0, "must enumerate value");
    return new EnumerationUpdater<>(name, updater, enumeration);
  }

}
