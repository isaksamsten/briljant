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

package org.briljantframework.data;

import org.briljantframework.Range;
import org.briljantframework.data.column.CategoricColumn;
import org.briljantframework.data.column.Column;
import org.briljantframework.data.column.Columns;
import org.briljantframework.data.column.DefaultCategoricColumn;
import org.briljantframework.data.types.CategoricType;
import org.briljantframework.data.types.FactorType;
import org.briljantframework.data.types.Types;
import org.briljantframework.data.values.Categoric;
import org.briljantframework.data.values.Missing;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

public class DenseDataFrameTest {

    private DenseDataFrame dataset;

    @Before
    public void setUp() throws Exception {
        DataFrame.Builder<DenseDataFrame> builder = DenseDataFrame.copyTo()
                .newBuilder(Types.range(CategoricType::new, 400));

        Random random = new Random(10);
        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 400; j++) {
                builder.add(random.nextInt(3));
            }
        }

        dataset = builder.create();
    }

    @Test
    public void testTakeColumn() {
        CategoricColumn column = Datasets.getColumnAs(dataset, 0, DefaultCategoricColumn.copyTo());
        long start = System.currentTimeMillis();

        DenseDataFrame rows = Arrays.asList(dataset, dataset, dataset, dataset, dataset)
                .stream()
                .flatMap(Traversable::stream)
                .map(x -> {
                    MutableRow row = x.asMutable();
                    row.add(Categoric.valueOf("hello world"));
                    row.add(Missing.valueOf());
                    return row;
                })
                .collect(Datasets.collect(() -> {
                    return DenseDataFrame.builder(
                            dataset.getTypes()
                                    .add(new CategoricType("hello"))
                                    .add(new CategoricType("Missing"))
                                    .create()
                    );
                }));

        System.out.println(System.currentTimeMillis() - start);
        System.out.println(rows);


        System.out.println(column.stream()
                .collect(Columns.collect(column.getType(), DefaultCategoricColumn.copyTo())));


    }

    @Test
    public void testConstructor() throws Exception {
        Types types = Types.range(FactorType::new, 3);
        List<Collection<Integer>> values = Arrays.asList(
                Range.closed(0, 10),
                Range.closed(0, 10),
                Range.closed(0, 10)
        );
        DenseDataFrame dataset = new DenseDataFrame(types, values);

        Column col = dataset.getColumn(0);
    }

}
