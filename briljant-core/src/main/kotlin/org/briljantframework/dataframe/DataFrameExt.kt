package org.briljantframework.dataframe

import org.briljantframework.all
import org.briljantframework.dataseries.DataSeriesCollection
import org.briljantframework.io.ArffInputStream
import org.briljantframework.io.MatlabTextInputStream
import org.briljantframework.io.RdsInputStream
import org.briljantframework.io.SequenceInputStream
import org.briljantframework.vector.Vec
import java.io.File
import java.io.FileInputStream

fun loadCSV(file: File): DataFrame {
    RdsInputStream(FileInputStream(file)).use {
        val types = it.readColumnTypes()
        val names = it.readColumnIndex()
        val builder = MixedDataFrame.Builder(types)
        val df = builder.read(it).build()
        df.setColumnIndex(HashIndex.from(names))
        return df
    }
}

fun loadSequence(file: File): DataFrame {
    SequenceInputStream(FileInputStream(file)).use {
        return DataSeriesCollection.Builder(Vec.STRING).read(it).build()
    }
}

fun loadMatlab(file: File): DataFrame {
    MatlabTextInputStream(FileInputStream(file)).use {
        return DataSeriesCollection.Builder(Vec.DOUBLE).read(it).build()
    }
}

fun loadArff(file: File): DataFrame {
    ArffInputStream(FileInputStream(file)).use {
        val types = it.readColumnTypes()
        val names = it.readColumnIndex()
        val df = MixedDataFrame.Builder(types).read(it).build()
        df.setColumnIndex(HashIndex.from(names))
        return df
    }
}

//fun DataFrame.get(row: Int, column: Int) = this.getAsValue(row, column)

fun DataFrame.get(row: Int, all: all) = this.getRecord(row)


fun DataFrame.get(all: all, column: Int) = this.get(column)

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
        return retain(columns)
    } else if (columns is all) {
        return getRecords(rows)
    } else {
        val b = newBuilder()
        var col = 0
        for (j in columns) {
            b.addColumnBuilder(getType(col))
            var row = 0
            for (i in rows) {
                b.set(row, col, this, i, j)
                row += 1
            }
            col += 1
        }

        return b.build()
    }
}

fun DataFrame.summary() = DataFrames.summary(this)