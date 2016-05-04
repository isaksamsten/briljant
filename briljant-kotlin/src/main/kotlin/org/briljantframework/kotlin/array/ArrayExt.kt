package org.briljantframework.kotlin.array

import org.briljantframework.array.*
import org.briljantframework.array.Arrays.*
import org.briljantframework.array.BasicIndex.all
import org.briljantframework.array.BooleanArray
import org.briljantframework.array.DoubleArray
import org.briljantframework.array.IntArray
import org.briljantframework.array.LongArray
import org.briljantframework.data.parser.SqlParser

infix fun DoubleArray.eq(x: DoubleArray) = this.eq(x)
infix fun LongArray.eq(x: LongArray) : BooleanArray = this.eq(x)
infix fun IntArray.eq(x: IntArray) : BooleanArray = this.eq(x)





/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */

fun main(args: Array<String>) {

    val sp = SqlParser("")

//    //    listOf<>()
//    val y = series("a", "b", "c", "d")
//
    //    println(doubleArray(10, 10, 10)[0, 1,2])
    //    println(y.reshape(2, 2))
    //    println(f[range(3), all])

    val f = intVector(1, 2, 3, 4, 5, 6, 7, 8).reshape(4, 2)
    println(f)
    f[listOf(range(3), all)] = intVector(10, 20)
    println(f)

//    println(f eq f)
//    println(f[intVector(0, 1), intVector(0, 1, 0, 1).reshape(2, 2)])
//
//    val x = linspace(0.0, 1.0, 20).reshape(4, 5)
//    println(x.where(DoubleArray.of(0.5)) { a, b -> a > b })
}
