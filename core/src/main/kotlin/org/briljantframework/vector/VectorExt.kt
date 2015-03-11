package org.briljantframework.vector

import org.briljantframework.complex.Complex

fun Double.toValue() = Convert.toValue(this)
fun Int.toValue() = Convert.toValue(this)
fun String.toValue() = Convert.toValue(this)
fun Complex.toValue() = Convert.toValue(this)
fun Bit.toValue() = Convert.toValue(this)

fun Vector.contains(value: Value) = Vectors.find(this, value)

fun Vector.contains(value: String) = Vectors.find(this, value)

fun Vector.contains(value: Int) = Vectors.find(this, value)