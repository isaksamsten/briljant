package org.briljantframework.kotlin.array

import org.briljantframework.array.Arrays.*
import org.briljantframework.array.BasicIndex.__
import org.briljantframework.array.DoubleArray

/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */

fun main(args: Array<String>) {
    val y = vector("a", "b", "c", "d")
    val f = intVector(1, 2, 3, 4, 5, 6, 7, 8).reshape(4, 2)
    println(y.reshape(2, 2))
    println(f[range(3), __])
    println(f)
    println(f[intVector(0, 1), intVector(0, 1, 0, 1).reshape(2, 2)])

    val x = linspace(0.0, 1.0, 20).reshape(4, 5)
    println(x.where(DoubleArray.of(0.5)) { a, b -> a > b })
}
