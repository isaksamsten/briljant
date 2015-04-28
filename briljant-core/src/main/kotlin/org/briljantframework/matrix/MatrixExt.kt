package org.briljantframework.matrix

import org.briljantframework.Bj
import org.briljantframework.all
import org.briljantframework.complex.Complex
import org.briljantframework.matrix.storage.Storage



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

val Matrix<*>.data: Storage get() = this.getStorage()

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

fun <T : Matrix<T>> T.get(rows: all, column: Int) = this.getColumnView(column)

fun <T : Matrix<T>> T.get(row: Int, columns: all) = this.getRowView(row)

//fun DoubleMatrix.get(range: Progression<Int>) = slice(range.toSlice())
//
//fun DoubleMatrix.get(indexes: Collection<Int>) = slice(indexes)
//
//fun DoubleMatrix.get(bits: BitMatrix) = slice(bits)
//
//fun DoubleMatrix.get(rows: Progression<Int>, columns: Progression<Int>)
//        = slice(rows.toSlice(), columns.toSlice())
//
//fun DoubleMatrix.get(rows: Collection<Int>, columns: Collection<Int>) = slice(rows, columns)
//
//fun DoubleMatrix.get(rows: all, columns: Progression<Int>) = this[0..this.rows, columns]
//
//fun DoubleMatrix.get(rows: Progression<Int>, columns: all) = this[rows, 0..this.columns]
//
//fun DoubleMatrix.get(rows: all, columns: Collection<Int>) = this[(0..this.rows).toList(), columns]
//
//fun DoubleMatrix.get(rows: Collection<Int>, columns: all) = this[rows, (0..this.columns).toList()]
//
//fun DoubleMatrix.get(rows: all, column: Int) = this.getColumnView(column)
//
//fun DoubleMatrix.get(row: Int, columns: all) = this.getRowView(row)
//
//fun IntMatrix.get(range: Progression<Int>) = slice(range.toSlice())
//
//fun IntMatrix.get(indexes: Collection<Int>) = slice(indexes)
//
//fun IntMatrix.get(bits: BitMatrix) = slice(bits)
//
//fun IntMatrix.get(rows: Progression<Int>, columns: Progression<Int>)
//        = slice(rows.toSlice(), columns.toSlice())
//
//fun IntMatrix.get(rows: Collection<Int>, columns: Collection<Int>) = slice(rows, columns)
//
//fun IntMatrix.get(rows: all, columns: Progression<Int>) = this[0..this.rows, columns]
//
//fun IntMatrix.get(rows: Progression<Int>, columns: all) = this[rows, 0..this.columns]
//
//fun IntMatrix.get(rows: all, columns: Collection<Int>) = this[(0..this.rows).toList(), columns]
//
//fun IntMatrix.get(rows: Collection<Int>, columns: all) = this[rows, (0..this.columns).toList()]
//
//fun IntMatrix.get(rows: all, column: Int) = this.getColumnView(column)
//
//fun IntMatrix.get(row: Int, columns: all) = this.getRowView(row)
//
//fun LongMatrix.get(range: Progression<Int>) = slice(range.toSlice())
//
//fun LongMatrix.get(indexes: Collection<Int>) = slice(indexes)
//
//fun LongMatrix.get(bits: BitMatrix) = slice(bits)
//
//fun LongMatrix.get(rows: Progression<Int>, columns: Progression<Int>)
//        = slice(rows.toSlice(), columns.toSlice())
//
//fun LongMatrix.get(rows: Collection<Int>, columns: Collection<Int>)
//        = slice(rows, columns)
//
//fun LongMatrix.get(rows: all, columns: Progression<Int>)
//        = this[0..this.rows, columns]
//
//fun LongMatrix.get(rows: Progression<Int>, columns: all)
//        = this[rows, 0..this.columns]
//
//fun LongMatrix.get(rows: all, columns: Collection<Int>)
//        = this[(0..this.rows).toList(), columns]
//
//fun LongMatrix.get(rows: Collection<Int>, columns: all)
//        = this[rows, (0..this.columns).toList()]
//
//fun LongMatrix.get(rows: all, column: Int) = this.getColumnView(column)
//
//fun LongMatrix.get(row: Int, columns: all) = this.getRowView(row)
//
//fun ComplexMatrix.get(range: Progression<Int>) = slice(range.toSlice())
//
//fun ComplexMatrix.get(indexes: Collection<Int>) = slice(indexes)
//
//fun ComplexMatrix.get(bits: BitMatrix) = slice(bits)
//
//fun ComplexMatrix.get(rows: Progression<Int>, columns: Progression<Int>)
//        = slice(rows.toSlice(), columns.toSlice())
//
//fun ComplexMatrix.get(rows: Collection<Int>, columns: Collection<Int>)
//        = slice(rows, columns)
//
//fun ComplexMatrix.get(rows: all, columns: Progression<Int>)
//        = this[0..this.rows, columns]
//
//fun ComplexMatrix.get(rows: Progression<Int>, columns: all)
//        = this[rows, 0..this.columns]
//
//fun ComplexMatrix.get(rows: all, columns: Collection<Int>)
//        = this[(0..this.rows).toList(), columns]
//
//fun ComplexMatrix.get(rows: Collection<Int>, columns: all)
//        = this[rows, (0..this.columns).toList()]
//
//fun ComplexMatrix.get(rows: all, column: Int)
//        = this.getColumnView(column)
//
//fun ComplexMatrix.get(row: Int, columns: all)
//        = this.getRowView(row)


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

fun Matrix<*>.times(other: Double) = asDoubleMatrix().mul(other)

fun Matrix<*>.times(other: Int) = asIntMatrix().mul(other)

fun Matrix<*>.times(other: Long) = asLongMatrix().mul(other)

fun Matrix<*>.times(other: Complex) = asComplexMatrix().mul(other)

fun Double.times(matrix: Matrix<*>) = matrix.asDoubleMatrix().mul(this)

fun Int.times(matrix: Matrix<*>) = matrix.asIntMatrix().mul(this)

fun Long.times(matrix: Matrix<*>) = matrix.asLongMatrix().mul(this)

fun Complex.times(matrix: Matrix<*>) = matrix.asComplexMatrix().mul(this)

fun DoubleMatrix.times(other: Matrix<*>) = mul(other.asDoubleMatrix())

fun IntMatrix.times(other: Matrix<*>) = mul(other.asIntMatrix())

fun ComplexMatrix.times(other: Matrix<*>) = mul(other.asComplexMatrix())

fun LongMatrix.times(other: Matrix<*>) = mul(other.asLongMatrix())

// Addition

fun Matrix<*>.plus(other: Double) = asDoubleMatrix().add(other)

fun Matrix<*>.plus(other: Int) = asIntMatrix().add(other)

fun Matrix<*>.plus(other: Long) = asLongMatrix().add(other)

fun Matrix<*>.plus(other: Complex) = asComplexMatrix().add(other)

fun Double.plus(matrix: Matrix<*>) = matrix.asDoubleMatrix().add(this)

fun Int.plus(matrix: Matrix<*>) = matrix.asIntMatrix().add(this)

fun Long.plus(matrix: Matrix<*>) = matrix.asLongMatrix().add(this)

fun Complex.plus(matrix: Matrix<*>) = matrix.asComplexMatrix().add(this)

fun DoubleMatrix.plus(other: Matrix<*>) = add(other.asDoubleMatrix())

fun IntMatrix.plus(other: Matrix<*>) = add(other.asIntMatrix())

fun ComplexMatrix.plus(other: Matrix<*>) = add(other.asComplexMatrix())

fun LongMatrix.plus(other: Matrix<*>) = add(other.asLongMatrix())

// Subtraction

fun Matrix<*>.minus(other: Double) = asDoubleMatrix().sub(other)

fun Matrix<*>.minus(other: Int) = asIntMatrix().sub(other)

fun Matrix<*>.minus(other: Long) = asLongMatrix().sub(other)

fun Matrix<*>.minus(other: Complex) = asComplexMatrix().sub(other)

fun Double.minus(matrix: Matrix<*>) = matrix.asDoubleMatrix().rsub(this)

fun Int.minus(matrix: Matrix<*>) = matrix.asIntMatrix().rsub(this)

fun Long.minus(matrix: Matrix<*>) = matrix.asLongMatrix().rsub(this)

fun Complex.minus(matrix: Matrix<*>) = matrix.asComplexMatrix().rsub(this)

fun DoubleMatrix.minus(other: Matrix<*>) = sub(other.asDoubleMatrix())

fun IntMatrix.minus(other: Matrix<*>) = sub(other.asIntMatrix())

fun ComplexMatrix.minus(other: Matrix<*>) = sub(other.asComplexMatrix())

fun LongMatrix.minus(other: Matrix<*>) = sub(other.asLongMatrix())

// Division

fun Matrix<*>.div(other: Double) = asDoubleMatrix().div(other)

fun Matrix<*>.div(other: Int) = asIntMatrix().div(other)

fun Matrix<*>.div(other: Long) = asLongMatrix().div(other)

fun Matrix<*>.div(other: Complex) = asComplexMatrix().div(other)

fun Double.div(matrix: Matrix<*>) = matrix.asDoubleMatrix().rdiv(this)

fun Int.div(matrix: Matrix<*>) = matrix.asIntMatrix().rdiv(this)

fun Long.div(matrix: Matrix<*>) = matrix.asLongMatrix().rdiv(this)

fun Complex.div(matrix: Matrix<*>) = matrix.asComplexMatrix().rdiv(this)

fun DoubleMatrix.div(other: Matrix<*>) = div(other.asDoubleMatrix())

fun IntMatrix.div(other: Matrix<*>) = div(other.asIntMatrix())

fun ComplexMatrix.div(other: Matrix<*>) = div(other.asComplexMatrix())

fun LongMatrix.div(other: Matrix<*>) = div(other.asLongMatrix())