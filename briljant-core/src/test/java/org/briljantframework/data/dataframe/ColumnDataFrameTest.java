/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 * DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package org.briljantframework.data.dataframe;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by isak on 17/08/15.
 */
public class ColumnDataFrameTest extends DataFrameTest {

//  @Test
//  public void testBuilderSetNA() throws Exception {
//    DataFrame.Builder builder = getBuilder();
//    builder.loc().setNA(0, 0);
//    builder.loc().setNA(0, 4);
//    builder.newColumn(Types.DOUBLE);
//
//    builder.loc().setNA(5, 5);
//
//    DataFrame build = builder.build();
//    assertTrue(Is.NA(build.loc().getDouble(5, 5)));
//    assertTrue(Is.NA(build.loc().get(Double.class, 5, 5).doubleValue()));
//  }
//
  @Override
  DataFrame.Builder getBuilder() {
    return new ColumnDataFrame.Builder();
  }
//
//  @Test
//  public void testBuilderSet2() throws Exception {
//    DataFrame.Builder builder = getBuilder();
//    builder.loc().set(0, 0, 10.0);
//    builder.loc().set(1, 1, 10);
//    builder.loc().set(2, 2, "hello");
//    builder.loc().set(3, 3, Complex.ONE);
//    builder.loc().set(4, 4, true);
//    builder.loc().set(5, 5, null);
//    builder.loc().set(6, 6, new Date());
//
//    DataFrame build = builder.build();
//    // assertEquals(Type.DOUBLE, build.getTypeAt(0));
//    // assertEquals(Type.INT, build.getTypeAt(1));
//    // assertEquals(Type.STRING, build.getTypeAt(2));
//    // assertEquals(Type.COMPLEX, build.getTypeAt(3));
//    // assertEquals(Type.LOGICAL, build.getTypeAt(4));
//    // assertEquals(Type.OBJECT, build.getTypeAt(5));
//    // assertEquals(Type.from(Date.class), build.getTypeAt(6));
//  }
//
//  @Test
//  public void testBuilderAddColumn() throws Exception {
//    DataFrame.Builder builder = getBuilder();
//    builder.addColumn(Series.of((Object[]) "1 2 3 4 5".split(" ")));
//    builder.addColumn(Series.of(1, 2, 3, 4, 5));
//    builder.addColumn(Series.of(1.0, 2, 3, 4, 5));
//    builder.addColumn(Series.of(Complex.I, Complex.I, Complex.I, Complex.I, Complex.I));
//    builder.addColumn(Series.of(true, true, false, false, false));
//
//    DataFrame build = builder.build();
//    // assertEquals(Type.STRING, build.getTypeAt(0));
//    // assertEquals(Type.INT, build.getTypeAt(1));
//    // assertEquals(Type.DOUBLE, build.getTypeAt(2));
//    // assertEquals(Type.COMPLEX, build.getTypeAt(3));
//    // assertEquals(Type.LOGICAL, build.getTypeAt(4));
//
//    assertEquals(1, build.loc().getInt(0, 1));
//    assertEquals(1, build.loc().getDouble(0, 2), 0);
//    assertEquals(Complex.I, build.loc().get(Complex.class, 0, 3));
//    assertEquals(Logical.TRUE, build.loc().get(Logical.class, 0, 4));
//    // assertEquals(Convert.toValue(1), build.getAsValue(0, 5));
//  }
//
//  @Test
//  public void testBuilderAddBuilder() throws Exception {
//    DataFrame.Builder builder = getBuilder();
//    builder.newColumn(Types.STRING);
//    builder.newColumn(Types.INT);
//    builder.newColumn(Types.DOUBLE);
//    builder.newColumn(Types.COMPLEX);
//    builder.newColumn(Types.LOGICAL);
//    builder.newColumn(Types.OBJECT);
//    builder.loc().set(0, 0, "hello");
//    builder.loc().set(0, 1, 1);
//    builder.loc().set(0, 2, 2);
//    builder.loc().set(0, 3, Complex.I);
//    builder.loc().set(0, 4, true);
//    builder.loc().set(0, 5, new Date());
//
//    DataFrame df = builder.build();
//    assertEquals(1, df.loc().getInt(0, 1));
//    assertEquals(2, df.loc().getDouble(0, 2), 0);
//    assertEquals(Complex.I, df.loc().get(Complex.class, 0, 3));
//    assertEquals(Logical.TRUE, df.loc().get(Logical.class, 0, 4));
//  }
//
//  @Test
//  public void testBuilderAddBuilder1() throws Exception {
//    DataFrame.Builder builder = getBuilder();
//    builder.addColumn(Types.STRING.newBuilder());
//    builder.addColumn(Types.INT.newBuilder());
//    builder.addColumn(Types.DOUBLE.newBuilder());
//    builder.addColumn(Types.COMPLEX.newBuilder());
//    builder.addColumn(Types.LOGICAL.newBuilder());
//    builder.addColumn(Types.OBJECT.newBuilder());
//    builder.addColumn(Types.from(LocalDate.class).newBuilder());
//
//    builder.loc().set(0, 0, "hello");
//    builder.loc().set(0, 1, 1);
//    builder.loc().set(0, 2, 2);
//    builder.loc().set(0, 3, Complex.I);
//    builder.loc().set(0, 4, true);
//    builder.loc().set(0, 5, "dsadsA");
//    builder.loc().set(0, 6, new Date(321321321738L));
//    builder.loc().set(1, 6, 1232L);
//    builder.loc().set(2, 6, "2015-03-15");
//
//    DataFrame build = builder.build();
//    build.setColumnIndex(
//        Index.of("String", "Integer", "Double", "Complex", "Logical", "Object", "Date"));
//    assertEquals(1, build.loc().getInt(0, 1));
//    assertEquals(2, build.loc().getDouble(0, 2), 0);
//    assertEquals(Complex.I, build.loc().get(Complex.class, 0, 3));
//    assertEquals(Logical.TRUE, build.loc().get(Logical.class, 0, 4));
//    assertEquals(Resolve.find(LocalDate.class).resolve(321321321738L),
//        build.loc().get(6).loc().get(LocalDate.class, 0));
//    assertEquals(1, (int) build.loc().get(Integer.class, 0, 1));
//  }

}
