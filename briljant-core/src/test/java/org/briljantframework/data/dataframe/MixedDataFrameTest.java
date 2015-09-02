/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2015 Isak Karlsson
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.briljantframework.data.dataframe;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.data.vector.DoubleVector;
import org.briljantframework.data.vector.IntVector;
import org.briljantframework.data.vector.Is;
import org.briljantframework.data.vector.Logical;
import org.briljantframework.data.vector.Vector;
import org.briljantframework.data.vector.VectorType;
import org.junit.Test;

import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by isak on 17/08/15.
 */
public class MixedDataFrameTest extends DataFrameTest {

  @Override
  DataFrame.Builder getBuilder() {
    return new MixedDataFrame.Builder();
  }

  @Test
  public void testBuilderSetNA() throws Exception {
    DataFrame.Builder builder = getBuilder();
    builder.loc().setNA(0, 0);
    builder.loc().setNA(0, 4);
    builder.add(VectorType.DOUBLE);
    builder.loc().setNA(5, 5);

    DataFrame build = builder.build();
//    assertEquals(VectorType.OBJECT, build.getTypeAt(0));
//    assertEquals(VectorType.OBJECT, build.getTypeAt(1));
//    assertEquals(VectorType.OBJECT, build.getTypeAt(2));
//    assertEquals(VectorType.OBJECT, build.getTypeAt(3));
//    assertEquals(VectorType.OBJECT, build.getTypeAt(4));
//    assertEquals(VectorType.DOUBLE, build.getTypeAt(5));
    assertTrue(Is.NA(build.loc().getAsDouble(5, 5)));
    assertTrue(Is.NA(build.loc().get(Double.class, 5, 5).doubleValue()));
  }

  @Test
  public void testBuilderSet2() throws Exception {
    DataFrame.Builder builder = getBuilder();
    builder.loc().set(0, 0, 10.0);
    builder.loc().set(1, 1, 10);
    builder.loc().set(2, 2, "hello");
    builder.loc().set(3, 3, Complex.ONE);
    builder.loc().set(4, 4, true);
    builder.loc().set(5, 5, null);
    builder.loc().set(6, 6, new Date());

    DataFrame build = builder.build();
//    assertEquals(VectorType.DOUBLE, build.getTypeAt(0));
//    assertEquals(VectorType.INT, build.getTypeAt(1));
//    assertEquals(VectorType.STRING, build.getTypeAt(2));
//    assertEquals(VectorType.COMPLEX, build.getTypeAt(3));
//    assertEquals(VectorType.LOGICAL, build.getTypeAt(4));
//    assertEquals(VectorType.OBJECT, build.getTypeAt(5));
//    assertEquals(VectorType.from(Date.class), build.getTypeAt(6));
  }

  @Test
  public void testBuilderAddColumn() throws Exception {
    DataFrame.Builder builder = getBuilder();
    builder.add(Vector.of((Object[]) "1 2 3 4 5".split(" ")));
    builder.add(new IntVector(1, 2, 3, 4, 5));
    builder.add(new DoubleVector(1, 2, 3, 4, 5));
    builder.add(Vector.of(Complex.I, Complex.I, Complex.I, Complex.I, Complex.I));
    builder.add(Vector.of(true, true, false, false, false));

    DataFrame build = builder.build();
//    assertEquals(VectorType.STRING, build.getTypeAt(0));
//    assertEquals(VectorType.INT, build.getTypeAt(1));
//    assertEquals(VectorType.DOUBLE, build.getTypeAt(2));
//    assertEquals(VectorType.COMPLEX, build.getTypeAt(3));
//    assertEquals(VectorType.LOGICAL, build.getTypeAt(4));

    assertEquals(1, build.loc().getAsInt(0, 1));
    assertEquals(1, build.loc().getAsDouble(0, 2), 0);
    assertEquals(Complex.I, build.loc().get(Complex.class, 0, 3));
    assertEquals(Logical.TRUE, build.loc().get(Logical.class, 0, 4));
//    assertEquals(Convert.toValue(1), build.getAsValue(0, 5));
  }

  @Test
  public void testBuilderAddBuilder() throws Exception {
    DataFrame.Builder builder = getBuilder();
    builder.add(VectorType.STRING);
    builder.add(VectorType.INT);
    builder.add(VectorType.DOUBLE);
    builder.add(VectorType.COMPLEX);
    builder.add(VectorType.LOGICAL);
    builder.add(VectorType.OBJECT);
    builder.loc().set(0, 0, "hello");
    builder.loc().set(0, 1, 1);
    builder.loc().set(0, 2, 2);
    builder.loc().set(0, 3, Complex.I);
    builder.loc().set(0, 4, true);
    builder.loc().set(0, 5, new Date());

    DataFrame df = builder.build();
//    assertEquals(VectorType.STRING, df.getTypeAt(0));
//    assertEquals(VectorType.INT, df.getTypeAt(1));
    assertEquals(1, df.loc().getAsInt(0, 1));
//    assertEquals(VectorType.DOUBLE, df.getTypeAt(2));
    assertEquals(2, df.loc().getAsDouble(0, 2), 0);
//    assertEquals(VectorType.COMPLEX, df.getTypeAt(3));
    assertEquals(Complex.I, df.loc().get(Complex.class, 0, 3));
//    assertEquals(VectorType.LOGICAL, df.getTypeAt(4));
    assertEquals(Logical.TRUE, df.loc().get(Logical.class, 0, 4));
//    assertEquals(VectorType.OBJECT, df.getTypeAt(5));
  }

  @Test
  public void testBuilderAddBuilder1() throws Exception {
    DataFrame.Builder builder = getBuilder();
    builder.add(VectorType.STRING.newBuilder());
    builder.add(VectorType.INT.newBuilder());
    builder.add(VectorType.DOUBLE.newBuilder());
    builder.add(VectorType.COMPLEX.newBuilder());
    builder.add(VectorType.LOGICAL.newBuilder());
    builder.add(VectorType.OBJECT.newBuilder());
    builder.add(VectorType.from(Date.class).newBuilder());

    builder.loc().set(0, 0, "hello");
    builder.loc().set(0, 1, 1);
    builder.loc().set(0, 2, 2);
    builder.loc().set(0, 3, Complex.I);
    builder.loc().set(0, 4, true);
    builder.loc().set(0, 5, "dsadsA");
    builder.loc().set(0, 6, new Date(321321321738L));
    builder.loc().set(1, 6, 1232L);
    builder.loc().set(2, 6, "2015-03-15");

    DataFrame build = builder.build();
//    assertEquals(VectorType.STRING, build.getTypeAt(0));
//    assertEquals(VectorType.INT, build.getTypeAt(1));
    assertEquals(1, build.loc().getAsInt(0, 1));
//    assertEquals(VectorType.DOUBLE, build.getTypeAt(2));
    assertEquals(2, build.loc().getAsDouble(0, 2), 0);
//    assertEquals(VectorType.COMPLEX, build.getTypeAt(3));
    assertEquals(Complex.I, build.loc().get(Complex.class, 0, 3));
//    assertEquals(VectorType.LOGICAL, build.getTypeAt(4));
    assertEquals(Logical.TRUE, build.loc().get(Logical.class, 0, 4));
//    assertEquals(VectorType.OBJECT, build.getTypeAt(5));
//    assertEquals(VectorType.from(Date.class), build.getTypeAt(6));
    assertEquals(new Date(321321321738L), build.loc().get(6).loc().get(Date.class, 0));
    assertEquals(1, (int) build.loc().get(Integer.class, 0, 1));
  }

}
