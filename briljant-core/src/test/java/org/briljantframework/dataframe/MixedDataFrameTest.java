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

package org.briljantframework.dataframe;

import org.apache.commons.math3.complex.Complex;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.GenericVector;
import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Logical;
import org.briljantframework.vector.Vector;
import org.briljantframework.vector.VectorType;
import org.junit.Test;

import java.util.Date;
import java.util.stream.IntStream;

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
    builder.setNA(0, 0);
    builder.setNA(0, 4);
    builder.addColumnBuilder(VectorType.DOUBLE);
    builder.setNA(5, 5);

    DataFrame build = builder.build();
    assertEquals(VectorType.OBJECT, build.getType(0));
    assertEquals(VectorType.OBJECT, build.getType(1));
    assertEquals(VectorType.OBJECT, build.getType(2));
    assertEquals(VectorType.OBJECT, build.getType(3));
    assertEquals(VectorType.OBJECT, build.getType(4));
    assertEquals(VectorType.DOUBLE, build.getType(5));
    assertTrue(Is.NA(build.getAsDouble(5, 5)));
    assertTrue(Is.NA(build.get(Double.class, 5, 5).doubleValue()));
  }

  @Test
  public void testBuilderSet2() throws Exception {
    DataFrame.Builder builder = getBuilder();
    builder.set(0, 0, 10.0);
    builder.set(1, 1, 10);
    builder.set(2, 2, "hello");
    builder.set(3, 3, Complex.ONE);
    builder.set(4, 4, true);
    builder.set(5, 5, null);
    builder.set(6, 6, new Date());

    DataFrame build = builder.build();
    assertEquals(VectorType.DOUBLE, build.getType(0));
    assertEquals(VectorType.INT, build.getType(1));
    assertEquals(VectorType.STRING, build.getType(2));
    assertEquals(VectorType.COMPLEX, build.getType(3));
    assertEquals(VectorType.LOGICAL, build.getType(4));
    assertEquals(VectorType.OBJECT, build.getType(5));
    assertEquals(VectorType.from(Date.class), build.getType(6));
  }

  @Test
  public void testBuilderAddColumn() throws Exception {
    DataFrame.Builder builder = getBuilder();
    builder.addColumn(Vector.of("1 2 3 4 5".split(" ")));
    builder.addColumn(new IntVector(1, 2, 3, 4, 5));
    builder.addColumn(new DoubleVector(1, 2, 3, 4, 5));
    builder.addColumn(Vector.of(Complex.I, Complex.I, Complex.I, Complex.I, Complex.I));
    builder.addColumn(Vector.of(true, true, false, false, false));

    DataFrame build = builder.build();
    assertEquals(VectorType.STRING, build.getType(0));
    assertEquals(VectorType.INT, build.getType(1));
    assertEquals(VectorType.DOUBLE, build.getType(2));
    assertEquals(VectorType.COMPLEX, build.getType(3));
    assertEquals(VectorType.LOGICAL, build.getType(4));

    assertEquals(1, build.getAsInt(0, 1));
    assertEquals(1, build.getAsDouble(0, 2), 0);
    assertEquals(Complex.I, build.get(Complex.class, 0, 3));
    assertEquals(Logical.TRUE, build.get(Logical.class, 0, 4));
//    assertEquals(Convert.toValue(1), build.getAsValue(0, 5));
  }

  @Test
  public void testBuilderAddBuilder() throws Exception {
    DataFrame.Builder builder = getBuilder();
    builder.addColumnBuilder(VectorType.STRING);
    builder.addColumnBuilder(VectorType.INT);
    builder.addColumnBuilder(VectorType.DOUBLE);
    builder.addColumnBuilder(VectorType.COMPLEX);
    builder.addColumnBuilder(VectorType.LOGICAL);
    builder.addColumnBuilder(VectorType.OBJECT);
    builder.set(0, 0, "hello")
        .set(0, 1, 1)
        .set(0, 2, 2)
        .set(0, 3, Complex.I)
        .set(0, 4, true)
        .set(0, 5, new Date());

    DataFrame build = builder.build();
    assertEquals(VectorType.STRING, build.getType(0));
    assertEquals(VectorType.INT, build.getType(1));
    assertEquals(1, build.getAsInt(0, 1));
    assertEquals(VectorType.DOUBLE, build.getType(2));
    assertEquals(2, build.getAsDouble(0, 2), 0);
    assertEquals(VectorType.COMPLEX, build.getType(3));
    assertEquals(Complex.I, build.get(Complex.class, 0, 3));
    assertEquals(VectorType.LOGICAL, build.getType(4));
    assertEquals(Logical.TRUE, build.get(Logical.class, 0, 4));
    assertEquals(VectorType.OBJECT, build.getType(5));
  }

  @Test
  public void testBuilderAddBuilder1() throws Exception {
    DataFrame.Builder builder = getBuilder();
    builder.addColumn(VectorType.STRING.newBuilder());
    builder.addColumn(VectorType.INT.newBuilder());
    builder.addColumn(VectorType.DOUBLE.newBuilder());
    builder.addColumn(VectorType.COMPLEX.newBuilder());
    builder.addColumn(VectorType.LOGICAL.newBuilder());
    builder.addColumn(VectorType.OBJECT.newBuilder());
    builder.addColumn(VectorType.from(Date.class).newBuilder());

    builder.set(0, 0, "hello")
        .set(0, 1, 1)
        .set(0, 2, 2)
        .set(0, 3, Complex.I)
        .set(0, 4, true)
        .set(0, 5, "dsadsA")
        .set(0, 6, new Date(321321321738L))
        .set(1, 6, 1232L)
        .set(2, 6, "2015-03-15");

    DataFrame build = builder.build();
    assertEquals(VectorType.STRING, build.getType(0));
    assertEquals(VectorType.INT, build.getType(1));
    assertEquals(1, build.getAsInt(0, 1));
    assertEquals(VectorType.DOUBLE, build.getType(2));
    assertEquals(2, build.getAsDouble(0, 2), 0);
    assertEquals(VectorType.COMPLEX, build.getType(3));
    assertEquals(Complex.I, build.get(Complex.class, 0, 3));
    assertEquals(VectorType.LOGICAL, build.getType(4));
    assertEquals(Logical.TRUE, build.get(Logical.class, 0, 4));
    assertEquals(VectorType.OBJECT, build.getType(5));
    assertEquals(VectorType.from(Date.class), build.getType(6));
    assertEquals(new Date(321321321738L), build.get(6).get(Date.class, 0));
    assertEquals(1, (int) build.get(Integer.class, 0, 1));
  }

  @Test
  public void testName() throws Exception {
    Vector a = new GenericVector.Builder(String.class).add("a").add("b").add("c").build();
    Vector b = new DoubleVector.Builder().add(1).addNA().add(100.23).build();

    DataFrame frame = new MixedDataFrame(a, b);
//    frame.setColumnName(0, "isak").setColumnName(1, "lisa");

    DataFrame.Builder copy = frame.newCopyBuilder();
    copy.addColumn(new DoubleVector.Builder().add(1).addNA().add(2));
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < copy.columns(); j++) {
        copy.set(i, j, 1);
      }
    }

    DataFrame.Builder builder = new MixedDataFrame.Builder(VectorType.from(String.class),
                                                           DoubleVector.TYPE);
    for (int i = 0; i < 10; i++) {
      builder.set(i + 3, 1, 32.2);
      builder.set(i + 3, 0, "hello");
    }
    DataFrame.Builder bu =
        new MixedDataFrame.Builder(
            VectorType.from(String.class).newBuilder().addAll("one", "two", "three",
                                                              "four", "four"),
            VectorType.from(Logical.class).newBuilder().addAll(Logical.TRUE, Logical.FALSE,
                                                               Logical.TRUE, 1),
            IntVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 5, 6),
            VectorType.from(Complex.class).newBuilder().addAll(Complex.I, new Complex(2, 3),
                                                               new Complex(2,
                                                                           2), null,
                                                               Complex.ZERO, 0.0),
            DoubleVector.newBuilderWithInitialValues(0, 1, 2, 3,
                                                     4, 4, 5, 6));

    for (int i = 10; i < 20; i++) {
      for (int j = 0; j < bu.columns(); j++) {
        bu.set(i, j, "10");
      }
    }

    bu.set(22, 0, "hello");

    DataFrame ff = bu.build();
    DataFrame.Builder simple =
        new MixedDataFrame.Builder(
            VectorType.from(String.class).newBuilder().addAll("a", "b", "c"),
            IntVector.newBuilderWithInitialValues(
                IntStream.range(0, 1000).toArray()));
    DataFrame s = simple.build();
    assertEquals(1, 1, 1);
  }
}
