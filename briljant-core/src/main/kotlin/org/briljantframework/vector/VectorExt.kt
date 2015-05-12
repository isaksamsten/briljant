package org.briljantframework.vector

import com.google.common.collect.Sets
import org.briljantframework.complex.Complex
import org.briljantframework.dataframe.Series

fun Vector.get(idx: Iterable<Int>) = slice(idx)

//public inline fun <reified T> Vector.get(i: Int): T = this.get(javaClass<T>(), i)


//fun Double.toValue() = Convert.toValue(this)
//
//fun Int.toValue() = Convert.toValue(this)
//
//fun String.toValue() = Convert.toValue(this)
//
//fun Complex.toValue() = Convert.toValue(this)
//
//fun Bit.toValue() = Convert.toValue(this)
//
//fun Boolean.toValue() = Convert.toValue(this)
//
//fun Vector.contains(value: Value) = this.find(value) != -1
//
//public fun Vector.contains(value: String): Boolean = Vec.find(this, value) != -1
//public fun Vector.contains(value: Int): Boolean = Vec.find(this, value) != -1
//
//public fun Vector.find(value: Value): Int = Vec.find(this, value)
//
public fun <T> Vector.find(value: T): Boolean = Vec.find(this, value) == 1

public inline fun <reified T> Vector.toSet(): Set<T> = Sets.newHashSet(this.asList(javaClass<T>()))

public inline fun <reified T : Any> Vector.toList(): List<T> = this.asList(javaClass<T>())

fun Vector.unique(): Vector = Vec.unique(this)

public fun Vector.unique(other: Vector, vararg rest: Vector): Vector
        = Vec.unique(this, other, *rest)

//public fun Vector.add(value: Value): Vector = this.newCopyBuilder().add(value).build()
//
//public fun Vector.add(value: Any): Vector = newCopyBuilder().add(value).build()

//[suppress("UNCHECKED_CAST")]
//public inline fun <reified T> Vector.count(): Map<T, Int> = when (javaClass<T>()) {
//    javaClass<Value>() -> Vec.count(this) as Map<T, Int>
//    else -> Vec.count(javaClass<T>(), this)
//}

public inline fun <reified T> Vector.get(index: Int): T = this.get(javaClass<T>(), index)

public inline fun <reified T> Series.get(index: Any): T = this.get(javaClass<T>(), index)


public inline fun <reified T> Vector.sort([noinline] cmp: (T, T) -> Int): Vector
        = Vec.sort(javaClass<T>(), this, cmp)

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
