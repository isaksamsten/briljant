package org.briljantframework.dataframe

import org.briljantframework.all

fun DataFrame.get(row: Int, column: Int) = this.getAsValue(row, column)

fun DataFrame.get(all: all, column: Int) = this.getColumn(column)

fun DataFrame.get(row: Int, all: all) = this.getRecord(row)

fun DataFrame.get(rows: Iterable<Int>, columns: Iterable<Int>): DataFrame {
    if (rows is all && columns is all) {
        return this
    } else if ( rows is all) {
        return takeColumns(columns)
    } else if (columns is all) {
        return takeRecords(rows)
    } else {
        throw UnsupportedOperationException()
    }
}

fun DataFrame.summary() = DataFrames.summary(this)