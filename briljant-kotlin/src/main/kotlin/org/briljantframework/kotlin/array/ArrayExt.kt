package org.briljantframework.kotlin.array

import org.briljantframework.array.*
import org.briljantframework.array.Arrays.*
import org.briljantframework.array.BasicIndex.all
import org.briljantframework.array.BooleanArray
import org.briljantframework.array.DoubleArray
import org.briljantframework.array.IntArray
import org.briljantframework.array.LongArray
import org.briljantframework.data.parser.SqlParser

operator fun DoubleArray.times(x: DoubleArray): DoubleArray = Arrays.times(this, x)
operator fun DoubleArray.timesAssign(x: DoubleArray) = Arrays.timesAssign(this, x)
operator fun DoubleArray.plus(x: DoubleArray): DoubleArray = Arrays.plus(this, x)
operator fun DoubleArray.plusAssign(x: DoubleArray) = Arrays.plusAssign(this, x)
operator fun DoubleArray.minus(x: DoubleArray): DoubleArray = Arrays.minus(this, x)
operator fun DoubleArray.div(x: DoubleArray): DoubleArray = Arrays.div(this, x)
operator fun IntArray.times(x: IntArray): IntArray = Arrays.times(this, x)
operator fun IntArray.plus(x: IntArray): IntArray = Arrays.plus(this, x)
operator fun IntArray.minus(x: IntArray): IntArray = Arrays.minus(this, x)
operator fun IntArray.div(x: IntArray): IntArray = Arrays.div(this, x)


infix fun DoubleArray.pow(x: Double) = Arrays.pow(this, x)
infix fun Double.pow(x: DoubleArray) = x.map { Math.pow(it, this) }

infix fun LongArray.pow(x: Long) = this.doubleArray() pow x.toDouble()
infix fun IntArray.pow(x: Int) = this.doubleArray() pow x.toDouble()

infix fun BooleanArray.and(x: BooleanArray): BooleanArray = this.and(x)
infix fun BooleanArray.or(x: BooleanArray): BooleanArray = this.or(x)
infix fun BooleanArray.xor(x: BooleanArray): BooleanArray = this.xor(x)


/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */

fun main(args: Array<String>) {

    println(10.0 pow doubleVector(2.0, 3.0))

    val x = doubleArray(10, 10, 10)
    val y = linspace(-1.0, 1.0, 100).reshape(10, 10)

    val z = broadcastCombine(x, y, { i, j -> i * j })
    val w = broadcast(x).combine(y, { i, j -> i * j })
    println(z)
    println(w)

    val a = zeros(10, 10)
    val b = linspace(-1.0, 1.0, 10 * 10).reshape(10, 10)

//    val where = b.where { it > 0 }
//    set(a, where, linspace(0.0, 49.0, 50))
//    println(a)

    a += b.transpose()
    println(a)
    println(b.transpose())


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


    val r = linspace(-1.0, 0.0, 10).reshape(1, 10)
    val s = linspace(0.0, 1.0, 10).reshape(10, 1)
    r.assign(s)
    println(r)


    //    println(f eq f)
    //    println(f[intVector(0, 1), intVector(0, 1, 0, 1).reshape(2, 2)])
    //
    //    val x = linspace(0.0, 1.0, 20).reshape(4, 5)
    //    println(x.where(DoubleArray.of(0.5)) { a, b -> a > b })
}
