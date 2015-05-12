package org.briljantframework

import org.briljantframework.dataframe.MixedDataFrame
import org.briljantframework.dataframe.get
import org.briljantframework.vector.*
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

        //        val e = Bj.doubleMatrix(1, 3) assign 3.0
        //        val r = Bj.doubleMatrix(1, 3) assign 1.9
        //        println((e vstack r))

        val left = MixedDataFrame.of("key", StringVector("foo", "foo", "ko"),
                                     "lval", IntVector(1, 2, 4))
        val right = MixedDataFrame.of("key", StringVector("foo", "bar"),
                                      "rval", IntVector(3, 5))

        println(left.join(right))
//        val x: Int = left[0, 1]
//        println(x)
//        println(left.get(0).toSet<String>())


    }
}