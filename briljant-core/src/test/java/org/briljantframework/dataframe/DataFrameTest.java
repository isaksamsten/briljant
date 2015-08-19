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

import org.briljantframework.vector.Vector;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public abstract class DataFrameTest {

  private DataFrame dataA, dataB;

  abstract DataFrame.Builder getBuilder();

  @Before
  public void setUp() throws Exception {
    dataA = getBuilder()
        .add(Vector.of(("a b c d e f".split(" "))))
        .add(Vector.of(1.0, 2, 3, 4, 5, 6))
        .build();

    dataB = getBuilder()
        .add(Vector.of("g h i j k l".split(" ")))
        .add(Vector.of(7.0, 8, 9, 10, 11, 12))
        .build();
  }

  @Test
  public void testHead() throws Exception {
    Vector first = Vector.of(1, 2, 3, 4, 5);
    Vector second = Vector.of(1, 2, 3);

    DataFrame df = getBuilder()
        .add(first)
        .add(second)
        .build();
    df.setColumnIndex(HashIndex.from("123", "abc"));

    int n = 3;
    DataFrame head = df.head(n);

    assertEquals(n, head.rows());
    for (int i = 0; i < n; i++) {
      Vector a = head.get("123");
      Vector b = head.get("abc");

      assertEquals(first.getAsInt(i), a.getAsInt(i));
      assertEquals(second.getAsInt(i), b.getAsInt(i));
    }
  }

  @Test
  public void testBuildingNewDataFrameFromLocationSetterAndDataFrame() throws Exception {
    Vector a = Vector.of(1, 2, 3, 4);
    Vector b = Vector.of(1, 2, 3, 4);
    DataFrame df = getBuilder()
        .add(a)
        .add(b)
        .build();
    df.setColumnIndex(HashIndex.from("a", "b"));

    DataFrame.Builder builder = df.newBuilder();
    for (int i = 0; i < df.rows(); i++) {
      for (int j = 0; j < df.columns(); j++) {
        builder.loc().set(i, j, df, i, j);
      }
    }
    DataFrame copy = builder.build();
    assertEquals(df, copy);
  }

  @Test
  public void testBuildingNewDataFrameFromLocationSetterAndValues() throws Exception {
    List<List<Integer>> values = Arrays.asList(
        Arrays.asList(1, 2, 3, 4),
        Arrays.asList(1, 2, 3, 4)
    );

    DataFrame.Builder builder = getBuilder();
    for (int j = 0; j < values.size(); j++) {
      List<Integer> column = values.get(j);
      for (int i = 0; i < column.size(); i++) {
        builder.loc().set(i, j, column.get(i));
      }
    }
    DataFrame df = builder.build();
    assertEquals(values.get(0), df.loc().get(0).asList(Integer.class));
    assertEquals(values.get(1), df.loc().get(1).asList(Integer.class));
  }

  @Test
  public void testBuildNewDataFrameFromLocationSetterAndRecords() throws Exception {
    Vector[] vectors = new Vector[]{
        Vector.of(1, 2, 3, 4),
        Vector.of(1, 2, 3, 4)
    };
    DataFrame.Builder builder = getBuilder();
    for (int i = 0; i < vectors.length; i++) {
      builder.loc().setRecord(i, vectors[i]);
    }
    DataFrame df = builder.build();

    assertEquals(vectors.length, df.rows());
    for (int i = 0; i < vectors.length; i++) {
      assertEquals(vectors[i], df.loc().getRecord(i));
    }
  }

  @Test
  public void testBuildNewDataFrameFromLocationSetterAndColumns() throws Exception {
    Vector[] vectors = new Vector[]{
        Vector.of(1, 2, 3, 4),
        Vector.of(1, 2, 3, 4)
    };

    DataFrame.Builder builder = getBuilder();
    for (int i = 0; i < vectors.length; i++) {
      builder.loc().set(i, vectors[i]);
    }
    DataFrame df = builder.build();

    assertEquals(vectors[0].size(), df.rows());
    for (int i = 0; i < vectors.length; i++) {
      assertEquals(vectors[i], df.loc().get(i));
    }
  }

  @Test
  public void testBuildNewDataFrameFromColumnAndKey() throws Exception {
    Vector actual = Vector.of(1, 2, 3, 4);
    DataFrame df = getBuilder()
        .set("abc", actual)
        .set("def", actual)
        .build();
    assertEquals(4, df.rows());
    assertEquals(actual, df.get("abc"));
    assertEquals(actual, df.get("def"));
  }

  @Test
  public void testBuildNewDataFrameFromCopyBuilderAndColumnAndKey() throws Exception {
    Vector actual = Vector.of(1, 2, 3, 4);
    Vector replace = Vector.of(4, 3, 2, 1);
    DataFrame df = getBuilder()
        .set("a", actual)
        .set("b", actual)
        .build()
        .newCopyBuilder()
        .set("c", actual)
        .set("b", replace)
        .build();

    assertEquals(4, df.rows());
    assertEquals(3, df.columns());
    assertEquals(actual, df.get("a"));
    assertEquals(replace, df.get("b"));
    assertEquals(actual, df.get("c"));
  }

  @Test
  public void testBuildNewDataFrameFromRecordAndKey() throws Exception {
    Vector actual = Vector.of(1, 2, 3, 4);
    DataFrame df = getBuilder()
        .setRecord("a", actual)
        .setRecord("b", actual)
        .build();

    assertEquals(4, df.columns());
    assertEquals(2, df.rows());
    assertEquals(actual, df.getRecord("a"));
    assertEquals(actual, df.getRecord("b"));
  }

  @Test
  public void testBuildNewDataFrameFromCopyBuilderAndRecordAndKey() throws Exception {
    Vector actual = Vector.of(1, 2, 3, 4);
    Vector replace = Vector.of(4, 3, 2, 1);
    DataFrame df = getBuilder()
        .setRecord("a", actual)
        .setRecord("b", actual)
        .build()
        .newCopyBuilder()
        .setRecord("c", actual)
        .setRecord("b", replace)
        .build();

    assertEquals(4, df.columns());
    assertEquals(3, df.rows());
    assertEquals(actual, df.getRecord("a"));
    assertEquals(replace, df.getRecord("b"));
    assertEquals(actual, df.getRecord("c"));
  }

  @Test
  public void testBuildNewDataFrameByAddingColumns() throws Exception {
    Vector actual = Vector.of(1, 2, 3, 4, 5);

    DataFrame df = getBuilder()
        .add(actual)
        .add(actual)
        .add(actual)
        .build();

    assertEquals(5, df.rows());
    assertEquals(3, df.columns());
    for (int i = 0; i < 3; i++) {
      assertEquals(actual, df.get(i));
    }
  }

  @Test
  public void testBuildNewDataFrameByAddingRecords() throws Exception {
    Vector actual = Vector.of("a", "b", "c");
    DataFrame df = getBuilder()
        .addRecord(actual)
        .addRecord(actual)
        .addRecord(actual)
        .build();

    assertEquals(3, df.rows());
    assertEquals(3, df.columns());
    for (int i = 0; i < 3; i++) {
      // For MixedDataFrame the type of a record is always Object
      assertEquals(actual.asList(String.class), df.getRecord(i).asList(String.class));
    }
  }

  @Test
  public void testBuildNewDataFrameFromValuesUsingKey() throws Exception {
    DataFrame df = getBuilder()
        .set("a", "id", 4).set("a", "age", 32)
        .set("b", "id", 37).set("b", "age", 44)
        .build();

    assertEquals(2, df.rows());
    assertEquals(2, df.columns());

    assertEquals(Vector.of(4, 32), df.getRecord("a"));
    assertEquals(Vector.of(37, 44), df.getRecord("b"));

    assertEquals(Vector.of(4, 37), df.get("id"));
    assertEquals(Vector.of(32, 44), df.get("age"));
  }

  //  @Test
//  public void testBuilderSet() throws Exception {
//    DataFrame.Builder builder = getBuilder();
//    builder.set(0, 0, dataA, 0, 1);
//    builder.set(0, 3, dataB, 0, 0);
//
//    DataFrame build = builder.build();
//    assertEquals(dataA.getTypeAt(1), build.getTypeAt(0));
//    assertEquals(dataB.getTypeAt(0), build.getTypeAt(3));
//  }
//
//  @Test
//  public void testBuilderSet1() throws Exception {
//    DataFrame.Builder builder = getBuilder();
//    builder.set(0, 0, dataA.getRecord(0), 0);
//    builder.set(3, 3, dataB.at().get(1), 2);
//
//    DataFrame build = builder.build();
//    assertEquals(dataA.getTypeAt(0), build.getTypeAt(0));
//    assertEquals(dataB.getTypeAt(1), build.getTypeAt(3));
//  }

//  @Test
//  public void testFizzBuzz() throws Exception {
//    IntVector.Builder b = new IntVector.Builder();
//    for (int i = 1; i <= 100; i++) {
//      b.set(i - 1, i);
//    }
//    DataFrame df = getBuilder().add(b).build();
//    df.setColumnIndex(HashIndex.from("number"));
//    DataFrame fizzBuzz =
//        df.transform(
//            v -> v.transform(Integer.class, String.class,
//                             i -> i % 15 == 0 ? "FizzBuzz" :
//                                  i % 3 == 0 ? "Fizz" :
//                                  i % 5 == 0 ? "Buzz" :
//                                  String.valueOf(i)))
//            .groupBy("number")
//            .collect(Object.class, Aggregates.count())
//            .sort(SortOrder.DESC, "number")
//            .head(3);
//
//    assertEquals(3, fizzBuzz.rows());
//    assertEquals(1, fizzBuzz.columns());
////    assertEquals(27, fizzBuzz.getAsInt("Fizz", "number"));
////    assertEquals(14, fizzBuzz.getAsInt("Buzz", "number"));
////    assertEquals(6, fizzBuzz.getAsInt("FizzBuzz", "number"));
//  }
//
//  @Test
//  public void testBuilderConcat() throws Exception {
//    DataFrame.Builder builderA = dataA.newCopyBuilder();
//    DataFrame concatAB = builderA.concat(dataB).concat(3, new IntVector(1, 2)).build();
//    assertTrue(concatAB.loc().isNA(0, 5));
//  }
//
//  @Test
//  public void testBuilderStack() throws Exception {
//    DataFrame.Builder builderA = dataA.newCopyBuilder();
//    DataFrame stackAB = builderA.stack(dataB).stack(1, new IntVector(2, 3, 4)).build();
//    assertTrue(stackAB.loc().isNA(12, 0));
//    dataA.setRecordIndex(HashIndex.from("a", "b", "c", "d", "e", "f"));
//    DataFrame df = getBuilder()
//        .add(Vector.of("d d d d".split(" ")))
//        .add(new IntVector(0, 0, 0, 0))
//        .build();
//    dataB.setColumnIndex(HashIndex.from("String", "Double B"));
//    dataA.setColumnIndex(HashIndex.from("String", "Double A"));
//  }
//
//  @Test
//  public void testMapConstructor() throws Exception {
//    Map<String, Vector> vectors = new HashMap<>();
//    vectors.put("engines", Vector.of("hybrid", "electric", "electric", "steam"));
//    vectors.put("bhp", new IntVector(150, 130, 75, Na.INT));
//    vectors.put("brand", Vector.of("toyota", "tesla", "tesla", "volvo"));
//
//    DataFrame frame = new MixedDataFrame(vectors);
//    frame.setRecordIndex(HashIndex.from(Arrays.asList("a", "b", "c", "d")));
//    frame.setColumnIndex(HashIndex.from(Arrays.asList("brand", "engines", "bhp")));
//  }
//
//  @Test
//  public void testRemoveColumnUsingBuilder() throws Exception {
//    Resolvers.find(LocalDate.class).put(String.class,
//                                        new StringDateConverter(DateTimeFormatter.ISO_DATE));
//    Vector a = new GenericVector.Builder(String.class).add("a").add("b").add(32).addNA().build();
//    Vector b = new DoubleVector.Builder().add(1).add(1).add(2).add(100.23).build();
//    Vector c = new GenericVector.Builder(LocalDate.class)
//        .add("2011-03-23")
//        .add(LocalDate.now())
//        .add(LocalDate.now())
//        .add(LocalDate.now())
//        .build();
//
//    DataFrame frame = new MixedDataFrame(a, b, c);
////    frame.setColumnIndex(HashIndex.from("a", "b", "c"));
////    frame.setColumnNames("a", "b");
//    frame = new RemoveIncompleteColumns().transform(frame);
//    assertEquals("The second column should be removed", 2, frame.columns());
////    assertEquals("The column names should be retained", "b", frame.getColumnName(0));
//  }
}
