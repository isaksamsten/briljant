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

package org.briljantframework.data.dataframe

import groovy.transform.CompileStatic
import groovy.transform.TypeCheckingMode
import org.briljantframework.data.index.DataFrameLocationGetter
import org.briljantframework.data.vector.Vector

import java.util.function.BiConsumer
import java.util.function.BinaryOperator
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collector

import static org.briljantframework.data.Collectors.*

/**
 * Created by isak on 04/06/15.
 */
@CompileStatic
class DataFrameExtensions {

  static DataFrameLocationGetter getLoc(DataFrame self) {
    return self.loc()
  }

  static Vector propertyMissing(DataFrame self, String name) {
    return self.get(name)
  }

  static Vector getAt(DataFrame self, Object key) {
    return self.get(key)
  }

  @CompileStatic(TypeCheckingMode.SKIP)
  static Vector collect(DataFrame self, Collector collector) {
    def safeCollector = new Collector() {

      @Override
      Supplier supplier() {
        return collector.supplier()
      }

      @Override
      BiConsumer accumulator() {
        return {a, b ->
          try {
            collector.accumulator().accept(a, b)
          } catch (ClassCastException ignored) {
            collector.accumulator().accept(a, null)
          }
        }
      }

      @Override
      BinaryOperator combiner() {
        return {left, right ->
          try {
            collector.combiner().apply(left, right);
          } catch (ClassCastException ignored) {

          }
        }
      }

      @Override
      Function finisher() {
        return {v ->
          try {
            return collector.finisher().apply(v)
          } catch (ClassCastException e) {
            return null
          }
        }
      }

      @Override
      Set<Collector.Characteristics> characteristics() {
        return collector.characteristics()
      }
    }
    return self.collect(Object, Object, safeCollector)
  }

  static Vector getMean(DataFrame self) {
    return self.collect(Number, Double, mean())
  }

  static Vector getMedian(DataFrame self) {
    return self.collect(Number, Number, median())
  }

  static Vector getValueCounts(DataFrame self) {
    return self.collect(Object, valueCounts())
  }
}
