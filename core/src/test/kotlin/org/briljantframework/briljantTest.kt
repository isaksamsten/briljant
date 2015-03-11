package org.briljantframework

import org.junit.Test as test
import org.briljantframework.dataframe.*
import org.briljantframework.dataframe.DataFrames.*

class briljantTest {
    test fun testTimes() {
        val a = vector(1, 2, 3, 4, 5)
        val b = vector("hello", "isak", 2.toString(), 3.toString(), "hello")
        val dt = frame(a, b)
        println(dt[0, all])
        println(summary(dt))
    }
}