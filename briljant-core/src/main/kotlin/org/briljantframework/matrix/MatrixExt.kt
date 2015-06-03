package org.briljantframework.matrix

import org.briljantframework.Bj
import org.briljantframework.all
import org.briljantframework.complex.Complex

public fun DoubleMatrix.round(): LongMatrix = this mapToLong { Math.round(it) }

public fun DoubleMatrix.sqrt(): DoubleMatrix = this map { Math.sqrt(it) }

public fun DoubleMatrix.exp(): DoubleMatrix = this map { Math.exp(it) }

public fun DoubleMatrix.min(): Double = Bj.min(this)

public fun DoubleMatrix.max(): Double = Bj.max(this)

fun <T : Matrix<T>> T.hstack(other: T): T = Bj.hstack(listOf(this, other))

fun <T : Matrix<T>> hstack(vararg others: T): T = Bj.hstack(listOf(*others))

fun <T : Matrix<T>> T.vstack(other: T): T = Bj.vstack(listOf(this, other))

fun <T : Matrix<T>> vstack(vararg others: T): T = Bj.vstack(listOf(*others))

fun <T : Matrix<T>> T.sort(): T = Bj.sort(this, { mat, a, b -> mat.compare(a, b) })

fun <T : Matrix<T>> T.sort(axis: Dim): T
        = Bj.sort(this, { mat, a, b -> mat.compare(a, b) }, axis)

fun <T : Matrix<T>> T.sort(axis: Dim = Dim.R, cmp: (t: T, i: Int, j: Int) -> Int): T
        = Bj.sort(this, cmp, axis)

fun DoubleMatrix.mean(axis: Dim) = Matrices.mean(this, axis)

fun DoubleMatrix.mean() = Matrices.mean(this)

private fun Progression<Int>.toSlice() = Bj.range(start, end, increment.toInt())

// Shape accessor
fun Shape.component1() = this.rows

fun Shape.component2() = this.columns

val Matrix<*>.shape: Shape get() = this.getShape()

val Matrix<*>.rows: Int get() = this.rows()

val Matrix<*>.columns: Int get() = this.columns()

val IntMatrix.T: IntMatrix get() = this.transpose()

val DoubleMatrix.T: DoubleMatrix get() = this.transpose()

val LongMatrix.T: LongMatrix get() = this.transpose()

val ComplexMatrix.T: ComplexMatrix get() = this.transpose()

val BitMatrix.T: BitMatrix get() = this.transpose()

// Primitive matrix creation
fun Double.toVector(size: Int) = Bj.doubleVector(size) assign this

fun Double.toMatrix(rows: Int, columns: Int) = Bj.doubleMatrix(rows, columns) assign this

fun Int.toVector(size: Int) = Bj.intVector(size) assign this

fun Int.toMatrix(rows: Int, columns: Int) = Bj.intMatrix(rows, columns) assign this

fun Long.toVector(size: Int) = Bj.longVector(size) assign this

fun Long.toMatrix(rows: Int, columns: Int) = Bj.longMatrix(rows, columns) assign this

fun Complex.toVector(size: Int) = Bj.complexVector(size) assign this

fun Complex.toMatrix(rows: Int, columns: Int) = Bj.complexMatrix(rows, columns) assign this

// Slicing
fun <T : Matrix<T>> T.get(range: Progression<Int>) = slice(range.toSlice())

fun <T : Matrix<T>> T.get(indexes: Collection<Int>) = slice(indexes)

fun <T : Matrix<T>> T.get(bits: BitMatrix) = slice(bits)

fun <T : Matrix<T>> T.get(rows: Progression<Int>, columns: Progression<Int>)
        = slice(rows.toSlice(), columns.toSlice())

fun <T : Matrix<T>> T.get(rows: Collection<Int>, columns: Collection<Int>) = slice(rows, columns)

fun <T : Matrix<T>> T.get(rows: all, columns: Progression<Int>): T = this[0..this.rows, columns]

fun <T : Matrix<T>> T.get(rows: Progression<Int>, columns: all): T = this[rows, 0..this.columns]

fun <T : Matrix<T>> T.get(rows: all, columns: Collection<Int>): T
        = this[(0..this.rows).toList(), columns]

fun <T : Matrix<T>> T.get(rows: Collection<Int>, columns: all): T
        = this[rows, (0..this.columns).toList()]

fun <T : Matrix<T>> T.get(rows: all, column: Int) = this.getColumn(column)

fun <T : Matrix<T>> T.get(row: Int, columns: all) = this.getRow(row)

fun <T : Matrix<T>> T.set(bits: BitMatrix, value: Double)
        = this[bits].asDoubleMatrix().assign(value)

fun <T : Matrix<T>> T.set(bits: BitMatrix, value: Int)
        = this[bits].asIntMatrix().assign(value)

fun <T : Matrix<T>> T.set(bits: BitMatrix, value: Long)
        = this[bits].asLongMatrix().assign(value)

fun <T : Matrix<T>> T.set(bits: BitMatrix, value: Complex)
        = this[bits].asComplexMatrix().assign(value)

fun DoubleMatrix.set(bits: BitMatrix, value: Matrix<*>)
        = this[bits].assign(value.asDoubleMatrix())

fun IntMatrix.set(bits: BitMatrix, value: Matrix<*>)
        = this[bits].assign(value.asIntMatrix())

fun LongMatrix.set(bits: BitMatrix, value: Matrix<*>)
        = this[bits].assign(value.asLongMatrix())

fun ComplexMatrix.set(bits: BitMatrix, value: Matrix<*>)
        = this[bits].assign(value.asComplexMatrix())

fun <T : Matrix<T>> T.set(range: Progression<Int>, value: Double)
        = this[range].asDoubleMatrix().assign(value)

fun <T : Matrix<T>> T.set(range: Progression<Int>, value: Int)
        = this[range].asIntMatrix().assign(value)

fun <T : Matrix<T>> T.set(range: Progression<Int>, value: Long)
        = this[range].asLongMatrix().assign(value)

fun <T : Matrix<T>> T.set(range: Progression<Int>, value: Complex)
        = this[range].asComplexMatrix().assign(value)

fun DoubleMatrix.set(range: Progression<Int>, value: Matrix<*>)
        = this[range].assign(value.asDoubleMatrix())

fun IntMatrix.set(range: Progression<Int>, value: Matrix<*>)
        = this[range].assign(value.asIntMatrix())

fun LongMatrix.set(range: Progression<Int>, value: Matrix<*>)
        = this[range].assign(value.asLongMatrix())

fun ComplexMatrix.set(range: Progression<Int>, value: Matrix<*>)
        = this[range].assign(value.asComplexMatrix())

fun <T : Matrix<T>> T.set(rows: Progression<Int>, columns: Progression<Int>, value: Double)
        = this[rows, columns].asDoubleMatrix().assign(value)

fun <T : Matrix<T>> T.set(rows: Progression<Int>, columns: Progression<Int>, value: Int)
        = this[rows, columns].asIntMatrix().assign(value)

fun <T : Matrix<T>> T.set(rows: Progression<Int>, columns: Progression<Int>, value: Long)
        = this[rows, columns].asLongMatrix().assign(value)

fun <T : Matrix<T>> T.set(rows: Progression<Int>, columns: Progression<Int>, value: Complex)
        = this[rows, columns].asComplexMatrix().assign(value)

fun DoubleMatrix.set(rows: Progression<Int>, columns: Progression<Int>, value: Matrix<*>)
        = this[rows, columns].assign(value.asDoubleMatrix())

fun IntMatrix.set(rows: Progression<Int>, columns: Progression<Int>, value: Matrix<*>)
        = this[rows, columns].assign(value.asIntMatrix())

fun LongMatrix.set(rows: Progression<Int>, columns: Progression<Int>, value: Matrix<*>)
        = this[rows, columns].assign(value.asLongMatrix())

fun ComplexMatrix.set(rows: Progression<Int>, columns: Progression<Int>, value: Matrix<*>)
        = this[rows, columns].assign(value.asComplexMatrix())

fun <T : Matrix<T>> T.set(rows: all, columns: Progression<Int>, value: Double)
        = this[rows, columns].asDoubleMatrix().assign(value)

fun <T : Matrix<T>> T.set(rows: all, columns: Progression<Int>, value: Int)
        = this[rows, columns].asIntMatrix().assign(value)

fun <T : Matrix<T>> T.set(rows: all, columns: Progression<Int>, value: Long)
        = this[rows, columns].asLongMatrix().assign(value)

fun <T : Matrix<T>> T.set(rows: all, columns: Progression<Int>, value: Complex)
        = this[rows, columns].asComplexMatrix().assign(value)

fun DoubleMatrix.set(rows: all, columns: Progression<Int>, value: Matrix<*>)
        = this[rows, columns].assign(value.asDoubleMatrix())

fun IntMatrix.set(rows: all, columns: Progression<Int>, value: Matrix<*>)
        = this[rows, columns].assign(value.asIntMatrix())

fun LongMatrix.set(rows: all, columns: Progression<Int>, value: Matrix<*>)
        = this[rows, columns].assign(value.asLongMatrix())

fun ComplexMatrix.set(rows: all, columns: Progression<Int>, value: Matrix<*>)
        = this[rows, columns].assign(value.asComplexMatrix())

fun <T : Matrix<T>> T.set(rows: Progression<Int>, columns: all, value: Double)
        = this[rows, columns].asDoubleMatrix().assign(value)

fun <T : Matrix<T>> T.set(rows: Progression<Int>, columns: all, value: Int)
        = this[rows, columns].asIntMatrix().assign(value)

fun <T : Matrix<T>> T.set(rows: Progression<Int>, columns: all, value: Long)
        = this[rows, columns].asLongMatrix().assign(value)

fun <T : Matrix<T>> T.set(rows: Progression<Int>, columns: all, value: Complex)
        = this[rows, columns].asComplexMatrix().assign(value)

fun DoubleMatrix.set(rows: Progression<Int>, columns: all, value: Matrix<*>)
        = this[rows, columns].assign(value.asDoubleMatrix())

fun IntMatrix.set(rows: Progression<Int>, columns: all, value: Matrix<*>)
        = this[rows, columns].assign(value.asIntMatrix())

fun LongMatrix.set(rows: Progression<Int>, columns: all, value: Matrix<*>)
        = this[rows, columns].assign(value.asLongMatrix())

fun ComplexMatrix.set(rows: Progression<Int>, columns: all, value: Matrix<*>)
        = this[rows, columns].assign(value.asComplexMatrix())


// Multiplication operator

fun DoubleMatrix.times(other: Number) = mul(other.toDouble())

fun LongMatrix.times(other: Number) = mul(other.toLong())

fun IntMatrix.times(other: Number) = mul(other.toInt())

fun ComplexMatrix.times(other: Number) = if (other is Complex) {
    mul(other)
} else {
    mul(Complex.valueOf(other.toDouble()))
}

fun Double.times(matrix: Matrix<*>) = matrix.asDoubleMatrix().mul(this)

fun Int.times(matrix: Matrix<*>) = matrix.asIntMatrix().mul(this)

fun Long.times(matrix: Matrix<*>) = matrix.asLongMatrix().mul(this)

fun Complex.times(matrix: Matrix<*>) = matrix.asComplexMatrix().mul(this)

fun DoubleMatrix.times(other: Matrix<*>) = mul(other.asDoubleMatrix())

fun IntMatrix.times(other: Matrix<*>) = mul(other.asIntMatrix())

fun ComplexMatrix.times(other: Matrix<*>) = mul(other.asComplexMatrix())

fun LongMatrix.times(other: Matrix<*>) = mul(other.asLongMatrix())

// Addition

fun DoubleMatrix.plus(other: Number) = add(other.toDouble())

fun LongMatrix.plus(other: Number) = add(other.toLong())

fun IntMatrix.plus(other: Number) = add(other.toInt())

fun ComplexMatrix.plus(other: Number) = if (other is Complex) {
    add(other)
} else {
    add(Complex.valueOf(other.toDouble()))
}

fun Double.plus(matrix: Matrix<*>) = matrix.asDoubleMatrix().add(this)

fun Int.plus(matrix: Matrix<*>) = matrix.asIntMatrix().add(this)

fun Long.plus(matrix: Matrix<*>) = matrix.asLongMatrix().add(this)

fun Complex.plus(matrix: Matrix<*>) = matrix.asComplexMatrix().add(this)

fun DoubleMatrix.plus(other: Matrix<*>) = add(other.asDoubleMatrix())

fun IntMatrix.plus(other: Matrix<*>) = add(other.asIntMatrix())

fun ComplexMatrix.plus(other: Matrix<*>) = add(other.asComplexMatrix())

fun LongMatrix.plus(other: Matrix<*>) = add(other.asLongMatrix())

// Subtraction

fun DoubleMatrix.minus(other: Number) = sub(other.toDouble())

fun LongMatrix.minus(other: Number) = sub(other.toLong())

fun IntMatrix.minus(other: Number) = sub(other.toInt())

fun ComplexMatrix.minus(other: Number) = if (other is Complex) {
    sub(other)
} else {
    sub(Complex.valueOf(other.toDouble()))
}

fun Double.minus(matrix: Matrix<*>) = matrix.asDoubleMatrix().rsub(this)

fun Int.minus(matrix: Matrix<*>) = matrix.asIntMatrix().rsub(this)

fun Long.minus(matrix: Matrix<*>) = matrix.asLongMatrix().rsub(this)

fun Complex.minus(matrix: Matrix<*>) = matrix.asComplexMatrix().rsub(this)

fun DoubleMatrix.minus(other: Matrix<*>) = sub(other.asDoubleMatrix())

fun IntMatrix.minus(other: Matrix<*>) = sub(other.asIntMatrix())

fun ComplexMatrix.minus(other: Matrix<*>) = sub(other.asComplexMatrix())

fun LongMatrix.minus(other: Matrix<*>) = sub(other.asLongMatrix())

// Division

fun DoubleMatrix.div(other: Number) = div(other.toDouble())

fun LongMatrix.div(other: Number) = div(other.toLong())

fun IntMatrix.div(other: Number) = div(other.toInt())

fun ComplexMatrix.div(other: Number) = if (other is Complex) {
    div(other)
} else {
    div(Complex.valueOf(other.toDouble()))
}

fun Double.div(matrix: Matrix<*>) = matrix.asDoubleMatrix().rdiv(this)

fun Int.div(matrix: Matrix<*>) = matrix.asIntMatrix().rdiv(this)

fun Long.div(matrix: Matrix<*>) = matrix.asLongMatrix().rdiv(this)

fun Complex.div(matrix: Matrix<*>) = matrix.asComplexMatrix().rdiv(this)

fun DoubleMatrix.div(other: Matrix<*>) = div(other.asDoubleMatrix())

fun IntMatrix.div(other: Matrix<*>) = div(other.asIntMatrix())

fun ComplexMatrix.div(other: Matrix<*>) = div(other.asComplexMatrix())

fun LongMatrix.div(other: Matrix<*>) = div(other.asLongMatrix())