package org.briljantframework

import org.briljantframework.matrix.netlib.NetlibMatrixFactory
import org.briljantframework.matrix.vstack
import org.junit.Test as test

class briljantTest {
    test fun testTimes() {
        //        val a = vector(1, 2, 3, 4, 5)
        //        val b = vector("hello", "isak", 2.toString(), 3.toString(), "hello")
        //        val dt = frame(a, b)
        //        println(dt[0, all])
        //        println(summary(dt))
        //
        //        val x = DoubleMatrix.newMatrix(3, 3) assign 10.0
        //        val y = DoubleMatrix.newMatrix(5, 10) assign { Utils.getRandom().nextDouble() }
        //
        //        val e = DoubleMatrix.newMatrix(1, 3).assign(3.0)
        //        val r = DoubleMatrix.newMatrix(1, 3).assign(1.9)
        //
        //        println((e vstack r vstack x hstack y).sort(Dim.C))

        val bj = NetlibMatrixFactory()
        val e = bj.doubleMatrix(1, 3) assign 3.0
        val r = bj.doubleMatrix(1, 3) assign 1.9
        println((e vstack r))

    }
}