package org.briljantframework.array

import org.briljantframework.Bj
import org.briljantframework.all
import org.briljantframework.complex.Complex

public fun DoubleArray.round(): LongArray = this mapToLong { Math.round(it) }

public fun DoubleArray.sqrt(): DoubleArray = this map { Math.sqrt(it) }

public fun DoubleArray.exp(): DoubleArray = this map { Math.exp(it) }

public fun DoubleArray.min(): Double = Bj.min(this)

public fun DoubleArray.max(): Double = Bj.max(this)

fun <T : BaseArray<T>> T.hstack(other: T): T = Bj.hstack(listOf(this, other))

fun <T : BaseArray<T>> hstack(vararg others: T): T = Bj.hstack(listOf(*others))

fun <T : BaseArray<T>> T.vstack(other: T): T = Bj.vstack(listOf(this, other))

fun <T : BaseArray<T>> vstack(vararg others: T): T = Bj.vstack(listOf(*others))

fun <T : BaseArray<T>> T.sort(): T = Bj.sort(this, { mat, a, b -> mat.compare(a, b) })

fun <T : BaseArray<T>> T.sort(axis: Int): T
        = Bj.sort(this, { mat, a, b -> mat.compare(a, b) }, axis)

fun <T : BaseArray<T>> T.sort(axis: Int = 0, cmp: (t: T, i: Int, j: Int) -> Int): T
        = Bj.sort(this, cmp, axis)

fun DoubleArray.mean(axis: Int) = Matrices.mean(axis, this)

fun DoubleArray.mean() = Matrices.mean(this)

private fun Progression<Int>.toSlice() = Bj.range(start, end, increment.toInt())

// Shape accessor
fun Shape.component1() = this.rows

fun Shape.component2() = this.columns


val BaseArray<*>.rows: Int get() = this.rows()

val BaseArray<*>.columns: Int get() = this.columns()

val IntArray.T: IntArray get() = this.transpose()

val DoubleArray.T: DoubleArray get() = this.transpose()

val LongArray.T: LongArray get() = this.transpose()

val ComplexArray.T: ComplexArray get() = this.transpose()

val BitArray.T: BitArray get() = this.transpose()

//// Primitive matrix creation
//fun Double.toVector(size: Int) = Bj.doubleVector(size) assign this
//
//fun Double.toMatrix(rows: Int, columns: Int) = Bj.doubleArray(rows, columns) assign this
//
//fun Int.toVector(size: Int) = Bj.intVector(size) assign this
//
//fun Int.toMatrix(rows: Int, columns: Int) = Bj.intMatrix(rows, columns) assign this
//
//fun Long.toVector(size: Int) = Bj.longVector(size) assign this
//
//fun Long.toMatrix(rows: Int, columns: Int) = Bj.longMatrix(rows, columns) assign this
//
//fun Complex.toVector(size: Int) = Bj.complexVector(size) assign this
//
//fun Complex.toMatrix(rows: Int, columns: Int) = Bj.complexMatrix(rows, columns) assign this

// Slicing
fun <T : BaseArray<T>> T.get(range: Progression<Int>) = get(range.toSlice())

fun <T : BaseArray<T>> T.get(bits: BitArray) = slice(bits)

fun <T : BaseArray<T>> T.get(rows: Progression<Int>, columns: Progression<Int>)
        = get(rows.toSlice())

fun <T : BaseArray<T>> T.get(rows: all, columns: Progression<Int>): T = this[0..this.rows, columns]

fun <T : BaseArray<T>> T.get(rows: Progression<Int>, columns: all): T = this[rows, 0..this.columns]

// Multiplication operator

fun DoubleArray.times(other: Number) = mul(other.toDouble())

fun LongArray.times(other: Number) = mul(other.toLong())

fun IntArray.times(other: Number) = mul(other.toInt())

fun ComplexArray.times(other: Number) = if (other is Complex) {
    mul(other)
} else {
    mul(Complex.valueOf(other.toDouble()))
}

fun Double.times(matrix: BaseArray<*>) = matrix.asDouble().mul(this)

fun Int.times(matrix: BaseArray<*>) = matrix.asInt().mul(this)

fun Long.times(matrix: BaseArray<*>) = matrix.asLong().mul(this)

fun Complex.times(matrix: BaseArray<*>) = matrix.asComplex().mul(this)

fun DoubleArray.times(other: BaseArray<*>) = mul(other.asDouble())

fun IntArray.times(other: BaseArray<*>) = mul(other.asInt())

fun ComplexArray.times(other: BaseArray<*>) = mul(other.asComplex())

fun LongArray.times(other: BaseArray<*>) = mul(other.asLong())

// Addition

fun DoubleArray.plus(other: Number) = add(other.toDouble())

fun LongArray.plus(other: Number) = add(other.toLong())

fun IntArray.plus(other: Number) = add(other.toInt())

fun ComplexArray.plus(other: Number) = if (other is Complex) {
    add(other)
} else {
    add(Complex.valueOf(other.toDouble()))
}

fun Double.plus(matrix: BaseArray<*>) = matrix.asDouble().add(this)

fun Int.plus(matrix: BaseArray<*>) = matrix.asInt().add(this)

fun Long.plus(matrix: BaseArray<*>) = matrix.asLong().add(this)

fun Complex.plus(matrix: BaseArray<*>) = matrix.asComplex().add(this)

fun DoubleArray.plus(other: BaseArray<*>) = add(other.asDouble())

fun IntArray.plus(other: BaseArray<*>) = add(other.asInt())

fun ComplexArray.plus(other: BaseArray<*>) = add(other.asComplex())

fun LongArray.plus(other: BaseArray<*>) = add(other.asLong())

// Subtraction

fun DoubleArray.minus(other: Number) = sub(other.toDouble())

fun LongArray.minus(other: Number) = sub(other.toLong())

fun IntArray.minus(other: Number) = sub(other.toInt())

fun ComplexArray.minus(other: Number) = if (other is Complex) {
    sub(other)
} else {
    sub(Complex.valueOf(other.toDouble()))
}

fun Double.minus(matrix: BaseArray<*>) = matrix.asDouble().rsub(this)

fun Int.minus(matrix: BaseArray<*>) = matrix.asInt().rsub(this)

fun Long.minus(matrix: BaseArray<*>) = matrix.asLong().rsub(this)

fun Complex.minus(matrix: BaseArray<*>) = matrix.asComplex().rsub(this)

fun DoubleArray.minus(other: BaseArray<*>) = sub(other.asDouble())

fun IntArray.minus(other: BaseArray<*>) = sub(other.asInt())

fun ComplexArray.minus(other: BaseArray<*>) = sub(other.asComplex())

fun LongArray.minus(other: BaseArray<*>) = sub(other.asLong())

// Division

fun DoubleArray.div(other: Number) = div(other.toDouble())

fun LongArray.div(other: Number) = div(other.toLong())

fun IntArray.div(other: Number) = div(other.toInt())

fun ComplexArray.div(other: Number) = if (other is Complex) {
    div(other)
} else {
    div(Complex.valueOf(other.toDouble()))
}

fun Double.div(matrix: BaseArray<*>) = matrix.asDouble().rdiv(this)

fun Int.div(matrix: BaseArray<*>) = matrix.asInt().rdiv(this)

fun Long.div(matrix: BaseArray<*>) = matrix.asLong().rdiv(this)

fun Complex.div(matrix: BaseArray<*>) = matrix.asComplex().rdiv(this)

fun DoubleArray.div(other: BaseArray<*>) = div(other.asDouble())

fun IntArray.div(other: BaseArray<*>) = div(other.asInt())

fun ComplexArray.div(other: BaseArray<*>) = div(other.asComplex())

fun LongArray.div(other: BaseArray<*>) = div(other.asLong())