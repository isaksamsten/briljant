package org.briljantframework

import org.briljantframework.complex.Complex
import org.briljantframework.matrix.netlib.NetlibMatrixFactory


public object all : Iterable<Int> {
    override fun iterator(): Iterator<Int> {
        throw UnsupportedOperationException()
    }
}

val bj = NetlibMatrixFactory.getInstance()


/*
 * Matrix creation
 */
fun matrix(vararg t: Int) = bj.matrix(t)

fun matrix(vararg t: Double) = bj.matrix(t)

fun matrix(vararg t: Long) = bj.matrix(t)

fun matrix(vararg t: Complex) = bj.matrix(t)

fun matrix(vararg t: Boolean) = bj.matrix(t)

fun linspace(start: Double, end: Double, size: Int = 100) = bj.linspace(start, end, size)

fun range(start: Int, end: Int, step: Int = 1) = bj.range(start, end, step)

/*
 * DataFrame creation
 */

//fun frame(vararg columns: Vector): DataFrame = MixedDataFrame(*columns)

/*
 * Vector creation
 */
//fun vector(vararg values: String): Vector = StringVector(*values)
//
//fun vector(vararg values: Int): Vector = IntVector(*values)
//fun vector(vararg values: Double): Vector = DoubleVector(*values)
//fun vector(vararg values: Complex): Vector = ComplexVector(*values)