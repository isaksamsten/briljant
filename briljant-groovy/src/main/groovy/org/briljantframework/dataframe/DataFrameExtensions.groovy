/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.dataframe

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.briljantframework.function.Aggregator
import org.briljantframework.vector.Vector

import java.util.function.BiConsumer
import java.util.function.Function
import java.util.function.Supplier

import static org.briljantframework.function.Aggregates.*

/**
 * Created by isak on 04/06/15.
 */
@CompileStatic
class DataFrameExtensions {

  static <T> T get(DataFrame self, Object r, Object c) {
    return self.get(T, r, c)
  }

  static <T> T getAt(DataFrame self, int r, int c) {
    return self.get(T, r, c)
  }

  static Vector getAt(DataFrame self, int i) {
    return self.getRecord(i)
  }

  static <T> T getAt(DataFrame self, Object r, Object c) {
    return self.get(T, r, c)
  }

  @CompileStatic(TypeCheckingMode.SKIP)
  static Vector agg(DataFrame self, Aggregator aggregator) {
    def safeAggregator = new Aggregator() {

      @Override
      Supplier supplier() {
        return aggregator.supplier()
      }

      @Override
      BiConsumer accumulator() {
        return {a, b ->
          try {
            aggregator.accumulator().accept(a, b)
          } catch (ClassCastException ignored) {
            aggregator.accumulator().accept(a, null)
          }
        }
      }

      @Override
      Function finisher() {
        return {v ->
          try {
            return aggregator.finisher().apply(v)
          } catch (ClassCastException e) {
            return null
          }
        }
      }
    }
    return self.aggregate(Object, Object, safeAggregator)
  }

  static Vector getMean(DataFrame self) {
    return self.aggregate(Number, Double, mean())
  }

  static Vector getMedian(DataFrame self) {
    return self.aggregate(Number, Number, median())
  }

  static Vector getValueCounts(DataFrame self) {
    return self.aggregate(Object, valueCounts())
  }
}
