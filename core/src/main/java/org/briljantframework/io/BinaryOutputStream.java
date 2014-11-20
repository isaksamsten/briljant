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

package org.briljantframework.io;

import org.briljantframework.data.Row;
import org.briljantframework.data.Traversable;
import org.briljantframework.data.types.DataType;
import org.briljantframework.data.types.Type;
import org.briljantframework.data.values.Value;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Isak Karlsson on 14/08/14.
 */
public class BinaryOutputStream extends DatasetOutputStream {


    /**
     * Instantiates a new Binary output stream.
     *
     * @param outputStream the output stream
     */
    public BinaryOutputStream(OutputStream outputStream) {
        super(new DataOutputStream(new BufferedOutputStream(outputStream)));
    }

    @Override
    public void write(Traversable instances) throws IOException {
        box(instances);
    }

    /**
     * Boxes a dataset into a byte valueStream.
     */
    private void box(Traversable dataset) throws IOException {
        DataOutputStream dout = (DataOutputStream) out;
        dout.writeByte(0x00);
        dout.writeInt(dataset.columns());
        boxHeader(dataset, dout);
        boxData(dataset, dout);
        dout.flush();
    }

    private void boxHeader(Traversable<?> dataset, DataOutputStream dout) throws IOException {
        for (Type c : dataset.getTypes()) {
            String name = c.getName();
            int len = name.length();
            DataType t = c.getDataType();
            dout.writeByte(t.getValue());
            dout.writeInt(len);
            dout.writeBytes(name);
        }
        dout.writeByte(0x00); // EO-types

    }

    private void boxData(Traversable<?> dataset, DataOutputStream dout) throws IOException {
        for (Row row : dataset) {
            for (int j = 0; j < row.size(); j++) {
                Value value = row.getValue(j);
                if (value.na()) {
                    dout.writeByte(DataType.MISSING.getValue());
                } else {
                    DataType dataType = dataset.getType(j).getDataType();
                    dout.writeByte(dataType.getValue());
                    switch (dataType) {
                        case FACTOR:
                            dout.writeInt((int) value.asDouble());
                            break;
                        case CATEGORIC:
                            String v = value.repr();
                            dout.writeInt(v.length());
                            dout.writeBytes(v);
                            break;
                        case NUMERIC:
                            dout.writeDouble(value.asDouble());
                            break;
                        default:
                            throw new IOException(String.format("unkown value type %s", dataType));
                    }
                }
            }
            dout.writeByte(0x00); // EO-row
        }
    }
}
