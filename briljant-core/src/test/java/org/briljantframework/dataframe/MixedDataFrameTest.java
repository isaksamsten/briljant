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

import org.briljantframework.complex.Complex;
import org.briljantframework.dataframe.transform.RemoveIncompleteColumns;
import org.briljantframework.io.resolver.Resolvers;
import org.briljantframework.io.resolver.StringDateConverter;
import org.briljantframework.vector.Bit;
import org.briljantframework.vector.BitVector;
import org.briljantframework.vector.ComplexVector;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.GenericVector;
import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.Is;
import org.briljantframework.vector.Vec;
import org.briljantframework.vector.Vector;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class MixedDataFrameTest {

  private DataFrame dataA, dataB;

  @Before
  public void setUp() throws Exception {
    dataA =
        new MixedDataFrame(Vector.of(("a b c d e f".split(" "))),
                           Vector.of(1.0, 2, 3, 4, 5, 6));
    dataB =
        new MixedDataFrame(Vector.of("g h i j k l".split(" ")),
                           Vector.of(7.0, 8, 9, 10, 11, 12));
  }

  @Test
  public void testBuilderSetNA() throws Exception {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    builder.setNA(0, 0);
    builder.setNA(0, 4);
    builder.addColumnBuilder(Vec.DOUBLE);
    builder.setNA(5, 5);

    DataFrame build = builder.build();
    assertEquals(Vec.VARIABLE, build.getType(0));
    assertEquals(Vec.VARIABLE, build.getType(1));
    assertEquals(Vec.VARIABLE, build.getType(2));
    assertEquals(Vec.VARIABLE, build.getType(3));
    assertEquals(Vec.VARIABLE, build.getType(4));
    assertEquals(Vec.DOUBLE, build.getType(5));
    assertTrue(Is.NA(build.getAsDouble(5, 5)));
    assertTrue(Is.NA(build.get(Double.class, 5, 5).doubleValue()));
  }

  @Test
  public void testBuilderSet() throws Exception {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    builder.set(0, 0, dataA, 0, 1);
    builder.set(0, 3, dataB, 0, 0);

    DataFrame build = builder.build();
    assertEquals(dataA.getType(1), build.getType(0));
    assertEquals(dataB.getType(0), build.getType(3));
  }

  @Test
  public void testBuilderSet1() throws Exception {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    builder.set(0, 0, dataA.getRecord(0), 0);
    builder.set(3, 3, dataB.get(1), 2);
//    builder.set(1, 1, dataA.getAsValue(0, 1));

    DataFrame build = builder.build();
    assertEquals(dataA.getType(0), build.getType(0));
    assertEquals(dataB.getType(1), build.getType(3));
//    assertEquals(dataA.getAsValue(0, 0), build.getAsValue(0, 0));
//    assertEquals(dataB.get(1).getAsValue(2), build.getAsValue(3, 3));
//    assertEquals(dataA.getAsValue(0, 1), build.getAsValue(1, 1));
  }

  @Test
  public void testBuilderSet2() throws Exception {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    builder.set(0, 0, 10.0);
    builder.set(1, 1, 10);
    builder.set(2, 2, "hello");
    builder.set(3, 3, Complex.ONE);
    builder.set(4, 4, true);
    builder.set(5, 5, null);
    builder.set(6, 6, new Date());

    DataFrame build = builder.build();
    assertEquals(Vec.DOUBLE, build.getType(0));
    assertEquals(Vec.INT, build.getType(1));
    assertEquals(Vec.STRING, build.getType(2));
    assertEquals(Vec.COMPLEX, build.getType(3));
    assertEquals(Vec.BIT, build.getType(4));
    assertEquals(Vec.VARIABLE, build.getType(5));
    assertEquals(Vec.typeOf(Date.class), build.getType(6));
  }

  @Test
  public void testBuilderAddColumn() throws Exception {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    builder.addColumn(Vector.of("1 2 3 4 5".split(" ")));
    builder.addColumn(new IntVector(1, 2, 3, 4, 5));
    builder.addColumn(new DoubleVector(1, 2, 3, 4, 5));
    builder.addColumn(new ComplexVector(Complex.I, Complex.I, Complex.I, Complex.I, Complex.I));
    builder.addColumn(new BitVector(true, true, false, false, false));

    DataFrame build = builder.build();
    assertEquals(Vec.STRING, build.getType(0));
    assertEquals(Vec.INT, build.getType(1));
    assertEquals(Vec.DOUBLE, build.getType(2));
    assertEquals(Vec.COMPLEX, build.getType(3));
    assertEquals(Vec.BIT, build.getType(4));

    assertEquals(1, build.getAsInt(0, 1));
    assertEquals(1, build.getAsDouble(0, 2), 0);
    assertEquals(Complex.I, build.getAsComplex(0, 3));
    assertEquals(Bit.TRUE, build.getAsBit(0, 4));
//    assertEquals(Convert.toValue(1), build.getAsValue(0, 5));
  }

  @Test
  public void testBuilderAddBuilder() throws Exception {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    builder.addColumnBuilder(Vec.STRING);
    builder.addColumnBuilder(Vec.INT);
    builder.addColumnBuilder(Vec.DOUBLE);
    builder.addColumnBuilder(Vec.COMPLEX);
    builder.addColumnBuilder(Vec.BIT);
    builder.addColumnBuilder(Vec.VARIABLE);
    builder.set(0, 0, "hello")
        .set(0, 1, 1)
        .set(0, 2, 2)
        .set(0, 3, Complex.I)
        .set(0, 4, true)
        .set(0, 5, new Date());

    DataFrame build = builder.build();
    assertEquals(Vec.STRING, build.getType(0));
    assertEquals(Vec.INT, build.getType(1));
    assertEquals(1, build.getAsInt(0, 1));
    assertEquals(Vec.DOUBLE, build.getType(2));
    assertEquals(2, build.getAsDouble(0, 2), 0);
    assertEquals(Vec.COMPLEX, build.getType(3));
    assertEquals(Complex.I, build.getAsComplex(0, 3));
    assertEquals(Vec.BIT, build.getType(4));
    assertEquals(Bit.TRUE, build.getAsBit(0, 4));
    assertEquals(Vec.VARIABLE, build.getType(5));
  }

  @Test
  public void testBuilderAddBuilder1() throws Exception {
    DataFrame.Builder builder = new MixedDataFrame.Builder();
    builder.addColumnBuilder(Vec.STRING.newBuilder());
    builder.addColumnBuilder(Vec.INT.newBuilder());
    builder.addColumnBuilder(Vec.DOUBLE.newBuilder());
    builder.addColumnBuilder(Vec.COMPLEX.newBuilder());
    builder.addColumnBuilder(Vec.BIT.newBuilder());
    builder.addColumnBuilder(Vec.VARIABLE.newBuilder());
    builder.addColumnBuilder(Vec.typeOf(Date.class).newBuilder());

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
    assertEquals(Vec.STRING, build.getType(0));
    assertEquals(Vec.INT, build.getType(1));
    assertEquals(1, build.getAsInt(0, 1));
    assertEquals(Vec.DOUBLE, build.getType(2));
    assertEquals(2, build.getAsDouble(0, 2), 0);
    assertEquals(Vec.COMPLEX, build.getType(3));
    assertEquals(Complex.I, build.getAsComplex(0, 3));
    assertEquals(Vec.BIT, build.getType(4));
    assertEquals(Bit.TRUE, build.getAsBit(0, 4));
    assertEquals(Vec.VARIABLE, build.getType(5));
    assertEquals(Vec.typeOf(Date.class), build.getType(6));
    assertEquals(new Date(321321321738L), build.get(6).get(Date.class, 0));
    assertEquals(1, (int) build.get(Integer.class, 0, 1));
  }

  @Test
  public void testName() throws Exception {
    Vector a = new GenericVector.Builder(String.class).add("a").add("b").add("c").build();
    DoubleVector b = new DoubleVector.Builder().add(1).addNA().add(100.23).build();

    DataFrame frame = new MixedDataFrame(a, b);
//    frame.setColumnName(0, "isak").setColumnName(1, "lisa");

    DataFrame.Builder copy = frame.newCopyBuilder();
    copy.addColumnBuilder(new DoubleVector.Builder().add(1).addNA().add(2));
    for (int i = 0; i < 10; i++) {
      for (int j = 0; j < copy.columns(); j++) {
        copy.set(i, j, 1);
      }
    }

    DataFrame.Builder builder = new MixedDataFrame.Builder(Vec.typeOf(String.class),
                                                           DoubleVector.TYPE);
    for (int i = 0; i < 10; i++) {
      builder.set(i + 3, 1, 32.2);
      builder.set(i + 3, 0, "hello");
    }
    DataFrame.Builder bu =
        new MixedDataFrame.Builder(
            Vec.typeOf(String.class).newBuilder().addAll("one", "two", "three",
                                                         "four", "four"),
            BitVector.newBuilderWithInitialValues(Bit.TRUE, Bit.FALSE,
                                                  Bit.TRUE, 1),
            IntVector.newBuilderWithInitialValues(1, 2, 3, 4, 5, 5, 6),
            ComplexVector
                .newBuilderWithInitialValues(Complex.I, new Complex(2, 3),
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
        new MixedDataFrame.Builder(Vec.typeOf(String.class).newBuilder().addAll("a", "b", "c"),
                                   IntVector.newBuilderWithInitialValues(
                                       IntStream.range(0, 1000).toArray()));
    DataFrame s = simple.build();
    assertEquals(1, 1, 1);
  }

  @Test
  public void testBuilderConcat() throws Exception {
    DataFrame.Builder builderA = dataA.newCopyBuilder();
    DataFrame concatAB = builderA.concat(dataB).concat(3, new IntVector(1, 2)).build();
    assertTrue(concatAB.isNA(0, 5));
  }

  @Test
  public void testBuilderStack() throws Exception {
    DataFrame.Builder builderA = dataA.newCopyBuilder();
    DataFrame stackAB = builderA.stack(dataB).stack(1, new IntVector(2, 3, 4)).build();
    assertTrue(stackAB.isNA(12, 0));
    dataA.setRecordIndex(HashIndex.from("a", "b", "c", "d", "e", "f"));
    DataFrame df = new MixedDataFrame.Builder()
        .addColumn(Vector.of("d d d d".split(" ")))
        .addColumn(new IntVector(0, 0, 0, 0))
        .build();
    dataB.setColumnIndex(HashIndex.from("String", "Double B"));
    dataA.setColumnIndex(HashIndex.from("String", "Double A"));
  }

  @Test
  public void testMapConstructor() throws Exception {
    Map<String, Vector> vectors = new HashMap<>();
    vectors.put("engines", Vector.of("hybrid", "electric", "electric", "steam"));
    vectors.put("bhp", new IntVector(150, 130, 75, IntVector.NA));
    vectors.put("brand", Vector.of("toyota", "tesla", "tesla", "volvo"));

    DataFrame frame = new MixedDataFrame(vectors);
    frame.setRecordIndex(HashIndex.from(Arrays.asList("a", "b", "c", "d")));
    frame.setColumnIndex(HashIndex.from(Arrays.asList("brand", "engines", "bhp")));
  }

  @Test
  public void testRemoveColumnUsingBuilder() throws Exception {
    Resolvers.find(LocalDate.class).put(String.class,
                                        new StringDateConverter(DateTimeFormatter.ISO_DATE));
    Vector a = new GenericVector.Builder(String.class).add("a").add("b").add(32).addNA().build();
    DoubleVector b = new DoubleVector.Builder().add(1).add(1).add(2).add(100.23).build();
    Vector c = new GenericVector.Builder(LocalDate.class)
        .add("2011-03-23")
        .add(1000L)
        .add(LocalDate.now())
        .add(LocalDate.now())
        .build();

    DataFrame frame = new MixedDataFrame(a, b, c);
//    frame.setColumnIndex(HashIndex.from("a", "b", "c"));
//    frame.setColumnNames("a", "b");
    frame = new RemoveIncompleteColumns().transform(frame);
    assertEquals("The second column should be removed", 2, frame.columns());
//    assertEquals("The column names should be retained", "b", frame.getColumnName(0));
  }
}
