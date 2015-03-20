package org.briljantframework

import org.briljantframework.dataframe.DataFrames.summary
import org.briljantframework.dataframe.get
import org.briljantframework.matrix.DoubleMatrix
import org.briljantframework.matrix.vstack
import org.junit.Test as test

class briljantTest {
    test fun testTimes() {
        val a = vector(1, 2, 3, 4, 5)
        val b = vector("hello", "isak", 2.toString(), 3.toString(), "hello")
        val dt = frame(a, b)
        println(dt[0, all])
        println(summary(dt))

        val x = DoubleMatrix.newMatrix(3, 3) assign 10.0
        val y = DoubleMatrix.newMatrix(3, 10) assign 3.0
        val z = (x vstack y) vstack x

        val o = vstack(x, x, x, x, x, x, x)
        println(o)

    }
}