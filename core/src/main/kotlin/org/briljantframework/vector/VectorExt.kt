package org.briljantframework.vector

import com.google.common.collect.Sets
import org.briljantframework.complex.Complex

fun Double.toValue() = Convert.toValue(this)

fun Int.toValue() = Convert.toValue(this)

fun String.toValue() = Convert.toValue(this)

fun Complex.toValue() = Convert.toValue(this)

fun Bit.toValue() = Convert.toValue(this)

fun Boolean.toValue() = Convert.toValue(this)

fun Vector.contains(value: Value) = this.find(value) != -1

public fun Vector.contains(value: String): Boolean = Vectors.find(this, value) != -1

public fun Vector.contains(value: Int): Boolean = Vectors.find(this, value) != -1

public fun Vector.find(value: Value): Int = Vectors.find(this, value)

public fun <T> Vector.find(value: T): Boolean = Vectors.find(this, value) == 1

public inline fun <reified T> Vector.toSet(): Set<T> = Sets.newHashSet(this.asList(javaClass<T>()))

public inline fun <reified T : Any> Vector.toList(): List<T> = this.asList(javaClass<T>())

fun Vector.unique(): Vector = Vectors.unique(this)

public fun Vector.unique(other: Vector, vararg rest: Vector): Vector
        = Vectors.unique(this, other, *rest)

public fun Vector.add(value: Value): Vector = this.newCopyBuilder().add(value).build()

[suppress("UNCHECKED_CAST")]
public inline fun <reified T> Vector.count(): Map<T, Int> = when (javaClass<T>()) {
    javaClass<Value>() -> Vectors.count(this) as Map<T, Int>
    else -> Vectors.count(javaClass<T>(), this)

}

public inline fun <reified T> Vector.get(index: Int): T = this.get(javaClass<T>(), index)

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
