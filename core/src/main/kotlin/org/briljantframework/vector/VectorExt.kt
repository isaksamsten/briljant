package org.briljantframework.vector

import org.briljantframework.complex.Complex
import com.google.common.collect.Sets

fun Double.toValue() = Convert.toValue(this)

fun Int.toValue() = Convert.toValue(this)

fun String.toValue() = Convert.toValue(this)

fun Complex.toValue() = Convert.toValue(this)

fun Bit.toValue() = Convert.toValue(this)

fun Vector.contains(value: Value) = this.find(value) != -1

fun Vector.contains(value: String) = Vectors.find(this, value) != -1

fun Vector.contains(value: Int) = Vectors.find(this, value) != -1

fun Vector.find(value: Value) = Vectors.find(this, value)

fun Vector.toSet() = Sets.newHashSet(this.asValueList())

fun Vector.toList() = asValueList()

fun Vector.unique() = Vectors.unique(this)

fun Vector.unique(other: Vector, vararg rest: Vector) = Vectors.unique(this, other, *rest)

fun Vector.add(value: Value) = this.newCopyBuilder().add(value).build()

fun Vector.count() = Vectors.count(this)

fun Vector.repeat(times: Int): Vector {
    if (times == 1) {
        return this
    } else {
        val builder = this.newBuilder()
        for (i in 0..times - 1) {
            for (j in 0..this.size() - 1) {
                builder.add(this, j)
            }
        }
        return builder.build()
    }
}
