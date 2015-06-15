package org.briljantframework.dataframe

import org.briljantframework.function.Aggregates
import org.briljantframework.vector.Vector
import spock.lang.Specification

/**
 * Created by isak on 04/06/15.
 */
class DataFrameExtensionsSpec extends Specification {

  def "getAt returns the correct type"() {
    when:
//    def df = MixedDataFrame.of(
//        "a", Vector.of([1, 2, 3, 4]),
//        "b", Vector.of(["a","b","q","f"])
//    )
    def df = new MixedDataFrame([
        a: Vector.of([1, 1, 1, 2]),
        b: Vector.of([1, 2, 3, 4]),
        c: Vector.of(["1", "3", "10", "g"])
    ])
    df.recordIndex = ["a", "b", "c", "d"] as HashIndex

    then:
    df[0, 0] == 1
//    df.ix.get(Double, "a", 0) == 1
//    df.ix.get(Double, 2, "b") == 3
//    df[0].index as ArrayList == ["a", "b"]
    println(df.median)
    println(df.valueCounts["a"][0])
    println(df.agg(Aggregates.mean()))
  }
}