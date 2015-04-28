package org.briljantframework

import org.briljantframework.complex.Complex


public object all : Iterable<Int> {
    override fun iterator(): Iterator<Int> {
        throw UnsupportedOperationException()
    }
}

/*
 * Matrix creation
 */
fun matrix(vararg t: Int) = Bj.matrix(t)

fun matrix(vararg t: Double) = Bj.matrix(t)

fun matrix(vararg t: Long) = Bj.matrix(t)

fun matrix(vararg t: Complex) = Bj.matrix(t)

fun matrix(vararg t: Boolean) = Bj.matrix(t)

fun linspace(start: Double, end: Double, size: Int = 100) = Bj.linspace(start, end, size)

fun range(start: Int, end: Int, step: Int = 1) = Bj.range(start, end, step)

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