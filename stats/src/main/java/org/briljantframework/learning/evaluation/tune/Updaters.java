/*
 * ADEB - machine learning pipelines made easy Copyright (C) 2014 Isak Karlsson
 * 
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU General Public License as published by the Free Software Foundation; either version 2 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program; if
 * not, write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */

package org.briljantframework.learning.evaluation.tune;

import com.google.common.base.Preconditions;

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
  public static <T> Updater<T> range(String name, Updater.Update<? super T, Integer> consumer,
      int start, int end, int step) {
    return new Updater<T>() {
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
        consumer.update(toUpdate, current);
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
  public static <T> Updater<T> range(String name, Updater.Update<? super T, Double> consumer,
      double start, double end, double step) {
    return new Updater<T>() {
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
        consumer.update(toUpdate, current);
        current += step;

        return current - step;
      }
    };
  }

  /**
   * Options updater.
   *
   * @param <T> the type parameter
   * @param updater the option
   * @param enumeration the options
   * @return the updater
   */
  @SafeVarargs
  public static <T, V> Updater<T> enumeration(String name, Updater.Update<T, V> updater,
      V... enumeration) {
    Preconditions.checkArgument(enumeration.length > 0, "must enumerate value");
    return new Updater<T>() {
      private int current = 0;

      @Override
      public String getParameter() {
        return name;
      }

      @Override
      public void restore() {
        current = 0;
      }

      @Override
      public boolean hasUpdate() {
        return current < enumeration.length;
      }

      @Override
      public Object update(T toUpdate) {
        V value = enumeration[current++];
        updater.update(toUpdate, value);
        return value;
      }
    };
  }
}
