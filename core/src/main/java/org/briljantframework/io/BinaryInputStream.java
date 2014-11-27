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

/**
 * Created by Isak Karlsson on 14/08/14. extends DataFrameInputStream
 */
public class BinaryInputStream {

    //    public BinaryInputStream(InputStream inputStream) {
    //        super(inputStream);
    //    }
    //
    //    @Override
    //    public <D extends DataFrame<?>> D read(DataFrame.CopyTo<D> copyTo) throws IOException {
    //        DataInputStream data = new DataInputStream(new BufferedInputStream(in));
    //        byte version;
    //        version = data.readByte();
    //        if (version != 0x00) {
    //            throw new IOException("cannot parse this dataset yet version=" + version);
    //        }
    //        int cols = data.readInt();
    //        List<Type> types = unboxHeader(data, cols);
    //
    //
    //        DataFrame.Builder<D> datasetBuilder = copyTo.newBuilder(types);
    //
    //        unboxValues(data, types, datasetBuilder);
    //        return datasetBuilder.create();
    //    }
    //
    //
    //    private void unboxValues(DataInputStream data, List<Type> types, DataFrame.Builder<?> datasetBuilder) throws
    //            IOException {
    //        int tag = 0;
    //        while (tag >= 0) {
    //            while (true) {
    //                tag = data.read();
    //                if (tag <= 0) {
    //                    break;
    //                }
    //                switch (DataType.fromTag((byte) tag)) {
    //                    case NUMERIC:
    //                        datasetBuilder.add(data.readFloat());
    //                        break;
    //                    case MISSING:
    //                        datasetBuilder.add((String) null);
    //                        break;
    //                    case FACTOR:
    //                        datasetBuilder.add(data.readInt());
    //                        break;
    //                    case CATEGORIC:
    //                        int size = data.readInt();
    //                        byte[] buf = new byte[size];
    //                        if (data.read(buf) != size) {
    //                            System.out.println("read to few bytes.. this is likely an error...");
    //                        }
    //
    //                        String value = new String(buf);
    //                        datasetBuilder.add(value);
    //                        break;
    //                    default:
    //                        throw new IOException("invalid valueOf type ++ " + tag);
    //                }
    //            }
    //
    //        }
    //    }
    //
    //    private List<Type> unboxHeader(DataInputStream data, int cols)
    //            throws IOException {
    //        List<Type> types = new ArrayList<>(cols);
    //        byte tag;
    //        while ((tag = data.readByte()) != 0x00) {
    //            int len = data.readInt();
    //            byte[] name = new byte[len];
    //            if (data.read(name) != len) {
    //                System.out.println("data read does not match the length.. possible error");
    //            }
    //            types.add(typeFactory.create(new String(name), DataType.fromTag(tag)));
    //        }
    //
    //        return types;
    //    }

}
