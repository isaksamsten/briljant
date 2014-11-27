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

import org.briljantframework.dataframe.DataFrame;
import org.briljantframework.io.DataFrameInputStream;
import org.briljantframework.io.TypeFactory;
import org.briljantframework.vector.Type;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Isak Karlsson on 14/08/14.
 */
public class MatrixInputStream extends DataFrameInputStream {
    protected MatrixInputStream(InputStream in, TypeFactory typeFactory) {
        super(in, typeFactory);
    }

    @Override
    public Type readColumnType() throws IOException {
        return null;
    }

    @Override
    public String readColumnName() throws IOException {
        return null;
    }

    @Override
    public boolean readValue(DataFrame.Builder builder, int index) throws IOException {
        return false;
    }
    //
    //
    //    private final boolean readSize;
    //    private final ByteOrder order;
    //    private int rows, cols;
    //
    //    public MatrixInputStream(InputStream inputStream) {
    //        this(inputStream, ByteOrder.nativeOrder());
    //    }
    //
    //    public MatrixInputStream(InputStream inputStream, ByteOrder order) {
    //        super(inputStream);
    //        this.readSize = true;
    //        this.order = order;
    //    }
    //
    //    public MatrixInputStream(InputStream inputStream, ByteOrder order, int rows, int cols) {
    //        super(inputStream);
    //        this.readSize = false;
    //
    //        this.order = order;
    //        this.rows = rows;
    //        this.cols = cols;
    //    }
    //
    //
    //    @Override
    //    public <D extends DataFrame<?>> D read(DataFrame.CopyTo<D> copyTo) throws IOException {
    //        if (copyTo == MatrixDataFrame.copyTo()) {
    //            MatrixDataFrame.Builder builder = (MatrixDataFrame.Builder) copyTo.newBuilder(null);
    //            ReadableByteChannel channel = Channels.newChannel(new BufferedInputStream(this));
    //            int rows = this.rows;
    //            int cols = this.cols;
    //            if (readSize) {
    //                ByteBuffer buf = ByteBuffer.allocate(8).order(order);
    //                channel.read(buf);
    //                buf.position(0);
    //                rows = buf.getInt();
    //                cols = buf.getInt();
    //            }
    //            ByteBuffer buf = ByteBuffer.allocate(rows * cols * 8).order(order);
    //            channel.read(buf);
    //            buf.position(0);
    //
    //            double[] values = new double[rows * cols];
    //            buf.asDoubleBuffer().get(values);
    //
    //            builder.values(values);
    //            builder.columns(cols);
    //            @SuppressWarnings("unchecked") D retVal = (D) builder.create();
    //            return retVal;
    //        } else {
    //            ReadableByteChannel channel = Channels.newChannel(new BufferedInputStream(this));
    //            int rows = this.rows;
    //            int cols = this.cols;
    //            if (readSize) {
    //                ByteBuffer buf = ByteBuffer.allocate(8).order(order);
    //                channel.read(buf);
    //                buf.position(0);
    //                rows = buf.getInt();
    //                cols = buf.getInt();
    //            }
    //
    //            int length = Math.toIntExact((long) rows * cols);
    //
    //            List<Type> types = new ArrayList<>(cols);
    //            for (int i = 0; i < cols; i++) {
    //                types.add(typeFactory.newNumeric(String.valueOf(i)));
    //            }
    //
    //            DataFrame.Builder<D> datasetBuilder = copyTo.newBuilder(types);
    //
    //            ByteBuffer buf = ByteBuffer.allocate(length * 8).order(order);
    //            channel.read(buf);
    //
    //            buf.position(0);
    //            DoubleBuffer din = buf.asDoubleBuffer();
    //
    //            for (int i = 0; i < length; i++) {
    //                int col = i % cols;
    //                int pos = col * rows + i / cols;
    //                datasetBuilder.add(din.get(pos));
    //            }
    //
    //            return datasetBuilder.create();
    //        }
    //    }
}
