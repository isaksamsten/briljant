package org.briljantframework.matrix

import org.briljantframework.Bj
import org.briljantframework.all
import org.briljantframework.complex.Complex

public fun DoubleArray.round(): LongArray = this mapToLong { Math.round(it) }

public fun DoubleArray.sqrt(): DoubleArray = this map { Math.sqrt(it) }

public fun DoubleArray.exp(): DoubleArray = this map { Math.exp(it) }

public fun DoubleArray.min(): Double = Bj.min(this)

public fun DoubleArray.max(): Double = Bj.max(this)

fun <T : Array<T>> T.hstack(other: T): T = Bj.hstack(listOf(this, other))

fun <T : Array<T>> hstack(vararg others: T): T = Bj.hstack(listOf(*others))

fun <T : Array<T>> T.vstack(other: T): T = Bj.vstack(listOf(this, other))

fun <T : Array<T>> vstack(vararg others: T): T = Bj.vstack(listOf(*others))

fun <T : Array<T>> T.sort(): T = Bj.sort(this, { mat, a, b -> mat.compare(a, b) })

fun <T : Array<T>> T.sort(axis: Int): T
        = Bj.sort(this, { mat, a, b -> mat.compare(a, b) }, axis)

fun <T : Array<T>> T.sort(axis: Int = 0, cmp: (t: T, i: Int, j: Int) -> Int): T
        = Bj.sort(this, cmp, axis)

fun DoubleArray.mean(axis: Int) = Matrices.mean(axis, this)

fun DoubleArray.mean() = Matrices.mean(this)

private fun Progression<Int>.toSlice() = Bj.range(start, end, increment.toInt())

// Shape accessor
fun Shape.component1() = this.rows

fun Shape.component2() = this.columns


val Array<*>.rows: Int get() = this.rows()

val Array<*>.columns: Int get() = this.columns()

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
fun <T : Array<T>> T.get(range: Progression<Int>) = slice(range.toSlice())

fun <T : Array<T>> T.get(indexes: Collection<Int>) = slice(indexes)

fun <T : Array<T>> T.get(bits: BitArray) = slice(bits)

fun <T : Array<T>> T.get(rows: Progression<Int>, columns: Progression<Int>)
        = slice(rows.toSlice())

fun <T : Array<T>> T.get(rows: Collection<Int>, columns: Collection<Int>) = slice(rows, columns)

fun <T : Array<T>> T.get(rows: all, columns: Progression<Int>): T = this[0..this.rows, columns]

fun <T : Array<T>> T.get(rows: Progression<Int>, columns: all): T = this[rows, 0..this.columns]

fun <T : Array<T>> T.get(rows: all, columns: Collection<Int>): T
        = this[(0..this.rows).toList(), columns]

fun <T : Array<T>> T.get(rows: Collection<Int>, columns: all): T
        = this[rows, (0..this.columns).toList()]

fun <T : Array<T>> T.get(rows: all, column: Int) = this.getColumn(column)

fun <T : Array<T>> T.get(row: Int, columns: all) = this.getRow(row)

fun <T : Array<T>> T.set(bits: BitArray, value: Double)
        = this[bits].asDouble().assign(value)

fun <T : Array<T>> T.set(bits: BitArray, value: Int)
        = this[bits].asInt().assign(value)

fun <T : Array<T>> T.set(bits: BitArray, value: Long)
        = this[bits].asLong().assign(value)

fun <T : Array<T>> T.set(bits: BitArray, value: Complex)
        = this[bits].asComplex().assign(value)

fun DoubleArray.set(bits: BitArray, value: Array<*>)
        = this[bits].assign(value.asDouble())

fun IntArray.set(bits: BitArray, value: Array<*>)
        = this[bits].assign(value.asInt())

fun LongArray.set(bits: BitArray, value: Array<*>)
        = this[bits].assign(value.asLong())

fun ComplexArray.set(bits: BitArray, value: Array<*>)
        = this[bits].assign(value.asComplex())

fun <T : Array<T>> T.set(range: Progression<Int>, value: Double)
        = this[range].asDouble().assign(value)

fun <T : Array<T>> T.set(range: Progression<Int>, value: Int)
        = this[range].asInt().assign(value)

fun <T : Array<T>> T.set(range: Progression<Int>, value: Long)
        = this[range].asLong().assign(value)

fun <T : Array<T>> T.set(range: Progression<Int>, value: Complex)
        = this[range].asComplex().assign(value)

fun DoubleArray.set(range: Progression<Int>, value: Array<*>)
        = this[range].assign(value.asDouble())

fun IntArray.set(range: Progression<Int>, value: Array<*>)
        = this[range].assign(value.asInt())

fun LongArray.set(range: Progression<Int>, value: Array<*>)
        = this[range].assign(value.asLong())

fun ComplexArray.set(range: Progression<Int>, value: Array<*>)
        = this[range].assign(value.asComplex())

fun <T : Array<T>> T.set(rows: Progression<Int>, columns: Progression<Int>, value: Double)
        = this[rows, columns].asDouble().assign(value)

fun <T : Array<T>> T.set(rows: Progression<Int>, columns: Progression<Int>, value: Int)
        = this[rows, columns].asInt().assign(value)

fun <T : Array<T>> T.set(rows: Progression<Int>, columns: Progression<Int>, value: Long)
        = this[rows, columns].asLong().assign(value)

fun <T : Array<T>> T.set(rows: Progression<Int>, columns: Progression<Int>, value: Complex)
        = this[rows, columns].asComplex().assign(value)

fun DoubleArray.set(rows: Progression<Int>, columns: Progression<Int>, value: Array<*>)
        = this[rows, columns].assign(value.asDouble())

fun IntArray.set(rows: Progression<Int>, columns: Progression<Int>, value: Array<*>)
        = this[rows, columns].assign(value.asInt())

fun LongArray.set(rows: Progression<Int>, columns: Progression<Int>, value: Array<*>)
        = this[rows, columns].assign(value.asLong())

fun ComplexArray.set(rows: Progression<Int>, columns: Progression<Int>, value: Array<*>)
        = this[rows, columns].assign(value.asComplex())

fun <T : Array<T>> T.set(rows: all, columns: Progression<Int>, value: Double)
        = this[rows, columns].asDouble().assign(value)

fun <T : Array<T>> T.set(rows: all, columns: Progression<Int>, value: Int)
        = this[rows, columns].asInt().assign(value)

fun <T : Array<T>> T.set(rows: all, columns: Progression<Int>, value: Long)
        = this[rows, columns].asLong().assign(value)

fun <T : Array<T>> T.set(rows: all, columns: Progression<Int>, value: Complex)
        = this[rows, columns].asComplex().assign(value)

fun DoubleArray.set(rows: all, columns: Progression<Int>, value: Array<*>)
        = this[rows, columns].assign(value.asDouble())

fun IntArray.set(rows: all, columns: Progression<Int>, value: Array<*>)
        = this[rows, columns].assign(value.asInt())

fun LongArray.set(rows: all, columns: Progression<Int>, value: Array<*>)
        = this[rows, columns].assign(value.asLong())

fun ComplexArray.set(rows: all, columns: Progression<Int>, value: Array<*>)
        = this[rows, columns].assign(value.asComplex())

fun <T : Array<T>> T.set(rows: Progression<Int>, columns: all, value: Double)
        = this[rows, columns].asDouble().assign(value)

fun <T : Array<T>> T.set(rows: Progression<Int>, columns: all, value: Int)
        = this[rows, columns].asInt().assign(value)

fun <T : Array<T>> T.set(rows: Progression<Int>, columns: all, value: Long)
        = this[rows, columns].asLong().assign(value)

fun <T : Array<T>> T.set(rows: Progression<Int>, columns: all, value: Complex)
        = this[rows, columns].asComplex().assign(value)

fun DoubleArray.set(rows: Progression<Int>, columns: all, value: Array<*>)
        = this[rows, columns].assign(value.asDouble())

fun IntArray.set(rows: Progression<Int>, columns: all, value: Array<*>)
        = this[rows, columns].assign(value.asInt())

fun LongArray.set(rows: Progression<Int>, columns: all, value: Array<*>)
        = this[rows, columns].assign(value.asLong())

fun ComplexArray.set(rows: Progression<Int>, columns: all, value: Array<*>)
        = this[rows, columns].assign(value.asComplex())


// Multiplication operator

fun DoubleArray.times(other: Number) = mul(other.toDouble())

fun LongArray.times(other: Number) = mul(other.toLong())

fun IntArray.times(other: Number) = mul(other.toInt())

fun ComplexArray.times(other: Number) = if (other is Complex) {
    mul(other)
} else {
    mul(Complex.valueOf(other.toDouble()))
}

fun Double.times(matrix: Array<*>) = matrix.asDouble().mul(this)

fun Int.times(matrix: Array<*>) = matrix.asInt().mul(this)

fun Long.times(matrix: Array<*>) = matrix.asLong().mul(this)

fun Complex.times(matrix: Array<*>) = matrix.asComplex().mul(this)

fun DoubleArray.times(other: Array<*>) = mul(other.asDouble())

fun IntArray.times(other: Array<*>) = mul(other.asInt())

fun ComplexArray.times(other: Array<*>) = mul(other.asComplex())

fun LongArray.times(other: Array<*>) = mul(other.asLong())

// Addition

fun DoubleArray.plus(other: Number) = add(other.toDouble())

fun LongArray.plus(other: Number) = add(other.toLong())

fun IntArray.plus(other: Number) = add(other.toInt())

fun ComplexArray.plus(other: Number) = if (other is Complex) {
    add(other)
} else {
    add(Complex.valueOf(other.toDouble()))
}

fun Double.plus(matrix: Array<*>) = matrix.asDouble().add(this)

fun Int.plus(matrix: Array<*>) = matrix.asInt().add(this)

fun Long.plus(matrix: Array<*>) = matrix.asLong().add(this)

fun Complex.plus(matrix: Array<*>) = matrix.asComplex().add(this)

fun DoubleArray.plus(other: Array<*>) = add(other.asDouble())

fun IntArray.plus(other: Array<*>) = add(other.asInt())

fun ComplexArray.plus(other: Array<*>) = add(other.asComplex())

fun LongArray.plus(other: Array<*>) = add(other.asLong())

// Subtraction

fun DoubleArray.minus(other: Number) = sub(other.toDouble())

fun LongArray.minus(other: Number) = sub(other.toLong())

fun IntArray.minus(other: Number) = sub(other.toInt())

fun ComplexArray.minus(other: Number) = if (other is Complex) {
    sub(other)
} else {
    sub(Complex.valueOf(other.toDouble()))
}

fun Double.minus(matrix: Array<*>) = matrix.asDouble().rsub(this)

fun Int.minus(matrix: Array<*>) = matrix.asInt().rsub(this)

fun Long.minus(matrix: Array<*>) = matrix.asLong().rsub(this)

fun Complex.minus(matrix: Array<*>) = matrix.asComplex().rsub(this)

fun DoubleArray.minus(other: Array<*>) = sub(other.asDouble())

fun IntArray.minus(other: Array<*>) = sub(other.asInt())

fun ComplexArray.minus(other: Array<*>) = sub(other.asComplex())

fun LongArray.minus(other: Array<*>) = sub(other.asLong())

// Division

fun DoubleArray.div(other: Number) = div(other.toDouble())

fun LongArray.div(other: Number) = div(other.toLong())

fun IntArray.div(other: Number) = div(other.toInt())

fun ComplexArray.div(other: Number) = if (other is Complex) {
    div(other)
} else {
    div(Complex.valueOf(other.toDouble()))
}

fun Double.div(matrix: Array<*>) = matrix.asDouble().rdiv(this)

fun Int.div(matrix: Array<*>) = matrix.asInt().rdiv(this)

fun Long.div(matrix: Array<*>) = matrix.asLong().rdiv(this)

fun Complex.div(matrix: Array<*>) = matrix.asComplex().rdiv(this)

fun DoubleArray.div(other: Array<*>) = div(other.asDouble())

fun IntArray.div(other: Array<*>) = div(other.asInt())

fun ComplexArray.div(other: Array<*>) = div(other.asComplex())

fun LongArray.div(other: Array<*>) = div(other.asLong())