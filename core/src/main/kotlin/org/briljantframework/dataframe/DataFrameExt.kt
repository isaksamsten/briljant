package org.briljantframework.dataframe

import org.briljantframework.all
import org.briljantframework.dataseries.DataSeriesCollection
import org.briljantframework.io.ArffInputStream
import org.briljantframework.io.DelimitedInputStream
import org.briljantframework.io.MatlabTextInputStream
import org.briljantframework.io.SequenceInputStream
import org.briljantframework.vector.Vectors
import java.io.File
import java.io.FileInputStream

fun loadCSV(file: File): DataFrame {
    DelimitedInputStream(FileInputStream(file)).use {
        val types = it.readColumnTypes()
        val names = it.readColumnNames()
        val builder = MixedDataFrame.Builder(names, types)
        return builder.read(it).build()
    }
}

fun loadSequence(file: File): DataFrame {
    SequenceInputStream(FileInputStream(file)).use {
        return DataSeriesCollection.Builder(Vectors.STRING).read(it).build()
    }
}

fun loadMatlab(file: File): DataFrame {
    MatlabTextInputStream(FileInputStream(file)).use {
        return DataSeriesCollection.Builder(Vectors.DOUBLE).read(it).build()
    }
}

fun loadArff(file: File): DataFrame {
    ArffInputStream(FileInputStream(file)).use {
        val types = it.readColumnTypes()
        val names = it.readColumnNames()
        return MixedDataFrame.Builder(names, types).read(it).build()
    }
}

fun DataFrame.get(row: Int, column: Int) = this.getAsValue(row, column)

fun DataFrame.get(all: all, column: Int) = this.getColumn(column)

fun DataFrame.get(row: Int, all: all) = this.getRecord(row)

//fun DataFrame.set(row: all, column: Int, value: Vector) = when {
//    column == this.columns() -> this.addColumnBuilder(value)
//    column > this.columns() || column < 0 -> throw IndexOutOfBoundsException()
//    else -> this.addColumnBuilder(column, value)
//}
//
//fun DataFrame.set(row: Int, column: all, value: Vector) = when {
//    row == this.rows() -> this.insertRecord(value)
//    row > this.rows() || row < 0 -> throw IndexOutOfBoundsException()
//    else -> this.insertRecord(row, value)
//}

fun DataFrame.get(rows: Iterable<Int>, columns: Iterable<Int>): DataFrame {
    if (rows is all && columns is all) {
        return this
    } else if ( rows is all) {
        return takeColumns(columns)
    } else if (columns is all) {
        return getRecords(rows)
    } else {
        throw UnsupportedOperationException()
    }
}

fun DataFrame.summary() = DataFrames.summary(this)