package org.briljantframework.kotlin.dataframe

import org.briljantframework.data.dataframe.DataFrame
import org.briljantframework.data.series.Series

/**
 * Created by isak on 08/06/16.
 */


fun main(args: Array<String>) {
    val x = DataFrame.of("DSV", Series.of("A", "B", "C", "D"), "SU", Series.of("AA", "B-", "C+", "Fx"))
    println(x)
}