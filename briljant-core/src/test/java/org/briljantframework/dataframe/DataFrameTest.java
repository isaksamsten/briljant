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

import org.briljantframework.dataframe.transform.RemoveIncompleteColumns;
import org.briljantframework.function.Aggregates;
import org.briljantframework.io.resolver.Resolvers;
import org.briljantframework.io.resolver.StringDateConverter;
import org.briljantframework.vector.DoubleVector;
import org.briljantframework.vector.GenericVector;
import org.briljantframework.vector.IntVector;
import org.briljantframework.vector.Na;
import org.briljantframework.vector.Vector;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public abstract class DataFrameTest {

  private DataFrame dataA, dataB;

  abstract DataFrame.Builder getBuilder();

  @Before
  public void setUp() throws Exception {
    dataA = getBuilder()
        .addColumn(Vector.of(("a b c d e f".split(" "))))
        .addColumn(Vector.of(1.0, 2, 3, 4, 5, 6))
        .build();

    dataB = getBuilder()
        .addColumn(Vector.of("g h i j k l".split(" ")))
        .addColumn(Vector.of(7.0, 8, 9, 10, 11, 12))
        .build();
  }

  @Test
  public void testBuilderSet() throws Exception {
    DataFrame.Builder builder = getBuilder();
    builder.set(0, 0, dataA, 0, 1);
    builder.set(0, 3, dataB, 0, 0);

    DataFrame build = builder.build();
    assertEquals(dataA.getType(1), build.getType(0));
    assertEquals(dataB.getType(0), build.getType(3));
  }

  @Test
  public void testBuilderSet1() throws Exception {
    DataFrame.Builder builder = getBuilder();
    builder.set(0, 0, dataA.getRecord(0), 0);
    builder.set(3, 3, dataB.get(1), 2);

    DataFrame build = builder.build();
    assertEquals(dataA.getType(0), build.getType(0));
    assertEquals(dataB.getType(1), build.getType(3));
  }

  @Test
  public void testFizzBuzz() throws Exception {
    IntVector.Builder b = new IntVector.Builder();
    for (int i = 1; i <= 100; i++) {
      b.set(i - 1, i);
    }
    DataFrame df = getBuilder().addColumn(b).build();
    df.setColumnIndex(HashIndex.from("number"));
    DataFrame fizzBuzz =
        df.transform(
            v -> v.transform(Integer.class, String.class,
                             i -> i % 15 == 0 ? "FizzBuzz" :
                                  i % 3 == 0 ? "Fizz" :
                                  i % 5 == 0 ? "Buzz" :
                                  String.valueOf(i)))
            .groupBy("number")
            .collect(Object.class, Aggregates.count())
            .sort(SortOrder.DESC, "number")
            .head(3);

    assertEquals(3, fizzBuzz.rows());
    assertEquals(1, fizzBuzz.columns());
    assertEquals(27, fizzBuzz.getAsInt("Fizz", "number"));
    assertEquals(14, fizzBuzz.getAsInt("Buzz", "number"));
    assertEquals(6, fizzBuzz.getAsInt("FizzBuzz", "number"));
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
    DataFrame df = getBuilder()
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
    vectors.put("bhp", new IntVector(150, 130, 75, Na.INT));
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
    Vector b = new DoubleVector.Builder().add(1).add(1).add(2).add(100.23).build();
    Vector c = new GenericVector.Builder(LocalDate.class)
        .add("2011-03-23")
        .add(LocalDate.now())
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
