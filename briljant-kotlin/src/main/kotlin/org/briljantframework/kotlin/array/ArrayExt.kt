package org.briljantframework.kotlin.array

import org.apache.commons.math3.distribution.UniformRealDistribution
import org.briljantframework.array.*
import org.briljantframework.array.Arrays.*
import org.briljantframework.array.BooleanArray
import org.briljantframework.array.DoubleArray
import org.briljantframework.array.IntArray
import org.briljantframework.array.LongArray

fun <S : BaseArray<S>> BaseArray<S>.reshape(shape: kotlin.IntArray): S = this.reshape(*shape)
fun <S : BaseArray<S>> BaseArray<S>.reshape(shape: IntArray): S = this.reshape(*toKotlinIntArray(shape))

operator fun DoubleArray.times(x: DoubleArray): DoubleArray = times(this, x)
operator fun DoubleArray.timesAssign(x: DoubleArray) = times(this, x, this)
operator fun DoubleArray.plus(x: DoubleArray): DoubleArray = plus(this, x)
operator fun DoubleArray.plusAssign(x: DoubleArray) = plus(this, x, this)
operator fun DoubleArray.minus(x: DoubleArray): DoubleArray = minus(this, x)
operator fun DoubleArray.div(x: DoubleArray): DoubleArray = div(this, x)

operator fun <S : BaseArray<S>> BaseArray<S>.get(vararg x: IntProgression): S =
        this.get(x.map { range(it.first, it.last, it.step) })


operator fun IntArray.times(x: IntArray): IntArray = times(this, x)
operator fun IntArray.plus(x: IntArray): IntArray = plus(this, x)
operator fun IntArray.minus(x: IntArray): IntArray = minus(this, x)
operator fun IntArray.div(x: IntArray): IntArray = div(this, x)


infix fun DoubleArray.pow(x: Double) = pow(this, x)
infix fun Double.pow(x: DoubleArray) = x.map { Math.pow(it, this) }

infix fun LongArray.pow(x: Long) = this.doubleArray() pow x.toDouble()
infix fun IntArray.pow(x: Int) = this.doubleArray() pow x.toDouble()

infix fun BooleanArray.and(x: BooleanArray): BooleanArray = and(this, x)
infix fun BooleanArray.or(x: BooleanArray): BooleanArray = or(this, x)
infix fun BooleanArray.xor(x: BooleanArray): BooleanArray = xor(this, x)

infix fun NumberArray.lt(x: Number): BooleanArray = this.doubleArray().where { it < x.toDouble() }
infix fun NumberArray.lt(x: NumberArray): BooleanArray = Arrays.broadcastCombine(doubleArray(), x.doubleArray(), { t, u ->
    val out = BooleanArray.falses(t.size)
    for (i in 0 until t.size) {
        out[i] = t[i] < u[i]
    }
    out.reshape(t.shape)
})

infix fun NumberArray.le(x: Number): BooleanArray = doubleArray().where { it <= x.toDouble() }
infix fun NumberArray.le(x: NumberArray): BooleanArray = Arrays.broadcastCombine(doubleArray(), x.doubleArray(), { t, u ->
    val out = BooleanArray.falses(t.size)
    for (i in 0 until t.size) {
        out[i] = t[i] <= u[i]
    }
    out.reshape(t.shape)
})

infix fun NumberArray.gt(x: Number): BooleanArray = this.doubleArray().where { it > x.toDouble() }
infix fun NumberArray.gt(x: NumberArray): BooleanArray = Arrays.broadcastCombine(doubleArray(), x.doubleArray(), { t, u ->
    val out = BooleanArray.falses(t.size)
    for (i in 0 until t.size) {
        out[i] = t[i] > u[i]
    }
    out.reshape(t.shape)
})

infix fun NumberArray.ge(x: Number): BooleanArray = doubleArray().where { it >= x.toDouble() }
infix fun NumberArray.ge(x: NumberArray): BooleanArray = Arrays.broadcastCombine(doubleArray(), x.doubleArray(), { t, u ->
    val out = BooleanArray.falses(t.size)
    for (i in 0 until t.size) {
        out[i] = t[i] >= u[i]
    }
    out.reshape(t.shape)
})

infix fun NumberArray.eq(x: Number): BooleanArray = doubleArray().where { it == x.toDouble() }
infix fun NumberArray.eq(x: NumberArray): BooleanArray = Arrays.broadcastCombine(doubleArray(), x.doubleArray(), { t, u ->
    val out = BooleanArray.falses(t.size)
    for (i in 0 until t.size) {
        out[i] = t[i] == u[i]
    }
    out.reshape(t.shape)
})

infix fun NumberArray.neq(x: Number): BooleanArray = doubleArray().where { it != x.toDouble() }
infix fun NumberArray.neq(x: NumberArray): BooleanArray = Arrays.broadcastCombine(doubleArray(), x.doubleArray(), { t, u ->
    val out = BooleanArray.falses(t.size)
    for (i in 0 until t.size) {
        out[i] = t[i] != u[i]
    }
    out.reshape(t.shape)
})




/**
 * @author Isak Karlsson <isak-kar@dsv.su.se>
 */

fun main(args: Array<String>) {

    println(10.0 pow doubleVector(2.0, 3.0))


    val x = rand(5 * 5, UniformRealDistribution(-10.0, 10.0)).reshape(5, 5)
    val y = randn(5 * 5).reshape(5, 5)
    val z = randi(5 * 5, -10, 10).reshape(5, 5)
    println(x)
    println(x le y)

    println(x[0..2, 0..5 step 2])


}

private fun toKotlinIntArray(shape: IntArray): kotlin.IntArray {
    val x = kotlin.IntArray(shape.size)
    for (i in 0 until shape.size) {
        x[i] = shape[i]
    }
    return x
}
