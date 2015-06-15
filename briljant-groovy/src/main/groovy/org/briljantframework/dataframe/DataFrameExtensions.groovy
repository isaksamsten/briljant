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
