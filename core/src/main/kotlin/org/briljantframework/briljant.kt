package org.briljantframework

import org.briljantframework.matrix.*
import org.briljantframework.complex.Complex
import org.briljantframework.vector.Vector
import org.briljantframework.dataframe.DataFrame
import org.briljantframework.dataframe.MixedDataFrame
import org.briljantframework.vector.StringVector
import org.briljantframework.vector.IntVector
import org.briljantframework.vector.DoubleVector
import org.briljantframework.vector.ComplexVector


public object all : Iterable<Int> {
    override fun iterator(): Iterator<Int> {
        throw UnsupportedOperationException()
    }
}

/*
 * Matrix creation
 */
fun matrix(vararg t: Int) = IntMatrix.of(*t)

fun matrix(vararg t: Double) = DoubleMatrix.of(*t)

fun matrix(vararg t: Long) = Matrices.newLongVector(*t)

fun matrix(vararg t: Complex) = Matrices.newComplexVector(*t)

fun matrix(vararg t: Boolean) = Matrices.newBitVector(*t)

fun linspace(start: Double, end: Double, size: Int = 100) = Matrices.linspace(start, end, size)

fun range(start: Int, end: Int, step: Int = 1) = Matrices.range(start, end, step)

/*
 * DataFrame creation
 */

fun frame(vararg columns: Vector): DataFrame = MixedDataFrame(*columns)

/*
 * Vector creation
 */
fun vector(vararg values: String): Vector = StringVector(*values)

fun vector(vararg values: Int): Vector = IntVector(*values)
fun vector(vararg values: Double): Vector = DoubleVector(*values)
fun vector(vararg values: Complex): Vector = ComplexVector(*values)