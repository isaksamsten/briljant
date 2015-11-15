/*
 * The MIT License (MIT)
 * 
 * Copyright (c) 2015 Isak Karlsson
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

import static org.briljantframework.data.Collectors.toDataFrame;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import org.briljantframework.array.BooleanArray;
import org.briljantframework.array.DoubleArray;
import org.briljantframework.data.Collectors;
import org.briljantframework.data.Na;
import org.briljantframework.data.dataframe.join.JoinType;
import org.briljantframework.data.index.Index;
import org.briljantframework.data.reader.DataEntry;
import org.briljantframework.data.reader.EntryReader;
import org.briljantframework.data.reader.StringDataEntry;
import org.briljantframework.data.vector.IntVector;
import org.briljantframework.data.vector.Vector;
import org.junit.Test;

// TODO ISSUE#13: edge cases
public abstract class DataFrameTest {

  abstract DataFrame.Builder getBuilder();

  @Test
  public void testWhere() throws Exception {
    DataFrame df = getBuilder().set("A", Vector.of(1, 2, 3)).set("B", Vector.of(1, 2, 3)).build();
    BooleanArray where = df.where(Integer.class, a -> a > 2);
    assertEquals(BooleanArray.of(0, 0, 1, 0, 0, 1).reshape(3, 2), where);
  }

  @Test
  public void testGet_BooleanArray() throws Exception {
    DataFrame df = getBuilder().set("A", Vector.of(1, 2, 3)).set("B", Vector.of(1, 2, 3)).build();
    DataFrame actual = df.get(df.where(Integer.class, i -> i > 1));
    DataFrame expected = getBuilder().set("A", IntVector.of(Na.INT, 2, 3))
        .set("B", IntVector.of(Na.INT, 2, 3)).build();

    assertEquals(expected, actual);
  }

  @Test
  public void testSet_BooleanArrayValue() throws Exception {
    DataFrame df = getBuilder().set("A", Vector.of(1, 2, 3)).set("B", Vector.of(1, 2, 3)).build();
    DataFrame expected =
        getBuilder().set("A", IntVector.of(30, 2, 3)).set("B", IntVector.of(30, 2, 3)).build();
    DataFrame actual = df.set(df.where(Integer.class, i -> i > 1), 30);

    assertEquals(expected, actual);
  }

  @Test
  public void testLimit_range() throws Exception {
    DataFrame df =
        getBuilder().set("A", Vector.of(1, 2, 3, 4)).setIndex(Index.of("a", "b", "c", "d")).build();
    DataFrame actual = df.limit("a", "c");
    DataFrame expected =
        getBuilder().set("A", IntVector.of(1, 2)).setIndex(Index.of("a", "b")).build();

    assertEquals(expected, actual);
  }

  @Test
  public void testTranspose() throws Exception {
    DataFrame df =
        getBuilder().set("A", Vector.of(1, 2, 3, 4)).setIndex(Index.of("a", "b", "c", "d")).build();
    DataFrame expected = getBuilder().setRecord("A", IntVector.of(1, 2, 3, 4))
        .setColumnIndex(Index.of("a", "b", "c", "d")).build();
    DataFrame actual = df.transpose();
    assertEquals(expected, actual);
  }

  @Test
  public void testApply_Function() throws Exception {
    DataFrame df =
        getBuilder().set("A", IntVector.of(1, 2, 3)).set("B", IntVector.of(1, 2, 3)).build();
    DataFrame expected =
        getBuilder().set("A", IntVector.of(2, 4, 6)).set("B", IntVector.of(2, 4, 5)).build();
    DataFrame actual = df.apply(a -> a.times(2));
    assertEquals(expected, actual);
  }

  @Test
  public void testFilter_records() throws Exception {
    DataFrame df =
        getBuilder().set("A", IntVector.of(1, 2, 3)).set("B", IntVector.of(1, Na.INT, 3)).build();
    DataFrame expected = getBuilder().set("A", IntVector.of(1, 3)).set("B", IntVector.of(1, 3))
        .setIndex(Index.of(0, 2)).build();
    DataFrame actual = df.filter((vector) -> !vector.hasNA());
    assertEquals(expected, actual);
  }

  @Test
  public void testToArray_with_class() throws Exception {
    DataFrame df = DataFrame.of("A", Vector.of("a", "b", "c"), "B", Vector.of(1, 2, 3));

    DoubleArray expected =
        DoubleArray.of(Double.NaN, Double.NaN, Double.NaN, 1, 2, 3).reshape(3, 2);
    DoubleArray actual = df.toDoubleArray();
    assertEquals(expected, actual);
  }

  @Test
  public void testStream() throws Exception {
    DataFrame df = getBuilder().set("A", Vector.of("a", "b", null))
        .set("B", IntVector.of(1, Na.INT, 3)).build();
    DataFrame expected =
        getBuilder().set("A", Vector.of("b", null)).set("B", IntVector.of(Na.INT, 3)).build();
    DataFrame actual = df.stream().filter(Vector::hasNA).collect(toDataFrame(this::getBuilder));
    actual.setColumnIndex(Index.of("A", "B"));
    assertEquals(expected, actual);
  }

  @Test
  public void testApply_Collector() throws Exception {
    DataFrame df =
        getBuilder().set("A", IntVector.of(1, 2, 3)).set("B", IntVector.of(1, 2, 3)).build();
    DataFrame expected = getBuilder().set("A", IntVector.of(1, 1, 2, 2, 3, 3))
        .set("B", IntVector.of(1, 1, 2, 2, 3, 3)).build();
    DataFrame actual = df.apply(Integer.class, Collectors.each(2));
    assertEquals(expected, actual);
  }

  @Test
  public void testGroupBy_column() throws Exception {
    DataFrame df = getBuilder().set("A", Vector.of(1, 2, 1, 2))
        .set("B", Vector.of(30.0, 2.0, 33.0, 6.0)).build();
    DataFrameGroupBy groups = df.groupBy("A");

    DataFrame expectedGroup1 = getBuilder().set("A", Vector.of(1, 1))
        .set("B", Vector.of(30.0, 33.0)).setIndex(Index.of(0, 2)).build();
    DataFrame expectedGroup2 = getBuilder().set("A", Vector.of(2, 2)).set("B", Vector.of(2.0, 6.0))
        .setIndex(Index.of(1, 3)).build();

    assertEquals(expectedGroup1, groups.get(1));
    assertEquals(expectedGroup2, groups.get(2));
  }

  @Test
  public void testGroupBy_apply() throws Exception {
    DataFrame df = getBuilder().set("A", Vector.of(1, 2, 1, 2))
        .set("B", Vector.of(30.0, 2.0, 33.0, 6.0)).build();
    DataFrame actual = df.groupBy("A").apply(v -> v.minus(v.mean()));

    DataFrame expected = getBuilder().set("A", Vector.of(1, 2, 1, 2)).set("B",
        Vector.of(30 - (30 + 33) / 2.0, 2 - (2 + 6) / 2.0, 33 - (30 + 33) / 2.0, 6 - (2 + 6) / 2.0))
        .build();
    assertEquals(expected, actual);
  }

  @Test
  public void testGroupBy_column_with_mapper() throws Exception {
    DataFrame df = getBuilder().set("A", Vector.of(1, 2, 10, 20))
        .set("B", Vector.of("a", "b", "c", "d")).build();

    DataFrame expectedGroup1 =
        getBuilder().set("A", Vector.of(1, 2)).set("B", Vector.of("a", "b")).build();
    DataFrame expectedGroup2 = getBuilder().set("A", Vector.of(10, 20))
        .set("B", Vector.of("c", "d")).setIndex(Index.of(2, 3)).build();
    DataFrameGroupBy groups = df.groupBy(String.class, String::length, "A");

    assertEquals(expectedGroup1, groups.get(1));
    assertEquals(expectedGroup2, groups.get(2));
  }

  @Test
  public void testGroupBy_columns() throws Exception {
    // @formatter:off
    DataFrame df = getBuilder()
        .set("A", Vector.of(1, 2, 3, 4))
        .set("B", Vector.of(1, 1, 0, 4))
        .set("C", Vector.of(1, 1, 0, 4))
        .build();
    // @formatter:on
    DataFrameGroupBy groups = df.groupBy(Vector::mean, "A", "B");
    DataFrame expected1_5 = getBuilder().set("A", Vector.of(2, 3)).set("B", Vector.of(1, 0))
        .set("C", Vector.of(1, 0)).setIndex(Index.of(1, 2)).build();


    DataFrame expected1 = getBuilder().set("A", Vector.of(1)).set("B", Vector.of(1))
        .set("C", Vector.of(1)).setIndex(Index.of(0)).build();


    DataFrame expected4 = getBuilder().set("A", Vector.of(4)).set("B", Vector.of(4))
        .set("C", Vector.of(4)).setIndex(Index.of(3)).build();

    assertEquals(expected1_5, groups.get(1.5));
    assertEquals(expected1, groups.get(1.0));
    assertEquals(expected4, groups.get(4.0));
  }

  @Test
  public void testHead() throws Exception {
    Vector first = Vector.of(1, 2, 3, 4, 5);
    Vector second = Vector.of(1, 2, 3);

    DataFrame df = getBuilder().add(first).add(second).build();
    df.setColumnIndex(Index.of("123", "abc"));

    int n = 3;
    DataFrame head = df.limit(n);

    assertEquals(n, head.rows());
    for (int i = 0; i < n; i++) {
      Vector a = head.get("123");
      Vector b = head.get("abc");

      assertEquals(first.loc().getAsInt(i), a.loc().getAsInt(i));
      assertEquals(second.loc().getAsInt(i), b.loc().getAsInt(i));
    }
  }

  @Test
  public void testBuildingNewDataFrameFromLocationSetterAndDataFrame() throws Exception {
    Vector a = Vector.of(1, 2, 3, 4);
    Vector b = Vector.of(1, 2, 3, 4);
    DataFrame df = getBuilder().add(a).add(b).build();
    df.setColumnIndex(Index.of("a", "b"));

    DataFrame.Builder builder = df.newBuilder();
    for (int i = 0; i < df.rows(); i++) {
      for (int j = 0; j < df.columns(); j++) {
        builder.loc().set(i, j, df, i, j);
      }
    }
    DataFrame copy = builder.build();
    copy.setColumnIndex(df.getColumnIndex());
    assertEquals(df, copy);
  }

  @Test
  public void testBuildingNewDataFrameFromLocationSetterAndValues() throws Exception {
    List<List<Integer>> values =
        Arrays.asList(Arrays.asList(1, 2, 3, 4), Arrays.asList(1, 2, 3, 4));

    DataFrame.Builder builder = getBuilder();
    for (int j = 0; j < values.size(); j++) {
      List<Integer> column = values.get(j);
      for (int i = 0; i < column.size(); i++) {
        builder.loc().set(i, j, column.get(i));
      }
    }
    DataFrame df = builder.build();
    assertEquals(values.get(0), df.loc().get(0).toList(Integer.class));
    assertEquals(values.get(1), df.loc().get(1).toList(Integer.class));
  }

  @Test
  public void testBuildNewDataFrameFromLocationSetterAndRecords() throws Exception {
    Vector[] vectors = new Vector[] {Vector.of(1, 2, 3, 4), Vector.of(1, 2, 3, 4)};
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
    Vector[] vectors = new Vector[] {Vector.of(1, 2, 3, 4), Vector.of(1, 2, 3, 4)};

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
    DataFrame df = getBuilder().set("abc", actual).set("def", actual).build();
    assertEquals(4, df.rows());
    assertEquals(actual, df.get("abc"));
    assertEquals(actual, df.get("def"));
  }

  @Test
  public void testBuildNewDataFrameFromCopyBuilderAndColumnAndKey() throws Exception {
    Vector actual = Vector.of(1, 2, 3, 4);
    Vector replace = Vector.of(4, 3, 2, 1);
    DataFrame df = getBuilder().set("a", actual).set("b", actual).build().newCopyBuilder()
        .set("c", actual).set("b", replace).build();

    assertEquals(4, df.rows());
    assertEquals(3, df.columns());
    assertEquals(actual, df.get("a"));
    assertEquals(replace, df.get("b"));
    assertEquals(actual, df.get("c"));
  }

  @Test
  public void testBuildNewDataFrameFromRecordAndKey() throws Exception {
    Vector actual = Vector.of(1, 2, 3, 4);
    DataFrame df = getBuilder().setRecord("a", actual).setRecord("b", actual).build();

    assertEquals(4, df.columns());
    assertEquals(2, df.rows());
    assertEquals(actual, df.getRecord("a"));
    assertEquals(actual, df.getRecord("b"));
  }

  @Test
  public void testBuildNewDataFrameFromCopyBuilderAndRecordAndKey() throws Exception {
    Vector actual = Vector.of(1, 2, 3, 4);
    Vector replace = Vector.of(4, 3, 2, 1);
    DataFrame df = getBuilder().setRecord("a", actual).setRecord("b", actual).build()
        .newCopyBuilder().setRecord("c", actual).setRecord("b", replace).build();

    assertEquals(4, df.columns());
    assertEquals(3, df.rows());
    assertEquals(actual, df.getRecord("a"));
    assertEquals(replace, df.getRecord("b"));
    assertEquals(actual, df.getRecord("c"));
  }

  @Test
  public void testBuildNewDataFrameByAddingColumns() throws Exception {
    Vector actual = Vector.of(1, 2, 3, 4, 5);

    DataFrame df = getBuilder().add(actual).add(actual).add(actual).build();

    assertEquals(5, df.rows());
    assertEquals(3, df.columns());
    for (int i = 0; i < 3; i++) {
      assertEquals(actual, df.get(i));
    }
  }

  @Test
  public void testBuildNewDataFrameByAddingRecords() throws Exception {
    Vector actual = Vector.of("a", "b", "c");
    DataFrame df = getBuilder().addRecord(actual).addRecord(actual).addRecord(actual).build();

    assertEquals(3, df.rows());
    assertEquals(3, df.columns());
    for (int i = 0; i < 3; i++) {
      // For MixedDataFrame the type of a record is always Object
      assertEquals(actual.toList(String.class), df.getRecord(i).toList(String.class));
    }
  }

  @Test
  public void testBuildNewDataFrameFromValuesUsingKey() throws Exception {
    DataFrame df = getBuilder().set("a", "id", 4).set("a", "age", 32).set("b", "id", 37)
        .set("b", "age", 44).build();

    assertEquals(2, df.rows());
    assertEquals(2, df.columns());

    assertEquals(Vector.of(4, 32).toList(Integer.class), df.getRecord("a").toList(Integer.class));
    assertEquals(Vector.of(37, 44).toList(Integer.class), df.getRecord("b").toList(Integer.class));

    assertEquals(Vector.of(4, 37).toList(Integer.class), df.get("id").toList(Integer.class));
    assertEquals(Vector.of(32, 44).toList(Integer.class), df.get("age").toList(Integer.class));
  }

  @Test
  public void testBuildNewDataFrameFromEntryReader() throws Exception {
    EntryReader entryReader = new EntryReader() {
      private final DataEntry[] entries =
          new DataEntry[] {new StringDataEntry("1", "2", "3"), new StringDataEntry("3", "2", "1")};
      private int current = 0;

      @Override
      public List<Class<?>> getTypes() {
        return Arrays.asList(String.class, String.class, String.class);
      }

      @Override
      public DataEntry next() {
        return entries[current++];
      }

      @Override
      public boolean hasNext() {
        return current < entries.length;
      }
    };

    DataFrame df = getBuilder().readAll(entryReader).build();

    assertEquals(2, df.rows());
    assertEquals(3, df.columns());

    assertEquals(Arrays.asList("1", "2", "3"), df.getRecord(0).toList(String.class));
    assertEquals(Arrays.asList("3", "2", "1"), df.getRecord(1).toList(String.class));
  }

  @Test
  public void testRemoveColumnUsingLocationIndex() throws Exception {
    DataFrame.Builder builder = getBuilder().set("a", Vector.of(1, 2, 3, 4))
        .set("b", Vector.of(1, 2, 3, 4)).set("c", Vector.of(1, 2, 3, 4));

    builder.loc().remove(0);
    DataFrame df = builder.build();
    assertEquals(2, df.columns());
    assertEquals(4, df.rows());
  }

  @Test
  public void testReduceBinaryOpWithInit() throws Exception {
    DataFrame df = getBuilder().set("i", Vector.of(1, 2, 3, 4, 5))
        .set("k", Vector.of(1, 2, 3, 4, 5)).set("d", Vector.of(1, 2, 3, 4, 5)).build();

    Vector sums = df.reduce(Integer.class, 0, Integer::sum);
    assertEquals(15, sums.getAsInt("i"));
    assertEquals(15, sums.getAsInt("k"));
    assertEquals(15, sums.getAsInt("d"));
  }

  @Test
  public void testReduceWithVectorFunction() throws Exception {
    DataFrame df = getBuilder().set("i", Vector.of(1, 2, 3, 4, 5))
        .set("k", Vector.of(1, 2, 3, 4, 5)).set("d", Vector.of(1, 2, 3, 4, 5)).build();

    Vector sums = df.reduce(Vector::sum);
    assertEquals(15, sums.getAsInt("i"));
    assertEquals(15, sums.getAsInt("k"));
    assertEquals(15, sums.getAsInt("d"));
  }

  @Test
  public void testCollectWithCollector() throws Exception {
    DataFrame df = getBuilder().set("i", Vector.of(1, 2, 3, 4, 5))
        .set("k", Vector.of(1, 2, 3, 4, 5)).set("d", Vector.of(1, 2, 3, 4, 5)).build();

    Vector sums = df.collect(Double.class, Collectors.sum());
    assertEquals(15, sums.getAsInt("i"));
    assertEquals(15, sums.getAsInt("k"));
    assertEquals(15, sums.getAsInt("d"));
  }

  @Test
  public void testMap() throws Exception {
    DataFrame df = getBuilder().set("i", Vector.of(1, 2, 3, 4, 5))
        .set("k", Vector.of(1, 2, 3, 4, 5)).set("d", Vector.of(1, 2, 3, 4, 5)).build();

    DataFrame dfs = df.map(Integer.class, a -> a * 2);
    assertEquals(Arrays.asList(2, 4, 6, 8, 10), dfs.get("i").toList(Integer.class));
    assertEquals(Arrays.asList(2, 4, 6, 8, 10), dfs.get("k").toList(Integer.class));
    assertEquals(Arrays.asList(2, 4, 6, 8, 10), dfs.get("d").toList(Integer.class));
  }

  @Test
  public void testGroupByObjectKey() throws Exception {
    DataFrame df = getBuilder().set("i", Vector.of(1, 1, 1, 2, 2, 2))
        .set("j", Vector.of(1, 2, 3, 3, 3, 3)).build();

    DataFrame sums = df.groupBy("i").collect(Vector::sum);
    assertEquals(9, sums.getAsInt(2, "j"));
    assertEquals(6, sums.getAsInt(1, "j"));
  }

  @Test
  public void testGroupByTransform() throws Exception {
    DataFrame df = getBuilder().set("i", Vector.of(1, 1, 2, 2, 3, 3))
        .set("j", Vector.of(10, 10, 20, 20, 30, null)).build();

    DataFrame replaced = df.groupBy("i").apply(v -> v.collect(Collectors.fillNa(22)));
    assertEquals(6, replaced.rows());
    assertEquals(2, replaced.columns());
    assertEquals(22, replaced.get("j").getAsInt(5));
  }

  @Test
  public void testGroupByYearFromLocalDate() throws Exception {
    DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DataFrame df = getBuilder().setRecord(LocalDate.parse("2010-02-22", format), Vector.of(1, 2, 3))
        .setRecord(LocalDate.parse("2011-03-10", format), Vector.of(11, 22, 33))
        .setRecord(LocalDate.parse("2011-03-11", format), Vector.of(11, 22, 33))
        .setColumnIndex("A", "B", "C").build();
    DataFrame sums = df.groupBy(v -> LocalDate.class.cast(v).getYear()).collect(Vector::sum);
    assertEquals(2, sums.rows());
    assertEquals(3, sums.columns());
    assertEquals(66, sums.get("C").getAsInt(2011));
    assertEquals(1, sums.getAsInt(2010, "A"));
  }

  @Test
  public void testInnerJoin() throws Exception {
    Vector values = Vector.of(10, 20, 30, 10, 20);
    DataFrame a = getBuilder().set("a", Vector.of(1, 2, 3, 4, 5)).set("left", values).build();
    DataFrame b = getBuilder().set("a", Vector.of(5, 2, 3, 2, 1)).set("right", values).build();

    DataFrame join = a.join(JoinType.INNER, b, "a");
    Vector on = Vector.of(1, 2, 2, 3, 5);
    Vector actualLeftAndRight = Vector.of(10, 20, 20, 30, 20);
    assertEquals(on, join.get("a"));
    assertEquals(actualLeftAndRight, join.get("left"));
    assertEquals(actualLeftAndRight, join.get("right"));
  }

  @Test
  public void testResetIndex() throws Exception {
    DataFrame df = getBuilder().setRecord("a", Vector.of(1, 2, 3))
        .setRecord("b", Vector.of(1, 2, 3)).setRecord("c", Vector.of(1, 2, 3))
        .setRecord("d", Vector.of(1, 2, 3)).setRecord("e", Vector.of(1, 2, 3)).build();

    DataFrame df2 =
        DataFrame.builder().setRecord("a", Vector.of(1, 2)).setRecord("b", Vector.of(1, 2)).build();
    System.out.println(df2.resetIndex());
    DataFrame actual = df.resetIndex();
    assertEquals(Vector.of("a", "b", "c", "d", "e"), actual.get("index"));
    assertEquals(Arrays.asList(0, 1, 2, 3, 4), actual.getIndex().asList());

  }

}
