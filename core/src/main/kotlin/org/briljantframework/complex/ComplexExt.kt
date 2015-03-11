package org.briljantframework.complex

fun Complex.times(other: Complex) = this multiply other
fun Complex.times(other: Double) = this multiply other
fun Double.times(other: Complex) = other multiply this
fun Double.div(other: Complex) = Complex(this) / other
fun Double.plus(other: Complex) = other + this
fun Double.minus(other: Complex) = Complex(this) - other