/*
 * ADEB - machine learning pipelines made easy
 * Copyright (C) 2014  Isak Karlsson
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 */

package org.briljantframework.matrix.io;

import org.briljantframework.data.DataFrame;
import org.briljantframework.data.Row;
import org.briljantframework.data.Traversable;
import org.briljantframework.io.DatasetOutputStream;
import org.briljantframework.matrix.dataset.MatrixDataFrame;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.Channels;
import java.nio.channels.WritableByteChannel;

/**
 * Created by Isak Karlsson on 14/08/14.
 */
public class MatrixOutputStream extends DatasetOutputStream {

    private final boolean writeSize;
    private final ByteOrder order;

    public MatrixOutputStream(OutputStream outputStream, ByteOrder order) {
        this(outputStream, true, order);
    }

    public MatrixOutputStream(OutputStream outputStream, boolean writeSize, ByteOrder order) {
        super(outputStream);
        this.writeSize = writeSize;
        this.order = order;
    }

    public MatrixOutputStream(OutputStream outputStream) {
        this(outputStream, true, ByteOrder.nativeOrder());
    }

    @Override
    public void write(Traversable<?> dataset) throws IOException {
        if (!(dataset instanceof DataFrame)) {
            throw new IllegalArgumentException("cannot box IterableDatasets as Matrices");
        }
        if (dataset instanceof MatrixDataFrame) {
            MatrixDataFrame matrix = (MatrixDataFrame) dataset;
            double[] values = matrix.toArray();

            ByteBuffer buf = ByteBuffer.allocate(8 + values.length * 8).order(order);

            buf.putInt(matrix.rows());
            buf.putInt(matrix.columns());
            buf.asDoubleBuffer().put(values);

            buf.position(8 + values.length * 8);
            buf.flip();
            WritableByteChannel channel = Channels.newChannel(new BufferedOutputStream(out));
            channel.write(buf);
            channel.close();
        } else {
            WritableByteChannel channel = Channels.newChannel(new BufferedOutputStream(out));
            int rows = ((DataFrame) dataset).rows();
            int cols = dataset.columns();
            int size = 8 + rows * cols * 8;
            ByteBuffer buf = ByteBuffer.allocate(size).order(order);

            if (writeSize) {
                buf.putInt(rows);
                buf.putInt(cols);
            }

            for (int i = 0; i < dataset.columns(); ++i) {
                for (Row row : dataset) {
                    buf.putDouble(row.getValue(i).asDouble());
                }
            }
            buf.position(size);
            buf.flip();

            channel.write(buf);
            channel.close();
        }
    }
}
